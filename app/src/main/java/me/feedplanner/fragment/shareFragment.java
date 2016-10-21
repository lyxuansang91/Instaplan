package me.feedplanner.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smr.feedplanner.R;

/**
 * Created by SMR on 9/10/2016.
 */
public class shareFragment extends Fragment {

    RemoveShareDelegate removeShareFragment;

    public shareFragment(){}
    @SuppressLint("ValidFragment")
    public shareFragment(RemoveShareDelegate removeShareFragment) {
        this.removeShareFragment = removeShareFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share,container,false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn_back = (Button)view.findViewById(R.id.btn_shareBack);
        Button btn_facebook = (Button)view.findViewById(R.id.btn_ShareFacebook);
        Button btn_twitter = (Button)view.findViewById(R.id.btn_ShareTwitter);
        Button btn_Insta = (Button)view.findViewById(R.id.btn_shareInsta);
        Button btn_Whatsapp = (Button)view.findViewById(R.id.btn_shareWhatsapp);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeShareFragment.removeShare();
            }
        });

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeShareFragment.share(1);
            }
        });

        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeShareFragment.share(3);
            }
        });

        btn_Insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeShareFragment.share(0);
            }
        });

        btn_Whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeShareFragment.share(2);
            }
        });
    }

    public  interface RemoveShareDelegate {
        void removeShare();
        void share(int sns_id);
    }
}
