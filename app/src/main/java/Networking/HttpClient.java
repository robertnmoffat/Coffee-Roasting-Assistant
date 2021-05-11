package Networking;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import Database.Bean;
import Database.Checkpoint;
import Database.DatabaseHelper;
import Database.Roast;

/**
 * To use:
 * HttpClient client = new HttpClient();
 * client.execute();
 */
public class HttpClient extends AsyncTask<Void, Void, String> {
    static final String REQUEST_METHOD = "GET";
    static final int READ_TIMEOUT = 1500;
    static final int CONNECTION_TIMEOUT = 1500;

    private static final String SERVER = "http://143.198.62.169:3000/";//TODO: Change to actual server.

    //Todo: find a way for this to return string

    @Override
    protected String doInBackground(Void... voids) {
        String result;
        String inputLine;

        Log.d("Server", "Starting server thread.");


        Checkpoint testPoint = new Checkpoint();
        testPoint.name = "Test Checkpoint";
        testPoint.temperature = 250;
        //result = checkPointPostRequest(testPoint);

        Bean testBean = new Bean();
        testBean.name = "Test Bean";
        testBean.flavours = "Testalicious";
        testBean.serverId = 1;

        Roast testRoast = new Roast();
        testRoast.name = "Test Roast";
        testRoast.bean = testBean;
        testRoast.dropTemp = 436;
        result = roastPostRequest(testRoast);


        //result = beanPostRequest(testBean);

        Log.d("Server", result);

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("Server", "Returned string: "+s);
    }

    public String beanPostRequest(Bean bean){
        String result="";
        String inputLine;

        try{
            //Connect to server
            URL myUrl = new URL(SERVER+"bean");
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            Log.d("Server", "Trying connection.");
            connection.connect();
            Log.d("Server", "Post connection.");

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            String dataString = bean.toString();
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

    public String checkPointPostRequest(Checkpoint checkpoint){
        String result="";
        String inputLine;

        try{
            //Connect to server
            URL myUrl = new URL(SERVER+"checkpoint");
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            Log.d("Server", "Trying connection.");
            connection.connect();
            Log.d("Server", "Checkpoint connection.");

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            String dataString = checkpoint.toString();
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

    public String roastPostRequest(Roast roast){
        String result="";
        String inputLine;

        try{
            //Connect to server
            URL myUrl = new URL(SERVER+"roast");
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            Log.d("Server", "Trying connection.");
            connection.connect();
            Log.d("Server", "Checkpoint connection.");

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            String dataString = roast.toString();
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

    public String getRequest(){
        String result;
        String inputLine;

        try{
            //Connect to server
            URL myUrl = new URL(SERVER);
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

    public String getRequest(String route){
        String result;
        String inputLine;

        try{
            //Connect to server
            URL myUrl = new URL(SERVER+route);
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

}
