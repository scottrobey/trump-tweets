package com.robey.trumptweets;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.util.Arrays.copyOfRange;

/**
 * Created by scott on 2/18/17.
 */
public class DeletedTrumpTweetReader {
    // 5:29 AM on 18 Feb 16
    // 7:18 PM on 30 Jun
    // 8:33 PM on 11 Feb
    static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("h:mm a 'on' dd MMM[ [YY]]")
            .parseDefaulting(ChronoField.YEAR, 2016).toFormatter();

    private final Log log;
    private final DocumentWriter writer;

    DeletedTrumpTweetReader(Log log, DocumentWriter writer) throws Exception {
        this.log = log;
        this.writer = writer;
    }

    // elements that are relevant
    // div class="byline" --> Deleted after <x> seconds at <a href>DATE</a>
    // p class="tweet-text" --> TWEET TEXT

    public void process( InputStream input ) throws Exception {
        final String HTML = IOUtils.toString(input);
        //System.out.println("HTML: " + HTML);

        Document doc = Jsoup.parse(HTML);

        Elements contents = doc.select("div.content");
        for ( int i = 0; i < contents.size(); i++ ) {
            final Element content = contents.get(i);
            final String tweetText = content.select("p.tweet-text").text();
            final String dateLine = content.select("div.byline").text();

            try {
                Info dateInfo = parseDate(dateLine);
                TrumpTweet tweet = TrumpTweet.deletedTweet(dateInfo.dateSent, dateInfo.dateDeleted, dateInfo.deletedAfterSeconds, tweetText);
                writer.write(tweet);
            } catch ( Exception e ) {
                log.error("error processing tweet: " + content, e);
                throw e;
            }
        }
    }
    
    Info parseDate(String dateLine) throws Exception {
        Info info = new Info();
        final String[] split = dateLine.split(" ");
        if ( split[0].equals("Deleted") && split[1].equals("after") ) {
            int time = Integer.parseInt(split[2]);
            String units = split[3];
            info.deletedAfterSeconds = convertToSeconds(time, units);
        } else if ( split[0].equals("Deleted") && split[1].equals("immediately") ) {
            info.deletedAfterSeconds = 1;
        } else {
            log.error("Unrecognized date format: " + dateLine);
        }

        if ( split[2].equals("at") ) {
            //Deleted immediately at 3:50 PM on 15 May. Twitter_Logo_Blue
            String[] sub = copyOfRange(split, 3, split.length);
            List<String> components = Arrays.asList(sub);
            String dateString = String.join(" ", components);
            if ( dateString.contains(".") ) dateString = dateString.substring(0, dateString.indexOf("."));
            LocalDateTime dateTime = LocalDateTime.parse(dateString, DATE_FORMAT);
            Date date = Date.from(dateTime.toInstant(ZoneOffset.ofTotalSeconds(0)));
            info.dateDeleted = date;
        }
        else if ( split[4].equals("at") ) {
            // date format: 8:33 PM on 11 Feb

            String[] sub = copyOfRange(split, 5, split.length);
            List<String> components = Arrays.asList(sub);
            String dateString = String.join(" ", components);
            if ( dateString.contains(".") ) dateString = dateString.substring(0, dateString.indexOf("."));
            LocalDateTime dateTime = LocalDateTime.parse(dateString, DATE_FORMAT);
            Date date = Date.from(dateTime.toInstant(ZoneOffset.ofTotalSeconds(0)));
            info.dateDeleted = date;
        } else if ( split[4].equals("about") ) {
            int time = Integer.parseInt(split[5]);
            String units = split[6];
            info.dateDeleted = new Date(System.currentTimeMillis() + convertToSeconds(time, units) * 1000);
        } else {
            log.error("Unrecognized format for tweet time: " + dateLine);
            throw new Exception("unrecognized format for tweet time: " + dateLine);
        }

        info.dateSent = new Date(info.dateDeleted.getTime() - (info.deletedAfterSeconds*1000));
        return info;
    }

    int convertToSeconds(int time, String units) {
        int timeInSeconds = -1;

        switch (units) {
            case "second"  :
            case "seconds" : timeInSeconds = time; break;
            case "minute"  :
            case "minutes" : timeInSeconds = time * 60; break;
            case "hour"    :
            case "hours"   : timeInSeconds = time * 60 * 60; break;
            case "day"     :
            case "days"    : timeInSeconds = time * 24 * 60 * 60; break;
            case "week"    :
            case "weeks"   :timeInSeconds = time * 7 * 24 * 60 * 60; break;
            default: log.error("Unrecognized units: " + units);
        }
        return timeInSeconds;
    }

    class Info {
        Date dateDeleted;
        Date dateSent;
        int deletedAfterSeconds;
    }
}
