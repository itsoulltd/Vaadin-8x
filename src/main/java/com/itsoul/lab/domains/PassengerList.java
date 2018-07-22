package com.itsoul.lab.domains;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.it.soul.lab.sql.query.models.DataType;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class PassengerList implements ClickListener{
	
	private static final long serialVersionUID = 1L;

	public PassengerList() {
		super();
	}

	private List<Passenger> passengerList = new ArrayList<Passenger>();
	 
    public List<Passenger> getPassengerList() {
        return passengerList;
    }
 
    public void setPassengerList(List<Passenger> passengerList) {
    	if (passengerList == null) {return;}
        this.passengerList = passengerList;
    }
    
    public PassengerList add(Passenger pass) {
    	if (pass == null || passengerList.contains(pass) == true) {return this;}
    	passengerList.add(pass);
    	return this;
    }

	@Override
	public void buttonClick(ClickEvent event) {
		//TODO:
		Client cl = ClientBuilder.newClient();
		WebTarget target = cl.target("http://localhost:8080/Jersey-2.0-Example/api/passenger/JPA");
		
		FetchQuery query = new FetchQuery();
		query.setTable("Passenger");
		query.setOrderBy("id");
		query.setLocation(0);
		query.setSize(5);
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(new Criteria("name","Sohana",DataType.STRING));
		query.setCriterias(criterias);
		
		Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(query, MediaType.APPLICATION_JSON));
		System.out.print(response.toString());
		PassengerList list = response.readEntity(getClass()); 
		setPassengerList(list.getPassengerList());
		for (Passenger pass : list.getPassengerList()) {
			System.out.println(pass.getName());
		}
	}
}
