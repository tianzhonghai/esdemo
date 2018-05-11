package com.fosun.estest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PagedResult<T> implements Serializable{
    private int page;
    private int total;
    private int limit;
    private T data;
}
