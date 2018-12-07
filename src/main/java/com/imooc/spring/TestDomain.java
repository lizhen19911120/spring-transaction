package com.imooc.spring;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by lizhen on 2018/9/21.
 */
@Document(collection="cm_testDomain")
public class TestDomain implements Serializable {

    private String id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cmamCreateTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate cmamEra;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate cmamYear;
    private LocalTime time;

    @Id
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCmamCreateTime() {
        return cmamCreateTime;
    }

    public LocalDate getCmamEra() {
        return cmamEra;
    }
    public LocalDate getCmamYear() {
        return cmamYear;
    }

    public void setCmamCreateTime(LocalDateTime cmamCreateTime) {
        this.cmamCreateTime = cmamCreateTime;
    }

    public void setCmamEra(LocalDate cmamEra) {
        this.cmamEra = cmamEra;
    }

    public void setCmamYear(LocalDate cmamYear) {
        this.cmamYear = cmamYear;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
