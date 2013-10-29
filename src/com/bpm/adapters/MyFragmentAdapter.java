package com.bpm.adapters;

import java.util.ArrayList;

import org.json.JSONObject;

import com.bpm.bpmpayment.FragmentClientes;
import com.bpm.bpmpayment.FragmentFacturas;
import com.bpm.bpmpayment.FragmentProductos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentAdapter extends FragmentPagerAdapter {
	protected static final String[] CONTENT = new String[] { "Clientes", "Facturas", "Productos"};
	private int ITEMS;
	private JSONObject jObjectClientes = null;
	private JSONObject jObjectFacturas = null;
	private JSONObject jObjectProductos = null;
	FragmentManager fm;
	
	public MyFragmentAdapter(FragmentManager fragmentManager, int items, ArrayList<JSONObject> datos) {
		super(fragmentManager);
		this.fm = fragmentManager;
		this.ITEMS = items;
		this.jObjectClientes = datos.get(0);
		this.jObjectFacturas = datos.get(1);
		this.jObjectProductos = datos.get(2);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return MyFragmentAdapter.CONTENT[position % CONTENT.length];
	}

	@Override
	public int getCount() {
		return ITEMS;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return FragmentClientes.newInstance(0, jObjectClientes);
		case 1:
			return FragmentFacturas.newInstance(1, jObjectFacturas);
		case 2: 
			return FragmentProductos.newInstance(2, jObjectProductos);
		default:
			return null;
		}
	}
}