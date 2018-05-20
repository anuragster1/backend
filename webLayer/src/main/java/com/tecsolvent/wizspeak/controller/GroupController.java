package com.tecsolvent.wizspeak.controller;

import com.tecsolvent.wizspeak.model.Group;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.service.GroupService;
import com.tecsolvent.wizspeak.utility.JsonConvert;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scala.util.parsing.combinator.testing.Str;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaison on 17/3/16.
 */
@Controller
public class GroupController {

	public static Logger logger = Logger.getLogger(GroupController.class);



	@Resource(name = "groupService")
	GroupService groupService;


	@RequestMapping(value = "/getGroupAbout/{groupCustomUrl}/{userId}",method = RequestMethod.GET)
	public
	@ResponseBody
	String getGroupAbout(@PathVariable String groupCustomUrl,@PathVariable long userId)throws Exception{

		Map<String,Object> group = groupService.getGroup(groupCustomUrl,userId);
		return JsonConvert.ObjJson(group);
	}



	@RequestMapping(value = "/getGroupWall/{groupCustomUrl}/{userId}/{lastId}",method = RequestMethod.GET)
	public
	@ResponseBody
	String getGroupWall(@PathVariable String groupCustomUrl,@PathVariable long userId,@PathVariable long lastId) throws Exception{

		Map<String ,Object> group = groupService.getGroupWall(groupCustomUrl,userId,1,lastId);
		return JsonConvert.ObjJson(group);
	}


	@RequestMapping(value = "/getGroupPagination/{groupCustomUrl}/{userStr}/{page}/{lastId}",method = RequestMethod.GET)
	public
	@ResponseBody
	String getGroupPagination(@PathVariable String groupCustomUrl,@PathVariable String userStr,@PathVariable int page,@PathVariable long lastId) throws Exception{

		ArrayList<Post> posts = groupService.getGroupWallPagination(groupCustomUrl,userStr,page,lastId);
		return JsonConvert.ObjJson(posts);
	}


	@RequestMapping(value = "/getGroupMedia/{groupCustomUrl}/{userId}/{postType}/{lastId}",method = RequestMethod.GET)
	public
	@ResponseBody
	String getGroupMedia(@PathVariable String groupCustomUrl,@PathVariable long userId,@PathVariable int postType,@PathVariable long lastId) throws Exception{

		Map<String ,Object> group = groupService.getGroupMedia(groupCustomUrl,userId,postType,1,lastId);
		return JsonConvert.ObjJson(group);
	}


	@RequestMapping(value = "/updatGroupName", method = RequestMethod.POST)
	public
	@ResponseBody
	String updatGroupName(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("group name = "+request.getParameter("name"));

		Map<String,String> status = new HashMap<>();

		status.put("status","0");

		if(groupService.updateGroupName(request.getParameter("name"),request.getParameter("id"))){
			status.put("status","1");

		}

		return JsonConvert.ObjJson(status);
	}



	@RequestMapping(value = "/updateGroupDesciption", method = RequestMethod.POST)
	public
	@ResponseBody
	String updateGroupDesciption(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("group name = "+request.getParameter("description"));

		Map<String,String> status = new HashMap<>();

		status.put("status","0");

		if(groupService.updateGroupDescription(request.getParameter("description"),request.getParameter("id"))){
			status.put("status","1");

		}

		return JsonConvert.ObjJson(status);
	}


	@RequestMapping(value = "/userGroupRole/{userId}/{groupCustomName}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserRoleGroup(@PathVariable long userId,@PathVariable String groupCustomName) throws Exception{

		logger.info("inside user group role finder");

		HashMap<String,Byte> state =  new HashMap<>();

		try {
			state = groupService.getUserRole(userId, groupCustomName);
		}catch (Exception e){

			logger.info("error in getting user role in group "+e);
		}

		return JsonConvert.ObjJson(state);
	}


	@RequestMapping(value = "/groupProfilePic/{picName}/{groupCustomName}", method = RequestMethod.GET)
	public
	@ResponseBody
	Integer groupProfilePic(@PathVariable  String picName,@PathVariable String groupCustomName)throws  Exception{

		logger.info("adding/update group profile pic "+picName+" group name "+groupCustomName);

		int gp = groupService.profilePic(picName,groupCustomName,2);

		return gp;
	}


	@RequestMapping(value = "/groupUserStatus/{groupStr}/{userStr}/{status}", method = RequestMethod.GET)
	public
	@ResponseBody
	String groupUserStatus(@PathVariable  String groupStr,@PathVariable String userStr,@PathVariable byte status)throws  Exception{

		logger.info("adding/update group user status "+groupStr+" group status "+status);

		HashMap<String, String>  gp = groupService.groupUserStatus(groupStr,userStr,status);

		return JsonConvert.ObjJson(gp);
	}


	@RequestMapping(value = "/inviteToGroup", method = RequestMethod.POST)
	public
	@ResponseBody
	String inviteToGroup(HttpServletRequest request,HttpServletResponse response)throws Exception{

		logger.info(" invite new users to grpoup");
		String user = request.getParameter("users");
		String userId = request.getParameter("userId");
		String groupId = request.getParameter("groupId");
		System.out.println(user);
		String[] ids = user.split(",");

		groupService.inviteUser(ids,userId,groupId);
		return null;

	}

	@RequestMapping(value = "/removeGroupInvitee", method = RequestMethod.POST)
	public
	@ResponseBody
	String removeGroupInvitee(HttpServletRequest request,HttpServletResponse response) throws Exception {

		logger.info("removing invitee from group relation table");
		String userRelationId = request.getParameter("URID");
		groupService.removeUser(userRelationId);
		return null;
	}


}
