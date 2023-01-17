package facades;

import entities.Show;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
}
