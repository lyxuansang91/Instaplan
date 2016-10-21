package me.feedplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.smr.feedplanner.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramSession;
import net.londatiga.android.instagram.InstagramUser;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "GBOOnY627Veilc2vJugY2OYXm";
    private static final String TWITTER_SECRET = "PAXWd3hSbcVxvlme7GSg6J3hGeaQZiB6noF7jNHFyznkWIVQpj";

    Button btn_login;
    private InstagramSession mInstagramSession;
    private Instagram mInstagram;

    //    private static final String CLIENT_ID = "6b2cb3e2c129486a95d4e70661a4942d";
//    private static final String CLIENT_SECRET = "58cf6ea88f494079a5785dd373c8746a";
//    private static final String REDIRECT_URI = "https://www.instagram.com/thanhsmr/";
    private static final String CLIENT_ID = "44cdda39501e43799baef40906424eb2";
    private static final String CLIENT_SECRET = "b15428252491471baae9bde85d7442f9";
    private static final String REDIRECT_URI = "http://feedplanner.me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        mInstagram = new Instagram(this, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);

        mInstagramSession = mInstagram.getSession();

        if (mInstagramSession.isActive()) {
            Intent it = new Intent(MainActivity.this, Login.class);
            startActivity(it);
            MainActivity.this.finish();
        } else {
            setContentView(R.layout.activity_main);

            btn_login = (Button) findViewById(R.id.btn_login);

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInstagram.authorize(mAuthListener);
                }
            });
        }


    }

    private Instagram.InstagramAuthListener mAuthListener = new Instagram.InstagramAuthListener() {
        @Override
        public void onSuccess(InstagramUser user) {
            finish();

            startActivity(new Intent(MainActivity.this, Login.class));
        }

        @Override
        public void onError(String error) {

        }

        @Override
        public void onCancel() {


        }
    };


}
