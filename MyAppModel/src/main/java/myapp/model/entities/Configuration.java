package myapp.model.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "configurations")
public class Configuration implements Identifiable<Integer>{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    //todo: add what describes your configuration here

    public Configuration() {
    }

    @Override
    public Integer getIdentifier() {
        return getId();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
