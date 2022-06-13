import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProxyThread extends Thread{
    private InputStream in;
    private OutputStream out;

    public ProxyThread(InputStream in, OutputStream out){
        this.in=in;
        this.out=out;
    }

    @Override
    public void run() {
        try {
            out.write(in.read());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
