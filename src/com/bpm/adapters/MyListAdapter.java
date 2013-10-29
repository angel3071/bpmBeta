package com.bpm.adapters;

import java.util.ArrayList;
import java.util.List;

import com.bpm.bpmpayment.ElementoGrid;
import com.bpm.bpmpayment.R;
import com.bpm.bpmpayment.R.id;
import com.bpm.bpmpayment.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {
    private List<Item> items = new ArrayList<Item>();
    private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public MyListAdapter(Context context, ArrayList elementos) {
        inflater = LayoutInflater.from(context);

        for (ElementoGrid elemento : (ArrayList<ElementoGrid>)elementos) {
        	items.add(new Item(elemento.nombre, elemento.pic));
		}
    }
    
	@Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).drawableId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if(v == null) {
            v = inflater.inflate(R.layout.item_layout, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);

        Item item = (Item)getItem(i);

        picture.setImageResource(item.drawableId);
        name.setText(item.name);

        return v;
    }

    private class Item {
        final String name;
        final int drawableId;

        Item(String name, int drawableId) {
            this.name = name;
            this.drawableId = drawableId;
        }
    }
}