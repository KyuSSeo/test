package com.map202306.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    //implements MapinfoActivity.OnRouteButtonClickListener
    ArrayList<String> mArrayLineID;
    ArrayList<String> mArrayMarkerID;
    private static int mMarkerID;

    private ArrayList<TMapPOIItem> mArrPoiItem = null;
    private TMapPOIItem mPoiItem = null;
    private CharSequence[] items = null;
    private ArrayList<Marker> markerList;
    private TMapPoint tMapPoint;
    private boolean permissionDeniedToastShown = false;


    public void onClickListenerCallback(View v) {
        onClickListenerCallback(tMapView);
    }

    //장소 검색 관련 변수 선언
    private ImageButton searchBtn;
    private EditText inputLocation_editText;
    private ListView listView;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private TMapData tMapData;
    private boolean list_display = false;


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1981;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2981;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;

    private ImageButton myPositionButton;
    private TMapGpsManager tMapGpsManager;
    private TMapView tMapView;
    public List<Toilet> toiletList;
    private Bitmap resized;


    private EditText edtSearch;
    private ImageButton btnSearch;
    private RecyclerView rvSearch;

    private ImageButton SosButton;
    private TMapView markerBitmap;

    //버튼 클릭 여부
    private static final int REQUEST_CODE_SUB_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //장소 검색 관련 변수 설정
        searchBtn = (ImageButton) findViewById(R.id.btn_search_delete);
        listView = (ListView)findViewById(R.id.locationListView);
        inputLocation_editText = (EditText) findViewById(R.id.edt_search);
        tMapData = new TMapData();
        markerList = new ArrayList<Marker>();
        listData = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);

        listView.setVisibility(View.INVISIBLE);

        //신고 문의
        SosButton = findViewById(R.id.btn_sos);


        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setHttpsMode(true);
        tMapView.setCenterPoint(129.1627, 37.4534); // 강원대학교 좌표
        tMapView.setSKTMapApiKey("Ft6x7kExpN7fmYGCT0AzB1PtTvqg03cS5lcaN0GV");
        linearLayoutTmap.addView(tMapView);

        myPositionButton = findViewById(R.id.my_position);


        SosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Sos.class);
                startActivity(intent);
            }
        });

        myPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();
                Toast.makeText(MainActivity.this, "Gps 이동", Toast.LENGTH_SHORT).show();
            }
        });

        //검색
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ListView 활성화 & 지도 활성화
                linearLayoutTmap.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                list_display = true;
                //listData 초기화
                listData.clear();
                //입력받은 장소 이름으로 POI검색 & ListView에 반영
                String inputLocation = inputLocation_editText.getText().toString();

                tMapData.findAllPOI(inputLocation, 7, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        // 검색 결과가 있을 경우
                        if (arrayList != null && !arrayList.isEmpty()) {
                            // 검색 결과를 모두 보여주는 ListView 업데이트
                            listData.clear();
                            for (TMapPOIItem item : arrayList) {
                                listData.add(item.getPOIName());
                            }
                            adapter.notifyDataSetChanged();

                            // 검색 결과의 첫 번째 항목으로 이동
                            TMapPOIItem firstItem = arrayList.get(0);
                            double latitude = firstItem.getPOIPoint().getLatitude();
                            double longitude = firstItem.getPOIPoint().getLongitude();

                            // 마커 위치로 카메라 이동
                            tMapView.setCenterPoint(longitude, latitude);

                            // 마커 생성 및 추가
                            TMapMarkerItem markerItem = new TMapMarkerItem();
                            TMapPoint markerPoint = new TMapPoint(latitude, longitude);
                            markerItem.setTMapPoint(markerPoint);
                            markerItem.setName(firstItem.getPOIName());
                            markerItem.setVisible(TMapMarkerItem.VISIBLE);

                            Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
                            markerItem.setIcon(markerBitmap);

                            // 마커 추가
                            //tMapView.addMarkerItem("marker", markerItem);


                            // 카메라 이동 후 자동으로 중지
                            tMapView.setTrackingMode(false);
                        } else {
                            // 검색 결과가 없을 경우 처리
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    // Toast.makeText(MainActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });



        // listView 아이템 클릭 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Marker marker;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tMapData.findAllPOI(listData.get(position), 1, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                        if (arrayList == null || arrayList.isEmpty()) {
                            // 검색 결과가 없거나 null인 경우 처리
                            return;
                        }

                        TMapPOIItem item = arrayList.get(0);
                        marker = new Marker(item.getPOIName(), item.getPOIAddress(), "marker_" + markerList.size(), item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                        // 마커 위치로 카메라 이동
                        tMapView.setCenterPoint(marker.getLongitude(), marker.getLatitude(), true);

                        // Point 좌표 설정
                        tMapPoint.setLongitude(marker.getLongitude());
                        tMapPoint.setLatitude(marker.getLatitude());

                        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();

                        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
                        tMapMarkerItem.setIcon(markerBitmap); // 마커 아이콘 지정
                        tMapMarkerItem.setCanShowCallout(true);
                        //tMapMarkerItem.setAutoCalloutVisible(true);
                        tMapMarkerItem.setVisible(TMapMarkerItem.VISIBLE);
                        tMapMarkerItem.setTMapPoint(tMapPoint); // 마커 좌표 설정

                        // 마커 Title & SubTitle 지정
                        tMapMarkerItem.setCalloutTitle(marker.getName());
                        tMapMarkerItem.setCalloutSubTitle(marker.getAddress().trim());

                        // 지도에 마커 추가
                        //tMapView.addMarkerItem(marker.getMarker_id(), tMapMarkerItem);
                        tMapView.addMarkerItem("marker", tMapMarkerItem);

                        // markerList에 추가
                        // markerList.add(marker);


                        //지도 활성화 & listView 비활성화
                        linearLayoutTmap.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        list_display = false;

                        // 리스트창을 닫아줌
                        listView.setAdapter(null);
                    }
                });
            }
        });


        //화장실 마커 클릭->정보제공
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {

            @Override
            public boolean onPressEvent(ArrayList markerlist, ArrayList poilist, TMapPoint point, PointF pointf) {
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                for (TMapMarkerItem markerItem : markerlist) {
                    TMapPoint toiletLocation = markerItem.getTMapPoint();

                    if (markerItem.getID().contains("marker_")) {
                        String markerId = markerItem.getID();
                        int toiletId = Integer.parseInt(markerId.substring("marker_".length())); // 마커 ID에서 "marker_"를 제거하여 화장실 ID로 사용

                        for (Toilet toilet : toiletList) {
                            if (toilet.getId() == toiletId) {
                                // 화장실 정보를 찾았을 때의 처리 코드
                                // 예를 들어, Intent를 사용하여 정보를 전달하거나 화면에 표시할 수 있음
                                String toiletAddr = toilet.getDoro();
                                String toiletName = toilet.getName();
                                int toiletMan_so = toilet.getMan_so();
                                int toiletMan_dae = toilet.getMan_dae();
                                int toiletMan2_so = toilet.getMan2_so();
                                int toiletMan2_dae = toilet.getMan2_dae();
                                int toiletWoman = toilet.getWoman();
                                int toiletWoman2 = toilet.getWoman2();
                                double toiletLatitude = toilet.getLatitude();
                                double toiletLongitude = toilet.getLongitude();


                                Intent intent = new Intent(MainActivity.this, MapinfoActivity.class);
                                intent.putExtra("id", toiletId);
                                intent.putExtra("name", toiletName);
                                intent.putExtra("address", toiletAddr);
                                intent.putExtra("man_so", toiletMan_so);
                                intent.putExtra("man_dae", toiletMan_dae);
                                intent.putExtra("man2_so", toiletMan2_so);
                                intent.putExtra("man2_dae", toiletMan2_dae);
                                intent.putExtra("woman", toiletWoman);
                                intent.putExtra("woman2", toiletWoman2);
                                intent.putExtra("latitude", toiletLatitude);
                                intent.putExtra("longitude", toiletLongitude);
                                startActivity(intent);

                                break;
                            }
                        }
                    }
                }
                return false;
            }
        });
        init();
        initLoadDB(); // initLoadDB() 메서드 호출 추가

    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        tMapGpsManager = new TMapGpsManager(this);
        tMapGpsManager.setMinTime(1000);
        tMapGpsManager.setMinDistance(5);
        tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
    }

    private void checkLocation() {
        if (isPermissionGranted()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }

    private boolean isPermissionGranted() {
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ResolvableApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CODE_LOCATION_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    sie.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Toast.makeText(MainActivity.this, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    updateLocation(location);
                }
            }
        };
    }

    private void updateLocation(Location location) {
        mLastLocation = location;
        TMapPoint currentLocation = new TMapPoint(location.getLatitude(), location.getLongitude());
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());

        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
        tMapView.setTrackingMode(true);

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setTMapPoint(currentLocation);

        Bitmap originalMarker = BitmapFactory.decodeResource(getResources(), R.drawable.my_location);
        originalMarker = Bitmap.createScaledBitmap(originalMarker, 100, 100, false);
        markerItem.setIcon(originalMarker);
        tMapView.setZoomLevel(15);//줌인

        tMapView.addMarkerItem("marker", markerItem);

    }

    //마커 크기 조정
    //sql데이터베이스를 통해 화장실 위치 마커로 표시
    private void initLoadDB() {
        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        try {
            mDbHelper.createDatabase();
            mDbHelper.open();

            toiletList = mDbHelper.getTableData();
            // 화장실 정보 리스트

            //  화장실 마커 표시
            for (Toilet toilet : toiletList) {
                TMapMarkerItem markerItem = new TMapMarkerItem();
                TMapPoint toiletLocation = new TMapPoint(toilet.getLatitude(), toilet.getLongitude());
                markerItem.setTMapPoint(toiletLocation);


                Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_tour);

                //마커 크기 조절
                markerBitmap = Bitmap.createScaledBitmap(markerBitmap, 50, 50, false);
                markerItem.setIcon(markerBitmap);
                tMapView.addMarkerItem("marker_" + toilet.getId(), markerItem);

            }

            mDbHelper.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    //보행자 경로 찾는 함수
    public void FindPath_Pedestrian(){
        TMapPoint startpoint = new TMapPoint(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        TMapPoint endpoint = new TMapPoint(markerBitmap.getLatitude(),markerBitmap.getLongitude());


        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tMapView.addTMapPath(tMapPolyLine);
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        checkLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE && !permissionDeniedToastShown) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                permissionDeniedToastShown = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location settings not satisfied, user canceled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
