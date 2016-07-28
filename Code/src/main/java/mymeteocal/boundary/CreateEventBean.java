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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import mymeteocal.control.EventDateController;
import mymeteocal.control.MailingService;
import mymeteocal.control.TimerGenerator;
import mymeteocal.control.WeatherService;
import mymeteocal.entity.Event;
import mymeteocal.entity.Invitation;
import mymeteocal.entity.Participation;
import mymeteocal.entity.User;
import mymeteocal.entityManager.EventManager;
import mymeteocal.entityManager.InvitationManager;
import mymeteocal.entityManager.ParticipationManager;
import mymeteocal.entityManager.UserManager;

/**
 *
 * @author J
 */
@Named
@ViewScoped
public class CreateEventBean implements Serializable {

    @EJB
    private EventManager em;

    @EJB
    private UserManager um;

    @EJB
    private ParticipationManager pm;

    @EJB
    private InvitationManager im;

    @Inject
    private InvitationBean ib;

    @Inject
    private WeatherService ws;

    @Inject
    private TimerGenerator tg;
    
    @Inject MailingService mailSender;

    private Logger logger = Logger.getLogger("CreateEvent");

    private Boolean check = false;

    /**
     * The event to be created.
     */
    private Event event;

    public CreateEventBean() {
    }

    /**
     * @return the event
     */
    public Event getEvent() {

        if (event == null) {
            event = new Event();
        }
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {

        this.event = event;
    }

    /**
     * @return the negation of the flag {@code isOutdoor} as a string
     */
    public String isOutdoor() {

        ib.setEdit(Boolean.FALSE);
        return Boolean.toString(!getEvent().getIsOutdoor());
    }

    public String showDialog() {
        if (check) {
            return "PF('inviteUsers').show()";
        } else {
            return "PF('inviteUsers').hide()";
        }
    }

    /**
     * Saves the event in the database, after setting its creator and checking
     * if it doesn't overlap with other events (of the same user).
     *
     * @return
     */
    public String createEvent() {

        // Event creator.
        User user = um.getLoggedUser();

        // Set the event creator.
        event.setCreator(user);
        // Create the controller to check for overlapping events.
        EventDateController controller = new EventDateController();
        // Retrieve user's events.
        List<Event> userEvents = um.getEvents(user);

        // Check whether the dates are consistent.
        if (!controller.areDatesConsistent(event)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "The ending date must come after the starting one.",
                    "The ending date must come after the starting one."));

            logger.log(Level.INFO, "The ending date must come after the starting one.");

            return null;
        }

        // Check whether the event doesn't overlap with other events.
        if (!controller.isDateAvailable(event, userEvents)) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "The event you're about to create overlaps with another one.",
                    "The event you're about to create overlaps with another one."));

            logger.log(Level.INFO, "The event you're about to create overlaps with another one.");

            return null;
        }
        check = true;

        if (ws.isForecastAvailable(event.getStartingDate())) {
            event.setWeatherForecast(ws.getWeather(event.getCity(), event.getStartingDate()));
        }

        // Create event.
        em.save(event);

        if (event.getIsOutdoor()) {
            tg.generateTimers(event, false);
        }

        // Create creator's participation
        Participation participation = new Participation();
        participation.setEvent(event);
        participation.setUser(user);
        pm.save(participation);

        // Log event creation
        logger.log(Level.INFO, "User {0} created a new event", user.getUsername());

        return null;
    }

    public String addInvitedUsers() {

        //Create invitations and send emails.
        for (User u : ib.getSelectedUsers()) {
            Invitation invitation = new Invitation();
            invitation.setEvent(event);
            invitation.setUser(u);
            invitation.setIsVisualized(Boolean.FALSE);
            invitation.setIsEventChanged(Boolean.FALSE);
            im.save(invitation);

            try {
                
                // Set the recipient.
                String to = u.getEmail();

                // Set the message subject.
                String subject = "Invitation Notification";

                // Set the message body.
                String text = "Dear " + u.getUsername() + "\n\n"
                        + "you've been invited by " + event.getCreator().getUsername()
                        + " to join the event " + event.getName()
                        + " starting on " + event.getStartingDate().toString() + ".\n\n"
                        + "Cheers,\n\n" + "EJB\n";

                // Send the message.
                mailSender.sendMail(to, subject, text);
               
            } catch (MessagingException ex) {

                logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
