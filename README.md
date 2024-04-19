# Distributed Systems Project in Java

This project is a Java application that implements a distributed system for HTML page analysis. Currently, the project has two main features:

## 1. Mini HTTP Client using Threads and Sockets

The mini HTTP client implemented in this project allows making requests to web servers using the HTTP protocol. It was developed using threads and sockets to support multiple connections efficiently.

## 2. HTML Analysis and Interpretation of Specific Tags

The application has a functionality to analyze HTML pages and interpret their parts separately, especially the `<link>`, `<img>`, and `<script>` tags. To achieve this goal, the system uses threads, referred to as "network threads" in the context of the project, to identify and process these tags asynchronously and in parallel.

These features enable the distributed system to interact with web servers, make HTTP requests, and analyze the content of HTML pages efficiently and scalably.

## How to Use

To use these features, simply follow the installation and execution instructions described in the `README.md` file of the project. Make sure to have the Java environment configured correctly and all dependencies installed before running the application.


