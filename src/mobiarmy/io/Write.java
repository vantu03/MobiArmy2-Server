package mobiarmy.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 *
 * @author Văn Tú
 */
public class Write {
    
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    
    
    public Write() {
        this.os = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(this.os);
    }

    public DataOutputStream writer() {
        return this.dos;
    }
    
    public byte[] getBytes() {
        return this.os.toByteArray();
    }
    
    public int size() {
        return this.os.size();
    }
}
