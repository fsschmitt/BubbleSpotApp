package com.bubblespot;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopDetail extends Activity {

	private int id;
	private String nome;
	private int piso;
	private int numero;
	private String telefone;
	private String detalhes;
	private String imagem;
	private String tags;
	private String shopping;
	private int idShopping;
	private ProgressDialog dialog;
	private Bitmap bImage;
	private Context context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ShopDetail.this.setContentView(R.layout.shopdetail);
		Header header = (Header) findViewById(R.id.header);
	    header.initHeader();
		Search.pesquisa(ShopDetail.this, ShopDetail.this);
		dialog = ProgressDialog.show(this, "", "Loading...",true);
		Bundle b = this.getIntent().getExtras();
		this.id = b.getInt("lojaID");
		this.nome = b.getString("lojaNome");
		this.piso = b.getInt("lojaPiso");
		this.numero = b.getInt("lojaNumero");
		this.telefone = b.getString("lojaTelefone");
		this.detalhes = b.getString("lojaDetalhes");
		this.imagem = b.getString("lojaImagem");
		this.tags = b.getString("lojaTags");
		this.shopping = b.getString("lojaShopping");
		this.idShopping = b.getInt("idShopping");
		
		
		new RetrieveLogo().execute();
		

	}
	
	class RetrieveLogo extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			
			bImage = null;
			try {
				bImage = Utils.loadImageFromNetwork(imagem);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			
			TextView loja_shopping = (TextView) ShopDetail.this.findViewById(R.id.loja_shopping);
			loja_shopping.setText(nome + " (" + shopping + ")");
			
			ImageView logo = (ImageView) ShopDetail.this.findViewById(R.id.loja_logo);
			logo.setImageBitmap(bImage);
			
			TextView loja_detalhes = (TextView) ShopDetail.this.findViewById(R.id.loja_detalhes);
			loja_detalhes.setText("\tPiso: " + piso + "\n\tN�mero: " + numero + "\n\tTelefone: " + telefone + "\n\t�reas de Neg�cio: " + tags + "\n\tDetalhes: " + detalhes);
			dialog.dismiss();
			
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.shop_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.loja_promocoes:
		{
			Intent intent = new Intent(context, ListPromo.class);
			Bundle b = new Bundle();
			b.putString("text", "shoppings/"+idShopping+"/lojas/"+id+"/promos.json");
			b.putInt("idShopping", idShopping);
			b.putInt("idLoja", id);
			b.putString("nomeShopping", shopping);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		}
		return true;
		case R.id.loja_partilhar:
			/*
			Dialog dialog = new Dialog(ShopDetail.this);
			dialog.setContentView(R.layout.shoppingmap);
			dialog.setTitle("Localiza��o do Shopping");
			dialog.setCancelable(true);
			ImageView mapa = (ImageView) dialog.findViewById(R.id.mapa);
			try {
				mapa.setImageBitmap(Utils.loadImageFromNetwork("http://maps.googleapis.com/maps/api/staticmap?markers=" + this.latitude + "," + this.longitude + "&zoom=16&size=400x400&sensor=false"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			dialog.show();
			return true;*/
		default:
			return super.onOptionsItemSelected(item);
		}
	}




}