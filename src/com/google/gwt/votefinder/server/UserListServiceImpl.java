package com.google.gwt.votefinder.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.votefinder.client.CUserList;
import com.google.gwt.votefinder.client.CVotingPlace;
import com.google.gwt.votefinder.client.UserListService;

public class UserListServiceImpl extends RemoteServiceServlet implements UserListService{

	private static final Logger LOG = Logger.getLogger(VotingPlaceServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private static final String[] adminList = {"gabrial.drs@gmail.com", "vineet.deoo@gmail.com", "fayyazazam@gmail.com"};
	
	@Override
	public CUserList getUserList(String email) {
		email = email.trim().toLowerCase();
		if (!isRegistered(email)){
			registerUser(email);
		}
		PersistenceManager pm = getPersistenceManager();
		List<String> symbols = new ArrayList<String>();
		try {
			Query q = pm.newQuery(UserList.class, "email == e");
			q.declareParameters("String e");
			List<UserList> users = (List<UserList>) q.execute(email);
			ArrayList<String> cvps = new ArrayList<String>();
			CUserList user = null;
			for (UserList curruser : users) {
				user = new CUserList(curruser.getEmail(),curruser.isAdmin());
				ArrayList<String> vps = curruser.getVotingPlaces();
				for (String vp : vps){
					cvps.add(vp);
				}
				user.setVotingPlaces(cvps);
			}
			return user;
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean isRegistered(String email) {
		email = email.trim().toLowerCase();
		PersistenceManager pm = getPersistenceManager();
		List<String> symbols = new ArrayList<String>();
		try {
			Query q = pm.newQuery(UserList.class, "email == e");
			q.declareParameters("String e");
			List<UserList> users = (List<UserList>) q.execute(email);
			for (UserList user : users) {
				return true;
			}
		} finally {
			pm.close();
		}
		return false;
	}

	@Override
	public boolean registerUser(String email) {
		email = email.trim().toLowerCase();
		LOG.log(Level.WARNING, "new user registered");
		PersistenceManager pm = getPersistenceManager();
		try {
			boolean ia = false;
			for (String adm : adminList) if (email.equals(adm))ia = true;
			pm.makePersistent(new UserList(email,ia));
		} finally {
			pm.close();
		}
		return false;
	}

	private PersistenceManager getPersistenceManager() {
		// TODO Auto-generated method stub
		return PMF.getPersistenceManager();
	}

	@Override
	public void addToUserList(String email, CVotingPlace cvp) {
		email = email.trim().toLowerCase();
		PersistenceManager pm = getPersistenceManager();
		List<String> symbols = new ArrayList<String>();
		try {
			Query q = pm.newQuery(UserList.class, "email == e");
			q.declareParameters("String e");
			List<UserList> users = (List<UserList>) q.execute(email);
			for (UserList user : users) {
				user.addVotingPlace(cvp.getFacilityName());
				pm.makePersistent(user);
			}
		} finally {
			pm.close();
		}
	}

	@Override
	public void removeFromUserList(String email, String uvp) {
		email = email.trim().toLowerCase();
		PersistenceManager pm = getPersistenceManager();
		List<String> symbols = new ArrayList<String>();
		try {
			Query q = pm.newQuery(UserList.class, "email == e");
			q.declareParameters("String e");
			List<UserList> users = (List<UserList>) q.execute(email);
			for (UserList user : users) {
				user.removeVotingPlace(uvp);
				pm.makePersistent(user);
			}
		} finally {
			pm.close();
		}
	}
}
