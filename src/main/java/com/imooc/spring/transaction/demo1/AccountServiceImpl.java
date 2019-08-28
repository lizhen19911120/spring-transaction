package com.imooc.spring.transaction.demo1;

import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by zZ on 2016/5/22.
 */
public class AccountServiceImpl implements AccountService {

	private AccountDao accountDao;

	private TransactionTemplate transactionTemplate;

	@Override
	public String transfer(final String out, final String in, final Double money) {
		return transactionTemplate.execute(transactionStatus->{

			accountDao.outMoney(out, money);
			//执行事务发生RunTimeException，回滚事务操作
//			int i = 10 / 0;
			accountDao.inMoney(in, money);
			return "success";
		});
		//这样也可以，这样的话是传入一个TransactionCallbackWithoutResult类(无返回值)实例，而不是TransactionCallback接口(有返回值)实例
//		new TransactionCallbackWithoutResult() {
//			@Override
//			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
//				accountDao.outMoney(out, money);
////				int i = 10 / 0;
//				accountDao.inMoney(in, money);
//			}
//		}
	}




	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
}
