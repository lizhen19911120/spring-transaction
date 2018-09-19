package com.imooc.spring.transaction.lizhen;

/**
 * Created by lizhen on 2018/9/18.
 */
public class BookStockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BookStockException() {
        super();
    }

    public BookStockException(String arg0, Throwable arg1, boolean arg2,
                              boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public BookStockException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public BookStockException(String arg0) {
        super(arg0);
    }

    public BookStockException(Throwable arg0) {
        super(arg0);
    }
}
