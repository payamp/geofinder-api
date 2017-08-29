package com.datastax.field.examples.geo.dao;

import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.field.examples.geo.model.Location;


@Accessor
public interface LocationDao {
	
	@Query("SELECT * from geonames.locations where solr_query = '{\"q\":\"*:*\", \"fq\":\"alternative_names:?\"}'")
	public Location getLocationByName( String name );
	
	

}
