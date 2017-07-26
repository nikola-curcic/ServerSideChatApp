import com.sun.corba.se.spi.activation.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by nikola on 11.6.17..
 */
public class ServerKlasa implements Runnable{

    private Socket s;
    private BufferedReader is;
    private JSONParser parser = new JSONParser();
    private PrintWriter os;
    private JSONObject objc = new JSONObject();
    private JSONObject receiveObj = new JSONObject();
    private String imeTreda;

    public ServerKlasa(Socket s)
    {
        this.s = s;
    }

    @Override
    public void run() {

        try {
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line;


            try {
                while ((line = is.readLine()) != null) {

                    try {
                        receiveObj = (JSONObject) parser.parse(line);

                        if ((Boolean) receiveObj.get("obicna_poruka") == false) { //false means that this is not a regular message, but is a message about logging of new user

                            this.imeTreda = (String) receiveObj.get("poruka");

                            MainKlasa.addUser(this.imeTreda);
                            odgovori(true, DataBase.getInstance().read(), true); // reads current lines from database to be send to new created user
                            odgovori(true, "Konektovan je korisnik " + this.imeTreda + ".", false); //reply message to all users
                            DataBase.getInstance().insert("Konektovan je korisnik " + this.imeTreda + "."); //writes history to database
                        } else {
                            odgovori(true, this.imeTreda + " kaze: " + receiveObj.get("poruka"), false); //regular message sent to all users
                            DataBase.getInstance().insert(this.imeTreda + " kaze: " + receiveObj.get("poruka"));//writes message to database
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
               /*following lines regulate the disconnection of an user */
                MainKlasa.removeUser(this.imeTreda);
                odgovori(true, "Diskonektovan je korisnik " + this.imeTreda + ".", false);
                DataBase.getInstance().insert("Diskonektovan je korisnik " + this.imeTreda + ".");
                System.out.println("Klijent diskonektovan");
            } catch (IOException e) {
                e.printStackTrace();
            }
         }

        private void odgovori (Boolean obicna, String poruka, Boolean prva) throws IOException //through this method messages are send to client side
        {

            objc.put("prva", prva);
            objc.put("obicna", obicna);
            objc.put("poruka", poruka);

            System.out.println(objc);

       for (int i = 0; i < MainKlasa.lista.size(); i++) {
           Socket tempSocket = MainKlasa.lista.get(i);
           os = new PrintWriter(tempSocket.getOutputStream());
           os.println(objc);
           os.flush();
       }
    }

}


