package io.parkgisa.board;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TableLayout parkBoard;

    private InstantAutoComplete siteEditText;
    private InstantAutoComplete workEditText;
    private InstantAutoComplete placeEditText;
    private InstantAutoComplete contentEditText;
    private SharedPreferences mPreferences;
    private AdView mAdView;
    private ArrayAdapter<String> adapterSite;
    private ArrayAdapter<String> adapterWork;
    private ArrayAdapter<String> adapterPlace;
    private ArrayAdapter<String> adapterContent;
    private ArrayList<String> SITE_ARRAY = new ArrayList<>();
    private ArrayList<String> WORK_ARRAY = new ArrayList<>();
    private ArrayList<String> PLACE_ARRAY = new ArrayList<>();
    private ArrayList<String> CONTENT_ARRAY = new ArrayList<>();
    private static final String SETTINGS_SITE_JSON = "settings_site_json";
    private static final String SETTINGS_WORK_JSON = "settings_WORK_json";
    private static final String SETTINGS_PLACE_JSON = "settings_PLACE_json";
    private static final String SETTINGS_CONTENT_JSON = "settings_CONTENT_json";

    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkVerify() {
        if (
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // ...
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_board:
                    return true;

                case R.id.navigation_taking:

                    // 보드판을 비트맵으로 만들기 -> 보드판 뷰의 이미지 변경
                    parkBoard = findViewById(R.id.ParkBoard);
                    parkBoard.setDrawingCacheEnabled(true);
                    parkBoard.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    parkBoard.layout(0, 0, parkBoard.getMeasuredWidth(), parkBoard.getMeasuredHeight());
                    parkBoard.buildDrawingCache(true);
                    Bitmap boardImage = Bitmap.createBitmap(parkBoard.getDrawingCache());
                    parkBoard.setDrawingCacheEnabled(false);

                    //비트맵을 바이트어레이로 바꿔서 인텐트에 담기
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    float scale = (1024 / (float) boardImage.getWidth());
                    int image_w = (int) (boardImage.getWidth() * scale);
                    int image_h = (int) (boardImage.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(boardImage, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    String site = siteEditText.getText().toString();

                    Intent shootingIntent = new Intent(MainActivity.this, TakingActivity.class);
                    shootingIntent.putExtra("board", byteArray);
                    shootingIntent.putExtra("site", site);
                    startActivity(shootingIntent);

                    return true;

                case R.id.navigation_gallery:
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(android.content.Intent.ACTION_VIEW);
                    galleryIntent.setType("image/*");
                    galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(galleryIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkVerify();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        EditText dateEditText = findViewById(R.id.dateEditText);
        dateEditText.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_board);

        siteEditText = findViewById(R.id.siteEditText);
        workEditText = findViewById(R.id.workEditText);
        placeEditText = findViewById(R.id.placeEditText);
        contentEditText = findViewById(R.id.contentEditText);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String site = mPreferences.getString("site", "");
        siteEditText.setText(site);
        String work = mPreferences.getString("work", "");
        workEditText.setText(work);
        String place = mPreferences.getString("place", "");
        placeEditText.setText(place);
        String content = mPreferences.getString("content", "");
        contentEditText.setText(content);

        SITE_ARRAY = getStringArrayPref(this, SETTINGS_SITE_JSON);
        if (SITE_ARRAY.contains("")) {
            SITE_ARRAY.remove("");
        }

        adapterSite = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, SITE_ARRAY);
        siteEditText.setAdapter(adapterSite);
        siteEditText.setThreshold(0);

        WORK_ARRAY = getStringArrayPref(this, SETTINGS_WORK_JSON);
        if (WORK_ARRAY.contains("")) {
            WORK_ARRAY.remove("");
        }

        adapterWork = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, WORK_ARRAY);
        workEditText.setAdapter(adapterWork);
        workEditText.setThreshold(0);

        PLACE_ARRAY = getStringArrayPref(this, SETTINGS_PLACE_JSON);
        if (PLACE_ARRAY.contains("")) {
            PLACE_ARRAY.remove("");
        }

        adapterPlace = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, PLACE_ARRAY);
        placeEditText.setAdapter(adapterPlace);
        placeEditText.setThreshold(0);

        CONTENT_ARRAY = getStringArrayPref(this, SETTINGS_CONTENT_JSON);
        if (CONTENT_ARRAY.contains("")) {
            CONTENT_ARRAY.remove("");
        }

        adapterContent = new ArrayAdapter(this,
                android.R.layout.simple_dropdown_item_1line, CONTENT_ARRAY);
        contentEditText.setAdapter(adapterContent);
        contentEditText.setThreshold(0);


    }

    @Override
    protected void onPause() {
        super.onPause();

        siteEditText.clearFocus();
        workEditText.clearFocus();
        placeEditText.clearFocus();
        contentEditText.clearFocus();

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("site", siteEditText.getText().toString());
        editor.putString("work", workEditText.getText().toString());
        editor.putString("place", placeEditText.getText().toString());
        editor.putString("content", contentEditText.getText().toString());
        editor.apply();

        if (siteEditText.getText().toString() != "") {
            if (!SITE_ARRAY.contains(siteEditText.getText().toString())) {
                SITE_ARRAY.add(siteEditText.getText().toString());
            }
        }
        setStringArrayPref(this, SETTINGS_SITE_JSON, SITE_ARRAY);

        if (workEditText.getText().toString() != "") {
            if (!WORK_ARRAY.contains(workEditText.getText().toString())) {
                WORK_ARRAY.add(workEditText.getText().toString());
            }
        }
        setStringArrayPref(this, SETTINGS_WORK_JSON, WORK_ARRAY);

        if (placeEditText.getText().toString() != "") {
            if (!PLACE_ARRAY.contains(placeEditText.getText().toString())) {
                PLACE_ARRAY.add(placeEditText.getText().toString());
            }
        }
        setStringArrayPref(this, SETTINGS_PLACE_JSON, PLACE_ARRAY);

        if (contentEditText.getText().toString() != "") {
            if (!CONTENT_ARRAY.contains(contentEditText.getText().toString())) {
                CONTENT_ARRAY.add(contentEditText.getText().toString());
            }
        }
        setStringArrayPref(this, SETTINGS_CONTENT_JSON, CONTENT_ARRAY);
    }

    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        EditText editTextDate = findViewById(R.id.dateEditText);
        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.e("withpd", "grantResults[i]  : " + permissions[i]);
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                }
            }
        }
    }

}
