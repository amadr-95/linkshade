package de.linkshade.repositories;

import de.linkshade.domain.entities.User;

public interface UserWithUrlCount {
    User getUser();
    Long getUrlCount();
}
