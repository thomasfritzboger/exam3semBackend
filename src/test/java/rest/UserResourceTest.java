package rest;

import dtos.UserDTO;
import entities.Role;
import entities.User;
import io.restassured.http.ContentType;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserResourceTest extends ResourceTestEnvironment {
    private final String BASE_URL = "/users/";

    @Test
    void createUserTest() {
        UserDTO userDTO = createUserDTO();

        int id = given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(GSON.toJson(userDTO))
                .when()
                .post(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED_201.getStatusCode())
                .contentType(ContentType.JSON)
                .body("username", equalTo(userDTO.getUsername()))
                .body("id", notNullValue())
                .body("name", equalTo(userDTO.getName()))
                .body("phone", equalTo(userDTO.getPhone()))
                .body("email", equalTo(userDTO.getEmail()))
                .body("festival.id", equalTo(userDTO.getFestival().getId()))
                .body("roles",hasSize(1))
                .body("roles",hasItem("guest"))
                .extract().path("id");

        assertDatabaseHasEntity(new User(), id);
    }

    @Test
    void createUserInvalidUsernameTest() {
        UserDTO userDTO = createUserDTO();
        userDTO = new UserDTO.Builder(userDTO)
                .setUsername(faker.letterify("??"))
                .build();

        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(GSON.toJson(userDTO))
                .when()
                .post(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message", notNullValue());
    }

    @Test
    void createUserWithUsernameThatAlreadyExistTest() {
        User existingUser = createAndPersistUser();
        UserDTO userDTO = createUserDTO();
        userDTO = new UserDTO.Builder(userDTO)
                .setUsername(existingUser.getUsername())
                .build();

        given()
                .header("Content-type",ContentType.JSON)
                .and()
                .body(GSON.toJson(userDTO))
                .when()
                .post(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT_409.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message", notNullValue());
    }

    @Test
    void createUserInvalidPasswordTest() {
        UserDTO userDTO = createUserDTO();
        userDTO = new UserDTO.Builder(userDTO)
                .setPassword(faker.letterify("??"))
                .build();

        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(GSON.toJson(userDTO))
                .when()
                .post(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .contentType(ContentType.JSON)
                .body("message",notNullValue());
    }

    @Test
    void getUserTest() {
        User user = createAndPersistUser();

        int id = user.getId();
        login(user);
        given()
            .header("Content-type", ContentType.JSON)
            .header("x-access-token", securityToken)
            .when()
            .get(BASE_URL+"me")
            .then()
            .assertThat()
            .statusCode(HttpStatus.OK_200.getStatusCode())
            .contentType(ContentType.JSON)
            .body("username", equalTo(user.getUsername()))
            .body("name", equalTo(user.getName()))
            .body("phone", equalTo(user.getPhone()))
            .body("email", equalTo(user.getEmail()))
            .body("festival.id", equalTo(user.getFestival().getId()))
            .body("id", equalTo(id))
            .body("roles",hasSize(1))
            .body("roles",hasItem("guest"));
    }

    @Test
    void getAllUsersTest() {
        User admin = createAndPersistUser();
        Role role = new Role("admin");
        admin.addRole(role);
        admin = (User) update(admin);
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
                .body("$",hasItem(hasEntry("username",admin.getUsername())));
    }

    @Test
    void getAllUsersWhenUnauthenticatedTest() {
        given()
                .header("Content-type", ContentType.JSON)
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN_403.getStatusCode());
    }

    @Test
    void getAllUsersWhenUnauthorizedTest() {
        User user = createAndPersistUser();
        login(user);

        given()
                .header("Content-type", ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get(BASE_URL)
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED_401.getStatusCode());
    }
}
