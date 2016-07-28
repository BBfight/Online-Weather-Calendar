/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.control;

import java.io.Serializable;

import java.util.List;

import mymeteocal.entity.Event;

/**
 * Class created to check whether a new event overlaps with
 * other existing ones.
 * 
 * @author J
 */
public class EventDateController implements Serializable {
    
    /**
     * Used to compare dates.
     */
    private final DateComparator dateComparator;
    
    public EventDateController() {
        
        dateComparator = new DateComparator();
    }
    
    
    /**
     * 
     * @param event
     * @param userEvents
     * @return true if the event doesn't overlap with any other event of the user
     */
    public boolean isDateAvailable(Event event, List<Event> userEvents) {
        
        for (Event e : userEvents) {
            
            if (overlappingEvents(e, event)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param event
     * @return true if the ending date of an event 
     *  comes after the starting one
     */
    public boolean areDatesConsistent(Event event) {
        
        return dateComparator.compare(event.getStartingDate(), event.getEndingDate()) == 1;
    }
    
    
    /**
     * 
     * @param e1
     * @param e2
     * @return true if the two events overlap
     */ 
    private boolean overlappingEvents(Event e1, Event e2) {
        
        // Two events don't overlap if, named s1, s2 and e1, e2 the starting and ending
        // dates of the two events, e1<=s2 or e2<=s1.
        // Note: we assume the starting and ending date are consistent (i.e. s1<e1 and s2<e2).
        return !(dateComparator.compare(e1.getEndingDate(), e2.getStartingDate()) != -1 ||
                dateComparator.compare(e2.getEndingDate(), e1.getStartingDate()) != -1);
    }
}
