package com.map202306.test;

import android.app.Activity;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class Dst extends Activity {
    public static void calculateWalkingRoute(TMapView tMapView) {
        // 출발지와 도착지 좌표 설정 (예시로 강원대학교와 목포해상대학교의 좌표를 사용)
        TMapPoint startPoint = new TMapPoint(37.4534, 129.1627); // 강원대학교 좌표
        TMapPoint endPoint = new TMapPoint(34.8181, 126.3911); // 목포해상대학교 좌표

        // 경로 탐색을 위한 TMapData 객체 생성
        TMapData tMapData = new TMapData();

        // 보행자 경로 탐색 요청
        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine path) {
                // 경로 탐색 결과를 받았을 때 처리하는 로직 작성
                // 경로를 지도에 추가하여 표시하거나, 다른 작업을 수행할 수 있습니다.

                // 경로를 지도에 추가하여 표시하는 예시
                tMapView.addTMapPath(path);
                float distance = (float) path.getDistance();
                int duration = calculateDuration(distance); // 계산된 소요 시간

                // 경로 정보를 출력 또는 다른 작업 수행
                System.out.println("경로 거리: " + distance + "m");
                System.out.println("소요 시간: " + duration + "분");
            }
        });
    }

    private static int calculateDuration(float distance) {
        // 보행자의 평균 이동 속도를 가정하여 소요 시간 계산
        float walkingSpeed = 1.4f; // 보행자의 평균 이동 속도 (m/s)
        float durationInSeconds = distance / walkingSpeed;
        int durationInMinutes = (int) (durationInSeconds / 60);
        return durationInMinutes;
    }
}
