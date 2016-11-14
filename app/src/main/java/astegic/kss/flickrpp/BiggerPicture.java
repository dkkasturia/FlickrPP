package astegic.kss.flickrpp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BiggerPicture extends AppCompatActivity {

    String picture_url_comp;

    String picture_title_comp;

    ImageView pict_img;

    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigger_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pict_img = (ImageView) findViewById(R.id.img_bigger);

        tv_title = (TextView) findViewById(R.id.title_img_bigger);

        picture_url_comp = getIntent().getStringExtra(PictureActivity.PICTURE_URL);

        picture_title_comp = getIntent().getStringExtra(PictureActivity.PICTURE_TITLE);

        Picasso.with(this).load(picture_url_comp).into(pict_img);

        tv_title.setText(picture_title_comp);


    }

    public void goBack(View view)  {
        this.finish();

    }

}
