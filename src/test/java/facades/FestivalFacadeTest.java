package facades;

import TestEnvironment.TestEnvironment;
import entities.Festival;
import entities.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FestivalFacadeTest extends TestEnvironment{

    private static FestivalFacade facade;

    public FestivalFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        TestEnvironment.setUpClass();
        facade = FestivalFacade.getFacade(emf);
    }

    @Test
    public void getFestivalByIdTest() {
        Festival expected = createAndPersistFestival();

        Festival actual = facade.getFestivalById(expected.getId());

        assertEquals(expected.getId(),actual.getId());
    }

    @Test
    public void getFestivalByNonExistingIdTest() {
        assertThrows(EntityNotFoundException.class,()-> facade.getFestivalById(nonExistingId));
    }

}
