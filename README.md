

## Overview
This is a simple web app to demonstrate some of the geospatial capabilities in DSE.  It is not meant to be a demonstration of how to build a REST API. I used the [spark java framework](http://sparkjava.com/) (not to be confused with Apache Spark) for its lean and simple approach.

--

**Demo Video**

Will insert a demo video here.

----------

## Assumptions

DSE running, and already loaded the sample data into DSE that is backed by DSE Search.
- Java 8 installed 
- maven is installed (only if you are building the project)





**build:**

`git clone`

`mvn clean package`

**run:**
```
usage: java -jar geofinder-api.jar
 -h,--hostname <arg>   cassandra host (required)
 -p,--password <arg>   Cassandra user password
 -s,--ssl              Use SSL, expects a truststore.jks file to be in
                       current directory
 -u,--user <arg>       Cassandra username
```

Connecting to local DSE instance:

`java -jar target/geofinder-api.jar -h localhost`


Connecting to remote DSE instance w/ username, password authentication and SSL

`java -jar target/geofinder-api.jar -h 192.168.1.190 -u cassandra -p ChangeMe123 -ssl`

This expects a valid truststore.jks file to be in the current working directory. 

The web app is exposed on port 9000

If you are running this from assethub, you will need to point your web browser at:

http://node0_ip:9000

---


## JSON Endpoints

## Name Suggest
```
GET /api/name-suggest?name=string&sort=sortfield (asc desc)
```

#### URL Parameters:
| Field     | Description                       | Required | Example      | 
|---------- |-----------------------------------|----------| ------------ | 
| name      | the place name you want to search | YES      |              | 
| sort      | The field you want to sort on     | NO       |  "population desc" or "date asc"

### Sample response
```
{
  "names": [
    "Chalgomchin",
    "Chichali Karam",
    "Chichali-e Seh",
    "Park-e Jangali-ye Chitgar",
    "Qanchi Gol",
    "Kuh-e Saghalchi",
    "Chin Parch",
    "Ilkhchi Kumeh",
    "Ichin",
    "Chinam Dahaneh",
    "Parchi Kola",
    "Shilat Chikrud",
    "Kuh-e Qal`ehchi",
    "Garachi",
    "Kuh-e Lak Chir",
    "Kuh-e Chidar",
    "Chir-e `Olya",
    "Chichuran",
    "Hajjiabad-e Okhtachi",
    "Alichin",
    "Chianeh",
    "Baleqchi",
    "Qomchian",
    "Eynehchi",
    "Turchi",
    "Kuh-e Chia",
    "Kuh-e Nochirya",
    "Kuh-e Tofangchian",
    "Pasha Chiftlik",
    "Pano Chiftlik",
    "Chai Chill",
    "Kachiones",
    "Chchinnokefalos",
    "Laxia tou Vachioti",
    "Kokkinorachi",
    "Chchinnomoutti",
    "Archistratigos",
    "Kuh-e Cheraghchi",
    "Kuh-e Kachi",
    "Kalateh-ye Chikal",
    "Kuh-e Chidandar",
    "Saqarchin",
    "Chiftlikoudhkia",
    "Kochinoyia",
    "Kochinoin",
    "Agios Vichianos",
    "Agios Tychikos",
    "Chistron Vounon",
    "Uqchi Kuchek",
    "Hivehchi-ye Bala"
  ],
  "query": "SELECT name from geonames.locations where solr_query = '{ \"q\":\"*:*\", \"fq\":\"name_lowercase:*chi*\"}' LIMIT 50"
}
```


## Geo Contextual suggest

For example:
 * Things Near me

```
GET /api/geo-name-suggest
```

### URL Parameters:

| Field     | Description                     | Required | Default      |   min / max    |
|---------- |---------------------------------|----------| ------------ | -------------- |
| name      | the string you want to search   | YES      |     N/A      |                |
| lat       | Latitude                        | YES      |     N/A      | -90.0 / 90.0   |
| lng       | Longitude                       | YES      |     N/A      | -180.0 / 180.0 |
| r         | The radius of the search in (km)| NO       |     5.0      | 1.0 / 20.0     |

## Faceting using a geospatial filter query

`Given a map extent, what category, and subcategory of POIs are available.`

In solr you can filter on an arbitrary rectangle by using the search `geo:[45,-94 TO 46,-93]` searching for a bounding.

The order of the query is `[lower-left TO upper-right]`, and the order is lat, lng.


```
GET /api/geo-facet-category?parameters
```

### URL Parameters:

| Field     | Description                     | Required |   min / max    |
|---------- |---------------------------------|----------| -------------- |
| lllat     | Lower Left Latitude             | YES      |  -90.0 / 90.0  |
| lllng     | Lower Left Longitude            | YES      | -180.0 / 180.0 |
| urlat     | Upper Right Latitude            | YES      |  -90.0 / 90.0  |
| urlng     | Upper Right Longitude           | YES      | -180.0 / 180.0 |


```
GET /api/geo-facet-category-subcategory?parameters
```

### URL Parameters:

| Field     | Description                     | Required |   min / max    |
|---------- |---------------------------------|----------| -------------- |
| lllat     | Lower Left Latitude             | YES      |  -90.0 / 90.0  |
| lllng     | Lower Left Longitude            | YES      | -180.0 / 180.0 |
| urlat     | Upper Right Latitude            | YES      |  -90.0 / 90.0  |
| urlng     | Upper Right Longitude           | YES      | -180.0 / 180.0 |


## Searching for a particular category or subcategory of POI within a map extent. 

```
GET /api/geo-bbox-filter-on-category?parameters
```

| Field       | Description                     | Required |   min / max    |
|------------ |---------------------------------|----------| -------------- |
| category    | Category                        | YES      |  N/A           |
| subcategory | Subcategory                     | NO       |  N/A           |
| num_records | Number of records (default 20)  | NO       |                |
| lllat       | Lower Left Latitude             | YES      |  -90.0 / 90.0  |
| lllng       | Lower Left Longitude            | YES      | -180.0 / 180.0 |
| urlat       | Upper Right Latitude            | YES      |  -90.0 / 90.0  |
| urlng       | Upper Right Longitude           | YES      | -180.0 / 180.0 |

###sample response (truncated):

```
{
  "category,subcategory": [
    {
      "field": "category",
      "value": "Airline",
      "count": 116,
      "pivot": [
        {
          "field": "subcategory",
          "value": "",
          "count": 116
        }
      ]
    },
    {
      "field": "category",
      "value": "Airport",
      "count": 23,
      "pivot": [
        {
          "field": "subcategory",
          "value": "",
          "count": 23
        }
      ]
    }
  ]
}
```

## Reference:


calculation on converting how many degrees will be [lat lon info](http://calgary.rasc.ca/latlong.htm) 

```javascript
theta = latitude;
let one_deg_lon_in_km = 111.13295 - 0.55982 * Math.cos(2 * theta) + 0.00117 * Math.cos(4 * theta);
```


 
