package com.map202306.test;

public class Toilet {
    //id,name,doro,old-doro,man_dae,man_so,man2_dae,man2_so,woman,woman2,latitude,longitude
    public int id; //id
    public String name; //화장실 이름
    public String doro; //도로명
/*
    public String old_doro; //지번
*/
    public int man_dae; //남-대변기
    public int man_so; //남-소변기
    public int man2_dae; //남-대변기(장애인)
    public int man2_so; //남-소변기(장애인)
    public int woman; //여-대변기
    public int woman2; //여-대변기(장애인)
    public double latitude; //위도
    public double longitude; //경도

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDoro(String doro) {
        this.doro = doro;
    }

    public String getDoro() {
        return doro;
    }

    /*public void setOld_doro(String old_doro) {
        this.old_doro = old_doro;
    }

    public String getOld_doro() {
        return old_doro;
    }
*/
    public void setMan_dae(int man_dae) {
        this.man_dae = man_dae;
    }

    public int getMan_dae() {
        return man_dae;
    }

    public void setMan_so(int man_so) {
        this.man_so = man_so;
    }

    public int getMan_so() {
        return man_so;
    }

    public void setMan2_dae(int man2_dae) {
        this.man2_dae = man2_dae;
    }

    public int getMan2_dae() {
        return man2_dae;
    }

    public void setMan2_so(int man2_so) {
        this.man2_so = man2_so;
    }

    public int getMan2_so() {
        return man2_so;
    }

    public void setWoman(int woman) {
        this.woman = woman;
    }

    public int getWoman() {
        return woman;
    }

    public void setWoman2(int woman2) {
        this.woman2 = woman2;
    }

    public int getWoman2() {
        return woman2;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }
    // TODO : get,set 함수 생략

}
