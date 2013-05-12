package com.example.hfutweather.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.hfutweather.R;

public class DBManager {
	private final int BUFFER_SIZE = 400000;
	private static final String PACKAGE_NAME = "com.example.hfutweather";
	public static final String DB_City_NAME = "china_city_name.db";
	public static final String DB_City_NUM = "db_weather.db";
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME ; // ���·��
	private Context mContext;
	private SQLiteDatabase database;

	public DBManager(Context context)
	{
		this.mContext = context;
	}

	/**
	 * �����÷���
	 */
	public void openDataBase()
	{
		this.database = this.openDataBase(DB_PATH + "/" + DB_City_NAME, R.raw.china_city_name);
		this.database = this.openDataBase(DB_PATH + "/" + DB_City_NUM, R.raw.db_weather);
	}

	/**
	 * �����ݿ�
	 * 
	 * @param dbFile
	 * @return SQLiteDatabase
	 * @author sy
	 */
	private SQLiteDatabase openDataBase(String dbFile, int resource)
	{
		File file = new File(dbFile);
		// ��ʼ�����ݿ�
		if (!file.exists())
		{
			// // ��raw�е����ݿ��ļ������stream��
			InputStream stream = this.mContext.getResources().openRawResource(resource);
			try
			{

				// ����ȡ����stream ��д���data��
				FileOutputStream outputStream = new FileOutputStream(dbFile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = stream.read(buffer)) > 0)
				{
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
				stream.close();
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				return db;
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return database;
	}

	public void closeDatabase()
	{
		if (database != null && database.isOpen())
		{
			this.database.close();
		}
	}
}
