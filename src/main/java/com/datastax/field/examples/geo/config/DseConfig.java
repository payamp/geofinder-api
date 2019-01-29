package com.datastax.field.examples.geo.config;

import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.auth.DsePlainTextAuthProvider;
import com.datastax.field.examples.geo.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class DseConfig {
	
	private DseCluster dseCluster;
	
	private DseSession dseSession;
	final Logger logger = LoggerFactory.getLogger(DseConfig.class);


	/**
	 * reference:  https://docs.datastax.com/en/developer/java-driver/3.6/manual/ssl/
	 *
	 * @return DseCluster
	 */
	@Bean 
	public DseCluster dseCluster() {

		// This type of logic may not really make sense in production (this is a demo).

		logger.info("DseConfig : connecting to cluster");

		DseCluster.Builder builder = DseCluster.builder();

		if( App.HOST == null || App.HOST.equals("") ){

			this.dseCluster = builder.addContactPoint("localhost").build();

		} else {

			builder.addContactPoint(App.HOST);
			
			if( App.CASSANDRA_USER != null && App.CASSANDRA_PASS != null ){

				builder.addContactPoint(App.HOST)
						.withAuthProvider(new DsePlainTextAuthProvider(App.CASSANDRA_USER, App.CASSANDRA_PASS));

				if( App.USE_SSL ){
					builder.withSSL();
				 }

			}

			this.dseCluster = builder.build();
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
		logger.info("Shutting down dseSession and dseCluster");
		dseSession.close();
		dseCluster.close();
	}
	
	
}
