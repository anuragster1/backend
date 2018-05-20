package com.tecsolvent.wizspeak.service;


import com.tecsolvent.wizspeak.*;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.*;
import com.tecsolvent.wizspeak.notification.dao.Notification.Type;
import com.tecsolvent.wizspeak.notification.upstream.handler.api.IWizspeakUpstreamHandler;
import com.tecsolvent.wizspeak.notification.upstream.handler.impl.WizspeakUpstreamHandler;
import com.tecsolvent.wizspeak.utility.DateUtil;
import com.tecsolvent.wizspeak.utility.StringUtil;

import wizspeak.notification.client.client.WizNotification;

import org.apache.log4j.Logger;
import com.tecsolvent.wizspeak.kafka.KafkaConsumer;
import com.tecsolvent.wizspeak.kafka.KafkaProducer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * @author sandeep
 * @since 19/02/16
 */
public class AmbitionService {

	public static Logger logger = Logger.getLogger(AmbitionService.class);

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
	//private WizspeakUpstreamHandler likeUpstreamHandler;

	private int verticalId = 1;


	public int getVerticalId() {
		return verticalId;
	}

	public void setVerticalId(int verticalId) {
		this.verticalId = verticalId;
	}

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

	/*public void setWizspeakUpstreamHandler(WizspeakUpstreamHandler wizspeakUpstreamHandler){
		this.likeUpstreamHandler = wizspeakUpstreamHandler;
	}*/

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



	public Map<String,Object> getPageJson(long userId) throws Exception{

		logger.info("getting json obj for ambition page ");

		Map<String,Object> ambitionJson = (Map<String, Object>) guavaAndCouchbaseCache.getObjectFromCache("ambitionJsonxz"+userId,Map.class);


		if(ambitionJson!=null){
			logger.info("ambition page json from cache");
			return ambitionJson;
		}
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

		}catch (Exception e){
			logger.info(" error in getting user rating ");
		}

		mapObject.put("userRating",rate);

		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionJson"+userId, mapObject);

		logger.info("  completed fetching json for ambition page ");

		return mapObject;
	}


	public User getUserName(long user_id) throws Exception {

		User user = (User) guavaAndCouchbaseCache.getObjectFromCache("userNamez"+user_id,User.class);

			if(user == null){

				logger.info("name fetching from mysql direct");
				user = userDao.getUserName(user_id);
				guavaAndCouchbaseCache.putObjectAsByteInCache("userName"+user_id, user);

			}else{

				logger.info(" name fetching from cache ");
			}

		return user;

	}


	public String getUserStatus(long user_id) throws Exception {

		String status = (String) guavaAndCouchbaseCache.getObjectFromCache("userStatus"+user_id,String.class);
		if(status == null){

			logger.info(" status fetching from mysql direct");
			status = userDao.getUserStatus(user_id,(byte)0);
			if(status == null){  status=" "; }
			guavaAndCouchbaseCache.putObjectAsByteInCache("userStatus"+user_id, status);

		}else{

			logger.info("status fetching from cache");
		}

		return status;
	}

	public String getUserPic(long user_id) throws Exception {

		String profilePic = (String) guavaAndCouchbaseCache.getObjectFromCache("userProfilePicc"+user_id,String.class);
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



	public User userDetails(long user_id) throws Exception {

		User user = new User();

		try{

			user = userService.userDetails(user_id);

		}catch (Exception e){


		}

		return user;
	}


	public ArrayList getUserGroups(long user_id) throws Exception {

		//get ambition groups

		 ArrayList groups = (ArrayList) guavaAndCouchbaseCache.getObjectFromCache("userGroupsz"+user_id,ArrayList.class);

		if(groups!=null){
			//System.out.println("from Group cache  ");
			return groups;
		}

		logger.info("fetching from mysql direct");

		ArrayList<Group> group = groupDao.getUserAmbitionGroups(user_id);

		Iterator<Group>  itr = group.iterator();

		ArrayList<Group> aGroups = new ArrayList<Group>();
		ArrayList<Group> hGroups = new ArrayList<Group>();
		ArrayList<Group> tGroups = new ArrayList<Group>();

		while (itr.hasNext()){


			Group groupz = itr.next();

			logger.info("group id s "+groupz.getVertical_id());
			if(groupz.getVertical_id()==1){

				aGroups.add(groupz);
			}
			if(groupz.getVertical_id()==2){

				hGroups.add(groupz);
			}
			if(groupz.getVertical_id()==4){

				tGroups.add(groupz);
			}

		}

		ArrayList allGroups = new ArrayList();

		try {
			allGroups.add(aGroups);
			allGroups.add(hGroups);
			allGroups.add(tGroups);
		}catch (Exception E){
			//System.out.println("AmbitionService  service 129 ");
		}

			guavaAndCouchbaseCache.putObjectAsByteInCache("userGroups"+user_id, allGroups);

		return allGroups;

	}


	public ArrayList<User> getUserFriends(String userStr) throws Exception{

		ArrayList<User> user = new ArrayList<>();
		try{
				long userId = userService.getUserId(userStr);
				user = friendService.getUserFriends(userId);
		}catch (Exception e){

			logger.info("error in getting user friends "+e);
		}
		return  user;
	}


	public ArrayList<User> getUserFriends(long user_id) throws Exception{


		ArrayList<User> uFriend = (ArrayList<User>) guavaAndCouchbaseCache.getObjectFromCache("userFriendsc"+user_id,ArrayList.class);
		if(uFriend!=null){
			//System.out.println("from frinds cache  ");
			return uFriend;
		}

		ArrayList<Long> frinds = friendDao.getUserFriends(user_id);
		ArrayList<User> myFriends = new ArrayList<User>();

		Iterator<Long> itr = frinds.iterator();

		while (itr.hasNext()){

			User user = new User();

			user = getUserName(itr.next());

			myFriends.add(user);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("userFriends"+user_id, myFriends);

		return myFriends;
	}



	public ArrayList<User> getUserMentors(long user_id) throws Exception{


		logger.info("no r");
		ArrayList<User> uMentors = (ArrayList<User>) guavaAndCouchbaseCache.getObjectFromCache("userMentora"+user_id,ArrayList.class);

		if(uMentors!=null){
			logger.info("from Cache");
			return uMentors;
		}
		logger.info("from mysql diret");
		ArrayList<Long> mentorIds = mentorDao.getUserMentors(user_id);
		ArrayList<User> mentors = new ArrayList<User>();

		Iterator itr = mentorIds.iterator();

		while (itr.hasNext()){

			User user = new User();

			user = getUserName((Long) itr.next());
			mentors.add(user);
		}
		guavaAndCouchbaseCache.putObjectAsByteInCache("userMentor"+user_id, mentors);

		return mentors;
	}


	public ArrayList<Post> getAmbitionPosts(long user_id,int verticalId,int page,long lastId) throws Exception{

		logger.info("am getting post for page no "+page+" and name of cache "+"ambitionPost"+user_id+"p"+page);

		ArrayList<Post> ambitionPosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("ambitionPostd"+user_id+"p"+page,ArrayList.class);

		if(ambitionPosts!=null){
			logger.info("post from from cache");
			return ambitionPosts;
		}
		logger.info("fetch ambi post user id ="+user_id);
		ArrayList<Post> posts = new ArrayList<>();


		ArrayList<Post> newPostArray = new ArrayList<>();

		try {
			//1 - ambition home page
			posts = postService.getHomePosts(1,user_id,page,lastId);

			Iterator post = posts.iterator();


			while (post.hasNext()){

				Post newPost = (Post) post.next();


				User postUser = userService.userDetails(newPost.getPostby_id());

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


				HashMap<Integer, Long> likes = likeDao.getLikes(newPost.getId(), 1);

				newPost.setiLikes(false);
				if (!likes.isEmpty()) {

					if (likes.containsValue(user_id)) {
						logger.info("getting i liked or not ");
						newPost.setiLikes(true);

					}
					newPost.setLikes(likes.size());
				}
				newPostArray.add(newPost);

			}


		}catch (Exception e){

			logger.info(" error in getting posts "+e);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionPost"+user_id+"p"+page, newPostArray);

		return newPostArray;
	}


	public ArrayList<Comment> getPostComments(long post_id,long user_id) throws Exception{

		ArrayList<Comment> comments = (ArrayList<Comment>) guavaAndCouchbaseCache.getObjectFromCache("postCommventsa"+post_id,ArrayList.class);

		if(comments!=null){

			return comments;
		}


		ArrayList<Comment> commentz = new ArrayList<Comment>();
		// 3- comments
		commentz = commentDao.getPostComments(post_id,4);


		ArrayList<Comment> newComments = new ArrayList<Comment>();

		Iterator itr = commentz.iterator();

		while (itr.hasNext()){

			User user = new User();

			Comment comment = (Comment) itr.next();
			user = getUserName(comment.getCommenter_id());
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


		return newComments;
	}




	public ArrayList<Category> getCategories(int vId) throws Exception{

		logger.info("vertical is "+verticalId);

		ArrayList<Category> category = (ArrayList<Category>) guavaAndCouchbaseCache.getObjectFromCache("ambitionCategory",ArrayList.class);

		if(category!=null){

			return category;
		}

		ArrayList<Category> categories = new ArrayList<>();
logger.info("getting from mysql vId "+vId);
		categories = groupDao.getCategories(vId);
		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionCategory1",categories);

		return categories;
	}



	public ArrayList<SubCategory> getSubCategories() throws Exception{

		logger.info("in service category ");

		ArrayList<SubCategory> category = (ArrayList<SubCategory>) guavaAndCouchbaseCache.getObjectFromCache("ambitionSubCategories",ArrayList.class);

		if(category!=null){

			return category;
		}


		ArrayList<SubCategory> subCategories = new ArrayList<>();

		subCategories = groupDao.getSubCategories();
		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionSubCategories1",subCategories);

		return subCategories;
	}


	public HashMap<String, String> addPost(Post post) throws Exception{

		logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxccccccccccccccccccc add post"+post.toString());

		if(post.getPostby_name() != null && !post.getPostby_name().isEmpty()){

			//get userId

			post.setPostby_id(userService.getUserId(post.getPostby_name()));
		}

		if(post.getPostto_name() != null && !post.getPostto_name().isEmpty()){

			//get userId
			if(Integer.parseInt(post.getWall_type())== 2){


				long groupId = 0;

				//get groupId
				try {
					groupId = userDao.getWallId(post.getPostto_name());
				}catch (Exception e){

					logger.info("error in fectching group id "+e);
					throw e;
				}
				post.setPostto_id(groupId);
			}else{
				post.setPostto_id(userService.getUserId(post.getPostto_name()));
			}



		}
		logger.info(post.toString());

		HashMap<String, String> s = postDao.addPost(post);
        return  s;

	}


	public HashMap<String, String> addMentorPost(Post post) throws Exception {

		logger.info(" add mentor post");

		HashMap<String, String> s = postDao.addMentorPost(post);
		return s;

	}

	public ArrayList<Post> getPost(ArrayList<Long> ids,long userId) throws Exception{


		ArrayList<Post> posts = postDao.getPost(ids);

		logger.info("posts from ids got ");

		ArrayList<Post> newPost = new ArrayList<Post>();

		Iterator itr = posts.iterator();

		while (itr.hasNext()) {
logger.info("feching as single post for user details");
			User user = new User();

			Post post = (Post) itr.next();

			try {

				try {
					user = (User) getUserName(post.getPostby_id());
				}catch (Exception e){
					logger.error("getUserName  function errror"+e);
				}
			String userPic = userDao.getUserPic(post.getPostby_id(),1,1);
			HashMap<Integer, Long> likes = likeDao.getLikes(post.getId(), 1);


	post.setPostby_name(user.getFirst_name() + " " + user.getLast_name());
	post.setPostby_pic(userPic);
	post.setLikes(likes.size());

	post.setComments(getPostComments(post.getId(), userId));
}catch (Exception e){
	logger.error("error in feching likes comment for post id "+post.getId()+e);
	throw e;
}
			logger.info(" post string  "+post.toString());
			newPost.add(post);
		}

		return newPost;
	}


	public Map<String ,String> checkLike(Map<String ,Object> like ) throws Exception {


		Map<String,String> likeStatus = new HashMap<>();

		likeStatus.put("success","1");
		likeStatus.put("count","0");
		likeStatus.put("post_id",""+like.get("item_id"));

		Map<String, String> liked = likeDao.checkLike(like);



		if(liked.isEmpty()){
			logger.info(" no previous like record ");
			//add new like
			logger.info("new like itemType"+like.get("item_type"));
			if(!likeDao.addLike(like)){
				likeStatus.put("success","0");

			}

		}else{
			liked.put("status", "1");
			logger.info("update previous column in like table");
			if(!likeDao.updateLike(liked)){

				likeStatus.put("success","0");
			}


		}


		HashMap<Integer, Long> likes = likeDao.getLike((Long) like.get("item_id"),(int) like.get("item_type"));

		if(!likes.isEmpty()){
			logger.info("no of likes is"+likes.size());
			likeStatus.put("count",likes.size()+"");

		}
		
		Long postOwnerId = (Long) like.get("post_owner_id");
		Long actorId = (Long) like.get("user_id");
		logger.info("Like invoked!! Sending notification now!! userid - " + postOwnerId + " and actorId - " + actorId);
		if(postOwnerId == null || actorId == null){
			logger.info("Not sending like status since userId or actorId is not defined!!");
			return likeStatus;
		}

		String userPicURL = userService.getUserPic(actorId);
                String name = "";
                User user = null;
               
                try{
                        user = userService.getUserName(actorId);
                        System.out.println("User instance null - " + (user == null));
                        if(user != null){
                                name = user.getFirst_name() + " " + user.getLast_name();
                        }
                        System.out.println("User name - " + name);
                }catch(Exception e){
                        System.err.println(e);
                }
		System.out.println("out of try catch");
		Map<String, String> msgContainer = new HashMap<String, String>();
		msgContainer.put("name", name);
		msgContainer.put("picUrl", userPicURL);
		System.out.println("populated msgContainer!!");
		//logger.info("Like invoked!! Sending notification now!! userid - " + userId + " and actorId - " + actorId);
		//wizspeakUpstreamHandler.sendNotification(userId, com.tecsolvent.wizspeak.notification.dao.Notification.Category.AMBITION, actorId, (Long) like.get("item_id"), Type.LIKE, msgContainer, true);
		
		//likeUpstreamHandler.sendNotification(postOwnerId, com.tecsolvent.wizspeak.notification.dao.Notification.Category.AMBITION, actorId, (Long) like.get("item_id"), Type.LIKE, msgContainer, false);
		WizNotification.createNotification(postOwnerId, com.tecsolvent.wizspeak.notification.dao.Notification.Category.AMBITION, actorId, (Long) like.get("item_id"), Type.LIKE, msgContainer, false);
		return likeStatus;
	}



	public Map<String ,String> removeLike(Map<String ,Object> like ) throws Exception {


		Map<String,String> likeStatus = new HashMap<>();

		likeStatus.put("post_id",""+like.get("item_id"));

			//remove  like
			likeDao.removeLike(like);
			likeStatus.put("success","1");

		HashMap<Integer, Long> likes = likeDao.getLikes((Long) like.get("item_id"),(int) like.get("item_type"));
		if(likes.isEmpty()){

			likeStatus.put("count","0");
		}
		likeStatus.put("count",""+likes.size());

		return likeStatus;
	}



	public Map<String,Object> addComment(Comment comment) throws Exception {

		Map<String,Object> ma = new HashMap<>();
		ma = commentDao.addComment(comment);
		return ma;

	}

	public Map<String,Object> updatePostText(Post post) throws Exception {

		Map<String,Object> p = new HashMap<>();
		p = postDao.updatePostText(post);
		return p;
	}

	public Map<String,Object> deletePost(Map<String,Object> delPost) throws Exception{
		logger.info("delete post service");
		Map<String,Object> status = postDao.deletePost(delPost);
		return status;
	}

	public ArrayList<Comment> getMoreComments(HashMap<String,Long> comment_id) throws Exception{
		logger.info("fetching more comments service");

		ArrayList<Comment> commentz = commentDao.getMoreComments(comment_id);
		Collections.reverse(commentz);
		long userId = comment_id.get("userId");

		ArrayList<Comment> newComments = new ArrayList<Comment>();

		Iterator itr = commentz.iterator();


		while (itr.hasNext()){

			User user = new User();

			Comment comment = (Comment) itr.next();
			user = getUserName(comment.getCommenter_id());
			String pic = userDao.getUserPic(comment.getCommenter_id(),1,1);

			HashMap<Integer, Long> likes = likeDao.getLikes(comment.getId(),2);

			comment.setLikes(likes.size());
			comment.setiLikes(false);
			if(likes.containsValue(userId)){
				//i liked comment
				comment.setiLikes(true);

			}

			comment.setcUserPic(pic);
			comment.setComenterName(user.getFirst_name()+" "+user.getLast_name());

			newComments.add(comment);
		}



		return newComments;
	}





	public boolean createGroup(Group group) throws Exception{

		logger.info("inside create group service");

		Long groupId;
		try {
			 groupId = groupDao.createGroup(group);
		}catch (Exception e){
			logger.info(" errorr in cretaing group "+e);
			throw e;
		}
		if(groupId!= null) {
		HashMap<String, Object> userGroupMaper = new HashMap<>();

		userGroupMaper.put("group_id", group.getId());
		userGroupMaper.put("rolesetby_id", group.getCreatedby_id());
		userGroupMaper.put("status", 1);
		userGroupMaper.put("invitedby_id", group.getCreatedby_id());
		userGroupMaper.put("date_invited", group.getDate_created());
		userGroupMaper.put("date_roleset", group.getDate_created());

		//add owner of group
		userGroupMaper.put("role_id", 1);
		userGroupMaper.put("user_id", group.getCreatedby_id());
		userGroupMaper.put("role_alias", "owner");


		// add owner
		GroupUser gUser = new GroupUser();
		gUser.setGroup_id(groupId);
		gUser.setUser_id(group.getCreatedby_id());
		gUser.setStatus(1);
		gUser.setRole_id(1);
		gUser.setRole_alias("owner");
		gUser.setDate_joined(group.getDate_created());
		gUser.setRolesetby_id(group.getCreatedby_id());
		gUser.setInvitedby_id(group.getCreatedby_id());


		groupDao.addGroupMember(gUser);
		//add members selected
		String invites = group.getInvites();

		String[] ids = invites.split(",");

		for (int i = 0; i < ids.length; i++) {

			logger.info(" invitee id is =" + ids[i]);

			GroupUser inviteeUser = new GroupUser();
			inviteeUser.setGroup_id(groupId);
			inviteeUser.setUser_id(Long.parseLong(ids[i]));
			inviteeUser.setStatus(5);
			inviteeUser.setRole_id(2);
			inviteeUser.setRole_alias("member");
			inviteeUser.setDate_joined(group.getDate_created());
			inviteeUser.setRolesetby_id(group.getCreatedby_id());
			inviteeUser.setInvitedby_id(group.getCreatedby_id());

			groupDao.addGroupMember(inviteeUser);
			logger.info("new user added to group");
		}
		try {


		} catch (Exception e) {

			logger.info("exception " + e);
		}

		logger.info("after GroupDao ");
		}else{

			return false;
		}

		return true;
	}


	public boolean addCustomGroupName(String groupName,Long groupId,int wallType,int status) throws Exception{

		//Add custom url for group
		String customName = StringUtil.getGroupCustomName(groupName);

		logger.info("new custom name "+customName);

		Map<String,Object> url = new HashMap<>();
		url.put("name",customName);
		url.put("wall_id",groupId);
		url.put("wall_type",wallType);
		url.put("status",status);
		url.put("date_created", DateUtil.getDate());

		boolean result = false;

		try {
			userDao.addCustomUrl(url);
			result = true;
		}catch (Exception e){
			logger.info("error in adding custom url "+e);
			throw e;
		}

		return result;
	}


	public List<UserEducation> getUserProfileEducation(String userIdStr) throws Exception {

		long userId = 0;

		try {
			userId = userService.getUserId(userIdStr);
		}catch (Exception e){

			logger.info("error in grtting user id "+e);
		}

		List<UserEducation> userProfileEducationMapper = (List<UserEducation>) guavaAndCouchbaseCache.getObjectFromCache("userEducation1" + userId, List.class);

		if (userProfileEducationMapper == null) {

			logger.info("userEducation call from mysql");
			userProfileEducationMapper = userDao.getUserProfileEducation(userId);

			guavaAndCouchbaseCache.putObjectAsByteInCache("userEducation" + userId, userProfileEducationMapper);

		} else {
			logger.info("userEducation call from cache");
		}

		return userProfileEducationMapper;
	}

	public List<Experience> getUserExperience(String userIdStr) throws Exception {

		List<Experience> getUserExperienceMapper = (List<Experience>) guavaAndCouchbaseCache.getObjectFromCache("userEducation1" + userIdStr, List.class);

		if (getUserExperienceMapper == null) {

			long userId = 0;
			userId = userService.getUserId(userIdStr);
			logger.info("getUserExperience call from mysql");
			getUserExperienceMapper = userDao.getUserExperience(userId);

			guavaAndCouchbaseCache.putObjectAsByteInCache("userEducation" + userIdStr, getUserExperienceMapper);

		} else {
			logger.info("getUserExperience call from cache");
		}

		return getUserExperienceMapper;
	}


	public List<Award> getAward(String userIdStr) throws Exception {

		List<Award> getUserExperienceMapper = (List<Award>) guavaAndCouchbaseCache.getObjectFromCache("award" + userIdStr, List.class);

		if (getUserExperienceMapper == null) {

			long userId = userService.getUserId(userIdStr);

			logger.info("award call from mysql");
			getUserExperienceMapper = mentorDao.getAward(userId);

			guavaAndCouchbaseCache.putObjectAsByteInCache("award1" + userIdStr, getUserExperienceMapper);

		} else {
			logger.info("award call from cache");
		}

		return getUserExperienceMapper;
	}

	public List<Certification> getCert(String userIdStr) throws Exception {

		List<Certification> getUserExperienceMapper = (List<Certification>) guavaAndCouchbaseCache.getObjectFromCache("award" + userIdStr, List.class);

		long userId  = userService.getUserId(userIdStr);

		if (getUserExperienceMapper == null) {

			logger.info("award call from mysql");
			getUserExperienceMapper = mentorDao.getCert(userId);

			guavaAndCouchbaseCache.putObjectAsByteInCache("award1" + userIdStr, getUserExperienceMapper);

		} else {
			logger.info("award call from cache");
		}

		return getUserExperienceMapper;
	}



	public Map<String, Object> addAward(Award post) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.addAward(post);


		return p;
	}


	public Map<String, Object> addCert(Certification post) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.addCert(post);


		return p;
	}

	public Map<String, Object> editCert(Certification post) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.editCert(post);
		return p;
	}

	public Map<String, Object> deleteCert(Certification post) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.deleteCert(post);
		return p;
	}


	public Map<String, Object> editAward(Award post) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.editAward(post);
		return p;
	}

	public Map<String, Object> deleteAward(Award post) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.deleteAward(post);
		return p;
	}

	public String updateUserEducation(UserEducation updateObject) throws Exception {


		try {
			userDao.updateUserEducation(updateObject);
		} catch (Exception e) {
			logger.info("error" + e);
		}
		return "";

	}

	public ArrayList<Post> getMentorPosts(long user_id, int verticalId) throws Exception {

		ArrayList<Post> mentorPosts = (ArrayList<Post>) guavaAndCouchbaseCache.getObjectFromCache("mentorPostscccx" + user_id, ArrayList.class);

		if (mentorPosts != null) {
			logger.info("post from from cache");
			return mentorPosts;
		}
		logger.info("fetch ambi post user id =" + user_id);
		ArrayList<Post> posts = new ArrayList<>();

		try {
			posts = postDao.getMentorPosts(user_id, verticalId);
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

				if (likes.containsValue(user_id)) {
					logger.info("getting i liked or not ");
					post.setiLikes(true);

				}
				post.setLikes(likes.size());
			}


			post.setPostby_name(user.getFirst_name() + " " + user.getLast_name());
			post.setPostby_pic(userPic);


			newPost.add(post);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("mentorPosts" + user_id, newPost);

		return newPost;
	}



	public ArrayList<Long> getMentorFollowerId(long user_id) throws Exception {

		ArrayList<Long> getMentorFollowerId = (ArrayList<Long>) guavaAndCouchbaseCache.getObjectFromCache("getMentorFollowerId1" + user_id, List.class);

		if (getMentorFollowerId == null) {

			logger.info("getMentorFollowerId call from mysql");
			getMentorFollowerId = mentorDao.getMentorFollowers(user_id);

			guavaAndCouchbaseCache.putObjectAsByteInCache("getMentorFollowerId" + user_id, getMentorFollowerId);

		} else {
			logger.info("getMentorFollowerId call from cache");
		}

		return getMentorFollowerId;
	}

	public Map<String, Object> mentorStatus(User user) throws Exception {

		Map<String, Object> p = new HashMap<>();

		long userId = userService.getUserId(user.getUserId());
		user.setId(userId);
		p = mentorDao.mentorStatus(user);

		if(p.get("success").equals("0")){

			logger.info(" add new status ");

		}
		return p;


	}

	public Map<String, Object> editMentorExperience(Mentor follow) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.editmentorExperience(follow);
		return p;
	}

	public Map<String, Object> deleteMentorExperience(Mentor follow) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.removementorExperience(follow);
		return p;
	}




	public Map<String, Object> addMentorExperience(Mentor follow) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.addmentorExperience(follow);
		return p;
	}


	public Map<String, Object> addMentorEducation(UserEducation follow) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.addMentorEducation(follow);
		return p;
	}

	public Map<String, Object> editMentorEducation(UserEducation follow) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.editMentorEducation(follow);
		return p;
	}

	public Map<String, Object> deleteMentorEducation(UserEducation follow) throws Exception {

		Map<String, Object> p = new HashMap<>();
		p = mentorDao.deleteMentorEducation(follow);
		return p;
	}


	public Map<String, Object> addFollow(MentorFollow follow) throws Exception {

		Map<String, Object> followz = new HashMap<>();

		follow.setmId(userService.getUserId(follow.getMentor_id()));
		follow.setuId(userService.getUserId(follow.getUser_id()));
		logger.info(" add follow service" + follow);
		followz = mentorDao.addFollow(follow);
		logger.info("service" + follow);
		return followz;


	}


	public Map<String, Object> addRating(MentorRate follow) throws Exception {

		Map<String, Object> followz = new HashMap<>();

		followz = mentorDao.addRating(follow);
		logger.info("service" + follow);
		return followz;


	}

	public Map<String, Object> reRating(MentorRate follow) throws Exception {

		Map<String, Object> followz = new HashMap<>();



		followz = mentorDao.reRating(follow);
		logger.info("service" + follow);
		return followz;


	}

	public Map<String, Object> unFollow(MentorFollow unfollow) throws Exception {

		Map<String, Object> p = new HashMap<>();

		unfollow.setmId(userService.getUserId(unfollow.getMentor_id()));
		unfollow.setuId(userService.getUserId(unfollow.getUser_id()));

		p = mentorDao.unFollow(unfollow);
		logger.info("service" + p);
		return p;


	}


	public Map<String, Object> reFollow(MentorFollow unfollow) throws Exception {

		Map<String, Object> p = new HashMap<>();
		unfollow.setmId(userService.getUserId(unfollow.getMentor_id()));
		unfollow.setuId(userService.getUserId(unfollow.getUser_id()));
		logger.info(" reFollow service ");
		p = mentorDao.reFollow(unfollow);
		logger.info("service" + p);
		return p;


	}





	public List<MentorRate> checkMentorRating(long user_id) throws Exception {

		List<MentorRate> getfollowCheckMapper = (List<MentorRate>) guavaAndCouchbaseCache.getObjectFromCache("followCheck" + user_id, List.class);

		if (getfollowCheckMapper == null) {

			logger.info("rating call from mysql");
			getfollowCheckMapper = mentorDao.checkMentorRating(user_id);

			guavaAndCouchbaseCache.putObjectAsByteInCache("followCheck1" + user_id, getfollowCheckMapper);

		} else {
			logger.info("raitng call from cache");
		}

		return getfollowCheckMapper;
	}


	public Map<String, Object> addFriend(FriendRequest addfrnd) throws Exception {

		Map<String, Object> followz = new HashMap<>();

		long userIda = 0;

		userIda = userService.getUserId(addfrnd.getRequest_status("user_id_a"));
		addfrnd.setRequest_status(String.valueOf(userIda));

		long userIdb = 0;

		userIdb = userService.getUserId(addfrnd.getUser_id_b("user_id_b"));
		addfrnd.setUser_id_b(String.valueOf(userIdb));


		followz = friendDao.addFriendz(addfrnd);
		logger.info("service" + addfrnd);
		return followz;


	}



	public Map<String, Object> cancelFriend(FriendRequest addfrnd) throws Exception {

		Map<String, Object> followz = new HashMap<>();

		long userIda = 0;

		userIda = userService.getUserId(addfrnd.getRequest_status("user_id_a"));
		addfrnd.setRequest_status(String.valueOf(userIda));

		long userIdb = 0;

		userIdb = userService.getUserId(addfrnd.getUser_id_b("user_id_b"));
		addfrnd.setUser_id_b(String.valueOf(userIdb));


		followz = friendDao.cancelFriend(addfrnd);

		logger.info("aaaaa" + userIda);
		logger.info("bbbbbbbb" + userIdb);
		return followz;


	}
	public Map<String, Object> rejectorAcceptFriend(FriendRequest addfrnd) throws Exception {

		Map<String, Object> followz = new HashMap<>();

		long userIda = 0;

		userIda = userService.getUserId(addfrnd.getRequest_status("user_id_a"));
		addfrnd.setRequest_status(String.valueOf(userIda));

		long userIdb = 0;

		userIdb = userService.getUserId(addfrnd.getUser_id_b("user_id_b"));
		addfrnd.setUser_id_b(String.valueOf(userIdb));


		followz = friendDao.rejectorAcceptFriend(addfrnd);
		logger.info("service" + addfrnd);
		return followz;


	}



}


