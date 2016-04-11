import com.sun.org.apache.regexp.internal.RE;

import java.io.*;
import java.util.*;

/**
 * <b> CS 180 - Project 4 - Email Server Skeleton </b>
 * <p>
 * <p>
 * This is the skeleton code for the EmailServer Class. This is a private email server for you and your friends to
 * communicate.
 *
 * @author Brian Duffy <duffy18@purdue.edu>
 * @version 4/6/2016
 * @lab 2
 */

public class EmailServer {
    // Useful constants
    public static final String FAILURE = "FAILURE";
    public static final String DELIMITER = "\t";
    public static final String SUCCESS = "SUCCESS";
    public static final String CRLF = "\r\n";

    // Used to print out extra information
    private boolean verbose = false;
    User root;
    int maxUsers;
    User[] users;
    DynamicBuffer db;
    File file;

    boolean io;

    public EmailServer() {
        root = new User("root", "cs180");
        maxUsers = 100;
        users = new User[1];
        users[0] = root;
        this.db = new DynamicBuffer(4);
        this.io = false;
    }

    public EmailServer(String fileName) throws IOException {
        root = new User("root", "cs180");
        maxUsers = 100;
        users = new User[1];
        users[0] = root;
        this.db = new DynamicBuffer(4);
        this.io = true;
        this.file = new File(fileName);
        if (new File(fileName).exists()) {
            try {
                FileReader fr = new FileReader(fileName);
                BufferedReader reader = new BufferedReader(fr);

                int counter = 1;
                String s = reader.readLine();
                System.out.println(s);
                while (s != null || s.length() == 0) {

                    String[] temp = s.split(",");
                    if (temp.length == 2) {
                        users = Arrays.copyOf(users, users.length + 1);
                        users[counter] = new User(temp[0], temp[1]);
                        counter++;
                    }
                    if (s.equals("")) {
                        break;
                    }
                    s = reader.readLine();
                }
                reader.close();
                fr.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            file = new File(fileName);
            file.createNewFile();
            PrintWriter out = new PrintWriter(file);
            for (int i = 0; i < users.length; i++) {
                out.printf(users[i].getName() + "," + users[i].password + "\r\n");
            }
            out.close();

        }

    }


    public String addUser(String[] args) { //TODO work on this

        if (args[0].equals("ADD-USER")) {
            args = Arrays.copyOfRange(args, 1, 3);
        }
        String lastParam;
        if (args[1].contains("\r\n")) {
            lastParam = args[1].substring(0, args[1].length() - 2);
        } else {
            lastParam = args[1].substring(0, args[1].length());
        }
        if (userExists(args[0])) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.USER_EXIST_ERROR);
        } else {
            if (!(User.correctForm(args[0], lastParam))) {
                return new ErrorFactory().makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
            }
            users = Arrays.copyOf(users, users.length + 1);
            users[users.length - 1] = new User(args[0], lastParam);
        }
        if (io) {
            try {
                FileReader fr = new FileReader(file.getName());
                BufferedReader reader = new BufferedReader(fr);
                String s = reader.readLine() + "\n";
                String t = "";
                while (s != null ^ s.length() == 0) {
                    if (s.equals("\n") || s.equals("null\n")) {
                        break;
                    }
                    t = t + s;
                    s = reader.readLine() + "\n";
                }
                FileOutputStream fos = new FileOutputStream(file.getName());
                PrintWriter pw = new PrintWriter(fos);
                pw.append(t + args[0] + "," + args[1]);
                pw.close();
                fos.close();
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "SUCCESS\r\n"; //change this
    }

    public String getAllUsers(String[] args) { //TODO
        if (args[0].equals("GET-ALL-USERS")) {
            args = Arrays.copyOfRange(args, 1, 3);
        }
        String lastParam;
        if (args[1].contains("\r\n")) {
            lastParam = args[1].substring(0, args[1].length() - 2);
        } else {
            lastParam = args[1].substring(0, args[1].length());
        }
        String allUsers = "SUCCESS";
        // A correct pair of username and password should be verified, so that the server can return
        // a list of all usernames.
        for (int i = 0; i < users.length; i++) {
            if (userExists(args[0]) && users[i].getName().equals(args[0])) {
                if (users[i].checkPassword(lastParam)) {
                    for (int j = 0; j < users.length; j++) {
                        allUsers = allUsers + "\t" + users[j].getName();
                    }
                    return allUsers + "\r\n";
                } else {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
                }
            } else {
                return new ErrorFactory().makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
            }
        }
        return allUsers + "\r\n";
    }

    public String deleteUser(String[] args) { //TODO
        if (args[0].equals("DELETE-USER")) {
            args = Arrays.copyOfRange(args, 1, 3);
        }
        if (args[0].equals("root")) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
        }
        String lastParam;
        if (args[1].contains("\r\n")) {
            lastParam = args[1].substring(0, args[1].length() - 2);
        } else {
            lastParam = args[1].substring(0, args[1].length());
        }

        if (!userExists(args[0])) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
        } else {
            for (int i = 0; i < users.length; i++) {
                if (users[i].checkPassword(args[1]) && !(users[i].getName().equals("root"))) {
                    users[i] = null;
                    for (int j = i + 1; j < users.length; j++) {
                        users[j] = users[j - 1];
                    }
                    users = Arrays.copyOf(users, users.length - 1);
                    break;
                }
                if (i == users.length) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
                }
            }
        }
        // For the request to succeed, the correct username and password are required.
        // cannot delete root user
        if (io) {
            try {
                FileReader fr = new FileReader(file.getName());
                BufferedReader br = new BufferedReader(fr);
                String s = br.readLine() + "\n";
                String t = "";
                while (s != null ^ s.length() == 0) {
                    if (s.equals("\n")) {
                        break;
                    }
                    t = t + s;
                    s = br.readLine() + "\n";
                }
                br.close();
                fr.close();
                t = t.replace("\n" + args[0] + "," + args[1] + "\n", "");
                FileOutputStream fos = new FileOutputStream(file.getName());
                PrintWriter pw = new PrintWriter(fos);
                pw.write(t);
                pw.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return "SUCCESS\r\n";
    }

    public boolean userExists(String username) {

        for (int i = 0; i < users.length; i++) {
            if (users[i].getName().equals(username)) {
                return true;
            }
            if (!(users[i].getName().equals(username)) && i == users.length - 1) {
                return false;
            }
        }
        return true;
    }

    public String sendEmail(String[] args) { //TODO check last param
        if (args[0].equals("SEND-EMAIL")) {
            args = Arrays.copyOfRange(args, 1, 5);
        }
        String lastParam;
        if (args[1].contains("\r\n")) {
            lastParam = args[3].substring(0, args[3].length() - 2);
        } else {
            lastParam = args[3].substring(0, args[3].length());
        }
        if (!userExists(args[0])) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
        }

        for (int i = 0; i < users.length; i++) {
            if (users[i].getName().equals(args[0]) && users[i].checkPassword(args[1])
                    && userExists(args[2]) && args[3].trim().length() > 0) {
                for (int j = 0; j < users.length; j++) {
                    if (users[j].getName().equals(args[2])) {
                        users[j].receiveEmail(args[0], args[2]);
                    }
                }
            }
            if (users[i].getName().equals(args[0]) && !users[i].checkPassword(args[1])) {
                return new ErrorFactory().makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
            }
        }
        if (!userExists(args[2])) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
        }

//        For the request to succeed, the message should contain at least 1 character after removing leading and
// trailing
//        white spaces from the message.
//        Hint:you can use myString.trim() to remove leading and trailing whitespace from a string
        return "SUCCESS\r\n";
    }

    public String getEmails(String[] args) { //TODO
        if (args[0].equals("GET-EMAILS")) {
            args = Arrays.copyOfRange(args, 1, 4);
        }

        String emailz = "\t";
        String lastParam;
        if (args[2].contains("\r\n")) {
            lastParam = args[2].substring(0, args[2].length() - 2);
        } else {
            lastParam = args[2].substring(0, args[2].length());
        }
        if (Integer.parseInt(lastParam) < 1) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
        }
        Email[] temp;
        if (userExists(args[0])) {
            for (int i = 0; i < users.length; i++) {
                if (users[i].getName().equals(args[0])) {
                    if (users[i].checkPassword(args[1])) {
                        temp = users[i].retrieveEmail(Integer.parseInt(lastParam));
                        if (temp == null) {
                            return "SUCCESS\r\n";
                        }
                        for (int k = 0; k < temp.length; k++) {
                            emailz = emailz + temp[k].toString();
                        }
                    } else {
                        return new ErrorFactory().makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
                    }
                } else {
                    continue;
                }
            }
        }
        if (!userExists(args[0])) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
        }
//        The number of messages requested must be >= 1, otherwise an INVALID_VALUE_ERROR (error #-23) is returned.
//        The number of messages requested can be higher than the number of available messages; in this case the
// function
//        returns as many as possible. It can also return 0 messages if none are available (SUCCESS\r\n).
        return "SUCCESS\r\n" + emailz;
    }

    public String deleteEmail(String[] args) { //TODO
        if (args[0].equals("DELETE-EMAIL")) {
            args = Arrays.copyOfRange(args, 1, 4);
        }
        if (userExists(args[0])) {
            for (int i = 0; i < users.length; i++) {
                if (users[i].getName().equals(args[0]) && users[i].checkPassword(args[1])) {
                    if (users[i].db.emails == null || users[i].db.numElements() == 0) {
                        return new ErrorFactory().makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                    }
                    for (int j = 0; j < users[i].numEmail(); j++) {
                        if (users[i].db.emails[j].getID() == Long.parseLong(args[2])) {
                            users[i].removeEmail(Long.parseLong(args[2]));
                            return "SUCCESS\r\n";
                        }
                        if (users[i].db.emails[j].getID() != Long.parseLong(args[2]) && j == users[i].numEmail()) {
                            return new ErrorFactory().makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                        }
                    }
                }
                if (users[i].getName().equals(args[0]) && !users[i].checkPassword(args[1])) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.AUTHENTICATION_ERROR);
                }
            }
//TODO
        }
        if (!userExists(args[0])) {
            return new ErrorFactory().makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
        }
//        To delete an email, not only the correct username and password should be verified,
//                but also a correct email ID must be specified.
//        The email ID information can be retrieved in the GET-EMAILS command
        return "SUCCESS\r\n";
    }

    /**
     * Determines which client command the request is using and calls the function associated with that command.
     *
     * @param request - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {
        // TODO: implement this method
//        Returns the server's response to the request
//        The argument request is the complete line of the client request.
//                The input to this method is not validated, you must perform validation.

//        In this method, you must perform these tasks in the following order:
//        Parse the client request (that means split the request into parameters).
//        Check format validity (correct command and number of parameters).
//        Verify the username and password.
//        Invoke the appropriate protocol method specified in the below Protocol Methods section.
//        Hint: this is a great time to use a switch statement.
//        Return the correct server response based on the success, failure, or improper format of the request.
        String[] input = request.split("\t");
        switch (input[0]) {
            case "ADD-USER": {
                if (input.length != 3 || !input[2].endsWith("\r\n")) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
                }
                return addUser(Arrays.copyOfRange(input, 1, 3));
            }
            case "GET-ALL-USERS": {
                if (input.length != 3 || !input[2].endsWith("\r\n")) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
                }

                return getAllUsers(Arrays.copyOfRange(input, 1, 3));

            }
            case "DELETE-USER": {
                if (input[1].equals("root")) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.INVALID_VALUE_ERROR);
                }
                if (userExists(input[1])) {
                    if (input.length != 3 || !input[2].endsWith("\r\n")) {
                        return new ErrorFactory().makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
                    }
                    return deleteEmail(Arrays.copyOfRange(input, 1, 3));
                } else {
                    return ErrorFactory.makeErrorMessage(ErrorFactory.USERNAME_LOOKUP_ERROR);
                }
            }
            case "SEND-EMAIL": {
                if (input.length != 5 || !input[4].endsWith("\r\n")) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
                }
                return sendEmail(Arrays.copyOfRange(input, 1, 5));

            }
            case "GET-EMAILS": {
                if (input.length != 4 || !input[3].endsWith("\r\n")) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
                }
                return getEmails(Arrays.copyOfRange(input, 1, 4));

            }
            case "DELETE-EMAIL": {
                if (input.length != 4 || !input[3].endsWith("\r\n")) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
                }
                return deleteEmail(Arrays.copyOfRange(input, 1, 4));
            }
            default: {
                String s;
                if (input[0].endsWith("\r\n")) {
                    s = input[0].substring(0, input[0].length() - 2);
                } else {
                    s = input[0];
                }
                if (!(s.equals("ADD-USER") || s.equals("DELETE-USER") || s.equals("GET-ALL-USERS")
                        || s.equals("SEND-EMAIL") || s.equals("GET-EMAILS") || s.equals("DELETE-EMAIL"))) {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.UNKNOWN_COMMAND_ERROR);
                } else {
                    return new ErrorFactory().makeErrorMessage(ErrorFactory.FORMAT_COMMAND_ERROR);
                }
            }

        }

    }


    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            command = replaceEscapeChars(command);

            if (command.equalsIgnoreCase("kill") || command.equalsIgnoreCase("kill\r\n"))
                break;

            if (command.equalsIgnoreCase("verbose") || command.equalsIgnoreCase("verbose\r\n")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getClass().toString()));
            }

            // change the formatting of the server response so it prints well on the terminal
            // (for testing purposes only)
            //if (response.startsWith("SUCCESS" + DELIMITER))
            //	response = response.replace(DELIMITER, NEWLINE);
            if (response.startsWith(FAILURE) && !DELIMITER.equals("\t"))
                response = response.replace(DELIMITER, "\t");

            if (verbose)
                System.out.print("response: ");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }


    /**
     * Replaces "poorly formatted" escape characters with their proper values. For some terminals, when escaped
     * characters are entered, the terminal includes the "\" as a character instead of entering the escape character.
     * This function replaces the incorrectly inputed characters with their proper escaped characters.
     *
     * @param str - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r\\n", "\r\n"); // may not be necessary, but just in case
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");
        str = str.replace("\\f", "\f");

        return str;
    }

    /**
     * This main method is for testing purposes only.
     *
     * @param args - the command line arguments
     */
    public static void main(String[] args) {
        (new EmailServer()).run();
    }
}

