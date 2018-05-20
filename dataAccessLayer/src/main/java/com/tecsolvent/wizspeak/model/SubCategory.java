package com.tecsolvent.wizspeak.model;

/**
 * Created by jaison on 2/3/16.
 */

public class SubCategory {

	private int id,categoryId;
	private String name,dateCreated;
	private int type;

	public SubCategory() {
	}

	public SubCategory(String name, int categoryId, int id) {
		this.name = name;
		this.categoryId = categoryId;
		this.id = id;
	}

	//setters
	public void setId(int id) {
		this.id = id;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setType(int type) {
		this.type = type;
	}

	//getters


	public int getId() {
		return id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public String getName() {
		return name;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public int getType() {
		return type;
	}


}
