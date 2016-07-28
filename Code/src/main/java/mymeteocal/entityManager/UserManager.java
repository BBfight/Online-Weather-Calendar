/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entityManager;

import mymeteocal.entity.Group;
import mymeteocal.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import mymeteocal.entity.Event;
import mymeteocal.entity.Invitation;
import mymeteocal.entity.Notification;
import mymeteocal.entity.Participation;
import mymeteocal.entity.WeatherNotification;

/**
 *
 * @author Ettore
 */
@Stateless
public class UserManager {

    @PersistenceContext
    EntityManager em;
    
    @EJB
    ParticipationManager pm;
    
    @EJB
    InvitationManager im;
    
    @EJB
    WeatherNotificationManager wm;
    
    @EJB
    EventManager evm;
    
    @Inject
    Principal principal;
    
    private Logger logger = Logger.getLogger("UserManager");

    public void save(User user) {
        user.setGroupName(Group.USERS);
        user.setHasPrivateCalendar(false);
        em.persist(user);
    }
    
    public void update(User user){
        // benny puzza
        em.merge(user);
    }

    public void unregister() {
        User temp=getLoggedUser();
        deleteParticipations(temp);
        deleteInvitations(temp);
        deleteWeatherNotifications(temp);
        deleteEvents(temp);
        em.remove(temp);
    }

    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    
   /* public List<User> findByUsername(String username) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<User> e = cq.from(User.class);
        cq.select(e).where(cb.equal(e.get("username"), username));
        return em.createQuery(cq).getResultList();
    }*/
    /**
     * query to find a user by its username in the database
     * notice that you can also findByUsername by using
     * em.find(User.class,username)
     * @param username to be found
     * @return a list of Users, it can either be empty or have one result
     */
    public List<User> findByUsername(String username){
        TypedQuery<User> query =
            em.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username",username);
        return query.getResultList();
    }
    /**
     * 
     * @return a List containing all the users in the database
     */
    public List<User> findAll(){
        Query query =
            em.createNamedQuery("User.findAll");
        return query.getResultList();
    }
    /**
     * TO BE TESTED
     * @param prefix
     * @return all the users who have a name starting with prefix
     */
    public List<User> findByUsernamePrefix(String prefix){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<User> e = cq.from(User.class);
        cq.select(e).where(cb.like(e.get("username"), prefix+"%"));
        return em.createQuery(cq).getResultList();
    }
    
    public List<User> findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        Root<User> e = cq.from(User.class);
        cq.select(e).where(cb.equal(e.get("email"), email));
        return em.createQuery(cq).getResultList();
    }
    
    public List<Event> getEvents(User username){
        Query query =
            em.createNamedQuery("Participation.findByUsername")
                    .setParameter("username",username.getUsername());
        /*List<Participation> participations =
                new ParticipationManager().findByUsername(username.getUsername());*/
        List<Participation> participations = query.getResultList();
        
        List<Event> events = new ArrayList<>();
        
        for (Participation p : participations){
            events.add(p.getEvent());
        }
        
        return events;
    }
    
    // in this case I think it's useless to get something out of it
    // so you can also just do the findByUsername of InvitationManager
    public List<Invitation> getInvitations(String username){
        List<Invitation> invitations =
                im.findByUsername(username);
        
        return invitations;
    }
    
    // in this case I think it's useless to get something out of it
    // so you can also just do the findByUsername of WeatherNotificationManager
    public List<WeatherNotification> getWeatherNotifications(String username){
        List<WeatherNotification> weatherNotifications =
                wm.findByUsername(username);
        
        return weatherNotifications;
    }
    /**
     * remember to use instanceof to retrieve its class
     * @param username
     * @return a list not sorted of Notifications, first invitations then weather notifications
     */
    public List<Notification> getNotifications(String username){
        List<Notification> notifications= new ArrayList<>();
        List<Invitation> invitations =
                im.findByUsername(username);
        for(Invitation i : invitations){
            notifications.add(i);
        }
        List<WeatherNotification> weatherNotifications =
                wm.findByUsername(username);
        for(WeatherNotification w: weatherNotifications){
            notifications.add(w);
        }
        logger.log(Level.INFO, "There are " + notifications.size() + " notifications to see!");
        return notifications;
    }
    
    public List<Event> getEventsCreatedByUser(User creator){
        return evm.findByCreator(creator);
    }

    private void deleteParticipations(User temp) {
        for(Participation p: pm.findByUsername(temp.getUsername())){
            pm.delete(p);
        }
    }

    private void deleteInvitations(User temp) {
        for(Invitation i: im.findByUsername(temp.getUsername())){
            im.delete(i);
        }
    }

    private void deleteWeatherNotifications(User temp) {
        for(WeatherNotification w: wm.findByUsername(temp.getUsername())){
            wm.delete(w);
        }
    }

    private void deleteEvents(User temp) {
        for(Event e: evm.findByCreator(temp)){
            evm.delete(e);
        }
    }
    
    
}


