package com.imooc.spring.transaction.lizhen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ParameterMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhen on 2018/9/17.
 */
@Repository("bookShopDao")
public class BookShopDaoImpl implements BookShopDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public double findBookPriceByIsbn(String isbn) {
        String sql = "SELECT price FROM book WHERE isbn = ?";

        return jdbcTemplate.queryForObject(sql, Double.class, isbn);
    }

    /**
     * 从库存中取书，检查是否有货
     * @param isbn 书本编号
     */
    @Override
    public void updateBookStock(String isbn) throws BookStockException {
        //检查书的库存是否足够，若不够，则抛出异常
        String sql2 = "SELECT stock FROM book_stock WHERE isbn = ?";
        int stock = jdbcTemplate.queryForObject(sql2, Integer.class, isbn);
        if (stock == 0) {
            throw new BookStockException("库存不足！");
        }
        String sql = "UPDATE book_stock SET stock = stock - 1 WHERE isbn = ?";
        jdbcTemplate.update(sql, isbn);
    }

    /**
     * 客户购买图书，检查客户金钱是否足够买书
     * @param username 顾客名称
     * @param price 书本价格
     */
    @Override
    public void updateUserAccount(String username, double price) throws UserAccountException {
        //检查余额是否不足，若不足，则抛出异常
        String sql2 = "SELECT money FROM account WHERE name = ?";
        double balance = jdbcTemplate.queryForObject(sql2, Double.class, username);
        if (balance < price) {
            throw new UserAccountException("余额不足！");
        }
        String sql = "UPDATE account SET money = money - ? WHERE name = ?";
        jdbcTemplate.update(sql, price, username);
    }

    @Override
    public void callFunAndPro() {

        /**
         * jdbcTemplate.call 不能用来执行有返回值的function/procedure
         */
        Map<String, Object> result0 = jdbcTemplate.call(connection -> {
            CallableStatement cs = connection.prepareCall("{call simpleproc1(?,?,?)}");
            cs.setString("param1", "d");
            cs.setString("param2", "4");
            cs.setInt("param3", 4);
            return cs;
        }, SqlParameter.sqlTypesToAnonymousParameterList(12, 12, 4));

        System.out.println(result0);


        /**
         * 1、sql表达式都要加上“{}”
         * 2、执行function也是用call，返回值用“? =”表示
         * 3、 cs.registerOutParameter(1, 12); 将第一个参数作为结果参数
         * 4、return cs.getString(1); 返回执行function后的第一个结果参数值
         * 5、不知道function如何使用命名参数？这里用的是参数下标
         */
        String result = jdbcTemplate.execute(con -> {
            CallableStatement cs = con.prepareCall("{? = call error_zhu(?)}");
            ParameterMetaData parameterMetaData = cs.getParameterMetaData();

            System.out.println(parameterMetaData.getParameterType(1));
            System.out.println(parameterMetaData.getParameterTypeName(2));

            cs.registerOutParameter(1, 12);
            cs.setInt(2, 100);
            return cs;
        }, (CallableStatementCallback<String>) cs -> {
            cs.execute();
            return cs.getString(1);
        });

        System.out.println(result);

        /**
         * 1、不同于function，procedure只要写call就好
         * 2、可以注册多个返回参数
         * 3、可以使用数据库定义的存储过程的参数名取代参数下标
         */
        Map<String, Object> result1 = jdbcTemplate.execute(con -> {
            CallableStatement cs = con.prepareCall("{call p(?,?)}");
            cs.registerOutParameter(1, 12);
            cs.registerOutParameter(2, 4);
            cs.setInt("incr_param", 9);
            return cs;
            /**
             * 泛型lambda表达式
             */
        }, (CallableStatementCallback<Map<String, Object>>) cs -> {
            Map<String, Object>  resultsMap = new HashMap<>();
            cs.execute();

            resultsMap.put("version",cs.getString("ver_param"));
            resultsMap.put("amount",cs.getInt("incr_param"));
            return resultsMap;

        });

        for (Map.Entry<String,Object> entry:result1.entrySet())
            System.out.println(entry.getKey()+":"+entry.getValue());
    }


}
