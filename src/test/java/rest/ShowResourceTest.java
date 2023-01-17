package rest;

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
    void getAllShowTest() {
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
    void getAllShowsWhenUnAuthorizedTest() {
        User admin = createAndPersistAdminUser();

        login(admin);

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

}
