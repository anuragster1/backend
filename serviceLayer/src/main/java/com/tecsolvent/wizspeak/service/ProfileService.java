package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.UserDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.Comment;
import com.tecsolvent.wizspeak.model.FriendRequest;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.model.User;
import org.apache.log4j.Logger;

import java.security.Timestamp;
import java.util.*;

/**
 * Created by jaison on 12/4/16.
 */
public class ProfileService {

	public static Logger logger = Logger.getLogger(ProfileService.class);
	private PostService postService;
	private UserService userService;
	private CommentService commentService;
	private GroupService groupService;
	private FriendService friendService;
	private MentorService mentorService;
	private LikeService likeService;
	private UserDao userDao;
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;


	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}

	public void setMentorService(MentorService mentorService) {
		this.mentorService = mentorService;
	}

	public void setLikeService(LikeService likeService) {
		this.likeService = likeService;
	}

	public Map<String,Object> getProfileJson(String userIdStr) throws Exception{

		long userId = 0;

		try {
			userId = userService.getUserId(userIdStr);
		}catch (Exception e){

			logger.info("error in grtting user id "+e);
		}

		logger.info(" getting profile page json data");

		logger.info("ambition page json from mysql");
		Map<String,Object> mapObject = new HashMap<>();


		logger.info("               getting user details");
		//user profile
		User user = userService.userDetails(userId);
		mapObject.put("userDetails",user);

		try {
			logger.info("               getting  userGroup");
			ArrayList userGroups = groupService.getUserGroups(userId);
			mapObject.put("userGroup",userGroups);

		}catch (Exception e){

			logger.info("error in fetching userGroups "+e);
		}

		//get user friends
		ArrayList<User> friends = new ArrayList<>();
		try {
			logger.info("               getting  user friends");
			friends = friendService.getUserFriends(userId);

		}catch (Exception e){

			logger.info("error in fetching friends "+e);
		}

		mapObject.put("frindList",friends);

		//get mentor list

		ArrayList<User> mentors = new ArrayList<>();

		try {
			mentors = mentorService.getUserMentors(userId);

		}catch (Exception e){

			logger.info("errro in getting mentor list "+e);
		}
		mapObject.put("mentorList",mentors);

		byte rate = 1;
		try {
			rate = userService.getUserRating(userId);
			logger.info("user user rating is "+rate);
		}catch (Exception e){
			logger.info(" error in getting user rating ");
		}

		mapObject.put("userRating",rate);

		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionJson"+userId, mapObject);

		logger.info("  completed fetching json for ambition page ");

		return mapObject;


	}

	public ArrayList<Post> getProfilePosts(String userIdStr,String visitorIdStr, int page,long lastId)throws Exception{

		logger.info("am getting post for page no "+page+" and name of cache "+"profile "+userIdStr+"p"+page+"lastId"+lastId);

		ArrayList<Post> ambitionPosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("profilePostssd"+userIdStr+"p"+page+"last"+lastId,ArrayList.class);

		if(ambitionPosts!=null){
			logger.info("post from from cache");
			return ambitionPosts;
		}
		logger.info("fetch profile  post user id ="+userIdStr);
		ArrayList<Post> posts = new ArrayList<>();

		long userId;
		long visitorId;
		try {
			 userId = userService.getUserId(userIdStr);
			 visitorId = userService.getUserId(visitorIdStr);
		}catch (Exception e){
			logger.error("error in getting usr iDs "+e);
			throw  e;
		}


		ArrayList<Post> newPostArray = new ArrayList<>();

		try {
			//1 - ambition home page
			try {
				posts = postService.getProfilePosts(userId,page,lastId);
			}catch (Exception e){

				logger.info("error in getting profile post from post servicccce "+e);
			}


			Iterator post = posts.iterator();


			while (post.hasNext()){

				Post newPost = (Post) post.next();

				User postUser = userService.userDetails(newPost.getPostby_id());

				//check i like or not
				logger.info("checking likes id = "+newPost.getId()+"  visit ="+visitorId);
				try {
					newPost.setiLikes(likeService.checkLike(newPost.getId(), visitorId, (byte) 1));
				}catch (Exception e){
					logger.info("error in checklike for id = "+newPost.getId()+"  visit ="+visitorId+e);
				}

				try {
					//no of likes
					newPost.setLikes(likeService.getLikeCount(newPost.getId(), visitorId, 1));
				}catch (Exception e){
					logger.info("error in gettijng likecount "+e);
					throw e;
				}

				newPost.setPostby_name(postUser.getFirst_name()+" "+postUser.getLast_name());
				newPost.setPostby_pic(postUser.getProfilePic());

				try {
					logger.info("getting comments for poost");
					ArrayList<Comment> cmts = commentService.getPostComments(newPost.getId(), newPost.getPostby_id());

					newPost.setComments(cmts);
				}catch (Exception e){
					logger.info("error in getting comments"+e);
					throw e;
				}
				newPostArray.add(newPost);
			}


		}catch (Exception e){

			logger.info(" error in getting posts "+e);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("profilePostdip" + userId + "p" + page + "last" + lastId, newPostArray);

		return newPostArray;

	}




	public int getUserProfileRole(String pageUser,String loginUser) throws Exception{

		if(pageUser.equals(loginUser)){
			//admin user
			return 1;
		}


		int cnt = 0;

		try {
			cnt = friendService.checkUserUserStatus(pageUser,loginUser);
			if(cnt > 0){
				return 2;
			}
		}catch (Exception e){

			logger.info("error in grtting user id "+e);
		}

     //no user user relation
		return 0;
	}


	public boolean addUserProfilePic(String link, String userStr, byte wallType) throws Exception {

		long userId = 0;
		logger.info("add profile pic Service");
		try {
			userId = userService.getUserId(userStr);
		}catch (Exception e){

			logger.info("error in grtting user id "+e);
		}
		logger.info("link = "+link+" userId " +userId);
		boolean status =false;

		try{

			logger.info("am not going to groupService");


			groupService.profilePic(link, userStr, wallType);

			//userService.addUserProfilePic(link,userId);
			status =true;

		}catch(Exception e){

			logger.info("                user service addUserProfilePic");
		}

		return status;


	}

	public User userDetails(String userIdStr) throws Exception{

		long userId = 0;
		logger.info("add profile pic Service");
		try {
			userId = userService.getUserId(userIdStr);
		}catch (Exception e){

			logger.info("error in grtting user id "+e);
		}


		User user = userService.userDetails(userId);

		return user;
	}


	public ArrayList<FriendRequest> getFriendStatus(String user_id_a, String user_id_b) throws Exception {

		ArrayList<FriendRequest> getFriendStatus = (ArrayList<FriendRequest>) guavaAndCouchbaseCache.getObjectFromCache("getFriendStatuas6666" + user_id_a + user_id_b, List.class);
		logger.info((user_id_a));
		logger.info((user_id_b));

		long userIda = 0;

		try {
			userIda = userService.getUserId(user_id_a);
		}catch (Exception e){

			logger.info("error user id 1 "+e);
		}

		long userIdb = 0;

		try {
			userIdb = userService.getUserId(user_id_b);
			logger.info(userIda);
			logger.info(userIdb);


		}catch (Exception e){

			logger.info("error user id 2 "+e);
		}

		if (getFriendStatus == null) {



			logger.info("getFriendStatus call from mysql"+ userIda+"            " +userIdb);
			try {

				getFriendStatus = friendService.getFriendStatus(userIda,userIdb);
				logger.info("retun service"+getFriendStatus);
			}catch (Exception e){

				logger.info("error to frnddao "+e);
			}

			guavaAndCouchbaseCache.putObjectAsByteInCache("gzzzetFriendStatus1" + user_id_a + user_id_b, getFriendStatus);

		} else {
			logger.info("getFriendStatus call from cache");
		}
		logger.info(getFriendStatus.toString());
		return getFriendStatus;

	}


	public Boolean updateUserDet(User user)throws Exception{

		Boolean status = false;
		try {
			status = userService.updateUserDet(user);
		}catch (Exception e){

			logger.info("error in update user det in user service "+e);
		}
		return status;
	}


	public ArrayList<User> getUserMentor(String userStr) throws Exception{

		long userId = 0;

		try {
			userId = userService.getUserId(userStr);
		}catch (Exception e){

			logger.info("error user id 1 "+e);
		}

		ArrayList<User> mentors = new ArrayList<>();

		try {
			mentors = mentorService.getUserMentors(userId);

		}catch (Exception e){

			logger.info("errro in getting mentor list "+e);
		}
		return mentors;

	}



	public ArrayList<Post> getUserMedia(String userStr,byte mediaType,byte page,long lastId) throws Exception{

		ArrayList<Post> posts = new ArrayList<>();
		long userId = 0;
		try{
			userId = userService.getUserId(userStr);

		}catch (Exception e){
			logger.error("error in getting user id "+e);
		}
		logger.info("calling post service ");
		posts = postService.getUserMedia(userId,mediaType,page,lastId);

		Iterator itr = posts.iterator();
		while (itr.hasNext()){

		Post post = new Post();
			post = (Post) itr.next();

			User user = userService.userDetails(post.getPostby_id());

			post.setPostby_name(user.getFirst_name()+" "+user.getLast_name());


		}

		return posts;

	}



	public HashMap<String, Object> userUserStatus(String userStr, String visitorStr) throws Exception{

		HashMap<String ,Object> status = new HashMap<>();

		status.put("id",0);
		status.put("word","Add Friend");
		status.put("status",0);

		long userId=0;
		long visitorId=0;
		try{
			userId = userService.getUserId(userStr);
			visitorId = userService.getUserId(visitorStr);
		}catch (Exception e){
			logger.error(" error in getting user ids "+e);
		}
		try {


			long[] state = userService.getUserUserStatus(userId, visitorId);

			if(state[3] == 0){

				if(state[1]== userId){
					//waiting for approvel of other
					status.put("id",state[0]);
					status.put("word","Cancel Friend Requested");
					status.put("status","");
				}else{
					//waiting for approvel of me
					status.put("id",state[0]);
					status.put("word","Accept Friend Requested");
					status.put("status",1);

				}
			}
			if(state[3] == 2){
					status.put("id",state[0]);
					status.put("word","Add Friend");
					status.put("status",0);

			}
			if(state[3] == 1){

					status.put("id",state[0]);
					status.put("word","Remove Friend");
					status.put("status","");
			}

		}catch (Exception e){

			logger.error("error in getting user user staus" +e);
		}

		return status;
	}


	public boolean amOnline(String userId)throws Exception{

		logger.info("am online");
		Date date = new Date();
		Date oldDate = (Date) guavaAndCouchbaseCache.getObjectFromCache("userOnline"+userId,String.class);

		logger.info(" ur old and new tome stanps "+date.toString()+"  "+oldDate.toString());

		return true;
	}

	public boolean addUserRating(String profileStr,String userStr,byte rate) throws Exception {

		long userId;
		long profileId;
		logger.info("addUserRating profile service ");
		try{

			userId = userService.getUserId(userStr);
			profileId = userService.getUserId(profileStr);

		}catch (Exception e){
			logger.error("error in getting user id "+e);
			throw e;

		}
		boolean status = false;
		try {
			status = userService.addUserRating(profileId, userId, rate);
		}catch (Exception e){

			logger.error("adding user rating error "+e);
		}
		return status;
	}

	public byte getUserUserRating(String profileStr,String userStr)throws Exception{

		long userId;
		long profileId;
		logger.info("getUserUserRating profile service ");
		try{

			userId = userService.getUserId(userStr);
			profileId = userService.getUserId(profileStr);

		}catch (Exception e){
			logger.error("error in getting user id "+e);
			throw e;

		}
		byte status = 1;
		try {
			status = userService.getUserUserRating(profileId, userId);
		}catch (Exception e){

			logger.error("adding user rating error "+e);
		}
		return status;

	}
	public byte getUserRating(String profileStr)throws Exception{


		long profileId;
		logger.info("getUserRating profile service ");
		try{

			profileId = userService.getUserId(profileStr);

		}catch (Exception e){
			logger.error("error in getting user id "+e);
			throw e;

		}
		byte status = 1;
		try {
			status = userService.getUserRating(profileId);
		}catch (Exception e){

			logger.error("adding user rating error "+e);
		}
		return status;

	}


	public HashMap<Integer,String> getUserCateMap(byte vertical,String userStr,byte isMentor){

		HashMap<Integer,String> getUserCateMap = new HashMap<>();
		long userId = 0;
		try{
			userId = userService.getUserId(userStr);
		}catch (Exception e){
			logger.info("errror in getting user id "+e);
		}
		try{

			getUserCateMap = userService.getUserCate(vertical,userId,isMentor);
		}catch (Exception e){

			logger.info("error in getting user cate "+e);

		}
		return getUserCateMap;

	}


	public Boolean addUserCatRel(int category,byte vertical,String userStr,byte userType) throws Exception{

		long userId = 0;
		boolean status = false;
		try{

			userId = userService.getUserId(userStr);
		}catch (Exception e){
			logger.info(" error in getting user id "+e);
		}
		try {
			status = userService.addUserCatRel(category, vertical,userId,userType);
		}catch (Exception e){
			logger.info("error in adding /update "+e);
		}
		return status;
	}


	public boolean removeAllUserCatMap(byte vertical,String userStr,byte userType)throws Exception{

		boolean stat = false;
		long userId = 0;

		try {
			userId = userService.getUserId(userStr);
			logger.info(" removing all user cate ");
			userService.removeAllUserCatMap(vertical,userId,userType);
		}catch (Exception e){

			logger.error("error in removing all user cate map "+e);
		}
		return stat;
	}


	public boolean addCoverPic(String link, String userStr) throws Exception {

		long userId = 0;
		logger.info("add cover  pic Service");
		try {
			userId = userService.getUserId(userStr);
		} catch (Exception e) {

			logger.info("error in grtting user id " + e);
		}
		logger.info("link = " + link + " userId " + userId);
		boolean status = false;

		try {

			logger.info("to porofile DAO");

			int wall_type = 1;
			groupService.addCoverPic(link, userStr, wall_type);

			//userService.addCoverPic(link,userId);
			status = true;

		} catch (Exception e)

		{

			logger.info(" user service addCoverPic");
		}

		return status;


	}


	public boolean add_grp_coverpic(String link, String userStr) throws Exception {

		long userId = 0;
		logger.info("add add_grp_coverpic  pic Service");
		try {
			userId = userService.getUserId(userStr);
		} catch (Exception e) {

			logger.info("error in grtting user id " + e);
		}
		logger.info("link = " + link + " userId " + userId);
		boolean status = false;

		try {

			logger.info("to add_grp_coverpic DAO");

			int wall_type = 2;
			groupService.addCoverPic(link, userStr, wall_type);

			//userService.addCoverPic(link,userId);
			status = true;

		} catch (Exception e)

		{

			logger.info(" user service add_grp_coverpic");
		}

		return status;


	}

	public ArrayList<String> countryId(long countryId) throws Exception {

		ArrayList<String> status = (ArrayList<String>) guavaAndCouchbaseCache.getObjectFromCache("countryid" + countryId, List.class);

		if (status == null) {

			logger.info(" country  from mysql direct");
			status = userDao.countryId(countryId);

			guavaAndCouchbaseCache.putObjectAsByteInCache("countryidz" + countryId, status);

		} else {
			logger.info("country fetching from cache");
		}

		return status;
	}

	public Map<String,Boolean> checkLive(String[] users) throws Exception{
		Map<String,Boolean> liveStatus = new HashMap<>();
		try{

			for(int i=0;i<users.length;i++){

				long uId = userService.getUserId(users[i]);
				logger.info("got user id = "+uId);
				boolean live = userService.checkLive(uId);
				logger.info("completed the x]call xxxxx");
				liveStatus.put(users[i],live);
			}

		}catch (Exception e){
		logger.error("error in getting live status"+e.getMessage());
		}
		return liveStatus;
	}

}
