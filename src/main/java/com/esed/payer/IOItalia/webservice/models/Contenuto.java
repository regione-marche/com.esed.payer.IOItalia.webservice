package com.esed.payer.IOItalia.webservice.models;

import com.seda.payer.core.bean.IoItaliaMessaggio;


public class Contenuto {

	private String oggetto_messaggio;
	private String corpo_messaggio;
	private DatiPagamento dati_pagamento;
	private String data_scadenza;
	
	public Contenuto() {
	}
	
	public Contenuto(IoItaliaMessaggio messaggio) {
		this.oggetto_messaggio = messaggio.getOggettoMessaggio();
		this.corpo_messaggio = messaggio.getCorpoMessaggio();
		this.dati_pagamento = new DatiPagamento(messaggio.getImporto(), messaggio.getAvvisoPagoPa(), messaggio.getScadenzaPagamento());
		this.data_scadenza = messaggio.getDataScadenzaMessaggio().toString();

	}
	
	
	public String getOggetto_messaggio() {
		return oggetto_messaggio;
	}

	public void setOggetto_messaggio(String oggetto_messaggio) {
		this.oggetto_messaggio = oggetto_messaggio;
	}

	public String getCorpo_messaggio() {
		return corpo_messaggio;
	}

	public void setCorpo_messaggio(String corpo_messaggio) {
		this.corpo_messaggio = corpo_messaggio;
	}

	public DatiPagamento getDati_pagamento() {
		return dati_pagamento;
	}

	public void setDati_pagamento(DatiPagamento dati_pagamento) {
		this.dati_pagamento = dati_pagamento;
	}

	public String getData_scadenza() {
		return data_scadenza;
	}

	public void setData_scadenza(String data_scadenza) {
		this.data_scadenza = data_scadenza;
	}

	
}
