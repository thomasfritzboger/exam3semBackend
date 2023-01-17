/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import entities.City;
import entities.Festival;
import entities.Role;
import entities.User;
import errorhandling.InvalidPasswordException;
import errorhandling.InvalidUsernameException;
import errorhandling.UniqueException;
import facades.UserFacade;

import java.time.LocalDate;
import java.time.Month;

public class Populator {

    //Fails quietly
    public static void populateWithInitialData(EntityManagerFactory emf) throws UniqueException, InvalidUsernameException, InvalidPasswordException {
        EntityManager em = emf.createEntityManager();

        City roskilde = new City("Roskilde",4000);

        Festival roskildeFestival = new Festival("Roskilde Festival", LocalDate.of(2023, Month.JUNE, 24),8,roskilde);

        try {
            em.getTransaction().begin();
            em.persist(roskilde);
            em.persist(roskildeFestival);
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
