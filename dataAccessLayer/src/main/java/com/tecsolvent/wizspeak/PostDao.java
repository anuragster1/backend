package com.tecsolvent.wizspeak;

import com.google.common.collect.Maps;
import com.tecsolvent.wizspeak.model.Post;
import org.apache.log4j.Logger;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 *
 * Created by jaison on 16/3/16.
 */
public class PostDao {

	public static Logger logger = Logger.getLogger(PostDao.class);

	private JdbcTemplate masterJdbcTemplate;
	private JdbcTemplate slaveJdbcTemplate;


	public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
		this.masterJdbcTemplate = masterJdbcTemplate;
	}

	public void setSlaveJdbcTemplate(JdbcTemplate slaveJdbcTemplate) {
		this.slaveJdbcTemplate = slaveJdbcTemplate;
	}




	public ArrayList<Post> getAmbitionPosts(long user_id,int verticalId) throws Exception{

		ArrayList<Post> posts = new ArrayList<Post>();
		try{
			logger.error(" no errots  ");
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,date_posted,is_private FROM posts WHERE postto_id = "+user_id+" AND wall_type = "+verticalId+" AND vertical_id = 1 AND status = 1  ORDER BY id DESC limit 10 ", new PostMapper());
		}catch (Exception e){

			logger.error(" error getting ambition posts  "+e);

		}
		return posts;

	}


	public ArrayList<Post> getPosts(ArrayList<Long> userIds,ArrayList<Long> groupIds,ArrayList<Long> mentorIds ,int isPrivate,int postType,int page,long lastId) throws Exception{

		ArrayList<Post> posts = new ArrayList<Post>();

		String condition = "";
		logger.info("getPosts page no "+page);

		String userStr ="";
		String mentorStr ="";
		String groupStr = "";

		if(!userIds.isEmpty()){

			Iterator userItr = userIds.iterator();
			String init = "(";
			while(userItr.hasNext()){
				init += userItr.next()+",";

			}
			int size = init.length();
			userStr = init.substring(0, size-1);
			userStr +=")";

			//condition for user
			condition = condition+" (postto_id IN "+userStr+" AND wall_type = 1) OR ";
		}else{
			userStr = "(null)";
		}

		if(!groupIds.isEmpty()){

			Iterator groupItr = groupIds.iterator();
			String gIds = "(";
			while(groupItr.hasNext()){
				gIds += groupItr.next()+",";

			}
			int size = gIds.length();
			groupStr = gIds.substring(0, size-1);
			groupStr +=")";

		}else {

			groupStr= "(null)";
		}

		if(!mentorIds.isEmpty()){
			Iterator mentorItr = mentorIds.iterator();
			String mIds = "(";
			while(mentorItr.hasNext()){
				mIds += mentorItr.next()+",";

			}
			int size = mIds.length();
			mentorStr = mIds.substring(0, size-1);
			mentorStr +=")";

		}else {
			mentorStr = "(null)";
		}

		String last = "";
		if(lastId > 0){
			last = " AND id > "+lastId;
		}

		String mentorCond = " (postto_id IN "+mentorStr+" AND wall_type = 3 AND is_private = 0 ) ";

		//condition for user
		condition = condition+" (postto_id IN "+groupStr+" AND wall_type = 2 AND is_private = "+isPrivate+") OR "+mentorCond;

		if(postType>0){

			condition = condition+" AND post_type_id = "+postType;
		}

		String limit = " limit "+(((page*10) )-10)+","+10;


		try{
			logger.error("SELECT id,postby_id, postto_id, title, description, link, post_type_id,is_private FROM posts WHERE ("+condition+" ) "+last+" AND status = 1 ORDER BY id DESC "+limit);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE ("+condition+" ) "+last+" AND status = 1 ORDER BY id DESC "+limit, new PostMapper());
		}catch (Exception e){

			logger.error(" error getting ambition posts  "+e);

		}
		return posts;

	}


	public  ArrayList<Post> groupPosts(long groupId,ArrayList<Long> conGroupIds,int postType,int page,long lastId)throws Exception{

		ArrayList<Post> posts = new ArrayList<Post>();
		String groupStr = "";

		if(!conGroupIds.isEmpty()){

			Iterator groupItr = conGroupIds.iterator();
			String gIds = "(";
			while(groupItr.hasNext()){
				gIds += groupItr.next()+",";

			}
			int size = gIds.length();
			groupStr = gIds.substring(0, size-1);
			groupStr +=")";

		}else {

			groupStr= "(null)";
		}

		String last = "";
		if(lastId > 0){
			last = " AND id > "+lastId;
		}

		String condition = "(postto_id IN "+groupStr+" AND wall_type = 2 AND is_private = 0) OR (postto_id = "+groupId+" AND wall_type = 2)";

		if(postType>0){

			condition = condition+" AND post_type_id = "+postType;
		}

		String limit = " limit "+(((page*10) )-10)+","+10;

		try{
			logger.error("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE ( "+condition+" ) AND status = 1 "+last+" ORDER BY id DESC "+limit);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE ( "+condition+" ) AND status = 1 "+last+" ORDER BY id DESC "+limit, new PostMapper());
		}catch (Exception e){

			logger.error(" error getting ambition posts  "+e);

		}

		return posts;
	}




	private class PostMapper implements ResultSetExtractor<ArrayList<Post>>{

		public ArrayList<Post> extractData(ResultSet resultSet) throws SQLException{

			ArrayList<Post> posts = new ArrayList<Post>();

			while (resultSet.next()){

				Post p =new Post(
						resultSet.getLong("id"),
						resultSet.getLong("postby_id"),
						resultSet.getLong("postto_id"),
						resultSet.getString("title"),
						resultSet.getString("description"),
						resultSet.getString("link"),
						resultSet.getInt("post_type_id"),
						resultSet.getString("wall_type"),
						resultSet.getString("date_posted"),
						resultSet.getByte("is_private")

				);

logger.info(" post access "+resultSet.getByte("is_private"));
				posts.add(p);
			}

			return posts;
		}


	}

	public HashMap<String, String> addPost(Post post)
			throws Exception
	{

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.masterJdbcTemplate).withTableName("posts").usingGeneratedKeyColumns(new String[] { "id" }).usingColumns(new String[] { "postby_id", "postto_id", "wall_type", "is_private", "post_type_id", "vertical_id", "title", "description" , "status", "link", "date_posted" });
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);

		Map<String, Object> testInsertMap = Maps.newHashMap();
		testInsertMap.put("postby_id", post.getPostby_id());
		testInsertMap.put("postto_id", post.getPostto_id());
		testInsertMap.put("wall_type", post.getWall_type());
		testInsertMap.put("is_private", post.getIsPrivate());
		testInsertMap.put("post_type_id",post.getPost_type_id());
		testInsertMap.put("vertical_id", post.getVertical_id());
		testInsertMap.put("title", post.getTitle());
		testInsertMap.put("description", post.getDescription());
		testInsertMap.put("status", post.getStatus());
		testInsertMap.put("link", post.getLink());
		testInsertMap.put("date_posted", currentTime);

		HashMap<String, String> status = new HashMap();
		status.put("success", "0");
		try
		{
			Number newId = simpleJdbcInsert.executeAndReturnKey(testInsertMap);
			logger.info("success = " + newId.longValue());

			status.put("success", "1");
			status.put("post_id", "" + newId.longValue());
			HashMap<String, String> localHashMap1 = status;

		}
		catch (Exception e)
		{
			logger.error("error in posting "+e);


		}

		return status;
	}




	public ArrayList<Post> getPost(ArrayList<Long> id)throws Exception{

		Iterator itr = id.iterator();
		String init = "(";
		while(itr.hasNext()){
			init += itr.next()+",";

		}
		int size = init.length();
		String str = init.substring(0, size-1);
		str +=")";
		ArrayList<Post> posts = new ArrayList<>();
		try{
			logger.info("SELECT id,postby_id, postto_id, title, description, link, post_type_id FROM posts WHERE id IN  "+str);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE id IN  "+str, new PostMapper());

		}catch (Exception e){

			logger.error("error selecting single posts "+e);


		}
		return posts;

	}




	public Map<String,Object> updatePostText(Post post) throws SQLException {

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);

		Map<String,Object> p = new HashMap<>();
		String SQL = "UPDATE posts SET description = ?,title = ? , date_updated =? WHERE id = ?";

		slaveJdbcTemplate.update(SQL, post.getDescription(), post.getTitle(),currentTime ,post.getId());
		p.put("success","1");
		logger.error("updated  post text");



		return p;
	}

	public Map<String,Object> deletePost(Map<String,Object> del) throws SQLException {

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);
		logger.info("delete post data layer");
		Map<String,Object> p = new HashMap<>();
		String SQL = "UPDATE posts SET deletedby_id = ? ,date_deleted = ? ,status = ? WHERE id = ? ";

		slaveJdbcTemplate.update(SQL, del.get("user_id"),currentTime ,del.get("status"),del.get("post_id"));
		p.put("success","1");


		return p;
	}


	public ArrayList<Post> getMentorPosts(long user_id,int verticalId) throws Exception{

		ArrayList<Post> mposts = new ArrayList<Post>();
		try{
			logger.error(" no errots  ");
			mposts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE postto_id = "+user_id+" AND wall_type = "+verticalId+" AND vertical_id = 5 AND status = 1 ORDER BY id DESC limit 10 ", new PostMapper());
		}catch (Exception e){

			logger.error(" error getting ambitionz posts  "+e);

		}
		return mposts;

	}



	public HashMap<String, String> addMentorPost(Post post)
			throws Exception
	{

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.masterJdbcTemplate).withTableName("posts").usingGeneratedKeyColumns(new String[] { "id" }).usingColumns(new String[] { "postby_id", "postto_id", "wall_type", "is_private", "post_type_id", "vertical_id", "title", "status", "link" });

		Map<String, Object> testInsertMap = Maps.newHashMap();
		testInsertMap.put("postby_id", "" + post.getPostby_id() + "");
		testInsertMap.put("postto_id", "" + post.getPostto_id() + "");
		testInsertMap.put("wall_type", "" + post.getWall_type() + "");
		testInsertMap.put("is_private", "" + post.getIsPrivate() + "");
		testInsertMap.put("post_type_id", "" + post.getPost_type_id() + "");
		testInsertMap.put("vertical_id", "" + post.getVertical_id() + "");
		testInsertMap.put("title", "" + post.getTitle() + "");
		testInsertMap.put("status", "" + post.getStatus() + "");
		testInsertMap.put("link", "" + post.getLink() + "");

		HashMap<String, String> status = new HashMap();
		status.put("success", "0");
		try
		{
			Number newId = simpleJdbcInsert.executeAndReturnKey(testInsertMap);
			logger.info("success = " + newId.longValue());

			status.put("success", "1");
			status.put("post_id", "" + newId.longValue());
			HashMap<String, String> localHashMap1 = status;

		}
		catch (Exception e)
		{
			logger.error("error in posting "+e);


		}

		return status;
	}

	public ArrayList<Post> getMentorPagePosts(long userId,long visitorId,byte type){

		ArrayList<Post> posts = new ArrayList<>();

		String sql = "";

		if(userId == visitorId){
			//side bar for mentor all messages from differebt users
			sql = "SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE postto_id ="+userId+" AND postby_id != "+userId+" AND is_private = 1 AND wall_type = "+type+" AND  status = 1 GROUP BY postby_id ORDER BY id DESC limit 10";

		}else{
			//my msg to mentor
			sql = "SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE postto_id = "+userId+" AND postby_id = "+visitorId+" AND is_private = 1 AND wall_type = "+type+" AND status = 1 ORDER BY id DESC limit 10";

		}



		try{
			logger.error(" getting mentor post typr  "+type);

			logger.info(sql);
			posts = slaveJdbcTemplate.query(sql, new PostMapper());
		}catch (Exception e){

			logger.error(" error getting ambitionz posts  "+e);

		}

		return posts;
	}



	public ArrayList<Post> getMentorWallPosts(long userId,long visitorId,byte isAdmin,long lastId,int page){

		ArrayList<Post> posts = new ArrayList<>();
		try{
			logger.error(" getting mentor post typr  "+isAdmin);
			String last = "";
			if(lastId > 0){
				last = " AND id > "+lastId;
			}

			String limit = "";
			if(page>0){
				limit = " limit "+(((page*10) )-10)+","+10;

			}

			logger.info("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type FROM posts WHERE  postto_id = "+userId+"  AND is_private = 0  AND status = 1 AND  wall_type = 3 "+last+" ORDER BY id DESC  "+limit);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE  postto_id = "+userId+"  AND is_private = 0  AND status = 1 AND  wall_type = 3 "+last+" ORDER BY id DESC  "+limit, new PostMapper());


		}catch (Exception e){




			logger.error(" error getting mentor posts  "+e);

		}

		return posts;
	}



	public ArrayList<Post> getMentorMedia(long userId,byte postType,int page){

		ArrayList<Post> posts = new ArrayList<>();
		try{

			String limit = "";
			if(page>0){
				limit = " limit "+(((page*10) )-10)+","+10;

			}

			logger.info("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type FROM posts WHERE  postto_id = "+userId+"  AND is_private = 0  AND status = 1 AND  wall_type =  ORDER BY id DESC  "+limit);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE  postto_id = "+userId+"  AND is_private = 0 AND post_type_id ="+postType+" AND status = 1 AND  wall_type = 3  ORDER BY id DESC  "+limit, new PostMapper());


		}catch (Exception e){




			logger.error(" error getting mentor posts  "+e);

		}

		return posts;
	}

	public Integer getMentorMediaPostCount(long userId,byte postType){

		int cnt = 0;
		try{


			logger.info("SELECT COUNT(id) as cnt FROM posts WHERE  postto_id = "+userId+"  AND is_private = 0 AND post_type_id ="+postType+" AND status = 1 AND  wall_type = 3 ");
			cnt = slaveJdbcTemplate.query("SELECT COUNT(id) as cnt FROM posts WHERE  postto_id = "+userId+"  AND is_private = 0 AND post_type_id ="+postType+" AND status = 1 AND  wall_type = 3 ", new PostCountMapper());


		}catch (Exception e){


			logger.error(" error getting mentor media count  "+e);

		}

		return cnt;
	}


	public ArrayList<Post> getMentorTalk(long mentorId,long userId,byte page,long lastId) throws Exception{

		ArrayList<Post> posts = new ArrayList<>();
		String limit = "";
		if(page>0){
			 limit = " limit "+(((page*10) )-10)+","+10;
		}
		String last = "";
		if(lastId >0){
			last = " AND id > "+lastId;
		}

		try{
			logger.info("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted FROM posts WHERE ((postto_id = "+mentorId+" AND postby_id = "+userId+") OR (postto_id = "+userId+" AND postby_id = "+mentorId+")) AND wall_type = 3 "+last+" ORDER BY id DESC "+limit);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE ((postto_id = "+mentorId+" AND postby_id = "+userId+") OR (postto_id = "+userId+" AND postby_id = "+mentorId+")) AND wall_type = 3 AND status = 1 "+last+" ORDER BY id DESC "+limit, new PostMapper());
		}catch (Exception e){

			logger.info("ererroer in talk fetch"+e);
		}

		return posts;

	}


	public ArrayList<Post> getMentorAchieve(long mentorId,long userId,byte page,long lastId) throws Exception{

		ArrayList<Post> posts = new ArrayList<>();
		String limit = "";
		if(page>0){
			 limit = " limit "+(((page*10) )-10)+","+10;
		}
		String last = "";
		if(lastId >0){
			last = " AND id > "+lastId;
		}

		try{
			logger.info("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE (postto_id = "+mentorId+" AND postby_id = "+mentorId+") AND wall_type = 4 AND status = 1 "+last+" ORDER BY id DESC "+limit);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE (postto_id = "+mentorId+" AND postby_id = "+mentorId+") AND wall_type = 4 AND status = 1 "+last+" ORDER BY id DESC "+limit, new PostMapper());
		}catch (Exception e){

			logger.info("ererroer in talk fetch"+e);
		}

		return posts;

	}


	public ArrayList<Post> getProfilePost(long userId,int page,long lastId){


		ArrayList<Post> posts = new ArrayList<>();

		try {
			String last = "";
			if(lastId > 0){
				last = " AND id > "+lastId;
			}

			String limit = "";
			if(page>0){
				limit = " limit "+(((page*10) )-10)+","+10;
			}

			logger.info("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type FROM posts WHERE postto_id = "+userId+" AND status = 1 "+last+" ORDER BY id DESC "+limit);
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE postto_id = "+userId+" AND status = 1 "+last+" ORDER BY id DESC "+limit, new PostMapper());


		}catch (Exception e){

			logger.info("error in getting profile post "+e);
		}

		return posts;
	}


	public ArrayList<Post> getMedia(long userId,byte mediaType,byte page,long lastId)throws Exception{


		ArrayList<Post> media = new ArrayList<>();

		try{

			String last = "";
			if(lastId > 0){
				last = " AND id > "+lastId;
			}

			String limit = "";
			if(page>0){
				limit = " limit "+(((page*9) )-9)+","+9;

			}
			try {
				logger.info("SELECT id,postby_id, postto_id, title,description ,link,post_type_id,wall_type FROM posts WHERE postto_id = " + userId + "  AND wall_type = 1 AND status = 1 AND post_type_id =" + mediaType + " " + last + " ORDER BY id DESC " + limit);
				media = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title,description ,link,post_type_id,wall_type,date_posted,is_private FROM posts WHERE postto_id = " + userId + "  AND wall_type = 1 AND status = 1 AND post_type_id =" + mediaType + " " + last + " ORDER BY id DESC " + limit, new PostMapper());
			}catch (Exception e){
				logger.info(" error in sql  "+e);
			}

		}catch (Exception e){

			logger.error("error in getting media data "+e);
		}
		return media;
	}



	public ArrayList<Post> creativityPlayer(long user_id,int verticalId) throws Exception{

		ArrayList<Post> posts = new ArrayList<Post>();
		try{
			logger.error(" no error  ");
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE id = "+user_id+" AND post_type_id = "+verticalId+" AND vertical_id = 3 AND status = 1 ORDER BY id DESC limit 10 ", new PostMapper());
		}catch (Exception e){

			logger.error(" error getting cre posts  "+e);

		}
		return posts;

	}




	public ArrayList<Post> getCreativityPosts(int verticalId) throws Exception{

		ArrayList<Post> posts = new ArrayList<Post>();
		try{
			logger.error(" no error  ");
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE post_type_id = "+verticalId+" AND vertical_id = 3 AND status = 1 ORDER BY id DESC limit 5 ", new PostMapper());
		}catch (Exception e){

			logger.error(" error getting cre posts  "+e);

		}
		return posts;

	}

	public ArrayList<Post> getCreativityCatPost(int postcat_id,int post_type) throws Exception{

		ArrayList<Post> posts = new ArrayList<Post>();
		try{
			logger.error(" no error  ");
			posts = slaveJdbcTemplate.query("SELECT id,postby_id, postto_id, title, description, link, post_type_id,wall_type,date_posted,is_private FROM posts WHERE postto_id = "+postcat_id+" AND post_type_id = "+post_type+" AND vertical_id = 3 AND status = 1 ORDER BY id DESC limit 5 ", new PostMapper());
		}catch (Exception e){

			logger.error(" error getting cre posts  "+e);

		}
		return posts;

	}




	public Map<String, Object> addCreativityPost(Post crepost) throws SQLException{

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentTime = sdf.format(dt);
		Map<String, Object> p = new HashMap<>();


		try {
			String SQL = "INSERT INTO posts SET postby_id =? , postto_id =?,vertical_id=?, is_private=?, title = ? ,post_type_id = ?,wall_type = ?,link = ?,status = ?,date_posted = ?";

			slaveJdbcTemplate.update(SQL, crepost.getPostby_id(),crepost.getPostto_id(), 3,0, crepost.getTitle(), crepost.getPost_type_id(), crepost.getWall_type(), crepost.getLink(),crepost.getStatus(),currentTime);
			p.put("success", "1");

		}catch (Exception e){

			logger.error("erorr no data"+e);

		}

		return p;
	}

	public Map<String, Object> updateCreVideo(Post update) throws SQLException {


		Map<String, Object> p = new HashMap<>();
		String SQL = "UPDATE posts SET status = ?,link = ? WHERE id = ? ";

		slaveJdbcTemplate.update(SQL, 3, update.getLink(), update.getId() );
		p.put("success", "1");


		return p;
	}


	public Map<String, Object> updateStatusVideo(Post update) throws SQLException {


		Map<String, Object> p = new HashMap<>();
		logger.info(update.getId() + "inmside post dao");
		try {
			String SQL = "UPDATE posts SET status = ? WHERE id = ? ";

			slaveJdbcTemplate.update(SQL, 1, update.getId());
			p.put("success", "1");
		}catch (Exception e){

			logger.error(" error update postttttttttttttttttttttt  "+e);

		}



		return p;
	}


	private class PostCountMapper implements ResultSetExtractor<Integer>{

		public Integer extractData(ResultSet resultSet) throws SQLException{


			resultSet.next();
			return resultSet.getInt("cnt");
		}


	}
}
