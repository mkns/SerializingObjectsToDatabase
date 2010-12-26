package com.kennyscott.serializingobjectstodatabase;

import java.io.Serializable;

/**
 * Basic bean, which is ultimately instantiated various times and stored in a
 * database, then read out again, hopefully.
 * 
 * @author mkns
 * 
 */
public class Person implements Serializable {

	private static final long serialVersionUID = -89741507291012920L;

	/**
	 * The person's name
	 */
	private String name;

	/**
	 * The person's age (or in reality, their shirt number)
	 */
	private int age;

	/**
	 * Default constructor
	 */
	public Person() {
	}

	/**
	 * Main constructor used by application
	 * 
	 * @param name
	 * @param age
	 */
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	/**
	 * Gets the name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the age
	 * 
	 * @return
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Sets the age
	 * 
	 * @param age
	 */
	public void setAge(int age) {
		this.age = age;
	}

}
