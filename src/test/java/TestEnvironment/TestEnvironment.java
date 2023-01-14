package TestEnvironment;

import com.github.javafaker.Faker;
import entities.*;
import entities.Entity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.EMF_Creator;

import javax.persistence.*;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class TestEnvironment {
    protected int nonExistingId;
    protected static Faker faker = Faker.instance(new Locale("da-DK"));
    protected static Role userRole;
    protected static Role adminRole;
    protected static EntityManagerFactory emf;

    protected static final String password = "test123";

    @BeforeEach
    void setup() {
        nonExistingId = faker.random().nextInt(-100, 0);

        resetDatabase();
    }

    private void resetDatabase() {
        truncateDatabase();
        populateDatabase();
    }

    private void truncateDatabase() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private void populateDatabase() {
        userRole = new Role("user");
        adminRole = new Role("admin");
        persist(userRole);
        persist(adminRole);
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
    }

    protected Entity persist(Entity entity) {
        return runInTransaction(em -> {
            em.persist(entity);
            return entity;
        });
    }

    protected Entity update(Entity entity) {
            return runInTransaction(em -> em.merge(entity));
    }


    //Tager en lambda funktion som parameter
    private Entity runInTransaction(Transactionable transactionable) {
        EntityManager em = emf.createEntityManager();
        Entity entity;
        try {
            em.getTransaction().begin();
            //Kører lambda funktionen
            entity = transactionable.transact(em);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return entity;
    }

    protected User createAndPersistAdminUser() {
        User user = createAdminUser();
        return (User) persist(user);
    }

    protected User createAdminUser() {
        try {
            User user = new User(
                    faker.letterify("?????"),
                    password,
                    faker.number().numberBetween(13, 120)
            );
            user.addRole(adminRole);
            return user;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return null;
    }

    protected User createAndPersistUser() {
        User user = createUser();
        return (User) persist(user);
    }

    protected User createUser() {
        try {
            User user = new User(
                    faker.letterify("?????"),
                    password,
                    faker.number().numberBetween(13, 120)
            );
            user.addRole(userRole);
            return user;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return null;
    }
    protected Role createAndPersistRole() {
        Role role = createRole();
        return (Role) persist(role);
    }

    protected Role createRole() {
        return new Role(faker.letterify("????"));
    }

    protected void assertDatabaseHasEntity(Entity entity, int id) {
        EntityManager em = emf.createEntityManager();
        try {
            entity = em.find(entity.getClass(), id);
            assertNotNull(entity, "Entity: " + entity.getClass()+" with id: " + id + " does not exist..");
        } finally {
            em.close();
        }
    }

    protected void assertDatabaseDoesNotHaveEntity(Entity entity, int id) {
        EntityManager em = emf.createEntityManager();
        try {
            Entity foundEntity = em.find(entity.getClass(), id);
            assertNull(foundEntity, "Entity: " + entity.getClass()+" with id: " + id + " does exist!");
        } finally {
            em.close();
        }
    }

    protected void assertDatabaseHasEntityWith(Entity persistedEntity, String property, Object value) {
        assertDatabaseHasEntity(persistedEntity,persistedEntity.getId());

        assertDatabaseHasPropertyAndValue(persistedEntity,property,value);
    }

    protected void assertDatabaseHasPropertyAndValue(Entity persistedEntity, String property, Object value) {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Entity> query = em.createQuery(
                "SELECT e FROM " + persistedEntity.getClass().getSimpleName()
                        + " e WHERE e." + property + " = :value " +
                        "AND e.id =:id",Entity.class);

        query.setParameter("value",value);
        query.setParameter("id",persistedEntity.getId());

        Entity entity = null;
        try {
            entity = query.getSingleResult();
        } catch (Exception e) {
            //
        }finally {
            em.close();
        }

        assertNotNull(entity, persistedEntity.getClass().getSimpleName()+" does not have "+
                property+" with value "+value+" in the database");
    }

//
//    protected void assertDatabaseHasEntityWith(Entity persistedEntity, String property, int value) {
//        assertDatabaseHasEntity(persistedEntity,persistedEntity.getId());
//
//        assertDatabaseHasPropertyAndValue(persistedEntity,property,value);
//    }
//
//    protected void assertDatabaseHasPropertyAndValue(Entity persistedEntity, String property, int value) {
//        EntityManager em = emf.createEntityManager();
//
//        TypedQuery<Entity> query = em.createQuery(
//                "SELECT e FROM " + persistedEntity.getClass().getSimpleName()
//                        + " e WHERE e." + property + " = :value " +
//                        "AND e.id =:id",Entity.class);
//
//        query.setParameter("value",value);
//        query.setParameter("id",persistedEntity.getId());
//
//        Entity entity = null;
//        try {
//            entity = query.getSingleResult();
//        } catch (Exception e) {
//            //
//        }finally {
//            em.close();
//        }
//
//        assertNotNull(entity, persistedEntity.getClass().getSimpleName()+" does not have "+
//                property+" with value "+value+" in the database");
//    }

    protected void assertDatabaseHasEntityRelatedToPerson(Entity persistedEntity, int personId) {
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT count(entity) FROM " + persistedEntity.getClass().getSimpleName()
                +"  entity WHERE entity.person.id "+  "=  " + personId);

        Long amount = (Long) query.getSingleResult();

        assertNotEquals(0,amount);
    }

    protected void assertDatabaseDoesNotHaveEntityRelatedToPerson(Entity persistedEntity, int personId) {
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT count(entity) FROM " + persistedEntity.getClass().getSimpleName()
                +"  entity WHERE entity.person.id "+  "=  " + personId);

        Long amount = (Long) query.getSingleResult();

        assertEquals(0,amount);
    }
}
