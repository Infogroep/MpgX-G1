package vub.ig.MpgxG1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class HTTP_Request {

	public static String get(String command) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(MpgXG1.server + "/" + command);
			HttpResponse httpResponse = httpclient.execute(httpget);
			httpclient.getConnectionManager().shutdown();
			return _getText((InputStream) httpResponse);
		} catch (Exception e) {
			return "";
		}
	}

	public static String post(String command, String body) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(MpgXG1.server + "/" + command);
			httppost.setEntity(new StringEntity(body));
			HttpResponse httpResponse = httpclient.execute(httppost);
			httpclient.getConnectionManager().shutdown();
			return _getText((InputStream) httpResponse);
		} catch (Exception e) {
			return "";
		}
	}
	
	private static String _getText(InputStream in) {
		String text = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			text = sb.toString();
		} catch (Exception ex) {
		} finally {
			try {
				in.close();
			} catch(Exception ex) {}
		}
		return text;
	}
}
