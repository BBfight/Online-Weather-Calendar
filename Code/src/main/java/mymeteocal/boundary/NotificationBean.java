/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import mymeteocal.control.EventDateController;
import mymeteocal.control.WeatherService;
import mymeteocal.entity.Event;
import mymeteocal.entity.Invitation;
import mymeteocal.entity.Notification;
import mymeteocal.entity.Participation;
import mymeteocal.entity.User;
import mymeteocal.entity.WeatherNotification;
import mymeteocal.entityManager.InvitationManager;
import mymeteocal.entityManager.ParticipationManager;
import mymeteocal.entityManager.UserManager;
import mymeteocal.entityManager.WeatherNotificationManager;

/**
 *
 * @author Benedetto
 */
@Named
@SessionScoped
public class NotificationBean implements Serializable {

    @Inject
    private EventBean eb;

    @Inject
    private SearchHandlerBean shb;

    @Inject
    private WeatherService ws;

    @EJB
    private UserManager um;

    @EJB
    WeatherNotificationManager wnm;

    @EJB
    private ParticipationManager pm;

    @EJB
    private InvitationManager im;

    private Logger logger = Logger.getLogger("NotificationsLogger");

    private int lastValue = 0;
    private User user;
    private List<Notification> notifications;
    private int unseen;

    public NotificationBean() {
    }

    /**
     * method that sets all the user notifications as seen and initialise the
     * list
     */
    public void init() {
        notifications = um.getNotifications(user.getUsername());
        for (Notification n : notifications) {
            n.setIsVisualized(Boolean.TRUE);
            if (n instanceof WeatherNotification) {
                wnm.update((WeatherNotification) n);
            } else {
                im.update((Invitation) n);
            }
        }
        lastValue = 0;
    }

    /**
     * method to get the forecast of a weather notification
     * @param n
     * @return 
     */
    public String getForecast(Notification n){
        return ((WeatherNotification)n).getForecast();
    }
    
    /**
     * method to check which message to show according to the type of
     * notification
     *
     * @param n, the relative notification
     * @return
     */
    public String setMessage(Notification n) {
        if (n instanceof Invitation) {
            if (((Invitation) n).getIsEventChanged()) {
                return "One of the events you were attending has been modified, here are the details:";
            } else {
                return "You have been invited to an event, here are the details:";
            }
        } else {
            if (((WeatherNotification) n).getHasWeatherChanged()) {
                if (ws.hasBadWeather(n.getEvent())) {
                    return "Unfortunately it's forecast bad weather for your event, here are the details";
                }
                return "The Weather Forecast for one of the events you are going to "
                        + "attend has changed, here are the details";
            }
        }
        return null;
    }

    /**
     * method that counts the unseen notification
     */
    public void checkUnseen() {
        user = um.getLoggedUser();
        int count = 0;
        notifications = um.getNotifications(user.getUsername());
        for (Notification n : notifications) {
            if (!n.getIsVisualized()) {
                count++;
            }
        }
        unseen = count;
        if (unseen != 0 && unseen != lastValue) {

            lastValue = unseen;
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "You have new notifications, click on relative button to see them!", null));

            logger.log(Level.INFO, "You have new notifications, click on relative button to see them!");

        }
    }

    /**
     * check whether the user can accept or not a certain invitation due to
     * overlapping
     *
     * @param i
     * @return
     */
    public String showMessage(Invitation i) {
        // Create the controller to check for overlapping events.
        EventDateController controller = new EventDateController();
        // Retrieve user's events.

        // Check whether the event doesn't overlap with other events.
        if (!controller.isDateAvailable(i.getEvent(), um.getEvents(user))) {
            return "PF('error').show()";
        } else {
            return "PF('error').hide()";
        }
    }

    /**
     * check if the notifications list is empty
     *
     * @return
     */
    public String isListEmpty() {
        if (notifications.isEmpty()) {
            return "true";
        }
        return "false";
    }

    /**
     * check if the dataGrid for notifications should be rendered or not
     *
     * @return
     */
    public String renderGrid() {
        if (!Boolean.parseBoolean(isListEmpty())) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * method to get the better day of the notification
     *
     * @param wn
     * @return
     */
    public String retrieveBetterDay(WeatherNotification wn) {
        if (wn.getBetterDay().getTime() == 0) {
            return "Sorry, no better day found in the available interval";
        }
        return wn.getBetterDay().toString();
    }

    /**
     * method to redirect the user to the event page
     *
     * @param notification, the relative notification
     * @return
     */
    public String eventLink(Notification notification) {
        shb.setSelected(notification.getEvent().getCreator());
        eb.setId(notification.getEvent().getIdEvent());
        return "eventPage?faces-redirect=true";
    }

    /**
     * check whether the notification is an invitation or not
     *
     * @param notification
     * @return
     */
    public String isInvitation(Notification notification) {
        if (notification instanceof Invitation) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * check whether the notification is a weather notification or not
     *
     * @param notificiation
     * @return
     */
    public String isWeatherNotification(Notification notificiation) {
        if (notificiation instanceof WeatherNotification) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * method that checks whether the better day is available or not
     *
     * @param wn
     * @return
     */
    public String checkBetterDay(Notification wn) {
        if (Boolean.parseBoolean(isWeatherNotification(wn))) {
            if (((WeatherNotification) wn).getBetterDay() == null) {
                return "false";
            } else {
                return "true";
            }
        }
        return null;
    }

    /**
     * method to accept an invitation
     *
     * @param invitation, the invitation to accept
     * @return
     */
    public String accept(Invitation invitation) {

        if (showMessage(invitation).equals("PF('error').show()")) {
            return null;
        }

        List<Event> userEvents = um.getEvents(user);
        Participation participation = new Participation();
        participation.setEvent(invitation.getEvent());
        participation.setUser(invitation.getUser());
        pm.save(participation);
        im.delete(invitation);
        notifications = um.getNotifications(user.getUsername());
        if (notifications.isEmpty()) {
            return "home?faces-redirect=true";
        }
        return null;
    }

    /**
     * method to decline an invitation
     *
     * @param invitation, the invitation to decline
     * @return
     */
    public String decline(Invitation invitation) {
        im.delete(invitation);
        notifications = um.getNotifications(user.getUsername());
        if (notifications.isEmpty()) {
            return "home?faces-redirect=true";
        }
        return null;
    }

    /**
     * method to delete a weather notification
     *
     * @param wn
     * @return
     */
    public String deleteWeatherNotification(WeatherNotification wn) {
        wnm.delete(wn);
        if (getNotifications().isEmpty()) {
            return "home?faces-redirect=true";
        }
        return null;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the notifications
     */
    public List<Notification> getNotifications() {
        notifications = um.getNotifications(um.getLoggedUser().getUsername());
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * @return the unseen
     */
    public int getUnseen() {
        checkUnseen();
        return unseen;
    }

    /**
     * @param unseen the unseen to set
     */
    public void setUnseen(int unseen) {
        this.unseen = unseen;
    }

}
