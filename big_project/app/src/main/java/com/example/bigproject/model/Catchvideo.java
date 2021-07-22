package com.example.bigproject.model;

import android.util.Log;

import com.example.bigproject.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Catchvideo {
    String TAG="Catchvideo";
    UploadAPI api;
    public List<VideoReturnMessage> getdatafrominternet(String name){
        List<VideoReturnMessage> data = new LinkedList<VideoReturnMessage>();
        initNetwork();
        data = getData(name);
        return data;
    }

    public void initNetwork(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(UploadAPI.class);
    }
    private List<VideoReturnMessage> getData(final String studentId){
        final MessageListResponse[] res = {null};
        Log.d(TAG, "run: success HERE!!");
        res[0] = getresponse(studentId);
        Log.d(TAG, "run: success"+ res[0].success);
        return res[0].feeds;
    }

    private MessageListResponse getresponse(String studentId){
        MessageListResponse response=null;
        try {

            String u=Constants.BASE_URL+"video?student_id=";
            if(studentId!=null){
                u=u+studentId;
            }

            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", Constants.token);
            if(conn.getResponseCode()==200){
                Log.d(TAG, "getresponse: success");
                InputStream in= conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                response=new Gson().fromJson(reader, new TypeToken<MessageListResponse>(){}.getType());
                Log.d(TAG, "getresponse: "+response.success);
                reader.close();
                in.close();
            }
            //Log.d(TAG, "------getresponse: failed "+conn.getResponseCode()+' '+url.toString());
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
