/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import java.io.Serializable;

/**
 *
 * @author cast13
 */
public interface Notification extends Serializable{
    public User getUser();
    public void setUser(User username);
    public Event getEvent();
    public void setEvent(Event idEvent);
    public Boolean getIsVisualized();
    public void setIsVisualized(Boolean isVisualized);
    
}
