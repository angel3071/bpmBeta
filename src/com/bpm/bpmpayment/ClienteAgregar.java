package com.bpm.bpmpayment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
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

import eu.janmuller.android.simplecropimage.CropImage;

public class ClienteAgregar extends Activity {
	private static final int REQUEST_CODE_CROP_IMAGE = 0;
	private UserLoginTask mAuthTask = null;
	private ProgressDialog pd = null;
	private EditText nombresView, apellidpPView, apellidpMView, emailView, razonSocialView, rfcView;
	private EditText paisView, estadoView, ciudadView, delegacionView, coloniaView, calleNumeroView, cpView;
	private String nombres, apellidpP, apellidpM, email, razonSocial, rfc;
	private String pais, estado, ciudad, delegacion, colonia, calleNumero, cp;
	private String usuario;
	private LinearLayout layoutTelefonos;
	private ImageView viewImageAddCliente, deleteFotoView;
	private File file;
	private boolean flagFoto = false;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	
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
        deleteFotoView = (ImageView) findViewById(R.id.clienteImageDeleteFoto);
        viewImageAddCliente = (ImageView) findViewById(R.id.imageViewAddClient);

        viewImageAddCliente.setClickable(true);
        viewImageAddCliente.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				final CharSequence[] options = { "Tomar Foto", "Escoger de la galería","Cancelar" };
				
				esconderTeclado();
				 
		        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteAgregar.this);
		        builder.setTitle("Agrega foto del cliente");
		        builder.setItems(options, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int item) {
		                if (item == 0) {
		                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
		                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		                    startActivityForResult(intent, 1);
		                }
		                else if (item == 1) {
		                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		                    startActivityForResult(intent, 2);
		                }
		                else if (item == 2) {
		                    dialog.dismiss();
		                }
		            }
		        });
		        builder.show();
			}
		});
                      
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
        
        deleteFotoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewImageAddCliente.setImageResource(R.drawable.addimageuser);
				file.delete();
				deleteFotoView.setEnabled(false);
				deleteFotoView.setVisibility(View.INVISIBLE);
				flagFoto = false;
			}
		});
        
        this.flagFoto = false;
	}
	
	private void runCropImage(String filePath) {
	    Intent intent = new Intent(this, CropImage.class);
	    intent.putExtra(CropImage.IMAGE_PATH, filePath);
	    intent.putExtra(CropImage.SCALE, true);
	    intent.putExtra(CropImage.ASPECT_X, 3);
	    intent.putExtra(CropImage.ASPECT_Y, 3);
	    startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	    
	    this.deleteFotoView.setEnabled(true);
	    this.deleteFotoView.setVisibility(View.VISIBLE);
	    this.flagFoto = true;
	}
	
	private Bitmap reduceImagen() {		
		Bitmap bitmap;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);
        
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));
        
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));
        
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));
        
        bitmap.recycle();
        
        OutputStream outFile = null;
        File file2 = new File(file.getAbsolutePath());
        try {
            outFile = new FileOutputStream(file2);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outFile);
            outFile.flush();
            outFile.close();
                                  
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return scaledBitmap;
	}
	
	private String eliminaEspacios(String palabras) {
    	return palabras.replaceAll("\\s", "~");
    }
	
	private void esconderTeclado() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
		InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private int dpToPx(int dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
	
	private File copyFile(String sourceFile) throws IOException {
		File destination = null;
		try {
			String path = FragmentActivityMain.dataAppDirectory;
		    File sd = new File(path);
		    
		    if (sd.canWrite()) {
		        String destinationImagePath = path + "tempFileClient.jpg";
		        File source= new File(sourceFile);
		        destination= new File(destinationImagePath);
		        if (source.exists()) {
		            fileInputStream = new FileInputStream(source);
					FileChannel src = fileInputStream.getChannel();
		            fileOutputStream = new FileOutputStream(destination);
					FileChannel dst = fileOutputStream.getChannel();
		            dst.transferFrom(src, 0, src.size());
		            src.close();
		            dst.close();
		        }
		    }
		} catch (Exception e) {
			Log.w("ERROR", "Error al copiar la imagen!");
		}
		
		return destination;
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
		
		if(this.flagFoto) {
			Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
			byte[] byteArray = stream.toByteArray();
			String imageEncode = Base64.encodeToString(byteArray, Base64.DEFAULT);
			params.add(new BasicNameValuePair("image", imageEncode));
		}
		else {
			params.add(new BasicNameValuePair("image", "NULL"));
		}
		
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
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        	if(requestCode == 0) {
                viewImageAddCliente.setImageBitmap(reduceImagen());
            }
        	else if (requestCode == 1) {
        		// Imagen tomada por la cámara
            	File f = new File(Environment.getExternalStorageDirectory().toString() + "/temp.jpg");
                
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    
                    String path = FragmentActivityMain.dataAppDirectory;
                    
                    f.delete();
                    OutputStream outFile = null;
                    file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                        runCropImage(file.getAbsolutePath());                       
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
            	// Imagen tomada de la galería
            	Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();                
                
                try {
					file = copyFile(picturePath);
					if( file != null ) {
						runCropImage(file.getAbsolutePath());
					}
					else {
						Toast.makeText(getBaseContext(), "Ocurrio algún error", Toast.LENGTH_SHORT).show();
					}					
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
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
