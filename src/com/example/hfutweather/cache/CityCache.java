package com.example.hfutweather.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.example.hfutweather.db.DBCity;



public class CityCache {
	private static final String CITYFILENAME = "citys.txt";
	private static final String LOCALCITYFILENAME = "localcitys.txt";
	private static final String DEFAULTCITY = "±±¾©";
	private Activity context;
	
	public CityCache(Activity context) {
		this.context = context;
	}
	public void save(ArrayList<String> citys) {
		try {
			FileOutputStream output = context.openFileOutput(CITYFILENAME, Context.MODE_WORLD_READABLE);
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
	public String readLocal() {
		File file = context.getFileStreamPath(LOCALCITYFILENAME);
		if(!file.exists()) {
			if (refreshLocal() == null ) {
				return DEFAULTCITY;
			}
		}
		try {
			FileInputStream input = context.openFileInput(LOCALCITYFILENAME);
			DataInputStream din = new DataInputStream(input);
			String localCity = din.readUTF();
			if (localCity.equals("")) {
				localCity = refreshLocal();
			}
			din.close();
			return localCity;
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return DEFAULTCITY;
	}
	public String refreshLocal() {
		String localCity = getLocation();
		DBCity dbCity = new DBCity(context);
		if (localCity == null) {
			return null;
		}
		localCity = dbCity.getDatabaseName(localCity);
		FileOutputStream output;
		try {
			output = context.openFileOutput(LOCALCITYFILENAME, Context.MODE_WORLD_READABLE);
			DataOutputStream dout = new DataOutputStream(output);
			dout.writeUTF(localCity);
			dout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return localCity;
	}
	public ArrayList<String> read() {
		ArrayList<String> citys = new ArrayList<String>();
		try {
			FileInputStream input = context.openFileInput(CITYFILENAME);
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
	
	private String getLocation() {
		Location mLocation = getLocation(context);
        
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
	        if (addresses.size() > 0) 
	            return addresses.get(0).getLocality();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public Location getLocation(Context context) {
		LocationManager locMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locMan
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}
	public ArrayList<HashMap<String, String>> readInMap() {
		ArrayList<HashMap<String, String>> citys = new ArrayList<HashMap<String, String>>();
		try {
			FileInputStream input = context.openFileInput(CITYFILENAME);
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
