package ragde.services;

import ragde.models.Authentication;
import ragde.models.Person;

import java.util.List;

public interface PersonService extends JpaService<Person> {

    /**
     * GraphQL function to load Person's Authentications (only needed with mongo or jpa which doesn't implement bi-directional relationship)
     *
     * @param person person where related data is loaded
     * @return Person's Authentications list
     */
    List<Authentication> getAuthentications(Person person);
}