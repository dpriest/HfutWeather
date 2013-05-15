package com.example.hfutweather;

import java.util.ArrayList;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.hfutweather.cache.CityCache;

public class CityOperate {

	private Activity context;
	private CityCache cityCache;
	
	public CityOperate(Activity context) {
		this.context = context;
		this.cityCache = new CityCache(context);
	}
	public ArrayList<String> load() {
		ArrayList<String> city = new ArrayList<String>();
		city.add(cityCache.readLocal());
		city.addAll(cityCache.read());
		Spinner spinner = (Spinner)context.findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, city);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		return city;
	}
	public void add(String string) {
		ArrayList<String> UserCitys = cityCache.read();
		UserCitys.add(string);
		cityCache.save(UserCitys);
	}
	public void relocate() {
		cityCache.refreshLocal();
	}

}
