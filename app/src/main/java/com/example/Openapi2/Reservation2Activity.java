package com.example.Openapi2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reservation2Activity extends Activity {

    String key="tJTT7FZrG7Wn5K77BVwpPwQqq2QVhkeXiPC5BuZcgRXeesYinqJ6y3y/CqDrW6uSGxdGH84uG2dLJ/880mabtQ==";
    String data;

    private List<String> first = new ArrayList<>();
    private List<String> second = new ArrayList<>();
    private List<String> lines = new ArrayList<>();
    private String carno = "";
    private String bstopid_in = "";
    private String bstopnm_in = "";
    private String arsno_in = "";

    //XmlPullParser를 이용하여 OpenAPI XML 파일 파싱하기(parsing)
    String getXmlData(){

        bstopid_in = getIntent().getStringExtra("bstopid_in");
        bstopnm_in = getIntent().getStringExtra("bstopnm_in");
        arsno_in = getIntent().getStringExtra("arsno_in");
        StringBuffer buffer=new StringBuffer();

        String queryUrl="http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?"//요청 URL
                +"bstopid="+bstopid_in
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
                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//테그 이름 얻어오기
                        //System.out.println("tag : " + tag);
                        if(tag.equals("item")){
                            //System.out.println("item : " + tag);
                            //System.out.println("bf : " + buffer.length());
                        }
                        else if(tag.equals("lineno")){
                            xpp.next();
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("번 \n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("lineid")){
                            xpp.next();
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("++\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("carno1")){
                            xpp.next();
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("##\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("min1")){
                            xpp.next();
                            buffer.append(xpp.getText());//description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("분 (");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("station1")){
                            xpp.next();
                            buffer.append(xpp.getText());//description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("번째 전 정류장 도착)\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("carno2")){
                            xpp.next();
                            buffer.append(xpp.getText());//category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("**\n");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("min2")){
                            xpp.next();
                            buffer.append(xpp.getText());//telephone 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("분 (");//줄바꿈 문자 추가
                        }
                        else if(tag.equals("station2")){
                            xpp.next();
                            buffer.append(xpp.getText());//description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("번째 전 정류장 도착)\n");//줄바꿈 문자 추가
                        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation2);

        ListView listview = findViewById(R.id.list_view);

        // 선택한 버스정류장이름(정류장번호) 가져오기
        String bstopnm_arsno_in = getIntent().getStringExtra("bstopnm_arsno_in");
        TextView textView = findViewById(R.id.busarrivals_view); // 출력할 텍스트뷰
        textView.setText(bstopnm_arsno_in);

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
                            //System.out.println("==" + items[i]);
                            if (items[i].contains("##")){
                                first.add(items[i]);
                            } else if (items[i].contains("**")) {
                                second.add(items[i]);
                            } else if (items[i].contains(":")) {
                                int index = sb.indexOf("번");
                                sb.insert(index + 1, " (" + items[i].substring(7, 9) + ")버스");
                            } else if (items[i].contains("++")) {
                                int index = sb.indexOf("++");
                                sb.append(items[i]).append("\n");
                            } else{
                                sb.append(items[i]).append("\n"); // 아이템을 추가할 StringBuilder에 1줄씩 추가
                            }
                            if (items[i].contains("++")) { // ++ 문자 있으면 끊어서 아이템에 추가
                                itemList.add(sb.toString().trim());
                                sb = new StringBuilder();
                                }
                        }

                        if (sb.length() > 0) { // 마지막에 4줄 미만의 데이터가 남아있을 경우 아이템에 추가
                            itemList.add(sb.toString().trim());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Reservation2Activity.this, android.R.layout.simple_list_item_1, itemList) {

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                                // 현재 아이템을 가져오고 두 부분으로 나눔
                                String item = itemList.get(position);
                                String[] parts = item.split("\\n");

                                // 텍스트를 위한 SpannableStringBuilder 생성
                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                // 첫 번째 부분을 파란색으로 표시하고 글자 크기를 두 배로 키움
                                //System.out.println("parts[0]" + parts[0]);
                                builder.append(parts[0]);
                                builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, parts[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.setSpan(new RelativeSizeSpan(1.3f), 0, parts[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                // 두 번째 부분을 그대로 추가함
                                if (parts.length > 1) {
                                    builder.append("\n  ");
                                    for (int i=1; i<parts.length-1; i++){
                                        builder.append(parts[i]+"\n  ");
                                        //System.out.println("builder.append(parts[i])" + parts[i]);
                                        if (i==parts.length-2) {
                                            lines.add(parts[i + 1]);
                                        }
                                    }
                                }

                                // TextView에 SpannableStringBuilder를 설정함
                                textView.setText(builder);

                                return view;
                            }
                        };
                        listview.setAdapter(adapter); // 리스트뷰에 어댑터를 설정하여 버스번호& 몇분 뒤에 오는지 출력

                        // 리스트뷰에 OnItemClickListener 등록 -> 오는 버스들 두개 중 몇번째꺼 탈건지
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // 아이템의 텍스트 가져오기
                                String text = ((TextView) view).getText().toString();

                                // 받아온 데이터를 줄바꿈을 기준으로 나눠서 문자열 배열에 저장
                                String[] items = text.split("\n");
                                String[] selectedItems = new String[items.length - 2];

                                // 리스트뷰 아이템을 저장할 리스트
                                List<String> itemList = new ArrayList<>();
                                // 라디오 버튼의 텍스트를 줄별로 저장
                                for (int i = 0; i < items.length - 1; i++) {
                                    itemList.add(items[i].trim());
                                    //System.out.println("h" + items[i]);
                                }

                                for (int i = 1; i < items.length - 1 ; i++) {
                                    selectedItems[i-1] = items[i];
                                }

                                // 리스트뷰 어댑터 생성
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Reservation2Activity.this, android.R.layout.simple_list_item_single_choice, selectedItems);

                                // 어댑터를 설정하여 다이얼로그 생성
                                AlertDialog.Builder builder = new AlertDialog.Builder(Reservation2Activity.this);
                                builder.setTitle(items[0]); // 다이얼로그 제목 설정
                                builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 라디오 버튼 선택 시 실행될 코드
                                    }
                                });
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                        if (selectedPosition == ListView.INVALID_POSITION) {
                                            Toast.makeText(Reservation2Activity.this, "선택해주세요", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (selectedPosition == 0) {
                                                // first 리스트 가져오는 코드
                                                String[] itemArray = first.toArray(new String[first.size()]);
                                                //System.out.println(Arrays.toString(itemArray));
                                                carno = itemArray[position].substring(0, itemArray[position].length() - 2);
                                                //System.out.println("carno" + carno);
                                            } else if (selectedPosition == 1) {
                                                // second 리스트 가져오는 코드
                                                String[] itemArray = second.toArray(new String[second.size()]);
                                                //System.out.println(Arrays.toString(itemArray));
                                                carno = itemArray[position].substring(0, itemArray[position].length() - 2);
                                                //System.out.println("carno" + carno);
                                            }
                                            // 선택된 라디오 버튼의 텍스트 가져오기
                                            String selectedText = itemList.get(selectedPosition);
                                            String line_id = lines.get(position);
                                            line_id = line_id.substring(0, line_id.length() - 2);
                                            // 다른 액티비티로 전달할 인텐트 생성
                                            Intent intent = new Intent(Reservation2Activity.this, Reservation3Activity.class);

                                            // 인텐트에 클릭된 아이템의 텍스트 추가
                                            intent.putExtra("lineno_in", items[0]); // 탑승 버스 번호
                                            intent.putExtra("bstopnm_in", bstopnm_in); // 탑승 정류소 명
                                            intent.putExtra("arsno_in", arsno_in); // 탑승 정류소 번호
                                            intent.putExtra("bstopnm_arsno_in", bstopnm_arsno_in); // 탑승 정류소명(정류소번호)
                                            intent.putExtra("carno_in", carno); // 탑승 차량 번호
                                            intent.putExtra("line_id_in", line_id); // 탑승 버스 노선 아이디
                                            intent.putExtra("bstopid_in", bstopid_in); // 정류소 아이디
                                            intent.putExtra("bstopnm_arsno_in", bstopnm_arsno_in); // 정류소명_정류소번호
                                            //System.out.println("탑승 정류소 아이디 : " + bstopid_in);
                                            //System.out.println("탑승 정류소 명 : " + bstopnm_in);
                                            //System.out.println("탑승 정류소명(정류소번호) : " + bstopnm_arsno_in);
                                            //System.out.println("탑승 버스 번호 : " + items[0]);
                                            //System.out.println("탑승 차량 번호 : " + carno);
                                            //System.out.println("탑승 버스 노선 아이디 : " + line_id);
                                            // 다른 액티비티 호출
                                            startActivity(intent);
                                        }
                                    }
                                });
                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 취소 버튼 클릭 시 실행될 코드
                                    }
                                });
                                builder.show();
                            }

                        });
                    }
                });
            }

        }).start();


        // 이전 버튼 : Reservation2Activity => Reservation1Activity
        Button back2 = findViewById(R.id.back2);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Reservation1Activity.class);
                startActivity(intent);
            }
        });

    }
}//Reservation2Activity class..