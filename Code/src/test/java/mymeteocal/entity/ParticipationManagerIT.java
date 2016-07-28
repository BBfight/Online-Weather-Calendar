/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import java.util.Date;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mymeteocal.entityManager.EventManager;
import mymeteocal.entityManager.ParticipationManager;
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
public class ParticipationManagerIT {
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    ParticipationManager cut;
    
    @Inject
    UserManager um;
    
    @Inject
    EventManager eCut;
    
    
    

    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(ParticipationManager.class)
                .addClass(UserManager.class)
                .addClass(EventManager.class)
                .addPackage(Participation.class.getPackage())
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
    public void TestQueries(){
        
        //check there is no exception by findingByUsername
        try{
            assertTrue(cut.findByUsername("aaa").isEmpty());
        }
        catch(Exception e){
            assertTrue(false);
        }
        User newUser = new User();
        newUser.setEmail("a@a.a");
        newUser.setUsername("aaa");
        newUser.setName("a");
        newUser.setSurname("a");
        newUser.setPassword("a");
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
        eCut.save(event);
        
        Participation newPart = new Participation();
        newPart.setEvent(event);
        newPart.setUser(newUser);
        
        cut.save(newPart);
        // it doesn't find "all"
        assertTrue(cut.findByUsername("bbb").isEmpty());
        
        //it does find it
        assertFalse(cut.findByUsername("aaa").isEmpty());
        Participation part0=cut.findByUsername("aaa").get(0);
        //and it's what it's meant to be
        assertTrue(part0.getUser().getUsername().equals("aaa"));
        assertTrue(part0.getEvent().getName().equals("a"));
        
        //getEvents works and does not throw exceptions
        try{
            assertFalse(um.getEvents(em.find(User.class, "aaa")).isEmpty());
        }catch (Exception e){
            assertTrue(false);
        }
        //and it's what it's meant to be
        assertTrue(um.getEvents(em.find(User.class, "aaa")).get(0).getName().equals("a"));
        assertFalse(um.getEvents(em.find(User.class, "aaa")).get(0).getName().equals("b"));
        
        User newUser1 = new User();
        newUser1.setEmail("a@aa.a");
        newUser1.setUsername("bbb");
        newUser1.setName("b");
        newUser1.setSurname("b");
        newUser1.setPassword("b");
        um.save(newUser1);
        
        assertTrue(um.getEvents(em.find(User.class, "bbb")).isEmpty());
    }
}
