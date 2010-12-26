package com.kennyscott.serializingobjectstodatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Simple class showing how to create a bunch of objects, serialize them and
 * stick them in a database, and later retrieve one and make sure we can read
 * the data back out again.
 * 
 * @author mkns
 * 
 */
public class Basic {

	/**
	 * It's probably easier to read the source, but ultimately, this method:
	 * 
	 * <ol>
	 * <li>truncates the db table
	 * <li>inserts 4 people
	 * <li>retrieves one of the people from the database
	 * <li>reads some data to make sure it's valid
	 * </ol>
	 * 
	 * @param args
	 */
	private void execute(String[] args) {
		try {
			truncateTable();
			storePerson("Michael", 23);
			storePerson("Steve", 13);
			storePerson("Tim", 21);
			storePerson("Shaquille", 36);
			Person person = this.getPersonFromDatabase("Steve");
			log("Person is called " + person.getName() + " and their age is " + person.getAge());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * CLI program needs main()
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Basic b = new Basic();
		b.execute(args);
	}

	/**
	 * Truncates the db table
	 * 
	 * @throws SQLException
	 */
	private void truncateTable() throws SQLException {
		QueryRunner queryRunner = getQueryRunner();
		queryRunner.update("TRUNCATE TABLE Person");
	}

	/**
	 * Given a name and an age, this creates a Person object, serializes it and
	 * stores it in the table
	 * 
	 * @param name
	 * @param age
	 * @throws SQLException
	 * @throws IOException
	 */
	private void storePerson(String name, int age) throws SQLException, IOException {
		QueryRunner queryRunner = getQueryRunner();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		Person person = new Person(name, age);
		oos.writeObject(person);
		oos.close();
		byte[] data = baos.toByteArray();
		queryRunner.update("INSERT INTO Person SET name = ?, data = ?", name, data);
	}

	/**
	 * Given a name, this performs a db query on the table, reads in the
	 * serialized object and returns it
	 * 
	 * @param name
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Person getPersonFromDatabase(String name) throws SQLException, IOException, ClassNotFoundException {
		QueryRunner queryRunner = getQueryRunner();
		MapListHandler rsh = new MapListHandler();
		List<Map<String, Object>> result = queryRunner.query("SELECT data FROM Person WHERE name=?", rsh, name);
		Map<String, Object> row = result.get(0);
		byte[] dataFromDatabase = (byte[]) row.get("data");
		ByteArrayInputStream bais = new ByteArrayInputStream(dataFromDatabase);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Person person = (Person) ois.readObject();
		return person;
	}

	/**
	 * I can't be bothered typing System.out.println()
	 * 
	 * @param o
	 */
	private void log(Object o) {
		System.out.println(o);
	}

	/**
	 * Creates and returns a DataSource
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected DataSource getDataSource() throws SQLException {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUser("user");
		ds.setPassword("pass");
		ds.setServerName("192.168.2.201");
		ds.setPortNumber(3306);
		ds.setDatabaseName("serializedobjects");
		return ds;
	}

	/**
	 * Generates a QueryRunner using the default info held in getDataSource()
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected QueryRunner getQueryRunner() throws SQLException {
		return new QueryRunner(getDataSource());
	}

}
