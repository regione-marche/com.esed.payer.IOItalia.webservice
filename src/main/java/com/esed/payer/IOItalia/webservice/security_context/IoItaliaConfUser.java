package com.esed.payer.IOItalia.webservice.security_context;

import java.security.Principal;

import com.seda.payer.core.bean.IoItaliaConfigurazione;

public class IoItaliaConfUser implements Principal {

	private IoItaliaConfigurazione ioItaliaConfigurazione;
	
	
	public IoItaliaConfUser(IoItaliaConfigurazione ioItaliaConfigurazione) {
		this.ioItaliaConfigurazione = ioItaliaConfigurazione;
	}
	
	
	@Override
	public String getName() {
		return null;
	}


	public IoItaliaConfigurazione getIoItaliaConfigurazione() {
		return ioItaliaConfigurazione;
	}

}
