package com.karrit.xoog;

import android.app.Service;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashSet;

public class VideoService extends Service {
    HashSet<String> set;

    private String key_ex_video_download="ex_download";
    private String key_downloaded="downloaded";
    int count;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences settings;
Boolean connected;
    private String TAG="video_service";
    public VideoService() {

        Log.i(TAG,"cons started");

    }
 /*   private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){

                boolean connected = VideoService.this.isNetworkAvailable(VideoService.this);V
                // Use e                                                                            xtras to verify that connection has been re-established...
                if (connected) {
                    // Unregister until we lose network connectivity again.
                    VideoService.this.unregisterReceiver(this);
                    // Resume handling requests.
                    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(VideoService.this);
                    set = new HashSet<String>(sharedPreferences.getStringSet(key_ex_video_download, new HashSet<String>()));
                    Log.i(TAG,"Broadcast received");
                    VideoService.this.downloadVideo();
                }
            }
        }

    };*/


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        set = (HashSet<String>) intent.getSerializableExtra("mySet");

        count=set.size();
        Log.i(TAG,"set "+set.toString());
        String name = "general";
       // SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences(name,MODE_PRIVATE);
        //
        settings= PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();

        Log.i(TAG,"count"+count);
        Log.i(TAG,"service started");
        downloadVideo();
        return START_REDELIVER_INTENT;
    }
    public void downloadVideo(){
        StoreSet();
        for ( String j : set){

            download_single(j);
        }


    }
    public void download_single(final String j){
        String refname;
        if(j.equals("music")){
            refname=j+".mp3";
        }else {
           refname = j + ".mp4";
        }
        Log.i(TAG,"ref name "+refname);
        StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://xoog-75949.appspot.com").child("sports_ex").child(refname);
        Log.i(TAG,storageReference.toString());
        ContextWrapper cw = new ContextWrapper(getApplication());
        File directory = cw.getDir("xoog_excercise",MODE_PRIVATE);
        if(!directory.exists()){
            directory.mkdirs();}
        String filename="ex_"+j+".txt";
        final File localfile=new File(directory,filename);
        storageReference.getFile(localfile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                count--;
                Log.i(TAG,"count"+count);
                if(task.isSuccessful()){
                Log.i(TAG,"file downloaded"+localfile.toString());
                set.remove(j);
                StoreSet();
                }else{
                    Log.i(TAG,j+"download_failure");
                }
                Log.i(TAG,"inside value"+j);
                Log.i(TAG,"set "+set.toString());
                if(count==(0)){
                    stopSelf();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,1+"download on failure");
               /*if(!isNetworkAvailable(VideoService.this)){
                    Log.i(TAG,"no network");
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    VideoService.this.registerReceiver(VideoService.this.mReceiver, filter);
                }*/
            }
        });
    }
  /*  public  boolean isNetworkAvailable(Context context) {
        if(context == null)  return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }

            else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_statut","Network is available : FALSE ");
        return false;
    }*/

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void StoreSet(){
        editor.putStringSet(key_ex_video_download,set);
        editor.apply();
    }




    @Override
    public void onDestroy() {

        Log.i(TAG,"service destroyed");
        editor.putBoolean(key_downloaded,true);
        editor.apply();
        super.onDestroy();
    }
}
