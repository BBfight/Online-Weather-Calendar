/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entityManager;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mymeteocal.entity.WeatherNotification;

/**
 *
 * @author cast13
 */
@Stateless
public class WeatherNotificationManager {
    @PersistenceContext
    EntityManager em;
    

    public void save(WeatherNotification weNot) {
        weNot.setIsVisualized(Boolean.FALSE);
        em.persist(weNot);
    }

    public void delete(WeatherNotification weNot) {
        em.remove(em.merge(weNot));
    }
    
    public void update(WeatherNotification weNot){
        em.merge(weNot);
    }
    
    public List<WeatherNotification> findByUsername(String username){
        Query query =
            em.createNamedQuery("WeatherNotification.findByUsername")
                    .setParameter("username",username);
        return query.getResultList();
    }
    
    /**
     * 
     * @param idEvent
     * @return a list of weather notifications regarding the event having that idEvent
     */
    public List<WeatherNotification> findByIdEvent(Long idEvent){
        Query query =
            em.createNamedQuery("WeatherNotification.findByIdEvent")
                    .setParameter("idEvent",idEvent);
        return query.getResultList();
    }
    
    /**
     * 
     * @param username
     * @param idEvent
     * @return the list of all weatherNotifications reffering that username and idEvent
     */
    public List<WeatherNotification> findByUsernameAndIdEvent(String username,Long idEvent){
        Query query =
            em.createNamedQuery("WeatherNotification.findByUsernameAndIdEvent")
                    .setParameter("idEvent",idEvent)
                    .setParameter("username",username);
        return query.getResultList();
    }
    
    /**
     * 
     * @return all the out of date weather notifications
     */
    public List<WeatherNotification> findOld(){
        Query query =
            em.createNamedQuery("WeatherNotification.findOld");
        return query.getResultList();
    }
}
