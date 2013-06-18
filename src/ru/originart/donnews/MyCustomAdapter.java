package ru.originart.donnews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyCustomAdapter extends ArrayAdapter<String> {
  private String[] result ; 
  private Context CONTXT;
  private long[] aids ;
  private int[] wsrd ;
  private MainActivity mac ;
  
  public MyCustomAdapter(Context context, int textViewResourceId, String[] objects, long[] aids, int[] wsrd) {
    super(context, textViewResourceId, objects);
  	result = objects;
  	CONTXT = context;
  	this.aids =  aids ;
  	this.wsrd = wsrd ;
  	mac = ((MainActivity)CONTXT) ;
  }
  	 
  @Override
  public View getView( int position, View convertView, ViewGroup parent ) {
  	View row = convertView ;
  	Resources macr = mac.getResources() ;
  	TextView label ;
  	final String color_black_text       = macr.getString( R.string.color_black_text       ),
  		         color_white_background = macr.getString( R.string.color_white_background ),
  		         color_gray_text        = macr.getString( R.string.color_gray_text        ),
  	             color_gray_backgroung  = macr.getString( R.string.color_gray_backgroung  );
  	
  	if ( row == null ) {
  	  LayoutInflater inflater = ((MainActivity)CONTXT).getLayoutInflater() ;
  	  row = inflater.inflate( R.layout.layout_article_row, parent, false ) ;
  	}
  	label = ( TextView ) row.findViewById( R.id.ArtHdr ) ;
  	label.setText( result[position] ) ;
  	label.setTag( aids[position] ) ;
  	
  	
  	
  	if ( ((Long)label.getTag() == aids[position] ) & ( wsrd[position] == 1 )) {
  	  label.setTextColor( Color.parseColor(color_gray_text) ) ;
  	  label.setBackgroundColor( Color.parseColor(color_gray_backgroung) ) ;
  	} else {
  	  label.setTextColor( Color.parseColor(color_black_text) ) ;
  	  label.setBackgroundColor( Color.parseColor(color_white_background) ) ;
  	}
  	
    return row ;
  }
}