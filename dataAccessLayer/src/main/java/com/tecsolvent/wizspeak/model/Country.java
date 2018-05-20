package com.tecsolvent.wizspeak.model;

/**
 * Created by jaison on 21/2/16.
 */
public class Country {

	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;

	}

	public String getName() {
		return name;
	}

	private  String code,name;

	public Country(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public Country(int id, String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}

	public Country() {
	}
}
