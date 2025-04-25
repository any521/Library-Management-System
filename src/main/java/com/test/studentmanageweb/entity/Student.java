package com.test.studentmanageweb.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @TableId
    Integer sid;
    String name;
    String sex;
    int grade;
    double score;
}
