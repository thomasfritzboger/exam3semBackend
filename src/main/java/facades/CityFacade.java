package facades;

import entities.City;
import entities.Festival;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import java.util.List;

public class CityFacade {
    private static EntityManagerFactory emf;
    private static CityFacade instance;

    private CityFacade() {
    }

    public static CityFacade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CityFacade();
        }
        return instance;
    }

    public List<City> getAllCities() {

        EntityManager em = emf.createEntityManager();
        TypedQuery<City> query = em.createQuery("SELECT c FROM City c",City.class);
        List<City> cities = query.getResultList();
        em.close();
        return cities;
    }

    public City getCityById(int id) {
        EntityManager em = emf.createEntityManager();
        City city = em.find(City.class,id);
        em.close();
        if (city == null) {
            throw new EntityNotFoundException("City with id: "+id+" does not exist in database");
        }
        return city;
    }
}
