package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.LikeDao;
import com.tecsolvent.wizspeak.UserDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import org.apache.log4j.Logger;
import scala.collection.generic.BitOperations;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaison on 27/4/16.
 */
public class LikeService {

	private UserDao userDao;
	private LikeDao likeDao;
	public static Logger logger = Logger.getLogger(LikeService.class);
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;


	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setLikeDao(LikeDao likeDao) {
		this.likeDao = likeDao;
	}

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}


	public boolean checkLike(long postId,long userId,byte itemType)throws Exception
	{


		boolean status = false;
		try{

			status = likeDao.checkMyLike(postId,userId,itemType);

		}catch (Exception e){
			logger.error(" error in check if like or not "+e);
		}
		return status;
	}

	public int getLikeCount(long postId,long userId,int type) throws Exception {

		HashMap<Integer, Long> likes = likeDao.getLikes(postId,type);
		return likes.size();
	}
}
