/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import mymeteocal.entityManager.UserManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author cast13
 */
@RunWith(Arquillian.class)
public class UserManagerIT {
    
    /*@Test
    public void fakeTest() {
        assertTrue(true);
    }*/
   
    @EJB
    UserManager cut;
    
    @PersistenceContext
    EntityManager em;
    
//    @PersistenceContext
//    EntityManager em;

    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
//                .addClass(UserManager.class)
                .addPackage(UserManager.class.getPackage())
                .addPackage(User.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
   
    @Test
    public void UserManagerShouldBeInjected() {
        assertNotNull(cut);
    }
    
     @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }
    
    @Test
    public void newUsersShouldBeValid() {
        User newUser = new User();
        newUser.setEmail("invalidemail");
        newUser.setUsername("test1");
        newUser.setName("test");
        newUser.setSurname("test");
        newUser.setPassword("test");
        try {
            cut.save(newUser);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }
        assertNull(em.find(User.class, "test1"));
    }
    
    @Test
    public void noDuplicates() {
        User newUser = new User();
        newUser.setEmail("test@test.t");
        newUser.setUsername("test");
        newUser.setName("test");
        newUser.setSurname("test");
        newUser.setPassword("test");
        cut.save(newUser);
        //it must be persisted
        assertNotNull(em.find(User.class, "test"));
        try {
            cut.save(newUser);
            assertTrue(false); //must not get there
        } catch (Exception e) {
            assertTrue(true); //must get there
        }
        
    }
    
    @Test
    public void queriesTest(){
        User newUser = new User();
        newUser.setEmail("a@a.a");
        newUser.setUsername("aaa");
        newUser.setName("a");
        newUser.setSurname("a");
        newUser.setPassword("a");
        cut.save(newUser);
        assertNotNull(cut.findByUsername("aaa"));
        assertTrue(cut.findByUsername("aaa").get(0).getName().equals("a"));
    }
}
