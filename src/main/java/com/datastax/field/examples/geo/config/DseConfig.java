package com.datastax.field.examples.geo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.JdkSSLOptions;
import com.datastax.driver.core.RemoteEndpointAwareJdkSSLOptions;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.auth.DsePlainTextAuthProvider;
import com.datastax.field.examples.geo.App;

@Configuration
public class DseConfig {
	
	private DseCluster dseCluster;
	
	private DseSession dseSession;
	final Logger logger = LoggerFactory.getLogger(DseConfig.class);
	
	@Bean 
	public DseCluster dseCluster() {

		// This type of logic would never really make sense in production.. 

		logger.info("DseConfig : connecting to cluster");
		
		if( App.HOST == null || App.HOST.equals("") ){
			this.dseCluster = DseCluster.builder()
					.addContactPoint("localhost").build();
		} else {
			if( App.CASSANDRA_USER != null && App.CASSANDRA_PASS != null ){
				
//				SSLContext sslcontext = null;
//				try {
//				sslcontext = SSLContext.custom()
//		                .loadTrustMaterial(
//		                		new File("truststore.jks"),
//		                		App.CASSANDRA_PASS.toCharArray(),
//		                        new TrustSelfSignedStrategy())
//		                .build();
//				} catch (Exception e){
//					e.printStackTrace();
//				}
//				
				if( App.USE_SSL ){
					SSLContext sslcontext = null;
					try {
					sslcontext = SSLContexts.custom()
			                .loadTrustMaterial(
			                		new File("truststore.jks"),
			                		App.CASSANDRA_PASS.toCharArray(),
			                        new TrustSelfSignedStrategy())
			                .build();
					} catch (Exception e){
						e.printStackTrace();
					}
					
					JdkSSLOptions sslOptions = RemoteEndpointAwareJdkSSLOptions.builder()
							  .withSSLContext(sslcontext)
							  .build();
					
					this.dseCluster = DseCluster.builder()
							.addContactPoint( App.HOST )
							.withSSL(sslOptions)
							.withAuthProvider(new DsePlainTextAuthProvider(App.CASSANDRA_USER, App.CASSANDRA_PASS))
							.build();
					
				} else {
					
					this.dseCluster = DseCluster.builder()
							.addContactPoint( App.HOST )
							.withAuthProvider(new DsePlainTextAuthProvider(App.CASSANDRA_USER, App.CASSANDRA_PASS))
							.build();
					
				}
				
			} else {
				this.dseCluster = DseCluster.builder()
						.addContactPoint(App.HOST).build();
			}
		}
		return this.dseCluster;
	}
	
	
	@Bean 
	public DseSession dseSession() {
		logger.info("Dseconfig : getting session");
		this.dseSession = this.dseCluster.connect();
		return this.dseSession;
	}
	
	
	
	@PreDestroy
	public void cleanUp() {
		//System.out.println("Shutting down dseSession and dseCluster");
		logger.info("Shutting down dseSession and dseCluster");
		dseSession.close();
		dseCluster.close();
	}
	
	
	private static SSLContext getSSLContext()  throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		FileInputStream tsf = null;
        //FileInputStream ksf = null;
        SSLContext ctx = null;
        try
        {
            tsf = new FileInputStream("truststore.jks");
            ctx = SSLContext.getInstance("SSL");
            KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(tsf, App.CASSANDRA_PASS.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
            ctx.init(null, tmf.getTrustManagers(), new SecureRandom());
            
        } catch(Exception e){
            e.printStackTrace();
        }
        finally
        {
        	tsf.close();
        }
		return ctx;
	}
	
}
