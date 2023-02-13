package com.esed.payer.IOItalia.webservice.resources;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import javax.ws.rs.core.Response.Status;

import com.esed.payer.IOItalia.webservice.applications.ApplicationRest;
import com.esed.payer.IOItalia.webservice.models.Contenuto;
import com.esed.payer.IOItalia.webservice.models.ListaMessaggi;
import com.esed.payer.IOItalia.webservice.models.MessageErrorResp;
import com.esed.payer.IOItalia.webservice.models.Messaggio;
import com.esed.payer.IOItalia.webservice.models.MessaggioPost;
import com.esed.payer.IOItalia.webservice.models.MessaggioPostResp;
import com.esed.payer.IOItalia.webservice.models.MessaggioPostResp1;
import com.esed.payer.IOItalia.webservice.security_context.IoItaliaConfUser;
import com.esed.payer.IOItalia.webservice.utils.PropertiesPath;
import com.seda.j2ee5.maf.components.servicelocator.ServiceLocator;
import com.seda.j2ee5.maf.components.servicelocator.ServiceLocatorException;
import com.seda.payer.core.bean.IoItaliaConfigurazione;
import com.seda.payer.core.bean.IoItaliaMessaggio;
import com.seda.payer.core.dao.IoItaliaDao;
import com.seda.payer.core.exception.DaoException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Resource {
	
	@Context
	private SecurityContext securityContext;
	
	@GET
	@Path("messages/{codice_fiscale}/{id_messaggio}")
	public Response statoMessaggio(@PathParam("codice_fiscale") String codiceFiscale, 
									@PathParam("id_messaggio") String idMessaggio) {
		IoItaliaMessaggio messaggio = null;
		IoItaliaConfUser confUser = (IoItaliaConfUser) securityContext.getUserPrincipal();
		IoItaliaConfigurazione conf = confUser.getIoItaliaConfigurazione();
		try (Connection conn = ServiceLocator.getInstance().getDataSource(ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSource.format(conf.getCodiceUtente()))).getConnection()){
			IoItaliaDao dao = new IoItaliaDao(conn, ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSourceSchema.format(conf.getCodiceUtente())));
			messaggio = dao.selectMessaggio(Long.parseLong(idMessaggio));
			
			if (messaggio == null) {
				MessageErrorResp error = new MessageErrorResp("No message found for the provided ID","404-Not Found","There was an error processing the request");
				return Response.status(Status.NOT_FOUND).entity(error).build();
			}
			if (!messaggio.getIdDominio().trim().equals(((IoItaliaConfUser) securityContext.getUserPrincipal()).getIoItaliaConfigurazione().getIdDominio().trim())) {
				MessageErrorResp error = new MessageErrorResp("Unauthorized","401-Not Found","Non si dispone delle autorizzazioni necessarie");
				return Response.status(Status.UNAUTHORIZED).entity(error).build();
			}
			if (!messaggio.getCodiceFiscale().trim().equals(codiceFiscale)) {
				MessageErrorResp error = new MessageErrorResp("No message found for the provided ID","409-Conflict","Il codice fiscale non corrisponde all'id messaggio");
				return Response.status(Status.CONFLICT).entity(error).build();
			}
			
		} catch (SQLException | NumberFormatException | DaoException | ServiceLocatorException e) {
			e.printStackTrace();
		}
		Messaggio mes = new Messaggio();
		mes.setContenuto(new Contenuto(messaggio));
		mes.setEmail(messaggio.getEmail());
		mes.setEsito_messaggio(messaggio.getStato());
		
		return Response.status(Status.OK).entity(mes).build();
	}
	
	@POST
	@Path("messages")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response invioMessaggi(ListaMessaggi messages) {

		List<IoItaliaMessaggio> listaMessaggiValidi = new ArrayList<>();
		IoItaliaConfUser confUser = (IoItaliaConfUser) securityContext.getUserPrincipal();
		IoItaliaConfigurazione conf = confUser.getIoItaliaConfigurazione();
		
		List<MessaggioPostResp> valori = new ArrayList<MessaggioPostResp>();
		MessageErrorResp error;
		Set<Long> lista_progressivi = new HashSet<>();
		//TODO METODO
			
		for(MessaggioPost mex : messages.getMessages()) {
			error = new MessageErrorResp("Campo non corretto","400-Bad Request","") ;
			
			//TODO SET progressivo_messaggio
			if(!(mex.getProgressivo_messaggio()>= 1 || mex.getProgressivo_messaggio()<= 9999999999L)) {
				error.setDetail("Progressivo Messaggio: Numero non valido al messaggio:["+mex.getProgressivo_messaggio()+"]");
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}
			
			if(!lista_progressivi.add(mex.getProgressivo_messaggio())) {
				error.setDetail("Progressivo Messaggio: Il numero è già stato usato al messaggio:["+mex.getProgressivo_messaggio()+"]");
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}
			
			if(mex.getCodice_fiscale() == null || mex.getCodice_fiscale().isEmpty()) {
				error.setDetail(error.getDetail() + "Codice Fiscale non valido al messaggio:["+mex.getProgressivo_messaggio()+"]");
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}
			
			error = controllaMessaggio(mex);
				if(error != null) {
					error.setDetail(error.getDetail()+ " al messaggio:["+mex.getProgressivo_messaggio()+"]");
					return Response.status(Status.BAD_REQUEST).entity(error).build();
				}
			
			IoItaliaMessaggio messaggio = new IoItaliaMessaggio();
			messaggio.setCutecute(conf.getCodiceUtente());
			messaggio.setIdDominio(conf.getIdDominio());
			messaggio.setTipologiaServizio(conf.getTipologiaServizio());
			messaggio.setTimestampParsingFile(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
			messaggio.setPosizione(4);
			messaggio.setCodiceFiscale(mex.getCodice_fiscale().trim());
			messaggio.setOggettoMessaggio(mex.getContenuto().getOggetto_messaggio());
			messaggio.setCorpoMessaggio(mex.getContenuto().getCorpo_messaggio());

			LocalDateTime localdate = LocalDateTime.parse(mex.getContenuto().getData_scadenza(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			messaggio.setDataScadenzaMessaggio(java.util.Date.from(localdate.atZone(ZoneId.systemDefault()).toInstant()));
				
			messaggio.setImporto(mex.getContenuto().getDati_pagamento().getImporto());
			messaggio.setAvvisoPagoPa(mex.getContenuto().getDati_pagamento().getAvviso_pagoPA());
			messaggio.setScadenzaPagamento(mex.getContenuto().getDati_pagamento().isScadenza_pagamento()? "1":"0");
			messaggio.setEmail(mex.getEmail());
			messaggio.setStato("0");
			messaggio.setImpostaServizio(conf.getImpostaServizio());
			messaggio.setIdInvioMessaggio(String.valueOf(mex.getProgressivo_messaggio()));
			
			listaMessaggiValidi.add(messaggio);
		}

		if(listaMessaggiValidi.size() > 0) {
			
			try (Connection conn = ServiceLocator.getInstance().getDataSource(ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSource.format(conf.getCodiceUtente()))).getConnection()){
				if(conn.getAutoCommit()) {
					conn.setAutoCommit(false);
				}
				IoItaliaDao dao = new IoItaliaDao(conn, ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSourceSchema.format(conf.getCodiceUtente())));
				long idfornitura = dao.insertFornitura(conf.getCodiceSocieta(), conf.getCodiceUtente(), conf.getCodiceEnte(), conf.getTipologiaServizio(), conf.getImpostaServizio(), "WS-"+LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
				
				for(IoItaliaMessaggio messaggio : listaMessaggiValidi) {
					messaggio.setIdFornitura(idfornitura);
					long idMessaggio = dao.insertMessaggio(messaggio);
					valori.add(new MessaggioPostResp(String.valueOf(idMessaggio), messaggio.getIdInvioMessaggio()));

				}
				for (int i = 0; i < valori.size(); i++) {
					if(valori.get(i).getIdMessaggio().equals("0")) {
						MessageErrorResp errorDb = new MessageErrorResp("The messages cannot be delivered","500-Internal Server Error","Errore nell' inserimento del messaggio:" + valori.get(i).getProgressivoMessaggio());
						return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorDb).build();
					}
				}
				
				conn.commit();
				
			} catch (SQLException | NumberFormatException | DaoException | ServiceLocatorException e) {
				MessageErrorResp errorDb = new MessageErrorResp("The messages cannot be delivered","500-Internal Server Error",e.getMessage());
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorDb).build();
				
			}
			
		}
		
		return Response.status(Status.CREATED).entity(valori).build();
	}
	
	@DELETE
	@Path("messages/{codice_fiscale}/{id_messaggio}")
	public Response cancellazioneMessaggio(@PathParam("codice_fiscale") String codiceFiscale, @PathParam("id_messaggio") String idMessaggio) {
		
		IoItaliaMessaggio messaggio = null;
		MessageErrorResp error = new MessageErrorResp();
		IoItaliaConfUser confUser = (IoItaliaConfUser) securityContext.getUserPrincipal();
		IoItaliaConfigurazione conf = confUser.getIoItaliaConfigurazione();
		
		try (Connection conn = ServiceLocator.getInstance().getDataSource(ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSource.format(conf.getCodiceUtente()))).getConnection()){
			IoItaliaDao dao = new IoItaliaDao(conn, ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSourceSchema.format(conf.getCodiceUtente())));
			messaggio = dao.selectMessaggio(Long.parseLong(idMessaggio));
			if (messaggio == null) {
				error.setTitle("No message found for the provided id");
				error.setStatus("404-Not Found");
				error.setDetail("There was an error processing the request");
				return Response.status(Status.NOT_FOUND).entity(error).build();
			}
			if (!messaggio.getIdDominio().trim().equals(((IoItaliaConfUser) securityContext.getUserPrincipal()).getIoItaliaConfigurazione().getIdDominio().trim())) {
				error = new MessageErrorResp("Unauthorized","401-Not Found","Non si dispone delle autorizzazioni necessarie");
				return Response.status(Status.UNAUTHORIZED).entity(error).build();
			}
			if (!messaggio.getCodiceFiscale().trim().equals(codiceFiscale)) {
				error = new MessageErrorResp("No message found for the provided ID","409-Conflict","Il codice fiscale non corrisponde all'id messaggio");
				return Response.status(Status.CONFLICT).entity(error).build();
			}
		
			 dao.deleteMessaggio(messaggio.getIdMessaggio());
			
		}catch (SQLException | NumberFormatException | DaoException | ServiceLocatorException e) {
			e.printStackTrace();
		}
		MessaggioPostResp1 resp = new MessaggioPostResp1(idMessaggio);
		return Response.status(Status.OK).entity(resp).build();
	}
	
	@PUT
	@Path("messages/{codice_fiscale}/{id_messaggio}")
	public Response aggiornamentoMessaggio(@PathParam("codice_fiscale") String codiceFiscale, @PathParam("id_messaggio") String idMessaggio, MessaggioPost messaggioPost) {
		
		IoItaliaMessaggio messaggio = null;
		IoItaliaConfUser confUser = (IoItaliaConfUser) securityContext.getUserPrincipal();
		IoItaliaConfigurazione conf = confUser.getIoItaliaConfigurazione();
		try (Connection conn = ServiceLocator.getInstance().getDataSource(ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSource.format(conf.getCodiceUtente()))).getConnection()){
			
			if(conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			
			IoItaliaDao dao = new IoItaliaDao(conn, ApplicationRest.getPropertiesTree().getProperty(PropertiesPath.dataSourceSchema.format(conf.getCodiceUtente())));
			messaggio = dao.selectMessaggio(Long.parseLong(idMessaggio));
			
			if (messaggio == null) {
				MessageErrorResp error = new MessageErrorResp("No message found for the provided ID","404-Not Found","There was an error processing the request");
				return Response.status(Status.NOT_FOUND).entity(error).build();
			}
			if (!messaggio.getIdDominio().trim().equals(((IoItaliaConfUser) securityContext.getUserPrincipal()).getIoItaliaConfigurazione().getIdDominio().trim())) {
				MessageErrorResp error = new MessageErrorResp("Unauthorized","401-Not Found","Non si dispone delle autorizzazioni necessarie");
				return Response.status(Status.UNAUTHORIZED).entity(error).build();
			}
			if (!messaggio.getCodiceFiscale().trim().equals(codiceFiscale)) {
				MessageErrorResp error = new MessageErrorResp("No message found for the provided ID","409-Conflict","Il codice fiscale non corrisponde all'id messaggio");
				return Response.status(Status.CONFLICT).entity(error).build();
			}
			if(messaggio.getStato().equals(1)) {
				MessageErrorResp error = new MessageErrorResp("Non è possibile modificare il messaggio","400-Bad Request","Messaggio già inviato");
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}
			
			MessageErrorResp error;
			error = controllaMessaggio(messaggioPost);
			if(error != null) {
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}
			
			messaggio.setCorpoMessaggio(messaggioPost.getContenuto().getCorpo_messaggio());
			messaggio.setOggettoMessaggio(messaggioPost.getContenuto().getOggetto_messaggio());
			messaggio.setImporto(messaggioPost.getContenuto().getDati_pagamento().getImporto());
			messaggio.setAvvisoPagoPa(messaggioPost.getContenuto().getDati_pagamento().getAvviso_pagoPA());
			messaggio.setScadenzaPagamento(messaggioPost.getContenuto().getDati_pagamento().isScadenza_pagamento()?"1":"0");
			
			LocalDateTime localdate = LocalDateTime.parse(messaggioPost.getContenuto().getData_scadenza(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			messaggio.setDataScadenzaMessaggio(java.util.Date.from(localdate.atZone(ZoneId.systemDefault()).toInstant()));
			messaggio.setEmail(messaggioPost.getEmail());
			
			dao.updateMessaggio(messaggio);
			
			conn.commit();
			
		}catch (SQLException | NumberFormatException | DaoException | ServiceLocatorException e) {
			e.printStackTrace();
		}
		MessaggioPostResp1 resp = new MessaggioPostResp1(idMessaggio);
		return Response.status(Status.OK).entity(resp).build();
	}
	
	private MessageErrorResp controllaMessaggio(MessaggioPost messaggioPost) {
		
		MessageErrorResp error = new MessageErrorResp("Campo non corretto","400-Bad Request","");
		if(messaggioPost.getContenuto().getOggetto_messaggio() == null || messaggioPost.getContenuto().getOggetto_messaggio().length() < 10 || messaggioPost.getContenuto().getOggetto_messaggio().length()> 120) {
			error.setDetail("Oggetto Messaggio: Numero caratteri non valido");
			return error;
		}
		if(messaggioPost.getContenuto().getCorpo_messaggio() == null || messaggioPost.getContenuto().getCorpo_messaggio().length() < 80 || messaggioPost.getContenuto().getCorpo_messaggio().length() > 10000) {
			error.setDetail("Corpo Messaggio: Numero caratteri non valido");
			return error;
		}
		if(messaggioPost.getContenuto().getDati_pagamento().getImporto() == null || messaggioPost.getContenuto().getDati_pagamento().getImporto().compareTo(BigDecimal.ONE) < 0 || messaggioPost.getContenuto().getDati_pagamento().getImporto().compareTo(BigDecimal.valueOf(9999999999L))>0) {
			error.setDetail("Importo non corretto");
			return error;
		}
		//Controllo data con parsetostring
		try {
			DateTimeFormatter.ISO_INSTANT.parse(messaggioPost.getContenuto().getData_scadenza());
		}catch (Exception e) {
			error.setDetail(error.getDetail() + "Formato data non valido");
			return error;
		}
		//Controllo AvvisoPagoPA (18 o 20)
		if(messaggioPost.getContenuto().getDati_pagamento().getAvviso_pagoPA().length() != 18 && messaggioPost.getContenuto().getDati_pagamento().getAvviso_pagoPA().length() != 20 ) {
			error.setDetail(error.getDetail() + "Formato Avviso PagoPA non valido");
			return error;
		}
		
		return null;
	}
	
}
