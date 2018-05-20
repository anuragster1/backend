package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.*;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.Group;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jaison on 23/3/16.
 */
public class PostService {


	private PostDao postDao;
	private GroupDao groupDao;
	private UserDao userDao;
	private FriendDao friendDao;
	private CommentDao commentDao;
	private MentorDao mentorDao;



	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;
	public static Logger logger = Logger.getLogger(PostService.class);

	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
	}

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public void setUserDao(UserDao userDao) { this.userDao = userDao; }

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public void setMentorDao(MentorDao mentorDao) {
		this.mentorDao = mentorDao;
	}

	public void setFriendDao(FriendDao friendDao) {
		this.friendDao = friendDao;
	}

	public ArrayList<Post> getGroupWallPosts(long wallId, int wallType, int vertical, int postType, int page,long lastId){

		ArrayList<Post> groupPosts = new ArrayList<>();

		//connected groupids and same group
		ArrayList<Long> connectedGroup = new ArrayList<>();
		try {

			connectedGroup = groupDao.getConnectedGroups(wallId,1);
			connectedGroup.add(wallId);

		} catch (Exception e) {

			logger.info("error in getting connected groups "+e);
		}



		ArrayList<Post> newPosts = new ArrayList<>();
		ArrayList<Long> userIds = new ArrayList<>();
		ArrayList<Long> mentorIds = new ArrayList<>();
		try {
			groupPosts = postDao.getPosts(userIds,connectedGroup,mentorIds,0,postType,page,lastId);

			Iterator posts = groupPosts.iterator();
			while (posts.hasNext()){

				Post post = (Post) posts.next();
				User user = userDao.getUserName(post.getPostby_id());

				post.setPostby_name(user.getFirst_name()+" "+user.getLast_name());
				post.setPostby_pic(userDao.getUserPic(user.getId(),1,1));
				newPosts.add(post);
			}



		} catch (Exception e) {
			logger.info("error in user posts "+e);
		}


		return newPosts;

	}


	public ArrayList<Post> getHomePosts(int verticalId,long userId,int page,long lastId) throws Exception {
		logger.info("  inide page  "+page);


		ArrayList<Post> cachePosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("homePagea"+verticalId+"u"+userId+"p"+page,ArrayList.class);

		if(cachePosts != null){
			logger.info(" fetching from cache posts  ");
			return cachePosts;
		}
		//ambition home page - post from user,userfriend,userGroups
		ArrayList<Long> friendList =  friendDao.getUserFriends(userId);

		//add userid also
		friendList.add(userId);

		//user groups
		ArrayList<Long> groups =  groupDao.getUserGroupId(userId);


		//user mentors
		ArrayList<Long> mentor = mentorDao.getUserMentors(userId);

		// postType,
		int postType = 0;//all posts



		ArrayList<Post> posts = new ArrayList<>();
		ArrayList<Post> newPosts = new ArrayList<>();

		try {

			posts = postDao.getPosts(friendList,groups,mentor,0,postType,page,lastId);

			Iterator postsItr = posts.iterator();
			while (postsItr.hasNext()){

				Post post = (Post) postsItr.next();
				User user = userDao.getUserName(post.getPostby_id());
				try {
					Map<String, String> postTo = getPostedWall(post.getPostto_id(), post.getWall_type());
					post.setPostto_name(postTo.get("wallId"));
					post.setPostPageName(postTo.get("wallName"));
				}catch (Exception e){

					logger.info("error in getting posted wall link and name "+e);
				}


				post.setPostby_name(user.getFirst_name()+" "+user.getLast_name());
				post.setPostby_pic(userDao.getUserPic(user.getId(),1,1));
				newPosts.add(post);
			}



		}catch (Exception e){

			logger.info("error in posts "+e);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("homePage"+verticalId+"u"+userId+"p"+page, posts);
		return posts;

	}



	public ArrayList<Post> getProfilePosts(long userId,int page,long lastId)throws Exception{

		logger.info("  inide page  "+page);


		ArrayList<Post> cachePosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("profilePagea"+"u"+userId+"p"+page,ArrayList.class);

		if(cachePosts != null){
			logger.info(" fetching from cache posts  ");
			return cachePosts;
		}


		ArrayList<Post> posts = new ArrayList<>();
		ArrayList<Post> newPosts = new ArrayList<>();

		try {

			posts = postDao.getProfilePost(userId,page,lastId);

			Iterator postsItr = posts.iterator();
			while (postsItr.hasNext()){

				Post post = (Post) postsItr.next();
				User user = userDao.getUserName(post.getPostby_id());

				post.setPostby_name(user.getFirst_name()+" "+user.getLast_name());
				post.setPostby_pic(userDao.getUserPic(user.getId(),1,1));
				newPosts.add(post);
			}



		}catch (Exception e){

			logger.info("error in posts "+e);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("profilePage"+"u"+userId+"p"+page, posts);
		return posts;

	}


	public ArrayList<Post> getMetorFollowerPost(long userId,long visitorId,byte type)throws Exception{
		logger.info("  inside fetching post  type  "+type);


		ArrayList<Post> cachePosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("mentorFollowerPostd"+"u"+userId+"p"+type,ArrayList.class);

		if(cachePosts != null){
			logger.info(" fetching from cache posts  ");
			return cachePosts;
		}


		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getMentorPagePosts(userId,visitorId,type);
		}catch (Exception e){
			logger.info("error in fetchoing mentor posts"+e);
		}


logger.info("post from service ");
		guavaAndCouchbaseCache.putObjectAsByteInCache("mentorFollowerPost"+"u"+userId+"p"+type, posts);


		return posts;



	}

	public ArrayList<Post> getMetorWallPost(long userId,long visitorId,byte isAdmin,long lastId,int page)throws Exception{
		logger.info("  inside fetching post  public/private isadmin =   "+isAdmin);


		ArrayList<Post> cachePosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("mentorWallPostdx"+"u"+userId+"p"+isAdmin,ArrayList.class);

		if(cachePosts != null){
			logger.info(" fetching from cache posts  ");
			return cachePosts;
		}


		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getMentorWallPosts(userId,visitorId,isAdmin,lastId,page);
		}catch (Exception e){
			logger.info("error in fetchoing mentor wall posts"+e);
		}


		logger.info("post from service ");
		guavaAndCouchbaseCache.putObjectAsByteInCache("mentorWallPostd"+"u"+userId+"p"+isAdmin, posts);


		return posts;



	}


	public ArrayList<Post> getMetorMedia(long userId,long visitorId,byte postType,int page)throws Exception{
		logger.info("  inside fetching post  public/private isadmin =   "+postType);


		ArrayList<Post> cachePosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("mentorWallPostdx"+"u"+userId+"p"+page,ArrayList.class);

		if(cachePosts != null){
			logger.info(" fetching from cache posts  ");
			return cachePosts;
		}


		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getMentorMedia(userId,postType,page);
		}catch (Exception e){
			logger.info("error in fetchoing mentor wall posts"+e);
		}


		logger.info("post from service ");
		guavaAndCouchbaseCache.putObjectAsByteInCache("mentorWallPostd"+"u"+userId+"p"+page, posts);


		return posts;



	}

	public int getMentorMediaPostCount(long mentorId,byte mediaType)throws Exception{

		int cnt =0;
		try{

			cnt = postDao.getMentorMediaPostCount(mentorId,mediaType);
		}catch (Exception e){

			logger.info("error in getting media count "+e);
		}
		return cnt;
	}


	public ArrayList<Post> getMentorTalk(long userId,long visitorId,byte page,long lastId)throws Exception{
		logger.info("  inside fetching talk  ");


		ArrayList<Post> cachePosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("mentorTalkxx"+"u"+userId+"p"+visitorId+"page"+page+lastId,ArrayList.class);

		if(cachePosts != null){
			logger.info(" fetching from cache posts  ");
			return cachePosts;
		}


		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getMentorTalk(userId,visitorId,page,lastId);
		}catch (Exception e){
			logger.info("error in fetchoing mentor wall posts"+e);
		}


		logger.info("post from service ");
		guavaAndCouchbaseCache.putObjectAsByteInCache("mentorTalkx"+"u"+userId+"p"+visitorId+"page"+page+lastId, posts);


		return posts;



	}




	public ArrayList<Post> getMentorAchieve(long userId,long visitorId,byte page,long lastId)throws Exception{
		logger.info("  inside fetching talk  ");


		ArrayList<Post> cachePosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("mentorTalkxx"+"u"+userId+"p"+visitorId+"page"+page+lastId,ArrayList.class);

		if(cachePosts != null){
			logger.info(" fetching from cache posts  ");
			return cachePosts;
		}


		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getMentorAchieve(userId,visitorId,page,lastId);
		}catch (Exception e){
			logger.info("error in fetchoing mentor wall posts"+e);
		}


		logger.info("post from service ");
		guavaAndCouchbaseCache.putObjectAsByteInCache("mentorTalkx"+"u"+userId+"p"+visitorId+"page"+page+lastId, posts);


		return posts;



	}



	public ArrayList<Post> getUserMedia(long userId,byte mediaType,byte page,long lastId)throws Exception{

		ArrayList<Post> media = new ArrayList<>();

		try {
			logger.info("calling post dao ");
			media = postDao.getMedia(userId, mediaType, page, lastId);
		}catch (Exception e){
			logger.error("error in getting media data from postdao "+e);
		}

		return media;

	}


	public Map<String,String> getPostedWall(long wallId,String wallType) throws Exception {

		int wType = Integer.parseInt(wallType);
		String name = "";
		String id = "";

		if(wType==1){
			//user
		User user = userDao.getUserName(wallId);
			name = user.getFirst_name()+" "+user.getLast_name();
			id = user.getUserId();

		}else if (wType == 2){
			//group
			Group group = groupDao.getGroup(wallId);
			name = group.getName();
			id = group.getCustomUrl();

		}else if (wallId == 3){
			//mentor
			User user = userDao.getUserName(wallId);
			name = user.getFirst_name()+" "+user.getLast_name();
			id = user.getUserId();

		}

		Map<String,String> wall = new HashMap<>();
		wall.put("wallName" ,name);
		wall.put("wallId",id);

		return wall;
	}





}
