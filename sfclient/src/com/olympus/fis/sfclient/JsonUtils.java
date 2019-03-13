package com.olympus.fis.sfclient;

 

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/*
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
*/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
 
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
 

public class JsonUtils {
	/*************************************************************************************************************************************************************/
	public static void  getJsonKeyVal(String json)  throws Exception {
		JSONObject jsonObject = new JSONObject(json);  
        Set<String> keys =jsonObject.keySet();
        for(String key:keys) {
            System.out.println("Key[" + key +"] ---  Value[" + jsonObject.get(key) + "]" );
        }
	}
	/*************************************************************************************************************************************************************/
	
	/*************************************************************************************************************************************************************/
	
	public static void displayJsonArray(JSONArray jsonArr) {

		Iterator<Object> iterator = jsonArr.iterator();
		// Set<String> keys = jsonObject.keySet();
		while (iterator.hasNext()) {

			Object obj = iterator.next();
			if (obj instanceof JSONObject) {
				Set<String> keys = ((JSONObject) obj).keySet();
				// System.out.println("%%%%%%%%% KEYS %%%%%%%%" + keys.toString() + "keyNum=" +
				// keys.size());
				for (String key : keys) {
					// System.out.println(key + ":" + jsonObject.get(key));
					System.out.println("*******Key: " + key + " -> " + ((JSONObject) obj).get(key));
				}
				// System.out.println("*******" + ((JSONObject) obj).get("CUST_ID"));
			}
		}
	}
	/*************************************************************************************************************************************************************/
	public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
	/*************************************************************************************************************************************************************/
	public static void getJsonData(String responseValue, String srch) {
		JSONObject myResponse = null;

		if (!responseValue.isEmpty()) {
			myResponse = new JSONObject(responseValue);
		} else {
			return;
		}
		String name = "";
		String id = "";
		Iterator iterator = myResponse.keys();
		String key = null;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			System.out.println("**** Key:" + key + "--" + "Value:" + key.valueOf(key));
		}
		JSONArray recsArr = (JSONArray) myResponse.getJSONArray(srch);
		// System.out.println("**** JSON_Array Length:" + recsArr.length());
		// System.out.println("**** JSON_Array:" + recsArr);

		if (recsArr.length() > 0) {
			JSONObject recs = recsArr.getJSONObject(0);
			name = recs.getString("Name");
			id = recs.getString("Id");
			System.out.println("**** JSON_OBJ -> (Name):" + name + "--" + " (ID):" + id + "--");
		}
		// Add values to a HashMap
	}

	/*************************************************************************************************************************************************************/
	
	public static void parseJsonData(String jsonStr) {
		JSONObject jsonObj = null;
		System.out.println("jsonStr:" + jsonStr);
		System.out.println("***********************************************************************************");
		if (jsonStr != null && isJSONValid(jsonStr)) {
			jsonObj = new JSONObject(jsonStr);
		} else {
			System.out.println("JSON string is not valid JSON");
			return;
		}
		idJsonObjType(jsonStr);

		JsonElement root = new JsonParser().parse(jsonStr);
		JsonObject object = root.getAsJsonObject().get("records").getAsJsonObject();
		Map<String, String> map = new Gson().fromJson(object.toString(), Map.class);
		System.out.println("MAP:" + map);

		/*
		 * //System.out.println("JO=" + jsonObj); Iterator iterator = jsonObj.keys();
		 * String key = null;
		 * 
		 * while (iterator.hasNext()) { key = (String) iterator.next();
		 * System.out.println("**** Key:" + key + "--" + "Value:" + jsonObj.get(key)); }
		 * 
		 */
	}

	/*************************************************************************************************************************************************************/
	public static void idJsonObjType(String jsonStr) {
		JSONObject jsonObj = null;
		// String response = "above is my response";
		Boolean arrStatus = false;

		if (jsonStr != null && isJSONValid(jsonStr)) {
			jsonObj = new JSONObject(jsonStr);
			Iterator iterator = jsonObj.keys();
			String key = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				// System.out.println("****^^^^^^^**** Key:" + key + "--" + "Value:" +
				// jsonObj.get(key));
				Object Obj = jsonObj.get(key);
				arrStatus = isJsonObjArray(jsonObj, key);
				if (arrStatus) {
					System.out.println("****^^^^^^^**** Key:" + key + "--" + "is a JSONArray");
				}

				/*
				 * if (Obj instanceof String) { System.out.println("Key=" + key +
				 * " ********** Value is String..........."); } else if (Obj instanceof Integer)
				 * { System.out.println("Key=" + key +
				 * " ********** Value is Integer..........."); } else if (Obj instanceof
				 * JSONArray) { System.out.println("Key=" + key +
				 * " ********** Value is JSONArray..........."); } else if (Obj instanceof
				 * Boolean) { System.out.println("Key=" + key +
				 * " ********** Value is Boolean..........."); }
				 */
			}
		}
	}
	/*************************************************************************************************************************************************************/
	public static Boolean isJsonObjArray(JSONObject jsonObj, String key) {

		// System.out.println("****^^^^^^^**** Key:" + key + "--" + "Value:" +
		// jsonObj.get(key));
		Object Obj = jsonObj.get(key);
		if (Obj instanceof JSONArray) {
			System.out.println("Key=" + key + " ********** Value is Array...........");
			return true;
		}
		return false;
	}

	/*************************************************************************************************************************************************************/
	/*************************************************************************************************************************************************************/

	
	

}
