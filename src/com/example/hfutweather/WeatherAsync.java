package com.example.hfutweather;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

public class WeatherAsync extends AsyncTask<String, Void, JSONObject> {

	private Activity activity;
	private ProgressDialog dialog;

	public WeatherAsync(Activity activity) {
		this.activity = activity;
		dialog = new ProgressDialog(activity);
		dialog.setTitle("更新中");
		dialog.setMessage("获取天气中...");
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		DefaultHttpClient defaultClient = new DefaultHttpClient();
		HttpGet httpGetRequest = new HttpGet(params[0]);
		try {
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(JSONObject jsonData) {
		dialog.dismiss();
		WeatherObj weatherObj = new WeatherObj(jsonData);
		TextView outTextView;
		outTextView = (TextView) activity.findViewById(R.id.info);
		outTextView.setText(weatherObj.toString());
		super.onPostExecute(jsonData);
	}

	@Override
	protected void onPreExecute() {
		dialog.show();
		super.onPreExecute();
	}

}
