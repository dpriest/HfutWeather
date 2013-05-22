package com.example.hfutweather.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hfutweather.R;

public class WeatherAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<HashMap<String, String>> mData;
	private Context context;

	public WeatherAdapter(Context context, List<HashMap<String, String>> arrayList) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.mData = arrayList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public final class ViewHolder {
		public TextView date;
		public TextView tvCondition;
		public TextView tvTemp;
		public ImageView tvWeatherImage;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.weatherlist, null);
			holder.date = (TextView)convertView.findViewById(R.id.date);
			holder.tvCondition = (TextView)convertView.findViewById(R.id.tvCondition);
			holder.tvTemp = (TextView)convertView.findViewById(R.id.tvTemp);
			holder.tvWeatherImage =(ImageView)convertView.findViewById(R.id.list_image); // thumb image
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.date.setText((String)mData.get(position).get("date"));
		holder.tvCondition.setText((String)mData.get(position).get("tvCondition"));
		holder.tvTemp.setText((String)mData.get(position).get("tvTemp"));

	    //Setting an image
	    String uri = "drawable/img"+ mData.get(position).get("img");
	    int imageResource = context.getApplicationContext().getResources().getIdentifier(uri, null, context.getApplicationContext().getPackageName());
	    Drawable image = context.getResources().getDrawable(imageResource);
	    holder.tvWeatherImage.setImageDrawable(image);
		
		return convertView;
	}
}
