package com.miluo.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.miluo.R;

public class LocationUtil {
    // 逆地理编码方法
    private static final String LOG_TAG = LocationUtil.class.getSimpleName();

    public static void getAddressFromLatLng(Context context, LatLng latLng, View bottomSheetView) throws AMapException {
        GeocodeSearch geocodeSearch = new GeocodeSearch(context);
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latLng.latitude, latLng.longitude), 200, GeocodeSearch.AMAP);

        geocodeSearch.getFromLocationAsyn(query);

        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getRegeocodeAddress() != null) {
                        RegeocodeAddress address = result.getRegeocodeAddress();
                        String addressName = address.getFormatAddress();

                        // 在底部弹窗中找到对应的 TextView 控件，并设置详细地址信息
                        TextView titleTextView = bottomSheetView.findViewById(R.id.titleTextView);
                        TextView snippetTextView = bottomSheetView.findViewById(R.id.snippetTextView);

                        titleTextView.setText("详细地址信息");
                        snippetTextView.setText(addressName);

                        Log.d("address",addressName);
                    }
                } else {
                    // 逆地理编码失败
                    Log.e(LOG_TAG, "逆地理编码失败，错误码：" + rCode);
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult result, int rCode) {
                // 不需要实现此方法
            }
        });
    }
}
