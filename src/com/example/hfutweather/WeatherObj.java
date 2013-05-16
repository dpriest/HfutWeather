package com.example.hfutweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;


public class WeatherObj {
	private static final String PREFS_NAME = "WeatherData";
	private static final String DAY = "day";
	private static final String PERIOD = "period";
	private static final String TAG = "WeatherObj";
	public String city = "";
	private String week;
	private int[] date;
	private String fchh;
	private String temp1;
	private String temp2;
	private String temp3;
	private String temp4;
	private String temp5;
	private String temp6;
	private String weather1;
	private String weather2;
	private String weather3;
	private String weather4;
	private String weather5;
	private String weather6;
	private String img_title1;
	private String img_title2;
	private String img_title3;
	private String img_title4;
	private String img_title5;
	private String img_title6;
	private String img_title7;
	private String img_title8;
	private String img_title9;
	private String img_title10;
	private String img_title11;
	private String img_title12;
	private String wind1;
	private String wind2;
	private String wind3;
	private String wind4;
	private String wind5;
	private String wind6;
	private String fl1;
	private String fl2;
	private String fl3;
	private String fl4;
	private String fl5;
	private String fl6;
	private String index;
	private String index_uv;
	private String index_tr;
	private String index_co;
	private String index_cl;
	private String index_xc;
	private String index_d;
	private Activity context;
	private SharedPreferences settings;
	private JSONObject dayJsonObject;
	private JSONObject periodJsonObject;
	private String temperature;
	private String time;
	private String cityid;
	
	public WeatherObj(Activity context, String cityCode) {
		this.context = context;
		settings  = context.getSharedPreferences(PREFS_NAME, 0);
		cityid = cityCode;
		String json1 = settings.getString(cityCode + DAY, "");
		String json2 = settings.getString(cityCode + PERIOD, "");
		try {
			dayJsonObject = new JSONObject(json1);
			periodJsonObject = new JSONObject(json2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void analysisData() {
		try {
			temperature = dayJsonObject.getString("temp").toString();
			time = dayJsonObject.getString("time").toString();
			// 设置城市名称 日期
	        city = periodJsonObject.getString("city").toString();
	        week = periodJsonObject.getString("week").toString();
	        date = getMonthAndDay(periodJsonObject.getString("date_y").toString());
	        fchh = periodJsonObject.getString("fchh").toString();
	        // 1到6天的气温
	        temp1 = periodJsonObject.getString("temp1").toString();
	        temp2 = periodJsonObject.getString("temp2").toString();
	        temp3 = periodJsonObject.getString("temp3").toString();
	        temp4 = periodJsonObject.getString("temp4").toString();
	        temp5 = periodJsonObject.getString("temp5").toString();
	        temp6 = periodJsonObject.getString("temp6").toString();
	        // 1到6天的天气
	        weather1 = periodJsonObject.getString("weather1").toString();
	        weather2 = periodJsonObject.getString("weather2").toString();
	        weather3 = periodJsonObject.getString("weather3").toString();
	        weather4 = periodJsonObject.getString("weather4").toString();
	        weather5 = periodJsonObject.getString("weather5").toString();
	        weather6 = periodJsonObject.getString("weather6").toString();
	        // 天气情况的图片
	        img_title1 = periodJsonObject.getString("img_title1").toString();
	        img_title2 = periodJsonObject.getString("img_title2").toString();
	        img_title3 = periodJsonObject.getString("img_title3").toString();
	        img_title4 = periodJsonObject.getString("img_title4").toString();
	        img_title5 = periodJsonObject.getString("img_title5").toString();
	        img_title6 = periodJsonObject.getString("img_title6").toString();
	        img_title7 = periodJsonObject.getString("img_title7").toString();
	        img_title8 = periodJsonObject.getString("img_title8").toString();
	        img_title9 = periodJsonObject.getString("img_title9").toString();
	        img_title10 = periodJsonObject.getString("img_title10").toString();
	        img_title11 = periodJsonObject.getString("img_title11").toString();
	        img_title12 = periodJsonObject.getString("img_title12").toString();
	        // 1到6天的风况
	        wind1 = periodJsonObject.getString("wind1").toString();
	        wind2 = periodJsonObject.getString("wind2").toString();
	        wind3 = periodJsonObject.getString("wind3").toString();
	        wind4 = periodJsonObject.getString("wind4").toString();
	        wind5 = periodJsonObject.getString("wind5").toString();
	        wind6 = periodJsonObject.getString("wind6").toString();
	        // 1到6天的风力
	        fl1 = periodJsonObject.getString("fl1").toString();
	        fl2 = periodJsonObject.getString("fl2").toString();
	        fl3 = periodJsonObject.getString("fl3").toString();
	        fl4 = periodJsonObject.getString("fl4").toString();
	        fl5 = periodJsonObject.getString("fl5").toString();
	        fl6 = periodJsonObject.getString("fl6").toString();
	        // 各种天气指数
	        index = periodJsonObject.getString("index").toString();
	        index_uv = periodJsonObject.getString("index_uv").toString();
	        index_tr = periodJsonObject.getString("index_tr").toString();
	        index_co = periodJsonObject.getString("index_co").toString();
	        index_cl = periodJsonObject.getString("index_cl").toString();
	        index_xc = periodJsonObject.getString("index_xc").toString();
	        index_d = periodJsonObject.getString("index_d").toString();
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
		if (city == "") {
			context = "暂无数据";
		} else {
			context += "当前温度:" + temperature + "°C\n"
					+ "更新时间:" + date[0] + "月" + date[1] + "日"+time + "\n"
					+ date[0] + "月" + date[1] + "日" + weather1 + temp1 + "\n"
					+ date[0] + "月" + (date[1]+1) + "日" + weather2 + temp2 + "\n"
					+ date[0] + "月" + (date[1]+2) + "日" + weather3 + temp3 + "\n";
		}
		return context;
	}

	public class WeatherAsync extends AsyncTask<String, Void, Boolean> {
		private static final int TIMEOUT_MILLISEC = 10000;
		public boolean refresh = false;
		private ProgressDialog dialog;
		private DefaultHttpClient client;
		private TextView outTextView;
	
		public WeatherAsync(Activity activity, TextView outTextView) {
			dialog = new ProgressDialog(activity);
			this.outTextView = outTextView;
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
				Long last = System.currentTimeMillis()/1000 - settings.getLong(cityid + PERIOD + "time", 0);
				if (periodJsonObject == null || (refresh && (last > 3600))) {
					JSONObject temp = getAndSaveJson(cityid, PERIOD);
					if (!temp.equals(null)) {
						periodJsonObject = temp;
					}
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
			} else if (type.equals(PERIOD)) {
				url = "http://113.108.239.107/data/"+cityCode+".html";
			}
			try {
				
				json = getJsonByUrl(url);
			    
				JSONObject jsonObject = new JSONObject(json);
				JSONObject weatherInfo = (JSONObject)jsonObject.getJSONObject("weatherinfo");
				
				// 缓存天气信息
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString(cityCode + type, weatherInfo.toString());
			    editor.putLong(cityCode + type + "time", System.currentTimeMillis()/1000);
			    editor.commit();
				return weatherInfo;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    refresh = false;
		    return null;
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
			outTextView.setText(WeatherObj.this.toString());
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}
	}

	public void fillWeatherInfo(TextView outTextView, Boolean refresh) {
		if (refresh || dayJsonObject == null || periodJsonObject == null ) {
			WeatherAsync async = new WeatherAsync(context, outTextView);
			async.refresh = true;
			async.execute();
		} else {
			analysisData();
			outTextView.setText(this.toString());
		}
	}

}
