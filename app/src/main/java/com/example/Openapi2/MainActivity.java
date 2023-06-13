package com.example.Openapi2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 200;
    String key="tJTT7FZrG7Wn5K77BVwpPwQqq2QVhkeXiPC5BuZcgRXeesYinqJ6y3y/CqDrW6uSGxdGH84uG2dLJ/880mabtQ==";
    String data;
    private List<String> arsno = new ArrayList<>();
    private List<String> busstopname = new ArrayList<>();
    private List<String> carno = new ArrayList<>();

    //XmlPullParser를 이용하여 OpenAPI XML 파일 파싱하기(parsing)
    String getXmlData(){

        // 선택한 노선번호 가져오기
        String line_id_in = getIntent().getStringExtra("line_id_in");
        StringBuffer buffer=new StringBuffer();
        //System.out.println("line_id_in" + line_id_in);

        String queryUrl="http://apis.data.go.kr/6260000/BusanBIMS/busInfoByRouteId?"//요청 URL
                +"lineid="+line_id_in
                +"&serviceKey=" + key;

        try {
            URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//테그 이름 얻어오기
                        if(tag.equals("item")){
                            //System.out.println("item : " + tag);
                            //System.out.println("item : " + buffer.length());
                        }
                        // 정류소 이름
                        else if(tag.equals("bstopnm")){
                            xpp.next();
                            //System.out.println("bstopnm" + xpp.getText());
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(" ++");//줄바꿈 문자 추가
                        }
                        // 차량 번호
                        else if(tag.equals("carno")){
                            xpp.next();
                            //System.out.println("carno" + xpp.getText());
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(" --");//줄바꿈 문자 추가
                            //System.out.println(xpp.getText());
                        }
                        // 해당 노선에서 정류소 번호
                        else if(tag.equals("arsno")){
                            xpp.next();
                            //System.out.println("bstopidx" + xpp.getText());
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append(" **");//줄바꿈 문자 추가
                            buffer.append("\n");//줄바꿈 문자 추가
                        }
                        break;
                }
                eventType= xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        buffer.append("\n------------------------------\n더이상 조회된 정보가 없습니다.\n");
        //System.out.println(buffer);
        //System.out.println(buffer.length());
        return buffer.toString();//StringBuffer 문자열 객체 반환

    }//getXmlData method....

    // 알림을 표시하는 메소드
    private void showNotification(String title, String message) {
        // Notification 채널 생성
        String channelId = "my_channel";
        String channelName = "My Channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        // 진동 패턴 설정
        long[] vibrationPattern = {0, 1000, 500, 1000};

        // 알림 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.smalllogo) // 알림 아이콘 설정 (drawable 폴더에 알림 아이콘 이미지 추가 필요)
                .setVibrate(vibrationPattern); // 진동 패턴 설정

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String call = "";
                String callExtra = getIntent().getStringExtra("call");
                //System.out.println("callExtra " + callExtra);
                if (callExtra == null || callExtra.equals("no")) {
                    call = "no";
                    //System.out.println("callno " + call);
                } else {
                    call = "yes";
                    //System.out.println("callyes " + call);
                }
                String carno_in = getIntent().getStringExtra("carno_in");
                String bstopnm_alarm = getIntent().getStringExtra("bstopnm_alarm");

                while (true) {
                    if (call.equals("yes")) {
                        System.out.println("~~~ run ~~~");
                        data = getXmlData(); // 아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] items = data.split("\n");
                                for (int i = 0; i < items.length; i++) {
                                    if (items[i].contains(carno_in)) {
                                        String[] splitText = items[i].split("\\+\\+"); // **로 분할
                                        String frontText = splitText[0]; // 앞부분 텍스트
                                        // 알림 처리
                                        System.out.println("현재 위치 : " + frontText);
                                    }
                                    //System.out.println("items[i]" + items[i] + "/" + carno_in + "/" + bstopnm_alarm);
                                    if (items[i].contains(carno_in) && items[i].contains(bstopnm_alarm)) {
                                        // 알림 처리
                                        showNotification("알림", "1정거장 뒤 하차하셔야합니다.\n현재 정류장 : " + bstopnm_alarm);
                                        System.out.println("=== finish ===");
                                        return; // 스레드 실행 중지
                                    }
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(30000); // 30초 대기
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Button button_reservation = findViewById(R.id.bell_reservation);
        button_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Reservation1Activity.class);
                startActivity(intent);
            }
        });

        Button button_bell = findViewById(R.id.bell);
        button_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BellActivity.class);
                startActivity(intent);
            }
        });

        ImageButton setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<String> savedTextList = new ArrayList<>();

                    InputStream inputStream = openFileInput("mydata.txt");

                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString;
                        List<String> savedList = new ArrayList<>();

                        while ((receiveString = bufferedReader.readLine()) != null) {
                            savedList.add(receiveString);
                        }

                        inputStream.close();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("예약 현황")
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });

                        String[] savedArray = new String[savedList.size()];
                        savedArray = savedList.toArray(savedArray);
                        List<String> filteredList = new ArrayList<>();
                        filteredList.add("\n승차 정류장\n  => " + savedArray[0]);
                        filteredList.add("\n탑승한 버스\n  => " + savedArray[4]);
                        filteredList.add("\n하차 정류장\n  => " + savedArray[8]);

                        //builder.setItems(savedArray, null);
                        builder.setItems(filteredList.toArray(new String[0]), null);

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Exception" + "File not found: " + e.toString());
                } catch (IOException e) {
                    System.out.println("Exception" + "Can not read file: " + e.toString());
                }
            }
        });


        /////// 아래는 카메라 사용 권한 및 설정들임 ///////

        Button My = findViewById(R.id.My);
        My.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 카메라 권한이 있는지 확인
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // 카메라 열기
                    openCamera();
                } else {
                    // 카메라 권한 요청
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_REQUEST_CODE);
        } else {
            Toast.makeText(this, "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            // 사진을 캡쳐한 후 처리할 로직을 추가하세요
            Bundle extras = data.getExtras();
            Bitmap capturedImage = (Bitmap) extras.get("data");
            // 감지 로직을 추가하고 신분증이 감지되면 해당 이미지를 사용하세요
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 카메라 권한이 승인됨
                openCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
