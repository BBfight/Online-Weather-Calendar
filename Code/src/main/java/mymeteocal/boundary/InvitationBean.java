/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.boundary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import mymeteocal.entity.User;
import mymeteocal.entityManager.EventManager;
import mymeteocal.entityManager.UserManager;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

@Named
@SessionScoped
public class InvitationBean implements Serializable {

    @Inject
    private EventBean eb;

    @EJB
    UserManager um;

    @EJB
    EventManager em;

    private DualListModel<String> usernames;
    private List<User> selected;
    private Boolean edit = false;

    @PostConstruct
    public void init() {

        List<User> users = um.findAll();
        List<String> invited;
        List<String> available;
        if (!edit) {
            invited = new ArrayList();
            available = new ArrayList();
            for (User u : users) {
                available.add(u.getUsername());
            }
            available.remove(um.getLoggedUser().getUsername());

            usernames = new DualListModel<>(available, invited);
        } else {
            invited = new ArrayList();
            available = new ArrayList();
            List<User> invitedUsers = em.getInvitedUsers(eb.getEvent().getIdEvent());
            List<User> attendingUsers = em.getParticipants(eb.getEvent().getIdEvent());
            for (User u : users) {
                if (!(invitedUsers.contains(u) || attendingUsers.contains(u))) {
                    available.add(u.getUsername());
                }
            }
            for (User u : invitedUsers) {
                invited.add(u.getUsername());
            }
            
            usernames = new DualListModel<>(available, invited);
            usernames.setSource(available);
            usernames.setTarget(invited);
        }

    }

    public DualListModel<String> getUsersnames() {
        return usernames;
    }

    public void setUsersnames(DualListModel<String> usernames) {
        this.usernames = usernames;
    }

    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for (Object item : event.getItems()) {
            builder.append(((String) item)).append("<br />");
        }

        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("User ready to be invited");
        msg.setDetail(builder.toString());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * @return a list of users to be invited for the relative event
     */
    public List<User> getSelectedUsers() {
        selected = new ArrayList();
        for (String username : usernames.getTarget()) {
            selected.add(um.findByUsername(username).get(0));
            }
        for(String s: usernames.getSource()){
            System.out.println(s);
        }
        return selected;
    }

    /**
     * @param edit, the flag to check if the event has to be edited or created
     */
    public void setEdit(Boolean edit) {
        this.edit = edit;
    }
}
