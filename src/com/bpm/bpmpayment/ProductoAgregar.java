package com.bpm.bpmpayment;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bpm.bpmpayment.json.JsonCont;;

public class ProductoAgregar extends Activity{
	private UserLoginTask mAuthTask = null;
	private ProgressDialog pd = null;
	private EditText nombreView, precioView, descripcionView;
	private String nombre, precio, descripcion, usuario;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_producto);
        
        Intent intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        
        nombreView = (EditText) findViewById(R.id.productName);
        precioView = (EditText) findViewById(R.id.productPrice);
        descripcionView = (EditText) findViewById(R.id.productDescription);
        
        ImageView addImagen = (ImageView) findViewById(R.id.imageViewAddProduct);
        addImagen.setClickable(true);
        addImagen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), "Agregar imagen aquí", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private String eliminaEspacios(String palabras) {
    	return palabras.replaceAll("\\s", "~");
    }
	
	private void esconderTeclado() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
		InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void agregaProducto() {
		nombre = eliminaEspacios(nombreView.getText().toString());
		precio = eliminaEspacios(precioView.getText().toString());
		descripcion = eliminaEspacios(descripcionView.getText().toString());
		
		String url = "http://bpmcart.com/bpmpayment/php/modelo/addProduct.php?name=" + nombre +
				     "&price=" + precio + "&desc=" + descripcion + "&email=" + usuario;
		
		esconderTeclado();
		ProductoAgregar.this.pd = ProgressDialog.show(ProductoAgregar.this, "Procesando...", "Registrando datos...", true, false);
		mAuthTask = new UserLoginTask();
		mAuthTask.execute(url);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.product_add_actions, menu);
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
	        case R.id.action_add:
	        	agregaProducto();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public class UserLoginTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... urls) {
			try {
				return new JsonCont().readJSONFeed(urls[0]);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			mAuthTask = null;
            	try{
	                if(!result.equals("false")) {	                	
	                	if (ProductoAgregar.this.pd != null) {
	                		ProductoAgregar.this.pd.dismiss();
	                		
	                		Toast.makeText(getBaseContext(), "Producto Agregado", Toast.LENGTH_SHORT).show();
	                		
	                		Intent returnIntent = new Intent();
	                		returnIntent.putExtra("result", usuario);
	                		returnIntent.putExtra("ver", "2");
	                		setResult(RESULT_OK,returnIntent);     
	                		finish();
		   	            }
	                }
	                else {
	                	Toast.makeText(getBaseContext(), "Credenciales inválidas",Toast.LENGTH_LONG).show();
	                }
	            } catch (Exception e) {
	                Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
	                Toast.makeText(getBaseContext(), "Imposible conectarse a la red",Toast.LENGTH_LONG).show();
	            }          
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
}
