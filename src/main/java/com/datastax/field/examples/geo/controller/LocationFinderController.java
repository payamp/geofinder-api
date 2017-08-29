package com.datastax.field.examples.geo.controller;

import com.datastax.field.examples.geo.service.LocationFinderService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import spark.utils.StringUtils;

public class LocationFinderController {
	
	public static final double MAX_RADIUS = 100.0d;
	public static final double MIN_RADIUS = 1.0d;
	public static final double MIN_LAT    = -90.0d;
	public static final double MAX_LAT    = 90.0d;
	public static final double MIN_LNG    = -180.0d;
	public static final double MAX_LNG    = 180.0d;
	
	
	public static JsonObject geoNameSuggestWithPointAndRadius( LocationFinderService service, String name, String lat, String lng, String radius  ){
		
		boolean isValidRequest = true;
		
		JsonObject response = new JsonObject();
		JsonArray messages = new JsonArray();
		
		double latDouble = 0.0d;
		double lngDouble = 0.0d;
		double radiusDouble = 0.0d;
		
		if( StringUtils.isEmpty(name) ) {
			messages.add("Name field was null or empty");
			isValidRequest = false;
		}
		// validate the lat (latitude)
		try {
			latDouble = Double.parseDouble(lat);
			if( latDouble < -90.0d  || latDouble > 90.0d  ){
				isValidRequest = false;
				messages.add("lat: (Latitude) was outside the -90.0 to 90.0 range: " + lat );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("lat does not contain a parsable double: " + lat);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("lat parameter was null");
		}
		
		// validate the parameter lat (latitude)
		try {
			lngDouble = Double.parseDouble(lng);
			if( lngDouble < -180.0d  || lngDouble > 180.0d  ){
				isValidRequest = false;
				messages.add("lng (Longitude) was outside the -180.0 to 180.0 range: " + lng );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("lng does not contain a parsable double: " + lat);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("lng parameter was null");
		}
		
		// make sure the radius is within bounds. 
		try {
			radiusDouble = Double.parseDouble(radius);
			if( radiusDouble < MIN_RADIUS  || radiusDouble > MAX_RADIUS ){
				isValidRequest = false;
				messages.add("r: (radius) was out of range (" +  MIN_RADIUS + " TO " + MAX_RADIUS + ") : " + radius );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("r does not contain a parsable double: " + radius);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("r parameter was null");
		}
		if( messages.size() == 0){
			messages.add("OK");
		}
		
		
		response.addProperty("success", isValidRequest);
		response.add("messages", messages);
		
		if( isValidRequest ){
			JsonArray names = service.nameSuggestWithPointAndRadius(name, latDouble, lngDouble, radiusDouble);
			String query  = service.nameSuggestWithPointAndRadiusQuery(name, latDouble, lngDouble, radiusDouble);
			response.add("names", names);
			response.addProperty("query", query);
		}
		
		return response;
	}
	
	public static JsonObject geoNameSearchWithPointAndRadius( LocationFinderService service, String name, String lat, String lng, String radius  ){

		boolean isValidRequest = true;
		
		JsonObject response = new JsonObject();
		JsonArray messages = new JsonArray();
		
		double latDouble = 0.0d;
		double lngDouble = 0.0d;
		double radiusDouble = 0.0d;
		
		if( StringUtils.isEmpty(name) ) {
			messages.add("Name field was null or empty");
			isValidRequest = false;
		}
		// validate the lat (latitude)
		try {
			latDouble = Double.parseDouble(lat);
			if( latDouble < -90.0d  || latDouble > 90.0d  ){
				isValidRequest = false;
				messages.add("lat: (Latitude) was outside the -90.0 to 90.0 range: " + lat );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("lat does not contain a parsable double: " + lat);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("lat parameter was null");
		}
		
		// validate the parameter lat (latitude)
		try {
			lngDouble = Double.parseDouble(lng);
			if( lngDouble < -180.0d  || lngDouble > 180.0d  ){
				isValidRequest = false;
				messages.add("lng (Longitude) was outside the -180.0 to 180.0 range: " + lng );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("lng does not contain a parsable double: " + lat);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("lng parameter was null");
		}
		
		// make sure the radius is within bounds. 
		try {
			radiusDouble = Double.parseDouble(radius);
			if( radiusDouble < MIN_RADIUS  || radiusDouble > MAX_RADIUS ){
				isValidRequest = false;
				messages.add("r: (radius) was out of range (" +  MIN_RADIUS + " TO " + MAX_RADIUS + ") : " + radius );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("r does not contain a parsable double: " + radius);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("r parameter was null");
		}
		if( messages.size() == 0){
			messages.add("OK");
		}
		
		response.addProperty("success", isValidRequest);
		response.add("messages", messages);
		
		if( isValidRequest ){
			JsonArray names = service.nameSearchWithPointAndRadius(name, latDouble, lngDouble, radiusDouble);
			String query  = service.nameSearchWithPointAndRadiusQuery(name, latDouble, lngDouble, radiusDouble);
			response.add("locations", names);
			response.addProperty("query", query);
		}
		
		return response;
	}
	
	public static String geoFilterPivotOnCateogory( LocationFinderService service, String lllat, String lllng, String urlat, String urlng  ){
		
		JsonObject response = validateBoundingBox(lllat, lllng, urlat, urlng);
		
		if ( response.get("success").getAsBoolean() ){
			
			double lllatDouble = Double.parseDouble(lllat);
			double lllngDouble = Double.parseDouble(lllng);
			double urlatDouble = Double.parseDouble(urlat);
			double urlngDouble = Double.parseDouble(urlng);
			
			return service.geoFilterPivotOnCateogory(lllatDouble, lllngDouble, urlatDouble, urlngDouble);
			
		} else {
			
			return response.toString();
		}
		
	}
	
	
	public static String geoFilterPivotOnCateogoryAndSubcategory( LocationFinderService service, String lllat, String lllng, String urlat, String urlng  ){

		JsonObject response = validateBoundingBox(lllat, lllng, urlat, urlng);
		
		if ( response.get("success").getAsBoolean() ){
			
			double lllatDouble = Double.parseDouble(lllat);
			double lllngDouble = Double.parseDouble(lllng);
			double urlatDouble = Double.parseDouble(urlat);
			double urlngDouble = Double.parseDouble(urlng);
			
			return service.geoFilterPivotOnCateogoryAndSubCategory(lllatDouble, lllngDouble, urlatDouble, urlngDouble);
			
		} else {
			return response.toString();
		}
		
	}
	
	public static String geoFilterLocationsOnCateogoryAndSubcategory( LocationFinderService service, String category, String subcategory, int numResults, String lllat, String lllng, String urlat, String urlng  ){
		
		JsonObject response = validateBoundingBox(lllat, lllng, urlat, urlng);
		
		if ( response.get("success").getAsBoolean() ){
			
			double lllatDouble = Double.parseDouble(lllat);
			double lllngDouble = Double.parseDouble(lllng);
			double urlatDouble = Double.parseDouble(urlat);
			double urlngDouble = Double.parseDouble(urlng);
			
			
			String query = service.geoFilterLocationsOnCateogoryAndOrSubcategoryQuery(category, subcategory, numResults, lllatDouble, lllngDouble, urlatDouble, urlngDouble);
			JsonArray locations = service.geoFilterLocationsOnCateogoryAndOrSubcategory(category, subcategory, numResults, lllatDouble, lllngDouble, urlatDouble, urlngDouble);
			response.addProperty("query", query);
			response.add("locations", locations);
			
		}
		
		return response.toString();
	}
	
	
	private static JsonObject validateBoundingBox( String lllat, String lllng, String urlat, String urlng ){
		
		double lllatDouble = 0.0d;
		double lllngDouble = 0.0d;
		double urlatDouble = 0.0d;
		double urlngDouble = 0.0d;
		
		boolean isValidRequest = true;
		
		JsonArray messages = new JsonArray();
		
		// make sure the lllat (Lower Left Latitude) is a double and within bounds. 
		try {
			lllatDouble = Double.parseDouble(lllat);
			if( lllatDouble < MIN_LAT  || lllatDouble > MAX_LAT ){
				isValidRequest = false;
				messages.add("lllat: (lower left latitude) was out of range (" +  MIN_LAT + " TO " + MAX_LAT + ") : " + lllat );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("lllat does not contain a parsable double: " + lllat);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("lllat parameter was null");
		}
		
		
		// make sure the lllng (Lower Left Longitude) is a double and within bounds. 
		try {
			lllngDouble = Double.parseDouble(lllng);
			if( lllngDouble < MIN_LNG  || lllngDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("lllng: (lower left longitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + "), provided: " + lllng );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("lllng does not contain a parsable double: " + lllng);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("lllng parameter was null");
		}
		
		
		// make sure the urlat (Upper Right Latitude) is a double and within bounds. 
		try {
			urlatDouble = Double.parseDouble(urlat);
			if( urlatDouble < MIN_LAT  || urlatDouble > MAX_LAT ){
				isValidRequest = false;
				messages.add("urlat: (upper-right latitude) was out of range (" +  MIN_LAT + " TO " + MAX_LAT + "), provided: " + urlat );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("urlat does not contain a parsable double: " + urlat);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("urlat parameter was null");
		}
		
		// make sure the urlng (Upper Right Longitude) is a double and within bounds. 
		try {
			urlngDouble = Double.parseDouble(urlng);
			if( urlngDouble < MIN_LNG  || urlngDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("urlng: (upper-right longitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + "), provided: " + urlng );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("urlng does not contain a parsable double: " + urlng);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("urlng parameter was null");
		}
		
		
		if( messages.size() == 0){
			messages.add("OK");
		}
		
		JsonObject response = new JsonObject();
		response.addProperty("success", isValidRequest );
		response.add("messages", messages);
		return response;
	}
	
}
