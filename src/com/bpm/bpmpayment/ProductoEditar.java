package com.bpm.bpmpayment;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.bpm.bpmpayment.json.JSONParser;

public class ProductoEditar extends Activity {
    private ProgressDialog pd = null;
    private DownloadTask mAuthTask = null;
    private EditText nombreView, precioView, descripcionView;
	private String nombre, precio, descripcion, idProducto, usuario;
	private boolean flag;

    @SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_producto);
        
        Intent intent = getIntent();
        idProducto = intent.getStringExtra("idProducto");
        usuario = intent.getStringExtra("usuario");
        
		nombreView = (EditText) findViewById(R.id.productName);
		precioView = (EditText) findViewById(R.id.productPrice);
		descripcionView = (EditText) findViewById(R.id.productDescription);
		
		List<NameValuePair> paramsEdit = new ArrayList<NameValuePair>();
		paramsEdit.add(new BasicNameValuePair("id_producto", idProducto));
        
        this.flag = true;
        this.pd = ProgressDialog.show(this, "Procesando...", "Descargando datos...", true, false);
        new DownloadTask().execute(paramsEdit);
    }
    
    private String eliminaEspacios(String palabras) {
    	return palabras.replaceAll("\\s", "~");
    }
	
	private void esconderTeclado() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
		InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	@SuppressWarnings("unchecked")
	private void editarProducto() {
		nombre = eliminaEspacios(nombreView.getText().toString());
		precio = precioView.getText().toString();
		descripcion = eliminaEspacios(descripcionView.getText().toString());
		
		List<NameValuePair> paramsUpdate = new ArrayList<NameValuePair>();
		paramsUpdate.add(new BasicNameValuePair("name", nombre));
		paramsUpdate.add(new BasicNameValuePair("price", precio));
		paramsUpdate.add(new BasicNameValuePair("desc", descripcion));
		paramsUpdate.add(new BasicNameValuePair("idProducto", idProducto));
						
		ProductoEditar.this.flag = false;
		esconderTeclado();
		ProductoEditar.this.pd = ProgressDialog.show(ProductoEditar.this, "Procesando...", "Actualizando datos del producto...", true, false);
		ProductoEditar.this.mAuthTask = new DownloadTask();
		mAuthTask.execute(paramsUpdate);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.product_edit_action, menu);
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
	        	editarProducto();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    private class DownloadTask extends AsyncTask<List<NameValuePair>, Void, String> {
         protected String doInBackground(List<NameValuePair>... params) {
        	if(flag) {
        		try {
        			return new JSONParser().getJSONFromUrl("http://bpmcart.com/bpmpayment/php/modelo/editProducto_Post.php", params[0]);
     			} catch (Exception e) {
     				return null;
     			}
        	}
        	else {
        		try {
        			return new JSONParser().getJSONFromUrl("http://bpmcart.com/bpmpayment/php/modelo/updateProducto_Post.php", params[0]);
     			} catch (Exception e) {
     				return null;
     			}
        	}
        	
         }

         protected void onPostExecute(String result) {
        	 if(flag) {
        		 try {
        			 JSONObject jObject  = new JSONObject(result);
    	         	 ProductoEditar.this.nombreView.setText(jObject.getString("nombre").equals("null") ? "" : jObject.getString("nombre"));
    	         	 ProductoEditar.this.precioView.setText(jObject.getString("precio").equals("null") ? "" : jObject.getString("precio"));
    	         	 ProductoEditar.this.descripcionView.setText(jObject.getString("descripcion").equals("null") ? "" : jObject.getString("descripcion"));
    	         	 
    	             if (ProductoEditar.this.pd != null) {
    	            	 ProductoEditar.this.pd.dismiss();
    	             }
            	 }
            	 catch(Exception e) {
            		 Log.w("Error", e.getMessage());
            	 }
        	 }
        	 else {
        		 if (ProductoEditar.this.pd != null) {
	            	 ProductoEditar.this.pd.dismiss();
	            	 Toast.makeText(getBaseContext(), "Producto Actualizado", Toast.LENGTH_SHORT).show();
             		 Intent returnIntent = new Intent();
             		 returnIntent.putExtra("result", usuario);
             		 returnIntent.putExtra("ver", "2");
             		 setResult(RESULT_OK,returnIntent);     
             		 finish();
	             }
        	 }       	 
         }
    }    
}