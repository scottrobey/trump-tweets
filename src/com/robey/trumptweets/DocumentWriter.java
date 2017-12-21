package com.robey.trumptweets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

/**
 * Created by scott on 2/18/17.
 */
public class DocumentWriter implements AutoCloseable{
    //private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").setPrettyPrinting().create();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Log log;
    private final JsonWriter writer;

    private final AtomicInteger numberOfTweets = new AtomicInteger(0);

    DocumentWriter(Log log, File fileToWriteTo) throws IOException {
        this.log = log;
        writer = new JsonWriter(new FileWriter(fileToWriteTo));
    }

    public void start() throws Exception {
        writer.setIndent("  ");
        writer.beginObject();
        writer.name("docs");
        writer.beginArray();
    }
    public void write(TrumpTweet tweet) throws Exception {
        gson.toJson(tweet, TrumpTweet.class, writer);
        numberOfTweets.incrementAndGet();
    }

    public int getNumberOfTweets() {
        return numberOfTweets.get();
    }

    @Override
    public void close() throws Exception {
        writer.endArray(); writer.endObject();
        writer.flush(); writer.close();
    }
}
