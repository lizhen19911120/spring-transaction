package com.imooc.spring.transaction.lizhen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by lizhen on 2018/9/18.
 */
//scanBasePackages这里写不写都一样，因为spring配置在Config中写好了
@SpringBootApplication(scanBasePackages = {"com.imooc.spring.transaction.lizhen"})
public class Test {

    public static void main(String[] args) {
        SpringApplication.run(Test.class, args);
        SpringTransitionTest springTransitionTest = new SpringTransitionTest();
        springTransitionTest.testTransactionPropagation();
    }
}
