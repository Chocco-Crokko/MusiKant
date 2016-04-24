package ru.chocco_crokko.musikant.utils;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


/*
 * logic, when the image starts and ends downloading
 */
public class ImgLoadListener implements ImageLoadingListener
{
    protected ProgressBar progressBar;

    public ImgLoadListener(ProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        progressBar.setVisibility(View.GONE);
    }
}
