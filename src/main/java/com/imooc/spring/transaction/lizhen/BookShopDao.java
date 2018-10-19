package com.imooc.spring.transaction.lizhen;

/**
 * Created by lizhen on 2018/9/17.
 */
public interface BookShopDao {
    // 根据书号获取书的单价
    double findBookPriceByIsbn(String isbn);
    // 更新书的库存，使书号对应的库存-1
    void updateBookStock(String isbn);
    // 更新用户的账户余额：account的money-price
    void updateUserAccount(String username, double price);

    // 调数据库函数和存储过程
    void callFunAndPro();
}
