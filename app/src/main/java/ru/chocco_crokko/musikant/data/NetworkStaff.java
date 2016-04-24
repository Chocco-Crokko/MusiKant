package ru.chocco_crokko.musikant.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.chocco_crokko.musikant.R;
import ru.chocco_crokko.musikant.deserializers.BunchOfArtistsDeserializer;
import ru.chocco_crokko.musikant.deserializers.ImagesDeserializer;
import ru.chocco_crokko.musikant.models.BunchOfArtists;
import ru.chocco_crokko.musikant.models.Images;

/*
 * class works with Internet, parses from Json file into BunchOfArtists. It uses deserializers, also shows ListView from MainActivity
 */
public class NetworkStaff extends AsyncTask<String, Void, BunchOfArtists> {
    CustomCallBack callback;
    private ProgressDialog dialog;
    public NetworkStaff(final CustomCallBack callback)
    {
        this.callback = callback;
    }
    // interface, which is implemented by MainActivity
    public interface CustomCallBack
    {
        void showList(BunchOfArtists result);
        void showError();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog((Context) callback);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(((Context) callback).getString(R.string.loading));
        dialog.show();
    }

    @Override
    protected BunchOfArtists doInBackground(String... params)
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BunchOfArtists.class, new BunchOfArtistsDeserializer())
                .registerTypeAdapter(Images.class, new ImagesDeserializer())
                .create();
        HttpURLConnection conn = null;
        JsonReader reader = null;
        BunchOfArtists result = null;
        try
        {
            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            if (conn != null)
            {
                conn.connect();
                reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                result = gson.fromJson(reader, BunchOfArtists.class);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (conn != null)
                conn.disconnect();
            try
            {
                if (reader != null)
                    reader.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(BunchOfArtists result)
    {
        dialog.dismiss();
        if (result != null)
            callback.showList(result);
        else
            callback.showError();

    }
}