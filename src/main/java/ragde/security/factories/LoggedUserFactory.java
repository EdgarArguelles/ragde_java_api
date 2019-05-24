package ragde.security.factories;

import ragde.models.Person;
import ragde.security.pojos.LoggedUser;

/**
 * Create LoggedUser instances
 */
public interface LoggedUserFactory {

    /**
     * Create a LoggedUser from a person using the first Role associated
     *
     * @param person Person data
     * @return LoggedUser created
     */
    LoggedUser loggedUser(Person person);

    /**
     * Create a LoggedUser from a person and roleId
     *
     * @param person Person data
     * @param roleId role ID
     * @return LoggedUser created
     */
    LoggedUser loggedUser(Person person, String roleId);

    /**
     * Create a LoggedUser from a person, roleId and image
     *
     * @param person Person data
     * @param roleId role ID
     * @param image  image to display
     * @return LoggedUser created
     */
    LoggedUser loggedUser(Person person, String roleId, String image);
}