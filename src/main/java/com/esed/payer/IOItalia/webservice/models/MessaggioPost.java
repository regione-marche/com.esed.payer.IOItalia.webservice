package com.esed.payer.IOItalia.webservice.models;

public class MessaggioPost {

	private long progressivo_messaggio;
	private String codice_fiscale;
	private Contenuto contenuto;
	private String email;
	
	public MessaggioPost() {
	}
	
	public long getProgressivo_messaggio() {
		return progressivo_messaggio;
	}

	public void setProgressivo_messaggio(long progressivo_messaggio) {
		this.progressivo_messaggio = progressivo_messaggio;
	}

	public String getCodice_fiscale() {
		return codice_fiscale;
	}

	public void setCodice_fiscale(String codice_fiscale) {
		this.codice_fiscale = codice_fiscale;
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
	
	
	
}
