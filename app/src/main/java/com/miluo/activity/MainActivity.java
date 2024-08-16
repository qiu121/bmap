package com.miluo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.miluo.R;
import com.miluo.listener.MapClickListenerHelper;
import com.miluo.listener.MarkerListenerHelper;
import com.miluo.service.CameraService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    private MapView mMapView = null;
    private AMap aMap = null;

    private MyLocationStyle myLocationStyle; // 将 MyLocationStyle 声明为类的成员变量
    private boolean isFirstLocation = true; // 添加一个标志位，用于判断是否是第一次定位

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 99;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String[] LOCATION_PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);

        // 检查是否已经获得定位权限(ACCESS_FINE_LOCATION、ACCESS_COARSE_LOCATION)
        if (!EasyPermissions.hasPermissions(this, LOCATION_PERMISSIONS)) {
            // 如果没有权限，则请求权限
            EasyPermissions.requestPermissions(this,
                    " This app may not work correctly without the requested permissions.\n" +
                            "        Open the app settings screen to modify app permissions.",
                    MY_PERMISSIONS_REQUEST_LOCATION,
                    LOCATION_PERMISSIONS
            );

        } else {
            // 如果已经有权限，则执行您的定位相关代码
            setupLocation();

            // 监听地图点击事件
            MapClickListenerHelper clickListenerHelper = new MapClickListenerHelper(aMap);
            aMap.setOnMapClickListener(clickListenerHelper);
        }


        Button button = findViewById(R.id.my_button);
        // 监听按钮事件
        button.setOnClickListener(b -> {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String now = LocalDateTime.now().format(dateTimeFormatter);
                Toast.makeText(MainActivity.this, now, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(MainActivity.this, "用户点击了按钮", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "用户点击了按钮");

        });


        Button newButton = findViewById(R.id.new_button);
        // 设置按钮点击事件监听器
        newButton.setOnClickListener(v -> {
            // 检查是否已经获得相机权限
            if (EasyPermissions.hasPermissions(MainActivity.this, android.Manifest.permission.CAMERA)) {
                // 如果已经有权限，则启动相机应用
                CameraService.openCamera(this);

            } else {
                // 如果没有权限，则请求相机权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
        });

    }


    /**
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
        // 获取地图控件引用
        mMapView = findViewById(R.id.map_view);
        // 创建地图
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();

        /*
         * 红色：表示道路拥堵严重。
         * 黄色：表示道路有一定程度的拥堵。
         * 绿色：表示道路畅通无阻。
         */
        aMap.setTrafficEnabled(true);// 显示实时交通状况
        aMap.setMapType(AMap.MAP_TYPE_NORMAL); // 导航模式

        UiSettings uiSettings = aMap.getUiSettings();
        // 配置地图底部logo 底部边距为 -100
        uiSettings.setLogoBottomMargin(-100);

        // 隐藏缩放按钮 默认显示
        uiSettings.setZoomControlsEnabled(false);

        // 显示比例尺 默认不显示，<当去除logo,比例尺也会显示不全>
        // uiSettings.setScaleControlsEnabled(true);

        // 指南针显示
        uiSettings.setCompassEnabled(true);

        // 初始化地图并设置地图中心位置为上海
        // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(31.2304, 121.4737), 10);
        // aMap.moveCamera(cameraUpdate);

        // marker标记物
        LatLng latLng = new LatLng(31.23958, 121.499763);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("东方明珠电视塔")
                .draggable(false)
                .setFlat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("<欣赏黄浦江两岸的美景和圆月>" +
                        "欣赏黄浦江两岸的美景和圆月欣赏黄浦江两岸的美景和圆月欣赏黄浦江两岸的美景和圆月");
        aMap.addMarker(markerOptions);

        // 添加标记监听器(点击、拖拽)
        MarkerListenerHelper markerListenerHelper = new MarkerListenerHelper(this);
        aMap.setOnMarkerClickListener(markerListenerHelper);
        aMap.setOnMarkerDragListener(markerListenerHelper);

    }

    /**
     * 定位操作
     */
    private void setupLocation() {
        if (myLocationStyle == null) {
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.interval(1000); // 设置定位间隔
        }
        // 连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);

        aMap.setMyLocationStyle(myLocationStyle);

        // 设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);

        // 设置为true表示启动显示定位蓝点
        aMap.setMyLocationEnabled(true);


        // 手动触发一次定位
        aMap.setOnMyLocationChangeListener(location -> {
            if (isFirstLocation) {
                // 第一次定位时将蓝点移动到地图中心
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                isFirstLocation = false; // 将标志位设置为false，表示已经进行过第一次定位
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了相机权限，启动相机应用
                CameraService.openCamera(this);
                // 用户拒绝了相机权限请求，您可以在这里处理逻辑
                // Toast.makeText(MainActivity.this, "用户拒绝权限请求", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "用户拒绝权限请求", Toast.LENGTH_SHORT).show();

            }
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        // 定位权限已经授予，触发一次定位
        setupLocation();

        // 监听地图点击事件
        MapClickListenerHelper mapClickListenerHelper = new MapClickListenerHelper(aMap);
        aMap.setOnMapClickListener(mapClickListenerHelper);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // 权限被拒绝
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            // 用户永久拒绝权限，可以引导用户去应用设置界面开启权限
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_LOCATION, new Bundle());
        }
    }


}
