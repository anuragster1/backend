package com.tecsolvent.wizspeak;

import com.tecsolvent.wizspeak.model.FriendRequest;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaison on 16/3/16.
 */
public class FriendDao {

	public static Logger logger = Logger.getLogger(FriendDao.class);

	private JdbcTemplate masterJdbcTemplate;
	private JdbcTemplate slaveJdbcTemplate;



	public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
		this.masterJdbcTemplate = masterJdbcTemplate;
	}

	public void setSlaveJdbcTemplate(JdbcTemplate slaveJdbcTemplate) {
		this.slaveJdbcTemplate = slaveJdbcTemplate;
	}



	public ArrayList<Long> getUserFriends(long user_id) throws Exception{

		ArrayList<Long> friends = new ArrayList<>();
		try{
			friends =  slaveJdbcTemplate.query("select user_id_a from user_friends where user_id_b = "+user_id+" AND request_status = 1 union select user_id_b from user_friends where user_id_a = "+user_id+" AND request_status = 1", new FriendMapper());

		}catch (Exception e){

			logger.error(" getting friend id error "+e);
		}
		return friends;
	}

	private class FriendMapper implements ResultSetExtractor<ArrayList<Long>> {

		public ArrayList<Long> extractData(ResultSet resultSet) throws SQLException {

			ArrayList<Long> friends = new ArrayList<Long>();
			while (resultSet.next()){

				logger.info("while userid ="+resultSet.getString("user_id_a"));

				long friend_id = Long.parseLong(resultSet.getString("user_id_a"));
				friends.add(friend_id);

			}

			return friends;
		}


	}

	public Map<String, Object> addFriendz(FriendRequest addfrnd) throws SQLException{

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);
		Map<String, Object> p = new HashMap<>();
		String SQL = "INSERT user_friends SET  user_id_a =?, user_id_b =?, request_status = ? ,date_requested = ?";

		logger.info("while userid ="+addfrnd.getRequest_status("user_id_a"));
		logger.info("while userid ="+addfrnd.getUser_id_b("user_id_b"));

		slaveJdbcTemplate.update(SQL, addfrnd.getRequest_status("user_id_a"), addfrnd.getUser_id_b("user_id_b"), 0, currentTime );
		p.put("success", "1");

		return p;
	}



	public Map<String, Object> cancelFriend(FriendRequest addfrnd) throws SQLException{

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);
		Map<String, Object> p = new HashMap<>();
		logger.info("UPDATE user_friends SET  request_status = ? ,date_requested = ? WHERE user_id_a =? AND user_id_b =?");
		String SQL = "DELETE FROM  user_friends  WHERE id =?  ";

		logger.info("while userid ="+addfrnd.getRequest_status("user_id_a"));
		logger.info("while userid ="+addfrnd.getUser_id_b("user_id_b"));

		slaveJdbcTemplate.update(SQL,addfrnd.getId("id")) ;
		p.put("success", "1");

		return p;
	}


	public Map<String, Object> rejectorAcceptFriend(FriendRequest addfrnd) throws SQLException{

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);
		Map<String, Object> p = new HashMap<>();
		String SQL = "UPDATE user_friends SET  request_status = ? ,date_requested = ? WHERE id =?  ";

		logger.info("while userid ="+addfrnd.getRequest_status("user_id_a"));
		logger.info("while userid ="+addfrnd.getUser_id_b("user_id_b"));

		slaveJdbcTemplate.update(SQL,addfrnd.getUser_id_a("request_status"),currentTime, addfrnd.getId("id") );
		p.put("success", "1");

		return p;
	}


	public ArrayList<Long> checkFriendRequest(long user_id) throws Exception{

		ArrayList<Long> friends = new ArrayList<>();
		try{
			friends =  slaveJdbcTemplate.query("select user_id_a from user_friends where user_id_b = "+user_id+" AND request_status = 0 union select user_id_b from user_friends where user_id_a = "+user_id+" AND request_status = 0", new FriendreqMapper());

		}catch (Exception e){

			logger.error(" getting friend id error "+e);
		}
		return friends;
	}


	private class FriendreqMapper implements ResultSetExtractor<ArrayList<Long>> {

		public ArrayList<Long> extractData(ResultSet resultSet) throws SQLException {

			ArrayList<Long> friends = new ArrayList<Long>();
			while (resultSet.next()){

				logger.info("while userid ="+resultSet.getString("user_id_a"));

				long friend_id = Long.parseLong(resultSet.getString("user_id_a"));
				friends.add(friend_id);

			}

			return friends;
		}


	}

	public ArrayList<FriendRequest> getFriendStatus(long userIda,long userIdb) throws Exception{

		ArrayList<FriendRequest> friends = new ArrayList<>();
		try{

			logger.info("SELECT id,user_id_a,user_id_b,request_status FROM user_friends WHERE user_id_a = "+userIda+" AND user_id_b = "+userIdb+"  OR user_id_a = "+userIdb+" AND user_id_b = "+userIda+"   ");

			friends = (ArrayList<FriendRequest>) slaveJdbcTemplate.query("SELECT id,user_id_a,user_id_b,request_status FROM user_friends WHERE user_id_a = "+userIda+" AND user_id_b = "+userIdb+"  OR user_id_a = "+userIdb+" AND user_id_b = "+userIda+"   ", new getFriendStatusMapper());

		}catch (Exception e){

			logger.error(" getting friend id error "+e);
		}

		logger.info(friends.toString());
		return friends;
	}


	private class getFriendStatusMapper implements ResultSetExtractor<List<FriendRequest>> {
		public List<FriendRequest> extractData(ResultSet resultSet) throws SQLException {


			List<FriendRequest> userfchk = new ArrayList<FriendRequest>();

			while (resultSet.next()) {


				FriendRequest friendStatus = new FriendRequest( resultSet.getInt("id"),resultSet.getString("user_id_a"),resultSet.getString("user_id_b"), resultSet.getString("request_status") );
				userfchk.add(friendStatus);


			}


			return userfchk;

		}
	}

}
