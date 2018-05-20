package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.FriendDao;
import com.tecsolvent.wizspeak.UserDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.FriendRequest;
import com.tecsolvent.wizspeak.model.User;
import org.apache.log4j.Logger;
import scala.util.parsing.combinator.testing.Str;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jaison on 18/3/16.
 */
public class FriendService {

	public static Logger logger = Logger.getLogger(FriendService.class);
	private FriendDao friendDao;
	private UserDao userDao;
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public void setFriendDao(FriendDao friendDao) {
		this.friendDao = friendDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public ArrayList<User> getUserFriends(long user_id) throws Exception{


		ArrayList<User> uFriend = (ArrayList<User>) guavaAndCouchbaseCache.getObjectFromCache("userFriendsx"+user_id,ArrayList.class);
		if(uFriend!=null){
			//System.out.println("from frinds cache  ");
			return uFriend;
		}
		ArrayList<Long> frinds = new ArrayList<>();
		try {
			frinds = friendDao.getUserFriends(user_id);
		}catch (Exception e){

			logger.info("error in getting friendDao"+e);
		}
		ArrayList<User> myFriends = new ArrayList<User>();

		Iterator<Long> itr = frinds.iterator();

		while (itr.hasNext()){

			User user = new User();

			long userId = itr.next();
			user = userDao.getUserName(userId);

			try {
				String pic = userDao.getUserPic(userId, 1, 1);
				user.setProfilePic(pic);
			}catch (Exception e){
				logger.error("error in getting profile pic"+e);
			}
			try {
				String status = userDao.getUserStatus(userId , (byte) 1);
				user.setProfileStatus(status);
			}catch (Exception e){
				logger.error("error in getting status "+e);
			}

			myFriends.add(user);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("userFriends"+user_id, myFriends);

		return myFriends;
	}


	public String checkAccess()throws Exception{

try {
	friendDao.getUserFriends(1);
}catch (Exception e){
logger.info("error in getting friendsDao"+e);

}

		return "acess granted";
	}



	public int checkUserUserStatus(String userOne, String userTwo) throws Exception{
		logger.info("getting user to user relation ");

		long userA = userDao.getUserId(userOne);
		long userB = userDao.getUserId(userTwo);

		return userDao.checkUserUserStatus(userA,userB);

	}

	public ArrayList<FriendRequest>  getFriendStatus (long userIda, long userIdb ) throws Exception {

		ArrayList<FriendRequest> status = friendDao.getFriendStatus(userIda,userIdb);

		logger.info("friennnnnnnnnnnnnnnnnnnndddddddd servvvvvvvvvvvvvicwwwwwwwwww"+status);






		return status;

	}




}


