package com.tecsolvent.wizspeak.model;

/**
 * Created by jaison on 23/2/16.
 */
public class User {

	private long id;
	private int city,state,country;
	private boolean activated;
	private String first_name, last_name, dob, email, profileStatus, profilePic, getCoverPic, userId, gender, status;
	private byte mentor;


	public User() {
	}

	public User(long id,String userId, String first_name, String last_name,String dob, String email) {
		this.id = id;
		this.userId = userId;
		this.first_name = first_name;
		this.last_name = last_name;
		this.dob = dob;
		this.email = email;
	}

	public User(long id, int city, int state, int country, byte mentor, String first_name, String last_name, String dob, String email, String profileStatus) {
		this.id = id;
		this.city = city;
		this.state = state;
		this.country = country;
		this.mentor = mentor;
		this.activated = activated;
		this.first_name = first_name;
		this.last_name = last_name;
		this.dob = dob;
		this.email = email;
		this.profileStatus = profileStatus;
	}

	public User(long id, int city, int state, int country, byte mentor, String first_name, String last_name, String dob, String email, String profileStatus,String profilePic) {
		this.id = id;
		this.city = city;
		this.state = state;
		this.country = country;
		this.mentor = mentor;
		this.activated = activated;
		this.first_name = first_name;
		this.last_name = last_name;
		this.dob = dob;
		this.email = email;
		this.profileStatus = profileStatus;
		this.profilePic = profilePic;
	}

	public String getGetCoverPic() {
		return getCoverPic;
	}

	public void setGetCoverPic(String getCoverPic) {
		this.getCoverPic = getCoverPic;
	}

	public long getId() {
		return id;

	}

	//setters

	public void setId(long id) {
		this.id = id;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public int getCity() {

		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCountry() {
		return country;
	}

	public void setCountry(int country) {
		this.country = country;
	}

	public byte getMentor() {
		return mentor;
	}

	public void setMentor(byte mentor) {
		this.mentor = mentor;
	}

	public String getFirst_name() {
		return first_name;
	}
//getter

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfileStatus() {
		return profileStatus;
	}

	public void setProfileStatus(String profileStatus) {
		this.profileStatus = profileStatus;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
