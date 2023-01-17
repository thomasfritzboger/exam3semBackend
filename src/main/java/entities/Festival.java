package entities;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "festivals")
public class Festival implements entities.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "startdate", nullable = false)
    private LocalDate startdate;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cities_id", nullable = false)
    private City city;

    @OneToMany(mappedBy = "festival")
    private Set<User> users = new LinkedHashSet<>();

    public Festival(String name, LocalDate startdate, Integer duration, City city) {
        this.name = name;
        this.startdate = startdate;
        this.duration = duration;
        this.city = city;
    }

    public Festival() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startdate;
    }

    public void setStartDate(LocalDate startdate) {
        this.startdate = startdate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City cities) {
        this.city = cities;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Festival festival = (Festival) o;
        return id.equals(festival.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}