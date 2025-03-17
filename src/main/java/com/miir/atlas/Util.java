package com.miir.atlas;

public class Util {
    public static int lon2tile(long lon,int zoom) { return (int) Math.floor((lon+180)/360*Math.pow(2,zoom)); }
    public static int  lat2tile(long lat,int zoom)  { return (int) Math.floor((1-Math.log(Math.tan(lat*Math.PI/180) + 1/Math.cos(lat*Math.PI/180))/Math.PI)/2 *Math.pow(2,zoom)); }
}
