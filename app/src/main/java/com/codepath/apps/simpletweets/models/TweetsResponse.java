package com.codepath.apps.simpletweets.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by smacgregor on 2/17/16.
 */
public class TweetsResponse {

    public static List<Tweet> parseJSON(String response) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
       return gson.fromJson(response, collectionType);
    }
}
