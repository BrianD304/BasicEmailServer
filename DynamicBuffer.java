import java.util.Arrays;

/**
 * Created by Brian Duffy on 3/24/2016.
 *
 * @author Brian Duffy <duffy18@purdue.edu>
 * @version 4/6/2016
 * @lab 2
 */
public class DynamicBuffer {

    int initSize; // can be predetermined
    Email[] emails;

    public DynamicBuffer(int initSize) {
        this.initSize = initSize;
        this.emails = new Email[initSize];
    }

    //    The buffer will have an array with an initial size.
//    You can choose whatever initial size you want.
//    You will double the size of the array when there is not enough space for new emails
//You will reduce the size to half (length/2) when the number of emails
// is less than or equal to 1/4 the size of the array.
// i.e. numElements() ⇐ emailArray.length / 4
//    The buffer size should never be lower than the initial size
    public int numElements() {
        int counter = 0;
        for (int i = 0; i < emails.length; i++) { //TODO returns num emails in array
            if (emails[i] == null) {
                return counter;
            } else {
                counter++;
            }
        }
        return counter;
    }

    public int getBufferSize() { //TODO returns array size of emails
        return emails.length;
    }

    public void add(Email email) { //TODO adds an email to email array

        for (int i = numElements(); i < emails.length; i++) {
            emails[i] = email;
            break;
        }
        if (numElements() == initSize) {
            initSize = initSize * 2;
            emails = Arrays.copyOf(emails, initSize);
        }
//        If the array becomes full by adding this email double its size.

    }

    public boolean remove(int index) { //TODO removes the email at specified index
        if (emails[index] == null) {
            return false;
        }
        for (int i = index + 1; i < emails.length; i++) {
            emails[i - 1] = emails[i];
        }
        if (numElements() <= (.25 * emails.length)) {
            emails = Arrays.copyOf(emails, emails.length / 2);
        }
//        Removes an email at the specified index from the buffer
//        Return true if the index is valid and an email is removed; else return false.
//       If the number of emails in the buffer becomes less than or equal to one fourth
// of the buffer size after the removal,
//       shrink the buffer size to half of the current buffer size.
//        Note that the buffer size should never be lower than the initial size.
        return true; //TODO change this
    }

    public Email[] getNewest(int n) { //TODO Gets the n newest emails from the buffer
        Email[] temp = new Email[n];
        if (n < 0 || numElements() == 0) {
            return null;
        }
        if (n > numElements()) {
            for (int i = 0; i < numElements(); i++) {
                temp[i] = emails[i];
            }
            temp = Arrays.copyOf(temp, temp.length - (n - numElements()));
        } else {
            int counter = 0;
            for (int i = numElements(); i > 0; i--) {
                temp[counter] = emails[i - 1];
                counter++;
                if (counter == n) {
                    break;
                }
            }
        }

//        Returned emails must be sorted from newest to oldest
//        Return all emails if n is greater than the number of emails in the buffer
//        Return null if the buffer is empty or an invalid number of emails is requested (e.g. -1)
        return temp;
    }


}
