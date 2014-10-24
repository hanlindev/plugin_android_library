package com.example.mia.snorelab;

/**
 * Created by Mia on 22/10/14.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;


public class CSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context          drawContext;
    public  DrawThread       drawThread;
    private SurfaceHolder    drawSurfaceHolder;
    private Boolean          threadExists = false;

    public static volatile Boolean drawFlag = false;

    private static int       rectPos = 0;

    private static final Handler handler = new Handler(){

        public void handleMessage(Message paramMessage)
        {
        }
    };

    public CSurfaceView(Context ctx, AttributeSet attributeSet)
    {
        super(ctx, attributeSet);

        drawContext = ctx;

        init();

    }



    public void init()
    {

        if (!threadExists) {

            drawSurfaceHolder = getHolder();
            drawSurfaceHolder.addCallback(this);

            drawThread = new DrawThread(drawSurfaceHolder, drawContext, handler);

            drawThread.setName("" +System.currentTimeMillis());
            drawThread.start();
        }

        threadExists = Boolean.valueOf(true);

        drawFlag = Boolean.valueOf(true);

        rectPos = 0;

        return;

    }


    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
    {
        drawThread.setSurfaceSize(paramInt2, paramInt3);
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {

        init();

    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {

        while (true)
        {
            if (!drawFlag)
                return;
            try
            {
                drawFlag = Boolean.valueOf(false);
                drawThread.join();

            }
            catch (InterruptedException localInterruptedException)
            {
            }
        }



    }


    class DrawThread extends Thread
    {
        private Bitmap         soundBackgroundImage;
        private short[]        soundBuffer;
        private int[]          soundSegmented;
        private double[]       soundFFT;
        private double[]       soundFFTMag;
        private double[]       soundFFTTemp;
        public  Boolean        soundCapture = Boolean.valueOf(false);
        public  Boolean        FFTComputed  = Boolean.valueOf(false);
        public  int            FFT_Len      = 1024;
        public  int            STFFT_Len    = 256;    // short time FFT length
        public  int            segmentIndex = -1;
        private int            soundCanvasHeight = 0;
        private int			   soundCanvasWidth  = 0;
        private Paint          soundLinePaint;
        private Paint		   soundLinePaint2;
        private Paint          soundLinePaint3;
        private SurfaceHolder  soundSurfaceHolder;
        private int            drawScale   = 8;
        private double         mxIntensity;


        public DrawThread(SurfaceHolder paramContext, Context paramHandler, Handler arg4)
        {
            soundSurfaceHolder = paramContext;

            soundLinePaint     = new Paint();
            soundLinePaint.setARGB(255, 0, 0, 255);
            soundLinePaint.setStrokeWidth(3);

            soundLinePaint2     = new Paint();
            soundLinePaint2.setAntiAlias(true);
            soundLinePaint2.setARGB(255, 255, 0, 0);
            soundLinePaint2.setStrokeWidth(4);

            soundLinePaint3     = new Paint();
            soundLinePaint3.setAntiAlias(true);
            soundLinePaint3.setARGB(255, 0, 255, 255);
            soundLinePaint3.setStrokeWidth(3);

            soundBuffer        = new short[2048];

            soundBackgroundImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

            soundSegmented     = new int[FFT_Len];
            soundFFT           = new double[FFT_Len*2];
            soundFFTMag        = new double[FFT_Len];

            soundFFTTemp       = new double[FFT_Len*2];
        }



        /************* the following performs FFT on a segment of sound ***********/
        public void doDraw(Canvas canvas)
        {

            soundCanvasHeight  = canvas.getHeight();
            soundCanvasWidth   = canvas.getWidth();

            int height         = soundCanvasHeight;
            int width          = soundCanvasWidth;

            Paint paint = new Paint();
            paint.setColor(Color.LTGRAY);
            paint.setStyle(Style.FILL);
            canvas.drawPaint(paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            canvas.drawText("Voice", 20, 20, paint);


            if (!soundCapture) {

                int xStart = 0;

                while (xStart < width -1)  {

                    int yStart = soundBuffer[xStart] / height * drawScale;
                    int yStop  = soundBuffer[xStart+1] / height * drawScale;

                    int yStart1 = yStart + height/4;
                    int yStop1  = yStop  + height/4;

                    canvas.drawLine(xStart, yStart1, xStart +1, yStop1, soundLinePaint2);

                    if (xStart %100 == 0) {
                        paint.setColor(Color.BLUE);
                        paint.setTextSize(20);
                        canvas.drawText(Integer.toString(xStart), xStart, height/2, paint);
                        canvas.drawText(Integer.toString(yStop),  xStart, yStop1, paint);
                    }

                    xStart++;

                }


            } else if (soundCapture) {

                if (segmentIndex < 0) {
                    segmentIndex = 0;
                    while (segmentIndex < FFT_Len) {
                        soundSegmented[segmentIndex] = soundBuffer[segmentIndex];
                        soundFFT[2*segmentIndex] = (double)soundSegmented[segmentIndex];
                        soundFFT[2*segmentIndex+1] = 0.0;
                        segmentIndex++;
                    }
                }


                int imax = -9999;
                int imin = 9999;
                int fftHeight = 300;

                if (!FFTComputed) {
                    // fft
                    DoubleFFT_1D fft = new DoubleFFT_1D(FFT_Len);
                    fft.complexForward(soundFFT);
                    FFTComputed = Boolean.valueOf(true);

                    // perform fftshift here
                    for (int i=0; i<FFT_Len; i++) {
                        soundFFTTemp[i]         = soundFFT[i+FFT_Len];
                        soundFFTTemp[i+FFT_Len] = soundFFT[i];
                    }
                    for (int i=0; i<FFT_Len*2; i++) {
                        soundFFT[i] = soundFFTTemp[i];
                    }

                    double mx = -99999;
                    for (int i=0; i<FFT_Len; i++) {
                        double re = soundFFT[2*i];
                        double im = soundFFT[2*i+1];
                        soundFFTMag[i] = Math.log(re*re + im*im + 0.001);
                        if (soundFFTMag[i] > mx) mx = soundFFTMag[i];
                    }


                    imax = -999;
                    imin = 9999;

                    // normalize
                    for (int i=0; i<FFT_Len; i++) {
                        soundFFTMag[i] = soundFFTMag[i]/mx * fftHeight;
                        if (soundFFTMag[i] > imax) imax = (int)soundFFTMag[i];
                        if (soundFFTMag[i] < imin) imin = (int)soundFFTMag[i];
                    }

                    mxIntensity = mx;
                }

                // print the maximum intensity
                paint.setColor(Color.BLACK);
                paint.setTextSize(30);
                canvas.drawText("max Intensity = " + String.valueOf(mxIntensity), 100, height-30, paint);

                // display the signal in temporal domain
                int xStart = 0;
                while (xStart < FFT_Len-1)  {
                    int yStart = soundSegmented[xStart] / height * drawScale;
                    int yStop  = soundSegmented[xStart+1] / height * drawScale;

                    int yStart1 = yStart + height/4;
                    int yStop1  = yStop  + height/4;

                    canvas.drawLine(xStart, yStart1, xStart +1, yStop1, soundLinePaint2);

                    if (xStart %100 == 0) {
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(20);
                        canvas.drawText(Integer.toString(xStart), xStart, height/2, paint);
                        canvas.drawText(Integer.toString(yStop),  xStart, yStop1, paint);
                    }
                    xStart++;
                }

                // display the fft results

                int yStepSz = (int)(FFT_Len/fftHeight);

                int sum = 0;
                int yTemp = 0;
                for (int i = 0; i<FFT_Len-yStepSz;  i+=yStepSz) {
                    sum = 0;
                    for (int j=0; j<yStepSz; j++) {
                        sum = sum + (int)(soundFFTMag[i+j]/yStepSz);
                    }
                    int gray       = (int)(sum-imin)*255/(imax-imin);
                    int blueness   = 255-gray;
                    int redness    = gray;
                    int greenness  = blueness;

                    soundLinePaint.setARGB(255, redness, greenness, blueness);

                    canvas.drawLine(rectPos, height-100-yTemp, rectPos, height-100-yTemp-1,  soundLinePaint);

                    yTemp++;

                }

                segmentIndex = -1;
                FFTComputed  = Boolean.valueOf(false);

            }

        }


        public void setBuffer(short[] paramArrayOfShort)
        {
            synchronized (soundBuffer)
            {
                soundBuffer = paramArrayOfShort;
                return;
            }
        }


        public void setSurfaceSize(int canvasWidth, int canvasHeight)
        {
            synchronized (soundSurfaceHolder)
            {
                soundBackgroundImage = Bitmap.createScaledBitmap(soundBackgroundImage, canvasWidth, canvasHeight, true);
                return;
            }
        }


        public void run()
        {

            while (drawFlag)
            {

                Canvas localCanvas = null;

                try
                {
                    localCanvas = soundSurfaceHolder.lockCanvas(new Rect(rectPos,0, rectPos+1,1150));

                    synchronized (soundSurfaceHolder)
                    {

                        if (localCanvas != null)
                            doDraw(localCanvas);

                    }
                }
                finally
                {

                    if (localCanvas != null)
                        soundSurfaceHolder.unlockCanvasAndPost(localCanvas);

                }

                rectPos = (rectPos+1)%1024;


            }
        }


    }


}