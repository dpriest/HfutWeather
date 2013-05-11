package com.example.hfutweather;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherObj {
	private static final String TAG = "WeatherObj";
	public String city = "";
	public String cityid = "";
	public String temperature = "";
	public String windDirection = "";
	public String windStrength = "";
	public String shidu = "";
	public Integer windStrengthInt = 0;
	public String time = "";
	public String date = "";
	
	public WeatherObj(JSONObject weatherInfo) {
		try {
			if (weatherInfo != null) {
				city = weatherInfo.getString("city");
				cityid = weatherInfo.getString("cityid");
				temperature = weatherInfo.getString("temp");
				windDirection = weatherInfo.getString("WD");
				windStrength = weatherInfo.getString("WS");
				shidu = weatherInfo.getString("SD");
				windStrengthInt = weatherInfo.getInt("WSE");
				time = weatherInfo.getString("time");
				date = weatherInfo.getString("date");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public String toString() {
		String context = "";
		if (city == "") {
			context = "��������";
		} else {
			context += "����:" + city + "\n"
					+ "�¶�:" + temperature + "��C\n"
					+ "����:" + windDirection + windStrength + "\n"
					+ "ʪ��:" + shidu + "\n"
					+ "����ʱ��:" + date+" "+time;
		}
		return context;
	}

}
