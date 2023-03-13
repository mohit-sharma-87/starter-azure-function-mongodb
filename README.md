# How to use Azure functions with MongoDB Atlas in Java

Cloud computing is one of the most discussed topics in the tech industry. Its ability to scale up and down infrastructure instantly, serverless
apps are just a few benefits to start with. In this article, we are going write the function as a service(FaaS)e i.e. serverless function that 
would interact with data via a database to produce meaningful results. FaaS can be also very useful in A/B testing where you want to 
release quickly an independent function without going into actual implementation or release.     

> In this article we will learn how to use [MongoDB atlas](https://www.mongodb.com/atlas/database), a cloud database,
> when you are getting started with [Azure functions](https://learn.microsoft.com/en-us/azure/azure-functions/functions-overview)
> in Java.

## Prerequisites

1. [Microsoft Azure](https://azure.microsoft.com/en-us) account that we will be
   using for running and deploying our serverless function, if you don't have one you can sign up for free.
2. [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register) account which is a cloud based document database, and you can sign up for an account
for free. 
3. [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/)  to aid our development
   activities for this tutorial. If this is not your preferred IDE then you can use other IDEs like Eclipse, Visual Studio, etc., but the steps will be slightly different.
4. An
   [Azure supported Java Development Kit (JDK)](https://learn.microsoft.com/en-us/azure/developer/java/fundamentals/java-support-on-azure)
   for Java, version 8 or 11.
5. Basic understanding of Java programming language.

## Serverless function: Hello World!

Getting started with the Azure serverless function is very simple, thanks to the Azure IntelliJ plugin which offers various features from generating
boilerplate code to the deployment of the Azure function. So before we jump into actual code let's install the plugin.

### Installing the Azure plugin

Azure plugin can be installed on IntelliJ in a very standard manner using the IntelliJ plugin
manager. So open Plugins and then search for "_[Azure Toolkit for IntelliJ](https://plugins.jetbrains.com/plugin/8053-azure-toolkit-for-intellij)_" in the Marketplace
and click Install.

![IntelliJ Plugin](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/marketplace_ecdaa3a97e.png)

With this, we are ready to create our first Azure function.

### First Azure function

Now let's create a project that would contain our function and have the necessary
dependencies to execute it. Go ahead and select File > New > Project from the menu bar and select Azure
functions from Generators as shown below and hit next.

![New Project Wizard](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_Function_Create_new_Project_2_7149cbb6b5.png)

Now we can edit the project details if needed, or you can leave them to default.

![New Project Wizard Azure function](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_Function_Create_new_Project_2_de20e0e545.png)

In the last step, update name of the project and location. 

![New Project Wizard Create](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_Function_Create_new_Project_3_c997b16796.png)

With this complete, we have a bootstrapped project with a sample function implementation. So without
further ado let's run this and see it in action.

![Project structure](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Project_Structure_8b2e29a477.png)

### Deploying & running

We can deploy the Azure function either locally or on the cloud, let's start by deploying it locally. To deploy and run locally press the play icon
against the function name, on line 20 as shown in the above screenshot, and select run from the dialogue.

![Hello World output console](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_function_Runing_output_773708fed1.png)

Now go ahead and copy the URL shown in the console log and open it in the browser to run the azure function.

![Hello World output error](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_Function_Hello_Output_2_ece7024552.png)

This would prompt passing the name as a query parameter as defined in the bootstrapped function.

```java
if (name == null) {
    return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
        .body("Please pass a name on the query string or in the request body").build();
} else {
    return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
}
```

So update the URL by appending the query parameter `name` to
`http://localhost:XXXXX/api/HttpExample?name=World` which would print the desired result.

![Hello World output](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_function_Hello_World_output_3_9fb2d86cb0.png)

To learn more in detail you can also follow this official
[guide](https://learn.microsoft.com/en-us/azure/azure-functions/functions-create-maven-intellij).

## Connecting serverless function with MongoDB Atlas

In the previous step, we created our first Azure function which takes user input and returns a result but real-world 
applications are far more complicated than this. In order to create a real-world function, which we would do in the next section, we need to 
understand how to connect our function with a database, as logic operates over data and databases hold the data.

Similar to serverless function, let's use a database which is also on the cloud and has the ability to scale up and down with the needs. Therefore,
we would be using [MongoDB Atlas](https://www.mongodb.com/atlas/database) which is a document-based cloud database.

### Setting up Atlas account

Creating an [Atlas account](https://www.mongodb.com/cloud/atlas/register) is very straightforward, free forever and perfect to validate
any MVP project idea, but if you need a guide you can follow this [documentation](https://www.mongodb.com/docs/atlas/getting-started/).

### Adding Azure function IP address in Atlas Network Config

Azure function uses multiple IP addresses instead of single address, so let's add these to Atlas. To get the range of IP address open your
[Azure account](https://azure.microsoft.com/en-us/free/) and search networking inside your Azure Virtual machine and copy the Outbound addresses from
Outbound traffic.

One of the steps while creating an account with [Atlas](https://www.mongodb.com/atlas/database) is to add the IP address for accepting incoming 
connection requests. This is essential to prevent unwanted access to our database. In our case, Atlas would get all the connection requests from the
Azure function so let's add this addresses.

![Azure IP address](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_IP_addres_cf5a3f9897.png)

And add these to IP individually under Network Access. 

![MongoDB IP Allow List](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Whitelist_IP_address_900cd90939.png)

### Installing dependency to interact with Atlas

There are various ways of interacting with Atlas, since we are building a service using a serverless function in Java my preference would be to use
[MongoDB Java driver](https://www.mongodb.com/docs/drivers/java-drivers/). So let's add the dependency for the driver in the `build.gradle` file.

```groovy
dependencies {
    implementation 'com.microsoft.azure.functions:azure-functions-java-library:3.0.0'
    // dependency for MongoDB Java driver
    implementation 'org.mongodb:mongodb-driver-sync:4.9.0'
}
```

With this, our project is ready to connect and interact with our cloud database.

## Building an Azure function with Atlas

With all prerequisites done, let us build our first real-world function using the [MongoDB sample
dataset](https://www.mongodb.com/docs/atlas/sample-data/)for movies. In this project, we would be building two functions one returns the count of the
total movies in the collection and the other would return the movie document based on the year of release.

So let's generate the boilerplate code for the function by right-clicking on the package name
and then selecting New > Azure function class, we would be calling this function class as `Movies`.

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

Now let us update the

1. `@FunctionName` parameter from `Movies` to `getMoviesCount`.
2. Rename the function name from `run` to `getMoviesCount`.
3. Remove the `query` & `name` variables as we don't have any query parameters.

So our update code looks like this.

```java
public class Movies {

    @FunctionName("getMoviesCount")
    public HttpResponseMessage getMoviesCount(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        return request.createResponseBuilder(HttpStatus.OK).body("Hello").build();
    }
}
```

Now to connect with MongoDB Atlas using Java driver we first need a connection string that can be found when we press to connect to our cluster on our
[Atlas account](https://account.mongodb.com/account/login?_ga=2.197766374.89042088.1678230173-713092659.1675961706), for details you can also refer 
to this [documentation](https://www.mongodb.com/docs/guides/atlas/connection-string/).

![Screenshot connection URL](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Connection_URI_fd9907e2bd.png)

Using the connection string we can create an instance of `MongoClients` that can be used to open connection
from the `database`.

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
    
    /*@FunctionName("getMoviesCount")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        return request.createResponseBuilder(HttpStatus.OK).body("Hello").build();
    }*/
}
```

We can query our database for the total number of movies in the collection as shown below.

```java
long totalRecords=database.getCollection(COLLECTION_NAME).countDocuments();
```

And updated code for `getMoviesCount` the function looks like this.

```java
@FunctionName("getMoviesCount")
public HttpResponseMessage getMoviesCount(
      @HttpTrigger(name = "req",
              methods = {HttpMethod.GET},
              authLevel = AuthorizationLevel.ANONYMOUS
      ) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

  if (database != null) {
      long totalRecords = database.getCollection(COLLECTION_NAME).countDocuments();
      return request.createResponseBuilder(HttpStatus.OK).body("Total Records, " + totalRecords + " - At:" + System.currentTimeMillis()).build();
  } else {
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
```

Now let's deploy this code locally and on the cloud to validate the output and would be using
[Postman](https://www.postman.com/).

![local deployment](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_function_get_Movie_Count_Build_ce2554944c.png)

Now copy the URL from the console output and paste it on the postman to validate the output.

![local postman output](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Auzre_function_Get_Movie_Postman_fa39cbf603.png)

Now let's deploy this on Azure cloud on a `Linux` machine. So click on `Azure Explore` and select Functions App to 
create a Virtual machine (VM).

![Azure explore](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Cloud_deploy_1_4bc140b0a5.png)

Now right-click on the Azure function and select create.

![create new VM](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_2_d51c469b0f.png)

Now change the platform to `Linux` with `Java 1.8`

![create vm with linux](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deply_3_c60bba1b7c.png)

After a few minutes, you would notice the VM we just created under `Function App`, now we can deploy
our app onto it.

![vm deploy](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_4_9a01d01ac1.png)

And press run to deploy it.

![vm deploy](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_5_5e4417c283.png)

Once deployment is successful you find the `URL` of the serverless function.

![cloud deployment success](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_6_153e7c5ba4.png)

Again we would copy this `URL` and validate using postman.

![cloud postman validation](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/cloud_deploy_7_408716b6a0.png)

With this we have successfully connected our first function with
[MongoDB Atlas](https://www.mongodb.com/atlas/database). Now lets take to next level, we would
create another function that returns a movie document based on the year of release. 

so let's add the boilerplate code again  

```java
@FunctionName("getMoviesByYear")
public HttpResponseMessage getMoviesByYear(
      @HttpTrigger(name = "req",
              methods = {HttpMethod.GET},
              authLevel = AuthorizationLevel.ANONYMOUS
      ) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

}
```
Now to capture user input year that would be used to query and gather information from the collection. 

```java
final int yearRequestParam = valueOf(request.getQueryParameters().get("year"));
```
To use this information for querying, we create a `Filters` object that can passed as input for `find` function.   

```java
Bson filter = Filters.eq("year", yearRequestParam);
Document result = collection.find(filter).first();
```
And the updated code is 

```java
@FunctionName("getMoviesByYear")
public HttpResponseMessage getMoviesByYear(
      @HttpTrigger(name = "req",
              methods = {HttpMethod.GET},
              authLevel = AuthorizationLevel.ANONYMOUS
      ) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

  final int yearRequestParam = valueOf(request.getQueryParameters().get("year"));
  MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

  if (database != null) {
      Bson filter = Filters.eq("year", yearRequestParam);
      Document result = collection.find(filter).first();
      return request.createResponseBuilder(HttpStatus.OK).body(result.toJson()).build();
  } else {
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Year missing").build();
  }
}
```

Now lets validate this against postman. 

![get movies by year output](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Screenshot_2023_03_07_at_22_54_16_81d42caa18.png)

Last step in making our app production ready is to secure the connection `URI`, as it contain credentials and should be kept private.  One of ways 
of securing it could be storing this into environment variable. 

Adding environment variable in Azure function can be done via Azure portal and Azure IntelliJ plugin as well. For now, we would be using Azure 
IntelliJ Plugin, so go ahead and open Azure Explore in IntelliJ. 

![Azure Explore](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_Explore_1_c80c9dadc0.png)

And then we select `Function App` and after right click select `Show Properties`.

![Azure Explore VM property](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_Explore_2_231c7f95b8.png)

This would open a tab with all existing properties, we add our property into it.   

![Azure Explore](https://mongodb-devhub-cms.s3.us-west-1.amazonaws.com/Azure_Explore_3_c6fe2182b9.png)

Now we can update our function code to use this variable. 

From 

```java
private static final String MONGODB_CONNECTION_URI = "mongodb+srv://xxxxx:xxxx@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority";
```
to 

```java
private static final String MONGODB_CONNECTION_URI = System.getenv("MongoDB_Connection_URL");
```

After redeploying the code, we are all set to use this app in production. 

## Summary
Thank you for reading, hopefully you find this article informative! The complete source code of
the app can be found on [GitHub](https://github.com/mongodb-developer/starter-azure-function-mongodb).

If you're looking for something similar using the Node.js runtime, check out this other
[tutorial](https://techcommunity.microsoft.com/t5/apps-on-azure-blog/getting-started-with-mongodb-atlas-and-azure-functions-using/ba-p/3662099) on the
subject.

With MongoDB Atlas on Microsoft Azure , developers receive access to the most comprehensive, secure, scalable, and cloud–based developer data platform
in the market. Now, with the availability of Atlas on the Azure Marketplace, it’s never been easier for users to start building with Atlas while
streamlining procurement and billing processes. Get started today through the Atlas on Azure Marketplace listing.

If you have any queries or comments, you can share them on the [MongoDB forum](https://www.mongodb.com/community/forums/) 
or tweet me [@codeWithMohit](https://twitter.com/codeWithMohit).