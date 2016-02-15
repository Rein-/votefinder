package com.google.gwt.votefinder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.votefinder.client.CVotingPlace;

@RemoteServiceRelativePath("votingplace")
public interface VotingPlaceService extends RemoteService {

	void addVotingPlace(CVotingPlace properties);

	void removeVotingPlace(String facilityName);
	
	boolean getVotingPlace(String facilityName);

	ArrayList<CVotingPlace> getVotingPlaces();
	
	void addVotingPlaceArray(ArrayList<CVotingPlace> result);
}
