package com.tecsolvent.wizspeak.model;

/**
 * Created by gopu on 2/4/16.
 */
public class Certification {

	private long id;

	public Certification(long id, int user_id, String certification, String authority, String date_certified) {

		this.id = id;
		this.user_id = user_id;
		this.certification = certification;
		this.authority = authority;
		this.date_certified = date_certified;
	}

	public Certification() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getDate_certified() {
		return date_certified;
	}

	public void setDate_certified(String date_certified) {
		this.date_certified = date_certified;
	}

	private int user_id;
	private String certification;
	private String authority;
	private String date_certified;




}
