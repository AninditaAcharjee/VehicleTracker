package com.jkkniu.vehicletracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int TIMER =5000;


    Animation topAnim, bottomAnim;

    ImageView imageView;
    TextView name, jkkniu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
       topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
       bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

       //hooks
        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.name);
        jkkniu = findViewById(R.id.jkkniu);

        //assign animation in image and text
        imageView.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        jkkniu.setAnimation(bottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        },TIMER);
    }
}