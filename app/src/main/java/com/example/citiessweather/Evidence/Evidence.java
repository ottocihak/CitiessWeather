package com.example.citiessweather.Evidence;

public class Evidence {

    private String lat;
    private String lon;
    private String address;
    private String minTem;
    private String maxTem;
    private String mainWeather;
    private String warning;
    private String pic;
    private String record;
    private String city;

    public Evidence(String lat, String lon, String address, String minTem, String maxTem, String mainWeather, String pic, String record, String warning) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.minTem = minTem;
        this.maxTem = maxTem;
        this.mainWeather = mainWeather;
        this.warning = warning;
        this.pic = pic;
        this.record = record;
    }

    public Evidence() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMinTem() {
        return minTem;
    }

    public void setMinTem(String minTem) {
        this.minTem = minTem;
    }

    public String getMaxTem() {
        return maxTem;
    }

    public void setMaxTem(String maxTem) {
        this.maxTem = maxTem;
    }

    public String getMainWeather() {
        return mainWeather;
    }

    public void setMainWeather(String mainWeather) {
        this.mainWeather = mainWeather;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
