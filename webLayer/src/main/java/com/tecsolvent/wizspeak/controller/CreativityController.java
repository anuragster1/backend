package com.tecsolvent.wizspeak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.service.AmbitionService;
import com.tecsolvent.wizspeak.service.CreativityService;
import com.tecsolvent.wizspeak.service.GroupService;
import com.tecsolvent.wizspeak.utility.JsonConvert;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author sunil.kata
 * @since 15/01/16
 */

@Controller
public class CreativityController extends HttpServlet {

    public static Logger logger = Logger.getLogger(CreativityController.class);

    @Resource(name = "ambitionService")
    AmbitionService ambitionService;

    @Resource(name = "creativityService")
    CreativityService creativityService;

    @Resource(name = "groupService")
    GroupService groupService;


    @RequestMapping(value = "/getCreativityPosts/{user_id}/{vertical_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getCreativityPosts(@PathVariable String user_id, @PathVariable int vertical_id) throws Exception

    {

        ArrayList<Post> post = new ArrayList<>();
        post = creativityService.getCreativityPosts(user_id, vertical_id);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(post);


        return json;

    }


    @RequestMapping(value = "/getCreativityCatPost/{user_id}/{postcat_id}/{post_type}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getCreativityCatPost(@PathVariable String user_id, @PathVariable int postcat_id, @PathVariable int post_type) throws Exception

    {

        ArrayList<Post> post = new ArrayList<>();
        post = creativityService.getCreativityCatPost(user_id, postcat_id, post_type);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(post);


        return json;

    }


    @RequestMapping(value = "/creativityPlayer/{file_id}/{vertical_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String creativityPlayer(@PathVariable long file_id, @PathVariable int vertical_id) throws Exception

    {

        ArrayList<Post> post = new ArrayList<>();
        post = creativityService.creativityPlayer(file_id, vertical_id);


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(post);


        return json;

    }


    @RequestMapping(value = "/addCreativityPost", method = RequestMethod.POST)
    public
    @ResponseBody
    String addCreativityPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("add creee ");
        Post crepost = new Post();
        try {

            crepost.setPostby_id(Long.parseLong(request.getParameter("postby_id")));
            crepost.setTitle(request.getParameter("title"));
            crepost.setPost_type_id(Integer.parseInt(request.getParameter("post_type_id")));
            crepost.setStatus(Integer.parseInt(request.getParameter("status")));
            crepost.setWall_type(request.getParameter("wall_type"));
            crepost.setPostto_id(Long.parseLong(request.getParameter("postto_id")));
            crepost.setLink(request.getParameter("link"));

        } catch (Exception e) {

            logger.error("erorr no data" + e);
        }


        Map<String, Object> status = new HashMap<>();
        status = creativityService.addCreativityPost(crepost);


        return JsonConvert.ObjJson(status);
    }


	@RequestMapping(value="/updateCreVideo", method = RequestMethod.POST)
	public
	@ResponseBody
	String updateCreVideo(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Post update = new Post();
		update.setId(Long.parseLong(request.getParameter("id")));
		update.setLink( request.getParameter("ylink"));
		Map<String ,Object> status  = new HashMap<>();
		status =creativityService.updateCreVideo(update);


		return JsonConvert.ObjJson(status);


	}

	@RequestMapping(value="/updateStatusVideo", method = RequestMethod.POST)
	public
	@ResponseBody
	String updateStatusVideo(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Post update = new Post();

		logger.info(request.getParameter("id"));

		update.setId(Long.parseLong(request.getParameter("id")));
		Map<String ,Object> status  = new HashMap<>();
		status =creativityService.updateStatusVideo(update);


		return JsonConvert.ObjJson(status);
	}



}
