package com.example.hfutweather;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	private String[] city = {"合肥", "北京", "上海"};
	private Spinner spinner;
	private TextView outText;
	
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
		outText = (TextView) findViewById(R.id.info);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, city);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setPrompt("请选择城市");
		spinner.setSelection(0);
		
		find_and_modif_button();
		
	}

	private void find_and_modif_button() {
		Button btnRef = (Button)findViewById(R.id.refresh);
		btnRef.setOnClickListener(oclbtnRef);
	}
	
	private Button.OnClickListener oclbtnRef = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (isNetworkAvailable() == false) outText.setText("网络不可用");
			else {
				WeatherAsync async = new WeatherAsync(MainActivity.this);
				switch(spinner.getSelectedItemPosition()) {
				case 0:
					async.execute("http://www.weather.com.cn/data/sk/101220101.html");
					break;
				case 1:
					async.execute("http://www.weather.com.cn/data/sk/101010100.html");
					break;
				case 2:
					async.execute("http://www.weather.com.cn/data/sk/101020100.html");
					break;
				default:
					async.execute("http://www.weather.com.cn/data/sk/101220101.html");
					break;
				}
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
