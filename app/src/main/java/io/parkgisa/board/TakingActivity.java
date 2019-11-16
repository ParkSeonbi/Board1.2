package io.parkgisa.board;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class TakingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_taking);

        Intent intent = getIntent();
        // 현장명 이름 가져오기
        String site = intent.getExtras().getString("site");
        // 보드판 불러오기_인텐트로 넘어온 이미지 받기
        byte[] byteArray = intent.getExtras().getByteArray("board");

        Camera2BasicFragment fragObj = Camera2BasicFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("site", site);
        bundle.putByteArray("board", byteArray);

        fragObj.setArguments(bundle);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragObj)
                    .commit();
        }
    }

}





