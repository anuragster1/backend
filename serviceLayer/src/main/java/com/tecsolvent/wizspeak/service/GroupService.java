package com.tecsolvent.wizspeak.service;


import com.tecsolvent.wizspeak.GroupDao;
import com.tecsolvent.wizspeak.LikeDao;
import com.tecsolvent.wizspeak.UserDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.*;
import com.tecsolvent.wizspeak.utility.DateUtil;
import com.tecsolvent.wizspeak.utility.StringUtil;
import org.apache.log4j.Logger;


import java.util.*;

/**
 * Created by jaison on 13/3/16.
 */


public class GroupService  {

	public static Logger logger = Logger.getLogger(GroupService.class);
	private GroupDao groupDao;
	private UserDao userDao;
	private LikeDao likeDao;
	private FriendService friendService;
	private MentorService mentorService;
	private PostService postService;
	private CommentService commentService;
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;

	public void setGroupDao(GroupDao groupDao){ this.groupDao = groupDao; }

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setMentorService(MentorService mentorService) {
		this.mentorService = mentorService;
	}

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public void setLikeDao(LikeDao likeDao) {
		this.likeDao = likeDao;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}



	public Map<String,Object>  getGroup(String groupCustomUrl, long userId)throws Exception {

		logger.info("inside get group service");

		long groupId = 0;

		//get groupId
		try {
			groupId = userDao.getWallId(groupCustomUrl);
		}catch (Exception e){

			logger.info("error in fectching group id "+e);
			throw e;
		}

		Map<String,Object> jsonData = new HashMap<>();

		jsonData = getGropPageBasics(jsonData,userId,groupId);

		//getgroup members
		logger.info("getting members");
		ArrayList<GroupUser> groupMembers =  getGroupMembers(groupId,1);

		jsonData.put("groupMembers",groupMembers);

		//group join requests
		logger.info("getting members");
		ArrayList<GroupUser> groupMembersReq =  getGroupMembers(groupId,4);

		jsonData.put("groupMemberRequest",groupMembersReq);


		//get join invites
		logger.info("getting members invites ");
		ArrayList<GroupUser> groupMembersInvite =  getGroupMembers(groupId,5);
		jsonData.put("groupMemberInvite",groupMembersInvite);


		//get connected groups
		logger.info("getting connected groups");

		ArrayList<Group> connectedGroup = new ArrayList<>();

		try{
			connectedGroup = getConnectedGroups(groupId);

		}catch (Exception e){

		logger.info(" error in getting connected groups "+e);
		}

		jsonData.put("connectedGroups",connectedGroup);


		return jsonData;
	}


	public ArrayList<Post> getGroupWallPagination(String groupCustomName,String userStr,int page,long lastId)throws Exception{
		long groupId = 0;
		long userId = 0;
		//get groupId
		try {
			groupId = userDao.getWallId(groupCustomName);
			userId = userDao.getUserId(userStr);
		}catch (Exception e){

			logger.info("error in fectching group id or user id "+e);
			throw e;
		}

		Group group = groupDao.getGroup(groupId);
		ArrayList<Post> posts =  new ArrayList<>();
		try {
			int wallType = 2;
			posts = postService.getGroupWallPosts(groupId, wallType, group.getVertical_id(), 0, page, lastId);
		}catch (Exception e){
			logger.info("error in getting group wall post "+e);
		}

		Iterator postItr = posts.iterator();

		ArrayList<Post> newPosts =  new ArrayList<>();
		while (postItr.hasNext()){

			Post post = (Post) postItr.next();

			try{

				HashMap<Integer, Long> likes = likeDao.getLikes(post.getId(), 1);
				post.setLikes(likes.size());
				post.setiLikes(likeDao.checkMyLike(post.getId(),userId,(byte) 1));
			}catch (Exception e){
				logger.error("error in getting i like this post "+e);
				throw e;
			}

			try {
				ArrayList<Comment> cmts = commentService.getPostComments(post.getId(), post.getPostby_id());

				post.setComments(cmts);
				newPosts.add(post);
			}catch (Exception e){
				logger.info("error in getting comments "+e);
				throw e;
			}

		}

		return posts;


	}
	public Map<String,Object>getGroupWall(String groupCustomName,Long userId,int page,long lastId)throws Exception{

		long groupId = 0;
		//get groupId
		try {
			groupId = userDao.getWallId(groupCustomName);
		}catch (Exception e){

			logger.info("error in fectching group id "+e);
			throw e;
		}

		Map<String,Object> jsonData = new HashMap<>();

		jsonData = getGropPageBasics(jsonData,userId,groupId);

		//get group posts
		Group groupDetail  = (Group) jsonData.get("groupDetail");
		logger.info("get vertical id "+groupDetail.getVertical_id());


		return jsonData;
	}



	public Map<String,Object>getGroupMedia(String groupCustomName,Long userId,int mediaType,int page, long lastId)throws Exception{

		long groupId = 0;
		//get groupId
		try {
			groupId = userDao.getWallId(groupCustomName);
		}catch (Exception e){

			logger.info("error in fectching group id "+e);
			throw e;
		}

		Map<String,Object> jsonData = new HashMap<>();

		jsonData = getGropPageBasics(jsonData,userId,groupId);

		//get group posts images

		Group groupDetail  = (Group) jsonData.get("groupDetail");
		logger.info("get vertical id "+groupDetail.getVertical_id());

		try{
			ArrayList<Post> posts = postService.getGroupWallPosts(groupId,2,groupDetail.getVertical_id(),mediaType,page,lastId);
			jsonData.put("groupPosts",posts);
		}catch (Exception e){

			logger.info("erro in getting group posts "+e);
		}

		return jsonData;
	}



	public Map<String,Object> getGropPageBasics(Map<String,Object> jsonData,Long userId,Long groupId)throws Exception{

		//get user groups

		try {

			ArrayList<Group> userGroups = groupDao.getUserAmbitionGroups(userId);
			jsonData.put("userGroup",userGroups);

		}catch (Exception e){

			logger.info("error in fetching userGroups "+e);
		}

		//get user friends
		ArrayList<User> friends = new ArrayList<>();
		try {

			friends = friendService.getUserFriends(userId);

		}catch (Exception e){

			logger.info("error in fetching friends "+e);
		}

		jsonData.put("frindList",friends);

		//get mentor list

		ArrayList<User> mentors = new ArrayList<>();

		try {
			mentors = mentorService.getUserMentors(userId);

		}catch (Exception e){

			logger.info("errro in getting mentor list "+e);
		}
		jsonData.put("mentorList",mentors);

		//check user role
		int roleId = 0;
		ArrayList userRole = new ArrayList();
		try{

			HashMap<String, Byte> state = groupDao.getUserRole(userId,groupId);

			roleId = state.get("roleId");

		}catch (Exception e){

			logger.info(" error in getting user role ");
		}

		jsonData.put("userGroupRole",roleId);


		ArrayList userGroups = new ArrayList();
		try{
			userGroups = getUserGroups(userId);
		}catch (Exception e){
			logger.info("exc "+e);
			throw e;
		}


		jsonData.put("userGroups",userGroups);

		//get group details
		Group group = getGroupDetails(groupId);
		jsonData.put("groupDetail",group);

		return jsonData;
	}



	public ArrayList<User> getUserFriends(long userId){
		ArrayList<User> userArrayList = new ArrayList<>();

		return userArrayList;
	}



	public ArrayList getUserGroups(long user_id) throws Exception {

		//get ambition groups

		ArrayList groups = (ArrayList) guavaAndCouchbaseCache.getObjectFromCache("userGroupsd"+user_id,ArrayList.class);

		if(groups!= null){
			logger.info("user group list from cache = userGroups"+user_id);
			return groups;
		}

		logger.info(" xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx  fetching from mysql direct");

		ArrayList<Group> group = groupDao.getUserAmbitionGroups(user_id);

		Iterator<Group> itr = group.iterator();

		ArrayList<Group> aGroups = new ArrayList<Group>();
		ArrayList<Group> hGroups = new ArrayList<Group>();
		ArrayList<Group> tGroups = new ArrayList<Group>();

		while (itr.hasNext()){


			Group groupz = itr.next();
			try {
				groupz.setProfilePic(getGroupPic(groupz.getId(), 1));
			}catch (Exception e){
				logger.error("error in getting group pic"+e);
			}

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

	public Group getGroupDetails(long groupId)throws Exception{

		Group group = new Group();
		try {
			group = groupDao.getGroup(groupId);
		}catch (Exception e){

			logger.info("error fecthing group basics "+e);
		}

		group.setProfilePic(getGroupPic(groupId,1));
		group.setCoverPic(getGroupPic(groupId, 0));

		return group;
	}


	public String getGroupPic(long groupId,int picType) throws Exception {

		String profilePic = (String) guavaAndCouchbaseCache.getObjectFromCache("groupProfilePicx"+groupId,String.class);
		if(profilePic == null){

			int wallType= 2;
			profilePic = userDao.getUserPic(groupId, wallType ,picType);
			if(profilePic == null){ profilePic=" ";}
			guavaAndCouchbaseCache.putObjectAsByteInCache("groupProfilePic"+groupId, profilePic);
		}else{

			logger.info("feching from cache");
		}

		return profilePic;
	}



	public ArrayList<GroupUser> getGroupMembers(long groupId,int status) throws Exception{

		ArrayList<GroupUser> members = new ArrayList<>();

		try {
			// 1- for members ,4-requested,5- invited
			members = groupDao.getGroupMembers(groupId,status);
			logger.info("getting members list "+groupId);
		}catch (Exception e){

			logger.info("error in getting group members");
		}

		Iterator memberItr = members.iterator();
		ArrayList<GroupUser> groupMembers = new ArrayList<>();
		while (memberItr.hasNext()){

			GroupUser groupUser = (GroupUser) memberItr.next();

			long userId = groupUser.getUser_id();
			logger.info("in loop"+userId);
			User user = userDao.getUserName(userId);
			String uPic = userDao.getUserPic(userId,1,1);

			groupUser.setName(user.getFirst_name()+" "+user.getLast_name());
			groupUser.setUserPic(uPic);

			groupMembers.add(groupUser);
		}

		return groupMembers;
	}


	public ArrayList<Group> getConnectedGroups(long groupId) throws Exception{

		ArrayList<Group> connectedGroups = new ArrayList<>();

		try{
			// 1 - connected  0 -requetsted
			ArrayList<Long> groupIds = groupDao.getConnectedGroups(groupId,1);

			Iterator group = groupIds.iterator();
			while (group.hasNext()){

				long gId = (long) group.next();

				Group newGroup = groupDao.getGroup(gId);
				try {
					newGroup.setProfilePic(getGroupPic(gId, 1));
				}catch (Exception e){
					throw  e;
				}try{
				newGroup.setCoverPic(getGroupPic(gId,2));
				}catch (Exception e){

					throw e;
				}

				connectedGroups.add(newGroup);

			}

		}catch (Exception e){
			logger.info("error in getting connected groupId "+e);
		}

		return connectedGroups;
	}


	public void inviteUser(String[] users,String userId,String groupId)throws Exception{


		for(String a : users){

			System.out.println(" user id "+a);
			logger.info(" adding user to group = "+a);

			GroupUser gUser = new GroupUser();
			try{
				long id = groupDao.getGroupId(groupId);
				long Uid = userDao.getUserId(userId);
				long guid = userDao.getUserId(a);
				try {
					gUser.setUser_id(guid);
					gUser.setGroup_id(id);
					gUser.setInvitedby_id(userDao.getUserId(userId));
					gUser.setRole_alias("member");
					gUser.setStatus(5);
					gUser.setRole_id(2);
					gUser.setRolesetby_id(Uid);
				}catch (Exception e){
					logger.info("error in setting group user values "+e.getMessage());
				}

			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);

			gUser.setDate_invited(currentTime);

				logger.info("groupId "+gUser.getGroup_id()+" userid "+gUser.getInvitedby_id());
			}catch (Exception e){
				logger.error("errro in group usre "+e.getMessage());
			}

			groupDao.addGroupMember(gUser);

		}



	}

	public boolean updateGroupName(String name,String groupCustomName) throws Exception{

		long groupId = 0;
		//get groupId
		try {
			groupId = userDao.getWallId(groupCustomName);
		}catch (Exception e){

			logger.info("error in fectching group id "+e);
			throw e;
		}

		return  groupDao.updateGroupName(name,groupId);

	}


	public boolean updateGroupDescription(String description,String groupCustomName) throws Exception{

		long groupId = 0;
		//get groupId
		try {
			groupId = userDao.getWallId(groupCustomName);
		}catch (Exception e){

			logger.info("error in fectching group id "+e);
			throw e;
		}

		return  groupDao.updateGroupDescription(description,groupId);

	}


	public HashMap<String, Byte> getUserRole(long userId,String groupCustomUrl) throws Exception{
		logger.info("user role finder ");


		long groupId = 0;

		//get groupId
		try {
			groupId = userDao.getWallId(groupCustomUrl);
		}catch (Exception e){

			logger.info("error in fectching group id "+e);
			throw e;
		}
		return groupDao.getUserRole(userId,groupId);
	}


	public int profilePic(String picName, String groupCustomName,int wallType) throws Exception{

		logger.info(" add/update group profile pic");



		long groupId = 0;

		//get groupId
		try {
			if(wallType == 1 || wallType == 3){
				groupId = userDao.getUserId(groupCustomName);
				logger.info("user profile pic ");
				wallType  = 1;

			}
			if(wallType == 2) {
				logger.info("user group pic ");
				groupId = userDao.getWallId(groupCustomName);
			}

			logger.info("group id = "+groupId);
		}catch (Exception e){

			logger.info("error in fectching group id "+e);
			throw e;
		}




		int id = 0;

		int isAvatar = 1;
		int isActive = 1;

		try {
			//check old pic

			id = groupDao.checkPic(groupId,wallType,isAvatar,isActive);

		}catch (Exception e){
			//
			logger.info("no old profile pic found");
		}
logger.error("askjdfhJQKAF KHADSFHSADJFG   ddddddd  "+id);
		if(id > 0){

			isActive = 0;
			//update
			groupDao.removeProfilePic(groupId,wallType,isAvatar,isActive);

			logger.info("removed previ images");
		}

		int top =0, left =0;

		logger.info("adding to mysql new profile pic ");
		try {
			isActive = 1;
			groupDao.addProfilePic(groupId,wallType,isAvatar,isActive,picName,top,left);
		}catch (Exception e){

			logger.error("exception in addin gprofile pic ");

		}


		return 1;
	}

	public HashMap<String, String> groupUserStatus(String groupStr, String userStr, byte status) throws Exception{

		logger.info("inside group service ");

		long userId=0,groupId=0;

		try{
			userId = userDao.getUserId(userStr);
			groupId = groupDao.getGroupId(groupStr);
		}catch (Exception e){
			logger.info(" error in getting user or group id "+e);
		}

		byte role_id = 2;
		String roleAlias = "Member";
		String dateRequested = "1999-02-12 23:21:23";

		HashMap<String,String> result = new HashMap<>();

		result.put("success","0");
		result.put("status","0");
		result.put("text","");

		try {
			groupDao.userGroupRelation(groupId, userId, status, role_id, roleAlias, dateRequested);
		}catch (Exception e){
			logger.info("error in update user group relation "+e);
		}

		return result;
	}


	public int addCoverPic(String picName, String groupCustomName, int wallType) throws Exception {

		logger.info(" add/update addCoverPic ");


		long groupId = 0;

		//get groupId
		try {
			if (wallType == 1) {
				groupId = userDao.getUserId(groupCustomName);
				logger.info("user profile pic ");

			}
			if (wallType == 2) {
				logger.info("user group pic ");
				groupId = userDao.getWallId(groupCustomName);
			}

			logger.info("group id = " + groupId);
		} catch (Exception e) {

			logger.info("error in fectching group id " + e);
			throw e;
		}


		int id = 0;

		int isAvatar = 0;
		int isActive = 1;

		try {
			//check old pic

			id = groupDao.checkPic(groupId, wallType, isAvatar, isActive);

		} catch (Exception e) {
			//
			logger.info("no old  pic found");
		}
		logger.error("my id= " + id);
		if (id > 0) {

			isActive = 0;
			//update
			groupDao.removeProfilePic(groupId, wallType, isAvatar, isActive);

			logger.info("removed previ images");
		}

		int top = 0, left = 0;

		logger.info("adding to mysql new addCoverPic  ");
		try {
			isActive = 1;
			groupDao.addProfilePic(groupId, wallType, isAvatar, isActive, picName, top, left);
		} catch (Exception e) {

			logger.error("exception in  addCoverPic  ");

		}


		return 1;
	}


	public void removeUser(String UGRID)throws Exception{

		try{

			long userRelationid = Long.parseLong(UGRID);
			groupDao.removeUser(userRelationid);
		}catch (Exception e){

			logger.error("error in remove user "+e.getMessage());

		}
	}


}
