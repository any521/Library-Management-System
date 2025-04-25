package com.test.studentmanageweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Zjh {
    String name;
    String title;
    Date date;
    Integer bid;
    Integer id;
}
