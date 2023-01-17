package facades;

import TestEnvironment.TestEnvironment;
import entities.User;
import errorhandling.InvalidUsernameException;

import errorhandling.UniqueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class UserFacadeTest extends TestEnvironment {
    private static UserFacade facade;

    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        TestEnvironment.setUpClass();
        facade = UserFacade.getFacade(emf);
    }

    @Test
    public void createUserTest() throws Exception {
        User user = createUser();

        assertNull(user.getId());

        user = facade.createUser(user);

        assertNotNull(user.getId());
    }

    @Test
    public void createUserWithNullUsernameTest() {
        User user = createUser();
        user.setUsername(null);
        assertThrows(InvalidUsernameException.class, () -> facade.createUser(user));
    }

    @Test
    public void createUserWithEmptyUsernameTest() {
        User user = createUser();
        user.setUsername("");
        assertThrows(InvalidUsernameException.class, () -> facade.createUser(user));
    }

    @Test
    public void createUserWithUsernameLengthBelowMinimumLengthTest() {
        User user = createUser();
        user.setUsername(faker.letterify("??")); //two random characters
        assertThrows(InvalidUsernameException.class, () -> facade.createUser(user));
    }

    @Test
    public void createUserWithUsernameLengthAboveMaximumLengthTest() {
        User user = createUser();
        user.setUsername(faker.letterify("?????????????????????"));
        assertThrows(InvalidUsernameException.class, () -> facade.createUser(user));
    }

    @Test
    public void createUserWithUsernameLengthExactlyMinimumLengthTest() {
        User user = createUser();
        user.setUsername(faker.letterify("???"));
        assertDoesNotThrow(() -> facade.createUser(user));
    }

    @Test
    public void createUserWithUsernameLengthExactlyMaximumLengthTest() {
        User user = createUser();
        user.setUsername(faker.letterify("????????????????????"));
        assertDoesNotThrow(() -> facade.createUser(user));
    }

    @Test
    public void createUserWithUsernameThatAlreadyExistTest() {
        User userA = createAndPersistUser();
        User userB = createUser();
        userB.setUsername(userA.getUsername());

        assertThrows(UniqueException.class,
                () -> facade.createUser(userB));
    }

    @Test
    public void createUserWhenUserIsNullTest() {
        assertThrows(NullPointerException.class, () -> facade.createUser(null));
    }

    @Test
    public void updateUsernameWhenItAlreadyIsInUseTest() {
        User userA = createAndPersistUser();
        User userB = createAndPersistUser();
        userA.setUsername(userB.getUsername());

        assertThrows(UniqueException.class,()-> facade.updateUser(userA));
    }

    @Test
    public void updateUsernameWhenEmptyTest() {
        User user = createAndPersistUser();
        user.setUsername("");

        assertThrows(InvalidUsernameException.class,()-> facade.updateUser(user));
    }

    @Test
    public void updateUsernameWhenNullTest() {
        User user = createAndPersistUser();
        user.setUsername("");

        assertThrows(InvalidUsernameException.class,()-> facade.updateUser(user));
    }

    @Test
    public void updateUsernameWhenUnderMinimumLengthTest() {
        User user = createAndPersistUser();
        user.setUsername(faker.letterify("??"));

        assertThrows(InvalidUsernameException.class,()-> facade.updateUser(user));
    }

    @Test
    public void updateUsernameWhenOverMaximumLengthTest() {
        User user = createAndPersistUser();
        user.setUsername(faker.letterify("?????????????????????"));

        assertThrows(InvalidUsernameException.class,()-> facade.updateUser(user));
    }

    @Test
    public void getUserByIdTest() {
        User expected = createAndPersistUser();

        User actual = facade.getUserById(expected.getId());

        assertEquals(expected.getId(),actual.getId());
    }

    @Test
    public void getUserByNonExistingIdTest() {
        assertThrows(EntityNotFoundException.class,()-> facade.getUserById(nonExistingId));
    }

   @Test
   public void getAllUsersTest() {
        User userA = createAndPersistUser();

        List<User> actual = facade.getAllUsers();

        assertTrue(actual.contains(userA));

   }

}
