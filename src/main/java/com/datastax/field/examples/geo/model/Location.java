package com.datastax.field.examples.geo.model;

import java.util.Date;
import java.util.List;

import com.datastax.driver.dse.geometry.Point;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;


@Data
@Table(keyspace = "geonames", name = "locations", readConsistency = "QUORUM", writeConsistency = "QUORUM", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class Location {
	
	@PartitionKey
	private int geonameId;
	
	private String asciiName;

	@Column(name = "alternative_names")
	private List<String> alternativeNames;

	/**
	 * geo point
	 * 
	 * in Cassandra it is 'text' type in Solr it is a
	 * 'SpatialRecursivePrefixTreeFieldType' type
	 * 
	 * see:
	 * http://docs.datastax.com/en/dse/5.1/dse-dev/datastax_enterprise/search/solrTypeMapping.html#solrTypeMapping
	 */
	private Point geo;
	private double latitude;
	private double longitude;
	private Feature feature;
	private long population;

	/**
	 * 	   not sure what dem is for:
	 * http://download.geonames.org/export/dump/readme.txt
	 * 
	 * dem: digital elevation model, srtm3 or gtopo30, average elevation of
	 * 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer.
	 * srtm processed by cgiar/ciat.
	 * 
	 */
	private int dem;
	private String timezone;
	private Date modification_date;
	
	
	
}
