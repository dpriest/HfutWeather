package com.example.hfutweather.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBCity {
	
	private SQLiteDatabase database;

	public DBCity() {
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_City_NUM, null);
	}

	public String getNumByName(String cityName) {
		Cursor cursor = database.rawQuery("SELECT city_num FROM citys where name = '" + cityName + "'", null);
		cursor.moveToPosition(0);
		return  cursor.getString(cursor.getColumnIndex("city_num"));
	}

}
