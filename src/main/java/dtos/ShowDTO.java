package dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public class ShowDTO {

    private final Integer id;
    private final String name;
    private final Integer duration;
    private final String location;
    private final String start_date;
    private final String start_time;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        return start_date;
    }

    public String getStartTime() {
        return start_time;
    }

    private ShowDTO (Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.duration = builder.duration;
        this.location = builder.location;
        this.start_date = builder.startDate == null ? null : builder.startDate.toString();
        this.start_time = builder.startTime == null ? null : builder.startTime.toString();
    }

    public static class Builder {
        private Integer id;
        private String name;
        private Integer duration;
        private String location;
        private LocalDate startDate;
        private LocalTime startTime;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDuration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ShowDTO build() {
            return new ShowDTO(this);
        }
    }
}
