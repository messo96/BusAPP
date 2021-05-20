package com.example.busapp.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.busapp.R;

public class MyProgressLoader {
    AppCompatDialog progressDialog;

    public MyProgressLoader(Context context) {
        progressDialog = new AppCompatDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_layout);
        ImageView imageView = progressDialog.findViewById(R.id.img_anim);
        Glide.with(context).asGif().load(R.raw.bus_loader).into(imageView);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public void show(){
        progressDialog.show();
    }

    public void hide(){
        progressDialog.hide();
    }

}


