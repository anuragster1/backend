package com.tecsolvent.wizspeak;

import com.google.common.collect.Maps;
import com.tecsolvent.wizspeak.model.Country;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author sunil.kata
 * @since 31/01/16
 */
public class TestDao {
	private JdbcTemplate masterJdbcTemplate;
	private JdbcTemplate slaveJdbcTemplate;

	public void setMasterJdbcTemplate(JdbcTemplate masterJdbcTemplate) {
		this.masterJdbcTemplate = masterJdbcTemplate;
	}

	public void setSlaveJdbcTemplate(JdbcTemplate slaveJdbcTemplate) {
		this.slaveJdbcTemplate = slaveJdbcTemplate;
	}

	public void testMysqlCall() throws Exception {

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(masterJdbcTemplate).withTableName("countries")
				.usingColumns("code","name");
		Map<String, Object> testInsertMap = Maps.newHashMap();
		testInsertMap.put("code", "IND");
		testInsertMap.put("name", "india");
		try {
			simpleJdbcInsert.execute(testInsertMap);
		} catch (Exception e) {
			throw e;
		}
	}

	public JSONObject testGetMysqlCall() throws Exception {
		try {
			return slaveJdbcTemplate.query("select name,id,code from countries limit 5", new IdMapper());
		} catch( Exception e) {
			throw e;
		}
	}

	private class IdMapper implements ResultSetExtractor<JSONObject> {
		public JSONObject extractData(ResultSet resultSet) throws SQLException {


		   ArrayList<Country> Ret = new ArrayList<Country>();
			String toReturn=null;
			JSONObject jo = new JSONObject();
			JSONArray ja = new JSONArray();
			while(resultSet.next()) {


				jo.put("id",resultSet.getString("id"));
				jo.put("code",resultSet.getString("code"));
				jo.put("name",resultSet.getString("name"));
				ja.add(jo);

			}

			JSONObject country = new JSONObject();
			country.put("countries",ja);
			return country;
		}
	}
}
