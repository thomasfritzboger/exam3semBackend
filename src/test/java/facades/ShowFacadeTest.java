package facades;

import TestEnvironment.TestEnvironment;
import entities.City;
import entities.Show;
import entities.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void getShowByIdTest() {
       Show expected = createAndPersistShow();

        Show actual = facade.getShowById(expected.getId());

        assertEquals(expected.getId(),actual.getId());
    }

    @Test
    public void getShowByNonExistingIdTest() {
        assertThrows(EntityNotFoundException.class,()-> facade.getShowById(nonExistingId));
    }

    @Test
    public void createShowTest() {
        Show show = createShow();

        Show actual = facade.createShow(show);

        assertDatabaseHasEntity(actual);
    }

    @Test
    public void deleteShowTest(){
        Show show = createAndPersistShow();
        facade.deleteShow(show);
        assertDatabaseDoesNotHaveEntity(show);
    }

    @Test
    public void deleteNonPersistedShowTest(){
        Show notPersisted = createShow();

        assertDoesNotThrow(() -> facade.deleteShow(notPersisted));
    }
}
