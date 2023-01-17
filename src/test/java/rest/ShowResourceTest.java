package rest;

import dtos.ShowDTO;
import entities.Show;
import entities.User;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ShowResourceTest extends ResourceTestEnvironment {
    private final String BASE_URL = "/shows/";

    @Test
    void getAllShowsTest() {
        User guest = createAndPersistUser();

        Show showA = createAndPersistShow();
        Show showB = createAndPersistShow();

        login(guest);

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
                .body("$",hasItem(hasEntry("id",showA.getId())))
                .body("$",hasItem(hasEntry("id",showB.getId())));
    }

    @Test
    void getAllShowsNoShowsTest() {
        User guest = createAndPersistUser();

        login(guest);

        given()
                .header("x-access-token", securityToken)
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .contentType(ContentType.JSON)
                .body("$",hasSize(0));
    }

    @Test
    void getAllShowsWhenAdminTest() {
        User admin = createAndPersistAdminUser();

        login(admin);

        given()
                .header("x-access-token", securityToken)
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    void getAllShowsWhenUnAuthorizedTest() {

        User unauthorized = createAndPersistUser();
        unauthorized.removeAllRoles();
        update(unauthorized);

        login(unauthorized);

        given()
                .header("x-access-token", securityToken)
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }

    @Test
    void getAllShowsWhenUnAuthenticatedTest() {

        given()
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode());
    }

    @Test
    public void createShowTest() {
        User admin = createAndPersistAdminUser();

        Show show = createShow();
        ShowDTO showDTO = new ShowDTO.Builder()
                .setName(show.getName())
                .setDuration(show.getDuration())
                .setLocation(show.getLocation())
                .setStartDate(show.getStartDate())
                .setStartTime(show.getStartTime())
                .build();

        login(admin);

        int id = given()
                .header("Content-type", ContentType.JSON)
                .header("x-access-token",securityToken)
                .body(GSON.toJson(showDTO))
                .post(BASE_URL)
                .then()
                .statusCode(HttpStatus.CREATED_201.getStatusCode())
                .contentType(ContentType.JSON)
                .body("id",notNullValue())
                .body("name", equalTo(showDTO.getName()))
                .body("duration",equalTo(showDTO.getDuration()))
                .body("location",equalTo(showDTO.getLocation()))
                .body("start_date",equalTo(showDTO.getStartDate().toString()))
                .body("start_time",equalTo(showDTO.getStartTime().toString()))
                .extract().path("id");

        show.setId(id);
        assertDatabaseHasEntity(show);
    }

    @Test
    public void createShowWhenUnauthenticatedTest() {

        given()
                .header("Content-type", ContentType.JSON)
                .body("")
                .post(BASE_URL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",notNullValue());
    }

    @Test
    public void createShowWhenUnauthorizedTest() {
        User guest = createAndPersistUser();

        login(guest);

        given()
                .header("Content-type", ContentType.JSON)
                .header("x-access-token",securityToken)
                .body("")
                .post(BASE_URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",notNullValue());
    }

    @Test
    public void deleteShowTest(){
        User admin = createAndPersistAdminUser();
        Show show = createAndPersistShow();

        login(admin);

        given()
                .header("x-access-token", securityToken)
                .delete(BASE_URL + show.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT_204.getStatusCode());

        assertDatabaseDoesNotHaveEntity(show);
    }

    @Test
    public void deleteShowWhenNonExistingTest(){
        User admin = createAndPersistAdminUser();

        login(admin);

        given()
                .header("x-access-token", securityToken)
                .delete(BASE_URL + nonExistingId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT_204.getStatusCode());
    }

    @Test
    void deleteShowNotAuthorizedUserTest() {
        User user = createAndPersistUser();
        Show show = createAndPersistShow();

        login(user);

        given()
                .header("x-access-token", securityToken)
                .delete(BASE_URL + show.getId())
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }

    @Test
    void deleteShowWhenUnAuthenticatedTest() {
        Show show = createAndPersistShow();

        given()
                .when()
                .delete(BASE_URL+show.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode());
    }

}
