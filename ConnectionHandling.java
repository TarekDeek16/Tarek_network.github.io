import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
/*
   @author tarek dar aldeek
   github: TarekDeek16
   Dec 22, 2023
*/

public class ConnectionHandling {
    Socket connectedSocket;
    String method,resource,protocol;
    String headerLines = "";
    ConnectionHandling(Socket socket) throws IOException {
        connectedSocket = socket;
    }
    void handleRequest() throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));
        String requestLine = read.readLine();
        System.out.println(requestLine);
        if (requestLine != null) {
            String[] HTTPparsing = requestLine.split(" "); //1st part: http method
            // second part:path of requested file | 3rd part: HTTP Version
            if(HTTPparsing.length ==3){
                method = HTTPparsing[0];
                resource = HTTPparsing[1];
                protocol = HTTPparsing[2];
                String headerLine="d";
                while (!headerLine.isEmpty()) {
                    headerLine=read.readLine();
                    headerLines+=headerLine+"\n";
                }
                System.out.println(headerLines);
            }
        }
    }
    void  response() throws IOException {
        File f=null;
        int status=0; //-1: error page , 0: to the requested page, 1:redirect to other pages

        if(resource == null) {
            f = new File("C:\\Users\\tarek\\IdeaProjects\\Intellij\\Network_server_Clients_Model\\src\\error.html");
            status=-1;
        }
        else if(resource.compareTo("/index.html") ==0 ||resource.compareTo("main_en.html") ==0||resource.compareTo("/") ==0 ||resource.compareTo("/en") ==0) {
            f = new File("C:\\Users\\tarek\\IdeaProjects\\Intellij\\Network_server_Clients_Model\\src\\main_en.html");
            status=0;
        }
        else if(resource.compareTo("/ar")==0 ||resource.compareTo("/main_ar.html?")==0) {
            f = new File("C:\\Users\\tarek\\IdeaProjects\\Intellij\\Network_server_Clients_Model\\src\\main_ar.html");
            status=0;
        }
        else if(resource.compareTo("/cr")==0){
            status=1;
        }
        else if(resource.compareTo("/so")==0){
            status=2;
        }else if(resource.compareTo("/rt")==0){
            status=3;
        }
        else {
           f = new File("C:\\Users\\tarek\\IdeaProjects\\Intellij\\Network_server_Clients_Model\\src"+resource);
           status=0;
           if(!f.exists()) {
               f = new File("C:\\Users\\tarek\\IdeaProjects\\Intellij\\Network_server_Clients_Model\\src\\error.html");
               status=-1;
           }
        }

        DataOutputStream write = new DataOutputStream(connectedSocket.getOutputStream());
        // 0: good request  -1: error html page  else(1,2,3): redirect
        if(status ==0){
            FileInputStream fileStream = new FileInputStream(f);
            String contentType = Files.probeContentType(f.toPath());
            BufferedInputStream bufInputStream = new BufferedInputStream(fileStream);
            write.writeBytes("HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\n\r\n"); //status line+ header lines
            byte[] bytes = new byte[(int) f.length()];
            bufInputStream.read(bytes); //entity body
            write.write(bytes);
            bufInputStream.close();
        }
        else if(status == -1){
            System.out.println(f.getAbsolutePath());
            FileInputStream fileStream = new FileInputStream(f);
            String contentType = Files.probeContentType(f.toPath());
            BufferedInputStream bufInputStream = new BufferedInputStream(fileStream);
            write.writeBytes("HTTP/1.1 404 Not Found\r\nContent-Type: " + contentType + "\r\n\r\n"); //status line+ header lines
            byte[] bytes = new byte[(int) f.length()];
            bufInputStream.read(bytes); //entity body
            write.write(bytes);
            bufInputStream.close();
        }else{
            write.writeBytes("HTTP/1.1 307 Temporary Redirect"+ "\r\n"); //status line+ header lines
            if(status ==1)
                   write.writeBytes("Location: https://www.cornell.edu/\r\n");
            else if(status ==2)
                write.writeBytes("Location: https://stackoverflow.com/\r\n");
            else if(status==3)
                write.writeBytes("Location: https://ritaj.birzeit.edu/\r\n");
        }
        write.flush();
        write.close();
    }
}
