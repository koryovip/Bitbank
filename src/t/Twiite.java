package t;

import java.util.Date;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import utils.DateUtil;

public class Twiite implements Runnable {
    final String twitter;

    public Twiite(final String twitter) {
        this.twitter = twitter;
    }

    @Override
    public void run() {
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            String msg = this.twitter + " @koryovip " + DateUtil.me().format2(new Date());
            System.out.println("Twitter:" + msg);
            twitter.updateStatus(msg);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

}
