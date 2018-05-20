package com.tecsolvent.wizspeak.model;

/**
 * Created by gopu on 2/4/16.
 */
public class Experience {

	private long id;
	private int user_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String company;
	private String jobtitle;
	private String date_from;
	private String date_to;

	public Experience(long id, int user_id, String company, String jobtitle, String date_from, String date_to) {


		this.id = id;
		this.user_id = user_id;
		this.company = company;
		this.jobtitle = jobtitle;
		this.date_from = date_from;
		this.date_to = date_to;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}


	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getDate_from() {
		return date_from;
	}

	public void setDate_from(String date_from) {
		this.date_from = date_from;
	}

	public String getDate_to() {
		return date_to;
	}

	public void setDate_to(String date_to) {
		this.date_to = date_to;
	}
}
