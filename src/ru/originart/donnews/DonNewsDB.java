package ru.originart.donnews;

import android.util.Log;

public class DonNewsDB {
  private long id;
  private String headline, hrefart, article, datepub;
  private int wreaded;
  private static final String LOG_TAG = "LOG_DONNEWS" ;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }
	  
  public String getHrefart() {
	return hrefart;
  }

  public void setHrefart(String hrefart) {
	this.hrefart = hrefart;
  }
	  
  public String getArticle() {
	return article;
  }

  public void setArticle(String article) {
    this.article = article;
  }
		  
  public String getDatepub() {
    return datepub;
  }

  public void setDatepub(String datepub) {
    this.datepub = datepub;
  }
	  
  public int getWreaded() {
	return wreaded;
  }

  public void setWreaded(int wreaded) {
    this.wreaded = wreaded;
  }
	  
  public String[] getAll() {
	Log.i(LOG_TAG,headline);
	return new String[] {headline};
  }

  // Will be used by the ArrayAdapter in the ListView
  @Override
  public String toString() {
    return headline;
  }
} 