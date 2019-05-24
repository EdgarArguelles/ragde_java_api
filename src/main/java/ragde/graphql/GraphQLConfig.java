package ragde.graphql;

import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ragde.exceptions.handlers.GraphQLExceptionsHandler;
import ragde.graphql.instrumentations.CustomMaxQueryDepthInstrumentation;

/**
 * Configure Custom GraphQL
 */
@Configuration
public class GraphQLConfig {

    private final Integer MAX_DEPTH = 3;

    @Bean
    public GraphQLSchema graphQLSchema(GraphQLSchemaGenerator schemaGenerator) {
        return schemaGenerator.generate();
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema schema, GraphQLExceptionsHandler graphQLExceptionsHandler) {
        return GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(new AsyncExecutionStrategy(graphQLExceptionsHandler))
                .mutationExecutionStrategy(new AsyncSerialExecutionStrategy(graphQLExceptionsHandler))
                .instrumentation(new CustomMaxQueryDepthInstrumentation(MAX_DEPTH))
                .build();
    }
}