package com.google.gwt.votefinder.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.votefinder.client.VotingPlaceService;
import com.google.gwt.votefinder.client.CVotingPlace;

public class VotingPlaceServiceImpl extends RemoteServiceServlet implements VotingPlaceService{

	 private static final Logger LOG = Logger.getLogger(VotingPlaceServiceImpl.class.getName());
	 private static final PersistenceManagerFactory PMF =
	      JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	@Override
	public void addVotingPlace(CVotingPlace properties) {
	    PersistenceManager pm = getPersistenceManager();
	    try {
	      VotingPlace vp = new VotingPlace(properties.toArray());
	      pm.makePersistent(vp);
	    } finally {
	      pm.close();
	    }
		
	}

	@Override
	public void removeVotingPlace(String facilityName) {
		PersistenceManager pm = getPersistenceManager();
	    try {
	      Query q = pm.newQuery(VotingPlace.class);
	      List<VotingPlace> vpArray = (List<VotingPlace>) q.execute();
	      for (VotingPlace vp : vpArray){
	    	  if (vp.getFacilityName().equals(facilityName)){
	    		  pm.deletePersistent(vp);
	    	  }
	      }
	    } finally {
	      pm.close();
	    }
	}

	@Override
	public ArrayList<CVotingPlace> getVotingPlaces() {
		PersistenceManager pm = getPersistenceManager();
	    ArrayList<CVotingPlace> votingPlacesArray = new ArrayList<CVotingPlace>();
	    try {
	      Query q = pm.newQuery(VotingPlace.class);
	      List<VotingPlace> vpArray = (List<VotingPlace>) q.execute();
	      for (VotingPlace vp : vpArray){
	    	  votingPlacesArray.add(new CVotingPlace(vp.toArray()));
	      }
	    } finally {
	      pm.close();
	    }
	    return votingPlacesArray;
	}

	@Override
	public boolean getVotingPlace(String facilityName) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	@Override
	public void addVotingPlaceArray(ArrayList<CVotingPlace> result) {
		for (CVotingPlace cvp : result){
			addVotingPlace(cvp);
		}
	}
	

}
