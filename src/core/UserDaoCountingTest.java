package core;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import dao.DaoFactoryCounting;
import dao.UserDao;
import entity.UserEntity;

public class UserDaoCountingTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactoryCounting.class);
		UserDao dao = context.getBean("userDao", UserDao.class);

		UserEntity user = new UserEntity();
		user.setId("idid");
		user.setName("namename");
		user.setPassword("pwpw");

		dao.add(user);
		System.out.println("registerd [" + user.getId() + "]");
		
		UserEntity user1 = new UserEntity();
		user1.setId("idid2");
		user1.setName("namename2");
		user1.setPassword("pwpw2");
		
		dao.add(user1);
		System.out.println("registerd [" + user1.getId() + "]");


		UserEntity user2 = dao.get(user.getId());

		System.out.println("retrieved [" + user2.getId() + "]");
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());

		UserEntity user3 = dao.get(user1.getId());
		
		System.out.println("retrieved [" + user3.getId() + "]");
		System.out.println(user3.getName());
		System.out.println(user3.getPassword());

		ConnectionMakerCounting cmc = context.getBean("connectionMaker", ConnectionMakerCounting.class);
		System.out.println("count: " + cmc.getCounter());
	}
}