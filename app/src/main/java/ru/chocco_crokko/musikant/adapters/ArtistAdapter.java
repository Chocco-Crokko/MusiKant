package ru.chocco_crokko.musikant.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.chocco_crokko.musikant.R;
import ru.chocco_crokko.musikant.data.DBHelper;
import ru.chocco_crokko.musikant.utils.DBConstants;
import ru.chocco_crokko.musikant.utils.ImgLoadListenerForMain;

/*
 * Adapter for ListView in MainActivity
 */
public class ArtistAdapter extends BaseAdapter
{
    private Context ctx;
    private int resource;
    private LayoutInflater inflater;
    private SQLiteDatabase db;
    private Cursor cursor;

    // save several indexes from database because they will be used a lot of times
    private int idIndex;
    private int imageIndex;
    private int nameIndex;
    private int genresIndex;
    private int albumsIndex;
    private int tracksIndex;

    public ArtistAdapter(Context context, int resource, String order)
    {
        ctx = context;
        db = new DBHelper(ctx).getWritableDatabase();
        // cursor will be sorted
        cursor = db.query(DBConstants.TABLE_NAME, null, null, null, null, null, order);
        this.resource = resource;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // database shouldn't be empty, so no checks
        cursor.moveToFirst();
        idIndex = cursor.getColumnIndex(DBConstants.ID);
        imageIndex = cursor.getColumnIndex(DBConstants.SMALL_IMAGE);
        nameIndex = cursor.getColumnIndex(DBConstants.NAME);
        genresIndex = cursor.getColumnIndex(DBConstants.GENRES);
        albumsIndex = cursor.getColumnIndex(DBConstants.ALBUMS);
        tracksIndex = cursor.getColumnIndex(DBConstants.TRACKS);
    }


    @Override
    public int getCount() {
        return cursor.getCount();
    }
    /*
     * function returns cursor, which was selected from database by id. It will be closed later (in getView)
     */
    @Override
    public Object getItem(int position) {
        long itemId = getItemId(position);
        Cursor c = db.query(DBConstants.TABLE_NAME, null, DBConstants.ID + " = ?", new String[] {String.valueOf(itemId)}, null, null, null);
        c.moveToFirst();
        return c;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(idIndex);
    }

    /*
     * function changes cursor sorting, also updates adapter
     */
    public void changeSorting(Cursor c)
    {
        cursor.close();
        cursor = c;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = inflater.inflate(resource, null);
            holder = new ViewHolder();
            holder.smallImage = (ImageView) convertView.findViewById(R.id.smallImage);
            holder.artistNamePreview = (TextView) convertView.findViewById(R.id.artistNamePreview);
            holder.genresPreview = (TextView) convertView.findViewById(R.id.genresPreview);
            holder.albumsPreview = (TextView) convertView.findViewById(R.id.albumsPreview);
            holder.tracksPreview = (TextView) convertView.findViewById(R.id.tracksPreview);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Cursor c = (Cursor) getItem(position);
        ImageLoader.getInstance().displayImage(c.getString(imageIndex), holder.smallImage,
                new ImgLoadListenerForMain((ProgressBar) convertView.findViewById(R.id.progressBarSmall)));
        holder.artistNamePreview.setText(c.getString(nameIndex));
        holder.genresPreview.setText(c.getString(genresIndex));
        holder.albumsPreview.setText(String.valueOf(c.getInt(albumsIndex)));
        holder.tracksPreview.setText(String.valueOf(c.getInt(tracksIndex)));
        c.close();
        return convertView;
    }

    /*
     * ViewHolder makes our program faster
     */
    private class ViewHolder
    {
        private ImageView smallImage;
        private TextView artistNamePreview;
        private TextView genresPreview;
        private TextView albumsPreview;
        private TextView tracksPreview;
    }
}