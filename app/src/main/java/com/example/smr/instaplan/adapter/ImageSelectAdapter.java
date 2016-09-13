package com.example.smr.instaplan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.smr.instaplan.ImageSelectItem;
import com.example.smr.instaplan.R;

import java.util.ArrayList;

/**
 * Created by chienchieu on 13/09/2016.
 */
public class ImageSelectAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    ArrayList<ImageSelectItem> mItems = new ArrayList<>();

    public ImageSelectAdapter(Context c) {
        mContext = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(ImageSelectItem item) {
        mItems.add(item);
    }

    public void setSelected(int position) {
        boolean current = mItems.get(position - 1).isSelected();
        mItems.get(position - 1).setSelected(!current);
    }

    public ArrayList<ImageSelectItem> getSelected() {
        ArrayList<ImageSelectItem> selecteds = new ArrayList<>();
        for (ImageSelectItem item : mItems) {
            if (item.isSelected()) {
                selecteds.add(item);
            }
        }
        return selecteds;
    }

    public int getCountSelected() {
        int count = 0;
        for (ImageSelectItem item : mItems) {
            if (item.isSelected()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getCount() {
        return mItems.size() + 1;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_image_select, null);
            holder.imageview = (ImageView) convertView.findViewById(R.id.img_android);
            holder.btnSelect = (ImageView) convertView.findViewById(R.id.btn_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.btnSelect.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imageview.setImageDrawable(mContext.getDrawable(R.drawable.camera_icon));
            } else {
                holder.imageview.setImageDrawable(mContext.getResources().getDrawable(R.drawable.camera_icon));
            }
        } else {
            if (mItems.get(position - 1).isSelected()) {
                holder.btnSelect.setVisibility(View.VISIBLE);
            } else {
                holder.btnSelect.setVisibility(View.GONE);
            }
            decodeImage mdecode = new decodeImage(holder.imageview);
            mdecode.execute(mItems.get(position - 1).getPath());
        }

        return convertView;

    }

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }


    private class decodeImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageToset;

        public decodeImage(ImageView imageToset) {
            this.imageToset = imageToset;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm = decodeSampledBitmapFromUri(params[0], 100, 100);
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageToset.setImageBitmap(result);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    class ViewHolder {
        ImageView imageview;
        ImageView btnSelect;
    }

}