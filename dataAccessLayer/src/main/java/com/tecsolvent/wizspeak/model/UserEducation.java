package com.tecsolvent.wizspeak.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by gopu on 18/3/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserEducation {

	private long id;
	private int user_id;
	private String education;
	private String institute;
	private String university;
	private String company;
	private String jobtitle;
	private String date_from;
	private String date_to;

	public UserEducation(int user_id, String company, String jobtitle, String date_from, String date_to) {


		this.user_id = user_id;
		this.company = company;
		this.jobtitle = jobtitle;
		this.date_from = date_from;
		this.date_to = date_to;

	}

	public UserEducation(long id, int user_id, String education, String institute, String university, String date_from, String date_to) {
		this.id = id;
		this.user_id = user_id;
		this.education = education;
		this.institute = institute;
		this.university = university;
		this.date_from = date_from;
		this.date_to = date_to;
	}

	public UserEducation() {

	}

	public UserEducation(String education, String institute, String university, String date_from, String date_to, Long id) {
		this.id = id;

		this.education = education;
		this.institute = institute;
		this.university = university;
		this.date_from = date_from;
		this.date_to = date_to;
	}

	public UserEducation(long id, int user_id, String company, String jobtitle, String date_from, String date_to) {

		this.id = id;
		this.user_id = user_id;
		this.company = company;
		this.jobtitle = jobtitle;
		this.date_from = date_from;
		this.date_to = date_to;

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

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
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

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
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

	public void education(String education) {
	}

	public void institute(String institute) {
	}

	public void university(String university) {
	}

	public void date_from(String date_from) {
	}

	public void date_to(String date_to) {
	}


}
