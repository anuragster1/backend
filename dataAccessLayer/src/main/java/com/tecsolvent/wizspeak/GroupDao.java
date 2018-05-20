package com.tecsolvent.wizspeak;

import com.google.common.collect.Maps;
import com.tecsolvent.wizspeak.model.Category;
import com.tecsolvent.wizspeak.model.Group;
import com.tecsolvent.wizspeak.model.GroupUser;
import com.tecsolvent.wizspeak.model.SubCategory;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by jaison on 12/3/16.
 */
public class GroupDao {


		public static Logger logger = Logger.getLogger(GroupDao.class);

		private JdbcTemplate masterJdbcTemplate;
		private JdbcTemplate slaveJdbcTemplate;


		public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
			this.masterJdbcTemplate = masterJdbcTemplate;
		}

		public void setSlaveJdbcTemplate(JdbcTemplate slaveJdbcTemplate) {
			this.slaveJdbcTemplate = slaveJdbcTemplate;
		}


		public Group getGroup(long groupId) throws Exception{

			Group group = new Group();

			try{

				group  =  slaveJdbcTemplate.query("SELECT  id,groupId, name, description, sub_category_id FROM groups WHERE id ="+groupId, new SingleGroupMapper());

			}catch (Exception e){

				logger.error("fetching group details");
			}

			return group;
		}
		public Long getGroupId(String groupStr) throws Exception{

			long groupId= 0;

			try{

				groupId  =  slaveJdbcTemplate.query("SELECT  id FROM groups WHERE groupId = '"+groupStr+"'", new GroupIdMapper());
				logger.info("SELECT  id FROM groups WHERE groupId = '"+groupStr+"'");

			}catch (Exception e){

				logger.error("fetching group id");
			}

			return groupId;
		}

		private class GroupIdMapper implements ResultSetExtractor<Long> {

			public Long extractData(ResultSet resultSet) throws SQLException {

				resultSet.next();
			return resultSet.getLong("id");
			}
		}


		private class SingleGroupMapper implements ResultSetExtractor<Group>{

			public Group extractData(ResultSet resultSet) throws SQLException {

				resultSet.next();

				Group group = new Group();
				group.setName(resultSet.getString("name"));
				group.setDescription(resultSet.getString("description"));
				group.setId(resultSet.getLong("id"));
				group.setCustomUrl(resultSet.getString("groupId"));
				int subCatId = resultSet.getInt("sub_category_id");
				try {
					int vertical = getVertical(subCatId);
					group.setVertical_id(vertical);
				}catch (Exception e){

					logger.info("error in getting vertical id");

				}


			return group;

			}
		}


		public Integer getVertical(long subCategoryId) throws Exception{

			int vertical = 0;

			try{

				vertical  =  slaveJdbcTemplate.query("SELECT vertical_id FROM sub_categories as sC JOIN categories as c ON sC.category_id = c.id where sC.id = "+subCategoryId, new GroupVerticalMapper());

			}catch (Exception e){

				logger.info("error in getting vertical"+e);
			}

			return vertical;
		}




		private class GroupVerticalMapper implements ResultSetExtractor<Integer>{

			public Integer extractData(ResultSet resultSet) throws SQLException{

				resultSet.next();


				return resultSet.getInt("vertical_id");
			}


		}



	//get group categories

	public ArrayList<Category> getCategories(int verticalId) throws Exception{

		ArrayList<Category> categories = new ArrayList<>();

		try{
logger.info("SELECT id,name,vertical_id,has_fans,type FROM categories WHERE vertical_id = "+verticalId);
			categories =  slaveJdbcTemplate.query("SELECT id,name,vertical_id,has_fans,type FROM categories WHERE vertical_id = "+verticalId,new CategoryMapper());
		}catch (Exception e){
			logger.error("error select categories "+e);

		}
		return categories;

	}


	private class CategoryMapper implements ResultSetExtractor<ArrayList<Category>> {


		public ArrayList<Category> extractData(ResultSet resultSet) throws SQLException {

			ArrayList<Category> categories = new ArrayList<>();
			while (resultSet.next()){
				Category category = new Category(resultSet.getInt("id"),resultSet.getString("name"),resultSet.getInt("has_fans"),resultSet.getInt("type"));
				categories.add(category);
			}
			return categories;
		}
	}


	public ArrayList<SubCategory> getSubCategories() throws Exception{

		ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
		try{

			subCategories =  slaveJdbcTemplate.query("SELECT id, name, category_id FROM `sub_categories` ",new SubCategoryMapper());
		}catch (Exception e){
			logger.error("error Sub categories query "+e);


		}
		return subCategories;

	}


	private class SubCategoryMapper implements ResultSetExtractor<ArrayList<SubCategory>> {


		public ArrayList<SubCategory> extractData(ResultSet resultSet) throws SQLException {

			ArrayList<SubCategory> subCategories = new ArrayList<>();
			while (resultSet.next()){

				SubCategory subCategory = new SubCategory(resultSet.getString("name"),resultSet.getInt("category_id"),resultSet.getInt("id"));
				subCategories.add(subCategory);
			}
			return subCategories;
		}
	}






	public Long createGroup (Group group) throws Exception{


		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(slaveJdbcTemplate).withTableName("groups").usingGeneratedKeyColumns(new String[] { "id" }).usingColumns("name","description","sub_category_id","type","status","createdby_id","date_created","groupId");

		Map<String, Object> newGroup = Maps.newHashMap();

		try {
			newGroup.put("name", group.getName());
			newGroup.put("description", group.getDescription());
			newGroup.put("sub_category_id", group.getSub_category_id());
			newGroup.put("type", group.getType());
			newGroup.put("status", group.getStatus());
			newGroup.put("user_id", group.getCreatedby_id());
			newGroup.put("createdby_id", group.getCreatedby_id());
			newGroup.put("date_created", group.getDate_created());
			newGroup.put("groupId", getMd5(group.getName()));
		} catch (Exception e){

			logger.info(" create  new group "+e);
			throw e;
		}
		long status = 0;


		try {
			Number newId = simpleJdbcInsert.executeAndReturnKey(newGroup);

			status = newId.longValue();

		} catch (Exception e) {

			logger.info("in addd new group  error "+e);

		}

		return status;
	}


	public String getMd5(String name) throws Exception{
		MessageDigest md = MessageDigest.getInstance("MD5");
		//Add password bytes to digest
		md.update(name.getBytes());
		//Get the hash's bytes
		byte[] bytes = md.digest();
		//This bytes[] has bytes in decimal format;
		//Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++)
		{
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		//Get complete hashed password in hex format
		String generatedPassword = sb.toString();
		return generatedPassword;
	}

	public ArrayList<Group> getUserAmbitionGroups(long user_id) throws Exception {

		ArrayList<Group> groups = new ArrayList<>();

		try{
			groups =  slaveJdbcTemplate.query("SELECT gp.id,gp.groupId,gp.name,gpic.link,c.vertical_id FROM groups as gp left join user_group_profile_pics as gpic on (gpic.wall_id = gp.id AND wall_type = 2 AND is_active = 1 AND is_avatar = 1) join sub_categories as sc on(gp.sub_category_id = sc.id) join categories as c on(sc.category_id = c.id) where gp.id IN (SELECT DISTINCT(group_id) FROM user_group_relations WHERE user_id = "+user_id+" AND status = 1) GROUP BY gp.id", new GroupMapper());
logger.info("SELECT gp.id,gp.groupId,gp.name,gpic.link,c.vertical_id FROM groups as gp left join user_group_profile_pics as gpic on (gpic.wall_id = gp.id AND wall_type = 2 AND is_active = 1 AND is_avatar = 1) join sub_categories as sc on(gp.sub_category_id = sc.id) join categories as c on(sc.category_id = c.id) where gp.id IN (SELECT DISTINCT(group_id) FROM user_group_relations WHERE user_id = "+user_id+" AND status = 1) GROUP BY gp.id");

		}catch (Exception e) {

			logger.error("Error getUserAmbition group "+e);

		}
		return groups;

	}

	public ArrayList<Long> getUserGroupId(long userId) throws Exception{

		ArrayList<Long> groupIds = new ArrayList<>();

		try{
			groupIds =  slaveJdbcTemplate.query("SELECT DISTINCT(group_id) FROM user_group_relations WHERE user_id = "+userId+" AND status = 1 ", new GroupIdsMapper());


		}catch (Exception e){

			logger.error("fetching user group ids "+e);
		}

		return groupIds;
	}

	private class GroupMapper implements ResultSetExtractor<ArrayList<Group>> {

		public ArrayList<Group> extractData(ResultSet resultSet) throws SQLException {

			ArrayList<Group> groups = new ArrayList<Group>();

			while (resultSet.next()){


				Group g = new Group(resultSet.getInt("id") ,resultSet.getInt("vertical_id"),resultSet.getString("name"));
				g.setCustomUrl(resultSet.getString("groupId"));
				groups.add(g);


			}


			return groups;

		}
	}


	private class GroupIdsMapper implements ResultSetExtractor<ArrayList<Long>> {

		public ArrayList<Long> extractData(ResultSet resultSet) throws SQLException {

			ArrayList<Long> groupIds = new ArrayList<Long>();

			while (resultSet.next()){

				groupIds.add(resultSet.getLong("group_id"));
			}

			return groupIds;
		}
	}

	public boolean addGroupMember(GroupUser groupUser) throws Exception{

		logger.info("inside  addGroupMember ");

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(slaveJdbcTemplate).withTableName("user_group_relations").usingColumns("role_id","status","group_id","user_id","rolesetby_id","date_invited","invitedby_id","role_alias","date_joined");


		Map<String, Object> newGroupUser = Maps.newHashMap();

		newGroupUser.put("role_id",groupUser.getRole_id());
		newGroupUser.put("status",groupUser.getStatus());
		newGroupUser.put("group_id",groupUser.getGroup_id());
		newGroupUser.put("user_id",groupUser.getUser_id());
		newGroupUser.put("rolesetby_id",groupUser.getRolesetby_id());
		newGroupUser.put("role_alias",groupUser.getRole_alias());
		newGroupUser.put("invitedby_id",groupUser.getInvitedby_id());
		newGroupUser.put("date_invited",groupUser.getDate_invited());
		newGroupUser.put("date_joined",groupUser.getDate_joined());

		boolean status = false;

		try {
			Number newId = simpleJdbcInsert.execute(newGroupUser);
			status = true;

		} catch (Exception e) {

			logger.info("in addd new groupUser  error "+e);

		}

		return status;
	}


	public ArrayList<GroupUser> getGroupMembers(long groupId,int status) throws Exception{

		ArrayList<GroupUser> member = new ArrayList<>();
		try{
			member =  slaveJdbcTemplate.query("SELECT id,user_id,role_id,role_alias from user_group_relations where status = "+status+" AND group_id ="+groupId, new GroupMemberMapper());

		}catch (Exception e) {

			logger.error("Error getUserAmbition group "+e);

		}

		return member;
	}


	private class GroupMemberMapper implements ResultSetExtractor<ArrayList<GroupUser>> {

		public ArrayList<GroupUser> extractData(ResultSet resultSet) throws SQLException{

			ArrayList<GroupUser> members = new ArrayList<>();
			while (resultSet.next()){
				GroupUser groupUser = new GroupUser();
				groupUser.setId(resultSet.getLong("id"));
				groupUser.setRole_id(resultSet.getInt("role_id"));
				groupUser.setUser_id(resultSet.getLong("user_id"));
				groupUser.setRole_alias(resultSet.getString("role_alias"));

				members.add(groupUser);
			}

			return members;
		}
	}



	public ArrayList<Long> getConnectedGroups(Long groupId,int status)throws Exception{

		ArrayList<Long> groupIds = new ArrayList<>();

		try{
			groupIds =  slaveJdbcTemplate.query("SELECT group_id_from FROM group_group_connects WHERE  group_id_to = "+groupId+" AND request_status = "+status+" union SELECT group_id_to FROM group_group_connects WHERE  group_id_from = "+groupId+" AND request_status ="+status , new ConnectedGroupMapper());

		}catch (Exception e) {

			logger.error("Error getUserAmbition group "+e);

		}

		return groupIds;
	}


	private class ConnectedGroupMapper implements ResultSetExtractor<ArrayList<Long>> {


		public ArrayList<Long> extractData(ResultSet resultSet) throws SQLException{

			ArrayList<Long> groupIds = new ArrayList<>();
			while (resultSet.next()){

				long connectedGroupId = resultSet.getLong("group_id_from");
				groupIds.add(connectedGroupId);
			}

			return groupIds;
		}

	}


	public boolean updateGroupName(String name,long id){


		Map<String,Object> p = new HashMap<>();

		String SQL = "UPDATE groups SET name = ? WHERE id = ?";

		boolean status = false;

		try {
			slaveJdbcTemplate.update(SQL, name, id);
			status = true;
		}catch (Exception e){

			logger.info("error in update group name "+e);
		}

		return status;
	}




	public boolean updateGroupDescription(String name,long id){


		Map<String,Object> p = new HashMap<>();

		String SQL = "UPDATE groups SET description = ? WHERE id = ?";

		boolean status = false;

		try {
			slaveJdbcTemplate.update(SQL, name, id);
			status = true;
		}catch (Exception e){

			logger.info("error in update group descri "+e);
		}

		return status;
	}


	public HashMap<String, Byte> getUserRole(long userId ,long groupId){

		HashMap<String, Byte> roleId = new HashMap<>();

		roleId.put("roleId", (byte) 3);
		roleId.put("status", (byte) 0);
		try{
			logger.info("SELECT role_id,status FROM user_group_relations WHERE group_id = "+groupId+" and user_id = "+userId+"  ORDER BY role_id ASC");
			roleId =  slaveJdbcTemplate.query("SELECT role_id,status FROM user_group_relations WHERE group_id = "+groupId+" and user_id = "+userId+"  ORDER BY role_id ASC" , new userGroupRoleMapper());

		}catch (Exception e) {

			logger.error("Error getUserAmbition group "+e);

		}
		return roleId;

	}


	private  class  userGroupRoleMapper implements ResultSetExtractor<HashMap<String, Byte>> {

		public HashMap<String, Byte> extractData(ResultSet resultSet) throws SQLException{



		HashMap<String,Byte> result = new HashMap<>();
			result.put("roleId", (byte) 3);
			result.put("status",(byte) 0);

		try {

			resultSet.next();
			result.put("status",resultSet.getByte("status"));
			if(resultSet.getByte("status") == 1){
				result.put("roleId",resultSet.getByte("role_id"));
			}

		}catch (Exception e){

			logger.info(" error in getting role id "+e);
			throw e;
		}

		return result;

		}


	}


	public int checkPic(long groupId,int wallType,int isAvatar,int isActive){

		int status = 0;

		try{
			status =  slaveJdbcTemplate.query("SELECT id FROM user_group_profile_pics WHERE wall_id= "+groupId+" AND wall_type = "+wallType+" AND is_avatar = "+isAvatar+" AND is_active = "+isActive , new UserPicMapper());
			logger.error("SELECT id FROM user_group_profile_pics WHERE wall_id= "+groupId+" AND wall_type = "+wallType+" AND is_avatar = "+isAvatar+" AND is_active = "+isActive);
		}catch (Exception e) {

			logger.error("Error getUserAmbition group "+e);

		}
		return status;


	}


	private class UserPicMapper implements ResultSetExtractor<Integer> {
		public Integer extractData(ResultSet resultSet) throws SQLException {

			resultSet.next();
			return resultSet.getInt("id");
		}
	}


	public boolean removeProfilePic(long groupId,int wallType,int isAvatar,int isActive){


		Map<String,Object> p = new HashMap<>();

		String SQL = "UPDATE user_group_profile_pics SET is_active = ? WHERE wall_id = ? AND wall_type = ? AND is_avatar = ?";

		boolean status = false;

		try {
			slaveJdbcTemplate.update(SQL, isActive, groupId,wallType,isAvatar);
			status = true;
		}catch (Exception e){

			logger.info("error in update group profile pic  "+e);
		}

		return status;
	}



	public Long addProfilePic (long groupId,int wallType,int isAvatar,int isActive,String groupCustomName,int top, int left) throws Exception{


		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(slaveJdbcTemplate).withTableName("user_group_profile_pics").usingColumns("wall_id", "wall_type", "link", "is_avatar", "is_active", "lft", "top", "date_shared");

		Map<String, Object> groupPic = Maps.newHashMap();

		groupPic.put("wall_id",groupId);
		groupPic.put("wall_type",wallType);
		groupPic.put("link",groupCustomName);
		groupPic.put("is_avatar",isAvatar);
		groupPic.put("is_active",isActive);
		groupPic.put("lft",left);
		groupPic.put("top",top);
		groupPic.put("date_shared", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		long status = 0;


		try {
			simpleJdbcInsert.execute(groupPic);

		} catch (Exception e) {

			logger.info("in addd new group profile pic  error "+e);

		}

		return status;
	}


	public byte userGroupRelation(long groupId,long userId,byte status,byte role_id,String roleAlias,String dateRequested)throws Exception{

		String SQL = "UPDATE user_group_relations SET status = ?,role_id = ?,role_alias = ?,rolesetby_id = ?,invitedby_id = ? ,date_requested = ? WHERE group_id = ? AND user_id = ?";
		byte result = 0;
		int state = 0;

		try {
			state = slaveJdbcTemplate.update(SQL, status, role_id,roleAlias,userId,userId,dateRequested,groupId,userId);
logger.info("UPDATE user_group_relations SET status = "+status+",role_id = "+role_id+",role_alias = "+roleAlias+",rolesetby_id = "+userId+",invitedby_id = "+userId+" ,date_requested = "+dateRequested+" WHERE group_id = "+groupId+" AND user_id = "+userId);
			result = 1;
			logger.info(" status = "+state);

			if(state==0){



				SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(slaveJdbcTemplate).withTableName("user_group_relations").usingColumns("role_id","status","group_id","user_id","rolesetby_id","invitedby_id","date_requested","role_alias");


				Map<String, Object> newGroupUser = Maps.newHashMap();

				newGroupUser.put("role_id",role_id);
				newGroupUser.put("status",status);
				newGroupUser.put("group_id",groupId);
				newGroupUser.put("user_id",userId);
				newGroupUser.put("rolesetby_id",userId);
				newGroupUser.put("role_alias",roleAlias);
				newGroupUser.put("date_requested",dateRequested);

				state = 0;
				try {
					Number newId = simpleJdbcInsert.execute(newGroupUser);
					state = 1;

				} catch (Exception e) {

					logger.info("in addd new groupUser request  error "+e);

				}

			}
		}catch (Exception e){

			logger.info("error in update group profile pic  "+e);
		}

		return result;
	}


	public void removeUser(long ugrID)throws Exception{
		String sql = "UPDATE user_group_relations SET status = ? where id = ?";
		byte status = 0;

		slaveJdbcTemplate.update(sql, status, ugrID);



	}


}
