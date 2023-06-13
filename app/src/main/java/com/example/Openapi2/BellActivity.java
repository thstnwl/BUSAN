package com.example.Openapi2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BellActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);

        // 이미지뷰 객체 참조
        ImageView before = findViewById(R.id.before);
        ImageView after = findViewById(R.id.after);

        after.setVisibility(View.INVISIBLE);

        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "'a'";

                // 데이터 전송을 위한 OutputStream 생성
                OutputStream outputStream;
                try {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter != null) {
                        if (ContextCompat.checkSelfPermission(BellActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(BellActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(BellActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(BellActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT},
                                    REQUEST_BLUETOOTH_PERMISSION);
                            return;
                        }

                        if (!bluetoothAdapter.isEnabled()) {

                            return;
                        }

                        // 블루투스 디바이스를 가져온다.
                        // 이 부분은 필요에 따라 디바이스 선택 및 검색 로직을 추가해야 합니다.
                        String deviceAddress = "98:DA:60:08:CF:F2"; // 블루투스 디바이스의 MAC 주소
                        device = bluetoothAdapter.getRemoteDevice(deviceAddress);

                        // BluetoothSocket 생성 및 연결
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        socket.connect();

                        outputStream = socket.getOutputStream();
                        outputStream.write(data.getBytes());

                        // 데이터 전송 후에는 연결을 닫아주는 것이 좋습니다.
                        socket.close();
                    }
                } catch (IOException e) {
                    // 에러 처리
                    e.printStackTrace();
                }
            }
        });
    }
}
