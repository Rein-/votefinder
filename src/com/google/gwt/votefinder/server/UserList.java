package com.google.gwt.votefinder.server;

import java.util.ArrayList;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable (identityType = IdentityType.APPLICATION)
public class UserList {
	
	@PrimaryKey
	@Persistent
	private String email;
	
	@Persistent
	private ArrayList<String> vpList;
	
	@Persistent
	private boolean isAdmin;
	
	
	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public ArrayList<String> getVotingPlaces() {
		return vpList;
	}


	public void setVotingPlaces(ArrayList<String> vp) {
		this.vpList = vp;
	}
	public void addVotingPlace(String vp) {
		vpList.add(vp);
	}
	public void removeVotingPlace(String vp) {
		vpList.remove(vp);
	}
	public boolean isAdmin(){
		return isAdmin;
	}
	
	public UserList(String email,boolean isAdmin) {
		this.email = email;
		vpList = new ArrayList<String>();
		this.isAdmin = isAdmin;
	}

}
