package com.example.smr.instaplan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smr.instaplan.R;

/**
 * Created by SMR on 9/11/2016.
 */
public class preShareFragment extends Fragment {
    PreShareFragmentFragment preShareFragmentFragment;
    Button btn_preshareback,btn_toshare;

    public preShareFragment(){}
    @SuppressLint("ValidFragment")
    public preShareFragment(PreShareFragmentFragment preShareFragmentFragment) {
        this.preShareFragmentFragment = preShareFragmentFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pre_share,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_preshareback = (Button)view.findViewById(R.id.btn_prShareBack);
        btn_toshare = (Button)view.findViewById(R.id.btn_toShare);

        btn_preshareback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preShareFragmentFragment.removePreShare();
            }
        });

        btn_toshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preShareFragmentFragment.toShare();
            }
        });
    }


    public  interface PreShareFragmentFragment {
        void removePreShare();
        void toShare();
    }
}
