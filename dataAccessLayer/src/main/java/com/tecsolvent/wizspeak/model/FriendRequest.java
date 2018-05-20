package com.tecsolvent.wizspeak.model;

/**
 * Created by gopu on 31/3/16.
 */
public class FriendRequest {
	public int id;
	public String user_id_a;
	public String user_id_b;


	public FriendRequest(int id, String user_id_a, String user_id_b, String request_status) {
		this.id = id;
		this.user_id_a = user_id_a;
		this.user_id_b = user_id_b;
		this.request_status = request_status;

	}

	public FriendRequest() {

	}

	public int getId(String id) {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser_id_a(String request_status) {
		return user_id_a;
	}

	public void setUser_id_a(String user_id_a) {
		this.user_id_a = user_id_a;
	}

	public String getUser_id_b(String user_id_b) {
		return this.user_id_b;
	}

	public void setUser_id_b(String user_id_b) {
		this.user_id_b = user_id_b;
	}

	public String getRequest_status(String user_id_a) {
		return request_status;
	}

	public void setRequest_status(String request_status) {
		this.request_status = request_status;
	}

	public String request_status;
}
