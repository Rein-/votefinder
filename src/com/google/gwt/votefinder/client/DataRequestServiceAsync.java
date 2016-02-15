package com.google.gwt.votefinder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.votefinder.server.VotingPlace;

public interface DataRequestServiceAsync {
	public void importData(AsyncCallback<ArrayList<CVotingPlace>> async);
}