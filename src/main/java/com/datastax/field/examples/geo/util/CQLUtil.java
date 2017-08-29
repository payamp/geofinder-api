package com.datastax.field.examples.geo.util;

import java.util.Arrays;
import java.util.List;

public class CQLUtil {
	
	private static final List<String> VALID_SORT_FIELDS = Arrays.asList("population","date");
	private static final List<String> VALID_SORT_DIRECTIONS = Arrays.asList("desc","asc");
	
	
	
	/**
	 * This method removes all ( ;, ", <, > ) characters from an input String.
	 * Then it escapes single quotes ' -> '' as described: 
	 * http://docs.datastax.com/en/cql/3.3/cql/cql_reference/escape_char_r.html
	 * 
	 * @param input String
	 * @return cleansed String
	 */
	public static String cleanseInput( String input ){
		return input
				.replaceAll("[;><\"]", " ")  // replace non-allowed characters, then
				.replaceAll("'", "''")       // escape single quotes
				.replaceAll("\\s+", " ")     // collapse all of the additional spaces down to one space.
				.trim();  
	}
	
	
	
	/**
	 * In addition to what cleanseInsert does, this method replaces all space " " with "\\\\ "
	 * 
	 * @param input String
	 * @return cleansed String
	 */
	public static String cleanseQueryStrField( String input ){
		return cleanseInput(input)
				.replace(" ", "\\\\ ");
	}
	
	/**
	 * In addition to what cleanseInsertRegex does, this method replaces all spaces " " with "\\\\ "
	 * 
	 * @param input String
	 * @return cleansed String
	 */
	public static String cleanseQueryStr( String input ){
		return cleanseInput(input).replace(" ", "\\\\ ");
	}

	/**
	 * With Solr you can sort with multiple fields, only supporting (exposing) one sort field for the moment.
	 * 
	 * see VALID_SORT_FIELDS for allowed sort fields.
	 * 
	 * @param sort
	 * @return
	 */
	public static boolean isValidSort( String sort ){
		
		sort = sort.toLowerCase();
		
		if ( sort != null &&  sort.length() > 3 || sort.indexOf(" ") < 0 ){
			
			String[] fields = sort.split(" ");
			
			if( fields.length != 2)
				return false;
			
			if (VALID_SORT_FIELDS.contains(fields[0]))
				if( VALID_SORT_DIRECTIONS.contains(fields[1]) )
					return true;
			
		}
		return false;
	}
	
}
