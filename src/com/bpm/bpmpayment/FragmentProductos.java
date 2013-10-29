package com.bpm.bpmpayment;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.bpm.adapters.MyListAdapter;
import com.bpm.bpmpayment.json.JsonCont;;

public class FragmentProductos extends Fragment {

	private static ArrayList<Producto> productos;
	static JSONArray articles;
	private static final String INDEX = "index";
	private String usuario;
	UserLoginTask mAuthTask = null;
	public ProgressDialog pd = null;

	public static FragmentProductos newInstance(int index, JSONObject jObject ) {
		FragmentProductos fragment = new FragmentProductos();
		Bundle bundle = new Bundle();
		bundle.putInt(INDEX, index);
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);
		
		productos = new ArrayList<Producto>();
		try {
			articles = jObject.getJSONArray("productos");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		Intent intent = getActivity().getIntent();
        usuario = intent.getStringExtra("usuario");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootViewProd = (ViewGroup) inflater.inflate(R.layout.fragment_productos, container, false);
		rootViewProd.setBackgroundColor(Color.BLACK);

		if(productos.size()!=0)
			productos.clear();
			try {
		    	int n = articles.length();
		    	for(int i = 0 ; i < n ; i++) {
		    		JSONObject article = articles.getJSONObject(i);
		    		productos.add(new Producto(article.getString("nombre"), article.getString("id_pruducto"),R.drawable.noimageproduct));
		    	}
		    	
		    	GridView gv = (GridView) rootViewProd.findViewById(R.id.grid_view_productos);
				gv.setAdapter(new MyListAdapter(getActivity(), productos));
				gv.setOnItemClickListener(new OnItemClickListener() {
					@Override
		            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						final int productID = position;
						
					   	   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						   builder.setTitle(productos.get(position).getNombre());
						   builder.setItems(R.array.opciones_productos, new DialogInterface.OnClickListener() {
							   public void onClick(DialogInterface dialog, int item) {
								   if (item == 0) {
									   Intent i = new Intent(getActivity().getBaseContext(), ProductoEditar.class);
							           i.putExtra("idProducto", productos.get(productID).getIdProducto());
							           i.putExtra("usuario", usuario);
							           startActivityForResult(i, 1);
								   }
								   else if(item == 1) {
									   AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
									   builder2.setTitle("Borrar producto");
								        builder2.setMessage("Seguro que desea eliminar:\n\"" + productos.get(productID).getNombre() + "\"")
								               .setPositiveButton("Si", new DialogInterface.OnClickListener() {
								                   public void onClick(DialogInterface dialog, int id) {
								                	   FragmentProductos.this.pd= ProgressDialog.show(getActivity(), "Procesando...", "Eliminando producto...", true, false);
													   mAuthTask = new UserLoginTask();
													   mAuthTask.execute("http://bpmcart.com/bpmpayment/php/modelo/deleteProducto.php?idProducto="+ productos.get(productID).getIdProducto());
												   }
								               })
								               .setNegativeButton("No", null);
								        AlertDialog alert = builder2.create();
										alert.show();
								   }
							    }
							});
							AlertDialog alert = builder.create();
							alert.show();
		            }
		        });
			} catch(Exception e) {
				Log.w("ERROR", "Algo pasÃ³");
			}
		return rootViewProd;
	}
	
	@Override
	public String toString() {
		if(productos.size() != 0) {
			return "Productos (" + productos.size() + ")";
		}
		return "Productos (0)";
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    getActivity().getMenuInflater().inflate(R.menu.productos, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.add_producto:            
	    	Intent i = new Intent(getActivity().getBaseContext(), ProductoAgregar.class);
            i.putExtra("usuario", usuario);
            startActivityForResult(i, 1);
            return true;
	    default:
	        break;
	    }

	    return false;
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
                if(!result.equals("false") || !result.equals("Argumentos invalidos")) {	                	
                	if (FragmentProductos.this.pd != null) {
                		FragmentProductos.this.pd.dismiss();
                		String temp = usuario;
                		Intent returnIntent = new Intent(getActivity(), FragmentActivityMain.class);
                		returnIntent.putExtra("usuario", temp);
                		returnIntent.putExtra("ver", "2");
                		getActivity().setResult(android.app.Activity.RESULT_OK,returnIntent);
                		startActivity(returnIntent);
                		getActivity().finish();
	   	            }
                }
                else {
                	Toast.makeText(getActivity().getBaseContext(), "Hubo algún error",Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.d("ReadJSONFeedTask", e.getLocalizedMessage());
                Toast.makeText(getActivity().getBaseContext(), "Imposible conectarse a la red",Toast.LENGTH_LONG).show();
            }       
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {	
		}
	}
}
