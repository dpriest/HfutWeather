package com.example.hfutweather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.hfutweather.cache.CityCache;

public class DelCityActivity extends Activity {

	private CityCache cityCache;
	private ArrayList<String> city;
	private ListView lv;
	private CityListAdapter adapter;
	public Context mContext;
	protected List<Integer> listItemID = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_del_city);
		mContext = getApplicationContext();
		lv = (ListView) findViewById(R.id.city_list);
		cityCache = new CityCache(DelCityActivity.this);
		city = cityCache.read();
		adapter = new CityListAdapter(city);
		lv.setAdapter(adapter);
		Button btnSubmit = (Button)findViewById(R.id.submit);
		btnSubmit.setOnClickListener(oclbtnSubmit);
	}
	
	private Button.OnClickListener oclbtnSubmit = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			listItemID .clear();
			for(int i=adapter.mChecked.size()-1;i>=0;i--){  
                if(adapter.mChecked.get(i)){  
                    city.remove(i);
                }
            }
			cityCache.save(city);
			finish();
			return ;
		}
	};
	
	class CityListAdapter extends BaseAdapter {
		List<Boolean> mChecked;
		List<String> city;
		HashMap<Integer, View> map = new HashMap<Integer, View>();
		
		public CityListAdapter(List<String> city) {
			this.city = new ArrayList<String>();
			this.city = city;
			mChecked = new ArrayList<Boolean>();
			for(int i=0;i<city.size();i++) {
				mChecked.add(false);
			}
		}
		@Override
		public int getCount() {
			return city.size();
		}
		@Override
		public Object getItem(int position) {
			return city.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = null;
			if (map.get(position) == null) {
				LayoutInflater mInflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = mInflater.inflate(R.layout.del_city_item, null);
				holder = new ViewHolder();
				holder.selected = (CheckBox)view.findViewById(R.id.row_chbox);
				holder.city = (TextView)view.findViewById(R.id.row_tv);
				final int p = position;
				map.put(position, view);
				holder.selected.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox)v;
						mChecked.set(p, cb.isChecked());
					}
				});
				view.setTag(holder);
			} else {
				view = map.get(position);
				holder = (ViewHolder)view.getTag();
			}
			holder.selected.setChecked(mChecked.get(position));
			holder.city.setText(city.get(position));
			return view;
		}
		
	}
	
	static class ViewHolder {
		CheckBox selected;
		TextView city;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.del_city, menu);
		return true;
	}

}
