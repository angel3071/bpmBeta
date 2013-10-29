package com.bpm.bpmpayment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bpm.adapters.MyListAdapter;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class FragmentFacturas extends Fragment {

	private static ArrayList<Factura> facturas;
	static JSONArray factur;
	private static final String INDEX = "index";
	private String usuario;

	public static FragmentFacturas newInstance(int index, JSONObject jObject ) {
		FragmentFacturas fragment = new FragmentFacturas();
		Bundle bundle = new Bundle();
		bundle.putInt(INDEX, index);
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);
		
		facturas = new ArrayList<Factura>();
		try {
			factur = jObject.getJSONArray("facturas");
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
		ViewGroup rootViewFrag = (ViewGroup) inflater.inflate(R.layout.fragment_facturas, container, false);
		rootViewFrag.setBackgroundColor(Color.DKGRAY);
		
		if(facturas.size() != 0)
			facturas.clear();
			try {
		    	int n = factur.length();
		    	for(int i = 0 ; i < n ; i++) {
		    		JSONObject article = factur.getJSONObject(i);
		    		facturas.add(new Factura(article.getString("detalles"),R.drawable.factura));
		    	}
		    	
		    	GridView gv = (GridView) rootViewFrag.findViewById(R.id.grid_view_facturas);
				gv.setAdapter(new MyListAdapter(getActivity(),facturas));
				gv.setOnItemClickListener(new OnItemClickListener() {
					@Override
		            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		            	Toast.makeText(getActivity().getBaseContext(), String.valueOf(position), Toast.LENGTH_LONG).show();
		            }
		        });
			} catch(Exception e) {
				Log.w("ERROR", "Algo pasó");
			}
			
		return rootViewFrag;
	}
	
	@Override
	public String toString() {
		if(facturas.size() != 0) {
			return "Facturas (" + facturas.size() + ")";
		}
		return "Facturas (0)";
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    getActivity().getMenuInflater().inflate(R.menu.facturas, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.add_facturas:            
	    	//Intent i = new Intent(getActivity().getBaseContext(), RegistrarProducto.class);
            //i.putExtra("usuario", usuario);
            //startActivityForResult(i, 1);
            return true;
	    default:
	        break;
	    }

	    return false;
	}
}
