package com.example.citiessweather.Evidence;

public class Evidence {

    private String lat;
    private String lon;
    private String address;
    private String minTem;
    private String maxTem;
    private String mainWeather;
    private String pic;

    public Evidence(String lat, String lon, String address, String minTem, String maxTem, String mainWeather, String pic) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.minTem = minTem;
        this.maxTem = maxTem;
        this.mainWeather = mainWeather;
        this.pic = pic;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
