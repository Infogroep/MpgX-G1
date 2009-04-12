package vub.ig;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public abstract class HTTP_Request {

	
	private static final HttpClient httpclient = new DefaultHttpClient();

	// GET - With Return Value
	public static String get(String server, String command) {
		return get(server, command, "", "");
	}
	public static String get(String server, String command, String opt1) {
		return get(server, command, opt1, "");
	}
	public static String get(String server, String command, String opt1, String opt2) {
		try {
			HttpGet httpget = new HttpGet(server + "/" + command + "/" + opt1 + "/" + opt2);
			HttpResponse httpResponse = httpclient.execute(httpget);
			return _getText((InputStream) httpResponse);
		} catch (Exception e) {
			return null;
		}
	}

	// GET - With Return Value as Body
	public static String get_body(String server, String command) {
		return get_body(server, command, "", "");
	}
	public static String get_body(String server, String command, String opt1) {
		return get_body(server, command, opt1, "");
	}
	public static String get_body(String server, String command, String opt1, String opt2) {
		try {
			HttpParams my_httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(my_httpParams, MpgX_G1.INTERNET_DELAY);
			HttpConnectionParams.setSoTimeout(my_httpParams, MpgX_G1.INTERNET_DELAY); 
			HttpClient httpclient = new DefaultHttpClient(my_httpParams);
			HttpGet httpget = new HttpGet(server + "/" + command + "/" + opt1  + "/" + opt2);
			HttpResponse httpResponse = httpclient.execute(httpget);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();  
				httpResponse.getEntity().writeTo(ostream);
				httpclient.getConnectionManager().shutdown();
				return ostream.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	// GET - With no Return Value
	public static void fast_get(final String server, final String command) {
		fast_get(server,command, "", "");
	}
	public static void fast_get(final String server, final String command, final String opt1) {
		fast_get(server,command, opt1, "");
	}
	public static void fast_get(final String server, final String command, final String opt1, final String opt2) {
		try {
			new Thread() {
				@Override public void run() {
					try {
						HttpParams my_httpParams = new BasicHttpParams();
						HttpConnectionParams.setConnectionTimeout(my_httpParams, MpgX_G1.INTERNET_DELAY);
						HttpConnectionParams.setSoTimeout(my_httpParams, MpgX_G1.INTERNET_DELAY); 
						HttpClient httpclient = new DefaultHttpClient(my_httpParams);
						HttpGet httpget = new HttpGet(server + "/" + command + "/" + opt1 + "/" + opt2);
						httpclient.execute(httpget);
					} catch (Exception e) {	}
				}
			}.start();
		} catch (Exception e) {
			return;
		}
	}

	public static String post(String server, String command) {
		return post(server, command, "");
	}
	public static String post(String server, String command, String body) {
		try {
			HttpPost httppost = new HttpPost(server + "/" + command);
			httppost.setEntity(new StringEntity(body));
			HttpResponse httpResponse = httpclient.execute(httppost);
			return _getText((InputStream) httpResponse);
		} catch (Exception e) {
			return null;
		}
	}

	public static void fast_post(final String server, final String command) {
		fast_post(server, command, "");
	}
	public static void fast_post(final String server, final String command, final String body) {
		try {
			new Thread() {
				@Override public void run() {
					try {
						HttpParams my_httpParams = new BasicHttpParams();
						HttpConnectionParams.setConnectionTimeout(my_httpParams, MpgX_G1.INTERNET_DELAY);
						HttpConnectionParams.setSoTimeout(my_httpParams, MpgX_G1.INTERNET_DELAY); 
						HttpClient httpclient = new DefaultHttpClient(my_httpParams);
						HttpPost httppost = new HttpPost(server + "/" + command);
						httppost.setEntity(new StringEntity(body));
						httpclient.execute(httppost);
					} catch (Exception e) {	}
				}
			}.start();
		} catch (Exception e) {
			return;
		}
	}

	private static String _getText(InputStream in) {
		String text = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		Boolean oneLineOk = false;
		try {
			while((line = reader.readLine()) != null) {
				oneLineOk = true;
				sb.append(line + "\n");
			}
			text = sb.toString();
		} catch (Exception ex) {
		} finally {
			try {
				in.close();
			} catch(Exception ex) {
				return null;
			}
		}
		if (!oneLineOk)
			return null;

		return text;
	}
}
