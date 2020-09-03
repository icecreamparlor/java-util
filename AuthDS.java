//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AuthDS {
    private JSONObject responseObj;

    public AuthDS(String resultJson) {
        try {
            JSONParser jsonParser = new JSONParser();
            this.responseObj = (JSONObject)jsonParser.parse(resultJson);
        } catch (ParseException var3) {
            var3.printStackTrace();
        }

    }

    public String getString(String key) {
        return this.responseObj != null && this.responseObj.containsKey(key) && this.responseObj.get(key) != null ? this.responseObj.get(key).toString() : "";
    }

    public int getInt(String key) {
        return this.responseObj != null && this.responseObj.containsKey(key) && this.responseObj.get(key) != null ? Integer.parseInt(this.responseObj.get(key).toString()) : 0;
    }

    public AuthDS getObject(String key) {
        return this.responseObj != null && this.responseObj.containsKey(key) && this.responseObj.get(key) != null ? new AuthDS(this.responseObj.get(key).toString()) : null;
    }

    public AuthDS getObject(String key, int index) {
        return this.responseObj != null && this.responseObj.containsKey(key) && this.responseObj.get(key) != null ? new AuthDS(((JSONArray)this.responseObj.get(key)).get(index).toString()) : null;
    }

    public String toJSONString() {
        return this.responseObj.toJSONString();
    }
}
