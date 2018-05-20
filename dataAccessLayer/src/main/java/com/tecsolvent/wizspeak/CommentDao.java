package com.tecsolvent.wizspeak;

import com.google.common.collect.Maps;
import com.tecsolvent.wizspeak.model.Comment;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaison on 16/3/16.
 */
public class CommentDao {

	public static Logger logger = Logger.getLogger(CommentDao.class);

	private JdbcTemplate masterJdbcTemplate;
	private JdbcTemplate slaveJdbcTemplate;


	public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
		this.masterJdbcTemplate = masterJdbcTemplate;
	}

	public void setSlaveJdbcTemplate(JdbcTemplate slaveJdbcTemplate) {
		this.slaveJdbcTemplate = slaveJdbcTemplate;
	}





	public ArrayList<Comment> getPostComments(long post_id, int limit) throws Exception{

		ArrayList<Comment> comments = new ArrayList<>();
		try{
			logger.info("SELECT id, user_id , comment, date_commented FROM post_user_comments WHERE post_id = "+post_id);

			return slaveJdbcTemplate.query("SELECT id, user_id , comment, date_commented FROM post_user_comments WHERE post_id = "+post_id+" AND status = 1 ORDER BY id DESC limit "+limit, new CommentMapper());

		}catch (Exception e){

			logger.error(" error getting Post Comments  "+e);

		}

		return comments;

	}



	private class CommentMapper implements ResultSetExtractor<ArrayList<Comment>> {

		public ArrayList<Comment> extractData(ResultSet resultSet) throws SQLException {
			logger.info("inside comment mapper classs");
			ArrayList<Comment> comments = new ArrayList<Comment>();

			while (resultSet.next()){

				logger.info("inside comment mapper classs");

				Comment comment = new Comment(resultSet.getInt("id"), resultSet.getString("comment"), resultSet.getString("date_commented"), resultSet.getLong("user_id"));

				comments.add(comment);
			}

			return comments;
		}


	}



	public ArrayList<Comment> getMoreComments(HashMap<String,Long> commentIds) throws Exception{


		ArrayList<Comment> comments = new ArrayList<>();

		long lastCommentId = commentIds.get("lastCommentId");
		long postId = commentIds.get("postId");
		try {

			comments = slaveJdbcTemplate.query("SELECT id, user_id , comment, date_commented FROM post_user_comments WHERE post_id = "+postId+" AND id < "+lastCommentId+" ORDER BY id DESC limit 5 ", new CommentMapper());
		}catch (Exception e){

			logger.error("error in gettin more comment"+e);
		}


		return comments;
	}




	public Map<String, Object> addComment(Comment comment) throws Exception
	{



		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(slaveJdbcTemplate).withTableName("post_user_comments").usingGeneratedKeyColumns(new String[] { "id" }).usingColumns("user_id","post_id","comment","status","date_commented");
		Map<String, Object> postComment = Maps.newHashMap();
		postComment.put("user_id", comment.getCommenter_id());
		postComment.put("post_id", comment.getPost_id());
		postComment.put("comment", comment.getComment());
		postComment.put("status", comment.getStatus());
		postComment.put("date_commented", comment.getCommentedDate());

		Map<String, Object> status = new HashMap<>();

		try {
			Number id = simpleJdbcInsert.executeAndReturnKey(postComment);

			final long l = id.longValue();
			status.put("id",l);
			status.put("success",1);



		} catch (Exception e) {
			logger.info("in addd new comments  error "+e);

		}

		return status;

	}



}
