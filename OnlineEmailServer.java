import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import cs180.net.ServerSocket;
import cs180.net.Socket;

/**
 * Created by Brian Duffy on 3/24/2016.
 *
 * @author Brian Duffy <duffy18@purdue.edu>
 * @version 4/6/2016
 * @lab 2
 */
public class OnlineEmailServer extends EmailServer {
    int port;
    InputStream in;
    OutputStream out;
    ServerSocket serverSocket;
    boolean b = true;

    public OnlineEmailServer(String fileName, int port) throws IOException {
        super(fileName);
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);

    }

    @Override
    public void run() {
        while (b) {
            try {
                Socket client = this.serverSocket.accept();
                client.setReuseAddress(true);
                client.setSoTimeout(60 * 1000);
                //while (!client.isClosed()) {
                processClient(client);
                //  }
                client.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void processClient(Socket client) throws IOException {
        client.setReuseAddress(true);
        client.setSoTimeout(60 * 1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter pw = new PrintWriter(client.getOutputStream());
        while (!client.isClosed()) {
            String g = br.readLine();
            if (g == null) {
                break;
            }
            if (g.startsWith("ADD-USER")) {
                String[] temp = g.split("\t");
                pw.printf(addUser(temp));
                pw.flush();
            } else if (g.startsWith("DELETE-USER")) {
                String[] temp = g.split("\t");
                pw.printf(deleteUser(temp));
                pw.flush();
            } else if (g.startsWith("GET-ALL-USERS")) {
                String[] temp = g.split("\t");
                pw.printf(getAllUsers(temp));
                pw.flush();
            } else if (g.startsWith("DELETE-EMAIL")) {
                String[] temp = g.split("\t");
                pw.printf(deleteEmail(temp));
                pw.flush();
            } else if (g.startsWith("SEND-EMAIL")) {
                String[] temp = g.split("\t");
                pw.printf(sendEmail(temp));
                pw.flush();
            } else if (g.startsWith("GET-EMAILS")) {
                String[] temp = g.split("\t");
                pw.printf(getEmails(temp));
                pw.flush();
            }
        }
        pw.close();
        br.close();
        client.close();

//        This method handles all communication with a client socket.
//        It scans input, calls the appropriate methods in EmailServer and sends output.
    }

    public void stop() {
        b = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            OnlineEmailServer oes = new OnlineEmailServer("test_student.csv", 23566);
            oes.run();
            oes.stop();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
