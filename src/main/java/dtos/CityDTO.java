package dtos;

public class CityDTO {
    private final Integer id;
    private final String name;
    private final Integer zipcode;

    private CityDTO (Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.zipcode = builder.zipcode;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getZipcode() {
        return zipcode;
    }
    public static class Builder {
        private Integer id;
        private String name;
        private Integer zipcode;

        public Builder() {
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setZipcode(Integer zipcode) {
            this.zipcode = zipcode;
            return this;
        }

        public CityDTO build() {
            return new CityDTO(this);
        }
    }
}
