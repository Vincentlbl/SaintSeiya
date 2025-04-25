package com.example.saintseiya.utils;

import android.content.Context;
import android.util.Log;

import com.example.saintseiya.model.StoryBlock;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StoryLoader {

    public static List<StoryBlock> loadStory(Context context) {
        try {
            InputStream is = context.getAssets().open("story_blocks.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<StoryBlock>>() {}.getType();

            List<StoryBlock> storyBlocks = gson.fromJson(json, listType);

            if (storyBlocks == null) {
                Log.e("StoryLoader", "Parsed storyBlocks is null!");
                return new ArrayList<>();  
            }

            return storyBlocks;

        } catch (IOException e) {
            Log.e("StoryLoader", "Error reading story_blocks.json", e);
            return new ArrayList<>(); 
        } catch (JsonSyntaxException e) {
            Log.e("StoryLoader", "Error parsing JSON", e);
            return new ArrayList<>();
        }
    }
}

