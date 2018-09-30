package com.imooc.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Created by lizhen on 2018/9/26.
 */

@RestController
@RequestMapping("/mongo")
public class MongoResource {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/queryTestDomain")
    public TestDomain queryTestDomain(@RequestParam(value = "id",required = false) String id){
        TestDomain testDomain = mongoTemplate.findById(id,TestDomain.class,"cm_testDomain");
        return testDomain;
    }

    /**
     * 接收请求体中的json字符串，通过HttpMessageConverter转为对应实体类？
     * @RequestBody
     * @param testDomain
     * @return
     */
    @PostMapping("/saveTestDomain")
    public TestDomain saveTestDomain(@RequestBody TestDomain testDomain){
        mongoTemplate.save(testDomain,"cm_testDomain");
        System.out.println(testDomain.getCmamCreateTime());
        System.out.println(testDomain.getCmamEra());
        return testDomain;
    }

    /**
     * 使用springmvc的接收参数机制配合@DateTimeFormat注解，将前端的时间参数注入到TestDomain里
     * @param testDomain
     * @return
     */
    @PostMapping("/saveTestDomain1")
    public TestDomain saveTestDomain1(TestDomain testDomain){
        mongoTemplate.save(testDomain,"cm_testDomain");
        System.out.println(testDomain.getCmamCreateTime());
        System.out.println(testDomain.getCmamEra());
        return testDomain;
    }

    /**
     * 接收前端参数为LocalDateTime类对象
     * @RequestParam&@DateTimeFormat
     * @param cmamCreateTime
     * @return
     */
    @GetMapping("/convertDateTime")
    public LocalDateTime convertDateTime(@RequestParam(value = "cmamCreateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime cmamCreateTime){
        System.out.println(cmamCreateTime);
        return cmamCreateTime;
    }

}
