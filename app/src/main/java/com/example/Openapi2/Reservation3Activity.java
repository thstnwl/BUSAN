package com.example.Openapi2;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Reservation3Activity extends Activity {

    String key="tJTT7FZrG7Wn5K77BVwpPwQqq2QVhkeXiPC5BuZcgRXeesYinqJ6y3y/CqDrW6uSGxdGH84uG2dLJ/880mabtQ==";
    String data;

    private List<String> arsno = new ArrayList<>();
    private List<String> busstopname = new ArrayList<>();
    private List<String> carno = new ArrayList<>();
    private String arsno_out = "";
    private String arsno_alarm = "";
    private String bstopnm_alarm = "";
    private Integer index = 0;

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
                        }
                        buffer.append("\n");//줄바꿈 문자 추가
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation3);

        ListView listview = findViewById(R.id.list_view);

        // 선택한 버스정류장이름(정류장번호) 가져오기
        String lineno_in = getIntent().getStringExtra("lineno_in");
        String bstopnm_in = getIntent().getStringExtra("bstopnm_in");
        String bstopid_in = getIntent().getStringExtra("bstopid_in");
        String carno_in = getIntent().getStringExtra("carno_in");
        String arsno_in = getIntent().getStringExtra("arsno_in");
        String line_id_in = getIntent().getStringExtra("line_id_in");
        String bstopnm_arsno_in = getIntent().getStringExtra("bstopnm_arsno_in");
        String selected_bus_number = getIntent().getStringExtra("selected_bus_number");

        TextView textView = findViewById(R.id.bus_stop_id_input); // 출력할 텍스트뷰
        textView.setText(bstopnm_in + "(" + lineno_in + ")");

        //Android 4.0 이상 부터는 네트워크를 이용할 때 반드시 Thread 사용해야 함
        new Thread(new Runnable() {

            @Override
            public void run() {
                data = getXmlData(); // 아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //System.out.println("data" + data);
                        String[] items = data.split("\n"); // 받아온 데이터를 줄바꿈을 기준으로 나눠서 문자열 배열에 저장
                        List<String> itemList = new ArrayList<>(); // 리스트뷰 아이템을 저장할 리스트
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < items.length; i++) {
                            //System.out.println("items" + items[i]);
                            int length_num = arsno.size();
                            int length_busstopname = busstopname.size();
                            int length_carno = carno.size();
                            //System.out.println(length_num + "/" + length_busstopname + "/" + length_carno);

                            if (items[i].contains("**")) {
                                sb = new StringBuilder();
                                if (length_num+2 == length_busstopname) {
                                    arsno.add("none");
                                    arsno.add(items[i]);
                                } else {
                                    arsno.add(items[i]);
                                }
                                if (items[i].contains(arsno_in)) {
                                    index = length_num; // 일치하는 값의 인덱스를 저장
                                }
                                //System.out.println("sb" + sb);
                            } else if (items[i].contains("++")) {
                                sb.append(items[i].substring(0, items[i].length() - 2)).append("\n");
                                //System.out.println("sb" + sb);
                                busstopname.add(items[i]);
                            } else if ((length_num == length_busstopname) && (length_carno == length_num-1)) {
                                if (items[i].contains("--")) {
                                    //System.out.println("items1" + items[i]);
                                    carno.add(items[i]);
                                } else {
                                    //System.out.println("none1" + items[i]);
                                    carno.add("none");
                                }
                            } else if ((length_num == length_busstopname) && (length_carno == length_num-2)) {
                                if (items[i].contains("--")) {
                                    //System.out.println("items2" + items[i]);
                                    carno.add("none");
                                    carno.add(items[i]);
                                } else {
                                    //System.out.println("none2" + items[i]);
                                    carno.add("none");
                                    carno.add("none");
                                }
                            }
                        }
                        int length = busstopname.size();
                        int length2 = arsno.size();
                        int length1 = carno.size();
                        //System.out.println("List의 길이: " + length + "==" + length2 + "==" + length1);
                        //System.out.println(busstopname);
                        //System.out.println(arsno);
                        //System.out.println(carno);
                        //System.out.println("탑승 정류장 인덱스 번호" + index);
                        //System.out.println("index" + index);
                        //System.out.println(busstopname.get(index) + "/" + arsno.get(index));

                        ArrayList<String> busStopNames = new ArrayList<>();

                        // busstopname 리스트의 index 변수부터 마지막 원소까지 아이템으로 설정
                        for (int i = index+1; i < busstopname.size(); i++) {
                            busStopNames.add(busstopname.get(i));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Reservation3Activity.this, android.R.layout.simple_list_item_1, busStopNames) {

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                                // 현재 아이템을 가져오고 두 부분으로 나눔
                                String item = busStopNames.get(position);
                                String[] parts = item.split("\\n");

                                // 텍스트를 위한 SpannableStringBuilder 생성
                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                // 첫 번째 부분을 파란색으로 표시하고 글자 크기를 두 배로 키움
                                builder.append(parts[0].substring(0, parts[0].length() - 3));
                                //builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, parts[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.setSpan(new RelativeSizeSpan(1.3f), 0, parts[0].length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                // TextView에 SpannableStringBuilder를 설정함
                                textView.setText(builder);

                                return view;
                            }
                        };

                        listview.setAdapter(adapter); // 리스트뷰에 어댑터를 설정하여 버스번호& 몇분 뒤에 오는지 출력
                        // 리스트뷰에 OnItemClickListener 등록 -> 오는 버스들 두 개 중 몇 번째꺼 탈건지
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // 아이템의 텍스트 가져오기
                                String bstopnm_out = ((TextView) view).getText().toString();
                                //System.out.println("here" + arsno.get(index+1+position).substring(0, arsno.get(index+1+position).length() - 3));
                                arsno_out = arsno.get(index+1+position).substring(0, arsno.get(index+1+position).length() - 3);
                                arsno_alarm = arsno.get(index+position).substring(0, arsno.get(index+position).length() - 3);
                                bstopnm_alarm = busstopname.get(index+position).substring(0, busstopname.get(index+position).length() - 3);
                                //System.out.println("============= " + bstopnm_alarm);
                                // 다른 액티비티로 전달할 인텐트 생성
                                Intent intent = new Intent(Reservation3Activity.this, Reservation4Activity.class);

                                // 인텐트에 클릭된 아이템의 텍스트 추가
                                //System.out.println("============= " + items[0]);
                                intent.putExtra("select_bus_num", items[0]);
                                intent.putExtra("selected_bus_number", selected_bus_number);
                                intent.putExtra("lineno_in", lineno_in); // 탑승 버스 번호
                                intent.putExtra("bstopnm_in", bstopnm_in); // 탑승 정류소 명
                                intent.putExtra("bstopnm_arsno_in", bstopnm_arsno_in); // 탑승 정류소명(정류소번호)
                                intent.putExtra("carno_in", carno_in); // 탑승 차량 번호
                                intent.putExtra("arsno_in", arsno_in); // 탑승 정류소 번호
                                intent.putExtra("line_id_in", line_id_in); // 탑승 버스 노선 아이디
                                intent.putExtra("bstopid_in", bstopid_in); // 탑승 정류소 아이디
                                intent.putExtra("bstopnm_out", bstopnm_out); // 하차 정류소 명
                                intent.putExtra("arsno_out", arsno_out); // 하차 정류소 번호
                                intent.putExtra("arsno_alarm", arsno_alarm); // 하차 정류소 2개 전 알람을 줄 번호
                                intent.putExtra("bstopnm_alarm", bstopnm_alarm); // 하차 정류소 2개 전 알람을 줄 번호
                                intent.putExtra("index", index); // 탑승 정류소 index
                                //System.out.println("탑승 정류소 아이디 : " + bstopid_in);
                                //System.out.println("탑승 정류소 명 : " + bstopnm_in);
                                //System.out.println("탑승 정류소명(정류소번호) : " + bstopnm_arsno_in);
                                //System.out.println("탑승 버스 번호 : " + lineno_in);
                                //System.out.println("탑승 차량 번호 : " + carno_in);
                                //System.out.println("탑승 정류소 번호 : " + arsno_in);
                                //System.out.println("하차 정류소 명 : " + bstopnm_out);
                                //System.out.println("하차 정류소 번호 : " + arsno_out);
                                //System.out.println("탑승 버스 노선 아이디 : " + line_id_in);

                                //intent.putExtra("line_id", line_id);
                                // 다른 액티비티 호출
                                startActivity(intent);
                            }
                        });
                    }
                });
            }

        }).start();


        // 이전 버튼 : Reservation2Activity => Reservation1Activity
        Button back2 = findViewById(R.id.back3);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Reservation2Activity.class);
                startActivity(intent);
            }
        });

    }
}//MainActivity class..