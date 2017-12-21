package com.robey.trumptweets;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.valueOf;

/**
 * Created by scott on 2/18/17.
 */
public class TrumpTweet {
    private final String id;
    private final Date dateSent;
    private final String tweet;
    private final String retweet;

    // attributes of delete tweets
    private final String deleted;
    private final Date dateDeleted;
    private final int deletedAfterSeconds;

    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);

    public static final TrumpTweet regularTweet(final String id, final Date dateSent, final boolean retweet, final String tweet) {
        return new TrumpTweet(id, dateSent, tweet, retweet, false, null, 0);
    }

    public static final TrumpTweet deletedTweet(final Date dateSent, final Date dateDeleted, final int deletedAfterSeconds, final String tweet) {
        GENERATED_ID.incrementAndGet();
        return new TrumpTweet(GENERATED_ID.toString(), dateSent, tweet, false, true, dateDeleted, deletedAfterSeconds);
    }

    public TrumpTweet(final String id, final Date dateSent, final String tweet, final boolean retweet,
                             final boolean isDeleted, final Date dateDeleted, final int deletedAfterSeconds) {
        this.id = id;
        this.dateSent = dateSent;
        this.tweet = tweet;
        this.retweet = valueOf(retweet);
        this.deleted = valueOf(isDeleted);
        this.dateDeleted = dateDeleted;
        this.deletedAfterSeconds = deletedAfterSeconds;
    }
    public String getId() {
        return id;
    }
    public Date getDateSent() {
        return dateSent;
    }
    public String getTweet() {
        return tweet;
    }
    public String getRetweet() {
        return retweet;
    }
    public Date getDateDeleted() {
        return dateDeleted;
    }
    public int getDeletedAfterSeconds() {
        return deletedAfterSeconds;
    }
    public String getDeleted() {
        return deleted;
    }
}
