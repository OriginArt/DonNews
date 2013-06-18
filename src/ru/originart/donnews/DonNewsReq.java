package ru.originart.donnews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.helper.HttpConnection.Response;
import org.jsoup.select.Elements;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

class DonNewsReq extends AsyncTask <String, Integer, String[]> {
  private static final String LOG_TAG = "LOG_DONNEWS" ;
  private ProgressBar pb ;
  private Context CONTXT ;
  private ImageView ir ;
  private MainActivity mac ;
  private int TOAST_LENGTH = Toast.LENGTH_LONG ;
  private static int count_news; 
  private String IS_ERR ;
  private Resources macr ;
  public static List<String> imgCnt = new ArrayList<String>();
  public static long[] ids ;
  
  public DonNewsReq ( Context context ) {
	CONTXT = context ;
	mac = ((MainActivity)CONTXT) ; 
	macr = mac.getResources() ;
  }
  
  public static int getCountNews () {
	return count_news ;  
  }
  
  public Object[] checkExists( String[] href ) {
	NewsDataSource ds = new NewsDataSource( CONTXT ) ;
	List <String> newHREF = new ArrayList<String>() ;
	Cursor news ;
	int szbd, szrq = href.length, i = 0 ;
	Object[] a = href ;
	
	ds.open();
	news = ds.getAllNews();
	ds.close();
	szbd = news.getCount();
	
	if ( szbd != 0  ) {
		while ( !news.isLast() ) {
			if ( !href[i].equals( news.getString( 2 )) ) {
				if ( ( i+1 ) != szrq ) i ++ ;
				else { newHREF.add( href[i] ) ;  Log.i(LOG_TAG,"checkExists | " + href[i]  ); i = 0; news.moveToNext() ; }
			}
			else news.moveToNext() ;
			
		}
	}
	Log.i(LOG_TAG,"checkExists | "+newHREF.size()  );
	a = newHREF.toArray() ; 
	news.close() ;
	return a ;
  }
  
  
  protected String[] doInBackground( String... url ) {
	String[] result = null, hr, newsContentRefs_new ;
	int i = 0, j = 0, sz = 0, end = 0, m = 0, sizeNH = 0, ncr_cnt = 0 ; //k = 0
	Elements newsHeaders = null, newsContent, newsDate, newsContentRefs_old ;
	org.jsoup.nodes.Document doc = null ;
	Object[] nhr = null ;
	HttpConnection.Response res ;
	String newsContentString, cnt_ref ;
	final int    news_timeout     = Integer.parseInt( macr.getString ( R.string.news_timeout ));
	final String usr_agent        = macr.getString( R.string.usr_agent         ),
		         news_content     = macr.getString( R.string.news_content      ),
   		         news_content_alt = macr.getString( R.string.news_content_alt  ),
		         news_date        = macr.getString( R.string.news_date         ),
		         news_headers     = macr.getString( R.string.news_headers      ),
		         news_content_ref = macr.getString( R.string.news_content_ref  ),
		         news_hrefs       = macr.getString( R.string.news_hrefs        ),
		         main_url_req	  =	macr.getString( R.string.main_url_req      );
	IS_ERR = "DonNews_OK" ;
	
	try {
      res = ( Response ) Jsoup.connect( url[0] ).ignoreHttpErrors( true ).followRedirects( true ).userAgent( usr_agent ).timeout( news_timeout ).execute();
	  if ( res.statusCode() != 200 ) {
		Log.i(LOG_TAG,"Нет возможности получить данные от сервера. HTTP ответ " + res.statusCode() );
		IS_ERR = "DonNews_ERROR_HTTP_RESPONSE_CODE" ;
		sz = 0 ;
	  } else {
	    doc = res.parse();
	    newsHeaders = doc.select( news_headers ) ;
	    sizeNH = newsHeaders.size() ;
	    sz = sizeNH * 4 ;
	  }
	} catch ( IOException en) {
	  Log.i(LOG_TAG,"Нет возможности подключиться.");
	  IS_ERR = "DonNews_ERROR_COULD_NOT_CONNECT_WHEN_REQUEST_HEADERS_REFS" ;
	  en.printStackTrace();
	  sz = -1 ;
	}	
	
	if ( sz > 0 ) {
	  hr = new String[ sizeNH ] ;
	  i = 0 ;
	  while ( i < ( sizeNH ) ) {
		hr[i] = newsHeaders.get( i ).attr( news_hrefs ).toString();
		i++;
	  }
	  if ( hr.length != 0 ) { //Статьи загружены, но мы не знаем есть ли новые статьи 
		nhr = checkExists( hr ) ;//смотрим - есть ли новые
		end = nhr.length ;
	  }
	  else end = 0 ;
	  
	  
	  if ( end > 0 ) {
	    sz = end * 4 ;
	  } else {//Если новых статей нет - должны взять старые из базы и написать что новых статей нет
		IS_ERR = "DonNews_NOTE_NO_FRESH_ARTICLES" ;
	  }
	  i = 0 ;
	  result = new String [ sz ] ;
	  while ( i < sz ) { 
		result[i] = htmlToText ( newsHeaders.get(j).text() ) ; 
		i++;
		if ( nhr.length > 0 ) result[i] = htmlToText ( nhr[j].toString() ) ;
		else result[i] = htmlToText ( hr[j] ) ;
		i++ ;
		//k = i ;
		try {
		  res = (Response) Jsoup.connect( result[i-1] ).ignoreHttpErrors( true ).followRedirects( true ).userAgent( usr_agent ).timeout( news_timeout ).execute();
		  if ( res.statusCode() != 200 ) {
			Log.i(LOG_TAG,"Нет возможности получить данные от сервера. HTTP ответ " + res.statusCode() );
			IS_ERR = "DonNews_ERROR_HTTP_RESPONSE_CODE" ;
			sz = 0 ;
		  } else {
		    doc = res.parse();
		    
		
		    newsContent = doc.select ( news_content ) ;
		    if ( newsContent.size() == 0 )  newsContent = doc.select ( news_content_alt ) ;
		    
		    
		    imgCnt.add(doc.select("#main-photo img").attr("src"));
		    
		    newsContentRefs_old = doc.select ( news_content_ref ) ;
		    ncr_cnt = newsContentRefs_old.size() ;
		    newsContentRefs_new = new String [ ncr_cnt ] ;
		    newsContentString = newsContent.toString() ;
		    while ( m < ncr_cnt ) {
		      cnt_ref = newsContentRefs_old.get(m).attr(news_hrefs).toString() ;
		      if ( !cnt_ref.startsWith("http://") ) {
		    	newsContentRefs_new[m] = main_url_req + cnt_ref ;
		    	newsContentString.replaceAll( cnt_ref, newsContentRefs_new[m] ) ;
		    	Log.i(LOG_TAG,newsContentRefs_new[m]) ;
		      }
		      m++;
		    }
		    result[i] = htmlToText ( newsContentString ) ;
		    i++;
		    newsDate = doc.select ( news_date ) ;
		    result[i] = htmlToText ( newsDate.text() ) ;
		    i++;
		  }
		} catch (IOException e) {
		  //i = k - 2 ;
			i = sz ;
		  Log.i(LOG_TAG,"Нет возможности подключиться. Неизвестная ошибка при запросе новости «" + result[i] + "»") ;
		  IS_ERR = "DonNews_ERROR_UNEXPECTED_ERROR_WHEN_REQUEST_ARTICLE" ;
		}
		if ( i%4 == 0 ) j++ ;
		IS_ERR = "DonNews_OK" ;
	  }
	}
	else {
	  if ( sz == 0 ) IS_ERR = "DonNews_ERROR_NOTHING_WAS_LOADED" ;
	  result = new String [ 1 ] ;
	  result [0] = "";
	}
	return result ;
  }
  
  
  private String htmlToText ( String a ) {
	a = a.replaceAll("&nbsp;"," ").replaceAll("</p>","\n\n").replaceAll("&deg;","°").replaceAll("&mdash;","—").replaceAll("&laquo;","«").replaceAll("&raquo;","»").replaceAll("\n{3,}","\n\n");
	return a ;
  }
  
  
  @Override
  protected void onPreExecute() {
	pb = ( ProgressBar ) mac.findViewById( R.id.PrgBar ) ;
	pb.setVisibility( View.VISIBLE ) ;
	ir = ( ImageView )   mac.findViewById( R.id.imgRefr ) ;
	ir.setVisibility( View.INVISIBLE ) ;
  }
  
  
  protected void onPostExecute ( String[] result ) {
	int sz = result.length, i = sz, j = 0 ;
	ids = new long [sz] ;
	NewsDataSource ds = new NewsDataSource( CONTXT ) ;
	
	if ( IS_ERR == "DonNews_OK" ) {
	  i = i - 1;
	  ds.open() ;	  
	  while ( i >= 0 ) {
		ids[j] = ds.insertNews( new String[] { result[i-3], result[i-2], result[i-1], result[i], "0" } ) ;
		 Log.i(LOG_TAG,""+ids[j]) ;
		j++;
		i = i - 4 ;
	  }
	  ds.close() ;
	  if ( getPrefs( CONTXT, "lod_pictures" ).equals("true")) { 
			new ImageLoader( CONTXT ).execute();//грузим картинки, а потом в ImageLoader в PostExecute показываем загруженное
			
		}
		else {
			showNews ( CONTXT ) ; //показываем сразу, если не надо грузить картинки
			Toast.makeText( CONTXT, macr.getString( R.string.DonNews_OK ), TOAST_LENGTH ).show() ;
		}
	} else if ( IS_ERR == "DonNews_ERROR_HTTP_RESPONSE_CODE" ) {
	  Toast.makeText( CONTXT,  macr.getString( R.string.DonNews_ERROR_HTTP_RESPONSE_CODE ), TOAST_LENGTH ).show() ;
	} else if ( IS_ERR == "DonNews_ERROR_COULD_NOT_CONNECT_WHEN_REQUEST_HEADERS_REFS" ) {
	  Toast.makeText( CONTXT,  macr.getString( R.string.DonNews_ERROR_COULD_NOT_CONNECT_WHEN_REQUEST_HEADERS_REFS), TOAST_LENGTH ).show() ;
	} else if ( IS_ERR == "DonNews_NOTE_NO_FRESH_ARTICLES" ) {
	  Toast.makeText( CONTXT, macr.getString( R.string.DonNews_NOTE_NO_FRESH_ARTICLES), TOAST_LENGTH ).show() ;
	} else if ( IS_ERR == "DonNews_ERROR_UNEXPECTED_ERROR_WHEN_REQUEST_ARTICLE" ) {
	  Toast.makeText( CONTXT, macr.getString( R.string.DonNews_ERROR_UNEXPECTED_ERROR_WHEN_REQUEST_ARTICLE), TOAST_LENGTH ).show() ;
	} else if ( IS_ERR == "DonNews_ERROR_NOTHING_WAS_LOADED" ) {
	  Toast.makeText( CONTXT, macr.getString( R.string.DonNews_ERROR_NOTHING_WAS_LOADED), TOAST_LENGTH ).show() ;
	}
	
	
	pb.setVisibility( View.INVISIBLE ) ;
	ir.setVisibility( View.VISIBLE ) ;
	
  }
  
  public static String getPrefs( Context context, String name ) {
	String result = null ;
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
    if ( name.equals("art_count") ) {
      result = prefs.getString( name, "1");//((MainActivity)context).getResources().getString( R.string.articles_limit )) ;
    } else if ( name.equals("lod_pictures") ) {
      result = String.valueOf(prefs.getBoolean(name, false));
    }
	return result ;
  }
  
  public static void showNews ( Context STCONTXT ) {
	  Log.i("LOG_DONNEWS","showNews");
	long[] aids ;
	int j = 0  ;
	int [] wsrd ;
	Cursor results = null ;
	String[] data ;
	NewsDataSource ds = new NewsDataSource( STCONTXT ) ;
	
	ds.open() ;
	results = ds.getAllNews();
	ds.close() ;
	
	count_news = results.getCount() ;
	
	data = new String[ count_news ] ;
	aids = new long  [ count_news ] ;
	wsrd = new int   [ count_news ] ;
	while ( !results.isAfterLast() ) {
	  data [j] = results.getString ( 1 ) ;
	  aids [j] = results.getLong   ( 0 ) ;
	  wsrd [j] = results.getInt    ( 5 ) ;
	  results.moveToNext() ; 
	  j++ ;
	}
	results.close() ;
	((MainActivity)STCONTXT).setListAdapter( new MyCustomAdapter( STCONTXT, R.layout.layout_article_row, data, aids, wsrd ) ) ;
  }
}