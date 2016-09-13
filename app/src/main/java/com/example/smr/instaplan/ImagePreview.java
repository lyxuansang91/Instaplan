package com.example.smr.instaplan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.yalantis.ucrop.UCrop;

import java.io.File;

/**
 * Created by SMR on 9/12/2016.
 */
public class ImagePreview extends Activity {
    Button btn_crop, btn_done, btn_back;
    ImageView img;
    String path;
    Boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_image);
        btn_crop = (Button)findViewById(R.id.btn_crop);
        btn_done = (Button)findViewById(R.id.btn_done);
        btn_back = (Button)findViewById(R.id.btn_previewImageBack);
        img = (ImageView)findViewById(R.id.img_photoPreview);

        path = getIntent().getStringExtra("path");
        isCamera = getIntent().getBooleanExtra("camera",false);

        Bitmap bm = decodeSampledBitmapFromUri(path, 500, 500);
        img.setImageBitmap(bm);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCamera) {
                    Intent it = new Intent();
                    it.putExtra("isSaved",true);
                    it.putExtra("isCrop",false);
                    setResult(1,it);
                    finish();
                } else  {
                    Intent it = new Intent();
                    it.putExtra("isCrop",false);
                    setResult(1,it);
                    finish();
                }

            }
        });


        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(new File("file://" + path).toString());
                String root = Environment.getExternalStorageDirectory().toString();
                if (isCamera) {
                    UCrop uCrop = UCrop.of(uri, uri);
                    uCrop.withMaxResultSize(400,400);
                    uCrop.start(ImagePreview.this);
                } else  {
                    Uri uriResult = Uri.parse(new File("file://" + root + "/instaplan/" + System.currentTimeMillis() + ".jpg").toString());
                    UCrop uCrop = UCrop.of(uri, uriResult);
                    uCrop.withMaxResultSize(400,400);
                    uCrop.start(ImagePreview.this);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Intent it = new Intent();
            it.putExtra("isCrop",true);
            setResult(1,it);
            finish();
        }
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

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }
    

}
