package com.bpm.bpmpayment.json;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class JsonCont {
	   public String readJSONFeed(String URL) {
		   Log.w("URL", URL);
	        StringBuilder stringBuilder = new StringBuilder();
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(URL);
	        try {
	            HttpResponse response = httpClient.execute(httpGet);
	            StatusLine statusLine = response.getStatusLine();
	            int statusCode = statusLine.getStatusCode();
	            if (statusCode == 200) {
	                HttpEntity entity = response.getEntity();
	                InputStream inputStream = entity.getContent();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	                String line; 
	                line = reader.readLine();
	                while (line != null) {
	                	stringBuilder.append(line);
	                	line = reader.readLine();
	                }
	                inputStream.close();
	            } else {
	                Log.d("JSON", "Failed to download file");
	            }
	        } catch (Exception e) {
	            Log.d("ReadJSONFeed", e.getLocalizedMessage());
	        }
	        return stringBuilder.toString();
	    }                                                    
}