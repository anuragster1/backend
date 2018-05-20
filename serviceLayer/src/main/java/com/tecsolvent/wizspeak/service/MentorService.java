package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.MentorDao;
import com.tecsolvent.wizspeak.UserDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jaison on 20/3/16.
 */
public class MentorService {


	private UserDao userDao;
	private MentorDao mentorDao;
	private UserService userService;
	private PostService postService;
	private CommentService commentService;
	private LikeService likeService;

	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;
	public static Logger logger = Logger.getLogger(MentorService.class);

	public void setUserDao(UserDao userDao){ this.userDao = userDao; }

	public void setMentorDao(MentorDao mentorDao) {
		this.mentorDao = mentorDao;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setLikeService(LikeService likeService) {
		this.likeService = likeService;
	}

	public ArrayList<User> getUserMentors(long user_id) throws Exception{


		ArrayList<User> uMentors = (ArrayList<User>) guavaAndCouchbaseCache.getObjectFromCache("userMentorst"+user_id,ArrayList.class);

		if(uMentors!=null){
			logger.info("from Cache");
			return uMentors;
		}
		logger.info("from mysql diret user_id= "+user_id);
		ArrayList<Long> mentorIds = mentorDao.getUserMentors(user_id);
		logger.info("got mentor ids ");
		ArrayList<User> mentors = new ArrayList<User>();

		Iterator itr = mentorIds.iterator();

		while (itr.hasNext()){

			User user = new User();

			long userId= (long) itr.next();
			logger.info(" users="+userId);
			try{
				user = userDao.getUserName(userId);
			}catch (Exception e){

				logger.error("error in gettin guser name "+e);
			}



			try {
				String pic = userDao.getUserPic(userId, 1, 1);
				user.setProfilePic(pic);
			}catch (Exception e){
				logger.error("error in getting profile pic"+e);
			}
			try {
				String status = userDao.getUserStatus(userId, (byte) 1);
				user.setProfileStatus(status);
			}catch (Exception e){
				logger.error("error in getting status "+e);
			}

			mentors.add(user);
		}
		guavaAndCouchbaseCache.putObjectAsByteInCache("userMentors"+user_id, mentors);

		return mentors;
	}


	public Map<String,Object> getMentorPageBasic(String userIdStr,String visitorStr) throws Exception{


		Map<String,Object> basicData = new HashMap<>();
		long userId = 0;
		long visitorId = 0;
		try {
			 userId = userService.getUserId(userIdStr);
			 visitorId = userService.getUserId(visitorStr);
		}catch (Exception e){

			logger.info("error in getting user id fro string "+e);
		}

		logger.info(" got usrids userId = "+userId+"  visitorId = "+visitorId);

		try {

			User user = userService.mentorDetails(userId);

			ArrayList<User> followers = getMentorFollowers(userId);

			ArrayList<Post> followerPosts = getFollowerPosts(userId,visitorId);
			ArrayList<User> mentors = new ArrayList<>();

			try {
				mentors = getUserMentors(visitorId);

			}catch (Exception e){

				logger.info("errro in getting mentor list "+e);
			}
			basicData.put("mentorList",mentors);

			HashMap<Integer, String> mentorCate = userService.getUserCate((byte) 1, userId, (byte) 1);

			ArrayList<User> mSuggection = new ArrayList<>();

			try {
				ArrayList<Long> mentorSuggestion = mentorDao.getMentorSuggestion(mentorCate,userId);
				Iterator suggestion  = mentorSuggestion.iterator();
				while (suggestion.hasNext()){

					long mId = (long) suggestion.next();

					final User mentor = userService.getUserName(mId);
					mentor.setProfilePic(userService.getUserPic(mId));
					mSuggection.add(mentor);
				}




			}catch (Exception e){
				logger.info("error in getting mentor suggections "+e);
			}




			basicData.put("suggection",mSuggection);
			basicData.put("sidePosts",followerPosts);
			basicData.put("userDetails",user);
			basicData.put("followers",followers);
		}catch (Exception e){

			logger.error("error in feching mentor basic data "+e);

			throw e;
		}




		return basicData;
	}



	public ArrayList<User> getMentorFollowers(long user_id) throws Exception {
logger.info("in get mentor Follwers function");

		ArrayList<User> mFollow = (ArrayList<User>) guavaAndCouchbaseCache.getObjectFromCache("getMentorFollowersd" + user_id, ArrayList.class);
		if (mFollow != null) {
			//System.out.println("from frinds cache  ");
			return mFollow;
		}

		ArrayList<Long> followers = new ArrayList<>();
		try {
			followers = mentorDao.getMentorFollowers(user_id);

		}catch (Exception e){

			logger.error(" error in getting followers id from mentor dao "+e);
		}

		ArrayList<User> myFriends = new ArrayList<User>();

		Iterator<Long> itr = followers.iterator();

		while (itr.hasNext()) {

			User user = new User();

			user = userService.userDetails(itr.next());

			myFriends.add(user);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("getMentorFollowers1" + user_id, myFriends);

		return myFriends;
	}


	public ArrayList<Post> getFollowerPosts(long userId,long visitorId) throws Exception {
		logger.info("in get mentor Follwers posts function");

		ArrayList<Post> mFollow = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("getMentorFollowerPostss" + userId, ArrayList.class);
		if (mFollow != null) {
			//System.out.println("from frinds cache  ");
			return mFollow;
		}

		ArrayList<Post> posts = new ArrayList<>();

		byte type = 3;
		try {
			posts = postService.getMetorFollowerPost(userId,visitorId, type);
		}catch (Exception e){
			logger.info("no post found"+e);
		}

		ArrayList<Post> followerPosts = new ArrayList<>();
		Iterator itr = posts.iterator();
		while (itr.hasNext()){

			Post p = (Post) itr.next();

			User postUser = userService.userDetails(p.getPostby_id());

			p.setPostby_pic(postUser.getProfilePic());
			p.setPostby_name(postUser.getFirst_name()+" "+postUser.getLast_name());
			p.setPostUserId(postUser.getUserId());

			followerPosts.add(p);
		}



		guavaAndCouchbaseCache.putObjectAsByteInCache("getMentorFollowerPosts" + userId, posts);

		return followerPosts;
	}



	public ArrayList<Post> getMentorWallPosts(String userIdStr,String visitorIdStr,long lastId,int page)throws Exception{

		logger.info("in get mentor wall posts function");

		ArrayList<Post> mFollow = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("getMentorWallPostss" + userIdStr+"p"+page+lastId, ArrayList.class);
		if (mFollow != null) {
			//System.out.println("from frinds cache  ");
			return mFollow;
		}

		ArrayList<Post> posts = new ArrayList<>();
		long userId = 0;
		long visitorId = 0;
		try {

			 userId = userService.getUserId(userIdStr);
			visitorId = userService.getUserId(visitorIdStr);
		}catch (Exception e){

			logger.error("userid not found "+e);
			throw e;
		}


		byte isAdmin = 0;
		if(userId==visitorId){
			isAdmin = 1;
		}

		try {
			posts = postService.getMetorWallPost(userId,visitorId,isAdmin,lastId,page);
		}catch (Exception e){
			logger.info("no post found"+e);
		}

		ArrayList<Post> followerPosts = new ArrayList<>();
		Iterator itr = posts.iterator();
		while (itr.hasNext()){

			Post p = (Post) itr.next();



			User postUser = userService.userDetails(p.getPostby_id());

			p.setiLikes(likeService.checkLike(p.getId(),visitorId, (byte) 1));
			p.setLikes(likeService.getLikeCount(p.getId(),visitorId,1));

			logger.info(" i like or not = "+likeService.checkLike(p.getId(),visitorId, (byte) 1));

			p.setPostby_pic(postUser.getProfilePic());
			p.setPostby_name(postUser.getFirst_name()+" "+postUser.getLast_name());
			p.setComments(commentService.getPostComments(p.getId(),visitorId));


			followerPosts.add(p);
		}



		guavaAndCouchbaseCache.putObjectAsByteInCache("getMentorFollowerPosts" + userIdStr+"p"+isAdmin, posts);

		return followerPosts;

	}



	public ArrayList<Post> getMentorMediaPost(String userIdStr,String visitorIdStr,byte postType,int page)throws Exception{

		logger.info("in get mentor wall posts function");

		ArrayList<Post> mFollow = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("getMentorMediaPostss" + userIdStr+"p"+page+postType, ArrayList.class);
		if (mFollow != null) {
			//System.out.println("from frinds cache  ");
			return mFollow;
		}

		ArrayList<Post> posts = new ArrayList<>();
		long userId = 0;
		long visitorId = 0;
		try {

			userId = userService.getUserId(userIdStr);
			visitorId = userService.getUserId(visitorIdStr);
		}catch (Exception e){

			logger.error("userid not found "+e);
			throw e;
		}


		try {
			posts = postService.getMetorMedia(userId,visitorId,postType,page);
		}catch (Exception e){
			logger.info("no post found"+e);
		}



		guavaAndCouchbaseCache.putObjectAsByteInCache("getMentorFollowerPosts" + userIdStr+"p"+page+postType, posts);

		return posts;

	}

	public Integer getMentorMediaPostCount(String userIdStr,byte postType)throws Exception{

		logger.info("in get mentor media  posts count function");

		int cnt = 0;
		long userId = 0;
		try {

			userId = userService.getUserId(userIdStr);
		}catch (Exception e){

			logger.error("userid not found "+e);
			throw e;
		}


		try {
			cnt = postService.getMentorMediaPostCount(userId,postType);
		}catch (Exception e){
			logger.info("no post found"+e);
		}


		return cnt;

	}


	public byte MentorUserRelation(String mentorIdStr,String visitorIdStr) throws Exception{
		byte relation = 3; //just visitor

		long mentorId  = 0;
		long visitorId  = 0;

		try{

			mentorId = userService.getUserId(mentorIdStr);
			visitorId = userService.getUserId(visitorIdStr);
		}catch (Exception e){

			logger.error("error in getting user ids "+e);
			throw  e;
		}

		try {

			relation = mentorDao.getMentorVisitorRelation(mentorId,visitorId);
		}catch (Exception e){

			logger.info("error in getting mentor visitor relation from mentor dao");
		}

		return relation;
	}


public ArrayList<Post> mentorTalk(String mentorIdStr,String visitorIdStr,byte page,long latId) throws Exception{

	    ArrayList<Post> posts = new ArrayList<>();
		long mentorId  = 0;
		long visitorId  = 0;

		try{

			mentorId = userService.getUserId(mentorIdStr);
			visitorId = userService.getUserId(visitorIdStr);
		}catch (Exception e){

			logger.error("error in getting user ids "+e);
			throw  e;
		}

		try {
			posts = postService.getMentorTalk(mentorId,visitorId,page,latId);
		}catch (Exception e){

			logger.info("error in getting mentor visitor relation from mentor dao");
		}

		ArrayList<Post> talkPosts = new ArrayList<>();
		Iterator itr = posts.iterator();
		while (itr.hasNext()){

			Post p = (Post) itr.next();


			User postUser = userService.userDetails(p.getPostby_id());

			p.setPostby_pic(postUser.getProfilePic());
			p.setPostby_name(postUser.getFirst_name()+" "+postUser.getLast_name());
			p.setComments(commentService.getPostComments(p.getId(),p.getPostby_id()));

			p.setiLikes(likeService.checkLike(p.getId(),visitorId,(byte) 1));
			p.setLikes(likeService.getLikeCount(p.getId(),visitorId,1));



			talkPosts.add(p);
		}



		return talkPosts;
	}

public ArrayList<Post> getAchieve(String mentorIdStr,String visitorIdStr,byte page,long latId) throws Exception{

	    ArrayList<Post> posts = new ArrayList<>();
		long mentorId  = 0;
		long visitorId  = 0;

		try{

			mentorId = userService.getUserId(mentorIdStr);
			visitorId = userService.getUserId(visitorIdStr);
		}catch (Exception e){

			logger.error("error in getting user ids "+e);
			throw  e;
		}

		try {
			posts = postService.getMentorAchieve(mentorId,visitorId,page,latId);
		}catch (Exception e){

			logger.info("error in getting mentor visitor relation from mentor dao");
		}

		ArrayList<Post> talkPosts = new ArrayList<>();
		Iterator itr = posts.iterator();
		while (itr.hasNext()){

			Post p = (Post) itr.next();


			User postUser = userService.userDetails(p.getPostby_id());

			p.setPostby_pic(postUser.getProfilePic());
			p.setPostby_name(postUser.getFirst_name()+" "+postUser.getLast_name());
			p.setComments(commentService.getPostComments(p.getId(),p.getPostby_id()));

			p.setiLikes(likeService.checkLike(p.getId(),visitorId,(byte) 1));
			p.setLikes(likeService.getLikeCount(p.getId(),visitorId,1));



			talkPosts.add(p);
		}



		return talkPosts;
	}


	public byte checkFollow(String mentorStr ,String userStr) throws Exception{

		byte status = 0;
		long mentorId = 0;
		long folloerId = 0;
		try{
			mentorId = userService.getUserId(mentorStr);
			folloerId = userService.getUserId(userStr);
logger.info(mentorId+"    folloer = "+folloerId);
		}catch (Exception e){

			logger.info("error in getting user ids "+e);
		}

		try{
			status = userService.checkFollow(mentorId,folloerId);

		}catch (Exception e){
			logger.info(" error in getting mentor user status");
		}

		return status;
	}

}
