package com.esed.payer.IOItalia.webservice.security_context;

import java.security.Principal;
import java.util.Set;

import javax.ws.rs.core.SecurityContext;

public class CustomSecurityContext<T extends Principal> implements SecurityContext {

	private final T userPrincipal;
	private final Set<String> userRoles;
	private final boolean secure;
	private final String authenticationScheme;
	
	
	
	public CustomSecurityContext(T userPrincipal, Set<String> userRoles, boolean secure, String authenticationScheme) {
		super();
		this.userPrincipal = userPrincipal;
		this.userRoles = userRoles;
		this.secure = secure;
		this.authenticationScheme = authenticationScheme;
	}



	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return userPrincipal;
	}



	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return userRoles.parallelStream().anyMatch(x -> x.equals(role));
	}



	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return secure;
	}



	@Override
	public String getAuthenticationScheme() {
		// TODO Auto-generated method stub
		return authenticationScheme;
	}

	

}
