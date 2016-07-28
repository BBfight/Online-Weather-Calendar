/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.control;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import mymeteocal.entity.Event;
import mymeteocal.entityManager.EventManager;

/**
 *
 * @author Ettore
 */
@Stateless
public class TimerGenerator {

    @Resource
    TimerService service;

    @EJB
    EventManager evm;

    @EJB
    WeatherService ws;

    @EJB
    NotificationHandler nh;

    private Logger logger = Logger.getLogger("TimerGenerator");

    /**
     * sets a timer for the event with that id
     *
     * @param timeout
     * @param idEvent
     */
    public void startTimer(long timeout, Long idEvent) {
        logger.log(Level.INFO, "Timer long "+ timeout +" for event " + idEvent);
        service.createTimer(timeout, idEvent);
        logger.log(Level.INFO, "Timer set for event " + idEvent);
    }

    @Timeout
    public void handleTimeout(Timer timer) {
        logger.log(Level.INFO, "Timeout for event " + timer.getInfo());
        List<Event> events = evm.findByIdEvent((Long) timer.getInfo());
        if (events.isEmpty()) {
            return;
        }
        Event event = events.get(0);

        if (ws.hasBadWeather(event)) {
            int daysLeft = ws.getDifferenceInDays(event.getStartingDate());
            switch (daysLeft) {
                case 1:
                    nh.notifyWeatherChange(event,false);
                    break;
                case 3:
                    nh.notifyCreator(event);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * creates two timers for the event, one and three days before it of course
     * they are created only if not before now. Dah.
     *
     * @param event
     */
    public void generateTimers(Event event) {
        logger.log(Level.INFO, "Checking whether timers are needed for event " + event.getIdEvent());

        Calendar now = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        c.setTime(event.getStartingDate());
        c.add(Calendar.DATE, -1);
        if (now.before(c)) {
            logger.log(Level.INFO, "Global timer set for event " + event.getName() + " of " + event.getCreator());
            startTimer(c.getTime().getTime()-now.getTimeInMillis(), event.getIdEvent());
        }
        c.add(Calendar.DATE, -2);
        if (now.before(c)) {
            logger.log(Level.INFO, "Creator timer set for event " + event.getName() + " of " + event.getCreator());
            startTimer(c.getTime().getTime()-now.getTimeInMillis(), event.getIdEvent());
        }
    }

    /**
     * 
     * creates two timers for the event, one and three days before it of course
     * they are created only if not before now. Dah.
     * if testing= true, it generate timers waiting 5 and 10 seconds.
     * @param event
     * @param testing true if we are testing!
     */
    public void generateTimers(Event event, boolean testing) {
        if (!testing) {
            generateTimers(event);
            return;
        }
        logger.log(Level.INFO, "Testing timers for event " + event.getIdEvent());

        Calendar now = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 5);
        logger.log(Level.INFO, "Global timer set in 5 secs for event " + event.getName() + " of " + event.getCreator());
        startTimer(c.getTime().getTime()-now.getTimeInMillis(), event.getIdEvent());
        c.add(Calendar.SECOND, 5);
        logger.log(Level.INFO, "Creator timer set in 10 secs for event " + event.getName() + " of " + event.getCreator());
        startTimer(c.getTime().getTime()-now.getTimeInMillis(), event.getIdEvent());
    }

}
