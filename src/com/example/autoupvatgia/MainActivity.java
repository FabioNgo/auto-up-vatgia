package com.example.autoupvatgia;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore.Files;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public DatabaseHelper db;
	public WebView loginView;
	public TextView response;
	public int index = 0;
	public boolean startUp = false;
	String responseText = "";
	Calendar dateUpped = null;
	Calendar cal = null;
	public ArrayList<RaoVatItem> items;
	public static CookieManager cookieManager = null;
	public int date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		copyAssets();

		/**
		 * copy database
		 */

		try {
			db = new DatabaseHelper(getApplicationContext());
			items = db.getAllItems();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		/**
		 * Initialize
		 */
		cal = Calendar.getInstance(TimeZone.getTimeZone("GMT +7"));
		date = cal.get(Calendar.DATE);

		response = (TextView) findViewById(R.id.http_response);
		loginView = (WebView) findViewById(R.id.login);
		loginView.setWebChromeClient(new WebChromeClient());
		loginView.setWebViewClient(new MyWebClient());
		loginView.clearCache(true);
		loginView.getSettings().setJavaScriptEnabled(true);
		loginView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		loginView.setVisibility(View.INVISIBLE);

		// if(!checkLogin()) {

		// }

	}

	private class MyWebClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			String title = view.getTitle();
			if (title.contains("Đăng nhập")) {
				response.setText("Chưa đăng nhập");
				loginView.setVisibility(View.VISIBLE);
			}
			if (title.contains("Setting") && index == 0) {

				loginView.setVisibility(View.VISIBLE);

				response.setText(title + "\n\n" + responseText);
				up(items.get(index).id);
				if (startUp) {
					index += 2;
				} else {
					index++;
				}
			}
			if (title.contains("Thông tin cá nhân") || !startUp) {
				response.setText(title + "\n\n" + responseText);
				if (index > 0 && index < items.size()) {

					// Toast.makeText(getApplicationContext(),
					// String.valueOf(date),
					// Toast.LENGTH_SHORT).show();;\
					up(items.get(index).id);
					if (startUp) {
						index += 2;
					} else {
						index++;
					}

				}
				if (index > items.size()) {
					Toast.makeText(getApplicationContext(), "Done",
							Toast.LENGTH_SHORT).show();
					String temp = cal.getTime().toString();
					File file = new File(
							getExternalFilesDir(ACCESSIBILITY_SERVICE),
							"dateUpped.txt");
					try {
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
						outputStreamWriter.write(temp);
						outputStreamWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
	}

	protected void up(int id) {
		// TODO Auto-generated method stub

		String urlString = "http://vatgia.com/raovat/up_raovat.php?record_id="
				+ String.valueOf(id)
				+ "&redirect=L3Byb2ZpbGUvaW5kZXgucGhwPyZtb2R1bGU9cmFvdmF0";
		if ((date - index) % 2 == 0) {
			// loginView.clearCache(true);
			//loginView.loadUrl("http://google.com");
			 loginView.loadUrl(urlString);
			responseText += String.valueOf(items.get(index).title) + "\n";

			// Toast.makeText(getApplicationContext(),
			// String.valueOf(items.get(index).title), Toast.LENGTH_SHORT
			// ).show();
			startUp = true;

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.up_bai) {

			if (compareDate(cal,getUppedDate())>0) {
				loginView.loadUrl("https://id.vatgia.com/dang-nhap/");
			} else {
				Toast.makeText(getApplicationContext(), "Hom nay up roi",
						Toast.LENGTH_SHORT).show();
			}
		} 

		return super.onOptionsItemSelected(item);
	}

	private int compareDate(Calendar cal1, Calendar cal2) {
		// TODO Auto-generated method stub
		
		
		if(cal1== null) return -1;
String a = cal1.getTime().toString();
		if(cal2 == null) return 1;
		String b = cal2.getTime().toString();
		if(cal1.get(Calendar.YEAR)<cal2.get(Calendar.YEAR)) {
			return -1;
		}
		if(cal1.get(Calendar.YEAR)>cal2.get(Calendar.YEAR)) {
			return 1;
		}
		if(cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR)) {
			if(cal1.get(Calendar.DAY_OF_YEAR)<cal2.get(Calendar.DAY_OF_YEAR)) {
				return -1;
			}
			if(cal1.get(Calendar.DAY_OF_YEAR)>cal2.get(Calendar.DAY_OF_YEAR)) {
				return 1;
			}
			if(cal1.get(Calendar.DAY_OF_YEAR)==cal2.get(Calendar.DAY_OF_YEAR)) {
				return 0;
			}
		}
		return 0;
	}

	private void copyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			// Log.e("tag", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				File outFile = new File(getExternalFilesDir(
						getApplicationContext().ACCESSIBILITY_SERVICE)
						.toString(), filename);
				out = new FileOutputStream(outFile);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				// Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private Calendar getUppedDate() {
		Calendar cal = Calendar.getInstance();
		File file = new File(getExternalFilesDir(ACCESSIBILITY_SERVICE),
				"dateUpped.txt");
		try {
			file.createNewFile();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		StringBuilder text = new StringBuilder();

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader br =  new BufferedReader(inputStreamReader);
			
			String line = br.readLine();
			inputStreamReader.close();
			if(line == null) {
				line ="";
			}
			dateUpped = Calendar.getInstance();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss z yyyy");
			dateUpped.setTime(simpleDateFormat.parse(line));

		} catch (IOException e) {
			// You'll need to add proper error handling here
			Log.d("io", e.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss z yyyy");
			try {
				dateUpped.setTime(simpleDateFormat
						.parse("Mon Mar 14 16:02:37 GMT 2011"));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		return dateUpped;
	}
}
