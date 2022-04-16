package com.example.integratingchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.integratingchat.adapter.Theme;
import com.example.integratingchat.ui.ChatActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Coustomize extends AppCompatActivity {

    String nameFriend="";
    String avata="";
    String roomId="";
    String idFriend="";
    CircleImageView icon_avata;
    private RecyclerView recyclerView;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    private ArrayList<String> gradlist;
    private Theme themeAdapter;
    public static Activity coustomize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coustomize);

        coustomize=this;

        nameFriend=getIntent().getStringExtra("sendingidoffriend");
        avata=getIntent().getStringExtra("avatarOFfriend2509");
        roomId=getIntent().getStringExtra("roomIdoffriend");
        idFriend=getIntent().getStringExtra("friendIDoffriend");
        icon_avata=findViewById(R.id.icon_avata);

        Window window = Coustomize.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setNavigationBarColor(ContextCompat.getColor(Coustomize.this, R.color.black));

        recyclerView = findViewById(R.id.rv_grid);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        gradlist = new ArrayList<>();
        gradlist.add("");
        gradlist.add("Inlfuenza");
        gradlist.add("Nelson");
        gradlist.add("Lemon Twist");
        gradlist.add("Hazel");
        gradlist.add("Blurry Beach");
        gradlist.add("Misty Meadow");
        gradlist.add("Dance To Forget");
        gradlist.add("Star Fall");
        gradlist.add("Man Of Steel");
        gradlist.add("Purple Bliss");
        gradlist.add("Ali");
        gradlist.add("Deep Sea Space");
        gradlist.add("Royal");
        gradlist.add("Mauve");
        gradlist.add("Jupiter");
        gradlist.add("AstroGrad");
        gradlist.add("Nehozure");
        gradlist.add("cosmic Fusion");
        gradlist.add("The Blue Lagoon");
        gradlist.add("Feel Tonight");
        gradlist.add("CinnaMint");
        gradlist.add("Visions of Grandeur");
        gradlist.add("Crystal Clear");
        gradlist.add("Relay");
        gradlist.add("Terminal");

        themeAdapter = new Theme(gradlist,nameFriend,roomId,idFriend, this);
        recyclerView.setAdapter(themeAdapter);

        byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        icon_avata.setImageBitmap(decodedImage);
        getSupportActionBar().setTitle(nameFriend);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Coustomize.this,ChatActivity.class);
        startActivity(intent);
        finish();
    }
}