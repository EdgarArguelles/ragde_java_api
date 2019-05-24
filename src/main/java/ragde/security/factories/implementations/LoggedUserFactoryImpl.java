package ragde.security.factories.implementations;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ragde.exceptions.RagdeValidationException;
import ragde.models.Person;
import ragde.models.Role;
import ragde.security.factories.LoggedUserFactory;
import ragde.security.pojos.LoggedUser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoggedUserFactoryImpl implements LoggedUserFactory {

    @Override
    public LoggedUser loggedUser(Person person) {
        return loggedUser(person, null);
    }

    @Override
    public LoggedUser loggedUser(Person person, String roleId) {
        return loggedUser(person, roleId, null);
    }

    @Override
    public LoggedUser loggedUser(Person person, String roleId, String image) {
        if (person == null) {
            throw new RagdeValidationException("User doesn't have personal information associated.");
        }
        if (ObjectUtils.isEmpty(person.getRoles())) {
            throw new RagdeValidationException("User doesn't have Roles associated.");
        }

        Role role;
        if (roleId != null) {
            List<Role> selectedRoles = person.getRoles().stream().filter(r -> r.getId().equals(roleId)).collect(Collectors.toList());
            if (selectedRoles.isEmpty()) {
                throw new RagdeValidationException("User doesn't have the requested Role.");
            }
            role = selectedRoles.get(0);
        } else {
            role = person.getRoles().iterator().next();
        }

        LoggedUser loggedUser = new LoggedUser(person.getId(), person.getFullName(), image, role.getId(), Collections.emptySet());
        if (role.getPermissions() != null) {
            loggedUser.setPermissions(role.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toSet()));
        }

        return loggedUser;
    }
}