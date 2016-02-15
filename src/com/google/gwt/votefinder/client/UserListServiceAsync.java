package com.google.gwt.votefinder.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserListServiceAsync {
	public void getUserList(String email,AsyncCallback<CUserList> async);
	public void isRegistered(String email, AsyncCallback<Boolean> async);
	public void registerUser(String email, AsyncCallback<Boolean> async);
	public void addToUserList(String email,CVotingPlace cvp, AsyncCallback<Void> async);
	public void removeFromUserList(String email, String uvp, AsyncCallback<Void> async);
}
