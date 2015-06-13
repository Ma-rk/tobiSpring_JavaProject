package core;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionMakerCounting implements ConnectionMaker {
	int counter = 0;
	private ConnectionMaker realConnectionMaker;

	public ConnectionMakerCounting(ConnectionMaker realConnectionMaker) {
		this.realConnectionMaker = realConnectionMaker;
	}

	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		this.counter++;
		return realConnectionMaker.makeConnection();
	}

	public int getCounter() {
		return this.counter;
	}
}