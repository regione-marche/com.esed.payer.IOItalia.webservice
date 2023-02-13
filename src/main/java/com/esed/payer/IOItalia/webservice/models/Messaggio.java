package com.esed.payer.IOItalia.webservice.models;

public class Messaggio {

	private Contenuto contenuto;
	private String email;
	private String esito_messaggio;
	
	public Messaggio() {
		
	}
	
	public Contenuto getContenuto() {
		return contenuto;
	}

	public void setContenuto(Contenuto contenuto) {
		this.contenuto = contenuto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEsito_messaggio() {
		return esito_messaggio;
	}

	public void setEsito_messaggio(String esito_messaggio) {
		this.esito_messaggio = esito_messaggio;
	}
	
	
	
}
