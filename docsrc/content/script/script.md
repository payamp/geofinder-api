---
date: 2017-09-07T22:35:05-04:00
title: Script
menu:
  main:
      parent: Script 
      identifier: script
      weight: 301
---

# Demo Script
After you have deployed the Geofinder API app from Asset Hub, log into your Rightscale account and find your deployment, it will be named "assethub-*your.name*@datastax.com---", with a long deployment ID following the "---." For example, the one I spun up for the screenshots in this script is named "assethub-phil.bayliss@datastax.com---35b3c9fc-bb30-4ae8-9db0-d463436392fe-2m25y5r7lakxg."

Once you've found your deployment, select it to see the overview page:

![](./img/rightscale_cluster_overview.png)

Then select "Cluster 1 Seed" to view the details. Take notice the "Public IP Address." This will be used for you to access the different parts of your demo:

![](./img/rightscale_seed1_details.png)

Now that you have your Public IP address you can access three different apps that you can leverage:
* Geofinder API application - <http://publicIP:9000/>
* DataStax OpsCenter - <http://publicIP:8888/>
* Solr Admin UI - <http://publicIP:8983/solr/>

---


## GeoFinder Application
### Tab 1 - "Map Search (Find)"
This tab is for locating various points of interest (POIs). The value here is that the transactional data stored in DSE is being displayed in a user-friendly format, allowing the user to explore via a search box or map interface. You can keep the user in the default location of Washington, DC or move it to anywhere else in the US. Just select the "Move User" button and drag it to wherever you want on the map. (The dataset loaded for this app is POIs within the continental US). You can also navigate on the map by holding the "shift" key and dragging your mouse over the map, creating a bounding box for the app to display.

![](./img/tab1_home.png)

In the search box, DSE Search has been configured to populate potential matches once 3 or more characters have been entered:

![](./img/tab1_type_ahead.png)

Once you have entered a selection, there is a list that populates. Rolling over items on the list will highlight that specific result on the map by turning it's "pin" on the map red:

![](./img/tab1_starbucks_detail.png)

Selecting the pin itself on the map will give you a small window with the address details:

![](./img/tab1_starbucks_pin_click.png)

The other button under the search bar is "CQL." Select this after you've done a search for a POI. The resulting window will display two syntaxes; the first being the query used to populate the type-ahead feature, and the second being the full CQL search query:

![](./img/tab1_CQL.png)

### Tab 2 - "Map Facets (Discover)"
This tab does just as you would think - it gives the user the ability to find different categories of businesses within the region of the map that is displayed. The application is leveraging a faceted search capability based on a bounded box region of the map (what is visible on the screen). Zooming in and out of the map will automatically update the facet totals, as well as show any applicable subfacets that exist:

![](./img/tab2_facet_home.png)

Selecting a facet allows you to see the details of the business that make it up. For example, after zooming in and scrolling, "Sports Bar" was selected. The bottom left now shows the list of the 28 sports bars located in the region of the map that is showing (while simultaneously dropping pins on their location on the map). The value here is allowing the application users to navigate their transactional data without needing to know exact keys:

![](./img/tab2_facet_selection.png) 

-------

### OpsCenter

If needed for your demo, you can access OpsCenter and give a tour of it at <http://publicIP:8888/>:

![](./img/opscenter.png)

### Solr Admin UI

The Solr Admin UI is accessible if needed. Typically this will be useful if you want to show the schema.xml or config.xml files to any experienced Solr users.

![](./img/solr_admin_ui.png)
