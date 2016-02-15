package com.google.gwt.votefinder.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.votefinder.client.CVotingPlace;
import com.google.gwt.votefinder.client.DataRequestService;
public class DataRequestServiceImpl extends RemoteServiceServlet implements
DataRequestService {
	private final URL DATA_URL = new URL("http://gabriel.sezefredo.com.br/vp.csv");
	public DataRequestServiceImpl() throws MalformedURLException {}
	@Override
	public ArrayList<CVotingPlace> importData() {
		String[] temp;
		ArrayList<CVotingPlace> result = new ArrayList<CVotingPlace>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(DATA_URL.openStream()));
			String line;
			line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				temp = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				if (temp.length == 8){
					for (int i=0;i<temp.length;i++){
						temp[i] = temp[i].replaceAll("[^a-zA-Z0-9., -]","");
					}
					result.add(new CVotingPlace(temp));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}