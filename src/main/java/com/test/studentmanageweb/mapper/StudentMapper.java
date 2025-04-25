package com.test.studentmanageweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.studentmanageweb.entity.Student;
import com.test.studentmanageweb.entity.users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    @Select("select *from users ")
    List<users> getAllusers();

    @Select("select * from student")
    List<Student> getAllStudents();

    @Select("select *from student where sid = #{sid}")
    Student getStudent(int sid);
}
