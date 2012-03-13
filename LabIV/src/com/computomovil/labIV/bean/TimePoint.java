package com.computomovil.labIV.bean;

import com.google.android.maps.GeoPoint;

public class TimePoint {

	private GeoPoint geoPoint;
	private Integer time;
	
	public GeoPoint getGeoPoint() {
		return geoPoint;
	}
	
	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
	
	public Integer getHora() {
		return time;
	}
	
	public void setHora(Integer time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "LatLon: [" + geoPoint.toString() + "], Time: [" + time + "]";
	}
}
