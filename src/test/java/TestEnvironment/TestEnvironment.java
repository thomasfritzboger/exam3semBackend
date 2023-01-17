package TestEnvironment;

import com.github.javafaker.Faker;
import entities.*;
import entities.Entity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.EMF_Creator;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TestEnvironment {
    protected int nonExistingId;
    protected static Faker faker = Faker.instance(new Locale("da-DK"));
    protected static Role guestRole;
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
            em.createQuery("DELETE FROM Festival").executeUpdate();
            em.createQuery("DELETE FROM City").executeUpdate();
            em.createQuery("DELETE FROM Show").executeUpdate();

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private void populateDatabase() {
        guestRole = new Role("guest");
        adminRole = new Role("admin");
        persist(guestRole);
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
        Festival festival = createAndPersistFestival();

        try {
            User user = new User(
                    password,
                    faker.name().username(),
                    faker.name().fullName(),
                    Integer.parseInt(faker.number().digits(8)),
                    faker.internet().emailAddress(),
                    festival
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
        Festival festival = createAndPersistFestival();

        try {
            User user = new User(
                    password,
                    faker.letterify("???"),
                    faker.name().fullName(),
                    Integer.parseInt(faker.number().digits(8)),
                    faker.internet().emailAddress(),
                    festival
            );
            user.addRole(guestRole);
            return user;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return null;
    }

    protected Show createAndPersistShow() {
        Show show = createShow();
        return (Show) persist(show);
    }

    protected Show createShow() {
        LocalTime startTime = LocalTime.of(
                faker.number().numberBetween(0,23),
                faker.number().numberBetween(0,59));

        return new Show(
                faker.name().name(),
                faker.number().numberBetween(10,500),
                faker.address().streetAddress(),
                faker.date().future(500, TimeUnit.DAYS).toInstant().
                        atZone(ZoneId.systemDefault()).toLocalDate(),
                startTime
        );
    }

    protected Festival createAndPersistFestival() {
        Festival festival = createFestival();
        return (Festival) persist(festival);
    }

    protected Festival createFestival() {
        City city = createAndPersistCity();

        return new Festival(
                faker.name().name(),
                faker.date().future(500, TimeUnit.DAYS).toInstant().
                        atZone(ZoneId.systemDefault()).toLocalDate(),
                faker.number().numberBetween(1,50),
                city
        );
    }

    protected City createAndPersistCity() {
        City city = createCity();
        return (City) persist(city);
    }

    protected City createCity() {
        City city = new City(
                faker.address().cityName(),
                Integer.parseInt(faker.number().digits(4))
        );
        return city;
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

    protected void assertDatabaseDoesNotHaveEntity(Entity entity) {
        EntityManager em = emf.createEntityManager();
        try {
            Entity foundEntity = em.find(entity.getClass(), entity.getId());
            assertNull(foundEntity, "Entity: " + entity.getClass()+" with id: " + entity.getId() + " does exist!");
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

    protected void assertDatabaseHasEntitiesRelated(Entity entity, Entity related) {
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT count(entity) FROM " + entity.getClass().getSimpleName()
                +"  entity WHERE entity."+related.getClass().getSimpleName().toLowerCase()+".id = " + related.getId());

        Long amount = (Long) query.getSingleResult();

        assertNotEquals(0,amount);
    }

    protected void assertDatabaseDoesNotHaveEntitiesRelated(Entity entity, Entity notRelated) {
        EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("SELECT count(entity) FROM " + entity.getClass().getSimpleName()
                +"  entity WHERE entity."+notRelated.getClass().getSimpleName().toLowerCase()+".id = " + notRelated.getId());

        Long amount = (Long) query.getSingleResult();

        assertEquals(0,amount);
    }
}
