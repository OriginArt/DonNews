package ru.originart.donnews;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ArticleActivity extends Activity {
  private TextView tvArtTxt, tvArtHdr, tvArtDat, tvArtCnt ;
  private static final String LOG_TAG = "LOG_DONNEWS" ;
  public long rowID = 0 ;	
  public int pos = 0 ;
  private int count_news ; 
  private Context CONTXT ;
  ArticleActivity ACONTXT ;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    
    
    
    
    
    LayoutInflater inflater = LayoutInflater.from(this);
    List<View> pages = new ArrayList<View>();
    
    View page = inflater.inflate(R.layout.layout_article, null);
    TextView textView = (TextView) page.findViewById(R.id.ArtHdr);
    textView.setText("Страница 1");
    pages.add(page);
    
    page = inflater.inflate(R.layout.layout_article, null);
    textView = (TextView) page.findViewById(R.id.ArtHdr);
    textView.setText("Страница 2");
    pages.add(page);
    
    page = inflater.inflate(R.layout.layout_article, null);
    textView = (TextView) page.findViewById(R.id.ArtHdr);
    textView.setText("Страница 3");
    pages.add(page);
    
    SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(pages);
    ViewPager viewPager = new ViewPager(this);
    viewPager.setAdapter(pagerAdapter);
    viewPager.setCurrentItem(1); 
    setContentView(viewPager);
    
    
    
    
    
    
    
    
    
    //setContentView(R.layout.layout_article);
    //getActionBar().setDisplayHomeAsUpEnabled(true);
    String[] data = new String [1], clmn = new String [1] ;
    CONTXT = this;
    ACONTXT = (ArticleActivity)CONTXT ; 
    setTxtV() ;
    rowID      = getIntent().getExtras().getLong(MySQLiteHelper.COLUMN_ID);//используется в DonNewsReqArt
    pos        = getIntent().getExtras().getInt("position") + 1 ;
    count_news = getIntent().getExtras().getInt("count_news") ;
	data[0] = "1" ;
	clmn[0] = MySQLiteHelper.COLUMN_WREADED ;
    
    
    NewsDataSource ds = new NewsDataSource(CONTXT);
	ds.open();
	Cursor result =  ds.selectCurrentNews(rowID);
	ds.updateNews(rowID, data, clmn, MySQLiteHelper.TABLE_NEWS);
	ds.close();
	//setTxtVDt(result);
	result.close() ;
	
  }
  
  public TextView[] setTxtV() {
	tvArtTxt = (TextView) ACONTXT.findViewById(R.id.ArtTxt);
	tvArtHdr = (TextView) ACONTXT.findViewById(R.id.ArtHdr);
	tvArtDat = (TextView) ACONTXT.findViewById(R.id.ArtDat);
	tvArtCnt = (TextView) ACONTXT.findViewById(R.id.ArtCnt);
	TextView[] result = new TextView[4] ;
	result[0] = tvArtTxt ;
	result[1] = tvArtHdr ;
	result[2] = tvArtDat ;
	result[3] = tvArtCnt ;
	
	return result;
  }
  
  public void setTxtVDt( Cursor results ) {
    tvArtHdr.setText( results.getString(1) );
	tvArtDat.setText( results.getString(4) );
	tvArtTxt.setLinksClickable( true );
	tvArtTxt.setMovementMethod( LinkMovementMethod.getInstance() );
	tvArtTxt.setText( Html.fromHtml( results.getString(3) ) );
	tvArtCnt.setText( pos + " из " + count_news );
	
	/*
	ImageView tvImgInArt = (ImageView) ACONTXT.findViewById(R.id.imgInArt);
	File imgFile = new  File("/sdcard/Android/data/" + ACONTXT.getPackageName() + "/img"+results.getInt(0)+".jpg");
	Log.i("LOG_DONNEWS","Activity   img"+results.getInt(0)+".jpg");
	Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	tvImgInArt.setImageBitmap(myBitmap);
	*/
  }
  
  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
	MenuItem mi = menu.add( 0, 1, 0, R.string.action_settings );
	mi.setIntent(new Intent( this, SettingsActivity.class ));
	return super.onCreateOptionsMenu( menu );
  }
  
  @Override
  protected void onPause() {
	super.onPause();
	Log.i(LOG_TAG,"onPause"+" ArticleActivity");
  }

  @Override
  protected void onResume() {
	super.onResume();
	Log.i(LOG_TAG,"onResume"+" ArticleActivity");
  }

  @Override
  protected void onStart() {
	super.onStart();
	Log.i(LOG_TAG,"onStart"+" ArticleActivity");
  }

  @Override
  protected void onStop() {
	super.onStop();
	finish();
	Log.i(LOG_TAG,"onStop"+" ArticleActivity");
  }
	
  @Override
  protected void onDestroy() {
	super.onDestroy();
	Log.i(LOG_TAG,"onDestroy"+" ArticleActivity");
  } 
}