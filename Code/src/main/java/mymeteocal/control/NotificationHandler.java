/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.control;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;

import mymeteocal.entity.Event;
import mymeteocal.entity.User;
import mymeteocal.entity.WeatherNotification;
import mymeteocal.entityManager.EventManager;
import mymeteocal.entityManager.WeatherNotificationManager;

/**
 *
 * @author Ettore
 */
@Stateless
public class NotificationHandler {

    @EJB
    EventManager evm;

    @EJB
    WeatherNotificationManager wm;
    
    @EJB
    WeatherService ws;
    
    @Inject MailingService mailSender;

    private Logger logger = Logger.getLogger("NotificationHandler");
    
    /**
     * it creates all the weatherNotifications of that event
     *
     * @param e
     */
    public void notifyWeatherChange(Event e, Boolean hasWeatherChanged) {

        logger.log(Level.INFO, "Notifying participants of event " + e.getIdEvent());
        for (User u : evm.getParticipants(e.getIdEvent())) {

            WeatherNotification notification = new WeatherNotification();
            notification.setEvent(e);
            notification.setUser(u);
            notification.setForecast(e.getWeatherForecast());
            notification.setHasWeatherChanged(hasWeatherChanged);
            wm.save(notification);

            
            try {
                // Set the recipient.
                    String to = u.getEmail();

                    // Set the message subject.
                    String subject = "Invitation Notification";

                    // Set the message body.
                    String text = "Dear " + u.getUsername() + "\n\n"
                            + "you've been invited by " + e.getCreator().getUsername()
                            + " to join the event " + e.getName()
                            + " starting on " + e.getStartingDate().toString() + ".\n\n"
                            + "Cheers,\n\n" + "EJB\n";

                    // Send the message.
                    mailSender.sendMail(to, subject, text);
                   
            } catch (MessagingException ex) {
                
                Logger.getLogger(NotificationHandler.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
    }    

    /**
     * notifies the creator only, suggesting him the better day
     * @param event 
     */
    public void notifyCreator(Event event) {
        logger.log(Level.INFO, "Notifying creator of event " + event.getIdEvent());
        WeatherNotification notification = new WeatherNotification();
        notification.setEvent(event);
        notification.setUser(event.getCreator());
        notification.setForecast(event.getWeatherForecast());
        notification.setHasWeatherChanged(Boolean.FALSE);
        
        notification.setBetterDay(ws.getBetterDay(event));
        if(notification.getBetterDay()==null){
            Date strangeDate = new Date();
            strangeDate.setTime(0);
            notification.setBetterDay(strangeDate);
        }
        wm.save(notification);

        try {

            // Set the recipient.
            String to = event.getCreator().getEmail();

            // Set the message subject.
            String subject = "Better Day Notification";

            // Set the message body.
            String text = "Dear " + event.getCreator().getUsername() + "\n\n" +
                          "we think that " + notification.getBetterDay().toString() +
                          " would be a better date to plan your event " + event.getName() + ".\n\n" +
                          "Cheers,\n\n" + "EJB\n";

            // Send the message.
            mailSender.sendMail(to, subject, text);
   
            } catch (MessagingException ex) {
                
                Logger.getLogger(NotificationHandler.class.getName()).log(Level.SEVERE, null, ex);
            }  
        
        
    }

}
