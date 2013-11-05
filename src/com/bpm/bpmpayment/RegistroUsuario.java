package com.bpm.bpmpayment;

import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import com.bpm.bpmpayment.json.JsonCont;

public class RegistroUsuario extends Activity {
    private ProgressDialog pd = null;
	private EditText aPaternoView, aMaternoView, nombresView, ciudadView, estadoView;
	private EditText paisView, sectorIdustrialView,giroEmpresaView, descripcionView;
	private Button btnLogin;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        
        aPaternoView = (EditText) findViewById(R.id.paterno);
        aMaternoView = (EditText) findViewById(R.id.materno);
        nombresView = (EditText) findViewById(R.id.nombre);
        ciudadView = (EditText) findViewById(R.id.ciudad);
        estadoView = (EditText) findViewById(R.id.estado);
        paisView = (EditText) findViewById(R.id.pais);
        sectorIdustrialView = (EditText) findViewById(R.id.sector);
        giroEmpresaView = (EditText) findViewById(R.id.giro);
        descripcionView = (EditText) findViewById(R.id.uso);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        this.pd = ProgressDialog.show(this, "Procesando...", "Descargando datos...", true, false);
        new DownloadTask().execute("http://bpmcart.com/bpmpayment/php/modelo/editUser.php?email=q@q");
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
        	 try {
	             JSONObject jObject  = new JSONObject(result);
	         	 RegistroUsuario.this.aPaternoView.setText(jObject.getString("apellidop").equals("null") ? "" : jObject.getString("apellidop"));
	         	 RegistroUsuario.this.aMaternoView.setText(jObject.getString("apellidom").equals("null") ? "" : jObject.getString("apellidom"));
	         	 RegistroUsuario.this.nombresView.setText(jObject.getString("nombres").equals("null") ? "" : jObject.getString("nombres"));
	         	 RegistroUsuario.this.ciudadView.setText(jObject.getString("ciudad").equals("null") ? "" : jObject.getString("ciudad"));
	         	 RegistroUsuario.this.estadoView.setText(jObject.getString("estado").equals("null") ? "" : jObject.getString("estado"));
	         	 RegistroUsuario.this.paisView.setText(jObject.getString("pais").equals("null") ? "" : jObject.getString("pais"));
	         	 RegistroUsuario.this.sectorIdustrialView.setText(jObject.getString("sector_industrial").equals("null") ? "" : jObject.getString("sector_industrial"));
	         	 RegistroUsuario.this.giroEmpresaView.setText(jObject.getString("giro_empresa").equals("null") ? "" : jObject.getString("giro_empresa"));
	         	 RegistroUsuario.this.descripcionView.setText(jObject.getString("descripcion").equals("null") ? "" : jObject.getString("descripcion"));
	         	 RegistroUsuario.this.btnLogin.setText("Guardar");
	         	 
	             if (RegistroUsuario.this.pd != null) {
	                 RegistroUsuario.this.pd.dismiss();
	             }
        	 }
        	 catch(Exception e) {
        		 Log.w("Error", e.getMessage());
        	 }
         }
    }    
}