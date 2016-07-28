/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.DATE;
import javax.validation.constraints.NotNull;

/**
 *
 * @author cast13
 */
@Entity(name = "WEATHERNOTIFICATION")
//@IdClass(WeatherNotification.class)
@NamedQueries({
    @NamedQuery(name="WeatherNotification.findByUsername",
            query= "SELECT w FROM WEATHERNOTIFICATION w WHERE w.id.username = :username"
    ),@NamedQuery(name="WeatherNotification.findByIdEvent",
            query= "SELECT w FROM WEATHERNOTIFICATION w WHERE w.id.idEvent = :idEvent"
    ),@NamedQuery(name="WeatherNotification.findByUsernameAndIdEvent",
            query= "SELECT w FROM WEATHERNOTIFICATION w WHERE w.id.idEvent = :idEvent AND"
                    + " w.id.username = :username"),
    @NamedQuery(name="WeatherNotification.findOld",
            query= "SELECT w FROM WEATHERNOTIFICATION w JOIN w.event e WHERE e.startingDate < CURRENT_TIMESTAMP")
})
public class WeatherNotification implements Notification {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private WeatherNotificationPK id;
    
    @ManyToOne
    @MapsId("username")
    private User user;

    
    
    @ManyToOne
    @MapsId("idEvent")
    private Event event;
    
//    private Long number;
    
    @NotNull
    private Boolean isVisualized;
    
    @NotNull
    private Boolean hasWeatherChanged;
    
    @Temporal(DATE)
    private Date betterDay;
    
    
    /**
     * it is needed in order to know the forecast of that exact notification
     * and not the dynamic and possibly changing one
     */
    @NotNull
    private String forecast;
    
    /**
     *
     * @return the forecast of such notification
     */
    public String getForecast() {
        return forecast;
    }
    
    /**
     * 
     * @param forecast 
     */
    public void setForecast(String forecast) {
        this.forecast = forecast;
    }
    
    /**
     * 
     * @return the id of the notification
     */
    public WeatherNotificationPK getId() {
        return id;
    }
    
    /**
     * 
     * @param id 
     */
    public void setId(WeatherNotificationPK id) {
        this.id = id;
    }
    
    /**
     * 
     * @return the user regarding the notification
     */
    public User getUser() {
        return user;
    }
    
    /**
     * 
     * @param user 
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * 
     * @return the event of the notification
     */
    public Event getEvent() {
        return event;
    }
    
    /**
     * 
     * @param event 
     */
    public void setEvent(Event event) {
        this.event = event;
    }
    
    /**
     * 
     * @return true if it is already been visualized by the user
     */
    public Boolean getIsVisualized() {
        return isVisualized;
    }
    
    /**
     * 
     * @param isVisualized 
     */
    public void setIsVisualized(Boolean isVisualized) {
        this.isVisualized = isVisualized;
    }
    
    /**
     * the better day can be also null, if there wasn't any available or
     * it has been impossible to contact the weather provider
     * @return the better day suggested
     */
    public Date getBetterDay() {
        return betterDay;
    }
    
    /**
     * 
     * @param betterDay 
     */
    public void setBetterDay(Date betterDay) {
        this.betterDay = betterDay;
    }

    public Boolean getHasWeatherChanged() {
        return hasWeatherChanged;
    }

    public void setHasWeatherChanged(Boolean hasWeatherChanged) {
        this.hasWeatherChanged = hasWeatherChanged;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.id);
        hash = 71 * hash + Objects.hashCode(this.isVisualized);
        hash = 71 * hash + Objects.hashCode(this.betterDay);
        hash = 71 * hash + Objects.hashCode(this.forecast);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WeatherNotification other = (WeatherNotification) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.isVisualized, other.isVisualized)) {
            return false;
        }
        if (!Objects.equals(this.betterDay, other.betterDay)) {
            return false;
        }
        if (!Objects.equals(this.forecast, other.forecast)) {
            return false;
        }
        return true;
    }

    
    
    
}