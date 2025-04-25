package com.test.studentmanageweb.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @TableId
    Integer bid;
    String name;
    String des;
    double price;
    String status;
}
