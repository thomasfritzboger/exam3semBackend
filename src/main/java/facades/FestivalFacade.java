package facades;

import entities.Festival;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;

public class FestivalFacade {
    private static EntityManagerFactory emf;
    private static FestivalFacade instance;

    private FestivalFacade() {
    }

    public static FestivalFacade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FestivalFacade();
        }
        return instance;
    }

    public Festival getFestivalById(int id) {
        EntityManager em = emf.createEntityManager();
        Festival festival = em.find(Festival.class,id);
        em.close();
        if (festival == null) {
            throw new EntityNotFoundException("Festival with id: "+id+" does not exist in database");
        }
        return festival;
    }

}
