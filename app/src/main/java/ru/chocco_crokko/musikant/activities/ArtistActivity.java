package ru.chocco_crokko.musikant.activities;

import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.chocco_crokko.musikant.R;
import ru.chocco_crokko.musikant.data.DBHelper;
import ru.chocco_crokko.musikant.utils.DBConstants;
import ru.chocco_crokko.musikant.utils.ImgLoadListener;

/*
 * This activity starts, when artist in ListView in MainActivity was selected
 */
public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvLink;
    private ProgressBar progressBar;
    private Cursor cursor;
    private FloatingActionButton fab;
    private int isInFavourite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_art);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // get id from MainActivity
        long id = getIntent().getLongExtra(DBConstants.ID, -1);
        if (id != -1) {
            try {
                init(id);
            } catch (NullPointerException e) {
                this.onBackPressed();
                Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    // set text and image from database
    void init(long id) throws NullPointerException
    {
        cursor = new DBHelper(this).getWritableDatabase().query(DBConstants.TABLE_NAME, null,
                DBConstants.ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            ImageLoader.getInstance().displayImage(cursor.getString(cursor.getColumnIndex(DBConstants.BIG_IMAGE)), ((ImageView) findViewById(R.id.bigImage)),
                    new ImgLoadListener(progressBar));
            isInFavourite = cursor.getInt(cursor.getColumnIndex(DBConstants.IN_FAVORITE));
            setFabIcon();
            String name = cursor.getString(cursor.getColumnIndex(DBConstants.NAME));
            setTitle(name);
            ((TextView) findViewById(R.id.genresDetail)).setText(cursor.getString(cursor.getColumnIndex(DBConstants.GENRES)));
            StringBuilder buffer = new StringBuilder();
            int albums =  cursor.getInt(cursor.getColumnIndex(DBConstants.ALBUMS)), tracks = cursor.getInt(cursor.getColumnIndex(DBConstants.TRACKS));
            buffer.append(getResources().getQuantityString(R.plurals.albums, albums, albums))
                    .append(" • ")
                    .append(getResources().getQuantityString(R.plurals.tracks, tracks, tracks));
            ((TextView) findViewById(R.id.albumsTrackDetail)).setText(buffer);
            ((TextView) findViewById(R.id.description)).setText(name + " - " + cursor.getString(cursor.getColumnIndex(DBConstants.DESCRIPTION)));
            tvLink = (TextView) findViewById(R.id.link);
            String link = cursor.getString(cursor.getColumnIndex(DBConstants.LINK));
            if (link != null)
                tvLink.setText(link);
            else
                tvLink.setVisibility(View.GONE);
        }
    }

    private void setFabIcon()
    {
        if (isInFavourite == 0)
            fab.setImageResource(R.drawable.ic_plus);
        else
            fab.setImageResource(R.drawable.ic_minus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // set animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.empty, R.anim.artist_activity_gone);
    }

    @Override
    public void onClick(View v) {
        ContentValues contentValues = new ContentValues();
        isInFavourite ^= 1;
        setFabIcon();
        contentValues.put(DBConstants.IN_FAVORITE, isInFavourite);
        new DBHelper(this).getWritableDatabase().update(DBConstants.TABLE_NAME, contentValues, DBConstants.ID + " = ?",
                new String[] {String.valueOf(cursor.getInt(cursor.getColumnIndex(DBConstants.ID)))});
        Snackbar.make(v, (isInFavourite == 1? getString(R.string.added_in_favorite) : getString(R.string.deleted_from_favorite)), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }
}
