/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import mymeteocal.entity.User;
import mymeteocal.entityManager.UserManager;

/**
 *
 * @author Benedetto
 */
@Named
@SessionScoped
public class SearchHandlerBean implements Serializable {

    @EJB
    UserManager um;

    private String prefix;
    private List<User> results;
    private User selected;

    public String actualUsername(){
        if(selected == null){
            return "Here's your schedule:";
        }else{
            return ("Here's '"+selected.getUsername()+"' 's schedule:");
        }
    }
    
        /**
     * method that enables the link to go back to the user home page from an event page, 
     * according to the privacy of its calendar
     * @return 
     */
    public String isLinkEnabled(){
        if(Boolean.parseBoolean(notIsYourCalendar()) && checkUserCalendar(selected).equals("Private")){
            return "true";
        }else{
            return "false";
        }
    }
    
    /**
     * method to check if the user calendar is private or public
     * @param u
     * @return 
     */
    public String checkUserCalendar(User u){
        if(u.getHasPrivateCalendar()){
            return "Private";
        }else{
            return "Public";
        }
    }
    
    /**
     * check whether the user is not on its personal calendar or on the one
     * of another user
     * @return 
     */
    public String notIsYourCalendar(){
        if(selected != null){
            if(selected != um.getLoggedUser()){
                return "true";
            }
        }
        return "false";
    }
   
      /**
     * check whether the user is on its personal calendar or on the one
     * of another user
     * @return 
     */
    public String isYourCalendar(){
        if(Boolean.parseBoolean(notIsYourCalendar())){
           return "false"; 
        }
        return "true";
    }
    
    /**
     * method that puts to null the selected user during the research 
     * when the user go back to its homepage
     */
    public void removeSelected(){
        selected = null;
    }
    
    /**
     * initialise the result list of the search after that the prefix has been set
     */
    public void init() {
        results = um.findByUsernamePrefix(prefix);
        results.remove(um.getLoggedUser());
    }

    /**
     * check if the link to a specific user's homepage should be enabled, 
     * by controlling if its calendar is private or not
     * @param username to be checked
     * @return true if the user has private calendar
     */
    public String checkLink(String username){
        
        if(um.findByUsername(username).get(0).getHasPrivateCalendar()){
           return "true"; 
        }else{
            return "false";
        }
    }
    
    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
        init();
    }

    /**
     * @return the results
     */
    public List<User> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<User> results) {
        this.results = results;
    }

    /**
     * @return the selected user
     */
    public User getSelected() {
        return selected;
    }

    /**
     * @param selected the selected user to set
     */
    public void setSelected(User selected) {
        this.selected = selected;
    }

}
