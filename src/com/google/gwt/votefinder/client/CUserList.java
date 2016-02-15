package com.google.gwt.votefinder.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gwt.votefinder.server.VotingPlace;

public class CUserList implements Serializable {

	private String email;
	

	private ArrayList<String> vpList;
	
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

	public void setVotingPlaces(ArrayList<String> vps) {
		this.vpList = vps;
	}

	public void addVotingPlace(String vp) {
		this.vpList.add(vp);
	}
	public void removeVotingPlace(String vp) {
		System.out.println("test");
		int i = 0;
		for (Iterator<String> it = vpList.iterator();it.hasNext();i++){
			String uvp = it.next();
			if (uvp.equals(vp)){
				break;
			}
		}
		vpList.remove(i);
	}
	public boolean isAdmin(){
		return isAdmin;
	}
	public CUserList(){}
	public CUserList(String email, boolean isAdmin) {
		this.email = email;
		this.isAdmin=isAdmin;
	}


	

}
