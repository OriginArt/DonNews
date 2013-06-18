package ru.originart.donnews;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	  public static final String TABLE_NEWS      = "donnews"  ;
	  public static final String COLUMN_ID       = "_id"      ;
	  public static final String COLUMN_HEADLINE = "headline" ;
	  public static final String COLUMN_HREFART  = "hrefart"  ;
	  public static final String COLUMN_ARTICLE  = "article"  ;
	  public static final String COLUMN_DATEPUB  = "datepub"  ;
	  public static final String COLUMN_WREADED  = "wreaded"  ;

	  private static final String DATABASE_NAME = "donnews_db.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
	      + TABLE_NEWS + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " 
	      + COLUMN_HEADLINE + " text not null,"
	      + COLUMN_HREFART  + " text unique not null,"
	      + COLUMN_ARTICLE  + " text not null,"
	      + COLUMN_DATEPUB  + " text not null,"
	      + COLUMN_WREADED  + " integer);";

	  public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	  
	  public static void createTable(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		  createTable(database);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
	    onCreate(db);
	  }
	  
	  public void Drop(SQLiteDatabase db) {
		  db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
	  }

	} 