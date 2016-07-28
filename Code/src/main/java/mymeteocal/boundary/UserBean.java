/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import mymeteocal.control.PasswordEncrypter;
import mymeteocal.entity.User;
import mymeteocal.entityManager.UserManager;

/**
 *
 * @author Benedetto
 */
@Named
@RequestScoped
public class UserBean implements Serializable {

    @EJB
    private UserManager um;

    private User user;

    private Logger logger = Logger.getLogger("UserLogger");

    private String oldPassword;
    private Boolean check = false;
    
    public UserBean() {
    }

    /**
     * @return the user
     */
    public User getUser() {

        if (user == null) {
            user = um.getLoggedUser();
        }
        return user;
    }

    /**
     * @param user, the user to set
     */
    public void setUser(User user) {

        this.user = user;
    }

    /**
     * @return name of the logged user
     */
    public String getName() {
        return um.getLoggedUser().getName();
    }

    /**
     * @return surname of the logged user
     */
    public String getSurname() {
        return um.getLoggedUser().getSurname();
    }

    /**
     * @return username of the logged user
     */
    public String getUsername() {
        return um.getLoggedUser().getUsername();
    }

    /**
     * @return password of the logged user
     */
    public String getPassword() {
        return um.getLoggedUser().getPassword();
    }

    /**
     * @return email of the logged user
     */
    public String getEmail() {
        return um.getLoggedUser().getEmail();
    }

    /**
     * @return the has private calendar flag of the logged user
     */
    public String getHasPrivateCalendar() {
        if(um.getLoggedUser().getHasPrivateCalendar()){
            return "Private";
        }else{
            return "Public";
        }
    }

    /**
     * @return the old password inserted
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * @param oldPassword the old password inserted
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String showDialog(){
        if(check){
            return "PF('confirmation').show()";
        }else{
            return "PF('confirmation').hide()";
        }
    }
    
    /**
     * Method to update the user data after that they've been modified in the
     * profile page, also checks that the new email is available in the system
     *
     * @return the page to load when the method succeeds
     */
    public String updateUserPersonalData() {

        try {

            um.update(user);
            return "profilePage?faces-redirect=true";
        } catch (Exception e) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email already in use!", "Email already in use!"));
            logger.log(Level.SEVERE, "Email already in use!");
            return null;
        }
    }

    /**
     * method to update the user password only if the old password has been
     * typed correctly
     *
     * @return
     */
    public String updateUserPassword() {

        if (!checkOldPassword()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Old password not valid!", "Old password not valid!"));
            logger.log(Level.SEVERE, "Old password not valid!");
            return null;
        }
        check = true;

        um.update(user);
        return null;
    }

    /**
     * @return true if the inserted password matches the old one
     */
    private boolean checkOldPassword() {

        String encryptedPassword = PasswordEncrypter.encryptPassword(getOldPassword());

        return getPassword().equals(encryptedPassword);
    }

}
