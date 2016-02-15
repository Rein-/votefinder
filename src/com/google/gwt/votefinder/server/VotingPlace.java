package com.google.gwt.votefinder.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;


@PersistenceCapable (identityType = IdentityType.APPLICATION)
public class VotingPlace{
	
	@Persistent
	private String VD;
	
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@PrimaryKey
	@Persistent
	private String facilityName;
	
	@Persistent
	private String facilityAddress;
	
	@Persistent
	private String location;
	
	@Persistent
	private double latitude;
	
	@Persistent
	private double longitude;
	
	@Persistent
	private String advanceOnly;

	@Persistent
	private String localArea;	
	
	public VotingPlace(String[] properties) {
		this.VD = properties[0].trim();
		this.facilityName = properties[1].trim();
		this.facilityAddress = properties[2].trim();
		this.location = properties[3].trim();
		this.latitude = Double.parseDouble(properties[4].trim());
		this.longitude = Double.parseDouble(properties[5].trim());
		this.advanceOnly = (properties[6].trim().equals("Yes"))?"Yes":"No";
		this.localArea = properties[7].trim();
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
	public String[] toArray() {
		String[] a = {getVD(),getFacilityName(),getFacilityAddress(),getLocation(),getLatitude().toString(),getLongitude().toString(),getAdvanceOnly(),getLocalArea()};
		return a;
	}
	
}