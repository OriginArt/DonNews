package ru.originart.donnews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection.Response;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ImageLoader extends AsyncTask <String, Integer, Integer> {
  private Context CONTXT ;
  private ImageView ir ;
  private ProgressBar pb ;
  private MainActivity mac ;
  private Resources macr ;
  private int TOAST_LENGTH = Toast.LENGTH_LONG, imgCnt = 0 ;
  private String IS_ERR ;
  
  public ImageLoader(Context context) {
	CONTXT = context ; 
	mac = ((MainActivity)CONTXT) ;
	macr = mac.getResources() ;
	imgCnt = DonNewsReq.imgCnt.size() ;
  }
	 
  protected Integer doInBackground( String... url ) {
	int i = 0, fp = -1, lp = -1 ;
	if ( imgCnt > 0 ) IS_ERR = "DonNews_OK" ;
	else IS_ERR = "DonNews_EMPTY_IMGS_ARAY" ;
	try {
      while ( i < imgCnt ) {
    	fp = DonNewsReq.imgCnt.get(i).indexOf("&file=") ;
    	lp = DonNewsReq.imgCnt.get(i).indexOf("&w=") ;
    	if ((lp != -1 ) & (fp != -1)) {
    	  Response resultImageResponse = ( Response ) Jsoup.connect("http://donnews.ru/?option=com_dnnews&task=imageTumb"+DonNewsReq.imgCnt.get(i).substring(0,lp)+"&w=320&h=200&m=adapt").ignoreContentType(true).execute();
    	  if ( resultImageResponse.statusCode() != 200 ) {
    			IS_ERR = "DonNews_ERROR_HTTP_RESPONSE_CODE" ;
    	  } else {
      	    FileOutputStream out = null;
    	    File storagePath = new File ( Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator + CONTXT.getPackageName() ) ;
    	    storagePath.mkdirs();
    	    out = (new FileOutputStream(new File(storagePath,"img"+(DonNewsReq.imgCnt.size() - DonNewsReq.ids[i] + 1)+".jpg")));
            out.write(resultImageResponse.bodyAsBytes());
      	    out.close();
    	  }
    	}
    	i++;
      }	
	} catch (IOException e) {
		IS_ERR = "DonNews_ERROR_WHILE_LOAD_IMAGES" ;
		e.printStackTrace();
	}
	return 1 ;
  }
 
  protected void onPreExecute() {
	pb = ( ProgressBar ) mac.findViewById( R.id.PrgBar ) ;
	pb.setVisibility( View.VISIBLE ) ;
	ir = ( ImageView )   mac.findViewById( R.id.imgRefr ) ;
	ir.setVisibility( View.INVISIBLE ) ;
  }
  
  protected void onPostExecute ( Integer result ) {
	DonNewsReq.showNews ( CONTXT ) ;
	pb.setVisibility( View.INVISIBLE ) ;
	ir.setVisibility( View.VISIBLE ) ;
	if ( IS_ERR == "DonNews_OK" ) Toast.makeText( CONTXT, macr.getString( R.string.DonNews_OK ), TOAST_LENGTH ).show() ;
	else if ( IS_ERR == "DonNews_ERROR_WHILE_LOAD_IMAGES" ) Toast.makeText( CONTXT, macr.getString( R.string.DonNews_ERROR_WHILE_LOAD_IMAGES ), TOAST_LENGTH ).show() ;
  }
}
