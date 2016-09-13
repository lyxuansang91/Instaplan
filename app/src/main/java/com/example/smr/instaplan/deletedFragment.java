package com.example.smr.instaplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by SMR on 9/10/2016.
 */
public class deletedFragment extends Fragment {
    RemoveDeletedFragment removeDeletedFragment;

    public deletedFragment(){}
    public deletedFragment(RemoveDeletedFragment removeDeletedFragment) {
        this.removeDeletedFragment = removeDeletedFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deleted_fragment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn_close = (Button)view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDeletedFragment.remove();
            }
        });
    }

    public  interface RemoveDeletedFragment {
        void remove();
    }
}
