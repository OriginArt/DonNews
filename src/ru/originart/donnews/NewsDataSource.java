package ru.originart.donnews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NewsDataSource {
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { 
			                         MySQLiteHelper.COLUMN_ID,
	                                 MySQLiteHelper.COLUMN_HEADLINE,
	                                 MySQLiteHelper.COLUMN_HREFART,
	                                 MySQLiteHelper.COLUMN_ARTICLE,
	                                 MySQLiteHelper.COLUMN_DATEPUB,
	                                 MySQLiteHelper.COLUMN_WREADED };
	  private final int articles_limit ;

	  public NewsDataSource( Context context ) {
	    dbHelper = new MySQLiteHelper( context ) ;
	    articles_limit = Integer.parseInt( DonNewsReq.getPrefs( context, "art_count" )) ;
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public long insertNews(String[] data) {
		long insertId = 0 ;  
	    ContentValues values = new ContentValues();
	    
	    values.put(MySQLiteHelper.COLUMN_HEADLINE, data[0]);
	    values.put(MySQLiteHelper.COLUMN_HREFART,  data[1]);
	    values.put(MySQLiteHelper.COLUMN_ARTICLE,  data[2]);
	    values.put(MySQLiteHelper.COLUMN_DATEPUB,  data[3]);
	    values.put(MySQLiteHelper.COLUMN_WREADED,  data[4]);
	    
	    MySQLiteHelper.createTable(database); 
	    insertId = database.insert(MySQLiteHelper.TABLE_NEWS, null, values);
	    Log.w("log","insertNews "+insertId);
	    return insertId ;
	  }
	  
	  public void updateNews(long rowID, String[] data, String[] column, String table) {
		ContentValues values = new ContentValues();
		int sz = data.length, i = 0 ;
		while ( i < sz ) {
		  values.put(column[i], data[i]);
		  i++;
		}
		database.update(table, values, "_id=" + rowID, null);
	  }
	  
	  public Cursor selectCurrentNews(long id) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NEWS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	  }
	  
	  public DonNewsDB selectAllNews() {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NEWS, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		DonNewsDB newNews = cursorToNews(cursor);
		cursor.close();
		return newNews;
	  }
	  
	  public Cursor getAllNews() {
		Cursor cursor = database.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"+MySQLiteHelper.TABLE_NEWS+"'",null) ;
		cursor.moveToFirst();
		if (cursor.getInt(0) == 0 ) {
		  MySQLiteHelper.createTable(database);
		}
	    cursor = database.query(MySQLiteHelper.TABLE_NEWS, allColumns, null, null, null, null, MySQLiteHelper.COLUMN_ID + " DESC LIMIT " + articles_limit );
	    cursor.moveToFirst();
		return cursor;
	  }

	  private DonNewsDB cursorToNews(Cursor cursor) {
		DonNewsDB data = new DonNewsDB();
		data.setId       ( cursor.getLong(0)   );
		data.setHeadline ( cursor.getString(1) );
		data.setHrefart  ( cursor.getString(1) );
		data.setArticle  ( cursor.getString(1) );
		data.setDatepub  ( cursor.getString(1) );
		data.setWreaded  ( cursor.getInt(1)    );
	    return data;
	  }
	}