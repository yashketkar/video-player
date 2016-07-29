package com.yashketkar.ykplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Yash on 11/28/2014.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;
    private TypedArray navDrawerIcons;

    public NavigationDrawerAdapter(Context context, String[] values) {
        super(context, R.layout.drawer_list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.drawer_item_title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.drawer_item_icon);
        textView.setText(values[position]);
        navDrawerIcons = context.getResources().obtainTypedArray(R.array.nav_drawer_icons);
        imageView.setImageResource(navDrawerIcons.getResourceId(position, -1));
        return rowView;
    }
}