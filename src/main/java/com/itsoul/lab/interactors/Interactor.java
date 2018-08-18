package com.itsoul.lab.interactors;

@FunctionalInterface
public interface Interactor<P, C> {
	public P fetch(C consume);
	default public String getAuthService() {
		return "http://localhost:8080/Auth-Service/auth/";
	}
	default public String getJerseyExample() {
		return "http://localhost:8080/Jersey-2.0-Example/api/";
	}
}
