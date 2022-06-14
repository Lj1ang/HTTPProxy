import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
class ProxyServer{
    final private int WEBPORT=80;
    private Socket client_socket;
    private String host="";

    public ProxyServer(ServerSocket listen) throws IOException {
        this.client_socket = listen.accept();
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
        while (null != (each_line = line_buffer.readLine(client_in))) {
            //add each line to string
            System.out.println(each_line);
            request.append(each_line + "\r\n");

            //parse each line and find host
            if (0 == each_line.length()) {
                break;
            } else {
                String[] temp = each_line.split(" ");
                if (temp[0].contains("Host")) {
                    host = temp[1];
                }
            }
        }

        Socket proxy_socket = new Socket(host, WEBPORT);
        InputStream proxy_in = proxy_socket.getInputStream();
        OutputStream proxy_out = proxy_socket.getOutputStream();
        //forward request
        proxy_out.write(request.toString().getBytes());

        // forward all
        new ProxyThread(client_in, proxy_out).start();
        while (true){
            client_out.write(proxy_in.read());
        }
    }

    public static void writeToFile(String response, String requested_url, String local_filename) throws IOException {
        String filename = "./"+requested_url+"/"+local_filename;
        OutputStream file = new FileOutputStream(filename);
        file.close();
    }

    public static void parseRequest(InputStream in,String host, StringBuilder request) throws IOException {
        //parse request

    }

    public static void main(String[] args) throws IOException{
        int port = 9999;
        ServerSocket server_socket = new ServerSocket(9999);
        ProxyServer proxy_server = new ProxyServer(server_socket);
        while(true){
            proxy_server.run();
        }
    }
}

