package com.example.hfutweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class WeatherObj {
	private static final String PREFS_NAME = "WeatherData";
	private static final String DETAIL_NAME = "WeatherDataDetail";
	private static final String DAY = "day";
	private static final String DETAIL = "detail";
	private static final String TAG = "WeatherObj";
	private int[] date = {0, 0};
	
	private Activity context;
	private SharedPreferences settings;
	private SharedPreferences detailCache;
	private JSONObject dayJsonObject;
	private String temperature;
	private String time;
	private String cityid;
	private int current_month;
	private int current_day;
	
	// 初始化数据
	public WeatherObj(Activity context, String cityCode) {
		this.context = context;
		settings  = context.getSharedPreferences(PREFS_NAME, 0);
		detailCache  = context.getSharedPreferences(cityCode+DETAIL_NAME, 0);
		cityid = cityCode;
		Calendar rightNow = Calendar.getInstance();
		current_month = rightNow.get(Calendar.MONTH) + 1;
		current_day = rightNow.get(Calendar.DAY_OF_MONTH);
	}

	public void fillWeatherInfo(ListView listview, Boolean refresh) {
		String json1 = settings.getString(cityid + DAY, "");
		String dateString = detailCache.getString("date", "");
		if (!dateString.equals("")) {
			date = getMonthAndDay(dateString);
		}
		try {
			dayJsonObject = new JSONObject(json1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isNetworkAvailable() == true && (refresh || dayJsonObject == null || date[0] == 0 )) {
			WeatherAsync async = new WeatherAsync(context, listview);
			async.refresh = true;
			async.execute();
		} else if (dayJsonObject != null && date[0] != 0 ) {
			analysisData();
			fillListviewWithList(listview, this.toStringArray());
		} else {
			ArrayList<String> data = new ArrayList<String>();
	        data.add("暂无数据");
			fillListviewWithList(listview, data);
			Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
		}
	}

	private ArrayList<String> toStringArray() {
		ArrayList<String> context = new ArrayList<String>();
		if (temperature == "") {
			context.add("暂无数据");
		} else {
			context.add("当前温度:" + temperature + "°C");
			context.add("更新时间:" + date[0] + "月" + date[1] + "日"+time);
			JSONObject tempData;
			// 从昨天开始
			for(int i = -1; i <= 2; i++) {
				String tempString = detailCache.getString(current_month + "/" +(current_day+i), "");
				try {
					tempData = new JSONObject(tempString);
					if (tempData != null) {
						context.add(current_month + "月" + (current_day+i) + "日" 
								+ tempData.get("weather") + tempData.get("temp"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return context;
	}

	private void fillListviewWithList(ListView listview, ArrayList<String> arrayList) {
		ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, arrayList);
		listview.setAdapter(adapter);
	}

	private void analysisData() {
		try {
			temperature = dayJsonObject.getString("temp").toString();
			time = dayJsonObject.getString("time").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int[] getMonthAndDay(String string) {
		Log.v(TAG, string);
		int[] day = new int[2];
		Pattern p = Pattern.compile("(\\d+)月(\\d+)日");
		Matcher m = p.matcher(string);
		m.find();
		day[0] = Integer.parseInt(m.group(1));
		day[1]= Integer.parseInt(m.group(2));
		return day;
	}

	@Override
	public String toString() {
		String context = "";
		if (temperature == "") {
			context = "暂无数据";
		} else {
			
			context += "当前温度:" + temperature + "°C\n"
					+ "更新时间:" + date[0] + "月" + date[1] + "日"+time + "\n";
			JSONObject tempData;
			// 从昨天开始
			for(int i = -1; i <= 2; i++) {
				String tempString = detailCache.getString(current_month + "/" +(current_day+i), "");
				try {
					tempData = new JSONObject(tempString);
					if (tempData != null) {
						context = context + current_month + "月" + (current_day+i) + "日" 
								+ tempData.get("weather") + tempData.get("temp") + "\n";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return context;
	}

	public class WeatherAsync extends AsyncTask<String, Void, Boolean> {
		private static final int TIMEOUT_MILLISEC = 10000;
		public boolean refresh = false;
		private ProgressDialog dialog;
		private DefaultHttpClient client;
		private ListView outTextView;
	
		public WeatherAsync(Activity activity, ListView listview) {
			dialog = new ProgressDialog(activity);
			this.outTextView = listview;
			dialog.setTitle("更新中");
			dialog.setMessage("获取天气中...");
		}
	
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
				client = new DefaultHttpClient(httpParams);
				dayJsonObject = getAndSaveJson(cityid, DAY);
				Long last = System.currentTimeMillis()/1000 - settings.getLong(cityid + DETAIL + "time", 0);
				// 需要重新获取详细天气信息
				if (date[0] == 0 || (refresh && (last > 3600))) {
					getAndSaveJson(cityid, DETAIL);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	
		private JSONObject getAndSaveJson(String cityCode, String type) {
			String url = "";
			String json = "";
			if (type.equals(DAY)) {
				url = "http://www.weather.com.cn/data/sk/"+cityCode+".html";
			} else if (type.equals(DETAIL)) {
				url = "http://113.108.239.107/data/"+cityCode+".html";
			}
			try {
				
				json = getJsonByUrl(url);
			    
				JSONObject jsonObject = new JSONObject(json);
				JSONObject weatherInfo = (JSONObject)jsonObject.getJSONObject("weatherinfo");

				if (type.equals(DAY)) {
					cacheDirect(weatherInfo);
				} else if (type.equals(DETAIL)) {
					cacheByDay(weatherInfo);
				}
				
				return weatherInfo;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    refresh = false;
		    return null;
		}

		private void cacheByDay(JSONObject weatherInfo) {
			// 缓存天气信息
		    SharedPreferences.Editor editor = detailCache.edit();
			try {
				// 获取昨天的天气
				String yesterday = detailCache.getString(current_month + "/" +(current_day-1), "");
				// 先清除缓存，防止缓存冗余
				editor.clear();
				editor.putString("date", weatherInfo.getString("date_y").toString());
				editor.putString(current_month + "/" +(current_day-1), yesterday);
				date = getMonthAndDay(weatherInfo.getString("date_y").toString());
				for ( int i = 1; i <= 6; i++ ) {
					JSONObject tempData = new JSONObject();
					tempData.put("weather", weatherInfo.getString("weather"+i).toString());
					tempData.put("temp", weatherInfo.getString("temp"+i).toString());
					editor.putString(date[0] + "/" + (date[1] + i-1), tempData.toString());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    editor.commit();
		}

		private void cacheDirect(JSONObject weatherInfo) {
			// 缓存天气信息
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString(cityid+DAY, weatherInfo.toString());
		    editor.putLong(cityid+DAY + "time", System.currentTimeMillis()/1000);
		    editor.commit();
		}

		private String getJsonByUrl(String url) throws ClientProtocolException, IOException {
			HttpGet httpGetRequest = new HttpGet(url);
			HttpResponse httpResponse;
			httpResponse = client.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			return reader.readLine();
		}

		@Override
		protected void onPostExecute(Boolean success) {
			dialog.dismiss();
			analysisData();
			fillListviewWithList(outTextView, WeatherObj.this.toStringArray());
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}
	}

	public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
	}
}
