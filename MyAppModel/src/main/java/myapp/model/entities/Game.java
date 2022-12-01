package myapp.model.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class Game implements Identifiable<Integer>, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "configuration_id", nullable = false)
    private Configuration configuration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_username", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn(name="idx", nullable=false)
    @JoinColumn(name = "game_id", nullable=false)
    private List<Round> rounds;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    public Game() {
    }

    @Override
    public Integer getIdentifier() {
        return id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = new ArrayList<>(rounds);
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getStartDateAsString() {
        return Timestamp.valueOf(startDate).toString();
    }

    public void setStartDateAsString(String startDate) {
        this.startDate = Timestamp.valueOf(startDate).toLocalDateTime();
    }

    public Integer getCurrentScore() {
        //todo: write logic for getting score
        return 0;
    }

    public boolean isFinished() {
        //todo: write logic for determining if game is finished - this is a placeholder
        return rounds.size() == 3;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", configuration=" + configuration +
                ", user=" + user +
                ", rounds=" + rounds +
                ", startDate=" + startDate +
                '}';
    }
}
