package ru.originart.donnews;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity  {
  public static ListView tvLstViw ;
  private static Context CONTXT ;
  private final int url = R.string.main_url_req ;
  private int index, top, count_news ;
	
  @Override
  protected void onCreate( Bundle savedInstanceState ) {
	super.onCreate( savedInstanceState ) ;
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView( R.layout.activity_main ) ;
	
	tvLstViw = ( ListView ) findViewById( android.R.id.list ) ;
	CONTXT = MainActivity.this ;
	//DonNewsReq.showNews( CONTXT ) ;
	count_news = DonNewsReq.getCountNews() ; 
	new DonNewsReq( CONTXT ).execute( getResources().getString( this.url ) ) ;
	
	
	tvLstViw.setOnItemClickListener(new OnItemClickListener() {
	  public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		TextView a = ( TextView ) view.findViewById( R.id.ArtHdr ) ;
		id = ( Long ) a.getTag() ;
		  
		NewsDataSource datasource = new NewsDataSource( CONTXT ) ;
		datasource.open() ;
		Cursor result = datasource.selectCurrentNews( id ) ;
	    datasource.close() ;
	    String href = result.getString( 2 ) ;
	    result.close() ;
	    count_news = DonNewsReq.getCountNews() ;
	    Intent i = new Intent( CONTXT, ArticleActivity.class ) ;
		i.putExtra( MySQLiteHelper.COLUMN_HREFART, href ) ;
		i.putExtra( MySQLiteHelper.COLUMN_ID, id ) ;
		i.putExtra( "position", position ) ;
		i.putExtra( "count_news", count_news ) ;
	    startActivity( i ) ;
	    //finish();// Завершить текущую активность.
	  }
	});
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
	MenuItem mi_1 = menu.add( 0, 1, 0, R.string.app_refresh ) ;
	//mi_1.set
	mi_1.setIcon(R.drawable.navigation_refresh) ;
	  
	MenuItem mi_2 = menu.add( 0, 1, 0, R.string.action_settings ) ;
	mi_2.setIntent( new Intent( this, SettingsActivity.class )) ;
	mi_2.setIcon(R.drawable.action_settings) ;
	
	return super.onCreateOptionsMenu( menu ) ;
  }
	
  public void clickRefresh ( View view ) {
	new DonNewsReq( CONTXT ).execute( getResources().getString( this.url ) ) ;	
  }
  
  @Override
  protected void onPause() {
	super.onPause();
	index = tvLstViw.getFirstVisiblePosition();
	View v = tvLstViw.getChildAt(0);
	top = (v == null) ? 0 : v.getTop();
  }

  @Override
  protected void onResume() {
	super.onResume();
	DonNewsReq.showNews( CONTXT ) ;
	tvLstViw.setSelectionFromTop(index, top);
  }

  @Override
  protected void onStart() {
	super.onStart();
  }

  @Override
  protected void onStop() {
	super.onStop();
  }
	
  @Override
  protected void onDestroy() {
	super.onDestroy();
  }
}
