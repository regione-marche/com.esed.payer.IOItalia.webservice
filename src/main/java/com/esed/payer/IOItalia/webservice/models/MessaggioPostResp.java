package com.esed.payer.IOItalia.webservice.models;

public class MessaggioPostResp {

	private String id_Messaggio;
	private String progressivo_messaggio;

	public MessaggioPostResp(String id) {
		this.id_Messaggio = id;
	}
	
	
	
	public MessaggioPostResp(String idMessaggio, String progressivoMessaggio) {
		super();
		this.id_Messaggio = idMessaggio;
		this.progressivo_messaggio = progressivoMessaggio;
	}
	public String getIdMessaggio() {
		return id_Messaggio;
	}
	public void setIdMessaggio(String idMessaggio) {
		this.id_Messaggio = idMessaggio;
	}
	public String getProgressivoMessaggio() {
		return progressivo_messaggio;
	}
	public void setProgressivoMessaggio(String progressivoMessaggio) {
		this.progressivo_messaggio = progressivoMessaggio;
	}
	
	
	
}
