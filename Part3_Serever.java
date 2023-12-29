import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
/*
   Local host web server project
   @author tarek dar aldeek
   github: TarekDeek16
   Dec 22, 2023
*/
public class Part3_Serever {
    public static void main(String[] args) {
        int port = 9966;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("server listening on port " + port); // BufferedReader b1r = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                try (Socket connectionSocket = serverSocket.accept())
                {
                    ConnectionHandling connection = new ConnectionHandling(connectionSocket);
                    connection.handleRequest();
                    connection.response();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
