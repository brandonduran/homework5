package com.example.contactlist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomListViewAdapter extends ArrayAdapter<String> {
    ArrayList<String> list;
    Context context;
    ArrayList<Float> costs;
    ArrayList<Integer> quant;

    // The ListViewAdapter Constructor
    // @param items: The list of items in our Grocery List
    public CustomListViewAdapter(Context context, ArrayList<String> items, ArrayList<Float> cost, ArrayList<Integer> quants) {
        super(context, R.layout.list_row, items);
        this.context = context;
        list = items;
        costs = cost;
        quant = quants;
    }

    // The method we override to provide our own layout for each View (row) in the ListView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_row, null);
            TextView name = convertView.findViewById(R.id.name);
            TextView costBox = convertView.findViewById(R.id.costBox);
            TextView quantity = convertView.findViewById(R.id.quantity);

            name.setText(list.get(position));
            String y = String.format("%.2f",costs.get(position) );//need this for padding decimal places
            costBox.setText("$" + y );
            quantity.setText("(x" + quant.get(position) + ")");
        }
        return convertView;
    }
}



