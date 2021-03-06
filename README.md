# OMTD-STORE #

omtd-store is the Store Service of OpenMinTeD; it includes the following (Maven) projects.	

  * omtd-store-api: It contains the API definition of the service (`eu.openminted.store.core.StoreService`) and two implementations of it; one that is based on local hard drive and one that uses GRNET's [PITHOS](https://okeanos.grnet.gr/services/pithos/) cloud service.
  
  * omtd-store-rest: A REST API for the Store Service.

  * omtd-store-rest-client: A REST client for omtd-store-rest.

  * omtd-store-common that includes some classes that are used in omtd-store-rest and omtd-store-rest-client.   

## Installation ##

**Step 1**: The implementation of the service for PITHOS cloud storage is based on the respective JAVA REST-client; the latter  is available at GitHub (https://github.com/grnet/e-science/tree/master/pithosfs) as a part of "pithosFS Connector for Hadoop" project. Install the respective artifact
as follows 

TO-DO: Download from...

TO-DO: Type

```
...
``` 
   
**Step 2**: Clone omtd-store by typing
  
```
git clone <repoURL>
```


**Step 3**: CD to omtd-store directory that has been created. For building the projects type

```
mvn clean install -DstoreApplicationCfg=<applicationConfigFilePath>
```


storeApplicationCfg parameter specifies for which configuration/implementation the JUnit tests will run. For example the commands below build the project and run the tests for Local hard drive and PITHOS cloud storage respectively. 
  
```
mvn clean install -DstoreApplicationCfg=file:$(pwd)/scripts/configLocal.properties
mvn clean install -DstoreApplicationCfg=file:$(pwd)/scripts/configPITHOS.properties
```  

In order to skip JUnit tests just type 

```
mvn clean install -DskipTests=true.
```

## Run ##

Scripts for starting the REST Server & Client in Windows and Linux are provided in omtd-store/scripts folder. 
For example in a Linux machine CD to scripts folder and type the following command to start the REST server that uses the local hard drive.  

```
LinuxStartOMTDStoreService.sh configLocal.properties
```

Then open a new terminal and type  

```
LinuxStartOMTDStoreClient.sh
```

to start the command line client of the service.

  
## Install it as an init.d service ##

CD to omtd-store/scripts folder and run 

```
installAsInit.dService.sh <configFileName>
```

Choose one of the configuration files that are available in scripts folder and adapt it
if required. 

e.g
```
installAsInit.dService.sh configLocal.properties
OR
installAsInit.dService.sh configPITHOS.properties
```
 
Then start service by running  

```
service omtdstore start
```

For stopping, restarting and getting the status of the service similar commands are also available.

```
service omtdstore {stop|restart|status} 
```

## Dockerization ##

The REST server of omtd-store has been dockerized so that it can be easily deployed and managed.
The `omtd-store-createDockerImgForStoreApp.sh` script that is provided takes as input the required 
docker file (`omtd-store.dockerfile`) and generates the image of the store (named `omtd-store-docker`) that will run in a docker container.
The docker image contains all the required software dependencies and installs the REST server as an init.d service.
The implementation that will be used (Local HD or PITHOS) and the required settings are specified in the respective configuration file (as described above).
In order to update this configuration file (before starting the container) use the following command from the host that runs the docker deamon.

```
omtd-store-updateConfigOfStoreAppInDockerContainer.sh <configFilePath>
```
e.g.

```
omtd-store-updateConfigOfStoreAppInDockerContainer.sh ./scripts/configLocal.properties
```

For more information on managing the store service image and container please consult the `omtd-store-docker.NOTES` file.  




## omtd-store-rest-client ##

In order to integrate the Store Service to your JAVA application use  the following Maven coordinates(`omtd-store-rest-client`).

```
  <groupId>eu.openminted</groupId>
  <artifactId>omtd-store-rest-client</artifactId>
  <version>VERSION</version>  
``` 
where `VERSION` is the current version of the library (e.g. 0.0.1-SNAPSHOT).
 


 


