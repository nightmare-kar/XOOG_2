package com.karrit.xoog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home_page extends AppCompatActivity {
    private final static int id_game = 1;
    private final static int id_leaderboard = 2;
    private final static int id_shop =  3;
    private final static int id_menu = 4;
    CircleImageView profile;
    View view;
    myDrawerLayout drawerLayout;
    private String TAG="Home_page";
    account_details accountDetails;
    NavigationView navigationView;
    Fragment shop,leaderboard,game;
    shared share;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_page);
        view=getLayoutInflater().inflate(R.layout.nav_header,null);

        mAuth=FirebaseAuth.getInstance();
        //-------------to delete----------------
        share=new shared(this);

       accountDetails=new account_details(this,share.getCurrent_kid());
        Log.i(TAG,"xcash "+accountDetails.getXcash());
        task_details task=new task_details(this,share.getCurrent_kid(),getString(R.string.rubik_type));
        task.apply();
        shop=new ShopFragment();
        leaderboard=new LeaderBoardFragment();
        game = new GameFragment();

       leaderboard = new LeaderBoardFragment();

        shop = new ShopFragment();
      replaceFragment(game);

      drawerLayout = findViewById(R.id.drawer_layout);
 drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

       navigationView=findViewById(R.id.nav_view);


    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent;
                switch (item.getItemId()){
                    case R.id.account:

                        intent=new Intent(getApplicationContext(),kids_account.class);
                        startActivity(intent);
                        break;
                    case R.id.referal:
                        intent=new Intent(getApplicationContext(),referra.class);
                        startActivity(intent);
                        break;

                    case R.id.bookcall:
                        intent= new Intent(getApplicationContext(),menu_book_call.class);
                        startActivity(intent);
                        break;
                    case R.id.upgrade:
                        intent= new Intent(getApplicationContext(),choose_plan.class);
                        startActivity(intent);
                        break;
                    case R.id.switch_profile:
                        intent=new Intent(getApplicationContext(),Parent_kid.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        mAuth.signOut();
                        shared share = new shared(getApplicationContext());
                        int i=share.getKids_number();
                        if(i==1){
                            clearData(1);
                        }
                        else{
                            clearData(1);
                           clearData(2);
                        }
                        share.clearShared();


                        intent=new Intent(getApplicationContext(),GetStarted.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;


                }

                return false;
            }
        });



       bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);



    }

    @Override
    protected void onStart() {

        profile=view.findViewById(R.id.profile_image);
        TextView textView=view.findViewById(R.id.username_text);
        textView.setText(accountDetails.getKid_name());
        if(!accountDetails.getProfImageurl().equals(getString(R.string.empty))) {
            String imageUrl = accountDetails.getProfImageurl();
            Uri uri = Uri.parse(imageUrl);
            Log.i(TAG, imageUrl);
            profile.setImageURI(uri);
            try {

                int rotate = getCameraPhotoOrientation(this, uri, imageUrl);
                profile.setRotation(rotate);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            profile.setImageResource(R.color.black);
        }
       navigationView.removeHeaderView(view);
        navigationView.addHeaderView(view);
        super.onStart();
    }

    public void clearData(int i){
        shared shared=new shared(this);
        task_details taskRubik=new task_details(this,share.getKids_id(i),getString(R.string.rubik_type));
        taskRubik.clearShared();
        task_details taskSports=new task_details(this,share.getKids_id(i),getString(R.string.sport_type));
        taskSports.clearShared();
        task_details taskHealth=new task_details(this,share.getKids_id(i),getString(R.string.health_type));
        taskHealth.clearShared();
        sql_health sqlHealth=new sql_health(this,shared.getKids_id(i));
        sqlHealth.delete();
        sql_sports sqlSports=new sql_sports(this,shared.getKids_id(i));
        sqlSports.delete();
        sql_rubik sqlRubik=new sql_rubik(this,shared.getKids_id(i));
        sqlRubik.delete();
        sql_leaderBoard sqlLeaderBoard=new sql_leaderBoard(this,shared.getKids_id(i));
        sqlLeaderBoard.delete();
        sql_address sqlAddress=new sql_address(this);
        sqlAddress.delete();

        account_details accountDetails=new account_details(this,share.getKids_id(i));
        accountDetails.clearShared();


    }
    private  BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment SelectedFragment = null;
            switch (menuItem.getItemId()){
                case R.id.gamePage:

                    drawerLayout.closeDrawer(GravityCompat.END);
                    replaceFragment(game);
                    //getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,game).commit();
                    break;
                case R.id.LeaderboardPage:
                   drawerLayout.closeDrawer(GravityCompat.END);
                  //  SelectedFragment = new LeaderBoardFragment();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,leaderboard).commit();
                    replaceFragment(leaderboard);
                    break;
                case R.id.shopPage:
                    replaceFragment(shop);

                 //   SelectedFragment = new ShopFragment();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,shop).commit();
                    drawerLayout.closeDrawer(GravityCompat.END);
                    break;
                case R.id.menuPage:
                   drawerLayout.openDrawer(GravityCompat.END);
                    break;
            }

            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if(!game.isVisible()){
           replaceFragment(game);
            bottomNavigationView.setSelectedItemId(R.id.gamePage);

            drawerLayout.closeDrawer(GravityCompat.END);
        }else if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            replaceFragment(game);
            drawerLayout.closeDrawer(GravityCompat.END);
            bottomNavigationView.setSelectedItemId(R.id.gamePage);

        }else{
            finish();
        }

    }
    private void replaceFragment (Fragment fragment){
        String backStateName =  fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frameContainer, fragment, fragmentTag);

            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
    public int getCameraPhotoOrientation(Context context, Uri imageUri,
                                         String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
}

