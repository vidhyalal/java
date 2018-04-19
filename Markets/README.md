# Server side-test:

This project is a Java console application which scrapes a portion of the Sainsbury’s Groceries website.Test are implemented using Junit.


## Getting Started and installing:

This project can be imported directly as a Maven project.All the dependency needed for the project are handled in the POM. 

## Model:

Berries class is the model.

## Parser:
 
 It does the web scraping for a given url and accesses the webpage elements to return a Json.
 
## Tests:
 The tests are written in Junit.This is achieved by writing cucumber stories and translating them to step definitions.

 There are 3 tests written in the BerriesTest.

 Give, When and Then are used for one of the test case.

## Build the project:
 
 Check out/clone the project.
 mvn clean install
 
## How to run:
 Run using the command java -jar Groceries-0.0.1-SNAPSHOT-jar-with-dependencies.jar

## Reason for using Jsoup to parse the given Html page:
 
 It provides a very convenient API for extracting and manipulating data, using the best of DOM.




