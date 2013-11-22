/**
 * Copyright 2004-2012 Crypto-Pro. All rights reserved.
 * Этот файл содержит информацию, являющуюся
 * собственностью компании Крипто-Про.
 *
 * Любая часть этого файла не может быть скопирована,
 * исправлена, переведена на другие языки,
 * локализована или модифицирована любым способом,
 * откомпилирована, передана по сети с или на
 * любую компьютерную систему без предварительного
 * заключения соглашения с компанией Крипто-Про.
 */

package wss4j.wss4j1_6_3.ws.security.components.crypto;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Properties;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.CredentialException;
import org.apache.ws.security.components.crypto.Merlin;

/*
 * This class extends default class Merlin to cache a private key and to avoid permanent
 * reading of key from store.
 */
public class MerlinEx extends Merlin {

    private PrivateKey cachedPrivateKey = null;
    private String cachedAlias = null;
	
	public MerlinEx(Properties properties) throws CredentialException,
			IOException {
		super(properties);
	}
	
	public MerlinEx(Properties properties, ClassLoader loader) throws CredentialException, 
			IOException {
		super(properties, loader);
	}
	
    public PrivateKey getPrivateKey(String alias, String password) {
    	
    	if (cachedPrivateKey == null || 
    			(cachedAlias != null && !cachedAlias.equalsIgnoreCase(alias))) {
    		cachedAlias = alias;
    		try {
				cachedPrivateKey = super.getPrivateKey(alias, password);
			} catch (WSSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return cachedPrivateKey;
    }
}
