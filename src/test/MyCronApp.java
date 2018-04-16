package test;

import it.sauronsoftware.cron4j.Scheduler;

public class MyCronApp {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        // every minute.
        scheduler.schedule("*/5 * * * *", new Cand());
        // start cron4j scheduler.
        scheduler.start();
    }
}
