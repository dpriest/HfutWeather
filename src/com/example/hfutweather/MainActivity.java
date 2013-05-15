package com.example.hfutweather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hfutweather.db.DBCity;

public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	private ArrayList<String> city = new ArrayList<String>();
	private Spinner spinner;
	private SharedPreferences settings = null;
	private int addCityCode = 1;
	private int delCityCode = 2;
	private CityOperate cityOperate;
	
	public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinner = (Spinner)findViewById(R.id.spinner);

		settings  = getSharedPreferences(WeatherAsync.PREFS_NAME, 0);
		cityOperate = new CityOperate(MainActivity.this);
		city = cityOperate.load();
		
	    String json = settings.getString(getUrlByPosition(0), "");
	    if (json != "") {
			try {
				JSONObject jsonObject = new JSONObject(json);
				fillWeatherInfo(jsonObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		loadCityList();
		
		find_and_modif_button();
	}

	private void loadCityList() {
		spinner.setSelection(0);
		spinner.setPrompt("��ѡ�����");
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				WeatherAsync async = new WeatherAsync(MainActivity.this);
				String url = getUrlByPosition(position);
				async.execute(url);
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
	}
	private void find_and_modif_button() {
		Button btnRef = (Button)findViewById(R.id.refresh);
		btnRef.setOnClickListener(oclbtnRef);
	}
	
	private Button.OnClickListener oclbtnRef = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (isNetworkAvailable() == false) {
				Toast.makeText(MainActivity.this, "���粻����", Toast.LENGTH_SHORT).show();
			}
			else {
				WeatherAsync async = new WeatherAsync(MainActivity.this);
				async.refresh = true;
				String url = getUrlByPosition(spinner.getSelectedItemPosition());
				Log.v(TAG, url);
				async.execute(url);
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected String getUrlByPosition(int selectedItemPosition) {
		String url = "";
		DBCity dbCity = new DBCity(MainActivity.this);
		String cityNum = dbCity.getNumByName(city.get(selectedItemPosition));
		url = "http://www.weather.com.cn/data/sk/"+ cityNum +".html";
		return url;
	}


	public void fillWeatherInfo(JSONObject jsonData) {
		WeatherObj weatherObj = new WeatherObj(jsonData);
		TextView outTextView;
		outTextView = (TextView) findViewById(R.id.info);
		outTextView.setText(weatherObj.toString());
	}
	
	// �˵�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    case R.id.add_city:
	    	startActivityForResult(new Intent(MainActivity.this, CityList.class), addCityCode);
	    	break;
	    case R.id.del_city:
	    	startActivityForResult(new Intent(MainActivity.this, DelCityActivity.class), delCityCode);
	    	break;
	    case R.id.relocate:
	    	cityOperate.relocate();
			city = cityOperate.load();
	    	break;
	    }
		return true;
	}
	
	// ����ѡ����еĽ��
	protected void onActivityResult(int requestCode, 
			int resultCode, Intent outputIntent) {
		super.onActivityResult(requestCode, resultCode, outputIntent);
		parseResult(this, requestCode, resultCode, outputIntent);
	}

	private void parseResult(MainActivity mainActivity, int requestCode,
			int resultCode, Intent outputIntent) {
		if (requestCode == addCityCode) {
			if (resultCode != Activity.RESULT_OK) {
				Log.d(TAG, "Result code is not ok");
				return ;
			}
			Bundle extras = outputIntent.getExtras();
			if (!city.contains(extras.getString("name"))) {
				cityOperate.add(extras.getString("name"));
				city = cityOperate.load();
			}
		} else if (requestCode == delCityCode) {
			city = cityOperate.load();
		}
//		Toast.makeText(MainActivity.this, extras.getString("name") + extras.getString("num"), Toast.LENGTH_SHORT).show();
	}

	public class WeatherAsync extends AsyncTask<String, Void, JSONObject> {
	
		public static final String PREFS_NAME = "WeatherData";
		public boolean refresh = false;
		private Activity activity;
		private ProgressDialog dialog;
	
		public WeatherAsync(Activity activity) {
			this.activity = activity;
			dialog = new ProgressDialog(activity);
			dialog.setTitle("������");
			dialog.setMessage("��ȡ������...");
		}
	
		@Override
		protected JSONObject doInBackground(String... params) {
		    String json = settings.getString(params[0], "");
			try {
			    if (json != "" && !refresh) {
					JSONObject jsonObject = new JSONObject(json);
					return jsonObject;
			    }
				DefaultHttpClient defaultClient = new DefaultHttpClient();
				HttpGet httpGetRequest = new HttpGet(params[0]);
				HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				json = reader.readLine();
			    
				JSONObject jsonObject = new JSONObject(json);
				JSONObject weatherInfo = (JSONObject)jsonObject.getJSONObject("weatherinfo");
				SimpleDateFormat sdf = new SimpleDateFormat("MM��dd��");
				String currentDate = sdf.format(new Date());
				weatherInfo.put("date", currentDate);
				
				// ����������Ϣ
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString(params[0], weatherInfo.toString());
			    editor.commit();
				
			    refresh = false;
				return weatherInfo;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	
		@Override
		protected void onPostExecute(JSONObject jsonData) {
			dialog.dismiss();
			fillWeatherInfo(jsonData);
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
			super.onPreExecute();
		}
	}

}
