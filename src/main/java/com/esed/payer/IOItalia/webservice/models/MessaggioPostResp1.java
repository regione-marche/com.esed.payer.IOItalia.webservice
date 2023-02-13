package com.esed.payer.IOItalia.webservice.models;

public class MessaggioPostResp1 {

	private String id_Messaggio;
	
	public MessaggioPostResp1(String id) {
		this.setId_Messaggio(id);
	}

	public String getId_Messaggio() {
		return id_Messaggio;
	}

	public void setId_Messaggio(String id_Messaggio) {
		this.id_Messaggio = id_Messaggio;
	}
	
}
