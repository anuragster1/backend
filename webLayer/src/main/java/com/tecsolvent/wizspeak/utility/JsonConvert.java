package com.tecsolvent.wizspeak.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

/**
 * Created by jaison on 8/3/16.
 */

public class JsonConvert {

	/* Get actual class name to be printed on */
public static  String ObjJson(Object object) throws JsonProcessingException {

	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	String json = ow.writeValueAsString(object);
	return json;
}



}
