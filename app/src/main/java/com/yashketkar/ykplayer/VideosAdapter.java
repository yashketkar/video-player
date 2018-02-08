package com.yashketkar.ykplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Yash Ketkar on 12/30/2014.
 */

public class VideosAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public VideosAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView content = (TextView) view.findViewById(R.id.videos_item_title);
        ImageView thumb = (ImageView) view.findViewById(R.id.videos_item_icon);
        content.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)));

        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
        SetThumbTask s = new SetThumbTask(context, thumb);
        s.execute(new Integer(id));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.videos_list_item, parent, false);
    }

    private class SetThumbTask extends AsyncTask<Integer, String, Bitmap> {

        Context context;
        ImageView thumb;

        public SetThumbTask(Context context, ImageView thumb) {
            this.context = context;
            this.thumb = thumb;
        }

        protected Bitmap doInBackground(Integer... ids) {
            int id = ids[0].intValue();
            ContentResolver crThumb = context.getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
            return thumbnail;
        }

        protected void onPostExecute(Bitmap result) {
            thumb.setImageBitmap(result);
        }
    }
}