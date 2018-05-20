package com.tecsolvent.wizspeak;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaison on 16/3/16.
 */
public class LikeDao {

	public static Logger logger = Logger.getLogger(LikeDao.class);

	private JdbcTemplate masterJdbcTemplate;
	private JdbcTemplate slaveJdbcTemplate;



	public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
		this.masterJdbcTemplate = masterJdbcTemplate;
	}

	public void setSlaveJdbcTemplate(JdbcTemplate slaveJdbcTemplate) {
		this.slaveJdbcTemplate = slaveJdbcTemplate;
	}




	public HashMap<Integer, Long> getLike(long post_id, int item_type) throws Exception{

		HashMap<Integer, Long> countLikes = new HashMap<>();
		try{

			countLikes =  slaveJdbcTemplate.query("SELECT id,user_id  from post_user_likes where item_id = "+post_id+" AND item_type = "+item_type+" AND status = 1",new PostLikeMapper());
		}catch (Exception e){
			logger.error(" error selecting like count "+e);

		}

		return countLikes;
	}


	public HashMap<Integer, Long> getLikes(long post_id,int item_type) throws Exception{

		HashMap<Integer, Long> whoLikes = new HashMap<>();
		try{


			whoLikes =  slaveJdbcTemplate.query("SELECT id,user_id  from post_user_likes where item_id = "+post_id+" AND item_type = "+item_type+" AND status = 1",new PostLikeMapper());
		}catch (Exception e){
			logger.error("error selecting likes details"+e);

		}
		return whoLikes;

	}

	private class PostLikeMapper implements ResultSetExtractor<HashMap<Integer, Long>> {

		public HashMap<Integer, Long> extractData(ResultSet resultSet) throws SQLException {
			HashMap<Integer,Long> likes = new HashMap<>();

			while(resultSet.next()){

				likes.put(resultSet.getInt("id"),resultSet.getLong("user_id"));
			}
			return likes;
		}
	}




	public HashMap<String, String> checkLike(Map<String, Object> like) throws Exception
	{
		HashMap<String, String> checkLikes = new HashMap<>();
		try
		{

			checkLikes =  this.slaveJdbcTemplate.query("SELECT id,status FROM post_user_likes WHERE user_id = " + (String)like.get("user_id") + " AND item_id = " + (String)like.get("item_id") + " AND item_type  = " + (String)like.get("item_type"), new CheckLikeMapper());
		}
		catch (Exception e)
		{
			logger.error("checking old likes columns "+e);

		}
		return checkLikes;
	}

	private class CheckLikeMapper implements ResultSetExtractor<HashMap>
	{
		private CheckLikeMapper() {}

		public HashMap<String, String> extractData(ResultSet resultSet)
				throws SQLException
		{

			HashMap<String, String> like = new HashMap<>();
			resultSet.next();
			like.put("id", resultSet.getString("id"));
			like.put("status", resultSet.getString("status"));

			return like;
		}
	}

	public boolean updateLike(Map<String, String> l) throws Exception
	{
		boolean result= false;
		try
		{
			String sql = "UPDATE post_user_likes SET status = "+Integer.parseInt(l.get("status"))+" WHERE id = " + Integer.parseInt(l.get("id"));

			this.slaveJdbcTemplate.execute(sql);
			result =  true;

		}
		catch (Exception e)
		{
			logger.error(" error update  post_user_likes "+e );

		}
		return result;
	}


	public boolean addLike(Map<String, Object> like)throws Exception
	{
		//change to a function
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);


		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(slaveJdbcTemplate).withTableName("post_user_likes").usingColumns("user_id","item_id","item_type","status","date_liked");
		Map<String, Object> postLike = Maps.newHashMap();
		postLike.put("user_id", like.get("user_id"));
		postLike.put("item_id", like.get("item_id"));
		postLike.put("status", 1);
		postLike.put("item_type", like.get("item_type"));
		postLike.put("date_liked", currentTime);
		try {

			simpleJdbcInsert.execute(postLike);
		} catch (Exception e) {
			logger.error("error in addd new like "+e);
		}
		return true;

	}


	public boolean removeLike(Map<String, Object> l) throws Exception
	{

		try
		{
			String sql = "UPDATE post_user_likes SET status = "+l.get("status")+" WHERE item_id = " + l.get("item_id")+" AND item_type= " + l.get("item_type")+" AND user_id ="+ l.get("user_id");

			this.slaveJdbcTemplate.execute(sql);
		}
		catch (Exception e)
		{
			logger.error("error update post like status "+e);
		}
		return true;
	}




	public boolean checkMyLike(long postId,long userId,byte itemType) throws Exception
	{
		boolean checkLikes = false;

		try{

			checkLikes =  this.slaveJdbcTemplate.query("SELECT id FROM post_user_likes WHERE user_id = "+userId+" AND item_id = "+postId+" AND item_type = "+itemType+" AND status = 1", new CheckMyLikeMapper());

		}catch (Exception e){

			logger.error("error in checking my like "+e);

		}
		return checkLikes;

	}

	private class CheckMyLikeMapper implements ResultSetExtractor<Boolean> {
		public Boolean extractData(ResultSet resultSet) throws SQLException{


			if(resultSet.next()){

				return true;
			}

			return false;
		}
	}


}
