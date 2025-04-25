package com.test.studentmanageweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.studentmanageweb.entity.Borrow;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public  interface BorrowMapper extends BaseMapper<Borrow> {

    @Insert("INSERT INTO borrow (bid, sid, borrowtime) " +
            "VALUES (#{bid}, #{sid}, #{borrowtime})")
    void addBorrow(@Param("bid") int bid,
                   @Param("sid") int sid,
                   @Param("borrowtime")Date borrowtime);

    @Select("select *from borrow")
    List<Borrow> getAllBorrows();

    @Select("select *from borrow where bid = #{bid}")
    List<Borrow> getActiveBorrowsByBook(int bid);

    @Select("select *from borrow where sid = #{sid}")
    List<Borrow> getStudentBorrowBooks(int sid);
}
