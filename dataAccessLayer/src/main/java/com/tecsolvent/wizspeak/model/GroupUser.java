package com.tecsolvent.wizspeak.model;

/**
 * Created by jaison on 15/3/16.
 */
public class GroupUser {



	private int role_id,status;
	private long group_id,user_id,rolesetby_id,invitedby_id,blockedbyid,id;
	private String role_alias,date_requested,date_invited,date_joined,date_roleset,date_exited,date_blocked,name,userPic;

	public GroupUser(int role_id, int status, long group_id, long user_id,String role_alias, long rolesetby_id, String date_invited, long invitedby_id) {
		this.role_id = role_id;
		this.status = status;
		this.group_id = group_id;
		this.user_id = user_id;
		this.role_alias = role_alias;
		this.rolesetby_id = rolesetby_id;
		this.date_invited = date_invited;
		this.invitedby_id = invitedby_id;
	}

	public GroupUser(){}



	//getters


	public String getName() {
		return name;
	}

	public String getUserPic() {
		return userPic;
	}

	public long getId() {
		return id;
	}

	public int getRole_id() {
		return role_id;
	}

	public int getStatus() {
		return status;
	}

	public long getGroup_id() {
		return group_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public long getRolesetby_id() {
		return rolesetby_id;
	}

	public long getInvitedby_id() {
		return invitedby_id;
	}

	public long getBlockedbyid() {
		return blockedbyid;
	}

	public String getRole_alias() {
		return role_alias;
	}

	public String getDate_requested() {
		return date_requested;
	}

	public String getDate_invited() {
		return date_invited;
	}

	public String getDate_joined() {
		return date_joined;
	}

	public String getDate_roleset() {
		return date_roleset;
	}

	public String getDate_exited() {
		return date_exited;
	}

	public String getDate_blocked() {
		return date_blocked;
	}



	//setters


	public void setName(String name) {
		this.name = name;
	}

	public void setUserPic(String userPic) {
		this.userPic = userPic;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setGroup_id(long group_id) {
		this.group_id = group_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public void setRolesetby_id(long rolesetby_id) {
		this.rolesetby_id = rolesetby_id;
	}

	public void setInvitedby_id(long invitedby_id) {
		this.invitedby_id = invitedby_id;
	}

	public void setBlockedbyid(long blockedbyid) {
		this.blockedbyid = blockedbyid;
	}

	public void setRole_alias(String role_alias) {
		this.role_alias = role_alias;
	}

	public void setDate_requested(String date_requested) {
		this.date_requested = date_requested;
	}

	public void setDate_invited(String date_invited) {
		this.date_invited = date_invited;
	}

	public void setDate_joined(String date_joined) {
		this.date_joined = date_joined;
	}

	public void setDate_roleset(String date_roleset) {
		this.date_roleset = date_roleset;
	}

	public void setDate_exited(String date_exited) {
		this.date_exited = date_exited;
	}

	public void setDate_blocked(String date_blocked) {
		this.date_blocked = date_blocked;
	}
}
