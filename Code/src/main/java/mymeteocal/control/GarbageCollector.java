/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.control;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import mymeteocal.entity.Invitation;
import mymeteocal.entity.WeatherNotification;
import mymeteocal.entityManager.InvitationManager;
import mymeteocal.entityManager.WeatherNotificationManager;

/**
 * Class created to periodly remove out of date 
 * invitations and weather notifications.
 * 
 * @author J
 */
@Stateless
public class GarbageCollector {

    @EJB
    private InvitationManager im;
    
    @EJB
    private WeatherNotificationManager wnm;
    
    @Schedule(hour = "*/2", persistent = false)
    //@Schedule(minute = "*", hour = "*", persistent = false)
    public void cleanUp() {
        
        // Delete all the invitations out of date.
        removeOldInvitations();
        // Delete all the weather notifications out of date.
        removeOldWeatherNotifications();
    }
    
    private void removeOldInvitations() {
        
        for (Invitation i : im.findOld()) {
            
            im.delete(i);
        }
    }
    
    private void removeOldWeatherNotifications() {
        
        for (WeatherNotification wn : wnm.findOld()) {
            
            wnm.delete(wn);
        }
    }
    
    
}
