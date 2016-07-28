/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.entity;

import mymeteocal.control.PasswordEncrypter;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Entity corresponding to the table user in our database, containing the list
 * of users registered in our application characterised by a username as their
 * unique key
 * @author Benedetto
 */
@Entity(name = "USERS")
@NamedQueries({
    @NamedQuery(name="User.findByUsername",
            query= "SELECT u FROM USERS u WHERE u.username = :username"
    ), @NamedQuery(name="User.findAll",
            query= "SELECT u FROM USERS u"
    )
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(unique=true)
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    @NotNull(message = "Email may not be empty")
    private String email;
    
    @NotNull(message = "Password may not be empty")
    private String password;
    
    //this attribute is necessary in order to set properly the realm on glassfish server
    @NotNull(message = "May not be empty")
    private String groupName;

    @Id
    @Pattern(regexp = "^[a-z0-9_-]{3,15}$", message = "invalid username")
    @NotNull(message = "Username may not be empty")
    private String username;

    @NotNull(message = "Name may not be empty")
    private String name;

    @NotNull(message = "Surname may not be empty")
    private String surname;
    
    @NotNull
    private Boolean hasPrivateCalendar;
    
    @OneToMany(mappedBy="creator", cascade = CascadeType.REMOVE)
    private List<Event> events;
    
    @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE)
    private List<Participation> participations;
    
    @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE)
    private List<Invitation> invitations;
    
    @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE)
    private List<WeatherNotification> weatherNotifications;
    
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
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * method to encrypt the password when saved
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = PasswordEncrypter.encryptPassword(password);
    }

    /**
     * @return the username
     */
    public String getUsername() {
       // if("aaa".equals(username)){
        //    return "Lazy Bitch";
       // }
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return the hasPrivateCalendar
     */
    public Boolean getHasPrivateCalendar() {
        return hasPrivateCalendar;
    }

    /**
     * @param hasPrivateCalendar the hasPrivateCalendar to set
     */
    public void setHasPrivateCalendar(Boolean hasPrivateCalendar) {
        this.hasPrivateCalendar = hasPrivateCalendar;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.username);
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
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }
    
}
