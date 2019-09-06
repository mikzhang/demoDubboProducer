package com.ran.demo.dubboproducer;

import java.io.Serializable;
import lombok.Data;

@Data
//这里注意下, 一定要实现Serializable接口~, 不然消费者调用返回json时候会序列化失败
public class User implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
}
