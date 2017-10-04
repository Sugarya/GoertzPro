package com.sugary.goertzpro.scene.banner;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sugary.goertzpro.R;
import com.sugary.goertzpro.utils.ImageLoader;

import butterknife.BindView;

/**
 * Created by Ethan 2017/09/21
 */
public class ImageFragment extends Fragment {

    private static final String TAG ="ImageFragment";

    @BindView(R.id.shadow_img)
    ImageView mImg;


    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance() {

        Bundle args = new Bundle();

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        return view;
    }

    public void displayImg(String url){
        ImageLoader.display(getContext(), url, new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mImg.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }




}
