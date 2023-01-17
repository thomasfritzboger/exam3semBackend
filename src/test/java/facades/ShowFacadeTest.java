package facades;

import TestEnvironment.TestEnvironment;
import entities.Show;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShowFacadeTest extends TestEnvironment {
    private static ShowFacade facade;

    public ShowFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        TestEnvironment.setUpClass();
        facade = ShowFacade.getFacade(emf);
    }

    @Test
    public void getAllShowTest() {
        Show showA = createAndPersistShow();
        Show showB = createAndPersistShow();

        List<Show> actual = facade.getAllShows();

        assertEquals(2,actual.size());
        assertTrue(actual.contains(showA));
        assertTrue(actual.contains(showB));
    }

    @Test
    public void getAllShowsWhenNoShowsTest() {
        List<Show> actual = facade.getAllShows();
        assertEquals(0,actual.size());
    }

}
