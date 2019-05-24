package ragde.repositories.mongo;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import ragde.models.Model;

@Component
public class MongoEventListener extends AbstractMongoEventListener<Model> {

    /**
     * MongoRepository doesn't trigger @PrePersist or @PreUpdate, so this Listener is crested to update CreatedAt and UpdatedAt
     */
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Model> event) {
        Model model = event.getSource();
        if (model.getId() != null) {
            model.updatedAt();
        } else {
            model.createdAt();
        }
    }
}