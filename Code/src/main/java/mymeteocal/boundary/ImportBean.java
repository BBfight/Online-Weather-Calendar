/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import mymeteocal.control.EventDateController;
import mymeteocal.control.TimerGenerator;
import mymeteocal.control.WeatherService;
import mymeteocal.entity.Event;
import mymeteocal.entity.Participation;
import mymeteocal.entityManager.EventManager;
import mymeteocal.entityManager.ParticipationManager;
import mymeteocal.entityManager.UserManager;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author J
 */
@Named
@SessionScoped
@ManagedBean
public class ImportBean implements Serializable {
 
    @EJB
    private UserManager um;
    
    @EJB
    private EventManager em;
    
    @EJB
    private ParticipationManager pm;
    
    @Inject
    private WeatherService ws;
    
    @Inject
    private TimerGenerator tg;
    
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Import");
    
    public void handleFileUpload(FileUploadEvent event) {
        
        upload(event.getFile());   
    }
    
    
    /**
     * Tries to import the calendar and shows different messages
     * depending on the import outcome.
     * 
     * @param file 
     */
    private void upload(UploadedFile file) {
        
        if(file != null) {
            
            try {
                
                importCalendar(file);
                
                FacesMessage message = new FacesMessage(file.getFileName() + " succesfully imported.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                
            } catch (IllegalArgumentException ex) {
                
                FacesMessage message = new FacesMessage("ERROR! " + ex.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
    }
    
    
    /**
     * Imports the calendar from the uploaded XML file.
     */
    private void importCalendar(UploadedFile file)  {
       
        SAXBuilder builder = new SAXBuilder();
        
        try {
               
            InputStream is = file.getInputstream();
            
            Document doc = builder.build(is);
            
            updateCalendarFromXML(doc);
            
            
        } catch (IOException | JDOMException ex) {
            
            throw new IllegalArgumentException("Unable to import the calendar.");
        } catch (IllegalArgumentException ex) {
            
            throw new IllegalArgumentException(ex.getMessage());  
        }      
    }
    
    /**
     * Given an XML document, updates the calendar with 
     * the events in the document
     * @param doc 
     */
    private void updateCalendarFromXML(Document doc) {
        
        // Element Calendar should be the root.
        Element root = doc.getRootElement();
        if (!root.getName().equals("Calendar")) {
            
            throw new IllegalArgumentException("XML file not valid: "
                    + "root element differs from Calendar.");
        }
        
        Element events = root.getChild("Events");
        List eventsList =  events.getChildren("Event");
        
        for (Object event : eventsList) {
            
            try {
                
                createEventFromXML((Element) event);
            } catch (IllegalArgumentException ex) {
                
                FacesMessage message = new FacesMessage("WARNING! " + ex.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }    
    }
    
    /**
     * Given an Event node, creates a new event in the user's
     * calendar, according to the usual constraints (consistent dates,
     * no overlaps, all required fields...).
     * 
     * @param event 
     */
    private void createEventFromXML(Element event) {
        
        Event newEvent = new Event();
           
        // Set event name.
        String eventName = event.getChildText("EventName");
        if (eventName == null || eventName.equals("")) {
            
            throw new IllegalArgumentException("XML file not valid: missing EventName element.");
        }
        newEvent.setName(eventName);
        
        // Set event creator.
        newEvent.setCreator(um.getLoggedUser());
    
        // Set event description (may be empty).
        String eventDescription = event.getChildText("EventDescription");
        newEvent.setDescription(eventDescription);
        
        // Set the dates.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        EventDateController edc = new EventDateController();
        
        try{
        
            String strDate = event.getChildText("StartingDate");
            if(strDate == null) {
            
                throw new IllegalArgumentException("Event: " + eventName + " XML file not valid: missing StartingDate element.");
               
            }
            Date startingDate = sdf.parse(strDate);
            newEvent.setStartingDate(startingDate);
            
            String endDate = event.getChildText("EndingDate");
            if(endDate == null) {
            
                throw new IllegalArgumentException("Event: " + eventName + " XML file not valid: missing EndingDate element.");
               
            }
            Date endingDate = sdf.parse(endDate);
            newEvent.setEndingDate(endingDate);
            
            if (!edc.areDatesConsistent(newEvent)) {
                
                throw new IllegalArgumentException("Event: " + eventName + " XML file not valid: inconsistent dates.");
            }
            
        } catch (ParseException ex) {
            
            throw new IllegalArgumentException("Event: " + eventName + " XML file not valid: bad-formatted dates.");
        }
        
        // Set the country.
        String country = event.getChildText("Country");
        if (country == null || country.equals("")) {
            
            throw new IllegalArgumentException("Event: " + eventName + " XML file not valid: missing Country element.");
        }
        newEvent.setCountry(country);
        
        // Set the city.
        String city = event.getChildText("City");
        if (city == null || city.equals("")) {
            
            throw new IllegalArgumentException("Event: " + eventName + " XML file not valid: missing City element.");
        }
        newEvent.setCity(city);
     
        // Set the place (may be empty).
        String place = event.getChildText("Place");
        newEvent.setPlace(place);
        
        // Set the outdoor flag (default false).
        String isOutdoor = event.getChildText("Outdoor");
        newEvent.setIsOutdoor(Boolean.getBoolean(isOutdoor));
        
        // Set private flag (default false).
        String isPrivate = event.getChildText("Private");
        newEvent.setIsPrivate(Boolean.getBoolean(isPrivate));
        
        // Check if the event imported overlaps with 
        // other events joined by the user.
        List<Event> userEvents = um.getEvents(um.getLoggedUser());
        
        if (!edc.isDateAvailable(newEvent, userEvents)) {
            
            throw new IllegalArgumentException("Event: " + eventName + " Sorry, this event overlaps with other existing events.");
        }
        
        if (newEvent.getIsOutdoor()) {
            
            tg.generateTimers(newEvent);

            if (ws.isForecastAvailable(newEvent.getStartingDate())) {
                
                newEvent.setWeatherForecast(ws.getWeather(newEvent.getCity(), newEvent.getStartingDate()));
            }
        }
        
        // Create event.
        em.save(newEvent);

        // Create creator's participation
        Participation participation = new Participation();
        participation.setEvent(newEvent);
        participation.setUser(um.getLoggedUser());
        pm.save(participation);
    }
}
