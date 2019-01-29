
/* This will work on any version of DSE but it will only do the right thing in DSE 6.7 which is to use the JTS, RPT (RecursivePrefixTree) type */
CREATE SEARCH INDEX ON simplegGeo.locations WITH OPTIONS {reindex: false, lenient: true};

/* turn indexing off for name */
ALTER SEARCH INDEX SCHEMA ON simplegeo.locations SET fields.field[@name='name'] @indexed='false';

/* add docValues=true */
ALTER SEARCH INDEX SCHEMA ON simplegeo.locations SET fields.field[@name='province'] @docValues='true';
ALTER SEARCH INDEX SCHEMA ON simplegeo.locations SET fields.field[@name='category'] @docValues='true';
ALTER SEARCH INDEX SCHEMA ON simplegeo.locations SET fields.field[@name='subcategory'] @docValues='true';
ALTER SEARCH INDEX SCHEMA ON simplegeo.locations SET fields.field[@name='city'] @docValues='true';
ALTER SEARCH INDEX SCHEMA ON simplegeo.locations SET fields.field[@name='post_code'] @docValues='true';
ALTER SEARCH INDEX SCHEMA ON simplegeo.locations SET fields.field[@name='type'] @docValues='true';
RELOAD SEARCH INDEX ON simplegeo.locations;
REBUILD SEARCH INDEX ON simplegeo.locations;

/* 
RELOAD moves PENDING changes into ACTIVE, 
Put another way, all of the ALTER commands above, puts those changes into temporary holding place "PENDNIG" until you call RELOAD. 

REBUILD starts reindexing data based on the ACTIVE schema. 

To see the active schema run:
DESCRIBE ACTIVE SEARCH INDEX SCHEMA ON simplegeo.locations;

To see the pending schema run:
DESCRIBE PENDING SEARCH INDEX SCHEMA ON simplegeo.locations;

*/
