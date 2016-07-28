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
import mymeteocal.entity.Participation;
import mymeteocal.entity.ParticipationPK;

/**
 *
 * @author Ettore
 */
@Stateless
public class ParticipationManager {
    @PersistenceContext
    EntityManager em;
    
    /**
    * persists a participation into the database, it gives exceptions if it
    * fails. Every @NotNull attribute has to be specified
    * @param participation
    */
    public void save(Participation participation) {
        em.persist(participation);
    }
    
    /**
     * deletes the tuple containing that participation from the database
     * @param participation
     */
    public void delete(Participation participation) {
        em.remove(em.merge(participation));
    }
    
    /**
     * it updates the tuple of that participation, it has to be coherent with the old one.
     * It should be utterly useless
     * @param participation 
     */
    @Deprecated
    public void update(Participation participation) {
        em.merge(participation);
    }
    
    /**
     * 
     * @param idEvent
     * @return a list of participations regarding the event having that idEvent
     */
    public List<Participation> findByIdEvent(Long idEvent){
        Query query =
            em.createNamedQuery("Participation.findByIdEvent")
                    .setParameter("idEvent",idEvent);
        return query.getResultList();
    }
    /**
     * 
     * @param username
     * @return a list of participations regarding the User having that username
     */
    public List<Participation> findByUsername(String username){
        /*TypedQuery<Participation> query =
            em.createNamedQuery("Participation.findByUsername", Participation.class)
                    .setParameter("username",username);
        return query.getResultList();*/
        Query query =
            em.createNamedQuery("Participation.findByUsername")
                    .setParameter("username",username);
        return query.getResultList();
    }
    
    /**
     * 
     * @param username
     * @param idEvent
     * @return the participation of that user
     */
    public Participation find(String username, Long idEvent){
        ParticipationPK id= new ParticipationPK();
        id.setUsername(username);
        id.setIdEvent(idEvent);
        return em.find(Participation.class, id);
    }
}
