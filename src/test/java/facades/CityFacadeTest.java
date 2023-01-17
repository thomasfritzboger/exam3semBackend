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

public class CityFacadeTest extends TestEnvironment {

    private static CityFacade facade;

    public CityFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        TestEnvironment.setUpClass();
        facade = CityFacade.getFacade(emf);
    }


    @Test
    public void getAllCitiesTest() {
        City cityA = createAndPersistCity();
        City cityB = createAndPersistCity();

        List<City> actual = facade.getAllCities();

        assertEquals(2,actual.size());
        assertTrue(actual.contains(cityA));
        assertTrue(actual.contains(cityB));
    }

    @Test
    public void getAllCitiesWhenNoFestivalsTest() {
        List<City> actual = facade.getAllCities();
        assertEquals(0,actual.size());
    }

    @Test
    public void getCityByIdTest() {
        City expected = createAndPersistCity();

        City actual = facade.getCityById(expected.getId());

        assertEquals(expected.getId(),actual.getId());
    }

    @Test
    public void getCityByNonExistingIdTest() {
        assertThrows(EntityNotFoundException.class,()-> facade.getCityById(nonExistingId));
    }
}
