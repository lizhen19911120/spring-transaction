package com.imooc.spring;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by lizhen on 2018/10/16.
 */
@Document(collection="cm_employee")
public class Employee {

    private Integer id;
    private String name;
    private Integer age;
    private Dept dept;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", dept=" + dept +
                '}';
    }
}
