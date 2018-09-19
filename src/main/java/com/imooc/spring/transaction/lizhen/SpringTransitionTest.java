package com.imooc.spring.transaction.lizhen;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by lizhen on 2018/9/18.
 */
public class SpringTransitionTest {

    private ApplicationContext ctx = null;
    private BookShopDao bookShopDao = null;
    private BookShopService bookShopService = null;
    private Cashier cashier = null;

    {
        ctx = new AnnotationConfigApplicationContext(Config.class);
        bookShopDao = ctx.getBean(BookShopDao.class);
        bookShopService = ctx.getBean(BookShopService.class);
        cashier = ctx.getBean(Cashier.class);
    }

    @Test
    public void testBookShopDaoFindPriceByIsbn() {
        System.out.println(bookShopDao.findBookPriceByIsbn("1"));
    }

    @Test
    public void testBookShopDaoUpdateBookStock(){
        bookShopDao.updateBookStock("1");
    }

    @Test
    public void testBookShopDaoUpdateUserAccount(){
        bookShopDao.updateUserAccount("aaa", 100);
    }

    @Test
    public void testBookShopService(){
        bookShopService.purchase("aaa", "1");
    }

    /**
     * 不能直接在这里测试，根本不进入DataSourceTransactionManager的事务方法中去，只在junit中逗留
     * 一个可能的解释是@Transactional注解需要spring容器的某些解析器进行处理，不然会被忽略。
     */
    @Test
    public void testTransactionPropagation(){
        cashier.checkout("aaa", Arrays.asList("1", "2","3"));
    }
}
