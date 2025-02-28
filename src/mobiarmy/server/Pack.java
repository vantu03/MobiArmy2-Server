package mobiarmy.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Văn Tú
 */
public class Pack {
    
    private ArrayList<String> entryNames = new ArrayList<>();
    private ArrayList<byte[]> entryData = new ArrayList<>();

    private static final byte[] XOR_KEY = new byte[]{78, 103, 117, 121, 101, 110, 86, 97, 110, 77, 105, 110, 104};

    public static byte[] xorEncrypt(byte[] data) {
        byte[] encryptedData = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            encryptedData[i] = (byte)(data[i] ^ XOR_KEY[i % XOR_KEY.length]);
        }
        return encryptedData;
    }
    
    public void addEntry(String name, byte[] data) {
        this.entryNames.add(name);
        this.entryData.add(data);
    }
    
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(outputStream);
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        
        dataStream.writeByte(entryNames.size());
        
        for (int i = 0; i < entryNames.size(); i++) {
            String entryName = entryNames.get(i);
            dataStream.writeByte(entryName.length());
            byte[] nameBytes = entryName.getBytes();
            dataStream.write(xorEncrypt(nameBytes), 0, nameBytes.length);
            dataStream.writeShort(entryData.get(i).length);
            dataBuffer.write(entryData.get(i), 0, entryData.get(i).length);
        }
        
        byte[] dataBytes = dataBuffer.toByteArray();
        dataStream.write(xorEncrypt(dataBytes), 0, dataBytes.length);
        
        return outputStream.toByteArray();
    }
}