package com.example.somture.getrssi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class MainActivity extends AppCompatActivity {


    Button turnon ;
    Button turnoff ;
    Button scanf ;
    TextView textView;
    ListView listView;

    BluetoothAdapter bluetoothAdapter;
    public  static ArrayAdapter adapter = null;
    public static BluetoothDevice device = null;

    //定义一个列表，存蓝牙设备的地址。
    public ArrayList<String> arrayList = null;
    //定义一个列表，存蓝牙设备地址，用于显示。
    public ArrayList<String> deviceName = null;

    //请求打开蓝牙设备
    private static final int BLUETOOTH_REQUEST_ENABLE = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public final static int REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();
        deviceName = new ArrayList<>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        turnon = (Button)findViewById(R.id.on);
        turnoff = (Button)findViewById(R.id.off);
        scanf = (Button)findViewById(R.id.scanf);
//        textView.findViewById(R.id.textview);

        turnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBluetooth();
            }
        });

        turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBluetooth();
            }
        });

        scanf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBluetooth();
            }
        });

        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, deviceName);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

    }

    //开启蓝牙
    protected void openBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            //向用户发出请求，开启蓝牙设备
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BLUETOOTH_REQUEST_ENABLE);
        }else{
            Toast.makeText(this, "蓝牙已开启，请勿重复点击！", Toast.LENGTH_SHORT).show();
        }
    }

    //关闭蓝牙
    protected void closeBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Toast.makeText(MainActivity.this, "已关闭蓝牙", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "蓝牙已关闭，请勿重复点击！", Toast.LENGTH_SHORT).show();
        }
    }

    //查找远端蓝牙设备
    protected void findBluetooth()  {
            adapter.clear();
            adapter.notifyDataSetChanged();//更新。
            registerReceiver();
            // 判断是否在搜索,如果在搜索，就取消搜索
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            //开始查找蓝牙设备
            bluetoothAdapter.startDiscovery();
            Toast.makeText(this, "搜索中...请稍后", Toast.LENGTH_SHORT).show();

    }

    //注册监听
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, filter);
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                float d = (abs(rssi) - 60) / (10 * 2);
                double p = pow(10,d);
                deviceName.add("设备名：" + device.getName() + "\n" + "设备地址：" + device.getAddress() + "\n" + "RSSI：" + p);//将搜索到的蓝牙名称和地址添加到列表。
                arrayList.add(device.getAddress());//将搜索到的蓝牙地址添加到列表。
                adapter.notifyDataSetChanged();//更新。
            }
        }
    };

    //解除注册
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

}
