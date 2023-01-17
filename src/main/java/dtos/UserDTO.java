package dtos;

import entities.Festival;

import java.util.List;

public class UserDTO {
    private final Integer id;
    private final String username;
    private final String password;

    private final String name;

    private final Integer phone;

    private final String email;

    private final FestivalDTO festival;

    private final List<String> roles;

    //Konstruktør er privat for at sikre indkapsling, det er kun fra den indre klasse 'Builder',
    // hvorfra build() metoden kan kaldes og lave en DTO
    private UserDTO (Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.name = builder.name;
        this.phone = builder.phone;
        this.email = builder.email;
        this.festival = builder.festivalDTO;
        this.roles = builder.roles;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Integer getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public FestivalDTO getFestival() {
        return festival;
    }

    public List<String> getRoles() {
        return roles;
    }

    public static class Builder {
        private Integer id;
        private String username;
        private String password;
        private String name;
        private Integer phone;
        private String email;
        private FestivalDTO festivalDTO;
        private List<String> roles;

        //Denne konstruktør er kun til test, lidt fy fy, hører ikke hjemme i produktionskoden
        public Builder(UserDTO userDTO) {
            this
                    .setId(userDTO.getId())
                    .setUsername(userDTO.getUsername())
                    .setPassword(userDTO.getPassword())
                    .setName(userDTO.getName())
                    .setPhone(userDTO.getPhone())
                    .setEmail(userDTO.getEmail())
                    .setFestivalDTO(userDTO.getFestival())
                    .setRoles(userDTO.getRoles());
        }

        //Fluent pattern bruges til at opbygge en Builder,
        public Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPhone(Integer phone) {
            this.phone = phone;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setFestivalDTO(FestivalDTO festivalDTO) {
            this.festivalDTO = festivalDTO;
            return this;
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setRoles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        //build metoden returnerer en DTO hvis konstruktør tager den opbyggede Builder som parameter
        public UserDTO build() {
            return new UserDTO(this);
        }
    }
}
