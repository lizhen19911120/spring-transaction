package com.imooc.spring.transaction.lizhen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public void updateBookStock(String isbn) {
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
    public void updateUserAccount(String username, double price) {
        //检查余额是否不足，若不足，则抛出异常
        String sql2 = "SELECT money FROM account WHERE name = ?";
        double balance = jdbcTemplate.queryForObject(sql2, Double.class, username);
        if (balance < price) {
            throw new UserAccountException("余额不足！");
        }
        String sql = "UPDATE account SET money = money - ? WHERE name = ?";
        jdbcTemplate.update(sql, price, username);
    }
}
