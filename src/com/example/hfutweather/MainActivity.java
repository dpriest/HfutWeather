package com.example.hfutweather;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hfutweather.db.DBCity;

public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	private ArrayList<String> city = new ArrayList<String>();
	private Spinner spinner;
	private SharedPreferences settings = null;
	private int addCityCode = 1;
	private int delCityCode = 2;
	private CityOperate cityOperate;
	private ListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinner = (Spinner)findViewById(R.id.spinner);
		listview = (ListView) findViewById(R.id.info);

		cityOperate = new CityOperate(MainActivity.this);
		city = cityOperate.load();
		
		loadCityList();

		WeatherObj weatherObj = new WeatherObj(MainActivity.this, 
				getCityNumByPosition(0));
		weatherObj.fillWeatherInfo(listview, false);
		find_and_modif_button();
	}

	private void loadCityList() {
		spinner.setSelection(0);
		spinner.setPrompt("请选择城市");
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

				WeatherObj weatherObj = new WeatherObj(MainActivity.this, 
						getCityNumByPosition(position));
				weatherObj.fillWeatherInfo(listview, false);
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
			WeatherObj weatherObj = new WeatherObj(MainActivity.this, 
					getCityNumByPosition(spinner.getSelectedItemPosition()));
			weatherObj.fillWeatherInfo(listview, true);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected String getCityNumByPosition(int selectedItemPosition) {
		String url = "";
		DBCity dbCity = new DBCity(MainActivity.this);
		return dbCity.getNumByName(city.get(selectedItemPosition));
	}
	
	// 菜单
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
	
	// 处理选择城市的结果
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
	}

}
