package com.tecsolvent.wizspeak.service;


import com.tecsolvent.wizspeak.*;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.*;
import com.tecsolvent.wizspeak.utility.DateUtil;
import com.tecsolvent.wizspeak.utility.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * @author sandeep
 * @since 19/02/16
 */
public class CreativityService {

	public static Logger logger = Logger.getLogger(CreativityService.class);

	private AmbitionDao ambitionDao;
	private GroupDao groupDao;
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;

	//services

	private UserService userService;
	private FriendService friendService;
	private MentorService mentorService;
	private GroupService groupService;
	private PostService postService;
	private CommentService commentService;


	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public void setAmbitionDao(AmbitionDao ambitionDao) {
		this.ambitionDao = ambitionDao;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}

	public void setMentorService(MentorService mentorService) {
		this.mentorService = mentorService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setCommentService(CommentService commentService) { this.commentService = commentService; }

	public GroupDao getGroupDao() {
			return groupDao;
		}

		public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}



	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	private UserDao userDao;



	public PostDao getPostDao() {
		return postDao;
	}

	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
	}

	private PostDao postDao;



	public CommentDao getCommentDao() {
		return commentDao;
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	private CommentDao commentDao;

	//like
	public LikeDao getLikeDao() {
		return likeDao;
	}

	public void setLikeDao(LikeDao likeDao) {
		this.likeDao = likeDao;
	}

	private LikeDao likeDao;

	//friend
	public FriendDao getFriendDao() {
		return friendDao;
	}

	public void setFriendDao(FriendDao friendDao) {
		this.friendDao = friendDao;
	}

	private FriendDao friendDao;

	//mentorDao
	public MentorDao getMentorDao() {
		return mentorDao;
	}

	public void setMentorDao(MentorDao mentorDao) {
		this.mentorDao = mentorDao;
	}

	private MentorDao mentorDao;






	public ArrayList<Post> getCreativityPosts(String user_id, int verticalId) throws Exception {


		long userId = 0;

		try {
			userId = userService.getUserId(user_id);
		}catch (Exception e){

			logger.info("error in grtting user id "+e);
		}

		ArrayList<Post> getCreativityPosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("ambitionPostscccx" + userId, ArrayList.class);

		if (getCreativityPosts != null) {
			logger.info("post from from cache");
			return getCreativityPosts;
		}
		logger.info("fetch ambi post user id =" + userId);
		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getCreativityPosts( verticalId);
		} catch (Exception e) {
			logger.info(" error getting postDao" + e);
		}


		ArrayList<Post> newPost = new ArrayList<Post>();

		Iterator itr = posts.iterator();

		while (itr.hasNext()) {

			User user = new User();

			Post post = (Post) itr.next();


			user = getUserName(post.getPostby_id());
			String userPic = userDao.getUserPic(post.getPostby_id(), 1, 1);

			if (userPic.isEmpty()) {
				userPic = "";
			}

			HashMap<Integer, Long> likes = likeDao.getLikes(post.getId(), 1);

			post.setiLikes(false);
			if (!likes.isEmpty()) {

				if (likes.containsValue(userId)) {
					logger.info("getting i liked or not ");
					post.setiLikes(true);

				}
				post.setLikes(likes.size());
			}


			post.setPostby_name(user.getFirst_name() + " " + user.getLast_name());
			post.setPostby_pic(userPic);


			newPost.add(post);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionPosts" + userId, newPost);

		return newPost;
	}


	public ArrayList<Post> getCreativityCatPost(String user_id, int postcat_id,int post_type) throws Exception {


		long userId = 0;

		try {
			userId = userService.getUserId(user_id);
		}catch (Exception e){

			logger.info("error in grtting user id "+e);
		}

		ArrayList<Post> getCreativityPosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("ambitionPostscccx" + userId, ArrayList.class);

		if (getCreativityPosts != null) {
			logger.info("post from from cache");
			return getCreativityPosts;
		}
		logger.info("fetch ambi post user id =" + userId);
		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getCreativityCatPost( postcat_id,post_type);
		} catch (Exception e) {
			logger.info(" error getting postDao" + e);
		}


		ArrayList<Post> newPost = new ArrayList<Post>();

		Iterator itr = posts.iterator();

		while (itr.hasNext()) {

			User user = new User();

			Post post = (Post) itr.next();


			user = getUserName(post.getPostby_id());
			String userPic = userDao.getUserPic(post.getPostby_id(), 1, 1);

			if (userPic.isEmpty()) {
				userPic = "";
			}

			HashMap<Integer, Long> likes = likeDao.getLikes(post.getId(), 1);

			post.setiLikes(false);
			if (!likes.isEmpty()) {

				if (likes.containsValue(userId)) {
					logger.info("getting i liked or not ");
					post.setiLikes(true);

				}
				post.setLikes(likes.size());
			}


			post.setPostby_name(user.getFirst_name() + " " + user.getLast_name());
			post.setPostby_pic(userPic);


			newPost.add(post);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionPosts" + userId, newPost);

		return newPost;
	}



	public User getUserName(long userId) throws Exception {

		User user = (User) guavaAndCouchbaseCache.getObjectFromCache("userNamez"+userId,User.class);

		if(user == null){

			logger.info("name fetching from mysql direct");
			user = userDao.getUserName(userId);
			guavaAndCouchbaseCache.putObjectAsByteInCache("userName"+userId, user);

		}else{

			logger.info(" name fetching from cache ");
		}

		return user;

	}

	public ArrayList<Post> creativityPlayer(long file_id, int verticalId) throws Exception {






		ArrayList<Post> getCreativityPosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("ambitionPostscccx" + file_id, ArrayList.class);

		if (getCreativityPosts != null) {
			logger.info("post from from cache");
			return getCreativityPosts;
		}
		logger.info("fetch ambi post user id =" + file_id);
		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.creativityPlayer(file_id, verticalId);
		} catch (Exception e) {
			logger.info(" error getting postDao" + e);
		}


		ArrayList<Post> newPost = new ArrayList<Post>();

		Iterator itr = posts.iterator();

		while (itr.hasNext()) {

			User user = new User();

			Post post = (Post) itr.next();


			user = getUserName(post.getPostby_id());
			String userPic = userDao.getUserPic(post.getPostby_id(), 1, 1);

			if (userPic.isEmpty()) {
				userPic = "";
			}

			HashMap<Integer, Long> likes = likeDao.getLikes(post.getId(), 1);

			post.setiLikes(false);
			if (!likes.isEmpty()) {

				if (likes.containsValue(file_id)) {
					logger.info("getting i liked or not ");
					post.setiLikes(true);

				}
				post.setLikes(likes.size());
			}


			post.setPostby_name(user.getFirst_name() + " " + user.getLast_name());
			post.setPostby_pic(userPic);


			newPost.add(post);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionPosts" + file_id, newPost);

		return newPost;
	}


	public Map<String, Object> addCreativityPost(Post crepost) throws Exception {

		logger.info(" add mentor post");

		Map<String, Object> s = postDao.addCreativityPost(crepost);
		return s;

	}


	public Map<String, Object> updateCreVideo(Post update) throws Exception {

		Map<String, Object> p = new HashMap<>();

		p = postDao.updateCreVideo(update);

		return p;


	}	public Map<String, Object> updateStatusVideo(Post update) throws Exception {

		Map<String, Object> p = new HashMap<>();
		logger.info(" update stsus");
		p = postDao.updateStatusVideo(update);

		return p;


	}

}



