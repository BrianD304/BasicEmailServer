import javax.xml.stream.events.Characters;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Brian Duffy on 3/24/2016.
 *
 * @author Brian Duffy <duffy18@purdue.edu>
 * @version 4/6/2016
 * @lab 2
 */
public class User {
    String username;
    String password;
    DynamicBuffer db;
    public Email[] emails;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        db = new DynamicBuffer(4);
    }

    public String getName() {
        return username;
    }

    public boolean checkPassword(String password) { //TODO
        if (password.equals(this.password)) {
            return true;
        }
//        Returns true if the given password matches the user's password exactly.
//        Returns false otherwise.
        return false; //TODO change this
    }

    public int numEmail() { //TODO returns # emails in user inbox
        return db.numElements();
//        int counter = 0;
//        for (int i = 0; i < db.numElements(); i++) {
//            if (db.emails[i] != null) {
//                counter++;
//            }
//        }
//        return counter; // change this
    }

    public void receiveEmail(String sender, String message) { //TODO
        Random ran = new Random();
        db.add(new Email(getName(), sender, ran.nextLong(), message));
//        Add the message to the user's inbox.
//        You should call some operations provided by DynamicBuffer
    }

    public Email[] retrieveEmail(int n) { //TODO
//        Retrieve the n most recent emails in the user's inbox.
//        You should call some operations provided by DynamicBuffer
        return db.getNewest(n); // change this
    }

    public boolean removeEmail(long emailID) { //TODO

        int index = -1;
        for (int i = 0; i < numEmail(); i++) {
            if (db.emails[i].getID() == emailID) {
                index = i;
            }
        }
        if (index == -1) {
            return false;
        }
//        Remove an email with the specified emailID
//        You should call some operations provided by DynamicBuffer
        return db.remove(index);

    }

    public static boolean correctForm(String username, String password) {
        if (username.length() < 1 || username.length() > 20) {
            return false;
        }
        if (password.length() < 4 || password.length() > 40) {
            return false;
        }
        for (int i = 0; i < username.length(); i++) {
            if (!Character.isLetterOrDigit(username.charAt(i))) {
                return false;
            }
        }
        for (int i = 0; i < password.length(); i++) {
            if (!Character.isLetterOrDigit(password.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
