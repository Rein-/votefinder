package com.google.gwt.votefinder.client;

import java.io.Serializable;

import org.apache.tools.ant.filters.TokenFilter.Trim;
public class CVotingPlace implements Serializable{
	private String VD;
	private String facilityName;
	private String facilityAddress;
	private String location;
	private double latitude;
	private double longitude;
	private String advanceOnly;
	private String localArea;
	public CVotingPlace(){}
	public CVotingPlace(String[] properties) {
		this.VD = properties[0];
		this.facilityName = properties[1];
		this.facilityAddress = properties[2];
		this.location = properties[3];
		this.latitude = Double.parseDouble(properties[4]);
		this.longitude = Double.parseDouble(properties[5]);
		this.advanceOnly = (properties[6].equals("Yes"))?"Yes":"No";
		this.localArea = properties[7];
	}
	public String getVD() {
		return VD;
	}
	public void setVD(String vD) {
		VD = vD;
	}
	public String getFacilityName() {
		return facilityName;
	}
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	public String getFacilityAddress() {
		return facilityAddress;
	}
	public void setFacilityAddress(String facilityAddress) {
		this.facilityAddress = facilityAddress;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getAdvanceOnly() {
		return advanceOnly;
	}
	public void setAdvanceOnly(String advanceOnly) {
		this.advanceOnly = advanceOnly;
	}
	public String getLocalArea() {
		return localArea;
	}
	public void setLocalArea(String localArea) {
		this.localArea = localArea;
	}
	public String buildInfoWindowContent(){
		String result = "";
		result += "<div id='"+getFacilityName().trim().toLowerCase()+"' class='infoWindow'>";
		result += "<h3>"+ this.getFacilityName()+"</h3>";
		result += "<p>Address:"+this.getFacilityAddress()+"</p>";
		result += "<p>Local Area:"+this.getLocalArea()+"</p>";
		result += "<p>Location:"+this.getLocation()+"</p>";
		result += "</div>";
		return result;
	}
	public String[] toArray() {
		String[] a = {getVD(),getFacilityName(),getFacilityAddress(),getLocation(),getLatitude().toString(),getLongitude().toString(),getAdvanceOnly(),getLocalArea()};
		return a;
	}
}