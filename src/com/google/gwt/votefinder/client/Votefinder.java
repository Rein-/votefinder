package com.google.gwt.votefinder.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.Position.Coordinates;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler.MarkerClickEvent;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geocode.Waypoint;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.votefinder.client.CVotingPlace;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Votefinder implements EntryPoint {

	private LoginInfo loginInfo = null;
	private FlexTable voteFlexTable = new FlexTable();
	private FlexTable nearVoteTable = new FlexTable();
	private HorizontalPanel searchPanel = new HorizontalPanel();
	private HorizontalPanel mainPanel = new HorizontalPanel();
	private VerticalPanel loginPanel = new VerticalPanel();
	private VerticalPanel tablePanel = new VerticalPanel();
	private VerticalPanel nearTablePanel = new VerticalPanel();
	private Button searchButton = new Button("Search");
	private TextBox searchTextBox = new TextBox();
	private Label loginLabel = new Label("Please log in using a Google Account to access voteFinder.");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private ArrayList<Marker> markerList = new ArrayList<Marker>();
	private final VotingPlaceServiceAsync votingPlaceService = GWT.create(VotingPlaceService.class);
	UserListServiceAsync userListService = GWT.create(UserListService.class);
	private static ArrayList<CVotingPlace> votingPlaces = new ArrayList<CVotingPlace>();
	private ArrayList<CVotingPlace> searchList = new ArrayList<CVotingPlace>();
	private static CUserList user;
	private static Geolocation currentLocation;
	private static LatLng origin = null;
	private static Marker personMarker;
	private static Polyline route = null;
	private static final Logger log = Logger.getLogger(Votefinder.class.getName());

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
			}
			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				buildUI(loginInfo);
			}
		});
	}
	

	private void buildUI(LoginInfo li) {
		if(loginInfo.isLoggedIn()) {
			login(loginInfo.getEmailAddress());
		} else {
			loadLoginView();
		}
	}

	private void login(String emailAddress) {
		userListService.getUserList(emailAddress, new AsyncCallback<CUserList>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Check user registered Failed: " + caught);
			}

			@Override
			public void onSuccess(CUserList result) {
				user = result;
				loadUserView();
			}
		});
	}

	private void loadLoginView(){
		Maps.loadMapsApi("", "2", false, new Runnable() {
			public void run() {
				final MapWidget map = new MapWidget();
				buildmapUI(map);
				// Assemble login panel.
				signInLink.setHref(loginInfo.getLoginUrl());
				loginPanel.add(signInLink);
				RootPanel.get("login").add(loginPanel);
			}
		});

	}

	private void loadUserView(){
		//build the google map
		Maps.loadMapsApi("", "2", false, new Runnable() {
			public void run() {
				final MapWidget map = new MapWidget();
				buildmapUI(map);
				getVotingPlaces(map);
				loadMenu(map);
				loadSearchResultsTable(map);

			}
		});
	}
	
	private void buildmapUI(MapWidget map) {	
		map.setSize("100%", "100%");
		// Open a map centered on Vancouver, BC
		final LatLng Vancouver = LatLng.newInstance(49.2500, -123.150);		
		map.setCenter(Vancouver);
		map.setZoomLevel(12);

		// Add some controls for the zoom level
		map.addControl(new SmallMapControl());

		final DockLayoutPanel dock = new DockLayoutPanel(Unit.PCT);
		dock.addNorth(map, 100);

		// Add the map to the HTML host page
		RootPanel.get("map").add(dock);
	}
	
	private void loadMenu(final MapWidget map) {
		Button loadDataButton = new Button("Load Data");
		loadDataButton.addStyleDependentName("load");
		loadDataButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				loadDataToServer(map);
			}
		});
		if (user.isAdmin()){
			mainPanel.add(loadDataButton);
		}else {
			Label l1 = new Label(user.getEmail());
			mainPanel.add(l1);
		}
		// Set up sign out hyperlink.
		signOutLink.setHref(loginInfo.getLogoutUrl());
		mainPanel.add(signOutLink);


		RootPanel.get("admin").add(mainPanel);

	}

	private void removeMapOverlays(MapWidget map){
		map.clearOverlays();
	}

	private void addMapOverlays(final MapWidget map, ArrayList<CVotingPlace> vp){
		for(int i = 0;i<vp.size();i++){
			final CVotingPlace cvp = vp.get(i);
			final LatLng latlng = LatLng.newInstance(cvp.getLatitude(),cvp.getLongitude());
			// Create our "tiny" marker icon
			MarkerOptions mo = MarkerOptions.newInstance();
			mo.setTitle(cvp.getFacilityName());

			if(user.getVotingPlaces().contains(cvp.getFacilityName())){
				mo.setIcon(getMapIcon("user"));
			}else{
				mo.setIcon(getMapIcon("voting"));
			}
			Marker m = new Marker(latlng,mo);
			addMarkerClickHandler(map,m,cvp, latlng);
			map.addOverlay(m);
			markerList.add(m);
		}
	}

	private void addMarkerClickHandler(final MapWidget map, final Marker m,final CVotingPlace vp, final LatLng latlng) {
		m.addMarkerClickHandler(new MarkerClickHandler(){
			@Override
			public void onClick(MarkerClickEvent event) {
				try{
					map.getInfoWindow().close();
				}finally{
					InfoWindowContent info = new InfoWindowContent(buildHTMLPanel(vp,m, map));
					info.setNoCloseOnClick(true);
					map.getInfoWindow().open(latlng,info);
					double pushView = latlng.getLongitude() - 0.04;
					LatLng newCenter = LatLng.newInstance(latlng.getLatitude(), pushView);
					map.setCenter(newCenter);
				}
			}
		});
	}

	private Widget buildHTMLPanel(final CVotingPlace vp, final Marker m, final MapWidget map) {
		HTMLPanel html = new HTMLPanel(vp.buildInfoWindowContent());
		Button add = new Button("Add");
		Button routeCar = new Button("Drive");
		Button routeWalk = new Button("Walk");
		add.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addToUserList(user.getEmail(),getVotingPlaceByTitle(m.getTitle()), map);
			}


		});

		routeCar.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getCurrentLocationForRoute(vp, map, "driving");
			}
		});
		
		routeWalk.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getCurrentLocationForRoute(vp, map, "walking");
			}


		});
		html.add(add, vp.getFacilityName().trim().toLowerCase());
		html.add(routeCar, vp.getFacilityName().trim().toLowerCase());
		html.add(routeWalk, vp.getFacilityName().trim().toLowerCase());
		return html.asWidget();
	}

	private void loadInitialTables(final MapWidget map) {
		setUserListHeader();
		voteFlexTable.setCellPadding(5);
		voteFlexTable.setStylePrimaryName("userListTable");


		//panels for search
		//adds a textbox, and a button for the search
		searchPanel.add(searchTextBox);
		searchPanel.add(searchButton);
		searchPanel.addStyleName("searchPanel");

		//assemble the panel for the table
		tablePanel.add(voteFlexTable);

		//Associate the Main panel with the HTML.
		RootPanel.get("searchBar").add(searchPanel);
		RootPanel.get("userList").add(tablePanel);

		displayUserList(getVotingPlacesByTitle(user.getVotingPlaces()), map);
		//move cursor focus to the search box
		searchTextBox.setFocus(true);

		//dealing with the mouse and keyboard functions on our search box
		//Listen for mouse events on search button
		searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String address = searchTextBox.getText().trim();
				searchTextBox.setFocus(true);
				address = address.replaceAll(" ", "+");

				getSearchLatLng(address, map);
			}
		});

		// Listen for keyboard events in input box
		searchTextBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					String address = searchTextBox.getText().trim();
					searchTextBox.setFocus(true);
					address = address.replaceAll(" ", "+");

					getSearchLatLng(address, map);
				}
			}
		});
	}
	
	private void setSearchHeader() {
		nearVoteTable.setText(0, 0, "Nearest Facility");
		nearVoteTable.setText(0, 1, "Address");
		nearVoteTable.setText(0, 2, "Location");
		nearVoteTable.setText(0, 3, "Local Area");
		nearVoteTable.setText(0, 4, "Find");
		
		//style in css 
		nearVoteTable.getRowFormatter().setStyleName(0, "userListHeader");
		nearVoteTable.getCellFormatter().addStyleName(0, 0, "longNameColumn");
		nearVoteTable.getCellFormatter().addStyleName(0, 1, "longNameColumn");
		nearVoteTable.getCellFormatter().addStyleName(0, 2, "longNameColumn");
		nearVoteTable.getCellFormatter().addStyleName(0, 3, "longNameColumn");
		nearVoteTable.getCellFormatter().addStyleName(0, 4, "longNameColumn");
		nearVoteTable.getCellFormatter().addStyleName(0, 4, "findColumn");
	}

	public void searchPlaces(LatLng point, final MapWidget map) {
		searchList.clear();
		Map<Double, CVotingPlace> VPandDistance = new TreeMap<Double, CVotingPlace>();

		//fill up the treemap so we have a relation between our latitude and longitude
		//and the distances (ordered) to each voting place
		for(int i =0; i < votingPlaces.size(); i++) {
			double distance = calculateDistance(point.getLatitude(), point.getLongitude(), 
					votingPlaces.get(i).getLatitude(), votingPlaces.get(i).getLongitude());
			VPandDistance.put(distance, votingPlaces.get(i));
		}
		//make a collection of the values which are essentially
		//the voting places but in order from closest to farthest
		Collection<CVotingPlace> c = VPandDistance.values();
		Iterator<CVotingPlace> itc = c.iterator();

		int count =0;
		while(count != 3 && itc.hasNext()) {
			searchList.add(itc.next());
			count++;
		}
		
		nearVoteTable.removeAllRows();
		setSearchHeader();
		nearVoteTable.setCellPadding(5);
		nearVoteTable.setStylePrimaryName("nearListTable"); 

		nearTablePanel.add(nearVoteTable);
		RootPanel.get("nearList").add(nearTablePanel);
		
		listSearchResults(searchList, map);		

		loadSearchResultsTable(map);
	}
	
	//Table for the closest search results
	private void loadSearchResultsTable(final MapWidget map) {
		nearVoteTable.removeAllRows();
		setSearchHeader();
		nearVoteTable.setWidth("100%");
		nearVoteTable.setStylePrimaryName("nearListTable"); 

		nearTablePanel.add(nearVoteTable);
		RootPanel.get("nearList").add(nearTablePanel);
		
		listSearchResults(searchList,map);
	}


	private void listSearchResults(ArrayList<CVotingPlace> searchL, final MapWidget map) {
		setSearchHeader();
		for(final CVotingPlace vp: searchL) {
			if (vp != null){
				int currentRow = nearVoteTable.getRowCount();
				//get all the values for our table
				nearVoteTable.setText(currentRow, 0, vp.getFacilityName());
				nearVoteTable.setText(currentRow, 1, vp.getFacilityAddress());
				nearVoteTable.setText(currentRow, 2, vp.getLocation());
				nearVoteTable.setText(currentRow, 3, vp.getLocalArea());
				
				nearVoteTable.getCellFormatter().addStyleName(currentRow, 0, "longNameColumn");
				
				Button viewOnMapButton = new Button("O");
				viewOnMapButton.addStyleDependentName("view");
				viewOnMapButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
					for(Marker mark:markerList){
							if(mark.getTitle().equals(vp.getFacilityName())){
								map.setCenter(mark.getLatLng(), 14);
							}
						}
					}
					});
				nearVoteTable.setWidget(currentRow, 4, viewOnMapButton);		
				DOM.getElementById("nearList").getStyle().setDisplay(Display.BLOCK);
			}
		}
	}



	private void setUserListHeader() {
		//create the table/headers for the data
		voteFlexTable.setText(0,0, "Facility Name");
		voteFlexTable.setText(0,1, "Remove");
		voteFlexTable.setText(0,2, "Find");

		//style in css for header row
		voteFlexTable.getRowFormatter().addStyleName(0, "userListHeader");
		voteFlexTable.getCellFormatter().addStyleName(0, 0, "longNameColumn");
		voteFlexTable.getCellFormatter().addStyleName(0, 1, "removeColumn");
		voteFlexTable.getCellFormatter().addStyleName(0, 1, "findColumn");

	}

	private void displayUserList(ArrayList<CVotingPlace> cvp, MapWidget map){
		voteFlexTable.removeAllRows();
		setUserListHeader();
		for (CVotingPlace vp : cvp){
			displayUserListItem(vp, map);
		}
	}

	private void displayUserListItem(final CVotingPlace place, final MapWidget map) {
		//Add the voting place to the table
		//get the current row to which we are adding data
		if (place != null){
			int currentRow = voteFlexTable.getRowCount();

			//get all the values for our table
			voteFlexTable.setText(currentRow, 0, place.getFacilityName());
			//style for css
			voteFlexTable.getCellFormatter().addStyleName(currentRow, 0, "longNameColumn");
			voteFlexTable.getCellFormatter().addStyleName(currentRow, 1, "removeColumn");
			voteFlexTable.getCellFormatter().addStyleName(currentRow, 1, "findColumn");


			//  Add a button to remove the place from the table.
			Button removePlaceButton = new Button("X");
			removePlaceButton.addStyleDependentName("remove");
			removePlaceButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Marker markToRemove = null;
					for(Marker mark:markerList){
						if(mark.getTitle().equals(place.getFacilityName())){
							map.removeOverlay(mark);
							log.log(Level.SEVERE, "removedoverlay?");
							Marker newMarker = addNewVotingPlaceMarker(mark, place);
							addMarkerClickHandler(map, newMarker, place, newMarker.getLatLng());
							map.addOverlay(newMarker);
							markToRemove = mark;
							break;
						}
					}
					removeRelationFromDatabase(user.getEmail(), place.getFacilityName());
					removeFromTable(place.getFacilityName());

					if(markToRemove != null)
						markerList.remove(markToRemove);
				}
			});
			voteFlexTable.setWidget(currentRow, 1, removePlaceButton);
			Button viewOnMapButton = new Button("O");
			viewOnMapButton.addStyleDependentName("view");
			viewOnMapButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					for(Marker mark:markerList){
						if(mark.getTitle().equals(place.getFacilityName())){
							map.setCenter(mark.getLatLng(), 14);
						}
					}
				}
			});
			voteFlexTable.setWidget(currentRow, 2, viewOnMapButton);
		}
	}



	private void addToUserList(String email, final CVotingPlace cvp, final MapWidget map) {
		if (!user.getVotingPlaces().contains(cvp.getFacilityName())){
			log.log(Level.SEVERE , "ADDDING");
			user.addVotingPlace(cvp.getFacilityName());
			userListService.addToUserList(email, cvp, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					Marker markToRemove = null;
					for(Marker mark:markerList){
						if(mark.getTitle().equals(cvp.getFacilityName())){
							map.removeOverlay(mark);
							Marker newMarker = addNewUserPlaceMarker(mark,cvp);
							addMarkerClickHandler(map, newMarker, cvp, newMarker.getLatLng());
							map.addOverlay(newMarker);

							markToRemove = mark;
							break;
						}
					}
					displayUserListItem(getVotingPlaceByTitle(cvp.getFacilityName()), map);
					if(markToRemove != null)
						markerList.remove(markToRemove);
				}

				@Override
				public void onFailure(Throwable caught) {
					System.out.println("Could not add user to list " + caught);
				}
			});
		}
	}
	private void removeFromUserList(String email, String uvp) {
		removeRelationFromDatabase(email,uvp);
		removeFromTable(uvp);
	}

	private void removeRelationFromDatabase(String email, final String uvp) {
		userListService.removeFromUserList(email, uvp, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				user.removeVotingPlace(uvp);
			}

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Could not remove the relation between userlist and voting place " + caught);
			}
		});
	}

	private void removeFromTable(String uvp) {
		for(int i=0;i<voteFlexTable.getRowCount();i++){
			for (int j=0;j<voteFlexTable.getCellCount(i);j++)
				if (voteFlexTable.getHTML(i, j).equals(uvp)){
					voteFlexTable.removeRow(i);
					break;
				}
		}
	}

	private ArrayList<CVotingPlace> getVotingPlacesByTitle(ArrayList<String> vps){
		ArrayList<CVotingPlace> r = new ArrayList<CVotingPlace>();
		for (CVotingPlace cvp : votingPlaces){
			if (vps.contains(cvp.getFacilityName())){
				r.add(cvp);
			}
		}
		return r;
	}

	private CVotingPlace getVotingPlaceByTitle(String title) {
		for(CVotingPlace vp:votingPlaces){
			if(vp.getFacilityName().equals(title)){
				return vp;
			}
		}
		return null;
	}

	//Loads data from excel file to our server
	private void loadDataToServer(final MapWidget map){
		final Label displayText = new Label();
		DataRequestServiceAsync dataService = GWT.create(DataRequestService.class);		
		dataService.importData(new AsyncCallback<ArrayList<CVotingPlace>>() {

			@Override
			public void onFailure(Throwable caught) {
				displayText.setText(caught.getMessage());
				RootPanel.get().add(displayText);
			}

			@Override
			public void onSuccess(ArrayList<CVotingPlace> result) {
				if(votingPlaces.size() == 0 && result != null){
					votingPlaces = result;
					addVotingPlaceArray(result,map);
				}else if(result != null){
					removeOutdatedVotingPlaces(result);
					addNewVotingPlaces(result);
					loadVotingPlaces(map,result);
				}

			}


		});
	}
	//Gets data from our database and display it on map
	private void getVotingPlaces(final MapWidget map){
		votingPlaceService.getVotingPlaces(new AsyncCallback<ArrayList<CVotingPlace>>() {
			public void onFailure(Throwable error) {
				System.out.println("Get voting places failed: " + error);
			}
			public void onSuccess(ArrayList<CVotingPlace> result) {
				votingPlaces = result;
				loadVotingPlaces(map, votingPlaces);
			}
		});
	}

	private void loadVotingPlaces(MapWidget map, ArrayList<CVotingPlace> vp){
		loadInitialTables(map);
		cleanUserList();
		removeMapOverlays(map);
		addMapOverlays(map,vp);
	}

	private void cleanUserList() {
		ArrayList<String> userVotingPlaces = user.getVotingPlaces();
		for (Iterator<String> it = userVotingPlaces.iterator();it.hasNext();){
			String uvp = it.next();
			if (getVotingPlaceByTitle(uvp) == null){
				removeFromUserList(user.getEmail(),uvp);
			}
		}
	}

	//Adds a voting place to our database
	private void addVotingPlace(CVotingPlace result) {
		votingPlaceService.addVotingPlace(result, new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				System.out.println("Failure to add voting place to user list " + error);
			}
			public void onSuccess(Void ignore) {
			}
		});

	}
	private void addVotingPlaceArray(final ArrayList<CVotingPlace> result, final MapWidget map) {
		votingPlaceService.addVotingPlaceArray(result, new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				System.out.println("Couldn't add voting place to voting place array" + error);
			}
			public void onSuccess(Void ignore) {
				loadVotingPlaces(map,result);
			}
		});

	}
	//Removes a voting place from the database
	private void removeVotingPlace(final String votingPlaceFacilityName){
		votingPlaceService.removeVotingPlace(votingPlaceFacilityName, new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				System.out.println("Couldn't remove voting place from user list" + error);
			}
			public void onSuccess(Void ignore) {
				votingPlaces.remove(getVotingPlaceByTitle(votingPlaceFacilityName));
				removeFromUserList(user.getEmail(),votingPlaceFacilityName);
			}
		});
	}
	private void updateVotingPlace(final CVotingPlace vp) {
		votingPlaceService.removeVotingPlace(vp.getFacilityName(), new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				System.out.println("Failure to remove voting place" + error);
			}
			public void onSuccess(Void ignore) {
				addVotingPlace(vp);
			}
		});
	}


	private void removeOutdatedVotingPlaces(ArrayList<CVotingPlace> votingPlacesArray){
		for(CVotingPlace vp:votingPlaces){
			boolean isNameMatch = false;
			boolean isDetailMatch = false;
			for(CVotingPlace votingPlace:votingPlacesArray){
				if(vp.getFacilityName().equals(votingPlace.getFacilityName()))
				{	
					isNameMatch = true;
					if(vp.getLatitude().equals(votingPlace.getLatitude()) 
							&& vp.getLongitude().equals(votingPlace.getLongitude())){
						//Check for more details here
						isDetailMatch = true;
						break;
					}else{
						break;
					}
				}
			}
			if(!isNameMatch){
				//Remove from VotingPlace Table
				removeVotingPlace(vp.getFacilityName());
			}else if(!isDetailMatch){
				//remove initial record from VotingPlaces table and add new one
				updateVotingPlace(vp);				
			}

		}
	}



	private void addNewVotingPlaces(ArrayList<CVotingPlace> votingPlacesArray){
		for(CVotingPlace votingPlace:votingPlacesArray){
			boolean isMatch = false;
			for(CVotingPlace vp:votingPlaces){
				if(vp.getFacilityName().equals(votingPlace.getFacilityName())){
					isMatch = true;
				}
			}
			if(!isMatch){
				addVotingPlace(votingPlace);
			}
		}
	}
	
	private void getSearchLatLng(String address , final MapWidget map) {
		Geocoder gc = new Geocoder();
		gc.getLatLng(address, new LatLngCallback() {

			@Override
			public void onFailure() {
				System.out.println("Couldn't get coordinates from given address");
			}

			@Override
			public void onSuccess(LatLng point) {
				map.setCenter(point, 14);
				searchPlaces(point, map);
			}
		});
	}



	//calculating the distances given two pairs of latitude and longitude
	private double calculateDistance(double lat, double lon, double lat2, double lon2) {
		double earthRadius = 6373; //in km
		double dLat = Math.toRadians(lat2-lat);
		double dLon = Math.toRadians(lon2-lon);
		double sinLat = Math.sin(dLat/2);
		double sinLon = Math.sin(dLon/2);
		double a = Math.pow(sinLat, 2) + Math.pow(sinLon, 2)
				* Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		return dist;

	}

	private void getCurrentLocationForRoute(final CVotingPlace vp, final MapWidget map, final String routeType){
		currentLocation = Geolocation.getIfSupported();
		currentLocation.getCurrentPosition(new Callback<Position, PositionError>(){		      
			public void onFailure(PositionError error) {
				int errorCode = error.getCode();
				if(errorCode == 1){
					GWT.log("Permission Denied");
				}else if(errorCode == 2){
					GWT.log("Position Unavailable");
				}else if(errorCode == 3){
					GWT.log("Timeout");
				}else if(errorCode == 0){
					GWT.log("Unknown Error");
				}	  
			}
			public void onSuccess(Position position) {
				if(origin != null){
					map.removeOverlay(personMarker);
				}
				Coordinates coord = position.getCoordinates();
				origin = LatLng.newInstance(coord.getLatitude(), coord.getLongitude());
				getRoute(vp,map);
				getRoutePolyline(vp.getLatitude(),vp.getLongitude(), map, routeType);
			}
		});
	}
	
	private void getRoute(final CVotingPlace vp, final MapWidget map){
		MarkerOptions mo = MarkerOptions.newInstance();
		mo.setIcon(getMapIcon("person"));
		personMarker = new Marker(origin, mo);
		map.addOverlay(personMarker);
		if(route!= null){
			map.removeOverlay(route);
			route = null;
		}
	}

	private void getRoutePolyline(Double vpLatitude, Double vpLongitude, final MapWidget map, String routeType){
		Waypoint[] waypoints = new Waypoint[2];
		waypoints[0] = new Waypoint(origin);
		LatLng destination = LatLng.newInstance(vpLatitude, vpLongitude);
		waypoints[1] = new Waypoint(destination);
		map.setCenter(destination, 13);
		DirectionQueryOptions dirOptions = new DirectionQueryOptions();
		if(routeType == "walking"){
			dirOptions.setTravelMode(DirectionQueryOptions.TravelMode.WALKING);
		}
		Directions.loadFromWaypoints(waypoints, dirOptions, new DirectionsCallback(){
			public void onFailure(int statusCode){
			}
			public void onSuccess(DirectionResults results){
				route = results.getPolyline();
				map.addOverlay(route);  
			}
		});
	}


	private Icon getMapIcon(String type){
		Icon icon;
		if(type.equals("user")){
			icon = Icon.newInstance(
					"img/pin-blue-solid-1.png");
		}else if(type.equals("person")){
			icon = Icon.newInstance("img/pegman.png");
		}
		else{
			icon = Icon.newInstance("img/pin-red-solid-1.png"); 
		}
		icon.setIconSize(Size.newInstance(26, 26));
		icon.setIconAnchor(Point.newInstance(8, 36));
		//icon.setInfoWindowAnchor(Point.newInstance(11, 2));
		return icon;
	}

	private Marker addNewVotingPlaceMarker(Marker oldMarker, CVotingPlace cvp){
		MarkerOptions mo = MarkerOptions.newInstance();
		mo.setTitle(cvp.getFacilityName());
		mo.setIcon(getMapIcon("voting"));
		Marker newMarker = new Marker(oldMarker.getLatLng(), mo);
		markerList.add(newMarker);
		log.log(Level.SEVERE, "addnewvotingplacemarker??");
		return newMarker;
	}

	private Marker addNewUserPlaceMarker(Marker oldMarker, CVotingPlace cvp){
		MarkerOptions mo = MarkerOptions.newInstance();
		mo.setTitle(cvp.getFacilityName());
		mo.setIcon(getMapIcon("user"));
		Marker newMarker = new Marker(oldMarker.getLatLng(), mo);
		markerList.add(newMarker);
		return newMarker;
	}

}