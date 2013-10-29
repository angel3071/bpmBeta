package com.bpm.bpmpayment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bpm.bpmpayment.json.JsonCont;;

public class ClienteEditar extends Activity {
    private ProgressDialog pd = null;
    private DownloadTask mAuthTask = null;
	private EditText aPaternoView, aMaternoView, nombresView, emailView, direccionView;
	private LinearLayout layoutTelefonosAeditar;
	private String nombres, apellidpP, apellidpM, direccion, email, id_cliente;
	private String usuario;
	private boolean flag;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_cliente);
        
        Intent intent = getIntent();
		String cliente = intent.getStringExtra("cliente");
		usuario = intent.getStringExtra("usuario");
        
        aPaternoView = (EditText) findViewById(R.id.clienteApellidoP);
        aMaternoView = (EditText) findViewById(R.id.clienteApellidoM);
        nombresView = (EditText) findViewById(R.id.clienteNombres);
        emailView = (EditText) findViewById(R.id.clienteEmail);
        direccionView = (EditText) findViewById(R.id.clienteDireccion);
        layoutTelefonosAeditar = (LinearLayout)findViewById(R.id.layoutTelefonos);
        
        ImageView addPhone = (ImageView) findViewById(R.id.imageAddCliente);
        addPhone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.layout_phones, null);
						
				ImageView iv = (ImageView)ll.getChildAt(1);
				iv.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						layoutTelefonosAeditar.removeView((View) v.getParent());
					}
				});
				
				layoutTelefonosAeditar.addView(ll, layoutTelefonosAeditar.getChildCount());
			}
		});
        
        this.flag = true;
        this.pd = ProgressDialog.show(this, "Procesando...", "Descargando datos...", true, false);
        new DownloadTask().execute("http://bpmcart.com/bpmpayment/php/modelo/editCliente.php?emailCliente=" + cliente);
    }
    
    private String eliminaEspacios(String palabras) {
    	return palabras.replaceAll("\\s", "~");
    }
	
	private void esconderTeclado() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
		InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void editarCliente() {
		nombres = eliminaEspacios(nombresView.getText().toString());
		apellidpP = eliminaEspacios(aPaternoView.getText().toString());
		apellidpM = eliminaEspacios(aMaternoView.getText().toString());
		email = emailView.getText().toString();
		direccion = eliminaEspacios(direccionView.getText().toString());
		
		ArrayList<String> tels = new ArrayList<String>();
		
		int childcount = layoutTelefonosAeditar.getChildCount();
		for (int i=1; i < childcount; i++){
		      LinearLayout tempView = (LinearLayout)layoutTelefonosAeditar.getChildAt(i);
		      int hijos = tempView.getChildCount();
		      
		      for(int j = 0 ;j < hijos ; j++) {
		    	  if( tempView.getChildAt(j) instanceof EditText ) {
		    		  if(!((EditText)tempView.getChildAt(j)).getText().toString().equals("")) {
		    			  String telefono =  ((EditText)tempView.getChildAt(j)).getText().toString();
			    		  tels.add(telefono);
		    		  }
		    	  }
		      }
		}
		
		String phones = "";
		for(int i = 0 ; i < tels.size() ; i++) {
			phones = phones + "telefono" + String.valueOf(i+1) + "=" + tels.get(i) + "&";
		}
						
		String url = "http://bpmcart.com/bpmpayment/php/modelo/updateClient.php?names=" + nombres +
				     "&apellidop=" + apellidpP + "&apellidom=" + apellidpM + "&emailCliente=" + email +
				     "&direccion=" + direccion + "&numTelefonos=" + String.valueOf(tels.size()) + 
				     "&" + phones + "idCliente=" + id_cliente;
						
		flag = false;
		esconderTeclado();
		ClienteEditar.this.pd = ProgressDialog.show(ClienteEditar.this, "Procesando...", "Actualizando datos del cliente...", true, false);
		ClienteEditar.this.mAuthTask = new DownloadTask();
			mAuthTask.execute(url);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.client_edit_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_cancel:
	        	esconderTeclado();
			    Intent returnIntent = new Intent();
        		returnIntent.putExtra("result", usuario);
        		setResult(RESULT_CANCELED,returnIntent);     
        		finish();
	            return true;
	        case R.id.action_edit:
	        	editarCliente();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private class DownloadTask extends AsyncTask<String, Void, String> {
         protected String doInBackground(String... urls) {
        	 try {
 				return new JsonCont().readJSONFeed(urls[0]);
 			} catch (Exception e) {
 				return null;
 			}
         }

         protected void onPostExecute(String result) {
        	 if(flag) {
        		 try {
    	             JSONObject jObject  = new JSONObject(result);
    	             JSONArray jArray = jObject.getJSONArray("telefonos");
    	             for (int i=0; i<jArray.length(); i++){
    	                 JSONObject anotherjsonObject = jArray.getJSONObject(i);
    	            	 
    	            	 final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	 				 LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.layout_phones, null);
    	 					
    	 				 EditText et = (EditText) ll.getChildAt(0);
    	 				 et.setText(anotherjsonObject.getString("telefono"));
    	 				
    	 				 ImageView iv = (ImageView)ll.getChildAt(1);
    	 				 iv.setOnClickListener(new View.OnClickListener() {
    	 				     @Override
    	 					 public void onClick(View v) {
    	 					     layoutTelefonosAeditar.removeView((View) v.getParent());
    	 					 }
    	 				 });
    	 				
    	 				 layoutTelefonosAeditar.addView(ll, layoutTelefonosAeditar.getChildCount());
    	             }
    	             
    	             ClienteEditar.this.id_cliente = jObject.getString("id_cliente");
    	         	 ClienteEditar.this.aPaternoView.setText(jObject.getString("apellidop").equals("null") ? "" : jObject.getString("apellidop"));
    	         	 ClienteEditar.this.aMaternoView.setText(jObject.getString("apellidom").equals("null") ? "" : jObject.getString("apellidom"));
    	         	 ClienteEditar.this.nombresView.setText(jObject.getString("nombres").equals("null") ? "" : jObject.getString("nombres"));
    	         	 ClienteEditar.this.emailView.setText(jObject.getString("email").equals("null") ? "" : jObject.getString("email"));
    	         	 ClienteEditar.this.direccionView.setText(jObject.getString("direccion").equals("null") ? "" : jObject.getString("direccion"));
    	         	 
    	             if (ClienteEditar.this.pd != null) {
    	            	 ClienteEditar.this.pd.dismiss();
    	             }
            	 }
            	 catch(Exception e) {
            		 Log.w("Error", e.getMessage());
            	 }
        	 }
        	 else {
        		 if (ClienteEditar.this.pd != null) {
	            	 ClienteEditar.this.pd.dismiss();
	            	 Toast.makeText(getBaseContext(), "Cliente Actualizado", Toast.LENGTH_SHORT).show();
             		 Intent returnIntent = new Intent();
             		 returnIntent.putExtra("result", usuario);
             		 returnIntent.putExtra("ver", "0");
             		 setResult(RESULT_OK,returnIntent);     
             		 finish();
	             }
        	 }
         }
    }    
}