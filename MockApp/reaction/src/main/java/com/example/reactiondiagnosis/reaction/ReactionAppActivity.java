package com.example.reactiondiagnosis.reaction;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.hardware.UsbHostDevice;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ReactionAppActivity extends Activity {

    private class UsbListenerInitial extends TimedListener {

        Object sender;

        public UsbListenerInitial(Object sender, long interval, TimeUnit unit) {
            super(sender, interval, unit);
        }

        @Override
        public void setExpectedSender(Object sender) {
            this.sender = sender;
        }

        @Override
        public void processData(Object sender, Object[] data) {
            /*//Check if the sender matched
            if (this.sender != sender) return;*/

            if (data.length > 0) {
                synchronized(buttonPushed) {
                    lastButtonTime = System.currentTimeMillis();
                    buttonPushed.notify();
                }
            }
        }
    }

    private class UsbListenerTime extends TimedListener {

        Object sender;

        public UsbListenerTime(Object sender, long interval, TimeUnit unit) {
            super(sender, interval, unit);
        }

        @Override
        public void setExpectedSender(Object sender) {
            this.sender = sender;
        }

        @Override
        public void processData(Object sender, Object[] data) {
            if (data.length > 0) {
                try {
                    buttonTime.put(System.currentTimeMillis());
                } catch (InterruptedException e) {
                    Log.d(ReactionAppActivity.LOG_TAG, "Interrupted while getting current time");
                }
            }
        }
    }

    private static final String LOG_TAG = "ReactionAppActivity";
    private static final int NUM_TRIES = 5;

    // If the button is pushed twice in this time range, only one is registered
    private static final long SAME_PUSH_TIME_RANGE = 250;

    private static final long INVALID_TIME = -1;

    private static LinkedBlockingQueue<Long> buttonTime;
    private static int triesCount;
    private static int timeSum;
    private static long lastButtonTime;

    private UsbHostDevice device;
    private DataWithEvent dataWithEvent;
    private UsbListenerInitial initialListener;
    private UsbListenerTime timeListener;
    private Object buttonPushed = new Object();

    private static Thread changingBackground;
    private static Thread readingButton;
    private static long curTime;

    private FrameLayout frameLayout;
    private TextView welcomeMessage;
    private TextView actionMessage;
    private TextView startMessage;
    private TextView infoMessage;


    private void setupDevice() {
        device = new UsbHostDevice(this, 1, 0);
        dataWithEvent = device.prepare();

        initialListener = new UsbListenerInitial(device, 100, TimeUnit.MILLISECONDS);
        initialListener.startListening();

        if (dataWithEvent != null && dataWithEvent.getEvent() != null) {
            dataWithEvent.getEvent().addListener(initialListener);
        }

        device.begin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_app);
    }

    @Override
    protected void onStart() {
        super.onStart();

        triesCount = 0;
        timeSum = 0;
        lastButtonTime = 0;

        frameLayout = (FrameLayout) findViewById(R.id.frame);
        welcomeMessage = (TextView) findViewById(R.id.welcome_message);
        actionMessage = (TextView) findViewById(R.id.action_message);
        startMessage = (TextView) findViewById(R.id.start);
        infoMessage = (TextView) findViewById(R.id.info_message);

        actionMessage.setVisibility(View.INVISIBLE);
        infoMessage.setVisibility(View.INVISIBLE);

        if (device == null) {
            setupDevice();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(buttonPushed) {
                    try {
                        buttonPushed.wait();
                    } catch (InterruptedException e) {
                        Log.d("Interrupted", "");
                    }
                }
                startTester();
            }
        }).start();
    }

    private void runWaitingScreen() {

        changingBackground = new Thread( new Runnable() {
            @Override
            public void run() {

                Log.d(ReactionAppActivity.LOG_TAG, "Running changing background");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        frameLayout.setBackgroundColor(Color.GREEN);
                        actionMessage.setText("Wait");
                        infoMessage.setVisibility(View.INVISIBLE);
                    }
                });

                Random rand = new Random();
                int time = (int) (1500 + rand.nextInt(1000) * 3);
                try {
                    Thread.sleep(time);
                    curTime = System.currentTimeMillis();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            frameLayout.setBackgroundColor(Color.YELLOW);
                            actionMessage.setText("Push");
                        }
                    });
                } catch (InterruptedException e) {
                    Log.d(ReactionAppActivity.LOG_TAG, "Interrupted while sleeping");
                }
            }
        });
        changingBackground.start();
    }

    private void setupReactionDisplay() {

        readingButton = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.d(ReactionAppActivity.LOG_TAG, "Running reading button");
                    Log.d("last time = ", lastButtonTime + "");
                    long pushedTime = INVALID_TIME;
                    try {
                        do {
                            pushedTime = buttonTime.take();
                        } while (pushedTime - lastButtonTime < SAME_PUSH_TIME_RANGE);
                    } catch (InterruptedException e) {
                        Log.d(ReactionAppActivity.LOG_TAG, "Interrupted when reading from blocking queue");
                    }
                    Log.d("this time = ", pushedTime + "");
                    //lastButtonTime = pushedTime;

                    String message = "";
                    if (curTime == INVALID_TIME || pushedTime < curTime) {
                        Log.d("Cheaing ", lastButtonTime + " / " + pushedTime);
                        message = "Cheating";
                        changingBackground.interrupt();
                    } else {
                        message = (pushedTime - curTime) + " ms";
                        triesCount++;
                        timeSum += (pushedTime - curTime);
                    }
                    lastButtonTime = pushedTime;

                    curTime = INVALID_TIME;

                    Log.d("fff", "after pushing button");
                    final String newMessage = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            actionMessage.setText(newMessage + ", push to continue");
                            infoMessage.setText(
                                    String.format(
                                            "Current tries: %d / %d. Current average = %d ms",
                                            triesCount,
                                            NUM_TRIES,
                                            (triesCount == 0) ? 0 : timeSum / triesCount
                                    )
                            );
                            infoMessage.setVisibility(View.VISIBLE);
                        }
                    });

                    try {
                        do {
                            pushedTime = buttonTime.take();
                        } while (pushedTime - lastButtonTime < SAME_PUSH_TIME_RANGE);
                    } catch (InterruptedException e) {
                        Log.d(ReactionAppActivity.LOG_TAG, "Interrupted when reading from blocking queue");
                    }
                    lastButtonTime = pushedTime;

                    if (triesCount == NUM_TRIES) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actionMessage.setText("Done");
                            }
                        });
                        break;
                    }

                    while (changingBackground.isAlive());
                    runWaitingScreen();
                }
            }
        });
    }

    private void startTester() {

        timeListener = new UsbListenerTime(device, 1, TimeUnit.MILLISECONDS);
        timeListener.startListening();

        if (dataWithEvent != null && dataWithEvent.getEvent() != null) {
            dataWithEvent.getEvent().removeListener(initialListener);
            dataWithEvent.getEvent().addListener(timeListener);
        }
        initialListener.stopListening();

        buttonTime = new LinkedBlockingQueue<Long>();
        curTime = INVALID_TIME;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                welcomeMessage.setVisibility(View.INVISIBLE);
                startMessage.setVisibility(View.INVISIBLE);
                actionMessage.setVisibility(View.VISIBLE);
            }
        });

        runWaitingScreen();
        setupReactionDisplay();
        readingButton.start();
    }
}
