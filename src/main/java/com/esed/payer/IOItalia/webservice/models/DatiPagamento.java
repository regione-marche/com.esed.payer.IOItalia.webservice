package com.esed.payer.IOItalia.webservice.models;

import java.math.BigDecimal;

public class DatiPagamento {

	private BigDecimal importo;
	private String avviso_pagoPA;
	private String scadenza_pagamento;
	
	public DatiPagamento(BigDecimal importo, String avviso_pagoPA, String scadenza_pagamento) {
		this.importo = importo;
		this.avviso_pagoPA = avviso_pagoPA;
		this.scadenza_pagamento = scadenza_pagamento;
	}

	public DatiPagamento() {
		
	}

	public BigDecimal getImporto() {
		return importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	public String getAvviso_pagoPA() {
		return avviso_pagoPA;
	}

	public void setAvviso_pagoPA(String avviso_pagoPA) {
		this.avviso_pagoPA = avviso_pagoPA;
	}

	public boolean isScadenza_pagamento() {
		
		return scadenza_pagamento.equals("1");
	}

	public void setScadenza_pagamento(String scadenza_pagamento) {
		this.scadenza_pagamento = scadenza_pagamento;
	}
	
	
}
