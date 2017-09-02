package com.datastax.field.examples.geo.config;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.staticFiles;
import static spark.Spark.port;
import com.datastax.field.examples.geo.controller.LocationFinderController;
import com.datastax.field.examples.geo.service.LocationFinderService;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

public class WebConfig {

	private LocationFinderService locationFinderService;
	

	public WebConfig(LocationFinderService lfService) {
		this.locationFinderService = lfService;
		port(9000);
		setupRoutes();
	}

	private void setupRoutes() {
		
		staticFiles.location("/public");

		path("/api", () -> {
			
			get("/geo-name-suggest", (req,res) -> {
				
				String name = req.queryParams("name");
				String lat = req.queryParams("lat");
				String lng = req.queryParams("lng");
				String radius = req.queryParamOrDefault("r", "5.0");
				
				JsonObject responseObj = LocationFinderController.geoNameSuggestWithPointAndRadius(locationFinderService, name, lat, lng, radius);
				res.status(200);
				res.type("application/json");
				return responseObj;
			});
			
			get("/geo-name-search", (req,res) -> {
				
				String name = req.queryParams("name");
				String lat = req.queryParams("lat");
				String lng = req.queryParams("lng");
				String radius = req.queryParamOrDefault("r", "5.0");
				
				JsonObject responseObj = LocationFinderController.geoNameSearchWithPointAndRadius(locationFinderService, name, lat, lng, radius);
				res.status(200);
				res.type("application/json");
				return responseObj;
				
			});
			
			get("/geo-bbox-pivot-on-category", (req,res) -> {
				
				String lllat = req.queryParams("lllat");
				String lllng = req.queryParams("lllng");
				String urlat = req.queryParams("urlat");
				String urlng = req.queryParams("urlng");
				
				String response = LocationFinderController.geoFilterPivotOnCateogory( locationFinderService, lllat, lllng, urlat, urlng );
				
				res.status(200);
				res.type("application/json");
				return response;
			});
			
			get("/geo-bbox-pivot-on-category-and-subcategory", (req,res) -> {
				
				String lllat = req.queryParams("lllat");
				String lllng = req.queryParams("lllng");
				String urlat = req.queryParams("urlat");
				String urlng = req.queryParams("urlng");
				
				String response = LocationFinderController.geoFilterPivotOnCateogoryAndSubcategory(locationFinderService, lllat, lllng, urlat, urlng);
				
				res.status(200);
				res.type("application/json");
				return response;
			});
			
			
			/**
			 * url parameters: 
			 * 		category (String)
			 * 		subcategory (String)
			 * 		num_results: int (optional, default 100)
			 */
			get("/geo-bbox-filter-on-category", (req,res) -> {
				
				String lllat = req.queryParams("lllat");
				String lllng = req.queryParams("lllng");
				String urlat = req.queryParams("urlat");
				String urlng = req.queryParams("urlng");
				String category = req.queryParams("category");
				String subcategory = req.queryParams("subcategory");
				String numResultsStr = req.queryParamOrDefault("num_results", "100");
				
				int numResults = 20;
				
				try {
					numResults = Integer.parseInt(numResultsStr);
				} catch (NumberFormatException e) {}
				
				res.status(200);
				res.type("application/json");
				return LocationFinderController.geoFilterLocationsOnCateogoryAndSubcategory(locationFinderService, category, subcategory, numResults, lllat, lllng, urlat, urlng);
			});
			
			
			exception(Exception.class, (exception,req,res) -> {
				exception.printStackTrace();
			});
			
		});

	}

}
