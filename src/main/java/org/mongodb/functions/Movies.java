package org.mongodb.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Optional;

import static java.lang.Integer.valueOf;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Movies {
    /**
     * This function listens at endpoint "/api/GetMovies". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/GetMovies
     * 2. curl {your host}/api/GetMovies?name=HTTP%20Query
     */

    private static final String MONGODB_CONNECTION_URI = System.getenv("MONGODB_URI");
    private static final String DATABASE_NAME = "sample_mflix";
    private static final String COLLECTION_NAME = "movies";
    private static MongoDatabase database = null;


    public Movies() {
        //Quick fix for Window VM in Azure.
        System.setProperty("java.naming.provider.url", "dns://8.8.8.8");
        createDatabaseConnection();
    }

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
}
