package com.datastax.field.examples.geo.model;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import lombok.Data;

@Data
@UDT(name="feature", keyspace="geonames")
public class Feature {
	
	// @Column(name="feature_class")
	@Field(name = "feature_class")
	private String featureClass;
	
	// @Column(name="feature_code")
	@Field(name = "feature_code")
	private String featureCode;

	@Field(name = "short_description")
	private String shortDescrition;

	@Field(name = "long_description")
	private String longDescription;
	
}
