package entities;

import TestEnvironment.TestEnvironment;
import errorhandling.InvalidPasswordException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest extends TestEnvironment {

    @Test
    public void createUserWithPasswordBelowMinimumLengthTest() {
        Festival festival = createAndPersistFestival();

        assertThrows(InvalidPasswordException.class, () -> {
            new User(
                   faker.letterify("???"),
                    faker.name().username(),
                    faker.name().fullName(),
                    Integer.parseInt(faker.number().digits(8)),
                    faker.internet().emailAddress(),
                    festival
            );
        });
    }

    @Test
    public void createUserPasswordWithExactlyMinimumLengthTest() {
        Festival festival = createAndPersistFestival();

        assertDoesNotThrow(() -> {
            new User(
                    faker.letterify("????"),
                    faker.name().username(),
                    faker.name().fullName(),
                    Integer.parseInt(faker.number().digits(8)),
                    faker.internet().emailAddress(),
                    festival
            );
        });
    }
}
