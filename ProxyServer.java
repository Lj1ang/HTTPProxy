import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
class ProxyServer{
    final private int WEBPORT=80;
    private Socket client_socket;
    private String host;
    private String requested_url;
    private String local_url;

    public ProxyServer(ServerSocket listen) throws IOException {
        this.client_socket = listen.accept();
        host="";
        requested_url="";
        local_url="";
    }

    public void run() throws IOException {
        // client
        InputStream client_in = client_socket.getInputStream();
        OutputStream client_out = client_socket.getOutputStream();

        //proxy

        StringBuilder request = new StringBuilder();
        //parseRequest(client_in, host, request);
        String each_line = "";
        LineBuffer line_buffer = new LineBuffer(1024);
        System.out.println("start parse header");
        while (null != (each_line = line_buffer.readLine(client_in))) {
            //add each line to string
            System.out.println(each_line);
            request.append(each_line + "\r\n");

            //parse each line and find host
            if (0 == each_line.length()) {
                break;
            } else {
                String[] temp = each_line.split(" ");

                if (temp[0].contains("GET")){

                    String[] requested_url_array = temp[1].split("/");
                    requested_url = requested_url_array[requested_url_array.length-1];

                }
                if (temp[0].contains("Host")) {
                    host = temp[1];
                }
            }
        }
        System.out.println("finish parse header");


        local_url = "../"+requested_url;
        File file = new File(local_url);
        // there is a file locally or not
        if(!file.exists()){
            Socket proxy_socket = new Socket(host, WEBPORT);
            InputStream proxy_in = proxy_socket.getInputStream();
            OutputStream proxy_out = proxy_socket.getOutputStream();
            //forward request
            proxy_out.write(request.toString().getBytes());
            //parse response and save it local
            parseResponse(proxy_in,local_url);
        }
        //manually forward with file
        System.out.println("start forward to client");
        InputStream file_input_stream = new FileInputStream(file);
        int count;
        while((count = file_input_stream.read()) > 0){
            client_out.write((byte)count);
        }
        client_out.close();
        while(-1!=client_in.read()){

        }
        client_in.close();
        client_out.close();
        client_socket.close();

        System.out.println("finish forward to client");
        System.out.println("------------");

    }
    public static void parseResponse(InputStream proxy_in, String local_url) throws IOException {
        System.out.println("start parse response header");
        StringBuilder header=new StringBuilder();
        String each_line="";
        LineBuffer line_buffer = new LineBuffer(3000);
        int flag=0;
        while(null != (each_line = line_buffer.readLine(proxy_in) )){
            if(0==flag){
                System.out.println(each_line);
                header.append(each_line+"\r\n");
                // between header and body
                if (each_line.equals(""))
                    break;
            }
        }
        System.out.println("finish parse response header");

        System.out.println("start parse response body");
        // body
        int n=0; // sequence
        int size = 1024;
        byte[] bytes=new byte[size]; //store input stram
        int each_byte; // read from input stream
        while(-1 != (each_byte=proxy_in.read())) {
            //DEBUG LINE
            System.out.print(each_byte+" ");
            if (size == n+1) {
                byte[] new_bytes = new byte[2 * size];
                System.arraycopy(bytes, 0, new_bytes, 0, bytes.length);
                bytes = new_bytes;
                size*=2;
            }
            bytes[n++] = (byte) each_byte;
        }

        System.out.println("finish parse response body");

        writeToFile(new String(bytes), local_url);
    }

    public static void writeToFile(String body, String local_url) throws IOException {
        String filename = local_url;
        OutputStream file = new FileOutputStream(filename);
        System.out.println("start write");
        file.write(body.getBytes());
        System.out.println("finish write");
        file.close();
    }

    public static void main(String[] args) throws IOException{
        int port = 9999;
        ServerSocket server_socket = new ServerSocket(9999);
        ProxyServer proxy_server = new ProxyServer(server_socket);
        proxy_server.run();
    }
}

