package me.feedplanner.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smr.feedplanner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.feedplanner.model.Album;

public class ListAlbumActivity extends AppCompatActivity {
    private int CODE_IMAGE_LOCAL = 67;
    private ListView lvAlbum;
    private List<Album> albumList;
    ListAlbumAdapter adapter;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_album);
        initUI();
        initData();
        event();
    }

    private void event() {
        lvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent(ListAlbumActivity.this, getImageFromLocal.class);
                it.putExtra("albumName", albumList.get(i).albumName);
                startActivityForResult(it, CODE_IMAGE_LOCAL);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        albumList = new ArrayList<>();
        getAlbum();
        adapter = new ListAlbumAdapter(ListAlbumActivity.this, albumList);
        lvAlbum.setAdapter(adapter);
        btn_back = (Button) findViewById(R.id.btn_imageLocalBack);
    }

    private void initUI() {
        lvAlbum = (ListView) findViewById(R.id.lvAlbum);
    }

    private class ListAlbumAdapter extends BaseAdapter {
        private Context context;
        private List<Album> albumList;
        private LayoutInflater mInflater;

        public ListAlbumAdapter(Context context, List<Album> albumList) {
            this.context = context;
            this.albumList = albumList;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return albumList.size();
        }

        @Override
        public Object getItem(int i) {
            return albumList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_album, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(albumList.get(position).albumName);

            return convertView;
        }

        class ViewHolder {
            TextView tvName;
        }
    }

    private void getAlbum() {
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = managedQuery(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        Log.i("ListingImages", " query count=" + cur.getCount());
        Map<String, String> hashMap = new HashMap<>();
        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            do {
                // Get the field values

                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                hashMap.put(bucket, date);
                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucket
                        + "  date_taken=" + date);
            } while (cur.moveToNext());
        }
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            albumList.add(new Album(entry.getValue(), entry.getKey()));
        }
    }

    private int RESULT_SELECTED = 1;
    private int RESULT_CAMERA_DONE = 11;
    private int RESULT_CAMERA_CROP = 21;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_IMAGE_LOCAL) {
            if (resultCode == RESULT_SELECTED) {
                Intent intent = new Intent();
                String path = data.getStringExtra("path");
                intent.putExtra("path", path);
                setResult(1, intent);
                finish();
            } else if (resultCode == RESULT_CAMERA_DONE) {
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
        }

    }
}
