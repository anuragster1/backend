package com.tecsolvent.wizspeak.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tecsolvent.wizspeak.elasticsearch.ESContentIndexer;

import com.tecsolvent.wizspeak.model.Award;
import com.tecsolvent.wizspeak.model.Category;
import com.tecsolvent.wizspeak.model.Certification;
import com.tecsolvent.wizspeak.model.Comment;
import com.tecsolvent.wizspeak.model.Experience;
import com.tecsolvent.wizspeak.model.FriendRequest;
import com.tecsolvent.wizspeak.model.Group;
import com.tecsolvent.wizspeak.model.Mentor;
import com.tecsolvent.wizspeak.model.MentorFollow;
import com.tecsolvent.wizspeak.model.MentorRate;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.model.SubCategory;
import com.tecsolvent.wizspeak.model.User;
import com.tecsolvent.wizspeak.model.UserEducation;
import com.tecsolvent.wizspeak.notification.dao.Notification;
import com.tecsolvent.wizspeak.notification.dao.NotificationResponse;
import com.tecsolvent.wizspeak.notification.services.ViewNotificationService;

import com.tecsolvent.wizspeak.service.AmbitionService;
import com.tecsolvent.wizspeak.service.GroupService;
import com.tecsolvent.wizspeak.utility.JsonConvert;

/**
 * @author sunil.kata
 * @since 15/01/16
 */

@Controller
public class AmbitionController extends HttpServlet{

	public static Logger logger = Logger.getLogger(AmbitionController.class);

	@Resource(name = "ambitionService")
	AmbitionService ambitionService;

	@Resource(name = "groupService")
	GroupService groupService;

	@Resource(name = "viewNotificationService")
	ViewNotificationService vns;

	@RequestMapping(value = "/ambitionPage/{userId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String ambitionPage(@PathVariable long userId) throws Exception {

		Map<String,Object> json = ambitionService.getPageJson(userId);

		return JsonConvert.ObjJson(json);
	}



	// API to test service call
	@RequestMapping(value = "/userDetails/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String userDetails(@PathVariable long user_id) throws Exception {


		User user = ambitionService.userDetails(user_id);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(user);

		return json;
	}

	@RequestMapping(value = "/getUserGroups/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserGroups(@PathVariable long user_id) throws Exception
	{

		ArrayList<Object> g = new ArrayList<>();
		g = groupService.getUserGroups(user_id);


		return JsonConvert.ObjJson(g);


	}

	@RequestMapping(value = "/notifications/{notification_id}", method = RequestMethod.PUT)
	public
	@ResponseBody
	String updateNotification(@PathVariable String notification_id){
		try{
			vns.updateStatus(notification_id, Notification.Status.READ);
		}catch(Exception e){
			System.err.println("Exception - " + e);
		}
		return "";
	}

	@RequestMapping(value = "/notifications/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getNotifications(@PathVariable long user_id) throws Exception
	{
		List<Notification> notifications = vns.get(user_id);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Notification> ambitions = new ArrayList<Notification>();
		List<Notification> hobbies = new ArrayList<Notification>();
		List<Notification> team = new ArrayList<Notification>();
		
		if(notifications != null){
			for (Notification notification : notifications) {
				switch(notification.getCategory()) {
				case AMBITION:
					ambitions.add(notification);
					break;
				case HOBBIES:
					hobbies.add(notification);
					break;
				case TEAMS:
					team.add(notification);
					break;
				default:
					break;
				
				}
			}
		}
		
		NotificationResponse ambitionResponse = new NotificationResponse(ambitions.size(), ambitions);
		NotificationResponse hobbiesResponse = new NotificationResponse(hobbies.size(), hobbies);
		NotificationResponse teamResponse = new NotificationResponse(team.size(), team);
		
		result.put("count", notifications.size());
		result.put("Ambition", ambitionResponse);
		result.put("Hobbies", hobbiesResponse);
		result.put("Team", teamResponse);
		
		
		return JsonConvert.ObjJson(result);
	}

	@RequestMapping(value = "/getUserFriends/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserFriends(@PathVariable String user_id) throws Exception
	{

		ArrayList<User> user = new ArrayList<User>();
		user = ambitionService.getUserFriends(user_id);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(user);


		return json;

	}


	@RequestMapping(value = "/getUserMentors/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserMentors(@PathVariable long user_id) throws Exception
	{
		logger.info("getting mentor details");
		ArrayList<User> user = new ArrayList<User>();

		user = ambitionService.getUserMentors(user_id);


		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(user);


		return json;

	}



	@RequestMapping(value = "/getMentorPost/{user_id}/{vertical_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getMentorPosts(@PathVariable long user_id,@PathVariable int vertical_id) throws Exception
	{

		ArrayList<Post> post = new ArrayList<>();
		post = ambitionService.getMentorPosts(user_id,vertical_id);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(post);


		return json;

	}

	@RequestMapping(value= "/getPostComments/{post_id}/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getPostComments(@PathVariable long post_id,@PathVariable long user_id) throws  Exception
	{

		logger.info("in conmtroller page id "+post_id);

		ArrayList<Comment> comments = new ArrayList<Comment>();

		comments = ambitionService.getPostComments(post_id,user_id);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(comments);

		return json;

	}

	@RequestMapping(value="/getAmbitionCategories", method =RequestMethod.GET)
	public
	@ResponseBody
	String getAmbitionCategories() throws Exception
	{

		ArrayList<Category> categories  = new ArrayList<>();
		categories = ambitionService.getCategories(1);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(categories);


		return json;
	}


	@RequestMapping(value="/getAmbitionSubCategories", method =RequestMethod.GET)
	public
	@ResponseBody
	String getAmbitionSubCategories() throws Exception
	{

		ArrayList<SubCategory> subCategories  = new ArrayList<>();
		subCategories = ambitionService.getSubCategories();

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(subCategories);


		return json;
	}



	@RequestMapping(value="/addPost", method =RequestMethod.POST)
	public
	@ResponseBody
	String addPost(HttpServletRequest request, HttpServletResponse response) throws Exception {


		logger.info("titlte = "+request.getParameter("title"));
		Post post = new Post();
		try {


			post = new Post(
					Integer.parseInt(request.getParameter("status")),
					Long.parseLong(request.getParameter("postby_id")),
					Long.parseLong(request.getParameter("postto_id")),
					request.getParameter("title"),
					request.getParameter("description"),
					Integer.parseInt(request.getParameter("post_type_id")),
					Integer.parseInt(request.getParameter("vertical_id")),
					request.getParameter("wall_type"),
					Byte.parseByte(request.getParameter("is_private")),
					request.getParameter("link")
			);
		}catch (Exception e){

			logger.info("not matching input "+e);
		}
		try {
			post = new Post(
					Integer.parseInt(request.getParameter("status")),
					request.getParameter("postby_id"),
					request.getParameter("postto_id"),
					request.getParameter("title"),
					request.getParameter("description"),
					Integer.parseInt(request.getParameter("post_type_id")),
					Integer.parseInt(request.getParameter("vertical_id")),
					request.getParameter("wall_type"),
					Byte.parseByte(request.getParameter("is_private")),
					request.getParameter("link")
			);
		}catch (Exception e){

			logger.info("not matching input "+e);
		}

		logger.info(" erooooooooooooooooooooooooooo");
		logger.info(post.toString());

		logger.info("going to ambition service");
		HashMap<String, String> s = ambitionService.addPost(post);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(s);
		logger.info(json+" in controller");
		return json;



	}


	@RequestMapping(value = "/getPost/{ids}/{userId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getPost(@PathVariable String ids,@PathVariable long userId) throws Exception
	{
		String[] ary = ids.split("-");
		logger.info("fetching post from ids " +ids);
		ArrayList<Long> postIds = new ArrayList<>();

		for (int i = 0; i < ary.length; i++)
		{
			postIds.add(Long.parseLong(ary[i]));
		}
		ArrayList<Post> post = new ArrayList<>();
		try {
			post = ambitionService.getPost(postIds,userId);

			logger.info(post.toString());

		}catch (Exception e){
			logger.error("error in getting post"+e);


		}


		return JsonConvert.ObjJson(post);


	}


	@RequestMapping(value="/addLike", method = RequestMethod.POST)
	public
	@ResponseBody
	String addLike(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Map<String, Object> like = new HashMap<String, Object>();

		logger.info("controller  item_type"+request.getParameter("item_type"));
		like.put("item_id",Long.parseLong(request.getParameter("item_id")));
		like.put("post_owner_id",Long.parseLong(request.getParameter("post_owner_id")));
		like.put("item_type",Integer.parseInt(request.getParameter("item_type")));
		like.put("user_id",Long.parseLong(request.getParameter("user_id")));
		Map <String,String> result= ambitionService.checkLike(like);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(result);
		return json;
	}


	@RequestMapping(value="/removeLike", method = RequestMethod.POST)
	public
	@ResponseBody
	String removeLike(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Map<String, Object> like = new HashMap<String, Object>();

		logger.info("controller");
		like.put("item_id",Long.parseLong(request.getParameter("item_id")));
		like.put("item_type",Integer.parseInt(request.getParameter("item_type")));
		like.put("user_id",Long.parseLong(request.getParameter("user_id")));
		like.put("status",0);

		Map <String,String> result= ambitionService.removeLike(like);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(result);
		return json;
	}


	@RequestMapping(value="/addComment", method = RequestMethod.POST)
	public
	@ResponseBody
	String addComment(HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		Comment comment = new Comment();
		comment.setComment(request.getParameter("comment"));
		comment.setCommenter_id(Long.parseLong(request.getParameter("user_id")));
		comment.setStatus(Integer.parseInt(request.getParameter("status")));
		comment.setCommentedDate(request.getParameter("date_commented"));
		comment.setPost_id(Long.parseLong(request.getParameter("post_id")));
		Map <String,Object> result = new HashMap<>();
		try{

			result = ambitionService.addComment(comment);
		}catch (Exception e){
			logger.info("error adding comment"+e);
			throw e;

		}

		User user = ambitionService.userDetails(Long.parseLong(request.getParameter("user_id")));
		result.put("name",user.getFirst_name()+" "+user.getLast_name());
		result.put("ProfilePic",user.getProfilePic());


		return JsonConvert.ObjJson(result);
	}


	@RequestMapping(value="/updatePostText", method = RequestMethod.POST)
	public
	@ResponseBody
	String updatePostText(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Post post = new Post();
		post.setTitle(request.getParameter("post_title"));
		post.setDescription(request.getParameter("post_description"));
		post.setPostby_id(Long.parseLong(request.getParameter("user_id")));
		post.setId(Long.parseLong(request.getParameter("post_id")));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.updatePostText(post);

		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value="/deletePost", method = RequestMethod.POST)
	public
	@ResponseBody
	String deletePost(HttpServletRequest request,HttpServletResponse response) throws Exception{

		Map<String,Object> post =new HashMap<>();
		post.put("post_id",Long.parseLong(request.getParameter("post_id")));
		post.put("user_id",Long.parseLong(request.getParameter("user_id")));
		post.put("status",0);
		Map<String,Object> status = ambitionService.deletePost(post);

		return JsonConvert.ObjJson(status);

	}

	@RequestMapping(value="/createGroup", method = RequestMethod.POST)
	public
	@ResponseBody
	boolean createGroup(HttpServletRequest request,HttpServletResponse response) throws Exception{


		Group group = new Group();
		group.setCreatedby_id(Long.parseLong(request.getParameter("createdby_id")));
		group.setDate_created(request.getParameter("date_created"));
		group.setName(request.getParameter("name"));
		group.setDescription(request.getParameter("description"));
		group.setInvites(request.getParameter("invitedId"));
		group.setStatus(Integer.parseInt(request.getParameter("status")));
		group.setType(Integer.parseInt(request.getParameter("type")));
		group.setSub_category_id(Integer.parseInt(request.getParameter("sub_category_id")));

		logger.info(" inside create group ");

		boolean status = ambitionService.createGroup(group);

		return status;

	}


	@RequestMapping(value = "/getMoreComments",method = RequestMethod.POST)
	public
	@ResponseBody
	String getMoreComments(HttpServletRequest request,HttpServletResponse response) throws Exception{

		HashMap<String,Long> comment = new HashMap<>();
		comment.put("lastCommentId",Long.parseLong(request.getParameter("id")));
		comment.put("postId",Long.parseLong(request.getParameter("post_id")));
		comment.put("userId",Long.parseLong(request.getParameter("user_id")));
		ArrayList<Comment> comments = ambitionService.getMoreComments(comment);

		return JsonConvert.ObjJson(comments);
	}



	@RequestMapping(value = "/getAmbitionHomePost/{userId}/{page}/{lastId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getAmbitionPost(@PathVariable long userId,@PathVariable int page,@PathVariable long lastId) throws Exception {
		ArrayList<Post> posts = new ArrayList<>();
		try {
			logger.info("user id is "+userId +"page is "+page);
			posts =  ambitionService.getAmbitionPosts(userId,1,page,lastId);
		}catch (Exception e){

			logger.info("error in getting pagination posts xxxxxxxxxxxxxxxxxx   xxxxxxxxxxxxxxxxx testing "+e);
			throw e;

		}

		return JsonConvert.ObjJson(posts);
	}






	@RequestMapping(value = "/userEducation/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public
	String getUserProfileEducation(@PathVariable String userId) throws Exception {

		logger.info("error in getting pagination posts xxxxxxxxxxxxxxxxxx   xxxxxxxxxxxxxxxxx testing ");


		List<UserEducation> UserEducationDataMapper = (List<UserEducation>) ambitionService.getUserProfileEducation(userId);
		ObjectWriter usereducation = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String juseredcation = usereducation.writeValueAsString(UserEducationDataMapper);
		String newout = juseredcation.replace("NULL", "empty");

		return newout;

	}


	@RequestMapping(value = "/updateUserEducation", method = RequestMethod.POST)
	@ResponseBody
	public
	String updateUserEducation(@RequestParam("education") String education, @RequestParam("institute") String institute, @RequestParam("university") String university, @RequestParam("date_from") String date_from, @RequestParam("date_to") String date_to, @RequestParam("id") long id ) throws Exception {


		UserEducation updateObject = new UserEducation( education, institute, university, date_from, date_to, id);

		logger.info("file new data - " + education+institute+university+date_from+date_to+id);
		ambitionService.updateUserEducation(updateObject);


		return "You entered " + education;
	}

	@RequestMapping(value="/addMentorPost", method =RequestMethod.POST)
	public
	@ResponseBody
	String addMentorPost(HttpServletRequest request, HttpServletResponse response) throws Exception {


		logger.info("titlte = "+request.getParameter("title"));

		Post post =new  Post(
				Integer.parseInt(request.getParameter("status")),
				Long.parseLong(request.getParameter("postby_id")),
				Long.parseLong(request.getParameter("postto_id")),
				request.getParameter("title"),
				request.getParameter("description"),
				Integer.parseInt(request.getParameter("post_type_id")),
				Integer.parseInt(request.getParameter("vertical_id")),
				request.getParameter("wall_type"),
				Byte.parseByte(request.getParameter("isPrivate")),
				request.getParameter("link")
		);

		HashMap<String, String> s = ambitionService.addMentorPost(post);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(s);
		logger.info(json+" in controller");
		return json;


	}


	@RequestMapping(value = "/getMentorFollowersId/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getMentorFollowerId(@PathVariable long user_id) throws Exception
	{

		ArrayList<Long> user = new ArrayList<Long>();
		user = ambitionService.getMentorFollowerId(user_id);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(user);


		return json;

	}




	@RequestMapping(value="/userStatus", method = RequestMethod.POST)
	public
	@ResponseBody
	String mentorStatus(HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		User user = new User();
		user.setUserId(request.getParameter("user_id"));
		user.setProfileStatus(request.getParameter("status"));
		user.setMentor(Byte.parseByte(request.getParameter("is_mentor")));

		logger.info("edit / addd status");
		logger.info(user.toString());
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.mentorStatus(user);

		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/editMentorExperience", method = RequestMethod.POST)
	public
	@ResponseBody
	String mentorExperience(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Mentor post = new Mentor();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setCompany(request.getParameter("company"));
		post.setJobtitle(request.getParameter("jobtitle"));
		post.setDate_from(request.getParameter("date_from"));
		post.setDate_to(request.getParameter("date_to"));
		post.setId(Integer.parseInt(request.getParameter("id")));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.editMentorExperience(post);


		logger.info((request.getParameter("date_from")));

		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/addMentorExperience", method = RequestMethod.POST)
	public
	@ResponseBody
	String addMentorExperience(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Mentor post = new Mentor();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setCompany(request.getParameter("company"));
		post.setJobtitle(request.getParameter("jobtitle"));
		post.setDate_from(request.getParameter("date_from"));
		post.setDate_to(request.getParameter("date_to"));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.addMentorExperience(post);


		logger.info((request.getParameter("user_id")));
		logger.info((request.getParameter("company")));
		logger.info((request.getParameter("jobtitle")));
		logger.info((request.getParameter("date_from")));
		logger.info((request.getParameter("date_to")));

		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/deleteMentorExperience", method = RequestMethod.POST)
	public
	@ResponseBody
	String deleteMentorExperience(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Mentor post = new Mentor();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setId(Integer.parseInt(request.getParameter("id")));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.deleteMentorExperience(post);


		logger.info((request.getParameter("date_from")));

		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value = {"/getUserExperience/{userIdStr}"}, method = RequestMethod.GET)

	public

	@ResponseBody
	String getUserExperience(@PathVariable String userIdStr) throws Exception {


		List<Experience> UserEducationDataMapper = (List<Experience>) ambitionService.getUserExperience(userIdStr);
		ObjectWriter usereducation = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String juseredcation = usereducation.writeValueAsString(UserEducationDataMapper);
		String newout = juseredcation.replace("NULL", "empty");

		return newout;

	}

	@RequestMapping(value = {"/getAward/{user_id}"}, method = RequestMethod.GET)

	public

	@ResponseBody
	String getAward(@PathVariable String user_id) throws Exception {


		List<Award> UserawardMapper = (List<Award>) ambitionService.getAward(user_id);
		ObjectWriter usereducation = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String jaward = usereducation.writeValueAsString(UserawardMapper);
		String newout = jaward.replace("NULL", "empty");

		return newout;

	}

	@RequestMapping(value = {"/getCert/{user_id}"}, method = RequestMethod.GET)

	public

	@ResponseBody
	String getCert(@PathVariable String  user_id) throws Exception {


		List<Certification> UserawardMapper = (List<Certification>) ambitionService.getCert(user_id);
		ObjectWriter usereducation = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String jaward = usereducation.writeValueAsString(UserawardMapper);
		String newout = jaward.replace("NULL", "empty");

		return newout;

	}


	@RequestMapping(value="/addFollow", method = RequestMethod.POST)
	public
	@ResponseBody
	String addFollow(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		MentorFollow follow = new MentorFollow();
		follow.setUser_id(request.getParameter("user_id"));
		follow.setMentor_id( request.getParameter("mentor_id"));
		Map<String ,Object> status  = new HashMap<>();

		logger.info((request.getParameter("addFo llow controller")));
		status = ambitionService.addFollow(follow);


		logger.info((request.getParameter("mentor_id")));
		logger.info((request.getParameter("user_id")));


		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/unFollow", method = RequestMethod.POST)
	public
	@ResponseBody
	String unFollow(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		MentorFollow unfollow = new MentorFollow();
		unfollow.setUser_id(request.getParameter("user_id"));
		unfollow.setMentor_id( request.getParameter("mentor_id"));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.unFollow(unfollow);


		logger.info((request.getParameter("mentor_id")));
		logger.info((request.getParameter("user_id")));


		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/reFollow", method = RequestMethod.POST)
	public
	@ResponseBody
	String reFollow(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		logger.info(" reFollow controller");
		MentorFollow unfollow = new MentorFollow();
		unfollow.setUser_id(request.getParameter("user_id"));
		unfollow.setMentor_id( request.getParameter("mentor_id"));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.reFollow(unfollow);


		logger.info((request.getParameter("mentor_id")));
		logger.info((request.getParameter("user_id")));


		return JsonConvert.ObjJson(status);
	}




	@RequestMapping(value="/addAward", method = RequestMethod.POST)
	public
	@ResponseBody
	String addAward(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Award post = new Award();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setAward(request.getParameter("award"));
		post.setAuthority(request.getParameter("authority"));
		post.setDate_awarded(request.getParameter("date_awarded"));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.addAward(post);



		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/editAward", method = RequestMethod.POST)
	public
	@ResponseBody
	String editAward(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Award post = new Award();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setAward(request.getParameter("award"));
		post.setAuthority(request.getParameter("authority"));
		post.setDate_awarded(request.getParameter("date_awarded"));
		post.setId(Integer.parseInt(request.getParameter("id")));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.editAward(post);

		logger.info((request.getParameter("user_id")));
		logger.info((request.getParameter("award")));
		logger.info((request.getParameter("authority")));
		logger.info((request.getParameter("date_awarded")));


		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value="/deleteAward", method = RequestMethod.POST)
	public
	@ResponseBody
	String deleteAward(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Award post = new Award();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setId(Integer.parseInt(request.getParameter("id")));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.deleteAward(post);


		logger.info((request.getParameter("user_id")));
		logger.info((request.getParameter("award")));
		logger.info((request.getParameter("authority")));
		logger.info((request.getParameter("date_awarded")));

		return JsonConvert.ObjJson(status);
	}





	@RequestMapping(value="/addCert", method = RequestMethod.POST)
	public
	@ResponseBody
	String addCert(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Certification post = new Certification();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setAuthority(request.getParameter("authority"));
		post.setCertification(request.getParameter("certification"));
		post.setDate_certified(request.getParameter("date_certified"));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.addCert(post);


		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value="/editCert", method = RequestMethod.POST)
	public
	@ResponseBody
	String editCert(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Certification post = new Certification();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setId(Integer.parseInt(request.getParameter("id")));
		post.setAuthority(request.getParameter("authority"));
		post.setCertification(request.getParameter("certification"));
		post.setDate_certified(request.getParameter("date_certified"));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.editCert(post);


		return JsonConvert.ObjJson(status);
	}



	@RequestMapping(value="/deleteCert", method = RequestMethod.POST)
	public
	@ResponseBody
	String deleteCert(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Certification post = new Certification();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setId(Integer.parseInt(request.getParameter("id")));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.deleteCert(post);


		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value="/addMentorEducation", method = RequestMethod.POST)
	public
	@ResponseBody
	String addMentorEducation(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		UserEducation post = new UserEducation();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setEducation(request.getParameter("education"));
		post.setInstitute(request.getParameter("institute"));
		post.setUniversity(request.getParameter("university"));
		post.setDate_to(request.getParameter("date_to"));
		post.setDate_from(request.getParameter("date_from"));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.addMentorEducation(post);



		return JsonConvert.ObjJson(status);
	}



	@RequestMapping(value="/editMentorEducation", method = RequestMethod.POST)
	public
	@ResponseBody
	String editMentorEducation(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		UserEducation post = new UserEducation();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setId(Integer.parseInt(request.getParameter("id")));
		post.setEducation(request.getParameter("education"));
		post.setInstitute(request.getParameter("institute"));
		post.setUniversity(request.getParameter("university"));
		post.setDate_to(request.getParameter("date_to"));
		post.setDate_from(request.getParameter("date_from"));

		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.editMentorEducation(post);



		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/deleteMentorEducation", method = RequestMethod.POST)
	public
	@ResponseBody
	String deleteMentorEducation(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		UserEducation post = new UserEducation();
		post.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		post.setId(Integer.parseInt(request.getParameter("id")));


		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.deleteMentorEducation(post);



		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value = "/checkMentorRating/{user_id}", method = RequestMethod.GET)
	public

	@ResponseBody
	String checkMentorRating(@PathVariable int user_id) throws Exception {


		List<MentorRate> UserawardMapper = (List<MentorRate>) ambitionService.checkMentorRating(user_id);
		ObjectWriter usereducation = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String jaward = usereducation.writeValueAsString(UserawardMapper);
		String newout = jaward.replace("NULL", "empty");

		return newout;

	}

	@RequestMapping(value="/addRating", method = RequestMethod.POST)
	public
	@ResponseBody
	String addRating(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		MentorRate follow = new MentorRate();
		follow.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		follow.setMentor_id (Integer.parseInt(request.getParameter("mentor_id")));
		follow.setRating (request.getParameter("rating"));
		follow.setDate_rated (request.getParameter("date_rated"));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.addRating(follow);

		logger.info((request.getParameter("rating")));


		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value="/reRating", method = RequestMethod.POST)
	public
	@ResponseBody
	String reRating(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		MentorRate follow = new MentorRate();
		follow.setUser_id(Integer.parseInt(request.getParameter("user_id")));
		follow.setMentor_id (Integer.parseInt(request.getParameter("mentor_id")));
		follow.setRating (request.getParameter("rating"));
		follow.setDate_rated (request.getParameter("date_rated"));
		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.reRating(follow);

		logger.info((request.getParameter("rating")));


		return JsonConvert.ObjJson(status);
	}

//*********************friend or not check*****************************//


	@RequestMapping(value = "/rejectorAcceptFriend", method = RequestMethod.POST)
	public
	@ResponseBody
	String rejectFriend(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FriendRequest addfrnd = new FriendRequest();


		addfrnd.setRequest_status(request.getParameter("user_id_a"));
		addfrnd.setId(Integer.parseInt(request.getParameter("id")));


		addfrnd.setUser_id_b(request.getParameter("user_id_b"));

		addfrnd.setUser_id_a(request.getParameter("request_status"));


		Map<String, Object> status = new HashMap<>();
		status = ambitionService.rejectorAcceptFriend(addfrnd);


		logger.info((request.getParameter("user_id_a")));
		logger.info((request.getParameter("user_id_b")));


		return JsonConvert.ObjJson(status);
	}





	@RequestMapping(value = "/addFriendd", method = RequestMethod.POST)
	public
	@ResponseBody
	String addFriend(HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		FriendRequest addfrnd = new FriendRequest();


		addfrnd.setRequest_status(request.getParameter("user_id_a"));


		addfrnd.setUser_id_b(request.getParameter("user_id_b"));




		Map<String ,Object> status  = new HashMap<>();
		status = ambitionService.addFriend(addfrnd);


		logger.info((request.getParameter("user_id_a")));
		logger.info((request.getParameter("user_id_b")));


		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value = "/cancelFriendd", method = RequestMethod.POST)
	public
	@ResponseBody
	String cancelFriend(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FriendRequest addfrnd = new FriendRequest();


		addfrnd.setRequest_status(request.getParameter("user_id_a"));


		addfrnd.setUser_id_b(request.getParameter("user_id_b"));

		addfrnd.setId(Integer.parseInt(request.getParameter("id")));


		Map<String, Object> status = new HashMap<>();
		status = ambitionService.cancelFriend(addfrnd);


		logger.info((request.getParameter("user_id_a")));
		logger.info((request.getParameter("user_id_b")));


		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value = "/es/{type}/{id}", method = RequestMethod.GET)
	public

	@ResponseBody
	String elasticsearch(@PathVariable String type, @PathVariable String id) throws Exception {



		ESContentIndexer esContentIndexer = ESContentIndexer.getInstance();

		try {
			esContentIndexer.get(type, id);
			logger.info(type + "id=" + id);
		}catch (Exception e){
			logger.error("error in elastic searc "+e);
		}

		return JsonConvert.ObjJson(esContentIndexer);
	}


}
