import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;

public class Client {

    public String hostname;
    public int port;

    public Client(String hostname, int port){
       this.hostname=hostname;
       this.port=port;
    }

    public void work() {
        try {
            Socket client = new Socket(hostname, port);
            //output stream -- byte stream
            OutputStream ops=client.getOutputStream();
            String mAg="hello";
            ops.write(mAg.getBytes("UTF-8"));

            // io stream -- char stream
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(ops));



            //read http request file

            System.out.println(compositeRequest(hostname));
            pw.write(compositeRequest(hostname));
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



    public static String compositeRequest(String hostname){

        return "GET / HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n" +
                "User-Agent: curl/7.43.0\r\n" +
                "Accept: */*\r\n\r\n";
    }

    public static void main(String[] args){
        //String hostname="www.baidu.com";
        //int port = 80;
        String hostname="120.79.147.165";
        int port=9999;
        //String hostname="127.0.0.1";
        //int port=10003;
        Client client=new Client(hostname,port);
        client.work();

    }


}