package ragde.graphql.instrumentations;

import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationValidationParameters;
import graphql.validation.ValidationError;

import java.util.List;

public class CustomMaxQueryDepthInstrumentation extends MaxQueryDepthInstrumentation {

    public CustomMaxQueryDepthInstrumentation(int maxDepth) {
        super(maxDepth);
    }

    @Override
    public InstrumentationContext<List<ValidationError>> beginValidation(InstrumentationValidationParameters parameters) {
        // GraphQL UI (/gui) send an IntrospectionQuery in order to get DOCS and SCHEMA information,
        // so if operation is IntrospectionQuery, not validate Query Depth limitation
        if (parameters.getOperation() != null && parameters.getOperation().equals("IntrospectionQuery")) {
            return new SimpleInstrumentationContext<>();
        }

        return super.beginValidation(parameters);
    }
}