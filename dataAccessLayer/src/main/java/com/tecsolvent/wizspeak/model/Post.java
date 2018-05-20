package com.tecsolvent.wizspeak.model;

import java.util.ArrayList;

/**
 * Created by jaison on 26/2/16.
 */
public class Post {

	private long id;
	private long postby_id;
	private long postto_id;
	private String title,description;
	private String date_posted;
	private int likes;
	private int post_type_id;
	private String link;

	private String wall_type;
	private int vertical_id;
	private byte isPrivate;
	private int status;


	private String postby_name;
	private String postto_name;
	private String postby_pic;
	private String postUserId;
	private String postPageName;


	private boolean iLikes;

	public void setPostto_name(String postto_name) {
		this.postto_name = postto_name;
	}

	private ArrayList<Comment> comments;





	public Post(long id, long postby_id, long postto_id, String title, String description, String date_posted, int likes, int post_type_id, String link, String postby_name, String postby_pic) {
		this.id = id;
		this.postby_id = postby_id;
		this.postto_id = postto_id;
		this.title = title;
		this.description = description;
		this.date_posted = date_posted;
		this.likes = likes;
		this.post_type_id = post_type_id;
		this.link = link;
		this.postby_name = postby_name;
		this.postby_pic = postby_pic;
	}

	public Post(long id, long postby_id, long postto_id, String title, String description, String link, int post_type_id,String wall_type,String date_posted,byte is_private) {

		this.id = id;
		this.postby_id = postby_id;
		this.postto_id = postto_id;
		this.title = title;
		this.description = description;
		this.link = link;
		this.post_type_id = post_type_id;
		this.wall_type = wall_type;
		this.date_posted = date_posted;
		this.isPrivate = is_private;

	}

	public Post(long id, long postby_id, long postto_id, String title, String link) {

		this.id = id;
		this.postby_id = postby_id;
		this.postto_id = postto_id;
		this.title = title;
		this.link = link;

	}

	public Post(int status, long postby_id, long postto_id, String title, String description, int post_type_id, int vertical_id, String wall_type, byte isPrivate, String link) {
		this.status = status;
		this.postby_id = postby_id;
		this.postto_id = postto_id;
		this.title = title;
		this.description = description;
		this.post_type_id = post_type_id;
		this.vertical_id = vertical_id;
		this.wall_type = wall_type;
		this.isPrivate = isPrivate;
		this.link = link;
	}

	public Post(int status, String postby_name, String postto_name, String title, String description, int post_type_id, int vertical_id, String wall_type, byte isPrivate, String link) {
		this.status = status;
		this.postby_name = postby_name;
		this.postto_name = postto_name;
		this.title = title;
		this.description = description;
		this.post_type_id = post_type_id;
		this.vertical_id = vertical_id;
		this.wall_type = wall_type;
		this.isPrivate = isPrivate;
		this.link = link;
	}

	public Post() {

	}


//getters


	public String getPostUserId() {
		return postUserId;
	}

	public String getPostto_name() {
		return postto_name;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}

	public boolean isiLikes() {
		return iLikes;
	}

	public String getWall_type() {
		return wall_type;
	}

	public int getVertical_id() {
		return vertical_id;
	}

	public byte getIsPrivate() {
		return isPrivate;
	}

	public int getStatus() {
		return status;
	}

	public String getPostby_name() {
		return postby_name;
	}


	public long getPostto_id() {
		return postto_id;
	}
	public long getId() {
		return id;
	}

	public long getPostby_id() {
		return postby_id;
	}

	public String getTitle() {
		return title;
	}

	public String getPostPageName() {
		return postPageName;
	}

	public String getDescription() {
		return description;

	}
	public String getDate_posted() {
		return date_posted;
	}

	public int getLikes() {
		return likes;
	}

	public int getPost_type_id() {
		return post_type_id;
	}

	public String getLink() {
		return link;
	}

	public String getPostby_pic() {
		return postby_pic;
	}

	//setters


	public void setPostUserId(String postUserId) {
		this.postUserId = postUserId;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}

	public void setiLikes(boolean iLikes) {
		this.iLikes = iLikes;
	}

	public void setPostby_name(String postby_name) {
		this.postby_name = postby_name;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPostby_id(long postby_id) {
		this.postby_id = postby_id;
	}

	public void setPostto_id(long postto_id) {
		this.postto_id = postto_id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDate_posted(String date_posted) {
		this.date_posted = date_posted;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public void setPost_type_id(int post_type_id) {
		this.post_type_id = post_type_id;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setPostby_pic(String postby_pic) {
		this.postby_pic = postby_pic;
	}

	public void setWall_type(String wall_type) {
		this.wall_type = wall_type;
	}

	public void setVertical_id(int vertical_id) {
		this.vertical_id = vertical_id;
	}

	public void setIsPrivate(byte isPrivate) {
		this.isPrivate = isPrivate;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setPostPageName(String postPageName) {
		this.postPageName = postPageName;
	}

	@Override
	public String toString() {
		return "Post{" +
				"id=" + id +
				", postby_id=" + postby_id +
				", postto_id=" + postto_id +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", date_posted='" + date_posted + '\'' +
				", likes=" + likes +
				", post_type_id=" + post_type_id +
				", link='" + link + '\'' +
				", wall_type='" + wall_type + '\'' +
				", vertical_id=" + vertical_id +
				", isPrivate=" + isPrivate +
				", status=" + status +
				", postby_name='" + postby_name + '\'' +
				", postby_pic='" + postby_pic + '\'' +
				", iLikes=" + iLikes +
				", comments=" + comments +
				'}';
	}
}
