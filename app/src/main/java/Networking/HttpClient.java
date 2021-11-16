package Networking;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.roastingassistant.user_interface.HttpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.HttpsURLConnection;

import Database.Bean;
import Database.Blend;
import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.DbData;
import Database.Roast;
import Database.RoastBlendAssociation;
import Database.RoastCheckpointAssociation;

/**
 * To use:
 * HttpClient client = new HttpClient();
 * client.execute();
 */
public class HttpClient extends AsyncTask<Void, Void, String> {
    public enum HttpFunction{
        getAllBeanNames,
        getAllRoastNames,
        getAllBlendNames,
        getAllNames,
        createEntries,
        getBean,
        getRoast,
        getBlend
    };

    public HttpFunction functionToPerform;
    public int idToGet = 0;

    static final String REQUEST_METHOD = "GET";
    static final int READ_TIMEOUT = 2500;
    static final int CONNECTION_TIMEOUT = 2500;

    private HttpCallback activityCallback;

    boolean listsLoaded = false;
    public ArrayList<Bean> beans;
    public ArrayList<Roast> roasts;
    public ArrayList<Blend> blends;
    public ArrayList<DbData> dbData;

    public Bean bean;
    public Roast roast;
    public Blend blend;

    public LinearLayout layout;

    private static final String SERVER = "http://143.198.62.169:3000/";

    //Todo: find a way for this to return string

    @Override
    protected String doInBackground(Void... voids) {
        String result;
        String inputLine;

        Log.d("Server", "Starting server thread.");

        switch (functionToPerform){
            case getAllBeanNames:
            case getAllBlendNames:
            case getAllRoastNames:
                getAll(functionToPerform);
                break;
            case getAllNames:
                getAllNames();
                break;
            case createEntries:
                createEntries();
                break;
            case getBean:
                getBean();
                break;
            case getRoast:
                getRoast();
                break;
            case getBlend:
                getBlend();
                break;
        }

        activityCallback.onDataLoaded();

        return "";
    }

    public void getBlend(){
        String result = getRequest("blend", idToGet);
        int serverBlendId = idToGet;

        JSONObject json = null;
        try {
            json = new JSONObject(result);
            blend = new Blend(json);

            blend.roasts = getAllRoastsForBlend(blend.serverId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Roast> getAllRoastsForBlend(int serverId){
        ArrayList<Roast> roasts = new ArrayList<>();

        String assocResult = getRequest("roast_blend", serverId);
        try {
            JSONArray jArray = new JSONArray(assocResult);
            for(int i=0; i<jArray.length(); i++){
                int roastServerId = jArray.getJSONObject(i).getInt("roast_profile_id");
                roast = null;
                idToGet = roastServerId;
                getRoast();
                roasts.add(roast);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return roasts;
    }

    public void getRoast(){
        String result = getRequest("roast", idToGet);

        int beanServerId=0;
        JSONObject json = null;
        try {
            json = new JSONObject(result);
            beanServerId = json.getInt("bean_id");
            roast = new Roast(json);
            idToGet = beanServerId;
            getBean();
            roast.bean = bean;
            ArrayList<Checkpoint> checkpoints = getAllCheckpointsForRoast(roast.serverId);
            roast.checkpoints = new ArrayList<>(checkpoints);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Checkpoint> getAllCheckpointsForRoast(int roastServerId){
        ArrayList<Checkpoint> checkpoints = new ArrayList<>();

        if(dbData==null)dbData = new ArrayList<DbData>();

        String result = getRequest("checkpoints", roastServerId);

        try {//JSON ARRAY
            JSONArray checkpointArray = new JSONArray(result);

            for(int i=0; i<checkpointArray.length(); i++) {
                JSONObject json = new JSONObject(checkpointArray.getString(i));//convert checkpoint associations to json
                String checkResult = getRequest("checkpoint", json.getInt("checkpoint"));//request checkpoint using id from the json object
                checkpoints.add(new Checkpoint(new JSONObject(checkResult)));//convert result to json, and then create a new checkpoint from that json and add it to the checkpoint array
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return checkpoints;
    }

    public void getBean() {
        String result = getRequest("bean", idToGet);

        JSONObject json = null;
        try {
            json = new JSONObject(result);
            bean = new Bean(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get all of a given data type (roast, bean, or blend) and store in dbData
     * @param toGet HttpFunction type to get. Only roast, bean, or blend.
     */
    public void getAll(HttpFunction toGet){
        if(dbData==null)dbData = new ArrayList<DbData>();
        String name="";
        switch (toGet){
            case getAllBeanNames:
                name="allBeans";
                break;
            case getAllRoastNames:
                name="allRoasts";
                break;
            case getAllBlendNames:
                name="allBlends";
                break;
        }
        if(name==""){
            Log.e("Server", "Incorrect server get request.");
            return;
        }

        String beanResult = getRequest(name);

        try {//JSON ARRAY
            JSONArray jArray = new JSONArray(beanResult);

            for(int i=0; i<jArray.length(); i++) {
                JSONObject json = new JSONObject(jArray.getString(i));
                switch (toGet){
                    case getAllBeanNames:
                        Bean jBean = new Bean(json);
                        dbData.add(jBean);
                        break;
                    case getAllRoastNames:
                        Roast jRoast = new Roast(json);
                        dbData.add(jRoast);
                        break;
                    case getAllBlendNames:
                        Blend jBlend = new Blend(json);
                        dbData.add(jBlend);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAllNames(){
        createEntries();

        if(dbData==null)dbData = new ArrayList<DbData>();

        String beanResult = getRequest("allBeans");
        String roastResult = getRequest("allRoasts");
        String blendResult = getRequest("allBlends");

        try {//JSON ARRAY
            JSONArray beanArray = new JSONArray(beanResult);
            JSONArray roastArray = new JSONArray(roastResult);
            JSONArray blendArray = new JSONArray(blendResult);
            for(int i=0; i<beanArray.length(); i++) {
                JSONObject json = new JSONObject(beanArray.getString(i));
                Bean jBean = new Bean(json);
                dbData.add(jBean);
                Log.d("Server", "");
            }
            for(int i=0; i<roastArray.length(); i++) {
                JSONObject json = new JSONObject(roastArray.getString(i));
                Roast jRoast = new Roast(json);
                dbData.add(jRoast);
                Log.d("Server", "");
            }
            for(int i=0; i<blendArray.length(); i++) {
                JSONObject json = new JSONObject(blendArray.getString(i));
                Blend jBlend = new Blend(json);
                dbData.add(jBlend);
                Log.d("Server", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createEntries(){
        Checkpoint check = new Checkpoint();
        check.name = "50/50 air";
        check.temperature = 250;
        String result = postRequest(check);
        check.serverId = getIdFromResult(result);

        Checkpoint check2 = new Checkpoint();
        check2.name = "Heat to full";
        check2.temperature = 300;
        result = postRequest(check2);
        check2.serverId = getIdFromResult(result);

        Bean bean = new Bean();
        bean.name = "Brazil";
        bean.origin = "Brazil";
        bean.flavours = "Sweet, nutty, caramel";
        result = postRequest(bean);
        bean.serverId = getIdFromResult(result);

        Roast roast = new Roast();
        roast.name = "Brazil Dark";
        roast.checkpoints.add(check);
        roast.checkpoints.add(check2);
        roast.bean = bean;
        result = postRequest(roast);
        int roastId = getIdFromResult(result);
        roast.serverId = roastId;

        RoastCheckpointAssociation roastCheck = new RoastCheckpointAssociation();
        roastCheck.roastId = roastId;
        roastCheck.checkpointId = check.serverId;
        postRequest(roastCheck);

        RoastCheckpointAssociation roastCheck2 = new RoastCheckpointAssociation();
        roastCheck2.roastId = roastId;
        roastCheck2.checkpointId = check2.serverId;
        postRequest(roastCheck2);

        Blend blend = new Blend();
        blend.name = "Sea to Sky";
        blend.description = "Earthy, nutty, caramel";
        blend.roasts.add(roast);
        result = postRequest(blend);
        blend.serverId = getIdFromResult(result);

        RoastBlendAssociation assoc = new RoastBlendAssociation();
        assoc.roast_profile_id = roastId;
        assoc.blend_id = blend.serverId;
        postRequest(assoc);

        //TODO: create roast_blend association object for post request on server
    }

    public void setLoadedCallback(HttpCallback browserActivityCallback){
        this.activityCallback = browserActivityCallback;
    };

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("Server", "Returned string: "+s);
    }

    public void uploadBean(Bean bean){
        String result;
        result = postRequest(bean);
        bean.serverId = getIdFromResult(result);
    }

    public void uploadRoast(Roast roast){
        String result;
        for (int i = 0; i < roast.checkpoints.size(); i++) {
            Checkpoint curCheck = roast.checkpoints.get(i);
            curCheck.minutes = 0;
            curCheck.seconds = 0;
            result = postRequest(curCheck);
            curCheck.serverId = getIdFromResult(result);
        }

        uploadBean(roast.bean);

        result = postRequest(roast);
        roast.serverId = getIdFromResult(result);

        for (int i = 0; i < roast.checkpoints.size(); i++) {
            RoastCheckpointAssociation roastCheck = new RoastCheckpointAssociation();
            roastCheck.roastId = roast.serverId;
            roastCheck.checkpointId = roast.checkpoints.get(i).serverId;
            postRequest(roastCheck);
        }
    }

    public void uploadBlend(Blend blend){
        String result;
        for(int i=0; i<blend.roasts.size(); i++){
            uploadRoast(blend.roasts.get(i));
        }
        result = postRequest(blend);
        blend.serverId = getIdFromResult(result);

        for(int i=0; i<blend.roasts.size(); i++){
            RoastBlendAssociation rba = new RoastBlendAssociation();
            rba.roast_profile_id = blend.roasts.get(i).serverId;
            rba.blend_id = blend.serverId;
            postRequest(rba);
        }

    }

    public String postRequest(DbData data){
        String result="";
        String inputLine;

        try{
            //Connect to server
            URL myUrl = new URL(SERVER+data.typeName);
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            Log.d("Server", "Trying connection "+myUrl.toString());
            connection.connect();
            Log.d("Server", data.typeName+" connection.");

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            String dataString = data.toString();
            Log.d("Server", dataString);
            writer.write(dataString);

            writer.flush();
            writer.close();
            os.close();
            int responseCode = connection.getResponseCode();

            if(responseCode== HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line=br.readLine())!=null)
                    result+=line;
            }else{
                result="Error responseCode:"+responseCode;
            }


        }catch (IOException e){
            e.printStackTrace();
            result = "Error exception:"+e.getMessage();
        }

        return result;
    }

    public String getRequest(String route){
        return getRequest(route, 0);
    }

    public String getRequest(String route, int id){
        String result;
        String inputLine;

        try{
            //Connect to server
            URL myUrl;
            if(id==0)
                myUrl = new URL(SERVER+route);
            else
                myUrl = new URL(SERVER+route+"/"+id);
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            Log.d("Server", "Trying connection.");
            connection.connect();
            Log.d("Server", "Post connection.");

            //get the string from the input stream
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine=reader.readLine())!=null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();

        }catch (IOException e){
            e.printStackTrace();
            result = "Error";
        }

        return result;
    }


    /**
     * Returns the Id contained in the result string from the nodejs server as an int.
     * @param result
     * @return
     */
    public int getIdFromResult(String result){
        if(result.equals("OK")||result.contains("Error")) return -1;

        String numStr = result.replaceFirst("ID:", "");
        return Integer.parseInt(numStr);
    }
}
