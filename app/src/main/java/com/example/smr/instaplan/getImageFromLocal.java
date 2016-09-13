package com.example.smr.instaplan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.smr.instaplan.activity.CameraActivity;
import com.example.smr.instaplan.adapter.ImageSelectAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by SMR on 9/12/2016.
 */
public class getImageFromLocal extends Activity {
    Button btn_back;
    GridView gv;
    TextView btn_done_select;

    String pathSelected;

//    public class ImageAdapter extends BaseAdapter {
//
//        private Context mContext;
//        ArrayList<String> itemList = new ArrayList<String>();
//
//        public ImageAdapter(Context c) {
//            mContext = c;
//        }
//
//        void add(String path) {
//            itemList.add(path);
//        }
//
//        @Override
//        public int getCount() {
//            return itemList.size() + 1;
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ImageView imageView;
//            if (convertView == null) {  // if it's not recycled, initialize some attributes
//                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(getWithOfScreen() / 3, getWithOfScreen() / 3));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//
//            if (position == 0) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    imageView.setImageDrawable(getDrawable(R.drawable.camera_icon));
//                } else {
//                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.camera_icon));
//                }
//            } else {
//                decodeImage mdecode = new decodeImage(imageView);
//                mdecode.execute(itemList.get(position - 1));
//
//            }
//            return imageView;
//        }
//
//
//        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
//
//            Bitmap bm = null;
//            // First decode with inJustDecodeBounds=true to check dimensions
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(path, options);
//
//            // Calculate inSampleSize
//            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//            // Decode bitmap with inSampleSize set
//            options.inJustDecodeBounds = false;
//            bm = BitmapFactory.decodeFile(path, options);
//
//            return bm;
//        }
//
//        public int calculateInSampleSize(
//
//                BitmapFactory.Options options, int reqWidth, int reqHeight) {
//            // Raw height and width of image
//            final int height = options.outHeight;
//            final int width = options.outWidth;
//            int inSampleSize = 1;
//
//            if (height > reqHeight || width > reqWidth) {
//                if (width > height) {
//                    inSampleSize = Math.round((float) height / (float) reqHeight);
//                } else {
//                    inSampleSize = Math.round((float) width / (float) reqWidth);
//                }
//            }
//
//            return inSampleSize;
//        }
//
//    }
//
//
//    private class decodeImage extends AsyncTask<String, Void, Bitmap> {
//        private ImageView imageToset;
//
//        public decodeImage(ImageView imageToset) {
//            this.imageToset = imageToset;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            Bitmap bm = decodeSampledBitmapFromUri(params[0], 100, 100);
//            return bm;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            imageToset.setImageBitmap(result);
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//    }
//
//    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
//
//        Bitmap bm = null;
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        bm = BitmapFactory.decodeFile(path, options);
//
//        return bm;
//    }
//
//    public int calculateInSampleSize(
//
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            if (width > height) {
//                inSampleSize = Math.round((float) height / (float) reqHeight);
//            } else {
//                inSampleSize = Math.round((float) width / (float) reqWidth);
//            }
//        }
//
//        return inSampleSize;
//    }


    //    ImageAdapter myImageAdapter;
    ImageSelectAdapter myImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.image_from_local);
            btn_back = (Button) findViewById(R.id.btn_imageLocalBack);
            gv = (GridView) findViewById(R.id.local_gridview);
            btn_done_select = (TextView) findViewById(R.id.btn_done_select);

            myImageAdapter = new ImageSelectAdapter(this);
            gv.setAdapter(myImageAdapter);
            String ExternalStorageDirectoryPath = Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath();
            String targetPath = ExternalStorageDirectoryPath + "/DCIM/Camera/";
            File targetDirector = new File(targetPath);

            final File[] files = targetDirector.listFiles();
            for (File file : files) {
                ImageSelectItem item = new ImageSelectItem(file.getAbsolutePath(), false);
                myImageAdapter.add(item);
            }

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btn_done_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyAplication.getInstance().imageSelected = myImageAdapter.getSelected();
                    Intent intent = new Intent();
                    intent.putExtra("path", MyAplication.SELECTED);
                    setResult(1, intent);
                    finish();
                }
            });

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
//                        pathSelected = files[position - 1].getAbsolutePath();
//                        Intent it = new Intent(getImageFromLocal.this, ImagePreview.class);
//                        it.putExtra("path", files[position - 1].getAbsolutePath());
//                        it.putExtra("camera", false);
//                        startActivityForResult(it, 35);
                        myImageAdapter.setSelected(position);
                        myImageAdapter.notifyDataSetChanged();
                        btn_done_select.setText(getString(R.string.select_count, myImageAdapter.getCountSelected()));
                    } else {
                        if (Utility.checkPermission(getImageFromLocal.this)) {
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, 124);

//                            Intent intent = new Intent(getImageFromLocal.this,customCameraView.class);
//                            startActivity(intent);

//                            Intent intent = new Intent(getImageFromLocal.this,customCameraView.class);
//                            startActivity(intent);

                            Intent intent = new Intent(getImageFromLocal.this, CameraActivity.class);
//                            startActivity(intent);
                            startActivityForResult(intent, 36);
                        }
                    }
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 35 && resultCode == 1) {
            if (data.getBooleanExtra("isCrop", false)) {
                Intent it = new Intent();
                it.putExtra("isCrop", true);
                setResult(1, it);
                finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra("path", pathSelected);
                setResult(1, intent);
                finish();
            }

        } else {
            Log.e("getImageFromLocal", "xxx-onActivityResult-resultCode=" + resultCode);
            if (requestCode == 124 && resultCode == -1) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                String root = Environment.getExternalStorageDirectory().toString();
                String path = root + "/instaplan/" + System.currentTimeMillis() + ".jpg";
                File file = new File(path);

                FileOutputStream fo;
                try {
                    file.createNewFile();
                    fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();

                    Intent it = new Intent(getImageFromLocal.this, ImagePreview.class);
                    it.putExtra("path", path);
                    it.putExtra("camera", true);
                    startActivityForResult(it, 36);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (requestCode == 36 && resultCode == 1) {
                    if (data.getBooleanExtra("isCrop", false)) {
                        Intent it = new Intent();
                        it.putExtra("isCrop", true);
                        setResult(1, it);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("isSaved", true);
                        setResult(1, intent);
                        finish();
                    }

                }

            }
        }

    }

    private int getWithOfScreen() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }


}
