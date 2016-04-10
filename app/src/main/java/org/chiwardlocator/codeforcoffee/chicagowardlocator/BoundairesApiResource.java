package org.chiwardlocator.codeforcoffee.chicagowardlocator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by codeforcoffee on 4/10/16.
 */
public class BoundairesApiResource {

    final String LOCATIONS_URI = "http://boundaries.tribapps.com/1.0/boundary";
    private Map<String, String> Locations = new HashMap<String, String>();

    public static Map<String, String> Parse(JSONObject json, Map<String,String> output) throws JSONException {
        Iterator<String> keys = json.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            String val = null;
            try {
                JSONObject value = json.getJSONObject(key);
                Parse(value, output);
            } catch (Exception ex) {
                val = json.getString(key);
            }
            if (val != null) {
                output.put(key,val);
            }
        }
        return output;
    }

    public Map<String, String> getLocationsHash() {
        return this.Locations;
    }

    public JSONArray Locations(String lat, String lon) throws IOException, JSONException {
        OkHttpClient http = new OkHttpClient();
        URL apiLocation = new URL(LOCATIONS_URI + "/?contains=" + lat + "," + lon + "&sets=wards");
        Request req = new Request.Builder().url(apiLocation).build();
        Response res = http.newCall(req).execute();
        String data = res.body().string();
        JSONObject obj = new JSONObject(data);
        Parse(obj, this.Locations);
        //JSONObject locations = obj.getJSONObject("objects");
        JSONArray locationsList = obj.getJSONArray("objects");
        Log.i("Ward Bound Locs", locationsList.toString());
        return locationsList;
    }

}
