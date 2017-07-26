import java.sql.*;

/**
 * Created by nikola on 19.6.17..
 */


public class DataBase {

    /* accessing database instance by using singleton pattern*/

    private static DataBase instance = new DataBase();
    private static Connection conn;
    private static Statement st;
    private static ResultSet rs;
    private String update;


    private DataBase()
    {

    }

    public static DataBase getInstance()
    {

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:/home/nikola/IdeaProjects/JSONServer/BazaPodataka");

            st = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instance;
    }


    public void insert(String recenica) //inserting a line in a database
    {
        try {

            update = "INSERT INTO tabela (TEKST) VALUES ('"+recenica+"');";

            st.execute(update);

            st.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String read() // reading all existing lines from a database
    {
        String povratni="";

        try {
            rs = st.executeQuery("SELECT TEKST FROM tabela;");

        while(rs.next())
            povratni+=rs.getString(1)+"\n";

            if(povratni.length()!=0)
            povratni = povratni.substring(0, povratni.length()-1);

        st.close();
        conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

     return povratni;
    }

}
