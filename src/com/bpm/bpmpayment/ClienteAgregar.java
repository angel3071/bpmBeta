package com.bpm.bpmpayment;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.Spinner;
import android.widget.Toast;
import com.bpm.bpmpayment.json.JSONParser;

public class ClienteAgregar extends Activity {
	private UserLoginTask mAuthTask = null;
	private ProgressDialog pd = null;
	private EditText nombresView, apellidpPView, apellidpMView, emailView, razonSocialView, rfcView;
	private EditText paisView, estadoView, ciudadView, delegacionView, coloniaView, calleNumeroView, cpView;
	private String nombres, apellidpP, apellidpM, email, razonSocial, rfc;
	private String pais, estado, ciudad, delegacion, colonia, calleNumero, cp;
	private String usuario;
	private LinearLayout layoutTelefonos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_cliente);
        
        Intent intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        
        nombresView = (EditText) findViewById(R.id.clienteNombres);
        apellidpPView = (EditText) findViewById(R.id.clienteApellidoP);
        apellidpMView = (EditText) findViewById(R.id.clienteApellidoM);
        emailView = (EditText) findViewById(R.id.clienteEmail);
        razonSocialView = (EditText)findViewById(R.id.clienteRazonSocial);
        rfcView = (EditText) findViewById(R.id.clienteRFC);
        layoutTelefonos = (LinearLayout)findViewById(R.id.layoutTelefonos);
        paisView = (EditText)findViewById(R.id.clientePais);
        estadoView = (EditText)findViewById(R.id.clienteEstado);
        ciudadView = (EditText)findViewById(R.id.clienteCiudad);
        delegacionView = (EditText)findViewById(R.id.clienteDelegacion);
        coloniaView = (EditText)findViewById(R.id.clienteColonia);
        calleNumeroView = (EditText)findViewById(R.id.clienteCalleNumero);
        cpView = (EditText)findViewById(R.id.clienteCP);
                      
        ImageView addPhone = (ImageView) findViewById(R.id.imageAddCliente);
        addPhone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.layout_phones, null);
				ImageView iv = (ImageView)ll.getChildAt(2);
				iv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						layoutTelefonos.removeView((View) v.getParent());
					}
				});
			    layoutTelefonos.addView(ll, layoutTelefonos.getChildCount());
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
	
	@SuppressWarnings("unchecked")
	private void agregaCliente() {
		nombres = eliminaEspacios(nombresView.getText().toString());
		apellidpP = eliminaEspacios(apellidpPView.getText().toString());
		apellidpM = eliminaEspacios(apellidpMView.getText().toString());
		email = emailView.getText().toString();
		razonSocial = razonSocialView.getText().toString();
		rfc = rfcView.getText().toString();
		pais = paisView.getText().toString();
		estado = estadoView.getText().toString();
		ciudad = ciudadView.getText().toString();
		delegacion = delegacionView.getText().toString();
		colonia = coloniaView.getText().toString();
		calleNumero = calleNumeroView.getText().toString();
		cp = cpView.getText().toString();
				
		ArrayList<String> tels = new ArrayList<String>();
		ArrayList<String> typeTels = new ArrayList<String>();
		
		int childcount = layoutTelefonos.getChildCount();
		for (int i=1; i < childcount; i++){
		      LinearLayout tempView = (LinearLayout)layoutTelefonos.getChildAt(i);
		      int hijos = tempView.getChildCount();
		      
		      for(int j = 0 ;j < hijos ; j++) {
		    	  if( tempView.getChildAt(j) instanceof EditText ) {
		    		  if(!((EditText)tempView.getChildAt(j)).getText().toString().equals("")) {
		    			  String telefono =  ((EditText)tempView.getChildAt(j)).getText().toString();
			    		  tels.add(telefono);
		    		  }
		    	  }
		    	  
		    	  if( tempView.getChildAt(j) instanceof Spinner ) {
		    	      String tipoTelefono =  ((Spinner)tempView.getChildAt(j)).getSelectedItem().toString();
		    	      typeTels.add(tipoTelefono);
		    	  }
		      }
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("nombres", nombres));
        params.add(new BasicNameValuePair("apellidoP", apellidpP));
        params.add(new BasicNameValuePair("apellidoM", apellidpM));
        params.add(new BasicNameValuePair("emailCliente", email));
        params.add(new BasicNameValuePair("razonSocial", razonSocial));
        params.add(new BasicNameValuePair("rfc", rfc));
        params.add(new BasicNameValuePair("pais", pais));
        params.add(new BasicNameValuePair("estado", estado));
        params.add(new BasicNameValuePair("ciudad", ciudad));
        params.add(new BasicNameValuePair("delegacion", delegacion));
        params.add(new BasicNameValuePair("colonia", colonia));
        params.add(new BasicNameValuePair("calleNumero", calleNumero));
        params.add(new BasicNameValuePair("cp", cp));
        
		params.add(new BasicNameValuePair("numTelefonos", String.valueOf(tels.size())));
		for(int i = 0 ; i < tels.size() ; i++) {
			params.add(new BasicNameValuePair("telefono" + String.valueOf(i+1), tels.get(i)));
		}
		
		params.add(new BasicNameValuePair("numTipoTelefonos", String.valueOf(typeTels.size())));
		for(int i = 0 ; i < typeTels.size() ; i++) {
			params.add(new BasicNameValuePair("tipoTelefono" + String.valueOf(i+1), typeTels.get(i)));
		}
		
		params.add(new BasicNameValuePair("emailUser", usuario));
		
		esconderTeclado();
		ClienteAgregar.this.pd = ProgressDialog.show(ClienteAgregar.this, "Procesando...", "Registrando datos...", true, false);
		mAuthTask = new UserLoginTask();
		mAuthTask.execute(params);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.client_add_actions, menu);
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
	        	agregaCliente();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public class UserLoginTask extends AsyncTask<List<NameValuePair>, Void, String>{
		@Override
		protected String doInBackground(List<NameValuePair>... params) {
			try {			
				String ret = new JSONParser().getJSONFromUrl("http://bpmcart.com/bpmpayment/php/modelo/addClientPost.php", params[0]);
				Log.w("Datos WEB", ret);
				return ret;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			mAuthTask = null;
            	try{
	                if(!result.equals("false") || !result.equals("Argumentos invalidos")) {	                	
	                	if (ClienteAgregar.this.pd != null) {
	                		ClienteAgregar.this.pd.dismiss();
	                		
	                		Toast.makeText(getBaseContext(), "Cliente Agregado", Toast.LENGTH_SHORT).show();
	                		
	                		Intent returnIntent = new Intent();
	                		returnIntent.putExtra("result", usuario);
	                		returnIntent.putExtra("ver", "0");
	                		setResult(RESULT_OK,returnIntent);     
	                		finish();
		   	            }
	                }
	                else {
	                	Toast.makeText(getBaseContext(), "Hubo algún error",Toast.LENGTH_LONG).show();
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
