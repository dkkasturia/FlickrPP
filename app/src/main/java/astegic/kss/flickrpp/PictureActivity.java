package astegic.kss.flickrpp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PictureActivity extends AppCompatActivity {

    ListView picture_list;

    ListAdapter picture_data;

    Context contxt;

    public static String PICTURE_URL = "picture_url";

    public static String PICTURE_TITLE = "picture_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contxt = this;

        picture_list = (ListView) findViewById(R.id.picture_list);

        picture_data = new ListAdapter();

        picture_list.setAdapter(picture_data);

        picture_data.fillAdapter();

        picture_data.notifyDataSetChanged();

        picture_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(contxt, BiggerPicture.class);

                String pict_url_full = (String)picture_data.getItem(position);
                intent.putExtra(PICTURE_URL, pict_url_full);

                String pict_title_full = (String)picture_data.getItemTitle(position);
                intent.putExtra(PICTURE_TITLE, pict_title_full);

                startActivity(intent);
            }
        });

    }


    private class ListAdapter extends BaseAdapter {
        String [] pict_titles = new String[MainActivity.MAX_PHOTOS];
        String [] pict_thmb_urls = new String[MainActivity.MAX_PHOTOS];
        String [] pict_urls = new String[MainActivity.MAX_PHOTOS];

        public ListAdapter() {
            super();
        }

        public void fillAdapter(){
            for (int i=0; i<MainActivity.MAX_PHOTOS; i++){

                if (MainActivity.photo_info_array[i] != null) {
                    pict_titles[i] = MainActivity.photo_info_array[i].getPicture_title();
                    pict_thmb_urls[i] = MainActivity.photo_info_array[i].getThumbnail_url();
                    pict_urls[i] = MainActivity.photo_info_array[i].getPicture_url();
                }
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pict_thmb_urls.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return pict_urls[position];
        }

        public String getItemTitle(int position) {
            return pict_titles[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.picture_row, parent, false);

            ImageView pict_img = (ImageView) row
                    .findViewById(R.id.picture_img);

            TextView tv_pict_title = (TextView) row
                    .findViewById(R.id.picture_title);


            tv_pict_title.setText(pict_titles[position]);

            String pict_url = pict_thmb_urls[position];

            // download image before setting
            Picasso.with(contxt).load(pict_url).into(pict_img);

            return row;
        }

    }

}
