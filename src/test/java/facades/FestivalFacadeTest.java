package facades;

import TestEnvironment.TestEnvironment;
import entities.City;
import entities.Festival;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void getAllFestivalsTest() {
        Festival festivalA = createAndPersistFestival();
        Festival festivalB = createAndPersistFestival();

        List<Festival> actual = facade.getAllFestivals();

        assertEquals(2,actual.size());
        assertTrue(actual.contains(festivalA));
        assertTrue(actual.contains(festivalB));
    }

    @Test
    public void getAllFestivalsWhenNoFestivalsTest() {
        List<Festival> actual = facade.getAllFestivals();
        assertEquals(0,actual.size());
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

    @Test
    public void updateFestivalTest() {
        Festival festival =  createAndPersistFestival();
        int originalDuration = festival.getDuration();
        festival.setDuration(originalDuration-1);

        facade.updateFestival(festival);

        assertDatabaseHasEntityWith(festival,"duration",originalDuration-1);
    }

    @Test
    public void updateFestivalNewCityTest() {
        Festival festival = createAndPersistFestival();
        City city = createAndPersistCity();

        festival.setCity(city);

        facade.updateFestival(festival);

        assertDatabaseHasEntitiesRelated(festival,city);
    }

}
