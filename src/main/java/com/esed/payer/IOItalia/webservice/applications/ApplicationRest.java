package com.esed.payer.IOItalia.webservice.applications;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.esed.payer.IOItalia.webservice.request_filter.Filtro;
import com.esed.payer.IOItalia.webservice.resources.Resource;
import com.esed.payer.IOItalia.webservice.utils.PropertiesPath;
import com.seda.commons.properties.PropertiesLoader;
import com.seda.commons.properties.tree.PropertiesNodeException;
import com.seda.commons.properties.tree.PropertiesTree;
import com.seda.compatibility.SystemVariable;

@ApplicationPath("/rest")
public class ApplicationRest extends Application {
	
	private static PropertiesTree propertiesTree;
	

	static {
		SystemVariable sv = new SystemVariable();
		
		try {
			String rootPath = sv.getSystemVariableValue(PropertiesPath.ROOT.format());
			
			if (rootPath == null || !new File(rootPath).exists()) {
				throw new RuntimeException("File di configurazione non impostato");
			}
			try {
				propertiesTree = new PropertiesTree(PropertiesLoader.load(rootPath));
			} catch (IOException | PropertiesNodeException e) {
				e.printStackTrace();
				throw new RuntimeException("Impossibile caricare il file di configurazione", e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Impossibile caricare il file di configurazione", e);
			// TODO: handle exception
		}
		
	}
	
	public static PropertiesTree getPropertiesTree() {
		return propertiesTree;
	}
	

	private Set<Object> singleton = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	
	public ApplicationRest() {
		classes.add(Resource.class);
		classes.add(Filtro.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return this.classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return this.singleton;
	}
	
}
