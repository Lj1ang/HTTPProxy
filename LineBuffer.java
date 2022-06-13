import java.io.*;

//read stream into line

public class LineBuffer {
    private int size;

    public LineBuffer(int size){
        this.size=size;
    }

    public String readLine(InputStream in)throws IOException{
        int flag=0; // \r or \n
        int n=0; // sequence
        byte[] bytes=new byte[this.size]; //store input stram
        int each_byte; // read from input stream
        while(-1 != (each_byte=in.read())) {
            bytes[n++] = (byte) each_byte;
            if (each_byte == '\r' && 0 == flag % 2) {
                flag++;
            } else if (each_byte == '\n' && 1 == flag % 2) {
                flag++;
                if (2 == flag) {
                    return new String(bytes, 0, n - 2);
                }
            } else {
                 //next byte
                flag = 0;
            }
            //overflow
            if (this.size == n) {
                byte[] new_bytes = new byte[2 * this.size];
                System.arraycopy(bytes, 0, new_bytes, 0, bytes.length);
                bytes = new_bytes;
            }
        }
        return null;
    }

}
