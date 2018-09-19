package com.imooc.spring.transaction.lizhen;

import java.util.List;

/**
 * Created by lizhen on 2018/9/18.
 */
public interface Cashier {
    void checkout(String username, List<String> isbns);
}
