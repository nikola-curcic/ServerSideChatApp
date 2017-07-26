
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by nikola on 9.6.17..
 */
public class MainKlasa {

     public static ArrayList<Socket> lista = new ArrayList<>();
     public static ArrayList<String> listaOnline = new ArrayList<>();
     private static JSONObject objc = new JSONObject();
     private static PrintWriter os;
     private static String listaString;

    public static void main(String []args) {



        System.out.println("Server Listening......");

        try
        {
            ServerSocket s1 = new ServerSocket(1234);
            Socket ss;

            while (true) {
                   ss = s1.accept();
                   ServerKlasa k = new ServerKlasa(ss);// Creates new thread for new user

                   lista.add(ss);

                System.out.println("connection Established");

                Thread t1 = new Thread(k);
                t1.start();
            }
        }catch(Exception e)
        {

         System.out.println(e);
       }
    }

    public static void addUser(String ime) // Invoked by ServerKlasa for adding new user to a list of users
    {
         StringBuilder builder = new StringBuilder();

         listaString="";
        listaOnline.add(ime);

        for(String s : listaOnline)
            builder.append(s).append("\n");

        listaString = builder.toString();

        add();
    }

    public static void removeUser(String ime) // Invoked by ServerKlasa for removing an user from the list
    {
        StringBuilder builder = new StringBuilder();

        listaString="";
        listaOnline.remove(ime);

        for(String s : listaOnline)
            builder.append(s).append("\n");

        listaString = builder.toString();

        add();
    }


    private static void add() // sends the message to clients about updated list of users
    {
        objc.put("obicna", false);
        objc.put("poruka", listaString);
        objc.put("prva", false);

        for (int i = 0; i < lista.size(); i++) {
            Socket tempSocket = lista.get(i);
            try {
                os = new PrintWriter(tempSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            os.println(objc);
            os.flush();
        }
    }
}

