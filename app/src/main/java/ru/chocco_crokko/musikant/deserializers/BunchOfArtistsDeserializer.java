package ru.chocco_crokko.musikant.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import ru.chocco_crokko.musikant.models.Artist;
import ru.chocco_crokko.musikant.models.BunchOfArtists;
import ru.chocco_crokko.musikant.models.Images;

/*
 * parse from JsonArray into BunchOfArtists
 */
public class BunchOfArtistsDeserializer implements JsonDeserializer<BunchOfArtists>
{

    @Override
    public BunchOfArtists deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (!json.isJsonArray())
            return null;
        BunchOfArtists result = new BunchOfArtists();
        JsonArray jsonArray = json.getAsJsonArray();
        for (JsonElement jsonElement : jsonArray)
        {
            if (!jsonElement.isJsonObject())
                return null;
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Artist artist = new Artist();
            artist.setId(jsonObject.get("id").getAsInt());
            artist.setName(getStringWithCheck(jsonObject.get("name")));

            // get String array from JsonArray
            Type arrayType = new TypeToken<String[]>(){}.getType();
            artist.setGenres((String[]) new Gson().fromJson(jsonObject.get("genres").getAsJsonArray(), arrayType));

            artist.setAlbums(jsonObject.get("albums").getAsInt());
            artist.setTracks(jsonObject.get("tracks").getAsInt());
            artist.setLink(getStringWithCheck(jsonObject.get("link")));
            artist.setDescription(getStringWithCheck(jsonObject.get("description")));
            artist.setImage((Images) context.deserialize(jsonObject.get("cover"), Images.class));
            result.getArtists().add(artist);
        }
        return result;
    }

    private String getStringWithCheck(JsonElement elem)
    {
        return elem != null? elem.getAsString() : null;
    }

}