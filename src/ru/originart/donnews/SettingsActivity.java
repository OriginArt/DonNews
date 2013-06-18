package ru.originart.donnews;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {
  private static Context CONTXT ;
  private int TOAST_LENGTH = Toast.LENGTH_LONG ; 

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
    CONTXT = this;
    addPreferencesFromResource(R.xml.preference);//для 10 api надо использовать если поддерживать с 11 - то вроде все хорошо
	    
    Preference del_articles = (Preference) findPreference("del_articles");
    del_articles.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	  public boolean onPreferenceClick(Preference preference) {
	    MySQLiteHelper msh = new MySQLiteHelper ( CONTXT ) ;
		SQLiteDatabase db = msh.getWritableDatabase() ;
		msh.Drop( db ) ;
		MainActivity.tvLstViw.setAdapter( null ) ;
		Toast.makeText( CONTXT, getResources().getString( R.string.del_articles_m ), TOAST_LENGTH ).show() ;
		return false;
      }
    });
    
    
    Preference del_pictures = (Preference) findPreference("del_pictures");
    del_pictures.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	  public boolean onPreferenceClick(Preference preference) {
        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator + CONTXT.getPackageName());
        File[] lstFile;
        if(path.exists()){
          lstFile = path.listFiles();
          for(int i =0; i<lstFile.length;i++){
            File file = lstFile[i];
            file.delete();
          }
          path.delete(); 
        }
        Toast.makeText( CONTXT, getResources().getString( R.string.del_pictures_m ), TOAST_LENGTH ).show() ;
		return false;
	  }
    });
    
    Preference abt_program = (Preference) findPreference("abt_program");
    abt_program.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	  public boolean onPreferenceClick(Preference preference) {
		Intent i = new Intent( CONTXT, AboutPrActivity.class ) ;
		startActivity( i ) ;
		//finish();  
		return false;
      }
    });
  }
}
