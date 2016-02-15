package com.google.gwt.votefinder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.votefinder.server.VotingPlace;

@RemoteServiceRelativePath("dataRequest")
public interface DataRequestService extends RemoteService {
	public ArrayList<CVotingPlace> importData();
}