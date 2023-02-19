package com.pd.chatapp.view.activities.display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.pd.chatapp.R;
import com.pd.chatapp.common.Common;
import com.pd.chatapp.databinding.ActivityViewImageBinding;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_image);

        try {
            binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);
        }
        catch (Exception ignored){

        }

    }
}
