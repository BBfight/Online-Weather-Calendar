/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymeteocal.control;

import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author cast13
 */
@Stateless
public class MailingService {
    /**
     * SMTP server that manages the communication.
     */
    private final String smtpServ = "smtp.gmail.com";
    
    /**
     * eMail address used as sender.
     */
    private final String from = "meteocal.ejb@gmail.com";
    
    /**
     * Authentication password.
     */
    private final String password = "mymeteocal";
    
    @Resource(name = "mail/MyMeteoCalMail")
    private Session mailSession;
    
    /**
     * not tested yet
     * @param to
     * @param subject
     * @param text
     * @throws MessagingException 
     */
    @Asynchronous
    public void sendMail(String to, String subject, String text) throws MessagingException{
        // Create the message object
     Message message = new MimeMessage(mailSession);

     // Adjust the recipients. Here we have only one
     // recipient. The recipient's address must be
     // an object of the InternetAddress class.
     message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to, false));

     // Set the message's subject
     message.setSubject(subject);

     // Insert the message's body
     message.setText(text);

     // Adjust the date of sending the message
     Date timeStamp = new Date();
     message.setSentDate(timeStamp);

     // Use the 'send' static method of the Transport
     // class to send the message
     Transport.send(message);
    }
}
