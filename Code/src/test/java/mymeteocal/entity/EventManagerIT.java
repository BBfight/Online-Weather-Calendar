/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mymeteocal.entityManager.EventManager;
import mymeteocal.entityManager.UserManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author cast13
 */
@RunWith(Arquillian.class)
public class EventManagerIT {
    
    @EJB
    EventManager cut;
    
    @EJB
    UserManager um;
    
    @PersistenceContext
    EntityManager em;
    
//    @PersistenceContext
//    EntityManager em;

    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(EventManager.class)
                .addClass(UserManager.class)
                .addPackage(Event.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
   
    @Test
    public void EventManagerShouldBeInjected() {
        assertNotNull(cut);
    }
    
     @Test
    public void EntityManagerShouldBeInjected() {
        assertNotNull(em);
    }
    
    @Test
    public void EventIsSaved(){
        User newUser = new User();
        newUser.setEmail("test@test.t");
        newUser.setUsername("test");
        newUser.setName("test");
        newUser.setSurname("test");
        newUser.setPassword("test");
        um.save(newUser);
        
        Event event = new Event();
        event.setCity("a");
        event.setName("a");
        event.setDescription("a");
        event.setCountry("a");
//        event.setStartingDate(new Date());
//        event.setEndingDate(new Date());
        event.setIsPrivate(Boolean.FALSE);
        event.setIsOutdoor(Boolean.FALSE);
        event.setCreator(newUser);
        
        cut.save(event);
        assertTrue(true);
        
    }
}
