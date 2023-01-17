package entities;

import errorhandling.InvalidPasswordException;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements entities.Entity {

    public static final int MINIMUM_PASSWORD_LENGTH = 4;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 25)
    @NotNull
    @Column(name = "username", nullable = false, length = 25, unique = true)
    private String username;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "phone", nullable = false)
    private Integer phone;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "festivals_id", nullable = false)
    private Festival festival;

    @ManyToMany
    @JoinTable(name = "shows_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "show_id"))
    private Set<Show> shows = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    public List<String> getRolesAsStringList() {
        List<String> rolesAsStrings = new ArrayList<>();
        roles.forEach((role) -> {
            rolesAsStrings.add(role.getRole());
        });
        return rolesAsStrings;
    }

    public User() {}

    public User(String password, String username, String name, Integer phone, String email, Festival festivals) throws InvalidPasswordException {
        this.password = validateAndHashPassword(password);
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.festival = festivals;
    }
    public boolean verifyPassword(String pw){
        return BCrypt.checkpw(pw, password);
    }

    private String validateAndHashPassword(String password) throws InvalidPasswordException {
        //check if empty or null?
        if(password.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new InvalidPasswordException("Password is too short");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festivals) {
        this.festival = festivals;
    }

    public Set<Show> getShows() {
        return shows;
    }

    public void setShows(Set<Show> shows) {
        this.shows = shows;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeAllRoles() {
        roles = new LinkedHashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    private void addRole(String role) {

    }
}