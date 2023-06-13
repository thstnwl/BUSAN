package com.example.Openapi2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Reservation4Activity extends Activity {

    private String result = "";
    private String selected_bus_number = "";
    private String call = "yes";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation4);

        String selected = "";
        // 선택한 버스정류장이름(정류장번호) 가져오기
        String select_bus_num = getIntent().getStringExtra("select_bus_num");
        String selected_bus_number = getIntent().getStringExtra("selected_bus_number");
        String lineno_in = getIntent().getStringExtra("lineno_in");
        String bstopnm_in = getIntent().getStringExtra("bstopnm_in");
        String bstopnm_arsno_in = getIntent().getStringExtra("bstopnm_arsno_in");
        String carno_in = getIntent().getStringExtra("carno_in");
        String arsno_in = getIntent().getStringExtra("arsno_in");
        String arsno_alarm = getIntent().getStringExtra("arsno_alarm");
        String bstopnm_alarm = getIntent().getStringExtra("bstopnm_alarm");
        String arsno_out = getIntent().getStringExtra("arsno_out");

        String line_id_in = getIntent().getStringExtra("line_id_in");
        String bstopid_in = getIntent().getStringExtra("bstopid_in");
        String bstopnm_out = getIntent().getStringExtra("bstopnm_out");

        //System.out.println("탑승 정류소 아이디 : " + bstopid_in);
        System.out.println("탑승 정류소 명 : " + bstopnm_in);
        //System.out.println("탑승 정류소명(정류소번호) : " + bstopnm_arsno_in);
        System.out.println("탑승 버스 번호 : " + lineno_in);
        System.out.println("탑승 차량 번호 : " + carno_in);
        //System.out.println("탑승 정류소 번호 : " + arsno_in);
        System.out.println("하차 정류소 명 : " + bstopnm_out);
        //System.out.println("하차 정류소 번호 : " + arsno_out);
        //System.out.println("탑승 버스 노선 아이디 : " + line_id_in);
        //System.out.println("하차 버스 2개 전 정류소 아이디 : " + arsno_alarm);
        System.out.println("하차 버스 2개 전 정류소 명 : " + bstopnm_alarm);

        selected = (bstopnm_arsno_in + "\n\n|\n|\n"+lineno_in + "버스" + "\n|\n↓" +  "\n\n" + bstopnm_out + " (" + arsno_out + ")");
        TextView textView = findViewById(R.id.textView); // 출력할 텍스트뷰
        textView.setText(selected);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("mydata.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(selected);
            outputStreamWriter.close();
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            //System.out.println("Exception" + "File write failed: " + e.toString());
        }


        // 이전 버튼 : Reservation2Activity => Reservation1Activity
        Button back4 = findViewById(R.id.back4);
        back4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Reservation3Activity.class);
                startActivity(intent);
            }
        });

        // 홈 버튼 : Reservation2Activity => Reservation1Activity
        Button home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("lineno_in", lineno_in); // 탑승 버스 번호
                intent.putExtra("bstopnm_in", bstopnm_in); // 탑승 정류소 명
                intent.putExtra("bstopnm_arsno_in", bstopnm_arsno_in); // 탑승 정류소명(정류소번호)
                intent.putExtra("carno_in", carno_in); // 탑승 차량 번호
                intent.putExtra("arsno_in", arsno_in); // 탑승 정류소 번호
                intent.putExtra("line_id_in", line_id_in); // 탑승 버스 노선 아이디
                intent.putExtra("bstopid_in", bstopid_in); // 탑승 정류소 아이디
                intent.putExtra("bstopnm_out", bstopnm_out); // 하차 정류소 명
                intent.putExtra("arsno_out", arsno_out); // 하차 정류소 번호
                intent.putExtra("arsno_alarm", arsno_alarm); // 하차 정류소 3개 전 알람을 줄 번호
                intent.putExtra("bstopnm_alarm", bstopnm_alarm); // 하차 정류소 3개 전 알람을 줄 번호
                intent.putExtra("call", call);
                //System.out.println("call " + call);
                // 다른 액티비티 호출
                startActivity(intent);
            }
        });
    }
}//Reservation2Activity class..