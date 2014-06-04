SAP HANA Cloud Samples - Library Application
==========================================

This application makes use of all SAP HANA Cloud services. Every new feature that the Platform will introduce will be incorporated into this application. 


Quick Start
-----------

Clone the repo, `https://github.com/SAP/cloud-sample-library.git`, or [download the latest release](https://github.com/SAP/cloud-sample-library/archive/master.zip).


Project Overview
----------------

Below is a basic description of the project. The structure is as follows:

* com.sap.hana.cloud.samples - the main package that holds all other
	+ adapters - classes for accessing the SAP HANA Cloud Platform services
	+ book.service - contains servlets (and filters, required for manipulation over books - 
		extracting, creating, editing, deleting, reserving, returning) + a servlet responsible for sending e-mails in the cases of reserving and returning a book
	+ my.books.service - servlets which manipulate books that the user has borrowed
	+ persistence.entities - entities for the database
	+ user.image.service - contains servlets that upload user's profile image to the Document Service repo and for retrieving this profile image from the repo
	+ user.service - contains servlets and filters which extract, save and manipulate user's data, as well as logging functionality 
	+ util - commonly used utility functionalities like date manipulation, streams manipulation, mails, Connectivity service utilities, etc.
		
	
	
Application Startup
-------------------

You can run the application either locally, or on SAP HANA Cloud.

* Running locally
 
 1. Go to your computer’s properties > Advanced system settings > Environment Variables, and create a new system variable named "NW\_CLOUD\_SDK\_PATH". 
 2. Enter the path to the directory where you have downloaded and unarchived the SAP HANA Cloud SDK. Check the pom.xml file for the SDK version and versions of other JAR files, if needed.
 3. Create a new local server.
 4. Double-click on it and choose the "Connectivity" tab.
 5. Create a new destination, named "OpenLibrary" and paste the following URL to the URL field:
 http://www.openlibrary.org or you can simply import the file "OpenLibrary", located in the src/main/resources folder.
 6. If you work behind a proxy server, you should configure your proxy settings (host and port). Double-click on the server,
 go to "Overview" tab and choose "Open launch configuration". In the tab "(x)= Arguments" > "VM Arguments", paste the following:
 -Dhttp.proxyHost=<yourproxyHost> -Dhttp.proxyPort=<yourProxyPort> -Dhttps.proxyHost=<yourproxyHost> -Dhttps.proxyPort=<yourProxyPort> 
Set your proxy hosts and ports. 
 7. Create local users: Double-click on the created server, go to the "Users" tab and create new users with the properties required. Set a role for each user of yours.
 Role with name "Everyone" is mandatory. If you want to use the admin functionalities, add one more role, named "admin".
 8. Run MongoDB - it is used for the SAP HANA Cloud document service when running it locally. Download MongoDB from here: http://www.mongodb.org/downloads
Save the archive, unpack and execute the following command: mongod --dbpath C:\mongodb_data, where "C:\mongodb_data" is an empty directory.
 9. Run the application.
  
NOTE: When running locally, you will receive the e-mails on your local file system, i.e.  <local server's folder>/work/mailservice/ 
(In the local scenario, you do not need a Mail destination.)
 


* Running on SAP HANA Cloud
 
 1. Go to your computer’s properties > Advanced system settings > Environment Variables, and create a new system variable named "NW\_CLOUD\_SDK\_PATH".
 2. Enter the path to the directory where you have downloaded and unarchived the SAP HANA Cloud SDK. Check the pom.xml file for the SDK version and versions of other JAR files, if needed.
 3. Create a SAP HANA Cloud server.
 4. Double-click on it and choose the "Connectivity" tab.
 5. Create a new destination, named "OpenLibrary" and paste the following URL to the URL field:
 http://www.openlibrary.org or you can simply import the file "OpenLibrary", located in the src/main/resources folder.
 6. To use the e-mail functionalities, you need to create a Mail destination (the app will work properly without it, but you will not receive e-mails). You can find a template in directory src/main/resources,
 named Session.template. Remove the .template extension(!) and fill in the data regarding SMTP, username, password, etc. for you e-mail account. You can then import it in the Eclipse IDE.
 7. Assign your user a specific role: Go to the SAP HANA Cloud cockpit. Choose "Authorizations" (located on the left of the screen), select an application from the combo box and the available roles will appear on the right.
 Assign the desired role for the user.
 8. Only for accounts using HANA DB: you should add extarnal jar {SDK-Location}\repository\plugins\com.sap.core.persistence.osgi.hdb.platform_x.y.z.jar to the build path of the project.
 
 NOTE: you can add this to your pom.xml:
 
   		  <dependency>
	                <groupId>com.sap.core.persistence.osgi</groupId>
	                <artifactId>com.sap.core.persistence.osgi.hdb.platform</artifactId>
	                <version>0.6.11</version>
	      </dependency>
 
 9. Run the application.

Authors
-------

**Ilhan Myumyun**


Copyright and license
---------------------

Copyright 2013 SAP AG

Licensed under the Apache License, Version 2.0
