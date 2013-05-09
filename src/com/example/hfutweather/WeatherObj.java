package com.example.hfutweather;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherObj {
	private static final String TAG = "WeatherObj";
	public String city = "";
	public String cityid = "";
	public String temperature = null;
	public String windDirection = "";
	public String windStrength = "";
	public String shidu = "";
	public Integer windStrengthInt = 0;
	public String time = "";
	
	public WeatherObj(JSONObject jsonData) {
		try {
			JSONObject weatherInfo = (JSONObject)jsonData.getJSONObject("weatherinfo");
			city = weatherInfo.getString("city");
			cityid = weatherInfo.getString("cityid");
			temperature = weatherInfo.getString("temp");
			windDirection = weatherInfo.getString("WD");
			windStrength = weatherInfo.getString("WS");
			shidu = weatherInfo.getString("SD");
			windStrengthInt = weatherInfo.getInt("WSE");
			time = weatherInfo.getString("time");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public String toString() {
		String context = "";
		context += "����:" + city + "\n"
				+ "�¶�:" + temperature + "��C\n"
				+ "����:" + windDirection + windStrength + "\n"
				+ "ʪ��:" + shidu + "\n"
				+ "����ʱ��:" + time;
		return context;
	}

}
