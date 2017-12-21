package com.robey.trumptweets;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Created by scott on 2/18/17.
 */
public class TwitterReader {
    private final Log log;
    private final DocumentWriter writer;

    // Wed Dec 23 17:38:18 +0000 2009
    static final DateTimeFormatter DF = new DateTimeFormatterBuilder().appendPattern("EEE MMM dd HH:mm:ss Z yyyy").toFormatter();

    // for reading in JSON from
    private final Gson gson = new GsonBuilder().create();

    public static class TrumpArchiveTweet {
        public String id_str;
        public String text;
        public String created_at;
        public boolean is_retweet;
    }

    public TwitterReader(Log log, DocumentWriter writer) {
        this.writer = writer;
        this.log = log;
    }
    public void process(InputStream is) throws Exception {
        List<TrumpArchiveTweet> items = gson.fromJson(IOUtils.toString(is),  new TypeToken<List<TrumpArchiveTweet>>(){}.getType());
        for ( TrumpArchiveTweet t : items ) {
            LocalDateTime dateTime = LocalDateTime.parse(t.created_at, DF);
            Date date = Date.from(dateTime.toInstant(ZoneOffset.ofTotalSeconds(0)));
            TrumpTweet tweet = TrumpTweet.regularTweet(t.id_str, date, t.is_retweet, t.text);
            writer.write(tweet);
        }
    }


}
