package com.datastax.field.examples.geo;


import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.datastax.field.examples.geo.config.WebConfig;
import com.datastax.field.examples.geo.service.LocationFinderService;
import com.google.common.base.Strings;

@Configuration
@ComponentScan({"com.datastax.field.examples.geo"})
public class App 
{
	public static String HOST;
	public static String CASSANDRA_USER;
	public static String CASSANDRA_PASS;
	public static boolean USE_SSL = false;
	
	final static Logger logger = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) throws ParseException, IOException
    {
        
        CommandLineParser parser = new DefaultParser();
        Options options = cliOptions();
        CommandLine cmd = parser.parse( options, args);
        
        String host = cmd.getOptionValue("h");
        if ( host != null )
        	HOST = host;
        
        String cassandraUser = cmd.getOptionValue("u");
        if( cassandraUser != null )
        	CASSANDRA_USER = cassandraUser;
        
        String cassandraPass = cmd.getOptionValue("p");
        if( cassandraPass != null )
        	CASSANDRA_PASS = cassandraPass;
        
        
        if ( cmd.hasOption("ssl"))
        	USE_SSL = true;
        
        // if the HOST is null or empty, just print the usage. 
        if( Strings.isNullOrEmpty(HOST) ){
        	
        	HelpFormatter formatter = new HelpFormatter();
        	formatter.printHelp( "java -jar geofinder-api.jar", options );
        	
        } else {
        	logger.info( "Starting up and connecting to: " + host );
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(App.class);
            
            new WebConfig(ctx.getBean(LocationFinderService.class));
            ctx.registerShutdownHook();
        }
    }
   
    
    public static Options cliOptions(){
    	Options o = new Options();
    	o.addOption("h", "hostname", true, "cassandra host (required)");
    	o.addOption("u", "user", true, "Cassandra username");
    	o.addOption("p", "password", true, "Cassandra user password");
    	o.addOption("s", "ssl" , false, "Use SSL, expects a truststore.jks file to be in current directory" );
    	return o;
    }
}
