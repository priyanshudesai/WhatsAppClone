package com.pd.chatapp.view.activities.display;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;
import com.pd.chatapp.R;

public class ImageViewerActivity extends AppCompatActivity {

    ZoomageView imageView;
    //private ImageView imageView;
    private String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);


        imageView=findViewById(R.id.image_viewer);
        imageUrl=getIntent().getStringExtra("url");


//        imageView.setImageBitmap(Common.IMAGE_BITMAP);
        Glide.with(this).load(imageUrl).into(imageView);
    }
}