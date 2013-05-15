package com.example.hfutweather.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.hfutweather.CityModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBCity {
	
	private SQLiteDatabase database;

	public DBCity(Context context) {
		DBManager db = new DBManager(context);
		db.openDataBase();
		db.closeDatabase();
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_City_NUM, null);
	}

	public String getNumByName(String cityName) {
		Cursor cursor = database.rawQuery("SELECT city_num FROM citys where name = '" + cityName + "'", null);
		cursor.moveToPosition(0);
		return  cursor.getString(cursor.getColumnIndex("city_num"));
	}

	public String getDatabaseName(String localCity) {
		String cityName = localCity.substring(0, Math.min(localCity.length(), 2));
		Cursor cursor = database.rawQuery("SELECT name FROM citys where name like '%" + cityName + "%' order by _id asc limit 1", null);
		cursor.moveToPosition(0);
		return  cursor.getString(cursor.getColumnIndex("name"));
	}

	public ArrayList<CityModel> getCityNames(String cityFilter) {
		ArrayList<CityModel> names = new ArrayList<CityModel>();
		HashMap<Integer, String> provinceMap = new HashMap<Integer, String>();
		Cursor cursor = database.rawQuery("SELECT * FROM provinces order by _id", null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			provinceMap.put(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex("name")));
		}
		String sql = "";
		if (cityFilter.equals("")) {
			sql = "SELECT * FROM citys order by _id";
		} else {
			sql = "SELECT * FROM citys where name like '%"+ cityFilter +"%' order by _id";
		}
		cursor = database.rawQuery(sql, null);
		for (int i = 0; i <cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			CityModel cityModel = new CityModel();
			cityModel.setCityName(cursor.getString(cursor.getColumnIndex("name")));
			cityModel.setCityNum(cursor.getString(cursor.getColumnIndex("city_num")));
			cityModel.setProvince(provinceMap.get(cursor.getInt(cursor.getColumnIndex("province_id"))+1));
			names.add(cityModel);
		}
		return names;
	}

}
