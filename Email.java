import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Random;

/**
 * Created by Brian Duffy on 3/24/2016.
 *
 * @author Brian Duffy <duffy18@purdue.edu>
 * @version 4/6/2016
 * @lab 2
 */


public class Email {
    String recipient;
    String sender;
    long id;
    String message;
    Date date;

    public Email(String recipient, String sender, long id, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.id = id;
        this.message = message;
        this.date = new Date();
    }

    //You have to record the time when each Email is constructed so
    // that you can print this information in GET-EMAILS command.
    public long getID() {
        return id;
    }

    public String getOwner() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return getID() + ";" + date.toString() + ";" + String.format(" From: " + getSender() + " \"%s\"", getMessage());
        //The format of the returned String includes mail id, create time, sender and mail body.
        // Each field have to be delimited by semicolons.
        //An example to illustrate the format of returned String:
        // “540077535771753178;Sun Oct 18 01:16:36 EDT 2015; From: root “This is the mail body.”
        // Note: You can use java.util.Date to format the date.
    }

}
