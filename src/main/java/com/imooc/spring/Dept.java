package com.imooc.spring;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by lizhen on 2018/10/16.
 */
@Document(collection="cm_dept")
public class Dept {

    private Integer id;
    private String name;

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

    @Override
    public String toString() {
        return "Dept{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
