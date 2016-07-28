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
import mymeteocal.entity.Invitation;
import mymeteocal.entity.InvitationPK;

/**
 *
 * @author Ettore
 */
@Stateless
public class InvitationManager {
    @PersistenceContext
    EntityManager em;
    
    /**
    * persists an invitation into the database, it gives exceptions if it
    * fails. Every @NotNull attribute has to be specified
    * @param invitation
    */
    public void save(Invitation invitation) {
        invitation.setIsVisualized(Boolean.FALSE);
        em.persist(invitation);
    }

    /**
     * deletes the tuple containing that invitation from the database
     * @param invitation 
     */
    public void delete(Invitation invitation) {
        em.remove(em.merge(invitation));
    }
    
    /**
     * it updates the tuple of that invitation, it has to be coherent with the old one.
     * The only usage of such is to update the isVisualized flag
     * @param invitation 
     */
    public void update(Invitation invitation){
        em.merge(invitation);
    }
    
    /**
     * 
     * @param idEvent
     * @return a list of invitations regarding the event having that idEvent
     */
    public List<Invitation> findByIdEvent(Long idEvent){
        Query query =
            em.createNamedQuery("Invitation.findByIdEvent")
                    .setParameter("idEvent",idEvent);
        return query.getResultList();
    }
    /**
     * 
     * @param username
     * @return a list of invitations regarding the User having that username
     */
    public List<Invitation> findByUsername(String username){
        Query query =
            em.createNamedQuery("Invitation.findByUsername")
                    .setParameter("username",username);
        return query.getResultList();
    }
    
    /**
     * 
     * @param username
     * @param idEvent
     * @return the invitation regarding that key
     */
    public Invitation find(String username, Long idEvent){
        InvitationPK id= new InvitationPK();
        id.setUsername(username);
        id.setIdEvent(idEvent);
        return em.find(Invitation.class,id);
    }
    
    /**
     * 
     * @return all the out of date Invitations
     */
    public List<Invitation> findOld(){
        Query query =
            em.createNamedQuery("Invitation.findOld");
        return query.getResultList();
    }
}
