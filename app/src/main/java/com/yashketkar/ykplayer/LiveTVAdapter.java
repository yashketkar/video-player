package com.yashketkar.ykplayer;

/**
 * Created by Yash on 1/4/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class LiveTVAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<TV> tvItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public LiveTVAdapter(Activity activity, List<TV> tvItems) {
        this.activity = activity;
        this.tvItems = tvItems;
    }

    @Override
    public int getCount() {
        return tvItems.size();
    }

    @Override
    public Object getItem(int location) {
        return tvItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.livetv_list_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.livetv_item_icon);
        TextView title = (TextView) convertView.findViewById(R.id.livetv_item_title);

        // getting movie data for the row
        TV t = tvItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(t.getThumbnailUrl(), imageLoader);

        // title
        title.setText(t.getTitle());

        return convertView;
    }

}
