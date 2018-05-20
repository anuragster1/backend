package com.tecsolvent.wizspeak.model;

/**
 * Created by jaison on 28/2/16.
 */
public class Comment {

	private int id;
	private String comment,commenterName,cUserPic,commentedDate;
	private int likes,status;
	private long commenter_id,post_id;
	private boolean iLikes;


	public int getStatus() {return status;}

	public long getPost_id() {return post_id;}

	public boolean isiLikes() {
		return iLikes;
	}

	public String getCommenterName() {
		return commenterName;
	}

	public int getId() {
		return id;
	}

	public String getComenterName() {
		return commenterName;
	}

	public String getComment() {
		return comment;
	}

	public String getcUserPic() {
		return cUserPic;
	}

	public String getCommentedDate() {
		return commentedDate;
	}

	public int getLikes() {
		return likes;
	}

	public long getCommenter_id() {
		return commenter_id;
	}
//setteres


	public void setStatus(int status) {this.status = status;}

	public void setPost_id(long post_id) { this.post_id = post_id; }

	public void setCommenterName(String commenterName) {
		this.commenterName = commenterName;
	}

	public void setiLikes(boolean iLikes) {
		this.iLikes = iLikes;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCommenter_id(long commenter_id) {
		this.commenter_id = commenter_id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setComenterName(String cName) {
		this.commenterName = cName;
	}

	public void setcUserPic(String cUserPic) {
		this.cUserPic = cUserPic;
	}

	public void setCommentedDate(String commentedDate) {
		this.commentedDate = commentedDate;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public Comment(int id, String cName, String cUserPic, String commentedDate, int likes) {
		this.id = id;
		this.commenterName = cName;
		this.cUserPic = cUserPic;
		this.commentedDate = commentedDate;
		this.likes = likes;
	}

	public Comment() {
	}

	public Comment(int id, String comment, String commentedDate, long commenter_id) {
		this.id = id;
		this.comment = comment;
		this.commentedDate = commentedDate;
		this.commenter_id = commenter_id;
	}
}
