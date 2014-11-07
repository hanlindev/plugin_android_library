package com.sana.android.plugin.hardware;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.sana.android.plugin.data.DataWithEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mia on 23/9/14.
 */
public class BuiltinCameraDevice implements GeneralDevice  {
    private Camera mCamera;
    private static File outputFolder;
    private static String outputFolderName;
    private static String fileExtention;
    private final static String FAIL_CREATE_DIR = "failed to create directory";

    @Override
    public DataWithEvent prepare() {
        mCamera = getCameraInstance();
        return null;
    }

    @Override
    public void begin() {
        try {
            mCamera.takePicture(null, null, mPicture);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        mCamera = null;
    }

    @Override
    public void reset() {
        mCamera = getCameraInstance();
    }

    @Override
    public void setCaptureSetting(CaptureSetting setting) {
        this.outputFolder = setting.getOutputFolder();
        this.outputFolderName = setting.getOutputFolderName();
        this.fileExtention = setting.getFileExtention();

    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
            e.printStackTrace();
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(outputFolder,outputFolderName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(outputFolderName, FAIL_CREATE_DIR);
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + fileExtention);

        return mediaFile;
    }
}
