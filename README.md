# Project 1 - Custom Object Relational Mapping Framework

## Description

Your first project will be to create a custom object relational mapping (ORM) framework. This framework will allow for a simplified and SQL-free interaction with the relational data source. The requirements of the project are purposefully vague, the intention is to allow for you to be creative in your implementation of this framework. There are many ways that this task can be approached, and you are encouraged to explore existing Java ORM implementations in order to get some inspiration. The minimum requirement for the custom ORM is to abstract JDBC boilerplate logic from the application which uses it.

Additionally, you will need to build a simple CRUD web application (what objects you CRUD is up to you). You should leverage the Java EE Servlet API to expose endpoints that allow for interaction with the application.

## Tech Stack

- Java 8
- JUnit
- Mockito
- Apache Maven
- Jackson library (for JSON marshalling/unmarshalling)
- Java EE Servlet API (v4.0+)
- PostGreSQL deployed on AWS RDS
- Git SCM (on GitHub)

## Functional Requirements

- CRUD operations are supported for one or more domain objects via the web application's exposed endpoints
- JDBC logic is abstracted away by the custom ORM
- Programmatic persistence of entities (basic CRUD support) using custom ORM
- File-based or programmatic configuration of entities

## Non-Functional Requirements

- 80% line coverage of all service layer classes
- Generated Jacoco reports that display coverage metrics
- Usage of the java.util.Stream API within your project
- Custom ORM source code should be included within the web application as a Maven dependency

## Bonus Features

- Deployment of web application to AWS EC2 (use of AWS Elastic Beanstalk is permitted)

## Presentation

- Finalized version of custom ORM and web application must be pushed to personal repository within this organization by the presentation date (November 24th, 2021)
- 10-15 minute live demonstration of the web application (that leverages your custom ORM); demonstration will be performed using PostMan to query your API's endpoints

## API for Project
https://github.com/JennicaLeClerc/Project1_WebApp.git
