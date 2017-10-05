package com.sugary.goertzpro.scene.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sugary.goertzpro.R;
import com.sugary.goertzpro.scene.camera.entity.PhotoEntity;
import com.sugary.goertzpro.scene.camera.event.AddPhotoClickEvent;
import com.sugary.goertzpro.utils.RxBus;
import com.sugary.goertzpro.widget.custom.SelectPhotoRecyclerView;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhihu.matisse.Matisse;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Ethan 2017/08/16
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final int CODE_GALLERY = 1;


    @BindView(R.id.container_footer_add)
    LinearLayout mLlFooterAdd;

    @BindView(R.id.img_activity_camera)
    ImageView mImg;

    @BindView(R.id.img_activity_camera2)
    ImageView mImg2;

    @BindView(R.id.recycler_select_photo)
    SelectPhotoRecyclerView mRecyclerView;

    @BindView(R.id.check_tv_view)
    CheckedTextView mCheckedTextView;

    private ImageView[] imgArray = new ImageView[]{mImg, mImg2};
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        initRxBus();
    }

    private void initRxBus(){
        mSubscription = RxBus.getInstance().toSubscription(AddPhotoClickEvent.class, new Action1<AddPhotoClickEvent>() {
            @Override
            public void call(AddPhotoClickEvent addPhotoClickEvent) {
                new RxPermissions(MainActivity.this)
                        .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if(aBoolean){
                                    mRecyclerView.openGallery(MainActivity.this);
                                }else{
                                    Toast.makeText(MainActivity.this, "同意授权相机和读取存储卡内容才能使用搭配功能", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }



    @Override
    protected void onDestroy() {
        mSubscription.unsubscribe();
        super.onDestroy();
    }

    @OnClick(R.id.container_footer_add)
    public void onFooterAddClick() {
//        openGallery3();
        mCheckedTextView.setChecked(true);
    }

    private void openGallery0() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "长按选择多张图片"), CODE_GALLERY);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "长按选择多张图片"), CODE_GALLERY);
        }
    }

    private void openGallery2() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT <= 18) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            Intent chooserIntent = Intent.createChooser(intent, "长按选择多张图片");
            startActivityForResult(chooserIntent, CODE_GALLERY);
        }
    }

    private void openGallery3() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT <= 18) {
            getIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            getIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        getIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, CODE_GALLERY);
    }


    public void onBtnClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_GALLERY:
                    Uri uri = data.getData();
                    String scheme = uri.getScheme();
                    Log.d(TAG, "onActivityResult: scheme = " + scheme);
                    String imagePath = getSelectedImagePathAfterKitKat(uri);
                    displayImage(imagePath, mImg);
                    break;
                case SelectPhotoRecyclerView.REQUEST_PHOTO_CODE:
                    List<Uri> uriList = Matisse.obtainResult(data);
                    List<PhotoEntity> photoEntityList = new ArrayList<>();
                    for(Uri u : uriList){
                        PhotoEntity entity = new PhotoEntity(u);
                        photoEntityList.add(entity);
                    }
                    mRecyclerView.addNewPhotoData(photoEntityList);
                    break;
            }
        }
    }


    private void handleUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            Log.d(TAG, "handleUri: count = " + count);
            while (cursor.moveToNext()) {
                String[] columnNames = cursor.getColumnNames();
                StringBuilder sb = new StringBuilder();
                for (String name : columnNames) {
                    sb.append(name + ":" + columnNames + "\n");
                }
                Log.d(TAG, "handleUri: columnNames[] = " + sb.toString());
            }
            cursor.close();
        }
    }

    //////////////////////////////////////////////方法II
    private Bitmap getBitmapByDescriptor(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //////////////////////////////////////方式I
    @TargetApi(19)
    private String getSelectedImagePathAfterKitKat(Uri uri) {
        String imagePath;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是 document 类型的 Uri，则通过 document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            Log.d(TAG, "getSelectedImagePathAfterKitKat: docId = " + docId);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的 id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                Log.d(TAG, "getSelectedImagePathAfterKitKat: selection = " + selection);
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            } else {
                imagePath = "";
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是 content 类型的 uri ， 则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是 file 类型的 Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        } else {
            imagePath = "";
        }

        return imagePath;
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过 Uri 和 selection 来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath, ImageView img) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            img.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_LONG).show();
        }
    }
}
