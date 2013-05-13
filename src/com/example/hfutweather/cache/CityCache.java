package com.example.hfutweather.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;



public class CityCache {
	private static final String FILENAME = "citys.txt";
	private Activity context;
	
	public CityCache(Activity context) {
		this.context = context;
	}
	public void save(ArrayList<String> citys) {
		try {
			FileOutputStream output = context.openFileOutput(FILENAME, Context.MODE_WORLD_READABLE);
			DataOutputStream dout = new DataOutputStream(output);
			dout.writeInt(citys.size());
			for(String city : citys)
				dout.writeUTF(city);
			dout.flush();
			dout.close();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
	
	public ArrayList<String> read() {
		ArrayList<String> citys = new ArrayList<String>();
		try {
			FileInputStream input = context.openFileInput(FILENAME);
			DataInputStream din = new DataInputStream(input);
			int sz = din.readInt();
			for (int i = 0; i < sz; i++) {
				String line = din.readUTF();
				citys.add(line);
			}
			din.close();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return citys;
	}
	
	public ArrayList<HashMap<String, String>> readInMap() {
		ArrayList<HashMap<String, String>> citys = new ArrayList<HashMap<String, String>>();
		try {
			FileInputStream input = context.openFileInput(FILENAME);
			DataInputStream din = new DataInputStream(input);
			int sz = din.readInt();
			for (int i = 0; i < sz; i++) {
				HashMap<String, String> tempHashMap = new HashMap<String, String>();
				String line = din.readUTF();
				tempHashMap.put("city", line);
				citys.add(tempHashMap);
			}
			din.close();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return citys;
	}
}
