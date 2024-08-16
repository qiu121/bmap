package com.miluo.listener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;

import java.util.ArrayList;

/**
 * 地图事件监听器
 */
public class MapClickListenerHelper implements AMap.OnMapClickListener {

    private final AMap aMap;

    public MapClickListenerHelper(AMap aMap) {
        this.aMap = aMap;
    }


    @Override
    public void onMapClick(LatLng latLng) {
        ArrayList<LatLng> latLngList = new ArrayList<>();
        ArrayList<Polygon> polygons = new ArrayList<>();


        boolean contain = false;

        for (Polygon polygon : polygons) {
            if (polygon.contains(latLng)) {
                contain = true;
                break;
            }
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                // .title("新标记：" + (contain ? "在多边形内" : "不在多边形内"))
                .title("新标记")
                .draggable(false)
                .setFlat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .snippet("新标记位置: " + latLng.latitude + ", " + latLng.longitude);
        aMap.addMarker(markerOptions);
        // 将标点移动到地图中心
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latLng.latitude, latLng.longitude)));

        latLngList.add(latLng);

        // 绘制多边形
        // if (latLngList.size() % 5 == 0) {
        //     PolygonOptions polygonOptions = new PolygonOptions()
        //             .strokeWidth(5)
        //             .visible(true)

        //             .strokeColor(Color.rgb(178, 210, 158))
        //             .fillColor(Color.rgb(254, 254, 254));
        //
        //     for (LatLng l : latLngList) {
        //         polygonOptions.add(l);
        //     }
        //
        //     Polygon polygon = aMap.addPolygon(polygonOptions);
        //     polygons.add(polygon);
        //
        //     latLngList.clear();
        // }
    }
}
