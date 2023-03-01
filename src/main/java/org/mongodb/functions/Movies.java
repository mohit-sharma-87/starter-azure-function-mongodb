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

    private static String MONGODB_CONNECTION_URI = System.getenv("MONGODB_URI");
    private static String DATABASE_NAME = "sample_mflix";
    private static String COLLECTION_NAME = "movies";

    @FunctionName("GetMoviesCount")
    public HttpResponseMessage getMoviesCount(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try (MongoClient client = MongoClients.create(MONGODB_CONNECTION_URI)) {
            MongoDatabase database = client.getDatabase(DATABASE_NAME);
            long totalRecords = database.getCollection(COLLECTION_NAME).countDocuments();
            return request.createResponseBuilder(HttpStatus.OK).body("Total Records, " + totalRecords + " - At:" + System.currentTimeMillis()).build();
        } catch (Exception e) {
            context.getLogger().info("Error occurred in Get Movie out" + e.getLocalizedMessage());
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

        try (MongoClient client = MongoClients.create(MONGODB_CONNECTION_URI)) {
            MongoDatabase database = client.getDatabase(DATABASE_NAME);
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

        } catch (Exception e) {
            context.getLogger().info("Error occurred in GetMoviesById" + e.getLocalizedMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
