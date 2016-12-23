package me.feedplanner.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.smr.feedplanner.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import me.feedplanner.util.Utility;

/**
 * Created by toh-member on 12/22/2016.
 */

public class ImageAdapter extends BaseAdapter<ImageAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<String> mList;
    private FragmentActivity mActivity;
    boolean isCrop;

    public ImageAdapter(FragmentActivity activity , ArrayList<String> list) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = list;
        mActivity = activity;
    }

    public void mNotifyDataSetChanged(int position , boolean crop){
        isCrop = crop;
        notifyItemChanged(position);
    }

    public void mNotifyDataSetChanged(boolean crop){
        isCrop = crop;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.image_row , null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0 && Utility.getAddImage(mActivity.getBaseContext())) {
            holder.imagePlus.setVisibility(View.VISIBLE);
            holder.imageview.setVisibility(View.INVISIBLE);

        } else {
            holder.imagePlus.setVisibility(View.GONE);
            holder.imageview.setVisibility(View.VISIBLE);
            if (Utility.getAddImage(mActivity.getBaseContext()))
                position = position - 1;
            if (isCrop) {
                Picasso.with(mActivity).load(new File(mList.get(position))).resize(200, 0).placeholder(R.drawable.thumb).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageview);
            } else {
                Picasso.with(mActivity).load(new File(mList.get(position))).resize(200, 0).placeholder(R.drawable.thumb).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageview);
            }
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (Utility.getAddImage(mActivity.getBaseContext()))
            return mList.size() + 1;
        else
            return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageview;
        ImageView imagePlus;

        public ViewHolder(View itemView) {
            super(itemView);
            imageview = (ImageView) itemView.findViewById(R.id.img_android);
            imagePlus = (ImageView) itemView.findViewById(R.id.img_plus);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            triggerOnItemClickListener(position, v);
        }
    }
}
