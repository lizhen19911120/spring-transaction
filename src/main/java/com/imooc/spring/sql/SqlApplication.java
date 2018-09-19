package com.imooc.spring.sql;//package com.imooc.spring.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by lizhen on 2018/9/14.
 */
@SpringBootApplication
//@ContextConfiguration(locations = "classpath:applicationContext.xml")
@ImportResource("classpath:applicationContext.xml")
@PropertySource(value = "classpath:数据表account.sql", factory = SqlPropertySourceFactory.class)
public class SqlApplication implements EnvironmentAware {

    private Environment env;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${create_table_account}")
    private String createAccount;

    @Value("${insert_table_account}")
    private String insertAccount;

    @Value("${create_table_book}")
    private String createBook;

    @Value("${create_table_book_stock}")
    private String createBookStock;

    @Value("${insert_table_book}")
    private String insertBook;

    @Value("${insert_table_book_stock}")
    private String insertBookStock;

    //为了使用非静态方法env.getProperty()等，构建一个bean方法
    @Bean
    public String initSqlBean() {
        //可以通过Environment读取
        System.out.println("SQL_1:" + env.getProperty("create_table_account"));
        //可以用@Value读取
        System.out.println("SQL_2:" + insertAccount);
        //执行数据库DDL操作，创建表
//        jdbcTemplate.execute(createAccount);
//        jdbcTemplate.execute(createBook);
//        jdbcTemplate.execute(createBookStock);

        String[] insertStr = insertAccount.split(";");
        //执行数据库DML操作，插入数据
        jdbcTemplate.batchUpdate(insertStr);
//        String[] insertStr1 = insertBook.split(";");
//        jdbcTemplate.batchUpdate(insertStr1);
//        String[] insertStr2 = insertBookStock.split(";");
//        jdbcTemplate.batchUpdate(insertStr2);
        return "-------------";
    }

    public static void main(String[] args) {
        SpringApplication.run(SqlApplication.class, args);


    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
