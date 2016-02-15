package com.google.gwt.votefinder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userlist")
public interface UserListService extends RemoteService {
	CUserList getUserList(String email);
	boolean isRegistered(String email);
	boolean registerUser(String email);
	void addToUserList(String email,CVotingPlace cvp);
	void removeFromUserList(String email, String uvp);
}
