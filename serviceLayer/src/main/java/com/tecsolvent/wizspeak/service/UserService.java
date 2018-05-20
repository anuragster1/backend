package com.tecsolvent.wizspeak.service;


import com.tecsolvent.wizspeak.UserDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;

import com.tecsolvent.wizspeak.model.User;
import org.apache.log4j.Logger;

import java.util.HashMap;


/**
 * Created by jaison on 16/3/16.
 */
public class UserService {

	public static Logger logger = Logger.getLogger(UserService.class);
	private UserDao userDao;
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;

	public void setUserDao(UserDao userDao){ this.userDao = userDao; }


	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}


	public User getUserName(long user_id) throws Exception {

		User user = (User) guavaAndCouchbaseCache.getObjectFromCache("userNamesa"+user_id,User.class);

		if(user == null){

			logger.info(" name fetching from mysql direct");
			user = userDao.getUserName(user_id);
			guavaAndCouchbaseCache.putObjectAsByteInCache("userName"+user_id, user);
		}else{

			logger.info("name fetching from cache");
		}

		return user;

	}


	public String getUserStatus(long user_id,byte mentor) throws Exception {

		String status = (String) guavaAndCouchbaseCache.getObjectFromCache("userStatusd"+user_id,String.class);
		if(status == null){
			logger.info(" status fetching from mysql direct");
			status = userDao.getUserStatus(user_id, mentor);
			if(status == null){ status=" ";}
			guavaAndCouchbaseCache.putObjectAsByteInCache("userStatus"+user_id, status);
		}else{

			logger.info("status fetching from cache");
		}

		return status;
	}


	public String getUserPic(long user_id) throws Exception {

		String profilePic = (String) guavaAndCouchbaseCache.getObjectFromCache("profilePic"+user_id,String.class);
		if(profilePic == null){
			logger.info(" status fetching from mysql direct");
			profilePic = userDao.getUserPic(user_id,1,1);
			if(profilePic == null){ profilePic=" ";}
			guavaAndCouchbaseCache.putObjectAsByteInCache("userProfilePic"+user_id, profilePic);
		}else{

			//System.out.println("status fetching from cache");
		}

		return profilePic;
	}

	public String getCoverPic(long user_id) throws Exception {

		String getCoverPic = (String) guavaAndCouchbaseCache.getObjectFromCache("gethfdCoverPic" + user_id, String.class);
		if (getCoverPic == null) {
			logger.info(" coverrrrrrrrrrrrrrrrrrrrrrrrrrrrrr piccccccccccccccccccccccccccc fetching from mysql direct");
			logger.info(userDao.getCoverPic(user_id, 1, 0) + "gopuuuuuuuuuuuuuuuu");
			getCoverPic = userDao.getCoverPic(user_id, 1, 0);
			if (getCoverPic == null) {
				getCoverPic = " ";
			}
			guavaAndCouchbaseCache.putObjectAsByteInCache("getCoverPic4jghf" + user_id, getCoverPic);
		} else {

			//System.out.println("status fetching from cache");
		}

		return getCoverPic;
	}



	public User userDetails(long user_id) throws Exception {

		User cacheUser = (User) guavaAndCouchbaseCache.getObjectFromCache("userDetailsz"+user_id,User.class);
		if(cacheUser != null){
			logger.info("user details from cache");
			return cacheUser;
		}
		User user = new User();
		try {
			user = getUserName(user_id);
		}catch (Exception e){

			throw e;
		}
		try {
			String status = getUserStatus(user_id, (byte) 0);
			user.setProfileStatus(status);

		}catch (Exception e){
			throw e;
		}
		try {
			String profilePic  = getUserPic(user_id);
			user.setProfilePic(profilePic);
		}catch (Exception e){
			logger.error("error in getting user profile pic" +e);
		}

		try {
			String coverPic = getCoverPic(user_id);
			user.setGetCoverPic(coverPic);
		} catch (Exception e) {
			logger.error("error in getting user profile pic" + e);
		}





		guavaAndCouchbaseCache.putObjectAsByteInCache("userDetail"+user_id, user);
		return user;
	}


	public User mentorDetails(long user_id) throws Exception {

		User cacheUser = (User) guavaAndCouchbaseCache.getObjectFromCache("userDetailsz"+user_id,User.class);
		if(cacheUser != null){
			logger.info("user details from cache");
			return cacheUser;
		}

		User user = getUserName(user_id);
		String status = getUserStatus(user_id, (byte) 1);
		String profilePic  = getUserPic(user_id);
		String coverPic = getCoverPic(user_id);

		user.setProfileStatus(status);
		user.setProfilePic(profilePic);
		user.setGetCoverPic(coverPic);
		guavaAndCouchbaseCache.putObjectAsByteInCache("userDetail"+user_id, user);
		return user;
	}

	public Long  getUserId(String userString) throws Exception {


		long userId = 0;
		try {
			userId = userDao.getUserId(userString);

		}catch(Exception e){
			logger.info("error tin getting user id");
		}
		return userId;

	}



	public boolean addUserProfilePic(String link,long userId) throws Exception{
		logger.info("    add profile pic user service");
		try {
			logger.info("removing old picss   ");
			userDao.removeProfilePic(userId, 1, 1, 1);
		}catch (Exception e){

			logger.error("error in removinf profile pic "+e);
		}

		boolean status = userDao.addUserProfilePic(link,userId);
		return status;
	}


	public Boolean updateUserDet(User user) throws Exception{

		Boolean status = false;
		try{

			status = userDao.updateUserDet(user);
		}catch (Exception e){

			logger.info("eerorr in update user det "+e);
		}
		return status;
	}


	public long[] getUserUserStatus(long userId,long visitorId)throws Exception{

		logger.info(" get user user status");
		//id ,usera,userb ,status
		long[] status = {0,0,0,3};
		try{
			status = 	userDao.getUSerUserStatus(userId,visitorId);

		}catch (Exception e){
			logger.error("error in gettin user user staatus "+e);

		}
		return status;

	}


	public byte checkFollow(long mentorId,long folloerId) throws Exception{

		byte status = 0;

		try{


		}catch (Exception e){

			logger.info("error in getting user ids "+e);
		}

		try{
			status = userDao.checkFollow(mentorId,folloerId);

		}catch (Exception e){
			logger.info(" error in getting mentor user status");
		}

		return status;
	}


	public boolean addUserRating(long profileId, long userId,byte rate)throws Exception{
		boolean status = false;
		logger.info("user ado add rating  ");
		try {
			status =  userDao.addUserRating(profileId,userId,rate);
		}catch (Exception e){

			throw  e;
		}
		return status;
	}

	public byte getUserUserRating(long profileId,long userId) throws Exception{

		byte rate = 1;
		logger.info(" checking user to user rating ");

		try{

			rate = userDao.getUserUserRating(profileId,userId);

		}catch (Exception e){

			logger.error("errer in getting user rating "+e);

			throw e;
		}
		return rate;
	}


	public byte getUserRating(long profileId) throws Exception{

		byte rate = 1;
		logger.info(" checking user to user rating ");

		try{

			rate = userDao.getUserRating(profileId);

		}catch (Exception e){

			logger.error("errer in getting user rating "+e);

			throw e;
		}
		return rate;
	}

	public HashMap<Integer,String> getUserCate(byte vertical,long userId,byte isMentor)throws Exception{

		HashMap<Integer,String> map = new HashMap<>();

		try{
			map = userDao.getUserCate(vertical,userId,isMentor);

		}catch (Exception e){
			logger.info(" error in getting user cate map "+e);

		}
		return map;
	}


	public boolean removeAllUserCatMap(byte vertical,long userId,byte userType)throws Exception{

	boolean stat = false;

		try {
			userDao.removeAllUserCatMap(vertical,userId,userType);
		}catch (Exception e){

			logger.error("error in removing all user cate map "+e);
		}
		return stat;
	}


	public boolean addUserCatRel(int category,byte vertical,long userId,byte userType) throws  Exception{

		HashMap<String,String> status = new HashMap<>();
		boolean success = false;
		try{
			status = userDao.userHaveThisCat(category,vertical,userId,userType);

			int yes = Integer.parseInt(status.get("success"));
			logger.info("already there stataus = "+yes);
			if(yes==0){
				//add new

				logger.info("adding cate to user");

				userDao.addUserCat(category,vertical,userId,userType);
				success = true;
			}else{
				success = false;
				//
				logger.info(" mysql  status "+status.get("status"));
				if(status.get("status").equals("0")){
					//update
					logger.info("updating status to 1");
					success =	userDao.updateUserCat(category,vertical,userId,userType, (byte) 1);
				}
			}

		}catch (Exception e){

			logger.info(" errror in add/update user categories "+e);
		}

		return success;
	}


	public boolean checkLive(long uId)throws Exception{

		return userDao.checkLive(uId);

	}
}
