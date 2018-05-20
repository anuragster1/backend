package com.tecsolvent.wizspeak.controller;

import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.service.AmbitionService;
import com.tecsolvent.wizspeak.service.PostService;
import com.tecsolvent.wizspeak.service.ProfileService;
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
 * Created by jaison on 6/4/16.
 */

@Controller
public class PostController extends HttpServlet {


	public static Logger logger = Logger.getLogger(PostController.class);

	@Resource(name = "profileService")
	ProfileService profileService;








}
