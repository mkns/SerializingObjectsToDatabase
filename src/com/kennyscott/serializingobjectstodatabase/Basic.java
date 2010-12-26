package com.kennyscott.serializingobjectstodatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Basic {

	public static void main(String[] args) {
		Basic b = new Basic();
		b.execute(args);
	}
	
	private void execute(String[] args) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			Person person = new Person("Bert", 23);
			oos.writeObject(person);
			oos.close();
			byte[] data = baos.toByteArray();
			QueryRunner queryRunner = getQueryRunner();
			queryRunner.update("INSERT INTO foo SET data = ?", data);
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Person p = (Person) ois.readObject();
			log("Person is called " + p.getName());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void log(Object o) {
		System.out.println(o);
	}
	
	protected DataSource getDataSource() throws SQLException {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUser("user");
		ds.setPassword("pass");
		ds.setServerName("192.168.2.201");
		ds.setPortNumber(3306);
		ds.setDatabaseName("serializedobjects");
		return ds;
	}

	protected QueryRunner getQueryRunner(DataSource dataSource) {
		return new QueryRunner(dataSource);
	}
	
	protected QueryRunner getQueryRunner() throws SQLException {
		return new QueryRunner(getDataSource());
	}

}
