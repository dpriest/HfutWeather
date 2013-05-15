package com.example.hfutweather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hfutweather.db.DBManager;
import com.example.hfutweather.view.MyLetterListView;
import com.example.hfutweather.view.MyLetterListView.OnTouchingLetterChangedListener;

public class CityList extends Activity{

	private BaseAdapter adapter;
	private ListView mCityList;
	private TextView overlay;
	private MyLetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;
	private SQLiteDatabase database;
	private ArrayList<CityModel> mCityNames;
	private EditText ed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_list);
		
		mCityList = (ListView) findViewById(R.id.city_list);
		letterListView = (MyLetterListView)findViewById(R.id.cityLetterListView);
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_City_NUM, null);
		ed = (EditText) findViewById(R.id.EditText01);
		
		ed.addTextChangedListener(new CitySearchListener());
		mCityNames = getCityNames("");
		
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
		setAdapter(mCityNames);
		mCityList.setTextFilterEnabled(true);
		mCityList.setOnItemClickListener(new CityListOnItemClick());
	}
	
	class CityListOnItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
			CityModel cityModel = (CityModel)mCityList.getAdapter().getItem(pos);
			Intent cityIntent = new Intent();
			cityIntent.putExtra("name", cityModel.getCityName());
			cityIntent.putExtra("num", cityModel.getCityNum());
			setResult(RESULT_OK, cityIntent);
			finish();
			return ;
		}
	}

	private void setAdapter(ArrayList<CityModel> list) {
		if (list != null)
		{
			adapter = new ListAdapter(this, list);
			mCityList.setAdapter(adapter);
		}
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<CityModel> list;

		public ListAdapter(Context context, List<CityModel> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++)
			{
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).getProvince();
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).getProvince() : " ";
				if (!previewStr.equals(currentStr))
				{
					String name = list.get(i).getProvince();
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}

		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(list.get(position).getCityName());
			String currentStr = list.get(position).getProvince();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1).getProvince() : " ";
			if (!previewStr.equals(currentStr))
			{
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else
			{
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder
		{
			TextView alpha;
			TextView name;
		}

	}

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private ArrayList<CityModel> getCityNames(String cityFilter) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.city_list, menu);
		return true;
	}

	private class LetterListViewListener implements OnTouchingLetterChangedListener
	{

		@Override
		public void onTouchingLetterChanged(final String s)
		{
			if (alphaIndexer.get(s) != null)
			{
				int position = alphaIndexer.get(s);
				mCityList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}
	private class CitySearchListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			mCityNames = getCityNames(ed.getText().toString());
			setAdapter(mCityNames);
		}
		
	}
	private class OverlayThread implements Runnable
	{

		@Override
		public void run()
		{
			overlay.setVisibility(View.GONE);
		}

	}

//	@Override
//	public boolean onQueryTextChange(String newText) {
//		mStatusView.setText("Query = " + newText);
//		return false;
//	}
//
//	@Override
//	public boolean onQueryTextSubmit(String query) {
//		mStatusView.setText("Query = " + query + " : submitted");
//		return false;
//	}
}
