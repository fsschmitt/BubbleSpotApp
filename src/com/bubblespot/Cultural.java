package com.bubblespot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.adapter.CulturalViewAdapter;
import com.adapter.PromocaoAdapter;
import com.bubblespot.ListPromo.RetrieveImages;
import com.zylinc.view.ViewPagerIndicator;

public class Cultural extends FragmentActivity {
	    static final int NUM_ITEMS = 10;

	    CulturalViewAdapter mAdapter;
	    ViewPager mPager;
	    ArrayList<Evento> eventos;
	    ArrayList<String> images;
	    String text;
	    Context mContext;
	    private ProgressDialog dialog;
	    private boolean loading;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.viewpager);
	        Header header = (Header) findViewById(R.id.header);
		    header.initHeader();
			Search.pesquisa(this, this);
			Bundle c = this.getIntent().getExtras();
	        this.text = c.getString("text");   
	        mContext = this;
	        eventos = new ArrayList<Evento>();
			images = new ArrayList<String>();
			
			/*
			loading = true;
			dialog = ProgressDialog.show(this, "", "A Carregar...",true);
	        dialog.setCancelable(true);
	        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	            	finish();
	                }
	        });
	        
	        new RetrieveCultural().execute();*/
			
			Evento e = new Evento(3,"Jantar de Natal","24/12/2011 21:00","Pra�a de Alimenta��o","Jantar de Natal no shopping com todas as pessoas envolventes.", "http://placehold.it/200x300");
			Evento e2 = new Evento(3,"Teatro de benefici�ncia","23/12/2011 18:00","Sal�o Silo","Teatro de benefici�ncia para ajudar todas as pessoas necessitadas nesta altura de Natal.","http://placehold.it/200x300");
			
			eventos.add(e2);
			eventos.add(e);
			images.add(e2.getImagem_url());
			images.add(e.getImagem_url());
			
			initAdapter(); 
	        
	        new RetrieveImages().execute();
	    }
	    
	    private void initAdapter() {
	    	mPager = (ViewPager)findViewById(R.id.pager);
	        mAdapter = new CulturalViewAdapter(mContext,eventos);
	        mPager.setAdapter(mAdapter);   
	        ViewPagerIndicator indicator = (ViewPagerIndicator)findViewById(R.id.indicator);
	        mPager.setOnPageChangeListener(indicator);
	        indicator.init(0, mAdapter.getCount(), eventos);
	        Resources res = getResources();
	        Drawable prev = res.getDrawable(R.drawable.indicator_prev_arrow);
	        Drawable next = res.getDrawable(R.drawable.indicator_next_arrow);
	        indicator.setArrows(prev, next);
			
		}

		class RetrieveCultural extends AsyncTask<String, Integer, String> {

			@Override
			protected String doInBackground(String... arg0) {
				
				String uri = "http://bubblespot.heroku.com/"+text;
				
				URL url = null;
				try {
					url = new URL(uri);				
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				String line = null;
				JSONArray jo = null;
				try {
					line = getJSONLine(url);
					if(line != null){
						jo = new JSONArray(line);
						for (int i = 0; i < jo.length(); i++) {
							JSONObject evento = jo.getJSONObject(i);
							//int id = evento.getInt("id");
							//String imagem = evento.getString("imagem");
							//images.add(imagem);
							
						}
					}
					else return null;
				} catch (IOException e) {
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}

			// Called once the background activity has completed
			@Override
			protected void onPostExecute(String result) { //
				if(images != null && !images.isEmpty()){
					initAdapter();
					new RetrieveImages().execute();
				}
				else{
					dialog.dismiss();
					setResult(RESULT_FIRST_USER);
					finish();
				}
				

			}
	 }
	    
	    
	    class RetrieveImages extends AsyncTask<String, Integer, String> {

			@Override
			protected String doInBackground(String... arg0) {
				
				
				try{
						Bitmap image = Utils.loadImageFromNetwork(images.get(0));
						eventos.get(eventos.size()-images.size()).setbImage(image);
				}
				catch(Exception e){
					Log.e("Erro ao baixar as imagens.", e.getMessage());
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(String result) {
				if(loading){
					dialog.dismiss();
					loading = false;
				}
				images.remove(0);
				mAdapter.notifyDataSetChanged();
				if(images.size()>0){
					new RetrieveImages().execute();
				}
			}
		}
	    
	    public static String getJSONLine(URL url) throws IOException {
			BufferedReader in;

			URLConnection tc = url.openConnection();
			tc.setDoInput(true);
			tc.setDoOutput(true);
			in = new BufferedReader(new InputStreamReader(tc.getInputStream()));	
			return in.readLine();
		}
}