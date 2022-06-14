import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;

public class Client {

    private String proxy_ip;
    private int port;
    private String requested_host;
    private String requested_url;
    private String local_filename;

    public Client(String proxy_ip, int port,String requested_host,String requested_url,String local_filename){
       this.proxy_ip=proxy_ip;
       this.port=port;
       this.requested_host=requested_host;
       this.requested_url = requested_url;
       this.local_filename=local_filename;
    }

    public void work() {
        try {
            Socket client = new Socket(proxy_ip, port);
            //output stream -- byte stream
            OutputStream client_out=client.getOutputStream();
            // io stream -- char stream
            // PrintWriter(string) ----> OutputStreamWriter ----> OutputStream(bytes)  ||
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(client_out));

            System.out.println(compositeRequest(requested_host));
            pw.write(compositeRequest(requested_host));
            pw.flush();

            InputStream client_in = client.getInputStream();
            parseResponse(client_in,requested_url,local_filename);
            System.out.println("save done");

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

    public static void parseResponse(InputStream in, String requested_url,String local_filename) throws IOException {
        StringBuilder header=new StringBuilder();

        String each_line="";
        LineBuffer line_buffer = new LineBuffer(3000);
        int flag=0;
        while(null != (each_line = line_buffer.readLine(in) )){
            if(0==flag){
                System.out.println(each_line);
                header.append(each_line+"\r\n");
                // between header and body
                if (each_line.equals(""))
                    break;
            }
        }

        // body
        BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder body = new StringBuilder();
        String line = "";
        int n=0; // sequence
        int size = 1024;
        byte[] bytes=new byte[size]; //store input stram
        int each_byte; // read from input stream
        while(255!=(each_byte=in.read())) {
            //DEBUG LINE
            System.out.println(each_byte);

            if (size == n+1) {
                byte[] new_bytes = new byte[2 * size];
                System.arraycopy(bytes, 0, new_bytes, 0, bytes.length);
                bytes = new_bytes;
                size*=2;
            }
            bytes[n++] = (byte) each_byte;
        }

        writeToFile(new String(bytes), requested_url, local_filename);

    }


    public static void writeToFile(String body, String requested_url, String local_filename) throws IOException {
        String filename = "."+requested_url+local_filename;
        OutputStream file = new FileOutputStream(filename);
        System.out.println("start write");
        file.write(body.getBytes());
        file.close();
    }

    public static void main(String[] args){
        //String requested_host="www.baidu.com";
        String requested_host="120.79.147.165";
        String proxy_ip="120.79.147.165";
        //String proxy_ip="127.0.0.1";
        int port=9999;
        String requested_url = "/";
        String local_filename="baidu.html";
        Client client=new Client(proxy_ip,port,requested_host,requested_url,local_filename);
        client.work();

    }

}