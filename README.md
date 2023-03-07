# How to use Azure function with MongoDB Atlas in Java

> In this article we learn how to use MongoDB atlas when you are getting started with Azure
> function in Java.

## Introduction

## Prerequisite

1. [Microsoft Azure](https://azure.microsoft.com/en-us) account that we will be
   using for running and deploying our serverless function, if you don't have you can set it up for
   free.
2. [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register) account, if you don't
   have you can set it up for free. //TODO: Add utm params for content
3. [IntelliJ IDE](https://www.jetbrains.com/idea/download/#section=mac) to aid our development
   activities.
4. An
   [Azure supported Java Development Kit (JDK)](https://learn.microsoft.com/en-us/azure/developer/java/fundamentals/java-support-on-azure)
   for Java, version 8 or 11.
5. Basic understand of JAVA programming language.

## Serverless function : Hello World!

Getting started with Azure serverless function is very simple, thanks to the Azure IntelliJ
plugin which offers various feature from generating boilerplate code to deployment of Azure
function. So before we jump into actual code lets intsall the plugin.

### Installing the Azure plugin

Azure plugin can be installed on IntelliJ in a very standard manner using IntelliJ plugin
manager. So open Plugins and then search for "_Azure Toolkit for IntelliJ_" in the Marketplace
and click Install.

![IntelliJ Plugin](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/marketplace_ecdaa3a97e.png)

With this we are ready to create our first Azure function.

### First Azure function

Now let's create a project where were we can write our function and also have the necessary
dependencies to run it. So select File > New > Project from the menu bar and select Azure
functions from Generators as shown below and press next.

![New Project Wizard](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_Function_Create_new_Project_2_7149cbb6b5.png)

Now we can edit the project details if needed, or you can leave them to default.

![New Project Wizard Azure function](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_Function_Create_new_Project_2_de20e0e545.png)

And in the last step we define name of the project, and it's location.

![New Project Wizard Create](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_Function_Create_new_Project_3_c997b16796.png)

With this complete, we have a bootstrapped project with sample function implementation so which
further ado let run this and see this in action.

![Project structure](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Project_Structure_8b2e29a477.png)

### Deploying & running

We can deploy Azure function either locally or on cloud, for now let's start by deploying
it on local. To deploy and run locally press play icon against the function name, on line 20
as shown in above screenshot, and select run from the dialog.

![Hello World output console](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_function_Runing_output_773708fed1.png)

Now go ahead and copy the URL shown in console log and open in browser to run the azure function.

![Hello World output error](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_Function_Hello_Output_2_ece7024552.png)

This would prompt for passing name as query parameter as defined in the bootstrapped function.

```kotlin
if (name == null) {
    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
        .body("Please pass a name on the query string or in the request body").build();
} else {
    return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
}
```

So update URL by appending the query parameter `name` to
`http://localhost:XXXXX/api/HttpExample?name=World` which would print the desired result.

![Hello World output](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_function_Hello_World_output_3_9fb2d86cb0.png)

To learn more in detail you can also follow this official
[guide](https://learn.microsoft.com/en-us/azure/azure-functions/functions-create-maven-intellij).

## Connecting serverless function with MongoDB Atlas

In the previous step we created our first Azure function which take a user input and print it
on screen but real world application far complicate than this. In order to create a real world
function which we would do in the next section we need to understand how to connect our
function with a database, as logic operate over data and databases hold the data.

Similar to serverless function, let use a database which is on cloud has the ability scale up
and down with the needs. Therefore, we would be using [MongoDB Atlas]() which is a document based
cloud database.

### Setting up Atlas account

Creating an [Atlas account]() is very straightforward, which is free forever and perfect to validate
any MVP project idea, but if you need a guide you can follow this [documentation]().

### Whitelisting Azure function IP address in Atlas

One of the step while creating an account with Atlas is to whitelist the IP address for incoming
connection request, this is essential as it prevent unwanted access to our database. In our case
Atlas would get all the connect request from Azure function so let whitelist those IP addresses.

Azure function uses a range of IP addresses instead of single address for outbound request, so
let's add this range to Atlas. To get the range of IP address open your [Azure account]() and
search networking inside your Azure Virtual machine and copy the Outbound addresses from
Outbound traffic.

//TODO screeenshot

### Installing dependency to interact with Atlas

There are various ways of connecting/interacting with Atlas, since we are building a service
using serverless function in Java my preference would be use [MongoDB Java driver](). So let add
the dependency for the driver in the `build.gradle` file.

```groovy
dependencies {
    implementation 'com.microsoft.azure.functions:azure-functions-java-library:3.0.0'
    // dependency for MongoDB Java driver
    implementation 'org.mongodb:mongodb-driver-sync:4.9.0'
}
```

With this our project is ready to connect and interact with our cloud database.

## Building an Azure function with Atlas

With all prerequisite done, lets build our first real world function using MongoDB sample
dataset for movies. In this project we would be building two functions one return count of
total document in the collection and other would return movies based on the year.

So let's generate the boilerplate code for the function by right-clicking on the package name
and then select New > Azure function class, we would be calling this function class as Movies.

```java
public class Movies {
    /**
     * This function listens at endpoint "/api/Movie". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/Movie
     * 2. curl {your host}/api/Movie?name=HTTP%20Query
     */
    @FunctionName("Movies")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
}
```

Now lets update the

1. `@FunctionName` parameter from `Movies` to `getMoviesCount`.
2. Rename the function name from `run` to `getMoviesCount`.
3. Remove the `query` & `name` arguments as in this request we don't need any parameters.

So our update code looks like this.

```java
public class Movies {

    @FunctionName("getMoviesCount")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        return request.createResponseBuilder(HttpStatus.OK).body("Hello").build();
    }
}
```

Now to connect with MongoDB Atlas using Java driver we first need a connection string that can
found when we press connect to our cluster on our [Atlas account](), you can also refer to this
[documentation](https://www.mongodb.com/docs/guides/atlas/connection-string/). Using this
connection string we can create an instance of `MongoClients` which can be used to open connection
with the database.

```java

public class Movies {

    private static final String MONGODB_CONNECTION_URI = "mongodb+srv://xxxxx@cluster0.xxxx.mongodb.net/?retryWrites=true&w=majority";
    private static final String DATABASE_NAME = "sample_mflix";
    private static final String COLLECTION_NAME = "movies";
    private static MongoDatabase database = null;

    private static MongoDatabase createDatabaseConnection() {
        if (database == null) {
            try {
                MongoClient client = MongoClients.create(MONGODB_CONNECTION_URI);
                database = client.getDatabase(DATABASE_NAME);
            } catch (Exception e) {
                throw new IllegalStateException("Error in creating MongoDB client");
            }
        }
        return database;
    }


    @FunctionName("getMoviesCount")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        return request.createResponseBuilder(HttpStatus.OK).body("Hello").build();
    }
}
```

Now we can query our database to count total number of movies in the collection as shown below.

```java
long totalRecords=database.getCollection(COLLECTION_NAME).countDocuments();
```

And updated code for `getMoviesCount` function look like this.

```java
@FunctionName("GetMoviesCount")
public HttpResponseMessage getMoviesCount(
@HttpTrigger(name = "req",
        methods = {HttpMethod.GET},
        authLevel = AuthorizationLevel.ANONYMOUS
) HttpRequestMessage<Optional<String>>request,
final ExecutionContext context){

        if(database!=null){
        long totalRecords=database.getCollection(COLLECTION_NAME).countDocuments();
        return request.createResponseBuilder(HttpStatus.OK).body("Total Records, "+totalRecords+" - At:"+System.currentTimeMillis()).build();
        }else{
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        }
```

Now let's deploy this code locally and on cloud to validate the output and would be using
[Postman](https://www.postman.com/).

![local deployment](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_function_get_Movie_Count_Build_ce2554944c.png)

Now copy the url from console output and paste it on postman to validate the output.

![local postman output](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_function_Get_Movie_Postman_fa39cbf603.png)

Now let's deploy this on Azure cloud using `Linux` machine. So lets click on `Azure Explore` and
create a Virtual machine (VM).

![Azure explore](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Cloud_deploy_1_4bc140b0a5.png)

Now right click on Azure function and select create.

![create new VM](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_2_d51c469b0f.png)

Now change the platform to `Linux` with `Java 1.8`

![create vm with linux](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deply_3_c60bba1b7c.png)

After few minutes you would notice the VM we just create under `Function App`, now we can deploy
our app onto it.

![vm deploy](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_4_9a01d01ac1.png)

And press run to deploy it.

![vm deploy](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_5_5e4417c283.png)

Once deployment is successful you find the `URL` of the serverless function.

![cloud deployment success](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_6_153e7c5ba4.png)

Again we would copy this `URL` and validate using postman.

![cloud postman validation](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_7_408716b6a0.png)




## Summary