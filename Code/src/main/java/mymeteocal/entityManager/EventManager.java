/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entityManager;

import java.util.ArrayList;
import java.util.Date;
import mymeteocal.entity.Event;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mymeteocal.entity.Invitation;
import mymeteocal.entity.Participation;
import mymeteocal.entity.User;
import mymeteocal.entity.WeatherNotification;

/**
 *
 * @author Ettore
 */
@Stateless
public class EventManager {
    @PersistenceContext
    EntityManager em;
    
    @EJB
    ParticipationManager pm;
    
    @EJB
    InvitationManager im;
    
    @EJB
    WeatherNotificationManager wm;
    /**
    * persists an event into the database, it gives exceptions if it
    * fails. Every @NotNull attribute has to be specified
    * @param event 
    */
    public void save(Event event) {
        em.persist(event);
//        em.refresh(event);
//        return event.getIdEvent();
    }
    
    /**
     * deletes the tuple containing that event from the database
     * @param event 
     */
    public void delete(Event event) {
        Event temp= em.merge(event);
        deleteParticipations(temp);
        deleteInvitations(temp);
        deleteWeatherNotifications(temp);
        em.remove(temp);
    }
    
    /**
     * it updates the tuple of that event, it has to be coherent with the old one.
     * @param event 
     */
    public void update(Event event){
        em.merge(event);
    }
    
    /**
     * 
     * @param idEvent
     * @return a List containing at most one Event, having that.idEvent= idEvent
     */
    public List<Event> findByIdEvent(Long idEvent){
        Query query =
            em.createNamedQuery("Event.findByIdEvent")
                    .setParameter("idEvent",idEvent);
        return query.getResultList();
    }
    
    /**
     * 
     * @param creator
     * @return a List containing all the Events created by creator
     */
    public List<Event> findByCreator(User creator){
        Query query =
            em.createNamedQuery("Event.findByCreator")
                    .setParameter("creator",creator.getUsername());
        return query.getResultList();
    }
    
    /**
     * It doesn't throw exceptions if the event does not exist.
     * The list returned, given an existing idEvent,
     * may never be empty, as every creator of an event must be
     * a participant aswell
     * @param idEvent
     * @return the list of participants of the event having that idEvent
     */
    public List<User> getParticipants(Long idEvent){
        List<Participation> participations =
                pm.findByIdEvent(idEvent);
        List<User> users = new ArrayList<>();
        
        for (Participation p : participations){
            users.add(p.getUser());
        }
        
        return users;
    }
    
    /**
     * It doesn't throw exceptions if the event does not exist.
     * @param idEvent
     * @return the list of uers invited to the event having that idEvent
     */
    public List<User> getInvitedUsers(Long idEvent){
        List<Invitation> invitations =
                im.findByIdEvent(idEvent);
        List<User> users = new ArrayList<>();
        
        for (Invitation i : invitations){
            users.add(i.getUser());
        }
        
        return users;
    }
    
    /**
     * 
     * @return the outdoor events not started yet
     */
    public List<Event> findNotStarted(){
        Query query =
            em.createNamedQuery("Event.findNotStarted")
                    .setParameter("now",new Date());
        return query.getResultList();
    }

    private void deleteParticipations(Event temp) {
        for(Participation p: pm.findByIdEvent(temp.getIdEvent())){
            pm.delete(p);
        }
    }

    private void deleteInvitations(Event temp) {
        for(Invitation i: im.findByIdEvent(temp.getIdEvent())){
            im.delete(i);
        }
    }

    private void deleteWeatherNotifications(Event temp) {
        for(WeatherNotification w
                : wm.findByIdEvent(temp.getIdEvent())){
            wm.delete(w);
        }
    }
}
