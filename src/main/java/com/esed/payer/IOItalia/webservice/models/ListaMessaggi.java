package com.esed.payer.IOItalia.webservice.models;

import java.util.List;

public class ListaMessaggi {

	private List<MessaggioPost> messages ;

	public ListaMessaggi() {
		
	}
	
	public List<MessaggioPost> getMessages() {
		return messages;
	}

	public void setMessages(List<MessaggioPost> messages) {
		this.messages = messages;
	}
	
}
