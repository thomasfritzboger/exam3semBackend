package facades;

import entities.City;
import entities.Show;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import java.util.List;

public class ShowFacade {

    private static EntityManagerFactory emf;
    private static ShowFacade instance;

    private ShowFacade() {
    }

    public static ShowFacade getFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ShowFacade();
        }
        return instance;
    }

    public List<Show> getAllShows() {

        EntityManager em = emf.createEntityManager();
        TypedQuery<Show> query = em.createQuery("SELECT s FROM Show s",Show.class);
        List<Show> shows = query.getResultList();
        em.close();
        return shows;
    }

    public void deleteShow(Show show) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            show = em.merge(show);
            em.remove(show);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Show getShowById(int id) {
            EntityManager em = emf.createEntityManager();
            Show show = em.find(Show.class,id);
            em.close();
            if (show == null) {
                throw new EntityNotFoundException("Show with id: "+id+" does not exist in database");
            }
            return show;

    }
}
