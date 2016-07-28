/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import mymeteocal.entity.Event;
import mymeteocal.entity.User;
import mymeteocal.entityManager.EventManager;
import mymeteocal.entityManager.UserManager;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author cast13
 */
@Named
@ViewScoped
public class CalendarBean implements Serializable {

    @EJB
    UserManager um;

    @EJB
    private EventManager em;

    @Inject
    private SearchHandlerBean shb;

    @Inject
    private NotificationBean nb;

    @Inject
    private EventBean eb;

    private ScheduleModel eventModel;

    private ScheduleEvent event;

    private String message;

    private Logger logger = Logger.getLogger("CalendarLogger");

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        if (shb.getSelected() != null) {
            if (!um.getLoggedUser().equals(shb.getSelected()) && shb.getSelected().getHasPrivateCalendar()) {
                return;
            }
        }
        if (shb.getSelected() == null) {

            for (Event e : um.getEvents(um.getLoggedUser())) {
                event = new DefaultScheduleEvent(e.getName(), e.getStartingDate(), e.getEndingDate());
                eventModel.addEvent(event);
                event.setId(e.getIdEvent().toString());
                eventModel.updateEvent(event);
            }
        } else {

            for (Event e : um.getEvents(shb.getSelected())) {
                event = new DefaultScheduleEvent(e.getName(), e.getStartingDate(), e.getEndingDate());
                eventModel.addEvent(event);
                event.setId(e.getIdEvent().toString());
                eventModel.updateEvent(event);
            }
        }

    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public Date todayDate() {
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        return date;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (DefaultScheduleEvent) selectEvent.getObject();
        Long tempId = Long.parseLong(event.getId());
        eb.setId(tempId);
        eb.init();
        User user = eb.getEvent().getCreator();
        if (!(Boolean.parseBoolean(eb.isInvitedOrCreator())) && !(Boolean.parseBoolean(eb.isParticipant()))
                && (em.findByIdEvent(tempId).get(0).getIsPrivate())) {
            message = "Sorry, the event is private and you're not either a participant or an invited user...";
            return;
        }
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("eventPage.xhtml");
        } catch (IOException ex) {
            logger.getLogger(CalendarBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
