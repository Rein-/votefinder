package com.google.gwt.votefinder.server;
import java.util.ArrayList;

import com.google.gwt.votefinder.client.CVotingPlace;



import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class DataRequestServiceImplTest {
	static DataRequestServiceImpl parserClass;
	static ArrayList<CVotingPlace> votingPlaces;
	static int currentNumberRows;
	
	@BeforeClass
	public static void setUp() throws Exception {
		parserClass = new DataRequestServiceImpl();
		votingPlaces = parserClass.importData();
		currentNumberRows = 123;
	}

	@Test
	public final void testNotNull() {
		assertNotNull(votingPlaces);
	}
	
	@Test
	public final void testVDColumnHasData(){
		boolean containsVD = true;
		String facilityName = "";
		for(CVotingPlace vp:votingPlaces){
			if(vp.getVD().length() <= 0){
				containsVD = false;
				facilityName = vp.getFacilityName();
			}
			if(vp.getVD().length() > 5){
				containsVD = false;
				facilityName = vp.getFacilityName();
			}
		}
		assertTrue(facilityName, containsVD);
	}
	
	
	//This tests whether the facility name column has an actual name in it
	@Test
	public final void testNameColumnHasData(){
		boolean containsName = true;
		for(CVotingPlace vp:votingPlaces){
			if(vp.getFacilityName().length() <= 0){
				containsName = false;
			}
		}
		assertTrue(containsName);
	}
	
	//This tests whether the lat is in the bounds of vancouver
	@Test
	public final void testLatColumnHasData(){
		boolean correctLat = true;
		String facilityName = "";
		for(CVotingPlace vp:votingPlaces){
			if(vp.getLatitude() > 49.3 || vp.getLatitude() < 49.2  ){
				correctLat = false;
				facilityName = vp.getFacilityName();
			}
		}
		assertTrue(facilityName, correctLat);
	}
	
	//This tests whether the long is in the bounds of vancouver
	@Test
	public final void testLongColumnHasData(){
		boolean correctLong = true;
		String facilityName = "";
		for(CVotingPlace vp:votingPlaces){
			if(vp.getLongitude() < -123.3 || vp.getLongitude() > -123.0  ){
				correctLong = false;
				facilityName = vp.getFacilityName();
			}
		}
		assertTrue(facilityName, correctLong);
	}
	
	//This tests whether the location column has an actual location in it
	@Test
	public final void testLocationColumnHasData(){
		boolean containsLocation = true;
		String facilityName = "";
		for(CVotingPlace vp:votingPlaces){
			if(vp.getLocation().length() <= 0){
				containsLocation = false;
				facilityName = vp.getFacilityName();
			}
		}
		assertTrue(facilityName, containsLocation);
	}
	
	//This tests whether the local area column has an actual location in it
	@Test
	public final void testLocalAreaColumnHasData(){
		boolean containsLocalArea = true;
		String facilityName = "";
		for(CVotingPlace vp:votingPlaces){
			if(vp.getLocalArea().length() <= 0){
				containsLocalArea = false;
				facilityName = vp.getFacilityName();
			}
		}
		assertTrue(facilityName, containsLocalArea);
	}
		
	//Test for repeating rows
	@Test
	public final void testRepeatingRows(){
		boolean repeatingRows = false;
		String previousFacilityName = votingPlaces.get(0).getFacilityName();
		for(int i = 1; i < votingPlaces.size(); i++){
			String currentFacilityName = votingPlaces.get(i).getFacilityName();
			if(currentFacilityName.equals(previousFacilityName) ){
				repeatingRows = true;
				break;
			}else{
				previousFacilityName = currentFacilityName;
			}
		}
		assertFalse(previousFacilityName, repeatingRows);
	}
	
	//Test to see if the number of rows in the data set match the number of rows we parse
	@Test
	public final void testNumRows(){
		int rowsParsed = votingPlaces.size();
		assertEquals("Rows Parsed: " + rowsParsed, currentNumberRows, rowsParsed );
	}
	

}
