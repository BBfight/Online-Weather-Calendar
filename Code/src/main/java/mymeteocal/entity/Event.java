/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.validation.constraints.NotNull;

/**
 *
 * @author cast13
 */
@Entity(name = "EVENT")
@NamedQueries({
    @NamedQuery(name="Event.findByIdEvent",
            query= "SELECT e FROM EVENT e WHERE e.idEvent = :idEvent"
    ),@NamedQuery(name="Event.findByCreator",
            query= "SELECT e FROM EVENT e WHERE e.creator = :creator"
    ),@NamedQuery(name="Event.findNotStarted",
            query= "SELECT e FROM EVENT e WHERE e.startingDate > :now")
})
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idEvent;
    
    @NotNull(message = "Name may not be empty")
    private String name;
    
    @NotNull
    private Boolean isPrivate = false;
    
    private String description;
    
    @NotNull(message = "Country must be specified")
    private String country;
    
    @NotNull(message = "City must be specified")
    private String city;
    
    private String place;
    
    @NotNull(message = "Starting date must be specified")
    @Temporal(TIMESTAMP)
    private Date startingDate; // must be < endingDate
    
    @NotNull(message = "Ending date must be specified")
    @Temporal(TIMESTAMP)
    private Date endingDate; // must be > startingDate
    
    @ManyToOne
    @JoinColumn(name = "creator")
    @NotNull(message = "Creator must be specified")
    private User creator;
    
    @NotNull
    private Boolean isOutdoor = false;
    
    private String weatherForecast;
    
    @NotNull
    private Boolean sunnyFlag = false;
    
    @NotNull
    private Boolean rainyFlag = false;
    
    @NotNull
    private Boolean cloudyFlag = false;
    
    @NotNull
    private Boolean snowyFlag = false;
    
    @OneToMany(mappedBy="event", cascade = CascadeType.REMOVE)
    private List<Participation> participations;
    
    @OneToMany(mappedBy="event", cascade = CascadeType.REMOVE)
    private List<Invitation> invitations;
    
    @OneToMany(mappedBy="event", cascade = CascadeType.REMOVE)
    private List<WeatherNotification> weatherNotifications;

    public Event(){
        
    }
    /**
     * @return the event id 
     */
    public Long getIdEvent() {
        return idEvent;
    }
    
    public void setIdEvent(Long idEvent) {
        this.idEvent= idEvent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the isPrivate
     */
    public Boolean getIsPrivate() {
        return isPrivate;
    }

    /**
     * @param isPrivate the isPrivate to set
     */
    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the place
     */
    public String getPlace() {
        if (place == null) {
            place = "";
        }
        return place;
    }

    /**
     * @param place the place to set
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * @return the startingDate
     */
    public Date getStartingDate() {
        return startingDate;
    }

    /**
     * @param startingDate the startingDate to set
     */
    public void setStartingDate(Date startingDate) {
        this.startingDate=startingDate;
    }
    
    /**
     * @return the endingDate
     */
    public Date getEndingDate() {
        return endingDate;
    }

    /**
     * @param endingDate the endingDate to set
     */
    public void setEndingDate(Date endingDate) {
        this.endingDate=endingDate;
    }
    
    /**
     * @return the creator
     */
    public User getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * @return the isOutdoor
     */
    public Boolean getIsOutdoor() {
        return isOutdoor;
    }

    /**
     * @param isOutdoor the isOutdoor to set
     */
    public void setIsOutdoor(Boolean isOutdoor) {
        this.isOutdoor = isOutdoor;
    }

    /**
     * @return the weatherForecast
     */
    public String getWeatherForecast() {
        return weatherForecast;
    }

    /**
     * @param weatherForecast the weatherForecast to set
     */
    public void setWeatherForecast(String weatherForecast) {
        this.weatherForecast = weatherForecast;
    }
    
    /**
     * 
     * @return true if the sunnyFlag is activated
     */
    public Boolean getSunnyFlag() {
        return sunnyFlag;
    }
    
    /**
     * 
     * @param sunnyFlag 
     */
    public void setSunnyFlag(Boolean sunnyFlag) {
        this.sunnyFlag = sunnyFlag;
    }
    
    /**
     * 
     * @return true if the rainyFlag is activated
     */
    public Boolean getRainyFlag() {
        return rainyFlag;
    }
    
    /**
     * 
     * @param rainyFlag 
     */
    public void setRainyFlag(Boolean rainyFlag) {
        this.rainyFlag = rainyFlag;
    }
    
    /**
     * 
     * @return true if the cloudyFlag is activated
     */
    public Boolean getCloudyFlag() {
        return cloudyFlag;
    }
    
    /**
     * 
     * @param cloudyFlag 
     */
    public void setCloudyFlag(Boolean cloudyFlag) {
        this.cloudyFlag = cloudyFlag;
    }
    
    /**
     * 
     * @return true if the snowyFlag is activated
     */
    public Boolean getSnowyFlag() {
        return snowyFlag;
    }
    
    /**
     * 
     * @param snowyFlag 
     */
    public void setSnowyFlag(Boolean snowyFlag) {
        this.snowyFlag = snowyFlag;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.idEvent);
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
        final Event other = (Event) obj;
        if (!Objects.equals(this.idEvent, other.idEvent)) {
            return false;
        }
        return true;
    }
    
    
}
