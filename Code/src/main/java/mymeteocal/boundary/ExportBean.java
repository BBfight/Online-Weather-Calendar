/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mymeteocal.entity.Event;
import mymeteocal.entity.User;
import mymeteocal.entityManager.UserManager;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author J
 */
@Named
@ViewScoped
public class ExportBean implements Serializable {
    
    @EJB
    private UserManager um;
    
    /**
     * The file to be exported.
     */
    private StreamedContent file;
    
    /**
     * The XML document to be created.
     */
    private Document doc;
    
    /**
     * Input stream containing the XML calendar.
     */
    private InputStream inputStream;
    
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Export");
    
    /**
     * @return the file
     */
    public StreamedContent getFile() {
        return file;
    }
    
    /**
     * @param file the file to set
     */
    public void setFile(StreamedContent file) {
        this.file = file;
    }
    
    @PostConstruct
    public void exportCalendar() {
        
        exportFile(um.getLoggedUser());
       
        String fileName = um.getLoggedUser().getUsername() + "Calendar.xml";

        file = new DefaultStreamedContent(inputStream, "text/xml", fileName);     
    }
    
    /**
     * Creates an XML file representing the user's calendar.
     * 
     * @param u
     */
    private void exportFile(User u) {
        
        // Here is how the XML file should look like:
        //
        //  <?xml version="1.0" encoding="UTF-8" standalone="no" ?> 
        //  <Calendar>
        //      <CalendarOwner>Name Surname</CalendarOwner>
        //      <Elements>
        //          <Element>
        //              <EventName>Event name</EventName>
        //              <EventCreator>Creator username</EventCreator>
        //              .
        //              .
        //              .
        //          </Element>
        //          <Element>
        //              .
        //              .
        //              .
        //          </Element>
        //          .
        //          .
        //          .
        //      </Elements>
        //  </Calendar>
        
        try {
            
            // Create the document.
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            
            // Root element.
            Element calendar = doc.createElement("Calendar");
            doc.appendChild(calendar);
            
            // Calendar owner. 
            Element calendarOwner = doc.createElement("CalendarOwner");
            String owner = u.getName() + " " + u.getSurname();
            calendarOwner.appendChild(doc.createTextNode(owner));
            calendar.appendChild(calendarOwner);
            
            // Events container.
            Element events = doc.createElement("Events");
            calendar.appendChild(events);
            
            // Events.
            List<Event> userEvents = um.getEvents(u);
            for (Event e : userEvents) {
                
                events.appendChild(addEventElement(e));
            }
            
            // Write the content into xml file.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            Source source = new DOMSource(doc);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Result result = new StreamResult(outputStream);
            
            transformer.transform(source, result);
            
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            
        } catch (ParserConfigurationException ex) {
            
            logger.log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            
            logger.log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates an Event XML element,
     * with all the event attributes as child elements.
     * 
     * @param event
     * @return an Event XML element
     */
    private Element addEventElement(Event event) {
        
        // Element event.
        Element newEvent = doc.createElement("Event");
        
        // Event name.
        Element eventName = doc.createElement("EventName");
        eventName.appendChild(doc.createTextNode(event.getName()));
        newEvent.appendChild(eventName);
        
        // Event creator.
        Element eventCreator = doc.createElement("EventCreator");
        eventCreator.appendChild(doc.createTextNode(event.getCreator().getUsername()));
        newEvent.appendChild(eventCreator);
        
        // Event description.
        Element eventDescription = doc.createElement("EventDescription");
        eventDescription.appendChild(doc.createTextNode(event.getDescription()));
        newEvent.appendChild(eventDescription);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        // Event starting date.
        Element eventStartDate = doc.createElement("StartingDate");
        String startDate = sdf.format(event.getStartingDate());
        eventStartDate.appendChild(doc.createTextNode(startDate));
        newEvent.appendChild(eventStartDate);
        
        // Event ending date.
        Element eventEndDate = doc.createElement("EndingDate");
        String endDate = sdf.format(event.getEndingDate());
        eventEndDate.appendChild(doc.createTextNode(endDate));
        newEvent.appendChild(eventEndDate);
        
        // Country.
        Element country = doc.createElement("Country");
        country.appendChild(doc.createTextNode(event.getCountry()));
        newEvent.appendChild(country);
        
        // City.
        Element city = doc.createElement("City");
        city.appendChild(doc.createTextNode(event.getCity()));
        newEvent.appendChild(city);
        
        // Place.
        Element place = doc.createElement("Place");
        place.appendChild(doc.createTextNode(event.getPlace()));
        newEvent.appendChild(place);
        
        // Outdoor flag.
        Element outdoorFlag = doc.createElement("Outdoor");
        outdoorFlag.appendChild(doc.createTextNode(event.getIsOutdoor().toString()));
        newEvent.appendChild(outdoorFlag);
        
        // Private flag.
        Element privateFlag = doc.createElement("Private");
        privateFlag.appendChild(doc.createTextNode(event.getIsPrivate().toString()));
        newEvent.appendChild(privateFlag);
        
        return newEvent;
    }
}
