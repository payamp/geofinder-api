package com.datastax.field.examples.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.geometry.Point;
import com.datastax.field.examples.geo.util.CQLUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Service
public class LocationFinderService {
	
	
	@Autowired
	private DseCluster dseCluster;
	
	@Autowired
	private DseSession dseSession;
	
	private static String PLAIN_NAME_SUGGEST_QUERY = "SELECT name from geonames.locations where solr_query = '{ \"q\":\"*:*\", \"fq\":\"name_lowercase:*%s*\"}' LIMIT 50";
	private static String SORT_NAME_SUGGEST_QUERY = "SELECT name from geonames.locations where solr_query = '{ \"q\":\"*:*\", \"fq\":\"name_lowercase:*%s*\", \"sort\":\"%s\"}' LIMIT 50";
	
	
	/**
	 * the GEO_NAME_SUGGEST_QUERY is not faceting, so the UI takes that into consideration and only displays unique values.  
	 * 
	 * Another strategy is to use facets but facets can only sorted by count OR alpha-numerically,    
	 */
	// %s order: name, lon, lat, radius
	private static String GEO_NAME_SUGGEST_QUERY = "SELECT name from simplegeo.locations where solr_query = '{ \"q\":\"*:*\", \"fq\":\"name_lowercase:*%s* AND geo:\\\"IsWithin(BUFFER(POINT(%s %s), %s))\\\"\"}' LIMIT 50";
	
	
	
	/* 
	 * simplegeo.locations table schema: 
	 * 
	 *     id text PRIMARY KEY,
	 *     address text,
	 *     category text,
	 *     city text,
	 *     geo 'org.apache.cassandra.db.marshal.PointType',
	 *     menulink text,
	 *     name text,
	 *     name_lowercase text,
	 *     phone text,
	 *     post_code text,
	 *     province text,
	 *     source text,
	 *     subcategory text,
	 *     tags list<text>,
	 *     type text,
	 *     website text
	 */
	private static String GEO_NAME_SEARCH_QUERY = "SELECT id, name, address, city, province, post_code, phone, category, subcategory,geo, website, menulink, tags from simplegeo.locations where solr_query = '{ \"q\":\"*:*\", \"fq\":\"name_lowercase:*%s* AND geo:\\\"IsWithin(BUFFER(POINT(%s %s), %s))\\\"\"}' LIMIT 50";
	private static final String GEO_FILTER_PIVOT_ON_CATEGORY = "SELECT * FROM simplegeo.locations where solr_query = '{ \"q\":\"*:*\", \"fq\":\"geo:[%s,%s TO %s,%s]\", \"facet\":{ \"pivot\":\"category\",\"limit\":\"-1\",\"mincount\":1,\"sort\":\"count\"}}'";
	private static final String GEO_FILTER_PIVOT_ON_CATEGORY_AND_SUBCATEGORY = "SELECT * FROM simplegeo.locations where solr_query = '{ \"q\":\"*:*\", \"fq\":\"geo:[%s,%s TO %s,%s]\", \"facet\":{ \"pivot\":\"category,subcategory\",\"limit\":\"-1\",\"mincount\":1}}'";
	private static final String GEO_FILTER_LOCATIONS_ON_CATEGORY = "SELECT * FROM simplegeo.locations where solr_query = '{\"q\":\"*:*\", \"fq\":\"category:%s AND geo:[%s,%s TO %s,%s]\"}' LIMIT %s";
	private static final String GEO_FILTER_LOCATIONS_ON_CATEGORY_AND_SUBCATEGORY = "SELECT * FROM simplegeo.locations where solr_query = '{\"q\":\"*:*\", \"fq\":\"category:%s AND subcategory:%s AND geo:[%s,%s TO %s,%s]\"}' LIMIT %s";
	
	/**
	 * Simply return a list of names that contain the input name substring. example query would be "*name*" type of query. 
	 * @param name
	 * @return
	 */
	public JsonArray getPlacesContainingName(String name ) {
		
		JsonArray results = new JsonArray();
		
		String query = getPlacesContainingNameQuery(name);
		
		ResultSet resultSet = this.dseSession.execute(query);
		
		for( Row row: resultSet.all() ){
			results.add( row.getString("name") );
		}
		
		return results;
	}
	
	/**
	 * Simple query to power the name suggestion, without sort. 
	 * @param name
	 * @return
	 */
	public String getPlacesContainingNameQuery(String name ) {
		
		return String.format(PLAIN_NAME_SUGGEST_QUERY, CQLUtil.cleanseQueryStr(name).toLowerCase());
		
	}
	
	/**
	 * Get Place Names containing a string, with sort field
	 * 
	 * @param name
	 * @param sort
	 * @return
	 */
	public JsonArray getPlacesContainingNameSort(String name, String sort ) {
		
		JsonArray results = new JsonArray();
		
		String query = String.format(SORT_NAME_SUGGEST_QUERY, CQLUtil.cleanseQueryStr(name).toLowerCase(), sort);
		
		ResultSet resultSet = this.dseSession.execute(query);
		
		for( Row row: resultSet.all() ){
			results.add( row.getString("name") );
		}
		
		return results;
		
	}
	
	
	/**
	 * for display in the UI to show how this works.
	 *  
	 */
	public String getPlacesContainingNameSortQuery(String name, String sort ) {
		
		return String.format(SORT_NAME_SUGGEST_QUERY, CQLUtil.cleanseQueryStr(name).toLowerCase(), sort);
		
	}
	
	/**
	 * for display in the UI
	 */
	public String nameSuggestWithPointAndRadiusQuery( String name, double lat, double lng, double radiusInKm ){
		
		double degrees = kilometersToDegrees(radiusInKm, lat);
		
		return String.format(GEO_NAME_SUGGEST_QUERY, 
					CQLUtil.cleanseQueryStr(name).toLowerCase(),
					String.valueOf(lng),
					String.valueOf(lat),
					String.valueOf(degrees));
		
	}
	
	public JsonArray nameSuggestWithPointAndRadius(String name, double lat, double lng, double radiusInKm){
		
		JsonArray results = new JsonArray();
		
		
		String query = nameSuggestWithPointAndRadiusQuery(name, lat, lng, radiusInKm);
		System.out.println(query);
		ResultSet resultSet = this.dseSession.execute(query);
		for( Row row: resultSet.all() ){
			results.add( row.getString("name") );
		}
		
		return results;
	}
	
public String nameSearchWithPointAndRadiusQuery( String name, double lat, double lng, double radiusInKm ){
		
		double degrees = kilometersToDegrees(radiusInKm, lat);
		
		return String.format(GEO_NAME_SEARCH_QUERY, 
					CQLUtil.cleanseQueryStr(name).toLowerCase(),
					String.valueOf(lng),
					String.valueOf(lat),
					String.valueOf(degrees));
		
	}
	
	public JsonArray nameSearchWithPointAndRadius(String name, double lat, double lng, double radiusInKm){
		
		JsonArray results = new JsonArray();
		
		String query = nameSearchWithPointAndRadiusQuery(name, lat, lng, radiusInKm);
		System.out.println(query);
		ResultSet resultSet = this.dseSession.execute(query);
		for( Row row: resultSet.all() ){

			JsonObject locObj = new JsonObject();
			
			locObj.addProperty("id", row.getString("id"));
			
			locObj.addProperty("name", row.getString("name"));
			locObj.addProperty("address", row.getString("address"));
			locObj.addProperty("city", row.getString("city"));
			locObj.addProperty("province", row.getString("province"));
			
			locObj.addProperty("phone", row.getString("phone"));
			locObj.addProperty("post_code", row.getString("post_code"));
			locObj.addProperty("category", row.getString("category"));
			locObj.addProperty("subcategory", row.getString("subcategory"));
			locObj.addProperty("website", row.getString("website"));
			locObj.addProperty("menulink", row.getString("menulink"));
			
			Point geo = (Point)row.getObject("geo");
			JsonObject geoObj = new JsonObject();
			geoObj.addProperty("lng", geo.X());
			geoObj.addProperty("lat", geo.Y());
			locObj.add("geo", geoObj);
			
			results.add(locObj);
		}
		return results;
	}
	
	public String geoFilterPivotOnCateogoryQuery(double lllat, double lllng, double urlat, double urlng){
		return String.format(GEO_FILTER_PIVOT_ON_CATEGORY, lllat, lllng, urlat, urlng);
	}
	
	public String geoFilterPivotOnCateogory(double lllat, double lllng, double urlat, double urlng){
		return executePivotQuery( geoFilterPivotOnCateogoryQuery(lllat, lllng, urlat, urlng) );
	}
	
	public String geoFilterPivotOnCateogoryAndSubcategoryQuery( double lllat, double lllng, double urlat, double urlng ){
		
		return String.format(GEO_FILTER_PIVOT_ON_CATEGORY_AND_SUBCATEGORY, 
				lllat, 
				lllng, 
				urlat, 
				urlng);
		
	}
	
	public String geoFilterPivotOnCateogoryAndSubCategory(double lllat, double lllng, double urlat, double urlng){
		return executePivotQuery( geoFilterPivotOnCateogoryAndSubcategoryQuery(lllat, lllng, urlat, urlng) );
	}
	
	
	
	public String executePivotQuery( String query ) {
		
		ResultSet rs = this.dseSession.execute(query);
		return rs.all().get(0).getString(0);
		
	}
	
	
	public String geoFilterLocationsOnCateogoryQuery( String category, int numRows, double lllat, double lllng, double urlat, double urlng ){
		return String.format(GEO_FILTER_LOCATIONS_ON_CATEGORY, category, numRows, lllat, lllng, urlat, urlng);
	}
	
	public String geoFilterLocationsOnCateogoryAndSubcategoryQuery( String category, String subcategory,
			int numRows, double lllat, double lllng, double urlat, double urlng ){
		
		return String.format(GEO_FILTER_LOCATIONS_ON_CATEGORY_AND_SUBCATEGORY, 
				category, 
				subcategory, 
				numRows, 
				lllat, 
				lllng, 
				urlat, 
				urlng);
	}
	
	public String geoFilterLocationsOnCateogoryAndOrSubcategory( String category, 
			String subcategory, int numRows,  double lllat, double lllng, double urlat, double urlng ){
		
		// If the subcategory is null, only do a category filter
		if( subcategory == null){
			String query = geoFilterLocationsOnCateogoryAndSubcategoryQuery( category, subcategory, numRows, lllat, lllng, urlat, urlng);
			
		}
		return String.format(GEO_FILTER_LOCATIONS_ON_CATEGORY, category, subcategory, lllat, lllng, urlat, urlng);
	}
	
	
	private static double kilometersToDegrees( double radiusInKm, double lat ) {
		double oneDegreeInKilometers = 111.13295 - 0.55982 * Math.cos(2 * lat) + 0.00117 * Math.cos(4 * lat); 
		return (1 / oneDegreeInKilometers) * radiusInKm;
	}
	
}
