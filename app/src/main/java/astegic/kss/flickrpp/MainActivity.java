package astegic.kss.flickrpp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    EditText tags;
    String tag;
    public static long [] photo_id_array = new long[]{0,0,0,0,0,0,0,0,0,0,0};

    public static PhotoInfo downloadedPhotoInfo;

    public static int next_photo_to_download =0;

    public static PhotoInfo [] photo_info_array = new PhotoInfo [] {null, null, null, null, null, null, null, null, null, null, null};

    public static MainActivity main_activity_instance;

    public static int MAX_PHOTOS = 10; // maximum allowed is 10


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tags = (EditText) findViewById(R.id.Tags);

        main_activity_instance = this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void findPictures(View view)  {

        tag = tags.getText().toString();

        tag = tag + "&tagmode=all";

        try {
            new FlickrGetPictures().execute(tag);
        } catch (Exception e){
            String msg = e.getMessage();
            msg = e.getLocalizedMessage();
        }
    }

    public void displayPhotoList(){
        // display the photo list

        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);

    }


}
