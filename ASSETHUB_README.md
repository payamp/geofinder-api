## Important!

* Use Instance Type:  **m3.2xlarge**
  
* Virtual Nodes: **1**

Geofinder API Script
===================

This is a guide for how to use the geo demo asset for DSE Search.

### Motivation
The Geofinder API sample application is a good way to showcase and quickly demonstrate the capability of DataStax Enterprise (DSE) Max and it's DSE Search functionality. The sample application is meant to be a way to show how different aspects of DSE Search can be surfaced through the application layer. Keep in mind that the demo is a conduit for the messaging of DSE, not the end user application itself, so the messaging should correlate. 

### What is included?
This field asset includes an interactive map demonstrating the following DSE Search functionality:

* Type Ahead searching
* CQL Search syntax
* Geospatial Query
* Facet Query

### Business Take Aways
Give your end users the search interface they expect over the transactional data stored in your DSE platform. In today's consumer driven world, end users expect to be able to search over their data, whether by typing or navigating a map when looking for certain points of interest (POI).

### Technical Take Aways
The Geofinder application leverages DSE Search's geospatial, type-ahead, and facet query capabilities to provide the easy-to-use UI end users expect. As new POI data is added to the cluster, it will be indexed immediately and be searchable as soon as it is ingested.
