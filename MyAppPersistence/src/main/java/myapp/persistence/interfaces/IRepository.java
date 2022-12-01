package myapp.persistence.interfaces;

import myapp.model.entities.Identifiable;

import java.io.Serializable;
import java.util.Collection;

public interface IRepository<ID extends Serializable, E extends Identifiable<ID>> {

    E create(E entity);

    E read(ID id);

    E update(E entity);

    E delete(ID id);
}
