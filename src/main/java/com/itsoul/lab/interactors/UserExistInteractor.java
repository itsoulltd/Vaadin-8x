package com.itsoul.lab.interactors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.itsoul.lab.domains.ExistanceConsume;
import com.itsoul.lab.domains.ExistanceProduce;

public class UserExistInteractor implements Interactor<ExistanceProduce, ExistanceConsume> {

	private WebTarget target;
	public UserExistInteractor() {
		Client cl = ClientBuilder.newClient();
		target = cl.target(getAuthService()+"users/isExist/");
	}

	@Override
	public ExistanceProduce fetch(ExistanceConsume consume) {
		Response response = target.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(consume, MediaType.APPLICATION_JSON));
		ExistanceProduce prod = response.readEntity(ExistanceProduce.class);
		return prod;
	}

}
