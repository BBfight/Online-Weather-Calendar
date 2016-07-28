/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *
 * @author cast13
 */
@Embeddable
public class WeatherNotificationPK  implements Serializable{
    //    @Column(name = "username")
    private String username;
//    @Column(name = "idEvent")
    private Long idEvent;
    
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long number;
    
    /**
     * 
     * @return the username of the id
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * 
     * @return the idEvent of the id
     */
    public Long getIdEvent() {
        return idEvent;
    }
    
    /**
     * 
     * @param idEvent 
     */
    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
    }
    
    /**
     * 
     * @return the number of the id
     */
    public Long getNumber() {
        return number;
    }
    
    /**
     * 
     * @param number 
     */
    public void setNumber(Long number) {
        this.number = number;
    }
}
