package rest;

import dtos.CityDTO;
import dtos.FestivalDTO;
import entities.City;
import entities.Festival;
import entities.Show;
import entities.User;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class FestivalResourceTest extends ResourceTestEnvironment {
    private final String BASE_URL = "/festivals/";

    @Test
    void getAllFestivalsTest() {
        User admin = createAndPersistAdminUser();

        Festival festivalB = createAndPersistFestival();

        login(admin);

        given()
                .header("Content-type", ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$",hasSize(2))
                .body("$",hasItem(hasEntry("id",admin.getFestival().getId())))
                .body("$",hasItem(hasEntry("id",festivalB.getId())));
    }

    @Test
    void getAllFestivalsWhenUnAuthorizedTest() {
        User guest = createAndPersistUser();

        login(guest);

        given()
                .header("x-access-token", securityToken)
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }

    @Test
    void getAllFestivalsWhenUnAuthenticatedTest() {

        given()
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode());
    }

    @Test
    public void updateFestivalTest(){
        User admin = createAndPersistAdminUser();
        Festival festival = createAndPersistFestival();
        int newDuration = festival.getDuration() + 1;

        CityDTO cityDTO = new CityDTO.Builder()
                .setId(festival.getCity().getId())
                .build();

        FestivalDTO festivalDTO = new FestivalDTO.Builder()
                .setName(festival.getName())
                .setDuration(newDuration)
                .setStartDate(festival.getStartDate())
                .setCityDTO(cityDTO)
                .build();

        login(admin);

        given()
                .header("Content-type", ContentType.JSON)
                .header("x-access-token",securityToken)
                .body(GSON.toJson(festivalDTO))
                .put(BASE_URL+festival.getId())
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .contentType(ContentType.JSON)
                .body("duration",equalTo(newDuration));

        assertDatabaseHasEntityWith(festival,"duration",newDuration);
    }

    @Test
    public void updateFestivalNewCityTest(){
        User admin = createAndPersistAdminUser();
        Festival festival = createAndPersistFestival();
        City newCity = createAndPersistCity();

        CityDTO cityDTO = new CityDTO.Builder()
                .setId(newCity.getId())
                .build();

        FestivalDTO festivalDTO = new FestivalDTO.Builder()
                .setName(festival.getName())
                .setDuration(festival.getDuration())
                .setStartDate(festival.getStartDate())
                .setCityDTO(cityDTO)
                .build();

        login(admin);

        given()
                .header("Content-type", ContentType.JSON)
                .header("x-access-token",securityToken)
                .body(GSON.toJson(festivalDTO))
                .put(BASE_URL+festival.getId())
                .then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .contentType(ContentType.JSON)
                .body("city.id",equalTo(newCity.getId()));

        assertDatabaseHasEntitiesRelated(festival, newCity);
    }

    @Test
    public void updateFestivalWhenUnauthenticatedTest() {
        given()
                .put(BASE_URL+1)
                .then()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode());
    }

    @Test
    void updateFestivalWhenUnAuthorizedTest() {
        User user = createAndPersistUser();
        Festival festival = createAndPersistFestival();

        login(user);

        given()
                .header("x-access-token", securityToken)
                .when()
                .put(BASE_URL + festival.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }

    @Test
    public void updateNonExistingFestivalTest() {
        User admin = createAndPersistAdminUser();

        login(admin);

        given()
                .header("x-access-token", securityToken)
                .put(BASE_URL + nonExistingId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }


    @Test
    public void updateFestivalNonExistingCityTest(){
        User admin = createAndPersistAdminUser();
        Festival festival = createAndPersistFestival();

        CityDTO cityDTO = new CityDTO.Builder()
                .setId(nonExistingId)
                .build();

        FestivalDTO festivalDTO = new FestivalDTO.Builder()
                .setName(festival.getName())
                .setDuration(festival.getDuration())
                .setStartDate(festival.getStartDate())
                .setCityDTO(cityDTO)
                .build();

        login(admin);

        given()
                .header("Content-type", ContentType.JSON)
                .header("x-access-token",securityToken)
                .body(GSON.toJson(festivalDTO))
                .put(BASE_URL+festival.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
    }
}
