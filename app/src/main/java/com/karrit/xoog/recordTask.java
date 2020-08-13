package com.karrit.xoog;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class recordTask extends AsyncTask<Void, Void, Void> {
    private Camera camera;
    public static final String LOGTAG = "VIDEOCAPTURE";
    public static final String TAG = "VIDEOCAPTURE";
    StorageReference storageReference;
    public static MediaRecorder recorder;
    private SurfaceHolder holder;

    File newFile;

    public SurfaceView cameraView;
    private CamcorderProfile camcorderProfile;
    @Override
    protected void onPreExecute() {
        Log.i(TAG,"on pre execute");
        cameraView= exercise_do1.cameraView;
        holder=exercise_do1.holder;
        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
       camera=exercise_do1.camera;

        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.i(TAG,"do in back");
        recorder = new MediaRecorder();
        // recorder.setPreviewDisplay(holder.getSurface());


        camera.unlock();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

           recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

        } else {
            recorder.setProfile(camcorderProfile);

        }


       //



        // This is all very sloppy
        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
            try {

                newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStorageDirectory());
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
            }
        } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            try {
                newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());

                // File newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStorageDirectory());
                recorder.setOutputFile(newFile.getAbsolutePath());

            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();

            }
        } else {
            try {
                newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
                recorder.setOutputFile(newFile.getAbsolutePath());

            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();

            }

        }
        //recorder.setMaxDuration(50000); // 50 seconds
        //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
        // recorder.setVideoFrameRate(10000);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(e.toString());

        } catch (IOException e) {
            e.printStackTrace();

        }

        return null;
    }

    @Override
    protected void onCancelled() {
        Log.i(TAG,"on cancelled");
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i(TAG,"on post execute");
      /*  try {
            camera.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.release();
        storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("upload");
        Uri file = Uri.fromFile(newFile);
        StorageReference riversRef = storageReference;
        UploadTask uploadTask = riversRef.putFile(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(LOGTAG, "upload sucess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOGTAG, "upload failure");
                e.printStackTrace();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.i(TAG, "upload complete");
            }
        });
        Log.v(LOGTAG, "Recording Stopped");
        // Let's prepareRecorder so we can record again*/

        super.onPostExecute(aVoid);
    }
}
