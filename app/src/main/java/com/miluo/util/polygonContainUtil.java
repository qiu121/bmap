package com.miluo.util;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;

import java.util.List;

public class polygonContainUtil {
    /**
     * 判断标点 是否在特定范围内
     *
     * @param aMap       地图对象
     * @param latLngList 经纬点集
     * @param latLng     经纬点
     * @return
     */
    public static boolean polygonContain(AMap aMap, List<LatLng> latLngList, LatLng latLng) {


        // 声明 多边形参数对象
        PolygonOptions polygonOptions = new PolygonOptions();

        // 设置区域是否显示
        polygonOptions.visible(false);
        // 添加 多边形的每个顶点（顺序添加）
        for (LatLng i : latLngList) {
            polygonOptions.add(i);
        }

        Polygon polygon = aMap.addPolygon(polygonOptions);
        boolean contains = polygon.contains(latLng);
        // polygon.remove();
        return contains;
    }
}
