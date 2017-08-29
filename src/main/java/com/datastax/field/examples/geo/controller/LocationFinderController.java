package com.datastax.field.examples.geo.controller;

import com.datastax.field.examples.geo.service.LocationFinderService;
import com.datastax.field.examples.geo.util.CQLUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

public class LocationFinderController {
	
	public static final double MAX_RADIUS = 100.0d;
	public static final double MIN_RADIUS = 1.0d;
	public static final double MIN_LAT    = -90.0d;
	public static final double MAX_LAT    = 90.0d;
	public static final double MIN_LNG    = -180.0d;
	public static final double MAX_LNG    = 180.0d;
	
	
	public static String nameSuggestSimple(Request req, Response res, LocationFinderService locationFinderService) {

		String name = req.queryParams("name");

		Gson gson = new Gson();
		JsonObject response = new JsonObject();

		/*
		 * Make sure the name query param is between 1 and 100 characters long. 
		 */
		if (name != null && name.length() > 1 && name.length() <= 100) {

			JsonArray nameList = locationFinderService.getPlacesContainingName(name);
			String query = locationFinderService.getPlacesContainingNameQuery(name);
			response.add("names", nameList);
			response.addProperty("query", query);

			res.status(200);
			res.type("application/json");
			return gson.toJson(response);

		} else {

			if (name.length() > 100) {

				response.addProperty("error", "name parameter was too long, at most 100 characters");

			} else {

				response.addProperty("error", "missing property (name)");

			}
			response.addProperty("url", "/api/name/suggest?name=<string>");
			res.status(200);
			res.type("application/json");
			return gson.toJson(response);
		}
	}

	public static Object nameSuggestSimpleSort(Request req, Response res, LocationFinderService locationFinderService) {

		String name = req.queryParams("name");
		String sort = req.queryParams("sort");

		boolean isValidSort = CQLUtil.isValidSort(sort);
		
		Gson gson = new Gson();
		JsonObject response = new JsonObject();
		
		if( !isValidSort )
			response.addProperty("message", "invalid sort");
		
		if (name != null && name.length() > 1 && name.length() <= 100  && isValidSort ) {
			
			JsonArray nameList = locationFinderService.getPlacesContainingNameSort(name, sort);
			String query = locationFinderService.getPlacesContainingNameSortQuery(name, sort);
			response.add("names", nameList);
			response.addProperty("query", query);

			res.status(200);
			res.type("application/json");
			return gson.toJson(response);
			
			
		} else {
			response.addProperty("message", "missing property (name)");
		}
		
		res.status(200);
		res.type("application/json");
		return gson.toJson(response);
	}
	
	
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
		
		double lllatDouble = 0.0d;
		double lllngDouble = 0.0d;
		double urlatDouble = 0.0d;
		double urlngDouble = 0.0d;
		
		boolean isValidRequest = true;
		
		JsonArray messages = new JsonArray();
		
		// make sure the lllat is a double and within bounds. 
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
		
		
		// make sure the lllng is a double and within bounds. 
		try {
			lllngDouble = Double.parseDouble(lllng);
			if( lllatDouble < MIN_LNG  || lllatDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("lllng: (lower left latitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + ") : " + lllng );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("lllng does not contain a parsable double: " + lllng);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("lllng parameter was null");
		}
		
		
		// make sure the lllat is a double and within bounds. 
		try {
			urlatDouble = Double.parseDouble(urlat);
			if( urlatDouble < MIN_LAT  || urlatDouble > MAX_LAT ){
				isValidRequest = false;
				messages.add("urlat: (upper-right latitude) was out of range (" +  MIN_LAT + " TO " + MAX_LAT + ") : " + urlat );
			}
		} catch (NumberFormatException nfe) {
			isValidRequest = false;
			messages.add("urlat does not contain a parsable double: " + urlat);
		} catch ( NullPointerException npe ) {
			isValidRequest = false;
			messages.add("urlat parameter was null");
		}
		
		
		
		// make sure the urlng is a double and within bounds. 
		try {
			urlngDouble = Double.parseDouble(urlng);
			if( urlatDouble < MIN_LNG  || urlatDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("urlng: (upper-right latitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + ") : " + urlng );
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
		
		
		if ( isValidRequest ){
			String resp = service.geoFilterPivotOnCateogory(lllatDouble, lllngDouble, urlatDouble, urlngDouble);
			return resp;
		} else {
			JsonObject response = new JsonObject();
			response.addProperty("success", isValidRequest);
			response.add("messages", messages);
			return response.toString();
		}
	}
	
	
public static String geoFilterPivotOnCateogoryAndSubcategory( LocationFinderService service, String lllat, String lllng, String urlat, String urlng  ){
		
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
			if( lllatDouble < MIN_LNG  || lllatDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("lllng: (lower left latitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + ") : " + lllng );
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
				messages.add("urlat: (upper-right latitude) was out of range (" +  MIN_LAT + " TO " + MAX_LAT + ") : " + urlat );
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
			if( urlatDouble < MIN_LNG  || urlatDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("urlng: (upper-right latitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + ") : " + urlng );
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
		
		
		if ( isValidRequest ){
			String resp = service.geoFilterPivotOnCateogoryAndSubCategory(lllatDouble, lllngDouble, urlatDouble, urlngDouble);
			return resp;
		} else {
			JsonObject response = new JsonObject();
			response.addProperty("success", isValidRequest);
			response.add("messages", messages);
			return response.toString();
		}
	}
	
	public static String geoFilterLocationsOnCateogoryAndSubcategory( LocationFinderService service, String category, String subcategory, int numResults, String lllat, String lllng, String urlat, String urlng  ){
		
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
			if( lllatDouble < MIN_LNG  || lllatDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("lllng: (lower left latitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + ") : " + lllng );
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
				messages.add("urlat: (upper-right latitude) was out of range (" +  MIN_LAT + " TO " + MAX_LAT + ") : " + urlat );
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
			if( urlatDouble < MIN_LNG  || urlatDouble > MAX_LNG ){
				isValidRequest = false;
				messages.add("urlng: (upper-right latitude) was out of range (" +  MIN_LNG + " TO " + MAX_LNG + ") : " + urlng );
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
		
		
		if ( isValidRequest ){
			String resp = service.geoFilterLocationsOnCateogoryAndOrSubcategory(category, subcategory, numResults, lllatDouble, lllngDouble, urlatDouble, urlngDouble);
			return resp;
		} else {
			JsonObject response = new JsonObject();
			response.addProperty("success", isValidRequest);
			response.add("messages", messages);
			return response.toString();
		}
	}
}
