import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client {

    private String proxy_ip;
    private int port;
    private String requested_host;
    final private String requested_url;
    private String local_url;

    public Client(String proxy_ip, int port,String requested_host,String requested_url){
       this.proxy_ip=proxy_ip;
       this.port=port;
       this.requested_host=requested_host;
       this.requested_url = requested_url;
    }

    public void work() {
        try {
            Socket client = new Socket(proxy_ip, port);
            //output stream -- byte stream
            OutputStream client_out=client.getOutputStream();
            // io stream -- char stream
            // PrintWriter(string) ----> OutputStreamWriter ----> OutputStream(bytes)  ||
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(client_out));

            System.out.println(compositeRequest(requested_url,requested_host));
            pw.write(compositeRequest(requested_url,requested_host));
            pw.flush();


            String[] requested_url_array=requested_url.split("/");
            local_url=requested_url_array[requested_url_array.length-1];
            InputStream client_in = client.getInputStream();
            parseResponse(client_in,local_url);
            System.out.println("save done");
            client_in.close();
            client_out.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String compositeRequest(String requested_url,String requested_host){

        return "GET "+ requested_url +" HTTP/1.1\r\n" +
                "Host: " + requested_host + "\r\n" +
                "User-Agent: curl/7.43.0\r\n" +
                "Accept: */*\r\n\r\n";
    }

    public static void parseResponse(InputStream in, String local_url) throws IOException {
        System.out.println("start parse response header");
        StringBuilder header = new StringBuilder();
        String each_line;
        LineBuffer line_buffer = new LineBuffer(3000);

        while (null != (each_line = line_buffer.readLine(in))) {
            System.out.println(each_line);
            header.append(each_line + "\r\n");
            // between header and body
            if (each_line.equals(""))
                break;
        }
        System.out.println("finish parse response header");

        System.out.println("start parse response body");

        int n = 0; // sequence
        int size = 1024;
        byte[] bytes = new byte[size]; //store input stram
        int each_byte; // read from input stream
        try {
            while (-1 != (each_byte = in.read())) {
                //DEBUG LINE
                System.out.print(each_byte + " ");
                if (size == n + 1) {
                    byte[] new_bytes = new byte[2 * size];
                    System.arraycopy(bytes, 0, new_bytes, 0, bytes.length);
                    bytes = new_bytes;
                    size *= 2;
                }
                bytes[n++] = (byte) each_byte;
            }
        } catch (SocketException e) {
            System.out.println(e);
        } finally {
            System.out.println("finish parse response body");
            writeToFile(new String(bytes), local_url);
        }
    }


    public static void writeToFile(String body, String requested_url) throws IOException {
        String filename = requested_url;
        OutputStream file = new FileOutputStream(filename);
        System.out.println("start write");
        file.write(body.getBytes());
        file.close();
        System.out.println("finish write");
    }

    public static void main(String[] args){
        //String requested_host="www.baidu.com";
        String requested_host="gaia.cs.umass.edu";
        String proxy_ip="120.79.147.165";
        //String proxy_ip="127.0.0.1";
        int port=9999;
        String requested_url = "/wireshark-labs/HTTP-wireshark-file1.html";
        Client client=new Client(proxy_ip,port,requested_host,requested_url);
        client.work();

    }

}