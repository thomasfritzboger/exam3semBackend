package dtos;

import java.time.LocalDate;

public class FestivalDTO {

    private final Integer id;
    private final String name;
    private final String start_date;
    private final Integer duration;
    private final CityDTO city;

    private FestivalDTO (Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.start_date = builder.startDate == null ? null : builder.startDate.toString();
        this.duration = builder.duration;
        this.city = builder.cityDTO;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return start_date;
    }

    public Integer getDuration() {
        return duration;
    }

    public CityDTO getCity() {
        return city;
    }

    public static class Builder {
        private Integer id;
        private String name;
        private LocalDate startDate;
        private Integer duration;
        private CityDTO cityDTO;

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

        public Builder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setDuration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Builder setCityDTO(CityDTO cityDTO) {
            this.cityDTO = cityDTO;
            return this;
        }

        public FestivalDTO build() {
            return new FestivalDTO(this);
        }
    }
}
