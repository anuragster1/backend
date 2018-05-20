package wizspeak.notification.client.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.google.gson.Gson;
import com.tecsolvent.wizspeak.notification.dao.Notification.Category;
import com.tecsolvent.wizspeak.notification.dao.Notification.Type;

public class WizNotification {
	public static /*Map<String, String>*/ void createNotification(long userId, Category category, long actorId, long postId, Type notificationType, Map<String, String> msgContainer, boolean isActorSubscriber) throws HttpException, IOException{
		System.out.println("creating notification in WizNotification");
		final HttpClient httpClient = new HttpClient();
		Gson gson = new Gson();
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("userId", ""+userId);
		requestMap.put("actorId", ""+actorId);
		requestMap.put("postId", ""+postId);
		requestMap.put("category", ""+category);
		requestMap.put("type", ""+notificationType.name());
		requestMap.put("msgContainer", ""+gson.toJson(msgContainer));
		
		PostMethod postMethod = null;
		try{
			String parameters = concatenate(requestMap);
			String url = "http://18.216.183.133:8081/backend/notifications" + parameters;
			System.out.println("create notification url - " + url);
			postMethod = new PostMethod(url);
			postMethod.addRequestHeader("Content-Type", "application/json");
			/*NameValuePair[] data = {
				    new NameValuePair("userId", ""+userId),
				    new NameValuePair("actorId", ""+actorId),
				    new NameValuePair("postId", ""+postId),
				    new NameValuePair("category", ""+category),
				    new NameValuePair("type", ""+notificationType.name()),
				    new NameValuePair("msgContainer", gson.toJson(msgContainer))
				};
			postMethod.setRequestBody(data);*/
			
			httpClient.executeMethod(postMethod);
			InputStream is = postMethod.getResponseBodyAsStream();
			String str = getStringFromInputStream(is);
			System.out.println(str);
		}catch(Exception e){
			System.err.println("Exception in notification creation");
			System.err.println(e);
		}finally{
			if(postMethod != null){
				postMethod.releaseConnection();
			}
		}
	}
	
	private static String concatenate(Map<String, String> requestMap) throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		if(requestMap != null){
			for(String key : requestMap.keySet()){
				if(isFirst){
					sb.append("?");
				}else{
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(requestMap.get(key));
				isFirst = false;
			}
		}
		return URLEncoder.encode(sb.toString(), "UTF-8");
	}
	
	/*public static boolean markNotificationAsRead(String notificationId){
		
	}*/
	
	public static Map<String, Object> getNotifications(long userId) throws HttpException, IOException{
		final HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod("http://18.216.183.133:8081/backend/notifications"+"/"+userId);
		try{
			getMethod.addRequestHeader("Content-Type", "application/json");
			/*NameValuePair[] data = {
				    new NameValuePair("user", "joe"),
				    new NameValuePair("password", "bloggs")
				};*/
			//postMethod.setRequestBody(data);
			
			httpClient.executeMethod(getMethod);
			InputStream is = getMethod.getResponseBodyAsStream();
			String str = getStringFromInputStream(is);
			Gson gson = new Gson();
			Map<String, Object> result = gson.fromJson(str, Map.class);
			//System.out.println(result);
			return result;
		}finally{
			getMethod.releaseConnection();
		}
	}
	
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
	
	public static void main(String[] args) throws Exception{
		getNotifications(4l);
	}
}
