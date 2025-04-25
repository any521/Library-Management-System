package com.test.studentmanageweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName("borrow")
public class Borrow {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer bid;
    private Integer sid;
    private Date borrowtime;

}