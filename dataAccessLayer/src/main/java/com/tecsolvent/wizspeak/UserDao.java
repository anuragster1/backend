package com.tecsolvent.wizspeak;

import com.google.common.collect.Maps;
import com.tecsolvent.wizspeak.model.Experience;
import com.tecsolvent.wizspeak.model.User;
import com.tecsolvent.wizspeak.model.UserEducation;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import scala.util.parsing.combinator.testing.Str;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jaison on 16/3/16.
 */
public class UserDao {


	public static Logger logger = Logger.getLogger(UserDao.class);

	private JdbcTemplate masterJdbcTemplate;
	private JdbcTemplate slaveJdbcTemplate;


	public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
		this.masterJdbcTemplate = masterJdbcTemplate;
	}

	public void setSlaveJdbcTemplate(JdbcTemplate slaveJdbcTemplate) {
		this.slaveJdbcTemplate = slaveJdbcTemplate;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");


	public User getUserName(long user_id) throws Exception {

		User user = new User();
		try {
			logger.info("userDao getting name");

			user = slaveJdbcTemplate.query("select id,userId,first_name,last_name,dob,email,gender,status,country,state,city from users where id = "+ user_id , new IdMapper());
		} catch( Exception e) {
			logger.error("Error in getting user name "+e);

		}

		return user;
	}



	public User getUserName(String userId) throws Exception {

		User user = new User();
		try {
			logger.info("userDao getting name");

			user = slaveJdbcTemplate.query("select id,userId,first_name,last_name,dob,email,gender,status,country,state,city from users where userId =  '"+userId+"'" , new IdMapper());
		} catch( Exception e) {
			logger.error("Error in getting user name "+e);

		}

		return user;
	}

	public String getUserStatus(long user_id, byte mentor) throws Exception {
		String status = "";
		try {
			logger.info("select status from user_profile_status where user_id = " + user_id + " AND is_mentor =" + mentor);
			status = slaveJdbcTemplate.query("select status from user_profile_status where user_id = " + user_id + " AND is_mentor =" + mentor, new StatusMapper());
		} catch (Exception e) {
			logger.error("Error in getting user status" + e);

		}

		return status;
	}

	public String getUserPic(long wallId, int wallType, int picType) throws Exception {

		String pic = "";
		try {
			logger.info(" fectching profile pic      select link from user_group_profile_pics where wall_id = " + wallId + " AND wall_type = " + wallType + " AND is_avatar = " + picType + " AND is_active = 1 ");
			pic = slaveJdbcTemplate.query("select link from user_group_profile_pics where wall_id = " + wallId + " AND wall_type = " + wallType + " AND is_avatar = " + picType + " AND is_active = 1 ", new UserPicMapper());
		} catch (Exception e) {
			logger.error("Error in getting profile pic " + e);
		}
		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx  xx xxxxxxxxxxx  profile pic name = " + pic);
		return pic;
	}

	public String getCoverPic(long wallId, int wallType, int picType) throws Exception {

		String pic = "";
		try {
			logger.info(" fectching profile pic      select link from user_group_profile_pics where wall_id = " + wallId + " AND wall_type = " + wallType + " AND is_avatar = " + picType + " AND is_active = 1 ");
			logger.info("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy cover pic name = " + pic);
			pic = slaveJdbcTemplate.query("select link from user_group_profile_pics where wall_id = " + wallId + " AND wall_type = " + wallType + " AND is_avatar = " + picType + " AND is_active = 1 ", new UsercPicMapper());
		} catch( Exception e) {
			logger.error("Error in getting profile pic " + e);
		}
		logger.info("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy cover pic name = " + pic);
		return pic;
	}

	public boolean addCustomUrl(Map<String, Object> newUrlName) throws Exception {

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.masterJdbcTemplate).withTableName("url_custom_name").usingColumns(new String[]{"wall_id", "wall_type", "name", "status", "date_created"});
		boolean status = false;
		try {
			simpleJdbcInsert.execute(newUrlName);
			status = true;
		} catch (Exception e) {
			logger.error("adding new custom url name");
		}

		return status;
	}

	public List<UserEducation> getUserProfileEducation(long user_id) throws Exception {
		try {

			return slaveJdbcTemplate.query("SELECT UserEducation.id, UserEducation.user_id, UserEducation.education, UserEducation.institute, UserEducation.university, UserEducation.date_from, UserEducation.date_to FROM user_educations AS UserEducation WHERE UserEducation.user_id = " + user_id, new UserEducationDataMapper());
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Experience> getUserExperience(long user_id) throws Exception {
		try {

			return slaveJdbcTemplate.query("SELECT  UserWork.id,UserWork.user_id, UserWork.company, UserWork.jobtitle, UserWork.date_from, UserWork.date_to FROM user_works AS UserWork WHERE UserWork.user_id = " + user_id, new UserExperienceDataMapper());
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean removeCustomUrl(long urlId) throws Exception {

		String SQL = "UPDATE url_custom_name SET status = ?  WHERE id = ?";
		try {
			slaveJdbcTemplate.update(SQL, 0, urlId);
		} catch (Exception e) {

			logger.error("removed custom url" + e);
		}

		return false;
	}

	public boolean updateCustomUrl(long urlId, String name) throws Exception {

		String SQL = "UPDATE url_custom_name SET name = ? , status = ?  WHERE id = ?";
		try {
			slaveJdbcTemplate.update(SQL, name, 1, urlId);
		} catch (Exception e) {

			logger.error("removed custom url" + e);
		}

		return false;
	}

	public Long getWallId(String customUrl) throws Exception {

		long wall_id;

		try {
			wall_id = slaveJdbcTemplate.query("select id from groups where groupId ='" + customUrl + "'", new WallIdMapper());
		} catch (Exception e) {
			logger.error("Error in getting wall_id" + e);
			throw e;
		}
		return wall_id;
	}

	public Map<String, Object> userProfileEducationMapper(UserEducation usereducation) throws SQLException {

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);

		Map<String, Object> p = new HashMap<>();
		String SQL = "UPDATE user_educations SET education = ? ,institute = ? ,university = ? ,date_from = ? , date_to =? WHERE id = ?";

		slaveJdbcTemplate.update(SQL, usereducation.getEducation(), usereducation.getInstitute(), usereducation.getUniversity(), usereducation.getDate_from(), usereducation.getDate_to(), usereducation.getId());
		p.put("success", "1");
		logger.error("updated  education  profile");


		return p;
	}

	public String updateUserEducation(UserEducation updateObject) throws Exception {


		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(masterJdbcTemplate).withTableName("user_educations")
				.usingColumns("user_id", "education", "institute", "university", "date_from", "date_to");
		Map<String, Object> updateUserEducation = Maps.newHashMap();
		updateUserEducation.put("user_id", updateObject.getId());
		updateUserEducation.put("education", updateObject.getEducation());
		updateUserEducation.put("institute", updateObject.getInstitute());
		updateUserEducation.put("university", updateObject.getUniversity());
		updateUserEducation.put("date_from", updateObject.getDate_from());
		updateUserEducation.put("date_to", updateObject.getDate_to());



		try {

			logger.debug("data - " + updateUserEducation);

			simpleJdbcInsert.execute(updateUserEducation);
		} catch (Exception e) {

			throw e;


		} finally {
			return "success";
		}

	}

	public long getUserId(String userString) throws Exception {

		long id = 0;

		try {
			id = slaveJdbcTemplate.query("SELECT id FROM users WHERE userId = '" + userString + "'", new UserIdMapper());
		} catch (Exception e) {
			logger.error("Error in getting user status" + e);
			throw e;
		}
		return id;
	}

	public int checkUserUserStatus(long userOne, long userTwo) throws Exception {

		int id = 0;
		try {
			id = slaveJdbcTemplate.query("SELECT count(id) as cnt FROM user_friends WHERE ( (user_id_a = " + userOne + " AND user_id_b = " + userTwo + ")  OR (user_id_a = " + userTwo + " AND user_id_b = " + userOne + ") )  AND request_status = 1 ", new UserUserMapper());
		} catch (Exception e) {
			logger.error("Error in getting user status" + e);
			throw e;
		}
		return id;
	}

	public boolean addUserProfilePic(String link, long userId) throws Exception {

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.masterJdbcTemplate).withTableName("user_group_profile_pics").usingColumns(new String[]{"wall_id", "wall_type", "link", "is_avatar", "lft", "top", "date_shared", "is_active"});
		boolean status = false;

		Map<String, Object> pic = Maps.newHashMap();
		pic.put("wall_id", userId);
		pic.put("wall_type", 1);
		pic.put("link", link);
		pic.put("is_avatar", 1);
		pic.put("lft", 0);
		pic.put("top", 0);
		pic.put("date_shared", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		pic.put("is_active", 1);



		try {
			simpleJdbcInsert.execute(pic);

			status = true;
		} catch (Exception e) {
			logger.error("adding new custom url name");
		}

		return status;

	}

	public boolean removeProfilePic(long userId, int wallType, int isAvatar, int isActive) {

		logger.info("remove old profile pic ");
		Map<String, Object> p = new HashMap<>();

		String SQL = "UPDATE user_group_profile_pics SET is_active = ? WHERE wall_id = ? AND wall_type = ? AND is_avatar = ?";

		boolean status = false;

		try {
			slaveJdbcTemplate.update(SQL, isActive, userId, wallType, isAvatar);
			status = true;
		} catch (Exception e) {

			logger.info("error in update user profile pic  " + e);
		}

		return status;
	}

	public Boolean updateUserDet(User user) {


		String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, dob = ? WHERE userId = ?";

		boolean status = false;

		try {
			slaveJdbcTemplate.update(sql, user.getFirst_name(), user.getLast_name(), user.getEmail(), user.getDob(), user.getUserId());
			status = true;
		} catch (Exception e) {

			logger.info("error in update user det   " + e);
		}

		return status;
	}

	public long[] getUSerUserStatus(long userId, long visitorId) throws Exception {

		long[] status = {0, 0, 0, 3};

		String sql = "SELECT id,user_id_a,user_id_b, request_status FROM user_friends WHERE (((user_id_a =" + userId + ") AND (user_id_b = " + visitorId + ")) OR ((user_id_b = " + userId + ") AND (user_id_a = " + visitorId + "))) LIMIT 1";

		try {
			status = slaveJdbcTemplate.query(sql, new UserUserStatusMapper());
		} catch (Exception e) {
			logger.error("Error in getting user status" + e);
			throw e;
		}

		return status;
	}

	public byte checkFollow(long mentorId, long followerId) throws Exception {

		byte status = 0;
		logger.info("SELECT id FROM user_mentor_followers WHERE user_id = " + followerId + " AND mentor_id = " + mentorId);
		try {
			status = slaveJdbcTemplate.query("SELECT id FROM user_mentor_followers WHERE user_id = " + followerId + " AND mentor_id = " + mentorId, new UserFollowMapper());
		} catch (Exception e) {
			logger.error("Error in getting user status" + e);
			throw e;
		}
		return status;
	}

	public boolean addUserRating(long profileId, long userId, byte rate) throws Exception {

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);

		String sql = "UPDATE user_mentor_ratings SET rating = ? , date_rated = ? WHERE user_id = ? AND mentor_id = ?";

		boolean status = false;

		try {
			int a = slaveJdbcTemplate.update(sql, rate, currentTime, userId, profileId);
			logger.info(" updated status " + a);

			if (a == 0) {
				//insert

				String SQL = "INSERT user_mentor_ratings SET  user_id =?, mentor_id =?, rating = ? ,date_rated = ?";

				slaveJdbcTemplate.update(SQL, userId, profileId, rate, currentTime);

			}
			status = true;
		} catch (Exception e) {

			logger.info("error in update user rating   " + e);
		}

		return status;
	}

	public byte getUserUserRating(long profileId, long userId) throws Exception {

		byte rate = 1;
		try {
			rate = slaveJdbcTemplate.query("SELECT rating FROM user_mentor_ratings WHERE user_id = " + userId + " AND mentor_id = " + profileId, new UserRating());
		} catch (Exception e) {
			logger.error("Error in getting user user rating" + e);
			throw e;
		}
		return rate;

	}

	public byte getUserRating(long profileId) throws Exception {

		byte rate = 1;
		try {
			logger.info("SELECT rating FROM user_mentor_ratings WHERE  mentor_id = " + profileId);
			rate = slaveJdbcTemplate.query("SELECT rating FROM user_mentor_ratings WHERE  mentor_id = " + profileId, new UserRating());
		} catch (Exception e) {
			logger.error("Error in getting user user rating" + e);
			throw e;
		}
		return rate;

	}

	public HashMap<Integer, String> getUserCate(byte vertical, long userId, byte isMentor) throws Exception {

		HashMap<Integer, String> rate = new HashMap<>();
		try {
			logger.info("SELECT ucr.sub_category_id ,sc.name from user_category_relations as ucr JOIN sub_categories sc on ucr.sub_category_id = sc.id where ucr.user_id =" + userId + " AND ucr.status =1 AND  ucr.vertical_id =" + vertical);
			rate = slaveJdbcTemplate.query("SELECT ucr.sub_category_id ,sc.name from user_category_relations as ucr JOIN sub_categories sc on ucr.sub_category_id = sc.id where ucr.user_id =" + userId + " AND ucr.status =1 AND ucr.is_mentor = " + isMentor + " AND ucr.vertical_id =" + vertical, new UserCategories());
		} catch( Exception e) {
			logger.error("Error in getting user user rating" + e);
			throw e;
		}
		return rate;

	}

	public HashMap<String, String> userHaveThisCat(int category, byte vertical, long userId, byte userType) throws Exception {

		HashMap<String, String> status = new HashMap<>();
		status.put("scuccess", "0");

		String sql = "SELECT id,status FROM user_category_relations WHERE user_id = " + userId + " AND vertical_id =" + vertical + " AND sub_category_id =" + category + " AND is_mentor = " + userType;
		logger.info(sql);
		try {
			status = slaveJdbcTemplate.query(sql, new UserCatCheck());
		} catch (Exception e) {
			logger.error("Error in getting user category mapping " + e);
			throw e;
		}
		return status;
	}

	public boolean addUserCat(int category, byte vertical, long userId, byte userType) throws Exception {

		boolean status = false;


		Map<String, Object> cate = new HashMap<>();

		cate.put("user_id", userId);
		cate.put("sub_category_id", category);
		cate.put("vertical_id", vertical);
		cate.put("is_mentor", userType);
		cate.put("status", 1);

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.masterJdbcTemplate).withTableName("user_category_relations").usingColumns(new String[]{"user_id", "sub_category_id", "vertical_id", "is_mentor", "status"});

		try {
			simpleJdbcInsert.execute(cate);
			status = true;
		}catch (Exception e){
			logger.error("adding user cate map " + e);
		}


		return status;

	}

	public boolean removeAllUserCatMap(byte vertical, long userId, byte userType) throws Exception {

		boolean yes = false;

		//			//insert
		try {
			logger.info("UPDATE user_category_relations SET  status =0 WHERE user_id =" + userId + " AND vertical_id =" + vertical + "  AND is_mentor= " + userType);
			String SQL = "UPDATE user_category_relations SET  status =? WHERE user_id =? AND vertical_id =?  AND is_mentor=? ";

			int s = slaveJdbcTemplate.update(SQL, 0, userId, vertical, userType);
			logger.info(" update s = " + s);
			yes = true;
		} catch (Exception e) {

			logger.info("error in inserting user cate " + e);
		}

		return yes;
	}

	public boolean updateUserCat(int category, byte vertical, long userId, byte userType, byte status) throws Exception {

		boolean yes = false;

		//			//insert
		try {

			String SQL = "UPDATE user_category_relations SET  status =? WHERE user_id =? AND vertical_id =? AND sub_category_id =? AND is_mentor=? ";

			slaveJdbcTemplate.update(SQL, status, userId, vertical, category, userType);
			yes = true;
		} catch (Exception e) {

			logger.info("error in inserting user cate " + e);
		}

		return yes;
	}

	public ArrayList<String> countryId(long countryId) throws Exception {

		ArrayList<String> status = new ArrayList<>();
		try {

			status = slaveJdbcTemplate.query("select name from countries where id = " + countryId + "", new CountryMapper());

		} catch (Exception e) {

			logger.error(" getting friend id error " + e);
		}
		return status;
	}

	private static class CountryMapper implements ResultSetExtractor<ArrayList<String>> {

		public ArrayList<String> extractData(ResultSet resultSet) throws SQLException {

			ArrayList<String> mentor = new ArrayList<>();
			while (resultSet.next()) {

				logger.info("while userid =" + resultSet.getString("name"));

				String friend_id = resultSet.getString("name");
				mentor.add(friend_id);

			}

			return mentor;
		}


	}

	private class IdMapper implements ResultSetExtractor<User> {
		public User extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();
			User user = new User(Long.parseLong(resultSet.getString("id")), resultSet.getString("dob"), resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getString("dob"), resultSet.getString("email"));

			user.setUserId(resultSet.getString("userId"));
			user.setGender(resultSet.getString("gender"));
			user.setStatus(resultSet.getString("status"));
			user.setCountry(resultSet.getInt("country"));
			user.setState(resultSet.getInt("state"));
			user.setCity(resultSet.getInt("city"));

			return user;
		}
	}

	private class StatusMapper implements ResultSetExtractor<String> {
		public String extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();
			return resultSet.getString("status");
		}
	}

	private class UserPicMapper implements ResultSetExtractor<String> {
		public String extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();
			return resultSet.getString("link");
		}
	}

	private class UsercPicMapper implements ResultSetExtractor<String> {
		public String extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();
			return resultSet.getString("link");
		}
	}

	public class WallIdMapper implements ResultSetExtractor<Long> {
		public Long extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();
			return resultSet.getLong("id");
		}
	}

	private class UserEducationDataMapper implements ResultSetExtractor<List<UserEducation>> {
		public List<UserEducation> extractData(ResultSet resultSet) throws SQLException {


			List<UserEducation> usereducation = new ArrayList<UserEducation>();

			while (resultSet.next()) {


				UserEducation userObject = new UserEducation(resultSet.getLong("id"), resultSet.getInt("user_id"), resultSet.getString("education"), resultSet.getString("institute"), resultSet.getString("university"), resultSet.getString("date_from"), resultSet.getString("date_to"));
				usereducation.add(userObject);


			}


			return usereducation;

		}
	}

	private class UserExperienceDataMapper implements ResultSetExtractor<List<Experience>> {
		public List<Experience> extractData(ResultSet resultSet) throws SQLException {


			List<Experience> userexp = new ArrayList<Experience>();

			while (resultSet.next()) {


				Experience userObject = new Experience(resultSet.getLong("id"), resultSet.getInt("user_id"), resultSet.getString("company"), resultSet.getString("jobtitle"), resultSet.getString("date_from"), resultSet.getString("date_to"));
				userexp.add(userObject);


			}


			return userexp;

		}
	}

	private class UserIdMapper implements ResultSetExtractor<Long> {
		public Long extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();

			long id = resultSet.getLong("id");

			return id;

		}
	}

	private class UserUserMapper implements ResultSetExtractor<Integer> {
		public Integer extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();

			int cnt = resultSet.getInt("cnt");

			return cnt;

		}
	}

	private class UserUserStatusMapper implements ResultSetExtractor<long[]> {
		public long[] extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();
			long[] status = {resultSet.getInt("id"), resultSet.getInt("user_id_a"), resultSet.getInt("user_id_b"), resultSet.getInt("request_status")};

			return status;

		}
	}

	private class UserFollowMapper implements ResultSetExtractor<Byte> {
		public Byte extractData(ResultSet resultSet) throws SQLException {


			if (resultSet.next()) {
				return (byte) 1;
			}
			return (byte) 0;


		}
	}

	private class UserRating implements ResultSetExtractor<Byte> {
		public Byte extractData(ResultSet resultSet) throws SQLException {

			long rate = 0;
			long count = 0;
			byte avg = 1;
			while (resultSet.next()) {
				rate = rate + resultSet.getByte("rating");
				count++;
			}

			logger.info("tototal = " + rate + " count = " + count);
			try {
				float av = (rate / count);
				avg = (byte) Math.round(av);
			} catch (Exception e) {
				logger.info(" error in finding avg " + e);
			}

			return avg;


		}
	}

	private class UserCategories implements ResultSetExtractor<HashMap<Integer, String>> {

		public HashMap<Integer, String> extractData(ResultSet resultSet) throws SQLException {

			HashMap<Integer, String> cate = new HashMap<>();

			while (resultSet.next()) {

				cate.put(resultSet.getInt("sub_category_id"), resultSet.getString("name"));
			}

			return cate;
		}
	}

	private class UserCatCheck implements ResultSetExtractor<HashMap<String, String>> {

		public HashMap<String, String> extractData(ResultSet resultSet) throws SQLException {

			long id = 0;
			HashMap<String, String> status = new HashMap<>();
			status.put("success", "0");
			status.put("status", "0");

			while (resultSet.next()) {

				status.put("success", "1");

				status.put("status", resultSet.getString("status"));
				logger.info(" user cate map id " + resultSet.getInt("id"));
			}
			logger.info("status =" + status.get("success"));
			return status;
		}
	}



	public boolean checkLive(long userId) throws Exception{
		try {
			logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			Boolean status = slaveJdbcTemplate.query("select time from user_live where id = " + userId + "", new LiveMapper());
			logger.info(status);
			return status;
		}catch (Exception e){

			logger.info("error in user dao "+e.getMessage());
		}
		return false;


	}


	private static class LiveMapper implements ResultSetExtractor<Boolean> {

		public Boolean extractData(ResultSet resultSet) throws SQLException {


				resultSet.next();

				Date date = new Date();
				long timeNow = (date.getTime()/1000);
			long time = 0;
			try {
				time = resultSet.getLong("time");

			}catch (Exception e){
				return false;
			}

			long differ = timeNow-time;

			if(differ>55){ //55 sec live check
				return  false;
			}else {
				return true;
			}





		}


	}

}
