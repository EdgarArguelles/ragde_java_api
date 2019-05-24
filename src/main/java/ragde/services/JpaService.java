package ragde.services;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ragde.models.Model;
import ragde.pojos.pages.PageDataRequest;

import javax.validation.Valid;
import java.util.List;

/**
 * Basic operations for all JPA services
 */
@Validated
public interface JpaService<T extends Model> {

    /**
     * Retrieves all entities.
     *
     * @return list of entities.
     */
    List<T> findAll();

    /**
     * Retrieves an entity by its id.
     *
     * @param id value to search.
     * @return the entity with the given id or null if none found.
     */
    T findById(String id);

    /**
     * Create an entity.
     *
     * @param entity entity to be created.
     * @return the entity created.
     */
    T save(@Valid T entity);

    /**
     * Update an entity.
     *
     * @param entity entity to be updated.
     * @return the entity updated.
     */
    T update(@Valid T entity);

    /**
     * Delete an entity.
     *
     * @param id entity id to be deleted.
     * @return the entity that was deleted.
     */
    T delete(String id);

    /**
     * Retrieves all requested entities.
     *
     * @param pageDataRequest Page data.
     * @return list of entities with metadata.
     */
    Page<T> page(@Valid PageDataRequest pageDataRequest);
}