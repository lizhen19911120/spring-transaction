package com.imooc.spring.transaction.demo4;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// 如果在此处和transactionManager中都配置了propagation的话，会以此处为准（把required改成never试试）
@Transactional(propagation = Propagation.REQUIRED)
public class AccountServiceImpl implements AccountService {

	private AccountDao accountDao;

	/**
	 * 可以精确到定义需要事务控制的方法
	 * 1.添加事务注解
	 * 使用propagation 指定事务的传播行为，即当前的事务方法被另外一个事务方法调用时如何使用事务。
	 * 默认取值为REQUIRED，即使用调用方法的事务
	 * REQUIRES_NEW：使用自己的事务，调用的事务方法的事务被挂起。
	 *
	 * 2.使用isolation 指定事务的隔离级别，最常用的取值为READ_COMMITTED
	 * 3.默认情况下 Spring 的声明式事务对所有的运行时异常进行回滚，也可以通过对应的属性进行设置。通常情况下，默认值即可。
	 * 4.使用readOnly 指定事务是否为只读。 表示这个事务只读取数据但不更新数据，这样可以帮助数据库引擎优化事务。若真的是一个只读取数据库值得方法，应设置readOnly=true
	 * 5.使用timeOut 指定强制回滚之前事务可以占用的时间。
	 */
	//	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void transfer(String out, String in, Double money) {
		accountDao.outMoney(out, money);
		int i = 10 / 0;
		accountDao.inMoney(in, money);
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

}

