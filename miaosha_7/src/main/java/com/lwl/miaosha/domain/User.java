package com.lwl.miaosha.domain;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
public class User {
    private int id;
    private String name;

    //要有构造方法才可以依赖注入
    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
