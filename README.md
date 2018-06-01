# trxplorer
TRXPlorer.io - TRON blockchain explorer

> TRXPlorer.io is a blockchain explorer for TRON 

It is composed of 6 independent modules bundled together to form the explorer

- *tron-cli:* is a simple client to query TRON fullnode / solidity node via GRPC
- *model:* is a generated jooq based model that represents the whole blockchain schema into java classes. This model is generated from the database sql schema. This project is also using flyway in order to operate migrations between differents database patch
- *sync-node:* is a standalone blockchain scanner it uses the previous model to fetch the data from the blockchain and put it into a database
- *search-engine*: is an indexer that puts TX HASH/BLOCK HASH/TOKEN NAME etc ... into a redis instance in order to retrieve these informations easily and quickly from the explorer
- *api-server*: is the api layer that queries the database and provide response into a json format, the api is deployed here: api.trxplorer.io
- *webapp*: the explorer web application that uses the previous module to show the blockchain informations to the users, the application is deployed here: https://www.trxplorer.io

## Build requirements

- maven 3
- jdk 8
- docker (optional see below to skip)

**Build process**

A running instance of docker is required to build the project: the model requires a database in order to generate the java reprensentation of the sql schema.

When building the model: a maven plugin starts a docker container, runs flyway on all the database migrations files (see model/src/main/resources) and outputs the java / jooq sources into model/target/generated-sources

**Skiping docker**

If you don't have docker or don't whant to install it, just checkout a branch with current version of the project + "-gen": 

For ex for version 0.0.1-SNAPSHOT do: git checkout 0.0.1-SNAPSHOT-gen

This branch is automatically generated and pushed by Travis

## Building the project

``` bash
git checkout git@github.com:trxplorer/explorer.git

$ cd explorer
$ mvn install

```

## Running the explorer

In order to run the explorer, you will have to retrieve some data from TRON blockchain explorer first:

**1. Synchronizing the database**

The synchronization node (syncnode module) was built so that it can keep up with TRON high TPS. Multiple instance of the node can be run simultaneously in order to operate and sychronize quickly if necessary.


*Start a mysql instance for example with docker*

``` bash
$ docker run --name mysql -e MYSQL_ROOT_PASSWORD=toto -p 3306:3306 -d mysql:5.7
```

Then create a schema with the name: trxplorer_dev. You don't have to do anything else, when you will run any of the server modules, flyway will take care of creating/migrate the database schema


*To start a single synchronization node run:*

``` bash
$ cd sync-node
$ mvn jooby:run
```


*To start multiple nodes simultaneously*

Firs build the project
``` bash
$ cd sync-node
$ mvn install
```

Then, you will have to assign a node id to each node while running it, by default the node has the id=1. A different port must be set too

For example, this will run two synchronization nodes, that will share the synchronization load

``` bash
$ java -jar ./target/syncnode-<CurrenVersion>.jar dev application.port=8282 node.id=1 
$ java -jar ./target/syncnode-<CurrenVersion>.jar dev application.port=8383 node.id=2
```

**2. Running the api server**

``` bash
$ cd api-server
$ mvn jooby:run
```
The api server is running on http://localhost:8383


**3. Running the search engine (optional, if not present the webapp explorer will fallback on its default basic search engine)**

The search engine is based on redisearch, you can start it as follow:

``` bash
$ docker run --name redisearch -p 6379:6379 redislabs/redisearch:latest redis-server /data/redis.conf --loadmodule "/usr/lib/redis/modules/redisearch.so"
```

Then run the search engine:

``` bash
$ mvn jooby:run
```

The search engine will start indexing the data and wil also server the search requests from the explorer

**4. Running the webapp (the explorer)**

``` bash
$ cd webapp
$ mvn jooby:run
```

It will start the explorer on http://localhost:8080
 







