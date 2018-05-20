package com.tecsolvent.wizspeak.model;


/**
 * Created by sand on 25/2/16.
 */
public class Group {

	private long id,createdby_id;
	private int vertical_id;
	private  String name;

	private String description,date_created,customUrl;
	private int sub_category_id,type,status;
	private String invites,profilePic,coverPic;

	public Group(long id, int vertical_id, String name) {
		this.id = id;
		this.vertical_id = vertical_id;
		this.name = name;
	}

	public Group() {

	}

	public void setId(long id) {


		this.id = id;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public void setCoverPic(String coverPic) {
		this.coverPic = coverPic;
	}

	public void setCustomUrl(String customUrl) {
		this.customUrl = customUrl;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}

	public void setSub_category_id(int sub_category_id) {
		this.sub_category_id = sub_category_id;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setCreatedby_id(long createdby_id) {
		this.createdby_id = createdby_id;
	}

	public void setInvites(String invites) {
		this.invites = invites;
	}

	public void setVertical_id(int vertical_id) {
		this.vertical_id = vertical_id;
	}

	public void setName(String name) {
		this.name = name;
	}



	//getters

	public long getId() {

		return id;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public String getCoverPic() {
		return coverPic;
	}

	public String getCustomUrl() {
		return customUrl;
	}

	public String getDescription() {
		return description;
	}

	public String getDate_created() {
		return date_created;
	}

	public int getSub_category_id() {
		return sub_category_id;
	}

	public int getType() {
		return type;
	}

	public int getStatus() {
		return status;
	}

	public long getCreatedby_id() {
		return createdby_id;
	}

	public String getInvites() {
		return invites;
	}

	public int getVertical_id() {
		return vertical_id;
	}

	public String getName() {
		return name;
	}


}
