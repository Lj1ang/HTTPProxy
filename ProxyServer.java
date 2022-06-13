import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;


class ProxyServer{
    final private int WEBPORT=80;
    private Socket client_socket;


    public ProxyServer(ServerSocket listen) throws IOException {
        this.client_socket = listen.accept();
    }

    public void run() throws IOException {
        // client
        InputStream client_in = client_socket.getInputStream();
        OutputStream client_out = client_socket.getOutputStream();


        //parse request
        String each_line = "";
        String host = "";
        StringBuilder string_builder = new StringBuilder();
        LineBuffer line_buffer = new LineBuffer(1024);
        while (null != (each_line = line_buffer.readLine(client_in))) {
            //add each line to string
            System.out.println(each_line);
            string_builder.append(each_line + "\r\n");

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



        //proxy
        Socket proxy_socket = new Socket(host, WEBPORT);
        InputStream proxy_in = proxy_socket.getInputStream();
        OutputStream proxy_out = proxy_socket.getOutputStream();
        //forward request
        proxy_out.write(string_builder.toString().getBytes());

        // forward all
        new ProxyThread(client_in, proxy_out).start();
        while (true){
            client_out.write(proxy_in.read());
        }
    }


    public static void main(String[] args) throws IOException {
        int port = 9999;
        ServerSocket server_socket = new ServerSocket(9999);
        ProxyServer proxy_server = new ProxyServer(server_socket);
        while (true) {
            proxy_server.run();
        }
    }
}