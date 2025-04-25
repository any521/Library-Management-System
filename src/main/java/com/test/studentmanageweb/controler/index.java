package com.test.studentmanageweb.controler;

import com.test.studentmanageweb.entity.*;
import com.test.studentmanageweb.mapper.BookMapper;
import com.test.studentmanageweb.mapper.BorrowMapper;
import com.test.studentmanageweb.mapper.StudentMapper;
import com.test.studentmanageweb.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class index {

    @Resource
    StudentMapper studentMapper;
    @Resource
    BookMapper bookMapper;
    @Resource
    BorrowMapper borrowMapper;
    @Resource
    UserMapper userMapper;

    @RequestMapping("/login")
    public String login(HttpSession session) {
        session.removeAttribute("flag");
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        List<users> list = studentMapper.getAllusers();
        for (users u : list) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                session.setAttribute("user", u);
                return "redirect:/index";
            }
        }
        session.setAttribute("flag", false);
        return "login";
    }

    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        List<Student> list = studentMapper.getAllStudents();
        model.addAttribute("list", list);

        if (session.getAttribute("err") != null) {
            model.addAttribute("errorMessage", session.getAttribute("err"));
            session.removeAttribute("err");
        }

        if (session.getAttribute("success") != null) {
            model.addAttribute("successMessage", session.getAttribute("success"));
            session.removeAttribute("success");
        }

        // 添加安全时间对象（带防御性拷贝）
        model.addAttribute("currentTime", LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        return "index";
    }

    @PostMapping("/index")
    public String addStudent(@RequestParam int sid,
                             @RequestParam String name,
                             @RequestParam String sex,
                             @RequestParam int grade,
                             @RequestParam double score,
                             HttpSession session) {
        List<Student> list = studentMapper.getAllStudents();
        for (Student s : list) {
            if(name.equals(s.getName())) {
                session.setAttribute("err", "⚠️ 该学生已存在，添加失败！");
                return "redirect:/index";
            }
        }
        Student student = new Student(sid, name, sex, grade, score);
        studentMapper.insert(student);
        session.setAttribute("success", "🎉 学生信息添加成功！");
        return "redirect:/index";
    }

    @PostMapping("/delete")
    public String deleteStudent(@RequestParam int sid,
                                HttpSession session) {
            List<Borrow> borrows = borrowMapper.getStudentBorrowBooks(sid);
            if (!borrows.isEmpty()) {
                List<String> bookNames = new ArrayList<>();
                for (Borrow b : borrows) {
                    bookNames.add("<<"+bookMapper.selectById(b.getBid()).getName()+">>");
                }
                session.setAttribute("err",
                        "无法删除！未归还图书：\n" + String.join("\n", bookNames));
                return "redirect:/index";
            }

            // 执行删除
            int result = studentMapper.deleteById(sid);

            if (result > 0) {
                session.setAttribute("success", "🎉 学生删除成功");
            } else {
                session.setAttribute("err", "⚠️ 学生不存在");
            }
        return "redirect:/index";
    }

    @GetMapping("/Books")
    public String showBooks(Model model,HttpSession session) {
        List<Book> list = bookMapper.getAllBooks();
        model.addAttribute("list", list);

        if (session.getAttribute("bookError") != null) {
            model.addAttribute("errorMessage", session.getAttribute("bookError"));
            session.removeAttribute("bookError");
        }

        if (session.getAttribute("bookSuccess") != null) {
            model.addAttribute("successMessage", session.getAttribute("bookSuccess"));
            session.removeAttribute("bookSuccess");
        }
        return "Books";
    }
    @PostMapping("/Books")
    public String showBookList(Model model) {
        List<Book> list = bookMapper.getAllBooks();
        model.addAttribute("list", list);
        return "Books";
    }
    @PostMapping("/deleteBook")
    @Transactional
    public String deleteBook(@RequestParam int bid, HttpSession session) {
            List<Borrow> activeBorrows = borrowMapper.getActiveBorrowsByBook(bid);
            if (!activeBorrows.isEmpty()) {
                List<String> borrowerNames = new ArrayList<>();
                for (Borrow b : activeBorrows) {
                    Student student = studentMapper.selectById(b.getSid());
                    borrowerNames.add(student != null ? student.getName() : "[已删除用户]");
                }

                String errorMsg = "无法删除！以下用户正在借阅：\n" +
                        String.join("\n", borrowerNames);
                session.setAttribute("bookError", errorMsg);
                return "redirect:/Books";
            }

            borrowMapper.deleteById(bid);
            session.setAttribute("bookSuccess", "图书删除成功！");
        return "redirect:/Books";
    }

    @PostMapping("/addBook")
    public String addBook(@RequestParam int bid,
                          @RequestParam String name,
                          @RequestParam String des,
                          @RequestParam double price,
                          HttpSession session) {
        List<Book> books = bookMapper.getAllBooks();
        for(Book i : books){
            if(name.equals(i.getName())){
                session.setAttribute("bookError", "⚠️ 图书已存在，添加失败！");
                return "redirect:/Books";
            }
        }
        Book book = new Book(bid, name, des, price,"待借阅");
        bookMapper.insert(book);
        session.setAttribute("bookSuccess", "🎉 图书添加成功！");
        return "redirect:/Books";
    }
    @PostMapping("/borrow")
    public String borrow(){
        return "redirect:/BookBorrow";
    }

    @GetMapping("/BookBorrow")
    public String showBorrowPage(Model model,HttpSession session) {
        List<Zjh> list = new ArrayList<>();
        for (Borrow b : borrowMapper.getAllBorrows()) {
            Student student = studentMapper.selectById(b.getSid());
            Book book = bookMapper.selectById(b.getBid());
            if (student == null || book == null) {
                continue;
            }
            Zjh zjh = new Zjh(
                    student.getName(),
                    book.getName(),
                    borrowMapper.selectById(b.getId()).getBorrowtime(),
                    b.getBid(),
                    b.getId()
            );
            list.add(zjh);
        }
        if (session.getAttribute("err") != null) {
            model.addAttribute("errorMessage", session.getAttribute("err"));
            session.removeAttribute("err");
        }

        if (session.getAttribute("success") != null) {
            model.addAttribute("successMessage", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        model.addAttribute("list", list);
        model.addAttribute("books", bookMapper.getAllBooks());
        model.addAttribute("students", studentMapper.getAllStudents());
        return "BookBorrow";
    }
    @PostMapping("/deleteBorrow")
    public String deleteBorrow(@RequestParam int id,@RequestParam int bid){
        borrowMapper.deleteById(id);
        Book book = bookMapper.selectById(bid);
        book.setStatus("待借阅");
        bookMapper.updateById(book);
        return "redirect:/BookBorrow";
    }
    @PostMapping("/addBorrow")
    public String addBorrow(@RequestParam int bid,@RequestParam int sid,HttpSession session){
        List<Borrow> borrows = borrowMapper.getAllBorrows();
        for (Borrow b : borrows) {
            if(b.getBid() == bid&&b.getSid() == sid) {
                session.setAttribute("err","⚠️ 借阅信息已存在,添加失败!");
                return "redirect:/BookBorrow";
            }
        }
        borrowMapper.addBorrow(bid,sid, new Date());
        Book book = bookMapper.selectById(bid);
        book.setStatus("借阅中");
        session.setAttribute("success", "🎉 借阅信息添加成功！");
        bookMapper.updateById(book);
        return "redirect:/BookBorrow";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        session.removeAttribute("error");
        session.removeAttribute("success");
        return "register";
    }

    @PostMapping("/register")
    public String zhuce(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String confirmpassword,
                        HttpSession session) {
        // 检查用户名是否已存在
        List<users> list = studentMapper.getAllusers();
        for (users u : list) {
            if (u.getUsername().equals(username)) {
                session.setAttribute("error", "用户名已存在！"); // 统一使用 error 键
                return "register";
            }
        }

        if (!password.equals(confirmpassword)) {
            session.setAttribute("error", "两次输入的密码不一致！");
            return "register";
        }

        users newUsers = new users();
        newUsers.setUsername(username);
        newUsers.setPassword(password);
        userMapper.insert(newUsers);
        session.setAttribute("success", "注册成功，请登录！");
        return "register";
    }
    // 忘记密码页面 GET 请求
    @GetMapping("/forget")
    public String forgetPasswordPage(HttpSession session) {
        session.removeAttribute("error");
        session.removeAttribute("success");
        return "forget"; // 对应 forget.html
    }

    // 忘记密码表单提交 POST 请求
    @PostMapping("/forget")
    public String forgetPassword(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String newpassword,
            HttpSession session) {

        if ("张景淏".equals(username)) {
            session.setAttribute("error", "还想改你爹密码?");
            return "forget";
        }

        // 检查用户是否存在
        List<users> usersList = studentMapper.getAllusers();
        users targetUsers = null;
        for (users u : usersList) {
            if (u.getUsername().equals(username)) {
                if(password.equals(newpassword)){
                    targetUsers = u;
                    break;
                }else{
                    session.setAttribute("error", "两次密码不一致!");
                    return "forget";
                }
            }
        }
        if (targetUsers == null) {
            session.setAttribute("error", "用户不存在,请注册!");
            return "forget";
        }


        targetUsers.setPassword(newpassword);
        userMapper.updateById(targetUsers);
        session.setAttribute("success", "密码重置成功");
        return "forget";
    }
    @GetMapping("/gameStart")
    public String gameStart(){
        return "gameStart";
    }

    @PostMapping("/gameStart")
    public String GameStart(){
        return "gameStart";
    }
}