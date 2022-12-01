package myapp.model.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements Identifiable<String>{

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    @Override
    public String getIdentifier() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null)
            return false;
        if(!(obj instanceof User))
            return false;
        return Objects.equals(username,((User) obj).getUsername());
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
