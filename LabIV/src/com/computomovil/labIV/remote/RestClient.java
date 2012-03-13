package com.computomovil.labIV.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class RestClient {

	private StringEntity params;
	private ArrayList<NameValuePair> headers;
	
	private String url;
	
	private int responseCode;
	private String message;
	private String response;
	
	public RestClient(String url) {
		this.url = url;
		headers = new ArrayList<NameValuePair>();
	}
	
	public String getResponse() {
		return response;
	}
	
	public String getErrorMessage() {
		return message;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public void setParam(String param) throws UnsupportedEncodingException {
		params = new StringEntity(param);
	}
	
	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}
	
	public void execute(RequestMethod method) throws Exception {
		switch (method) 
		{
		case GET:
//			String combinedParams = "";
//			if (!params.isEmpty()) 
//			{
//				combinedParams += "?";
//				for (NameValuePair nvp : params) 
//				{
//					String paramString = nvp.getName() +  "=" 
//							+ URLEncoder.encode(nvp.getValue(), "UTF-8");
//					if (combinedParams.length() > 1)
//						combinedParams += "&" + paramString;
//					else
//						combinedParams += paramString;
//				}
//			}
//			HttpGet getRq = new HttpGet(url + combinedParams);
			
			HttpGet getRq = new HttpGet(url);
			for (NameValuePair nvp : headers) 
				getRq.addHeader(nvp.getName(), nvp.getValue());
			
			executeRequest(getRq, url);
			break;

		case POST:
			HttpPost postRq = new HttpPost(url);
			for (NameValuePair nvp : headers) 
				postRq.addHeader(nvp.getName(), nvp.getValue());
			
			if (params != null)
				postRq.setEntity(params);
			
			executeRequest(postRq, url);
			break;
		}
	}
	
	private void executeRequest(HttpUriRequest request, String url) {
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;
		
		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();
			
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null)
			{
				InputStream instream = entity.getContent();
				response = RestClient.convertStreamToString(instream);
				instream.close();
			}
		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (Exception e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
	}
	
	private static String convertStreamToString(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder builder = new StringBuilder();
		String line = null;
		
		try 
		{
			while ((line = reader.readLine()) != null)
				builder.append(line + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try 
			{
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return builder.toString();
	}
}