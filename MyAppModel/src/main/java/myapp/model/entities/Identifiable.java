package myapp.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public interface Identifiable<ID extends Serializable> extends Serializable {

    @JsonIgnore
    ID getIdentifier();
}