package me.feedplanner.activity;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import me.feedplanner.util.TypefaceUtil;
import me.feedplanner.model.ImageSelectItem;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by SMR on 9/11/2016.
 */
public class MyAplication extends Application {

    private static MyAplication mInstance;
    public static ArrayList<ImageSelectItem> imageSelected;

    public static final String SELECTED = "selected";

    public static MyAplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mInstance = this;
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fontNormal.ttf");
        changeAppFont();
    }

    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        if (isVersionGreaterOrEqualToLollipop()) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("sans-serif", newTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isVersionGreaterOrEqualToLollipop() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        return false;
    }

    private void changeAppFont() {

        setDefaultFont(this, "DEFAULT", "fontNormal.ttf");
        setDefaultFont(this, "DEFAULT_BOLD", "fontBold.ttf");
//
//        setDefaultFont(this, "MONOSPACE", "fonts/FuturaLT-Oblique.ttf");
//        setDefaultFont(this, "SANS_SERIF", "fonts/FuturaLT.ttf");
        setDefaultFont(this, "SERIF", "fontNormal.ttf");
    }
}
