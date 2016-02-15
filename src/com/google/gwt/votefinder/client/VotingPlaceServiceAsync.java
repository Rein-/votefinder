package com.google.gwt.votefinder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.votefinder.client.CVotingPlace;


public interface VotingPlaceServiceAsync {
	public void addVotingPlace(CVotingPlace result, AsyncCallback<Void> async);
	public void removeVotingPlace(String facilityName, AsyncCallback<Void> async);
	public void getVotingPlace(String facilityName, AsyncCallback<Boolean> async);
	public void getVotingPlaces(AsyncCallback<ArrayList<CVotingPlace>> async);
	public void addVotingPlaceArray(ArrayList<CVotingPlace> result, AsyncCallback<Void> asyncCallback);
}
