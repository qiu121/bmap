package com.miluo.listener;

import android.content.Context;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.miluo.R;

/**
 * 地图点标记(Marker)监听器
 */
public class MarkerListenerHelper implements AMap.OnMarkerClickListener, AMap.OnMarkerDragListener {

    private final Context context;

    public MarkerListenerHelper(Context context) {
        this.context = context;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // 创建一个旋转动画，从当前角度到当前角度加上360度，以 marker 的中心点为旋转中心
        Animation animation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 360, 0, 0, 0);
        animation.setDuration(1000L);

        // 设置动画插值器为线性插值器，使动画匀速进行
        animation.setInterpolator(new LinearInterpolator());

        marker.setAnimation(animation);
        marker.startAnimation();

        // 创建一个底部对话框
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        // 加载底部对话框的布局
        View bottomInputView = View.inflate(context, R.layout.bottom_input_layout, null);
        bottomSheetDialog.setContentView(bottomInputView);

        // 找到布局中的类型和描述的 EditText
        EditText typeEditText = bottomInputView.findViewById(R.id.typeEditText);
        EditText descriptionEditText = bottomInputView.findViewById(R.id.descriptionEditText);

        // 设置输入框的初始内容
        typeEditText.setText("");
        descriptionEditText.setText("");

        // 显示底部对话框
        bottomSheetDialog.show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng specifiedLatLng = new LatLng(31.23958, 121.499763);
        LatLng currentLatLng = marker.getPosition();
        if (currentLatLng.equals(specifiedLatLng)) {
            marker.setTitle("东方明珠电视塔");
        }
        marker.setTitle("测试标题");
        marker.setSnippet("移动后位置: " + marker.getPosition().latitude + ", " + marker.getPosition().longitude);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker.setTitle("测试标题");
        marker.setSnippet("移动后位置: " + marker.getPosition().latitude + ", " + marker.getPosition().longitude);
    }
}
