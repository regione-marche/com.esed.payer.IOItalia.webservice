package com.esed.payer.IOItalia.webservice.request_filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.esed.payer.IOItalia.webservice.applications.ApplicationRest;
import com.esed.payer.IOItalia.webservice.security_context.CustomSecurityContext;
import com.esed.payer.IOItalia.webservice.security_context.IoItaliaConfUser;
import com.esed.payer.IOItalia.webservice.utils.PropertiesPath;
import com.seda.commons.properties.tree.PropertiesTree;
import com.seda.j2ee5.maf.components.servicelocator.ServiceLocator;
import com.seda.j2ee5.maf.components.servicelocator.ServiceLocatorException;
import com.seda.payer.core.bean.IoItaliaConfigurazione;
import com.seda.payer.core.dao.IoItaliaDao;
import com.seda.payer.core.exception.DaoException;


@Provider
public class Filtro implements ContainerRequestFilter {

	@Context
	private SecurityContext securityContext;
	

	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		MultivaluedMap<String, String> headers = requestContext.getHeaders();
		List<String> stringhe = headers.get("Ocp-Apim-Subscription-Key");
		List<String> stringhe2 = headers.get("Codice-Utente");
		
		if(stringhe == null || stringhe.isEmpty()) {
			requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
			return;
		}
		if(stringhe2 == null || stringhe2.isEmpty()) {
			requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
			return;
		}
		
		String apiKey = stringhe.get(0);
		
		String cutecute = stringhe2.get(0);
		
		
		IoItaliaConfigurazione conf = null;
	
		PropertiesTree config = ApplicationRest.getPropertiesTree();
		
		try (Connection conn = ServiceLocator.getInstance().getDataSource(ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSource.format(cutecute))).getConnection()){
			IoItaliaDao ioItaliaDao = new IoItaliaDao(conn, config.getProperty(PropertiesPath.dataSourceSchema.format(cutecute)));

//			YLM PG22XX06 INI			
			if ( cutecute.equals("000P4")) {
				conf = ioItaliaDao.selectConfigurazione(apiKey);
			} else {
				conf = ioItaliaDao.selectConfigurazioneTail(apiKey, true);
			}
//			YLM PG22XX06 INI	
			
			
		} catch (SQLException | DaoException | ServiceLocatorException e) {
			e.printStackTrace();
		}
		
		if(conf == null || conf.getCodiceUtente() == null) {
			requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
			return;
		} else {
			if(!conf.getCodiceUtente().equals(cutecute)) {
				requestContext.abortWith(Response.status(Status.BAD_REQUEST).build());
				return;
			}
		}
		
		IoItaliaConfUser confUser = new IoItaliaConfUser(conf);
		CustomSecurityContext<IoItaliaConfUser> customSecurityContext = new CustomSecurityContext<IoItaliaConfUser>(confUser, new HashSet<String>(), securityContext.isSecure(), SecurityContext.DIGEST_AUTH);
		requestContext.setSecurityContext(customSecurityContext);
	}

}
