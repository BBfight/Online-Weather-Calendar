/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 *
 * @author cast13
 */
@Entity(name = "INVITATION")
//@IdClass(InvitationId.class)
@NamedQueries({
    @NamedQuery(name="Invitation.findByIdEvent",
            query= "SELECT i FROM INVITATION i WHERE i.id.idEvent = :idEvent"
    ),@NamedQuery(name="Invitation.findByUsername",
            query= "SELECT i FROM INVITATION i WHERE i.id.username = :username"
    ),@NamedQuery(name="Invitation.findOld",
            query= "SELECT i FROM INVITATION i JOIN i.event e WHERE e.startingDate < CURRENT_TIMESTAMP")
})
public class Invitation implements Notification {
    
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private InvitationPK id;
    
    @ManyToOne
    @MapsId("username")
    private User user;
    
    @ManyToOne
    @MapsId("idEvent")
    private Event event;
    
    @NotNull
    private Boolean isVisualized;
    
    @NotNull
    private Boolean isEventChanged;
    
    public Invitation(){
        
    }
    /**
     * 
     * @return the composite id of the class
     */
    public InvitationPK getId() {
        return id;
    }
    
    /**
     * 
     * @param id 
     */
    public void setId(InvitationPK id) {
        this.id = id;
    }
    
    /**
     *
     * @return the user referred to the invitation
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
     * @return the event regarding the invitation
     */
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * 
     * @return true if it is already been visualized by the user
     */
    public Boolean getIsVisualized() {
        return isVisualized;
    }
    
    /**
     * 
     * @param isVisualized 
     */
    public void setIsVisualized(Boolean isVisualized) {
        this.isVisualized = isVisualized;
    }

    public Boolean getIsEventChanged() {
        return isEventChanged;
    }

    public void setIsEventChanged(Boolean isEventChanged) {
        this.isEventChanged = isEventChanged;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.isVisualized);
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
        final Invitation other = (Invitation) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.isVisualized, other.isVisualized)) {
            return false;
        }
        return true;
    }

    
    
}

