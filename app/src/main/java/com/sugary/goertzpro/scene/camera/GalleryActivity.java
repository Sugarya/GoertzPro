package com.sugary.goertzpro.scene.camera;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sugary.goertzpro.R;

public class GalleryActivity extends AppCompatActivity {

    private static final int LOADER_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

    }

    private void initGallery(){
        Loader<Cursor> galleryLoader = getLoaderManager().initLoader(LOADER_ID, null, new LoaderCallBack());

    }

    private class LoaderCallBack implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Images.Media.MIME_TYPE +"=? or" + MediaStore.Images.Media.MIME_TYPE + "=?";
            String[] selectionArgs = new String[]{"image/jpeg", "image/png"};
            String sortOrder = MediaStore.Images.ImageColumns.DATE_MODIFIED + " desc";

            CursorLoader cursorLoader = new CursorLoader(GalleryActivity.this, uri, null, selection, selectionArgs, sortOrder);
            return cursorLoader;
        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
