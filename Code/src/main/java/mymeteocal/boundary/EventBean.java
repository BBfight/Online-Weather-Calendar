/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
 * @author Benedetto
 */
@Named
@SessionScoped
public class EventBean implements Serializable {

    @Inject
    private InvitationBean ib;

    @Inject
    private SearchHandlerBean shb;

    @Inject
    private WeatherService ws;

    @Inject
    private TimerGenerator tg;

    @EJB
    private EventManager em;

    @EJB
    private InvitationManager im;

    @EJB
    private ParticipationManager pm;

    @EJB
    private UserManager um;
    
    @Inject MailingService mailSender;

    private Event event;
    private Long id;

    private Logger logger = Logger.getLogger("EventLogger");

    public EventBean() {
    }
    
    /**
    *method to initialise the event by getting its ID in the database
    */
    @PostConstruct
    public void init() {
        if (id != null) {
            event = em.findByIdEvent(id).get(0);
            if (!event.getCreator().equals(um.getLoggedUser())) {
                shb.setSelected(event.getCreator());
            } else {
                shb.setSelected(null);
            }
        } else if (um.getLoggedUser() == null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                logger.getLogger(CalendarBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * method to check if the event description is available or not
     *
     * @return
     */
    public String showDescription() {
        if (getEvent().getDescription().equals("")) {
            return "false";
        } else {
            return "true";
        }
    }

    /**
     * set the value of the link in the event page to different strings if the
     * user is the creator of the event or not
     *
     * @return the string
     */
    public String linkValue() {
        if (shb.getSelected() == null) {
            return "Go back to your home page";
        } else {
            return "Go back to the user calendar";
        }
    }

    /**
     * checks with icon to load according to the forecast
     *
     * @return
     */
    public String setMeteoIcon() {
        if (getWeatherForecast().equals("Clear")) {
            return "../images/sunny.png";
        }
        if (getWeatherForecast().equals("Clouds")) {
            return "../images/cloudy.gif";
        }
        if (getWeatherForecast().equals("Rain")) {
            return "../images/rainy.png";
        }
        if (getWeatherForecast().equals("Snow")) {
            return "../images/snowy.png";
        }
        return null;
    }

    /**
     * check whether to show the Delete Participation button or not
     *
     * @return
     */
    public String showDeleteParticipation() {
        if (!Boolean.parseBoolean(isCreator()) && Boolean.parseBoolean(isParticipant())) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * check whether the logged user is the creator of the event or not
     *
     * @return
     */
    public String isCreator() {
        if (um.getLoggedUser().equals(getEvent().getCreator())) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * check whether the logged user is not the creator of the event or not
     *
     * @return
     */
    public String isNotCreator() {
        if (um.getLoggedUser().equals(getEvent().getCreator())) {
            return "false";
        } else {
            return "true";
        }
    }

    /**
     * method to check if an user is invited or not to the event
     *
     * @return
     */
    public String isInvited() {
        List<User> invited = em.getInvitedUsers(id);
        if (invited.contains(um.getLoggedUser())) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * checks whether the user looking at the event is a participant and not the
     * creator
     *
     * @return
     */
    public String isParticipant() {
        List<User> attending = em.getParticipants(id);
        if (!Boolean.parseBoolean(isCreator()) && attending.contains(um.getLoggedUser()) || Boolean.parseBoolean(isCreator())) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * method to check if the logged user is invited to the event or is the creator
     * @return 
     */
    public String isInvitedOrCreator() {
        List<User> invited = em.getInvitedUsers(id);
        if (!Boolean.parseBoolean(isCreator()) && invited.contains(um.getLoggedUser()) || Boolean.parseBoolean(isCreator())) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        if (event == null) {
            init();
            return event;
        }
        return event;
    }

    /**
     * @param event, the event to set
     */
    public void setEvent(Event event) {

        this.event = event;
    }

    /**
     * @return name of the selected event
     */
    public String getName() {
        return getEvent().getName();
    }

    public String getCountry() {
        return getEvent().getCountry();
    }

    public String getCity() {
        return getEvent().getCity();
    }

    public String getPlace() {
        return getEvent().getPlace();
    }

    public String getDescription() {
        return getEvent().getDescription();
    }

    public String getIsPrivate() {
        if (getEvent().getIsPrivate()) {
            return "Private";
        } else {
            return "Public";
        }
    }

    public String getIsOutdoor() {
        if (getEvent().getIsOutdoor()) {
            return "Outdoor";
        } else {
            return "Indoor";
        }
    }

    /**
     * method to check the value of the isOutdoor flag in order to enable the bad weather flags
     * @return 
     */
    public String isOutdoor() {
        ib.setEdit(Boolean.TRUE);
        if (event != null) {
            return Boolean.toString(!event.getIsOutdoor());
        } else {
            return null;
        }
    }

    public String getStartingDate() {
        return getEvent().getStartingDate().toString();
    }

    public String getEndingDate() {
        return getEvent().getEndingDate().toString();
    }

    public String getBadWeatherFlags() {
        Event temp = getEvent();
        StringBuilder flags = new StringBuilder();
        if (temp.getIsOutdoor()) {
            if (temp.getSunnyFlag()) {
                flags.append("{Sunny}");
            }
            if (temp.getCloudyFlag()) {
                flags.append("{Cloudy}");
            }
            if (temp.getRainyFlag()) {
                flags.append("{Rainy}");
            }
            if (temp.getSnowyFlag()) {
                flags.append("{Snowy}");
            }
            String result = flags.toString();
            if ("".equals(result)) {
                return "Not specifyed";
            } else {
                return result;
            }
        }
        return null;
    }

    public String getAttendingUsers() {
        StringBuilder sequence = new StringBuilder();
        for (User u : em.getParticipants(getId())) {
            sequence.append("{");
            sequence.append(u.getUsername());
            sequence.append("} ");
        }
        return sequence.toString();
    }

    public String getInvitedUsers() {
        StringBuilder sequence = new StringBuilder();
        if (em.getInvitedUsers(getId()).isEmpty()) {
            return "It's lonely here, no invites yet";
        }
        for (User u : em.getInvitedUsers(getId())) {
            sequence.append("{");
            sequence.append(u.getUsername());
            sequence.append("} ");
        }
        return sequence.toString();
    }

    public String getWeatherForecast() {
        if (getEvent().getWeatherForecast() == null) {
            return "Sorry,not available for the selected location";
        }
        return getEvent().getWeatherForecast();
    }

    public String getCreator() {
        return getEvent().getCreator().getUsername();
    }

    /**
     * method to accept an invitation in the event page
     *
     * @return
     */
    public String accept() {

        // Create the controller to check for overlapping events.
        EventDateController controller = new EventDateController();
        // Retrieve user's events.

        // Check whether the event doesn't overlap with other events.
        if (!controller.isDateAvailable(getEvent(), um.getEvents(um.getLoggedUser()))) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "You can't accept this invitation, the relative event would overlap with another one you're attending.",
                    "You can't accept this invitation, the relative event would overlap with another one you're attending."));

            logger.log(Level.INFO, "You can't accept this invitation, the relative event would overla with another one you're attending.");

            return null;
        }

        Participation p = new Participation();
        p.setEvent(getEvent());
        p.setUser(um.getLoggedUser());
        pm.save(p);
        im.delete(im.find(um.getLoggedUser().getUsername(), id));
        return "eventPage?faces-redirect=true";
    }

    /**
     * method to decline an invitation in the event page
     *
     * @return
     */
    public String decline() {
        im.delete(im.find(um.getLoggedUser().getUsername(), id));
        if (getEvent().getIsPrivate()) {
            shb.setSelected(null);
            return "home?faces-redirect=true";
        } else {
            return "eventPage?faces-redirect=true";
        }
    }

    /**
     * method to update the event data in the database, checking also the
     * overlapping and the consistency of data
     *
     * @return the url of the event page
     */
    public String editEventData() {

        //Retrieve the creator
        User user = um.getLoggedUser();

        // Create the controller to check for overlapping events.
        EventDateController controller = new EventDateController();
        // Retrieve user's events.
        List<Event> userEvents = um.getEvents(user);
        userEvents.remove(getEvent());

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

        // Update event.
        Event e = new Event();
        e.setName(event.getName());
        e.setCity(event.getCity());
        e.setCountry(event.getCountry());
        e.setPlace(event.getPlace());
        e.setDescription(event.getDescription());
        e.setIsOutdoor(event.getIsOutdoor());
        e.setIsPrivate(event.getIsPrivate());
        e.setStartingDate(event.getStartingDate());
        e.setEndingDate(event.getEndingDate());
        e.setSunnyFlag(event.getSunnyFlag());
        e.setCloudyFlag(event.getCloudyFlag());
        e.setRainyFlag(event.getRainyFlag());
        e.setSnowyFlag(event.getSnowyFlag());
        e.setCreator(event.getCreator());

        if (ws.isForecastAvailable(e.getStartingDate())) {
            e.setWeatherForecast(ws.getWeather(e.getCity(), e.getStartingDate()));
        }

        // Create event.
        em.save(e);

        List<User> participants = em.getParticipants(id);
        participants.remove(um.getLoggedUser());
        for (User u : participants) {
            Participation p = pm.find(u.getUsername(), id);
            Invitation i = new Invitation();
            i.setEvent(e);
            i.setUser(u);
            i.setIsEventChanged(Boolean.TRUE);
            i.setIsVisualized(Boolean.FALSE);
            im.save(i);
            pm.delete(p);

        }
        pm.delete(pm.find(um.getLoggedUser().getUsername(), id));

        // Create creator's participation
        Participation participation = new Participation();
        participation.setEvent(e);
        participation.setUser(user);
        pm.save(participation);

        id = e.getIdEvent();

        //Delete the old event
        em.delete(event);

        event = e;

        if (e.getIsOutdoor()) {
            tg.generateTimers(event, false);
        }

        // Log event modification
        logger.log(Level.INFO, "User {0} edited an event", user.getUsername());

        return "eventPage?faces-redirect=true";
    }

    /**
     * method to edit the users invited to an event
     * @return the link to the homepage
     */
    public String editUsers() {
        
        // Delete the invitation of those that have been uninvited1
        List <User> selected = ib.getSelectedUsers();
        for (User u : em.getInvitedUsers(id)) {
            if (!(selected.contains(u))){
                Invitation i = im.find(u.getUsername(), id);
                im.delete(i);
            }
        }
        
        // Create the invitation of the new invited users
        for (User u : selected) {
            if (!em.getInvitedUsers(id).contains(u)) {
                Invitation invitation = new Invitation();
                invitation.setEvent(getEvent());
                invitation.setUser(u);
                invitation.setIsEventChanged(Boolean.FALSE);
                invitation.setIsVisualized(Boolean.FALSE);
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
        }


        return "eventPage?faces-redirect=true";

    }

    /**
     * method to let an user delete its participation from another user event
     *
     * @return
     */
    public String deleteParticipation() {

        String url;
        Participation p = pm.find(um.getLoggedUser().getUsername(), id);
        if (p.getEvent().getIsPrivate()) {
            shb.setSelected(null);
            url = "home?faces-redirect=true";
        } else {
            url = "eventPage?faces-redirect=true";
        }
        //Delete the relative participation
        pm.delete(pm.find(um.getLoggedUser().getUsername(), id));

        return url;
    }

    /**
     * method to delete the event from the database
     *
     * @return
     */
    public String deleteEvent() {

        //Delete the event
        em.delete(event);
        return null;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

}
