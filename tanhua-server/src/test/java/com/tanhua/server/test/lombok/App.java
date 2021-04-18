package com.tanhua.server.test.lombok;

import org.junit.Test;

public class App {
    @Test
    public void test1() {
        Person p = new Person();
        p.setId(100);
        p.setName("jack");
    }

    @Test
    public void test2() {
        Person person = Person.builder().id(100).name("jack").build();
        System.out.println("person = " + person);
    }
}
