package com.test.studentmanageweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.studentmanageweb.entity.Book;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface BookMapper extends BaseMapper<Book>{

    @Select("select *from book")
    List<Book> getAllBooks();

}
