package com.esed.payer.IOItalia.webservice.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;


public enum PropertiesPath {

	dataSourceSchema,
	dataSource, 
	ROOT;
	
	private static ResourceBundle rb;
   
    public String format(Object... args ) {
        synchronized(PropertiesPath.class) {
            if(rb==null)
                rb = ResourceBundle.getBundle(PropertiesPath.class.getName());
            return MessageFormat.format(rb.getString(name()),args);
        }
    }
}
