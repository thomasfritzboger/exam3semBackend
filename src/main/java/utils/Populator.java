/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import entities.*;
import errorhandling.InvalidPasswordException;
import errorhandling.InvalidUsernameException;
import errorhandling.UniqueException;
import facades.UserFacade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

public class Populator {

    //Fails quietly
    public static void populateWithInitialData(EntityManagerFactory emf) throws UniqueException, InvalidUsernameException, InvalidPasswordException {
        EntityManager em = emf.createEntityManager();

        City roskilde = new City("Roskilde",4000);
        City ronne = new City("Rønne",3700);

        Festival roskildeFestival = new Festival("Roskilde Festival", LocalDate.of(2023, Month.JUNE, 24),8,roskilde);
        Festival roskildeDyrskue = new Festival("Roskilde Dyrskue", LocalDate.of(2023, Month.JUNE, 2),3,roskilde);
        Festival wonderFestiwall = new Festival("WonderFestiwall", LocalDate.of(2023, Month.AUGUST, 17),4,ronne);

        Show showA = new Show("WaterShow",120,"Lalandia",LocalDate.of(2023, Month.JUNE, 14), LocalTime.of(12,00));
        Show showB = new Show("FireShow",60,"Tivoli",LocalDate.of(2023, Month.MAY, 9), LocalTime.of(21,00));
        Show showC = new Show("WindShow",120,"Fanø",LocalDate.of(2023, Month.OCTOBER, 18), LocalTime.of(10,00));
        Show showD = new Show("EarthShow",90,"Ærø",LocalDate.of(2023, Month.APRIL, 29), LocalTime.of(16,15));

        try {
            em.getTransaction().begin();
            em.persist(roskilde);
            em.persist(ronne);
            em.persist(roskildeFestival);
            em.persist(roskildeDyrskue);
            em.persist(wonderFestiwall);
            em.persist(showA);
            em.persist(showB);
            em.persist(showC);
            em.persist(showD);
            em.getTransaction().commit();
        }finally {
            em.close();
        }

        UserFacade userFacade = UserFacade.getFacade(emf);
        Role guestRole = persistRole("guest",emf);
        Role adminRole = persistRole("admin",emf);

        User user = new User("test123", "guest", "Hans Nielsen",30303030,"hn@mail.dk",roskildeFestival);
        User admin = new User("test123", "admin", "Anders Hansen",40404040,"ah@mail.dk",roskildeFestival);

        user.addRole(guestRole);
        admin.addRole(adminRole);

        userFacade.createUser(user);
        userFacade.createUser(admin);
    }

    private static Role persistRole(String roleString, EntityManagerFactory emf) {
        Role role = new Role(roleString);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(role);
        em.getTransaction().commit();

        return role;
    }
}
