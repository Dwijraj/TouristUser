package pass.com.passsecurity.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 1405214 on 27-03-2017.
 */

public class JSONParser {

    public static String JSONValue(JSONObject jsonObject, String KEY) throws JSONException {
        String Value;
        Value=jsonObject.getString(KEY);
        return Value;
    }
    public static JSONArray GetJsonArray(JSONObject jsonObject, String KEY) throws JSONException {

        JSONArray jsonArray= jsonObject.getJSONArray(KEY);

        return  jsonArray;
    }
}
