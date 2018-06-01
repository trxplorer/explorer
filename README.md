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


