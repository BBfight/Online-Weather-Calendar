/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author cast13
 */
@Entity(name = "PARTICIPATION")
@NamedQueries({
    @NamedQuery(name="Participation.findByIdEvent",
            query= "SELECT p FROM PARTICIPATION p WHERE p.id.idEvent = :idEvent"
    ),@NamedQuery(name="Participation.findByUsername",
            query= "SELECT p FROM PARTICIPATION p WHERE p.id.username = :username"
    )
})
public class Participation implements Serializable {

    
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private ParticipationPK id;
    
    @ManyToOne
    @MapsId("username")
    private User user;
    
    @ManyToOne
    @MapsId("idEvent")
    private Event event;
    
    /**
     * 
     * @return the composite id of the class
     */
    public ParticipationPK getId() {
        return id;
    }
    
    /**
     * 
     * @param id 
     */
    public void setId(ParticipationPK id) {
        this.id = id;
    }
    
    /**
     * 
     * @return the user regarding the participation
     */
    public User getUser() {
        return user;
    }
    
    /**
     * 
     * @param user 
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * 
     * @return the event regarding the participation
     */
    public Event getEvent() {
        return event;
    }
    
    /**
     * 
     * @param event 
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Participation other = (Participation) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    
    
    
}
