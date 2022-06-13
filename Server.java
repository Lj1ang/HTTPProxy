import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {

        //创建服务器套接字实例，设置监听端口为2000
        ServerSocket server=new ServerSocket(10003);
        //开始监听客户端的请求，并阻塞
        Socket socket=server.accept();
        //请求收到后，自动建立连接。通过IO流进行数据传输
        System.out.println("connection done");

        OutputStream os=socket.getOutputStream();
        PrintWriter pw=new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)),true);
        pw.write("hello");
        pw.flush();
        //因为我关闭了输出流，所以另一端的readLine方法才正常结束了
        socket.shutdownOutput();

        InputStream is=socket.getInputStream();
        InputStreamReader isr=new InputStreamReader(is);
        BufferedReader br=new BufferedReader(isr);
        while(true) {
            String str=br.readLine();
            if(str.equals("quit")) {
                break;
            }
            System.out.println("Client said: "+str);
        }
        socket.shutdownInput();
        //socket.shutdownOutput();
        socket.close();
        server.close();
    }
}

