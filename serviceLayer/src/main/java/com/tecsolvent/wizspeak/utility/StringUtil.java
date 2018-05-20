package com.tecsolvent.wizspeak.utility;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by jaison on 18/3/16.
 */
public class StringUtil {



	public  static String getGroupCustomName(String groupName){

		String customName = groupName+randString();

		customName  = customName.replaceAll("[^a-zA-Z0-9]", "");
		customName  = customName.replaceAll("\\s+","");

		return customName;
	}





		public static String randString() {

			SecureRandom random = new SecureRandom();
			return   new BigInteger(130, random).toString(32);

		}

}
