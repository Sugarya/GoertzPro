package com.sugary.goertzpro.scene.camera;

import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sugary.goertzpro.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ethan 2017/08/16
 */
public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";

    private static final int CODE_GALLERY = 1;


    @BindView(R.id.container_footer_add)
    LinearLayout mLlFooterAdd;

    @BindView(R.id.img_activity_camera)
    ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.container_footer_add)
    public void onFooterAddClick(){
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if(Build.VERSION.SDK_INT >= 18){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent, "长按选择多张图片"), CODE_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == CODE_GALLERY){

        }
    }
}
