/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.IOException;
import mymeteocal.entity.User;
import mymeteocal.entityManager.UserManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author Benedetto
 */
@Named
@RequestScoped
public class RegistrationBean {

    @EJB
    private UserManager um;
    
    @Inject
    private CalendarBean cb;
    
    private User user;
    
    Logger logger = Logger.getLogger("Registration");

    public RegistrationBean() {
    }
    
    /**
     * method to redirect the user after the registration is successful
     */
    public void redirect(){
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(CalendarBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Registers the user and sends him a confirmation email.
     * 
     * @return 
     */
    public String register() {
        
        try {
            um.save(user);

            logger.log(Level.INFO, "New user registered: {0}", user.getUsername());
            
            return "confirmationPage?faces-redirect=true";
        } catch (Exception e) {
            isUsernameValid(user.getUsername());
            isEmailValid(user.getEmail());
            return null;
        }
    }

    public void isUsernameValid(String newUsername) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        if (!um.findByUsername(newUsername).isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username already in use!", "Username already in use!"));
            logger.log(Level.SEVERE, "Username already in use!");
        }
    }
    
    public void isEmailValid(String newEmail) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        if (!um.findByEmail(newEmail).isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email already in use!", "Email already in use!"));
            logger.log(Level.SEVERE, "Email already in use!");
        }
    }
}
