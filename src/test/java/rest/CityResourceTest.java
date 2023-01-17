package rest;

import entities.City;
import entities.Festival;
import entities.User;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CityResourceTest extends ResourceTestEnvironment {
    private final String BASE_URL = "/cities/";

    @Test
    void getAllCitiesTest() {
        User admin = createAndPersistAdminUser();
        City cityB = createAndPersistCity();

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
                .body("$",hasItem(hasEntry("id",admin.getFestival().getCity().getId())))
                .body("$",hasItem(hasEntry("id",cityB.getId())));
    }

    @Test
    void getAllCitiesWhenUnAuthorizedTest() {
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
    void getAllCitiesWhenUnAuthenticatedTest() {

        given()
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode());
    }
}
