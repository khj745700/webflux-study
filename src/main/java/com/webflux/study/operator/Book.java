package com.webflux.study.operator;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Book {
    private String bookName;
    private String authorName;
    private String penName;
    private int price;
    private int stockQuantity;
}
