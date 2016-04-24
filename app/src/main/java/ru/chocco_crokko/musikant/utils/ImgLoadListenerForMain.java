package ru.chocco_crokko.musikant.utils;


import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ru.chocco_crokko.musikant.R;


/*
 * additional logic for ListView in MainActivity
 */
public class ImgLoadListenerForMain extends ImgLoadListener
{
    public ImgLoadListenerForMain(ProgressBar progressBar)
    {
        super(progressBar);
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        super.onLoadingStarted(imageUri, view);
        ((ImageView) view).setImageResource(R.drawable.ic_ghost);
    }


}
