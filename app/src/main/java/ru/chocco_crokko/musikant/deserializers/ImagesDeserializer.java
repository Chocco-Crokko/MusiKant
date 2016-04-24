package ru.chocco_crokko.musikant.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ru.chocco_crokko.musikant.models.Images;

/*
 * parse from JsonObject into Images
 */
public class ImagesDeserializer implements JsonDeserializer<Images> {
    @Override
    public Images deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject())
            return null;
        Images result = new Images();
        JsonObject jsonImages = json.getAsJsonObject();
        result.setSmall(jsonImages.get("small").getAsString());
        result.setBig(jsonImages.get("big").getAsString());
        return result;
    }
}
