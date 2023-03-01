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
        System.setProperty("java.naming.provider.url","dns://8.8.8.8");
        createDatabaseConnection();
    }

    private static MongoDatabase createDatabaseConnection() {
        if (database == null) {
            try {
                MongoClient client = MongoClients.create(MONGODB_CONNECTION_URI);
                database = client.getDatabase(DATABASE_NAME);
            } catch (Exception e) {
                return null;
            }
        }
        return database;
    }

    @FunctionName("GetMoviesCount")
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

    @FunctionName("GetMovies")
    public HttpResponseMessage getMoviesById(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        final String query = request.getQueryParameters().get("year");
        final String year = request.getBody().orElse(query);

        if (year != null) {
            Bson filter = Filters.eq("year", Integer.valueOf(year));
            Document result = collection.find(filter).first();
            return request.createResponseBuilder(HttpStatus.OK).body(result.toJson()).build();
        } else {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Year missing").build();
        }
    }
}
