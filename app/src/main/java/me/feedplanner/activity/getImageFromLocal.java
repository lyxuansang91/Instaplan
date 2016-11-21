package me.feedplanner.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.smr.feedplanner.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.feedplanner.adapter.ImageSelectAdapter;
import me.feedplanner.model.ImageSelectItem;

import static me.feedplanner.util.Utility.REQUEST_CAMERA_PERMISSION;


/**
 * Created by SMR on 9/12/2016.
 */
public class getImageFromLocal extends AppCompatActivity {
    Button btn_back;
    GridView gv;
    TextView btn_done_select;

    String pathSelected;
    ImageSelectAdapter myImageAdapter;
    private static final String FRAGMENT_DIALOG = "dialog";
    private String albumName;
    private int CODE_CAMERA = 36;
    private int CODE_CROP = 35;
    private int RESULT_SELECTED = 1;
    private int RESULT_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.image_from_local);
            Intent intent = getIntent();
            albumName = intent.getStringExtra("albumName");
            btn_back = (Button) findViewById(R.id.btn_imageLocalBack);
            gv = (GridView) findViewById(R.id.local_gridview);
            btn_done_select = (TextView) findViewById(R.id.btn_done_select);

            myImageAdapter = new ImageSelectAdapter(this);
            gv.setAdapter(myImageAdapter);

            ArrayList<ImageSelectItem> imageSelectItems = getAllShownImagesPath(this);
            for (int i = imageSelectItems.size() - 1; i >= 0; i--) {
                myImageAdapter.add(imageSelectItems.get(i));
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
                    setResult(RESULT_SELECTED, intent);
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
//                        if (Utility.checkPermission(getImageFromLocal.this)) {
////                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                            startActivityForResult(intent, 124);
//
////                            Intent intent = new Intent(getImageFromLocal.this,customCameraView.class);
////                            startActivity(intent);
//
////                            Intent intent = new Intent(getImageFromLocal.this,customCameraView.class);
////                            startActivity(intent);
//
//                            Intent intent = new Intent(getImageFromLocal.this, CameraActivity.class);
////                            startActivity(intent);
//                            startActivityForResult(intent, 36);
//                        }
                        if (ContextCompat.checkSelfPermission(getImageFromLocal.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(getImageFromLocal.this, CameraActivity.class);
                            startActivityForResult(intent, CODE_CAMERA);
                        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getImageFromLocal.this,
                                Manifest.permission.CAMERA)) {
                            CameraActivity.ConfirmationDialogFragment
                                    .newInstance(R.string.camera_permission_confirmation,
                                            new String[]{Manifest.permission.CAMERA},
                                            REQUEST_CAMERA_PERMISSION,
                                            R.string.camera_permission_not_granted)
                                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
                        } else {
                            ActivityCompat.requestPermissions(getImageFromLocal.this, new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getImageFromLocal.this, CameraActivity.class);
                    startActivityForResult(intent, CODE_CAMERA);
                }
                break;
        }
    }

    private int RESULT_CAMERA_DONE = 11;
    private int RESULT_CAMERA_CROP = 21;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CROP && resultCode == 1) {
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
                    startActivityForResult(it, CODE_CAMERA);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (requestCode == CODE_CAMERA) {
                    if (resultCode == RESULT_CAMERA_DONE) {
                        Intent intent = new Intent();
                        intent.putExtra("isSaved", true);
                        setResult(RESULT_CAMERA_DONE, intent);
                        finish();
                    } else if (resultCode == RESULT_CAMERA_CROP) {
                        Intent intent = new Intent();
                        intent.putExtra("isCrop", true);
                        setResult(RESULT_CAMERA_CROP, intent);
                        finish();
                    }
                    /*boolean isCrop = data.getBooleanExtra("isCrop", false);
                    boolean isSaved = data.getBooleanExtra("isSaved", false);
                    if (isCrop) {
                        Intent it = new Intent();
                        it.putExtra("isCrop", true);
                        setResult(1, it);
                        finish();
                    } else if (isSaved) {
                        Intent intent = new Intent();
                        intent.putExtra("isSaved", true);
                        setResult(1, intent);
                        finish();
                    }*/

                }

            }
        }

    }

    private int getWithOfScreen() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    private ArrayList<ImageSelectItem> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<ImageSelectItem> listOfAllImages = new ArrayList<ImageSelectItem>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            if (cursor.getString(column_index_folder_name).equals(albumName)) {
                absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(new ImageSelectItem(absolutePathOfImage, false));
            }

        }
        return listOfAllImages;
    }


}
