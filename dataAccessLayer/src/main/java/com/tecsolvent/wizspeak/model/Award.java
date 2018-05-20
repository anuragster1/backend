package com.tecsolvent.wizspeak.model;

/**
 * Created by gopu on 2/4/16.
 */
public class Award {

	private long id;
	private int user_id;
	private String award;

	public Award(long id, int user_id, String award, String authority, String date_awarded ) {
		this.id = id;
		this.user_id = user_id;
		this.award = award;
		this.authority = authority;
		this.date_awarded = date_awarded;

	}

	public Award() {

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

	public String getAward() {
		return award;
	}

	public void setAward(String award) {
		this.award = award;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getDate_awarded() {
		return date_awarded;
	}

	public void setDate_awarded(String date_awarded) {
		this.date_awarded = date_awarded;
	}

	private String authority;
	private String date_awarded;


}
