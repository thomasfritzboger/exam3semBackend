package facades;

import entities.Festival;
import entities.Show;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import java.util.List;

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

    public List<Festival> getAllFestivals() {

        EntityManager em = emf.createEntityManager();
        TypedQuery<Festival> query = em.createQuery("SELECT f FROM Festival f",Festival.class);
        List<Festival> festivals = query.getResultList();
        em.close();
        return festivals;
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

    public void updateFestival(Festival festival) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(festival);
            em.getTransaction().commit();
        }  finally {
            em.close();
        }
    }
}
