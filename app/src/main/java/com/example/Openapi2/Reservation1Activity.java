package com.example.Openapi2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

public class Reservation1Activity extends Activity {

    private SpeechRecognizer speechRecognizer;
    private static final int REQUEST_CODE = 1234;

    EditText edit;
    ListView text;

    XmlPullParser xpp;
    String key="tJTT7FZrG7Wn5K77BVwpPwQqq2QVhkeXiPC5BuZcgRXeesYinqJ6y3y/CqDrW6uSGxdGH84uG2dLJ/880mabtQ==";
    //서비스 키

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation1);

        edit= (EditText)findViewById(R.id.bus_stop_id_input);
        text= (ListView)findViewById(R.id.list_view);

        //Reservation1Activity 클래스가 실행되면 activity_main.xml로 이동되는 소스코드
        // 이전 버튼 : Reservation1Activity => MainActivity
        Button back1 = findViewById(R.id.back1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //Button을 클릭했을 때 자동으로 호출되는 callback method....
    public void onSearchButtonClick(View v){
        switch( v.getId() ){
            case R.id.search_button:

                //Android 4.0 이상 부터는 네트워크를 이용할 때 반드시 Thread 사용해야 함
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        data = getXmlData(); // 아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] items = data.split("\n"); // 받아온 데이터를 줄바꿈을 기준으로 나눠서 문자열 배열에 저장
                                List<String> itemList = new ArrayList<>(); // 리스트뷰 아이템을 저장할 리스트
                                StringBuilder sb = new StringBuilder();

                                for (int i = 0; i < items.length; i++) {
                                    sb.append(items[i]).append("\n"); // 아이템을 추가할 StringBuilder에 1줄씩 추가

                                    if ((i + 1) % 4 == 0) { // 4줄씩 끊어서 아이템에 추가
                                        itemList.add(sb.toString().trim());
                                        sb = new StringBuilder();
                                    }
                                }

                                if (sb.length() > 0) { // 마지막에 4줄 미만의 데이터가 남아있을 경우 아이템에 추가
                                    itemList.add(sb.toString().trim());
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Reservation1Activity.this, android.R.layout.simple_list_item_1, itemList);
                                text.setAdapter(adapter); // 리스트뷰에 어댑터를 설정하여 아이템 출력


                                // 리스트뷰에 OnItemClickListener 등록
                                text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        // 클릭된 아이템의 텍스트 가져오기
                                        String selectedItem = itemList.get(position);

                                        // 정류장이름(정류장번호)
                                        String[] lines = selectedItem.split("\\n"); // 텍스트를 줄별로 스플릿
                                        String[] busstop_dt = lines[0].split(":");
                                        String[] busstop_nm = lines[1].split(":");
                                        String[] busstop_id = lines[2].split(":");
                                        String selected_busstop_id = busstop_id[1];

                                        String selected_busstop = busstop_nm[1] + " (" + busstop_id[1] + ")"; // 첫 번째 줄의 텍스트 추출

                                        // 다른 액티비티로 전달할 인텐트 생성
                                        Intent intent = new Intent(Reservation1Activity.this, Reservation2Activity.class);

                                        // 인텐트에 클릭된 아이템의 텍스트 추가
                                        intent.putExtra("bstopnm_in", busstop_nm[1]); // 탑승 정류소 명
                                        intent.putExtra("bstopid_in", busstop_dt[1]); // 탑승 정류소 아이디
                                        intent.putExtra("arsno_in", busstop_id[1]); // 탑승 정류소 번호
                                        intent.putExtra("bstopnm_arsno_in", selected_busstop); // 탑승 정류소명_정류소번호
                                        //System.out.println("탑승 정류소 아이디 : " + busstop_dt[1]);
                                        //System.out.println("탑승 정류소명(정류소번호) : " + selected_busstop);
                                        // 다른 액티비티 호출
                                        startActivity(intent);
                                    }
                                });


                            }
                        });
                    }

                }).start();
                break;
        }
    }//mOnClick method..


    //XmlPullParser를 이용하여 OpenAPI XML 파일 파싱하기(parsing)
    String getXmlData(){

        StringBuffer buffer=new StringBuffer();

        String str= edit.getText().toString();//EditText에 작성된 Text얻어오기
        String location = URLEncoder.encode(str);//한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..

        String queryUrl="http://apis.data.go.kr/6260000/BusanBIMS/busStopList?"//요청 URL
                +"bstopnm="+location
                +"&pageNo=1&numOfRows=100&ServiceKey=" + key;

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
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//테그 이름 얻어오기

                        if(tag.equals("item")) ;// 첫번째 검색결과
                        else if(tag.equals("bstopid")){
                            buffer.append("정류소아이디 : ");
                            xpp.next();
                            buffer.append(xpp.getText());//title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n"); //줄바꿈 문자 추가
                        }
                        else if(tag.equals("bstopnm")){
                            buffer.append("정류소명 : ");
                            xpp.next();
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("arsno")){
                            buffer.append("정류소번호 :");
                            xpp.next();
                            buffer.append(xpp.getText());//description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("stoptype")){
                            buffer.append("정류소구분 :");
                            xpp.next();
                            buffer.append(xpp.getText());//telephone 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            //buffer.append("\n");//줄바꿈 문자 추가
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //테그 이름 얻어오기

                        if(tag.equals("item")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                        break;
                }

                eventType= xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        buffer.append("\n------------------------------\n더이상 조회된 정보가 없습니다.\n");
        return buffer.toString();//StringBuffer 문자열 객체 반환

    }//getXmlData method....

    // 마이크
    public void micclk(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN); // 한국어로 설정
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀하세요");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "귀하의 기기에서 음성 인식 기능이 지원되지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            edit.setText(result.get(0));
        }
    }

}//MainActivity class..