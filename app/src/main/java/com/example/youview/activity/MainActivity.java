package com.example.youview.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.MenuItem;

import com.example.youview.utils.UploadVersions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.youview.R;
import com.example.youview.fragment.HomeFragment;
import com.example.youview.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment = new HomeFragment();
    private SearchFragment searchFragment = new SearchFragment();

    private BottomNavigationView menuBawah;

    private String android_id;
    int videoNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuBawah = findViewById(R.id.menu_bawah);
        onServiceConnected();
        setFragment(homeFragment);
        menuBawah.setSelectedItemId(R.id.menu_home);
        menuBawah.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()){
                    return true;
                } else {
                    switch (menuItem.getItemId()){
                        case R.id.menu_home:
                            setFragment(homeFragment);
                            getSupportActionBar().setTitle("Home");
                            return true;
                        case R.id.menu_search:
                            setFragment(searchFragment);
                            getSupportActionBar().setTitle("Search");
                            return true;
                        default:
                            setFragment(homeFragment);
                            getSupportActionBar().setTitle("Home");
                            return true;
                    }
                }
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment);
        ft.commit();
    }


    public void onServiceConnected() {
        String versionName = getYtVersion("com.google.android.youtube", getPackageManager());
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String version = currentVersion();
        String dimensions = getDimensions();
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        String device = Build.DEVICE;
        String model = Build.MODEL;
        String api = String.valueOf(SDK_INT);
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        UploadVersions versions = new UploadVersions();
        versions.doInBackground(android_id, version, api, dimensions, versionName, device, model);

        SharedPreferences sh = getSharedPreferences("com.example.youview", MODE_PRIVATE);
        videoNumber = sh.getInt("videoNumber", 0);
    }

    private String getYtVersion(String packageName, PackageManager packageManager) {
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            String versionName = info.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "UNKNOWN";
        }
    }

    private static String currentVersion(){
        double release=Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)","$1"));
        String codeName="Unsupported";//below Jelly Bean
        if(release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
        else if(release < 5)   codeName="Kit Kat";
        else if(release < 6)   codeName="Lollipop";
        else if(release < 7)   codeName="Marshmallow";
        else if(release < 8)   codeName="Nougat";
        else if(release < 9)   codeName="Oreo";
        else if(release < 10)  codeName="Pie";
        else if(release >= 10) codeName="Android "+((int)release);//since API 29 no more candy code names
        return codeName+" v"+release+"";
    }

    private static String getDimensions(){
        return "width: "+ Resources.getSystem().getDisplayMetrics().widthPixels+", height: "+ Resources.getSystem().getDisplayMetrics().heightPixels+"";
    }
}
