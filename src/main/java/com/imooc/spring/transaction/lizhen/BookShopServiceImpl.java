package com.imooc.spring.transaction.lizhen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lizhen on 2018/9/18.
 */
@Service("bookShopService")
public class BookShopServiceImpl implements BookShopService {

    @Autowired
    private BookShopDao bookShopDao;

    /**
     * 收银员对某一个客户的某一本进行结账行为
     *
     * 1.添加事务注解
     * 使用propagation 指定事务的传播行为，即当前的事务方法被另外一个事务方法调用时如何使用事务。
     * 默认取值为REQUIRED，即使用调用方法的事务
     * REQUIRES_NEW：使用自己的事务，调用的事务方法的事务被挂起。
     *
     * 2.使用isolation 指定事务的隔离级别，最常用的取值为READ_COMMITTED
     * 3.默认情况下 Spring 的声明式事务对所有的运行时异常进行回滚，也可以通过对应的属性进行设置。通常情况下，默认值即可。
     * 4.使用readOnly 指定事务是否为只读。 表示这个事务只读取数据但不更新数据，这样可以帮助数据库引擎优化事务。若真的是一个只读取数据库值得方法，应设置readOnly=true
     * 5.使用timeOut 指定强制回滚之前事务可以占用的时间。
     * @param username 顾客名称
     * @param isbn 书本编号
     */
    @Transactional(
            //这里传播行为是REQUIRES_NEW，意味着结账时对每一本书结账的事务是内层事务，而对一个客户的所有书结账时外层事务，内层的事务不应该影响外层事务。——现实这样也是合理的
            propagation= Propagation.REQUIRES_NEW,
            isolation= Isolation.READ_COMMITTED,
            transactionManager = "transactionManager",
            //事务管理器默认只回滚RuntimeException和Error，但对检查异常不回滚，可以这里设置自己要回滚的检查异常
//            rollbackFor = {UserAccountException.class},
            //如果如下设置，则结账时先减了库存却发现客户余额不足时不会回滚减库存操作，即忽略了updateUserAccount()方法可能抛出的异常对事务的影响——现实中这样不合理
//            noRollbackFor={UserAccountException.class},
            readOnly=false, timeout=3)
    @Override
    public void purchase(String username, String isbn) {

        try{
            //1.获取书的单价
            double price = bookShopDao.findBookPriceByIsbn(isbn);
            //2.更新书的库存
            bookShopDao.updateBookStock(isbn);
            //3.更新用户余额
            bookShopDao.updateUserAccount(username, price);
            //4.针对可能抛出的BookStockException捕获提示，不能影响后续图书的结账事务
        }
        catch (BookStockException e){
            System.out.format("所购图书中编号为：%s的图书没货了\r\n",isbn);
        }
    }
}
