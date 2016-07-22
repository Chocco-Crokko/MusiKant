package ru.chocco_crokko.musikant.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ru.chocco_crokko.musikant.R;
import ru.chocco_crokko.musikant.adapters.ArtistAdapter;
import ru.chocco_crokko.musikant.data.DBHelper;
import ru.chocco_crokko.musikant.data.NetworkStaff;
import ru.chocco_crokko.musikant.fragments.AboutFragment;
import ru.chocco_crokko.musikant.models.Artist;
import ru.chocco_crokko.musikant.models.BunchOfArtists;
import ru.chocco_crokko.musikant.utils.DBConstants;

/*
 * Main activity.
 * It calls other activities and classes
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NetworkStaff.CustomCallBack,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private final String URL_TO_GET_DATA_FROM = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
    private SQLiteDatabase db;
    private ArtistAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLL;
    private ListView lvArtists;
    private SearchView searchView;

    // this fields are used with sorting list
    private String currentSort = DBConstants.NAME;
    private boolean isAscending = true;
    private boolean isInFavorite = false;
    private final String descending = " DESC";
    private String currentSearch = "";


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentSort", currentSort);
        outState.putString("currentSearch", currentSearch);
        outState.putBoolean("isAscending", isAscending);
        outState.putBoolean("isInFavorite", isInFavorite);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            currentSort = savedInstanceState.getString("currentSort");
            currentSearch = savedInstanceState.getString("currentSearch");
            isAscending = savedInstanceState.getBoolean("isAscending");
            isInFavorite = savedInstanceState.getBoolean("isInFavorite");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.artists);

        // these code allowed to cache loaded images in memory and disk
        // class ImageLoaders require init(...) function before use
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        errorLL = (LinearLayout) findViewById(R.id.error_ll);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // create database (could be empty)
        db = new DBHelper(this).getWritableDatabase();

        // if database isn't empty then show list of artists, else before it we need to create database from URL
        if (fillDatabaseIfIsNotEmpty())
            showList(null);
    }

    private boolean fillDatabaseIfIsNotEmpty()
    {
        errorLL.setVisibility(View.INVISIBLE);
        if (!db.query(DBConstants.TABLE_NAME, null, null, null, null, null, null).moveToFirst()) {
            NetworkStaff task = new NetworkStaff(this);
            task.execute(URL_TO_GET_DATA_FROM);
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            setSortingInAdapter(currentSort, currentSearch);
    }

    private void createDBFromArrayList(BunchOfArtists arr)
    {
        boolean flag = true;
        db.beginTransaction();
        for (Artist artist : arr.getArtists()) {
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.ID, artist.getId());
            cv.put(DBConstants.NAME, artist.getName());

            // get one String from String[] with commas
            StringBuilder buffer = new StringBuilder();
            String[] genres = artist.getGenres();
            for (int i = 0; i < genres.length; ++i)
            {
                buffer.append(genres[i]);
                if (i != genres.length - 1)
                    buffer.append(", ");
            }
            cv.put(DBConstants.GENRES, artist.getGenres().length == 0? null: buffer.toString());

            cv.put(DBConstants.ALBUMS, artist.getAlbums());
            cv.put(DBConstants.TRACKS, artist.getTracks());
            cv.put(DBConstants.LINK, artist.getLink());
            cv.put(DBConstants.DESCRIPTION, artist.getDescription());
            cv.put(DBConstants.SMALL_IMAGE, artist.getImage().getSmall());
            cv.put(DBConstants.BIG_IMAGE, artist.getImage().getBig());
            cv.put(DBConstants.IN_FAVORITE, 0);
            if (db.insert(DBConstants.TABLE_NAME, null, cv) == -1)
            {
                flag = false;
                break;
            }
        }
        if (flag)
            db.setTransactionSuccessful();
        db.endTransaction();
    }

    /*
     * This method is implemented from NetworkStaff.CustomCallBack interface
     */
    @Override
    public void showList(BunchOfArtists result) {
        if (result != null)
        {
            createDBFromArrayList(result);
        }
        // create adapter, which will sort objects from database
        adapter = new ArtistAdapter(this, R.layout.row, DBConstants.NAME);
        lvArtists = (ListView) findViewById(R.id.lvMain);
        lvArtists.setAdapter(adapter);
        lvArtists.setDivider(null);
        lvArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
                intent.putExtra(DBConstants.ID, id);
                startActivity(intent);
                // set animation between activities
                overridePendingTransition(R.anim.main_activity_gone, R.anim.empty);
            }
        });
    }
    @Override
    public void showError() {
        errorLL.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified())
        {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId())
        {
            case R.id.sort_name:
                changeSortingInAdapter(DBConstants.NAME, currentSearch);
                break;
            case R.id.sort_tracks:
                changeSortingInAdapter(DBConstants.TRACKS, currentSearch);
                break;
            case R.id.sort_albums:
                changeSortingInAdapter(DBConstants.ALBUMS, currentSearch);
                break;
            case R.id.about:
                new AboutFragment().show(getFragmentManager(), "about");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void setSortingInAdapter(String order, String searchText)
    {
        adapter.changeSorting(db.query(DBConstants.TABLE_NAME, null,
                DBConstants.NAME + " like '%" + searchText + "%'" + (isInFavorite? " AND " + DBConstants.IN_FAVORITE + " = 1": ""), null, null, null,
                order + (isAscending? "" : descending)));
    }

    public void changeSortingInAdapter(String order, String searchText)
    {
        if (currentSort == order)
        {
            isAscending = !isAscending;
        } else
        {
            currentSort = order;
            isAscending = true;
        }
        setSortingInAdapter(order, searchText);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId())
        {
            case R.id.artists:
                setTitle(R.string.artists);
                if (!isInFavorite)
                {
                    lvArtists.post(new Runnable() {
                        @Override
                        public void run() {
                            if (lvArtists.getFirstVisiblePosition() > 20) {
                                lvArtists.setSelection(20);
                            }
                            lvArtists.smoothScrollToPositionFromTop(0, 0, 300);
                        }
                    });
                } else
                {
                    isInFavorite = false;
                    setSortingInAdapter(currentSort, currentSearch);
                }
                Toast.makeText(this, getString(R.string.artists), Toast.LENGTH_SHORT).show();
                break;
            case R.id.favorite:
                setTitle(R.string.favorite);
                if (isInFavorite)
                {
                    lvArtists.smoothScrollToPositionFromTop(0, 0, 300);
                }

                else
                {
                    isInFavorite = true;
                    setSortingInAdapter(currentSort, currentSearch);
                }
                Toast.makeText(this, getString(R.string.favorite), Toast.LENGTH_SHORT).show();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                fillDatabaseIfIsNotEmpty();
                setSortingInAdapter(currentSort, currentSearch);
            }
        }, 500);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        currentSearch = newText;
        setSortingInAdapter(currentSort, newText);
        return true;
    }
}
