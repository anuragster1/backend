package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.CommentDao;
import com.tecsolvent.wizspeak.LikeDao;
import com.tecsolvent.wizspeak.UserDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.Comment;
import com.tecsolvent.wizspeak.model.User;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by jaison on 29/3/16.
 */
public class CommentService {

	private CommentDao commentDao;
	private UserDao userDao;
	private LikeDao likeDao;
	public static Logger logger = Logger.getLogger(CommentService.class);
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;



	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public void setLikeDao(LikeDao likeDao) {
		this.likeDao = likeDao;
	}

	public ArrayList<Comment> getPostComments(long post_id, long user_id) throws Exception{

		ArrayList<Comment> comments = (ArrayList<Comment>) guavaAndCouchbaseCache.getObjectFromCache("postCommventsx"+post_id,ArrayList.class);

		if(comments!=null){

			return comments;
		}
logger.info("inside getPostcomment vvccccccccccccccccccccccccccccccccccccccccccc");

		ArrayList<Comment> commentz = new ArrayList<Comment>();
		try {
			int limit = 4;
			commentz = commentDao.getPostComments(post_id,limit);

		}catch (Exception e){
			logger.info("error in fetching comment Dao file"+e);
		}

		ArrayList<Comment> newComments = new ArrayList<Comment>();

		Iterator itr = commentz.iterator();

		while (itr.hasNext()){

			User user = new User();

			Comment comment = (Comment) itr.next();
			user = userDao.getUserName(comment.getCommenter_id());
			String pic = userDao.getUserPic(comment.getCommenter_id(),1,1);

			HashMap<Integer, Long> likes = likeDao.getLikes(comment.getId(),2);

			comment.setLikes(likes.size());
			comment.setiLikes(false);
			if(likes.containsValue(user_id)){
				//i liked comment
				comment.setiLikes(true);

			}

			comment.setcUserPic(pic);
			comment.setComenterName(user.getFirst_name()+" "+user.getLast_name());

			newComments.add(comment);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("postComments"+post_id, newComments);

		Collections.reverse(newComments);
		return newComments;
	}



}
