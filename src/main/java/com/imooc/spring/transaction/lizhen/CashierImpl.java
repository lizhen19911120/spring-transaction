package com.imooc.spring.transaction.lizhen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lizhen on 2018/9/18.
 */
@Service("cashier")
public class CashierImpl implements Cashier {

    @Autowired
    private BookShopService bookShopService;

    /**
     * 客户结账
     *
     * @param username 顾客名称
     * @param isbns 书本编号
     */
    @Transactional(transactionManager = "transactionManager")
    @Override
    public void checkout(String username, List<String> isbns) {
        for(String isbn : isbns) {
            bookShopService.purchase(username, isbn);
        }
        throw new UserAccountException("aaa");
    }
}
