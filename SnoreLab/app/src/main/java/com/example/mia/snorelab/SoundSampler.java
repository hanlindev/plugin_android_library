package com.example.mia.snorelab;

/**
 * Created by Mia on 22/10/14.
 */
import android.media.AudioRecord;
import android.util.Log;
import com.sana.android.plugin.data.listener;


public class SoundSampler {

    private static final int  FS = 16000;     // sampling frequency
    private AudioRecord       audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    private MainActivity      mainActivity;
    private Thread            recordingThread;


    public SoundSampler(MainActivity mAct) throws Exception
    {
        mainActivity = mAct;

        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));

        }
        catch (Exception e) {
            Log.d("Error in SoundSampler(MainActivity mAct) ", e.getMessage());
            throw new Exception();
        }


        return;


    }



    public void init() throws Exception
    {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));

        }
        catch (Exception e) {
            Log.d("Error in Init() ", e.getMessage());
            throw new Exception();
        }

        MainActivity.bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
        MainActivity.buffer = new short[MainActivity.bufferSize];

        audioRecord.startRecording();

        recordingThread = new Thread()
        {
            public void run()
            {
                while (true)
                {

                    audioRecord.read(MainActivity.buffer, 0, MainActivity.bufferSize);
                    mainActivity.surfaceView.drawThread.setBuffer(MainActivity.buffer);

                }
            }
        };
        recordingThread.start();

        return;

    }


}
