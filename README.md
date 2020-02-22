# revolut-test

Simple maven java program that emulates a bank transfer service. using the java.net HttpServer to start a http server and 
implements a subtle REST implementation for API's to manage a bank transfer request. 

You can start the REST server on localhost:8080 by running the main method on the TransferApplication class. which exposes 
the endpoints 
* GET /bookings  ->  REST endpoint to poll bookings from a particular 
* POST /transfer ->  REST endpoint to request a transfer of funds to another account
