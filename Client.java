import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;

public class Client {

    public String proxy_ip;
    public int port;
    public String request_host;
    public Client(String proxy_ip, int port,String request_host){
       this.proxy_ip=proxy_ip;
       this.port=port;
       this.request_host=request_host;
    }

    public void work() {
        try {
            Socket client = new Socket(proxy_ip, port);
            //output stream -- byte stream
            OutputStream ops=client.getOutputStream();
            // io stream -- char stream
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(ops));



            //read http request file

            System.out.println(compositeRequest(request_host));
            pw.write(compositeRequest(request_host));
            pw.flush();




            //Print the message in screen
            InputStream in = client.getInputStream();
            String msg;
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            while((msg=reader.readLine())!=null){
                System.out.println(msg);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static String compositeRequest(String request_host){

        return "GET / HTTP/1.1\r\n" +
                "Host: " + request_host + "\r\n" +
                "User-Agent: curl/7.43.0\r\n" +
                "Accept: */*\r\n\r\n";
    }

    public static void main(String[] args){
        String request_host="www.baidu.com";
        //int port = 80;
        String proxy_ip="120.79.147.165";
        int port=9999;
        //String hostname="127.0.0.1";
        //int port=10003;
        Client client=new Client(proxy_ip,port,request_host);
        client.work();

    }


}