package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) {
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("runnable running");
            }
        };

        Calendar inOneMin = Calendar.getInstance();
        inOneMin.add(Calendar.MINUTE, 1);
        timer.schedule(timerTask, inOneMin.getTime());


        TimeUnit timeInMins = TimeUnit.MINUTES;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("scheduler running");
            }
        }, 1, timeInMins);
    }
}
