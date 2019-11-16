package io.parkgisa.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GalleryActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_board:
                    Intent boardIntent = new Intent(GalleryActivity.this, MainActivity.class);
                    startActivity(boardIntent);
                    return true;

                case R.id.navigation_taking:
                    // 보드판을 비트맵으로 만들기 -> 보드판 뷰의 이미지 변경
/*                    parkBoard = findViewById(R.id.ParkBoard);

                    parkBoard.setDrawingCacheEnabled(true);

                    parkBoard.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    parkBoard.layout(0, 0, parkBoard.getMeasuredWidth(), parkBoard.getMeasuredHeight());

                    parkBoard.buildDrawingCache(true);

                    Bitmap boardImage = Bitmap.createBitmap(parkBoard.getDrawingCache());
                    parkBoard.setDrawingCacheEnabled(false);

                    //비트맵을 바이트어레이로 바꿔서 인텐트에 담기
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    float scale = (1024/(float)boardImage.getWidth());
                    int image_w = (int) (boardImage.getWidth() * scale);
                    int image_h = (int) (boardImage.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(boardImage, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();*/

                    Intent shootingIntent = new Intent(GalleryActivity.this, TakingActivity.class);
//                    shootingIntent.putExtra("board", byteArray);
                    startActivity(shootingIntent);
                    return true;

                case R.id.navigation_gallery:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_gallery);
    }
}
