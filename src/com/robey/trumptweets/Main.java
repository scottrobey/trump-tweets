package com.robey.trumptweets;

import java.io.File;
import java.util.Date;

public class Main {
    static final String[] TWEET_FILES = {
            "2009.json",
            "2010.json",
            "2011.json",
            "2012.json",
            "2013.json",
            "2014.json",
            "2015.json",
            "2016.json",
            "2017.json"
    };

    static final String[] DELETED_TWEET_FILES = {
            "deleted-page1.html",
            "deleted-page2.html",
            "deleted-page3.html",
            "deleted-page4.html",
            "deleted-page5.html",
            "deleted-page6.html",
            "deleted-page7.html",
            "deleted-page8.html",
            "deleted-page9.html"
    };

    public static void main(String[] args) {
        testFormatting();

        final Log log = new Log();
        try(DocumentWriter writer = new DocumentWriter(log, new File("out/trump-tweets.json"))) {
            writer.start();

            final TwitterReader twitterReader = new TwitterReader(log, writer);
            final DeletedTrumpTweetReader deletedTrumpTweetReader = new DeletedTrumpTweetReader(log, writer);

            for( String tweet : TWEET_FILES ) {
                log.info("Processing: " + tweet);
                twitterReader.process(Main.class.getResourceAsStream("/" + tweet));
            }

            for ( String deletedTweet : DELETED_TWEET_FILES ) {
                log.info("Processing: " + deletedTweet);
                deletedTrumpTweetReader.process(Main.class.getResourceAsStream("/" + deletedTweet));
            }

            log.info("Captured: " + writer.getNumberOfTweets() + " tweets.");
        } catch ( Throwable t ) {
            System.err.print(t);
            t.printStackTrace();
        }
    }

    private static void testFormatting() {
        final Log log = new Log();
        try(DocumentWriter writer = new DocumentWriter(log, new File("out/test.json"))) {
            writer.start();
            TrumpTweet testTweet = TrumpTweet.regularTweet("101", new Date(), true, "my bogus real tweet");
            writer.write(testTweet);

            TrumpTweet testDeletedTweet = TrumpTweet.deletedTweet(new Date(), new Date(), 5, "bogus deleted tweet.");
            writer.write(testDeletedTweet);
        } catch ( Throwable t ) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }
}
