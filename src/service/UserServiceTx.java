package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import entity.UserEntity;

public class UserServiceTx implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceTx.class);

	/*
	 * DI codes
	 */
	UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	PlatformTransactionManager transactionManager;

	public void setTransactionManager(PlatformTransactionManager transactionMansger) {
		this.transactionManager = transactionMansger;
	}

	/*
	 * functional methods
	 */
	public void add(UserEntity user) {
		this.userService.add(user);
	}

	public void upgradeLevelOfEveryUser() {
		logger.info("upgradeLevelOfEveryUser()==>");
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		logger.info("transaction info: begin on [{}]", this.toString());
		try {
			this.userService.upgradeLevelOfEveryUser();
			this.transactionManager.commit(status);
			logger.info("transaction info: commit");
		} catch (RuntimeException e) {
			this.transactionManager.rollback(status);
			logger.info("transaction info: rollback");
			throw e;
		} finally {
			logger.info("upgradeLevelOfEveryUser()<==");
		}
	}
}