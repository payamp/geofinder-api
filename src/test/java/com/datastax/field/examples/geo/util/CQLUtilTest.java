package com.datastax.field.examples.geo.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class CQLUtilTest extends TestCase {
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CQLUtilTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CQLUtilTest.class );
    }

    /**
     * Test 
     */
    public void testCleanseQueryStrField()
    {
    	// cleanseQueryStrField
    	//String input = "San Franciso; DELETE * from system.peers;";
    	
    	String input = " \"San Francisco\"; ";
    	String expected = "San\\\\ Francisco";
    	String result = CQLUtil.cleanseQueryStrField(input);
    	
    	assertTrue( result.equals( expected ) );
    }
	
    /**
     * Test 
     */
    public void testCleanseQueryStrFieldRegex()
    {
    	// cleanseQueryStrField
    	//String input = "San Franciso; DELETE * from system.peers;";
    	
    	String input = " \"San Francisco\"; ";
    	String expected = "San\\\\ Francisco";
    	String result = CQLUtil.cleanseQueryStr(input);
    	
    	System.out.println("input:" + input );
    	System.out.println("expected" + expected);
    	System.out.println("result" + result);
    	
    	assertTrue( result.equals( expected ) );
    }

}
