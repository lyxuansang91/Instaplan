package me.feedplanner.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smr.feedplanner.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramSession;
import net.londatiga.android.instagram.InstagramUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.feedplanner.fragment.deletedFragment;
import me.feedplanner.fragment.preShareFragment;
import me.feedplanner.fragment.shareFragment;
import me.feedplanner.model.ImageSelectItem;
import me.feedplanner.ucrop.UCrop;
import me.feedplanner.util.Utility;
import me.feedplanner.widget.PopoverView;

/**
 * Created by SMR on 9/8/2016.
 */
public class Login extends FragmentActivity implements PopoverView.PopoverViewDelegate, deletedFragment.RemoveDeletedFragment, shareFragment.RemoveShareDelegate, preShareFragment.PreShareFragmentFragment {
    ProgressBar pb_loadImage;
    Button btn_signin, btn_logout;
    EditText edt_username, edit_password;
    GridView gv_images;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView nickname, post, follower, following;
    ImageView img_avatar;
    PopoverView popoverView;
    deletedFragment mDeletedFragment;
    preShareFragment mPreShareFramgnet;
    shareFragment mshareFragment;
    LoginButton btn_loginFacebook;
    int selectedPosition;
    private ImageAdapter imageAdapter;
    private InstagramSession mInstagramSession;
    private Instagram mInstagram;
    View rowViewInclick;

    private static final int NUMBER_OF_PAGES = 18;
//    private int numberPages ;
//    private boolean isFull = false;
//    private boolean isLoad;

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    RequestQueue mrequestQueue;
    CallbackManager callbackManager;

    ArrayList<String> fileArrayImage = new ArrayList<String>();// list of file paths
    ArrayList<String> oldFileArrayImage = new ArrayList<String>();
    ArrayList<String> arrayImageInInstagram = new ArrayList<String>();// list of Instagram
    File[] listFile;
    private FloatingActionButton fab;
    private static final String CLIENT_ID = "44cdda39501e43799baef40906424eb2";
    private static final String CLIENT_SECRET = "b15428252491471baae9bde85d7442f9";
    private static final String REDIRECT_URI = "http://feedplanner.me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FacebookSdk.sdkInitialize(getApplicationContext());

            mInstagram = new Instagram(this, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);

            mInstagramSession = mInstagram.getSession();

            if (mInstagramSession.isActive()) {
                setContentView(R.layout.list_image);
                if (Utility.checkPermission(Login.this)) {
//                    isLoad = false;
//                    numberPages = NUMBER_OF_PAGES;
                    mainAction();
                }
            } else {
                setContentView(R.layout.login);
                btn_signin = (Button) findViewById(R.id.btn_signin);
                edt_username = (EditText) findViewById(R.id.txt_username);
                edit_password = (EditText) findViewById(R.id.txt_password);

                edt_username.setSingleLine();
                edit_password.setSingleLine();
                edit_password.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btn_signin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userName = edt_username.getText().toString().trim();
                        String password = edit_password.getText().toString().trim();
                        if (userName.equalsIgnoreCase("") || userName.length() == 0 || password.equalsIgnoreCase("") || password.length() == 0) {
                            showToast("Please check your username and password !");
                        } else {
                            mInstagram.authorize(mAuthListener);
                        }
                    }
                });
            }
        }
    }

    private void mainAction() {
        callbackManager = CallbackManager.Factory.create();

        gv_images = (GridView) findViewById(R.id.gridView);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.SwipeRefreshLayout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(gv_images);
        fab.setColorNormalResId(R.color.deleted_background);
        fab.setColorPressedResId(R.color.deleted_background);
        nickname = (TextView) findViewById(R.id.txt_nickname);
        post = (TextView) findViewById(R.id.txt_posts);
        follower = (TextView) findViewById(R.id.txt_follwers);
        following = (TextView) findViewById(R.id.txt_Following);
        img_avatar = (ImageView) findViewById(R.id.image_avatar);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        pb_loadImage = (ProgressBar) findViewById(R.id.progressBar);
        mrequestQueue = Volley.newRequestQueue(this);
        getUserInfo();
        getFromSdcard();
        imageAdapter = new ImageAdapter();
        gv_images.setAdapter(imageAdapter);

        if (Utility.getAddImage(getBaseContext())){
            fab.setVisibility(View.GONE);
        }else{
            fab.setVisibility(View.VISIBLE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshItems();
                mSwipeRefreshLayout.setRefreshing(false);
                oldFileArrayImage.clear();
                oldFileArrayImage.addAll(fileArrayImage);
            }

            void refreshItems() {
                getImages();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        gv_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Utility.getAddImage(getBaseContext()) || position > 0) {
                    rowViewInclick = view;
                    selectedPosition = position;
                    RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootLayout);

                    popoverView = new PopoverView(Login.this, R.layout.popover_showed_view);
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;
                    Point p;
                    if (width <= 768) {
                        p = new Point(300, 430);

                    } else {
                        if (width > 768 && width <= 1080) {
                            p = new Point(400, 590);
                        } else {
                            p = new Point(500, 760);
                        }
                    }
                    popoverView.setContentSizeForViewInPopover(p);
                    popoverView.setDelegate(Login.this);
                    popoverView.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(view), PopoverView.PopoverArrowDirectionAny, true);

                    ImageView img = (ImageView) view.findViewById(R.id.foregroundImage);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        img.setImageDrawable(Login.this.getDrawable(R.drawable.ticker));
                    } else {
                        img.setImageDrawable(getResources().getDrawable(R.drawable.ticker));
                    }

                } else {
                    Intent intent = new Intent(Login.this, ListAlbumActivity.class);
                    startActivityForResult(intent, 67);
                }
            }
        });

//        gv_images.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {}
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if ((lastInScreen == totalItemCount) && !isFull && !isLoad ) {
//                    isLoad = true;
//                    final int oldSize = fileArrayImage.size();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getFromSdcard();
//                            for (int i = oldSize - 1 ; i < fileArrayImage.size() -1 ; i++){
//
//                            }
//                            imageAdapter.notifyDataSetChanged();
//                            numberPages = numberPages + NUMBER_OF_PAGES;
//                            isLoad = false;
//                        }
//                    } , 500);
//                }
//            }
//        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ListAlbumActivity.class);
                startActivityForResult(intent, 67);
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private Instagram.InstagramAuthListener mAuthListener = new Instagram.InstagramAuthListener() {
        @Override
        public void onSuccess(InstagramUser user) {
            finish();
            startActivity(new Intent(Login.this, Login.class));
        }

        @Override
        public void onError(String error) {
            showToast(error);
        }

        @Override
        public void onCancel() {
            showToast("OK. Maybe later?");
        }
    };

    protected void showProgresBar() {
        pb_loadImage.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar() {
        pb_loadImage.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void getImages() {
        showProgresBar();
        final Map<String, String> photoList = new HashMap<>();
        String url = "https://api.instagram.com/v1/users/self/media/recent/?access_token=" + mInstagramSession.getAccessToken();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (new JSONTokener(response).more()) {
                    try {
                        JSONObject json = (JSONObject) new JSONTokener(response).nextValue();
                        JSONArray jsonData = json.getJSONArray("data");
                        int length = jsonData.length();

                        if (length > 0) {
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonPhoto = jsonData.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution");
                                photoList.put(jsonData.getJSONObject(i).getString("created_time"), jsonPhoto.getString("url"));
                            }
                            downloadAndSaveToLocal(photoList);

                        } else {
                            hideProgressBar();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgressBar();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getImages();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("COUNT", "10");
                return param;
            }
        };
        mrequestQueue.add(stringRequest);
    }

    private void queceDowloadImage(final List<String> key, final List<String> value, final Response.Listener<Boolean> listener) {
        if (key.size() > 0) {
            Boolean isNewImage = true;
            for (int j = 0; j < fileArrayImage.size(); j++) {
                if (fileArrayImage.get(j).contains(key.get(0)) || checkImageIsInBlacklist(key.get(0))) {
                    isNewImage = false;
                }
            }
            if (isNewImage) {
                ImageRequest ir = new ImageRequest(value.get(0), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        saveToInternalStorage(response, key.get(0));
                        key.remove(0);
                        value.remove(0);
                        if (key.size() > 0) {
                            queceDowloadImage(key, value, new Response.Listener<Boolean>() {
                                @Override
                                public void onResponse(Boolean response) {
                                    if (response) {
                                        listener.onResponse(true);
                                    } else {
                                        listener.onResponse(false);
                                    }
                                }
                            });
                        } else {
                            listener.onResponse(true);
                        }
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResponse(false);
                    }
                });
                mrequestQueue.add(ir);
            } else {
                key.remove(0);
                value.remove(0);
                if (key.size() > 0) {
                    queceDowloadImage(key, value, new Response.Listener<Boolean>() {
                        @Override
                        public void onResponse(Boolean response) {
                            if (response) {
                                listener.onResponse(true);
                            } else {
                                listener.onResponse(false);
                            }
                        }
                    });
                } else {
                    listener.onResponse(true);
                }
            }
        } else {
            listener.onResponse(true);
        }
    }

    private void downloadAndSaveToLocal(final Map<String, String> urlList) {

        List<String> key = new ArrayList<>();
        arrayImageInInstagram.clear();
        List<String> value = new ArrayList<>();
        for (final Map.Entry<String, String> entry : urlList.entrySet()) {
            key.add(entry.getKey() + ".jpg");
            value.add(entry.getValue());
        }
        arrayImageInInstagram.addAll(key);
        queceDowloadImage(key, value, new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response) {
                    getFromSdcard();
                    if (oldFileArrayImage == null || oldFileArrayImage.size() != fileArrayImage.size()){
                        imageAdapter.notifyDataSetChanged();
                        gv_images.invalidateViews();
//                        isLoad = false;
                    }
                    hideProgressBar();
                } else {
                    showToast("Something wrong on load image!");
                    hideProgressBar();
                }
            }
        });

    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/instaplan");
        myDir.mkdirs();
        File mypath = new File(myDir, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Use the compress method on the BitMap object to write image to the OutputStream
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        return myDir.getAbsolutePath();
    }

    public void getFromSdcard() {
        ArrayList<String> listOfInstagram = new ArrayList<String>();
        ArrayList<String> listImageAdd = new ArrayList<String>();
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root + "/instaplan");
        file.mkdirs();

        if (file.isDirectory()) {
            listFile = file.listFiles();
            Arrays.sort(listFile);
            boolean check;
//            if (numberPages > listFile.length){
//                numberPages = listFile.length;
////                isFull = true;
//            }
            for (int i = listFile.length - 1; i >= 0; i--){
                check = false;
                for (int j=0 ; j<arrayImageInInstagram.size() ; j++){
                    if (arrayImageInInstagram.get(j).equals(listFile[i].getName())){
                        check = true;
                        break;
                    }
                }
                if(check){
                    listOfInstagram.add(listFile[i].getAbsolutePath());
                }else{
                    listImageAdd.add(listFile[i].getAbsolutePath());
                }
            }
            Log.e("Delete Image" , listImageAdd.size() + "");
            if (listImageAdd.size() == 0){
                Utility.setAddImage(getBaseContext() , true);
            }
            fileArrayImage.clear();
            fileArrayImage.addAll(listImageAdd);
            fileArrayImage.addAll(listOfInstagram);
        }
    }

    @Override
    public void popoverViewWillShow(PopoverView view) {

    }

    @Override
    public void popoverViewDidShow(PopoverView view) {

    }

    @Override
    public void popoverViewWillDismiss(PopoverView view) {

    }

    @Override
    public void popoverViewDidDismiss(PopoverView view) {
        ImageView img = (ImageView) rowViewInclick.findViewById(R.id.foregroundImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img.setImageResource(android.R.color.transparent);
        } else {
            img.setImageResource(android.R.color.transparent);
        }
    }

    @Override
    public void clickItem(PopoverView view, int idButton) {
        switch (idButton) {
            case 0:
                addImage();
                popoverView.dissmissPopover(true);
                break;
            case 1:
                gotoCropview();
                popoverView.dissmissPopover(true);
                break;
            case 2:
                gotoPreShareview();
                popoverView.dissmissPopover(true);
                break;
            case 3:
                deleteImage();
                popoverView.dissmissPopover(true);
                break;
            case 4:
                logout();
                popoverView.dissmissPopover(true);
                break;
            default:
                break;
        }
    }

    private void gotoPreShareview() {
        /*mPreShareFramgnet = new preShareFragment(Login.this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(android.R.id.content, mPreShareFramgnet).commit();*/
        this.toShare();
    }

    private void logout() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage("Are you sure to logout !")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mInstagramSession.reset();
                        Intent it = new Intent(Login.this, MainActivity.class);
                        Login.this.finish();
                        startActivity(it);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
        builder.show();
    }

    private void addImage() {
        Intent it = new Intent(Login.this, getImageFromLocal.class);
        startActivityForResult(it, 67);
    }

    private void deleteImage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage("Are you sure to delete it !")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        File file = new File(listFile[listFile.length - 1 - selectedPosition].getAbsolutePath());
                        boolean deleted = file.delete();
                        if (deleted) {
                            addToBlackListImage(fileArrayImage.get(listFile.length - 1 - selectedPosition));
                            fileArrayImage.remove(listFile.length - 1 - selectedPosition);
                            getFromSdcard();
                            if (Utility.getAddImage(getBaseContext())){
                                fab.setVisibility(View.GONE);
                            }
                            imageAdapter.notifyDataSetChanged();
                            gv_images.invalidateViews();
                            mDeletedFragment = new deletedFragment(Login.this);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                            fragmentTransaction.replace(android.R.id.content, mDeletedFragment).commit();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private Set<String> getBlackListImage() {
        SharedPreferences sharedpreferences = getSharedPreferences("black_list_image", Context.MODE_PRIVATE);
        return sharedpreferences.getStringSet("delete_list", new HashSet<String>());
    }

    private void addToBlackListImage(String imageLink) {
        SharedPreferences sharedpreferences = getSharedPreferences("black_list_image", Context.MODE_PRIVATE);
        Set<String> images = sharedpreferences.getStringSet("delete_list", new HashSet<String>());
        images.add(imageLink);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet("delete_list", images);
        editor.apply();
    }

    private Boolean checkImageIsInBlacklist(String imageLink) {
        for (String imageid : getBlackListImage()) {
            if (imageid.contains(imageLink)) {
                return true;
            }
        }
        return false;
    }

    private void gotoCropview() {
        Uri uri = Uri.parse(new File("file://" + listFile[listFile.length - 1 - selectedPosition].getPath()).toString());

        UCrop uCrop = UCrop.of(uri, uri);

        uCrop.start(Login.this, UCrop.REQUEST_CROP);
    }

    @Override
    public void remove() {
        if (mDeletedFragment != null && mDeletedFragment.isAdded()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.remove(mDeletedFragment).commit();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void removeShare() {
        if (mshareFragment != null && mshareFragment.isAdded()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.remove(mshareFragment).commit();

        } else {
            onBackPressed();
        }
    }

    @Override
    public void share(int sns_id) {
        switch (sns_id) {
            case 0:
                shareInsta();
                break;
            case 1:
                shareFacebook();
                break;
            case 2:
                shareWhatsapp();
                break;
            case 3:
                shareTwitter();
                break;
            default:
                break;
        }
    }

    private void shareWhatsapp() {
        Uri uri = Uri.parse(new File("file://" + listFile[listFile.length - 1 - selectedPosition].getPath()).toString());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage("com.whatsapp");
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(share);
        } catch (android.content.ActivityNotFoundException ex) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
            showToast("Please install whatsapp from Google Play");
        }
    }

    private boolean verificaInstagram() {
        boolean installed = false;
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.instagram.android", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    private boolean verificaTwitter() {
        boolean installed = false;
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.twitter.android", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    private boolean verificaFacebook() {
        boolean installed = false;

        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    private void shareInsta() {
        if (verificaInstagram()) {
            String type = "image/*";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setPackage("com.instagram.android");
            share.setType(type);
            Uri uri = Uri.parse(new File("file://" + listFile[listFile.length - 1 - selectedPosition].getPath()).toString());
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(share);
        } else {
            showToast("Please install instagram app on Google play!");
        }
    }


    private void shareFacebookPhoto() {
        ShareDialog shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            Uri uri = Uri.parse(new File("file://" + listFile[listFile.length - 1 - selectedPosition].getPath()).toString());
            SharePhoto photo = new SharePhoto.Builder()
                    .setImageUrl(uri)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            shareDialog.show(content);
        } else {
            showToast("Please install facebook app to share photo");
        }

    }

    private void shareFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            shareFacebookPhoto();
        } else {
            if (verificaFacebook()) {
                shareFacebookPhoto();
            } else {
                btn_loginFacebook = (LoginButton) findViewById(R.id.btn_loginFacebook);
                btn_loginFacebook.setReadPermissions("email");

                btn_loginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        shareFacebookPhoto();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

                btn_loginFacebook.callOnClick();
            }

        }
    }

    private void shareTwitter() {
        if (verificaTwitter()) {
            Intent tweetIntent = new Intent(Intent.ACTION_SEND);
            tweetIntent.setPackage("com.twitter.android");
            Uri uri = Uri.parse(new File("file://" + listFile[listFile.length - 1 - selectedPosition].getPath()).toString());
            String type = "image/*";
            tweetIntent.setType(type);
            tweetIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(tweetIntent);
        } else {
            showToast("Please install Twitter app from Google play");
        }
    }

    @Override
    public void removePreShare() {
        if (mPreShareFramgnet != null && mPreShareFramgnet.isAdded()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.remove(mPreShareFramgnet).commit();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void toShare() {
        mshareFragment = new shareFragment(Login.this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(android.R.id.content, mshareFragment).commit();
    }

    private boolean isCrop = false;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (Utility.getAddImage(getBaseContext()))
                return fileArrayImage.size() + 1;
            else
                return fileArrayImage.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.image_row, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.img_android);
                holder.imagePlus = (ImageView) convertView.findViewById(R.id.img_plus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0 && Utility.getAddImage(getBaseContext())) {
                holder.imagePlus.setVisibility(View.VISIBLE);
                holder.imageview.setVisibility(View.INVISIBLE);

            } else {
                holder.imagePlus.setVisibility(View.GONE);
                holder.imageview.setVisibility(View.VISIBLE);
                if (Utility.getAddImage(getBaseContext()))
                    position = position - 1;
                if (isCrop) {
                    Picasso.with(Login.this).load(new File(fileArrayImage.get(position))).resize(200, 0).placeholder(R.drawable.thumb).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageview);
                } else {
                    Picasso.with(Login.this).load(new File(fileArrayImage.get(position))).resize(200, 0).placeholder(R.drawable.thumb).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageview);
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView imageview;
        ImageView imagePlus;
    }

    private void getUerProfile(String url) {
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                img_avatar.setImageBitmap(response);
                getImages();
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getImages();
            }
        });
        mrequestQueue.add(ir);
    }

    public void getUserInfo() {
//        isLoad = true;
        String url = "https://api.instagram.com/v1/users/self/?access_token=" + mInstagramSession.getAccessToken();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (new JSONTokener(response).more()) {
                    try {
                        JSONObject json = (JSONObject) new JSONTokener(response).nextValue();
                        JSONObject jsonData = json.getJSONObject("data");
                        nickname.setText("@" + jsonData.getString("full_name"));
                        post.setText(jsonData.getJSONObject("counts").getString("media"));
                        follower.setText(jsonData.getJSONObject("counts").getString("followed_by"));
                        following.setText(jsonData.getJSONObject("counts").getString("follows"));
                        getUerProfile(jsonData.getString("profile_picture"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getUserInfo();
            }
        });
        mrequestQueue.add(stringRequest);
    }

    private int CODE_IMAGE_LOCAL = 67;
    private int RESULT_SELECTED = 1;
    private int RESULT_CAMERA_DONE = 11;
    private int RESULT_CAMERA_CROP = 21;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult" , resultCode + " - " + Utility.getAddImage(getBaseContext()));
        if (resultCode != RESULT_CANCELED){
            Utility.setAddImage(getBaseContext() , false);
            if (Utility.getAddImage(getBaseContext())){
                fab.setVisibility(View.GONE);
            }else{
                fab.setVisibility(View.VISIBLE);
            }
        }
        Log.e("onActivityResult" , resultCode + " - " + Utility.getAddImage(getBaseContext()));
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            isCrop = true;
            getFromSdcard();
            imageAdapter = new ImageAdapter();
            gv_images.setAdapter(imageAdapter);
            imageAdapter.notifyDataSetChanged();
        } else {
            if (requestCode == CODE_IMAGE_LOCAL) {
                if (resultCode == RESULT_SELECTED) {
                    String path = data.getStringExtra("path");
                    if (path.equals(MyAplication.SELECTED)) {
                        if (MyAplication.imageSelected != null && MyAplication.imageSelected.size() > 0) {
                            for (ImageSelectItem item : MyAplication.imageSelected) {
                                Bitmap bm = decodeSampledBitmapFromUri(item.getPath(), 500, 500);
                                saveToInternalStorage(bm, System.currentTimeMillis() + "");
                            }
                            getFromSdcard();
                            imageAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Bitmap bm = decodeSampledBitmapFromUri(data.getStringExtra("path"), 500, 500);
                        saveToInternalStorage(bm, System.currentTimeMillis() + "");
                        getFromSdcard();
                        imageAdapter.notifyDataSetChanged();
                    }
                } else if (resultCode == RESULT_CAMERA_DONE) {
                    getFromSdcard();
                    imageAdapter.notifyDataSetChanged();

                } else if (resultCode == RESULT_CAMERA_CROP) {
                    getFromSdcard();
                    imageAdapter.notifyDataSetChanged();
                }


            }
        }
    }

    @Override
    public void onBackPressed() {

        if (popoverView != null && popoverView.isShown()) {
            popoverView.dissmissPopover(true);
        } else {
            if (mDeletedFragment != null && mDeletedFragment.isAdded()) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.remove(mDeletedFragment).commit();
            } else {
                if (mshareFragment != null && mshareFragment.isAdded()) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.remove(mshareFragment).commit();
                } else {
                    if (mPreShareFramgnet != null && mPreShareFramgnet.isAdded()) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        fragmentTransaction.remove(mPreShareFramgnet).commit();
                    } else {
                        super.onBackPressed();
                    }
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mainAction();
                } else {
                    showToast("Please reload app and accept for external storage permission !");
                }
                break;
            case Utility.REQUEST_CAMERA_PERMISSION:
                break;

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
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    private class decodeImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageToset;

        public decodeImage(ImageView imageToset) {
            this.imageToset = imageToset;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm = decodeSampledBitmapFromUri(params[0], 500, 500);
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

}