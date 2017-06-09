#!/bin/bash

cd ~/github/midas_hand/common
mvn compile && mvn test && mvn install

cd ~/github/midas_hand/spider
mvn compile && mvn test && mvn install

cd ~/github/midas_hand/midas_server
mvn compile && mvn test && mvn install

cd ~/github/midas_hand/midas_client
npm install
