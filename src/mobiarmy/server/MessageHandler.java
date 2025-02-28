package mobiarmy.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import mobiarmy.io.Message;

public class MessageHandler {

    private byte                curR, curW;
    private byte[]              key;
    private int                 sendByteCount;
    private int                 recvByteCount;
    private boolean             isKeyInitialized;
    private DataInputStream     inputStream;
    private DataOutputStream    outputStream;
    public final List<Message>  sendingMessages;
    public final List<Message>  receivedMessages;
    private volatile boolean    isRunning = true;
    private Session             session;

    public MessageHandler(Session session) throws IOException {
        this.session = session;
        this.inputStream = new DataInputStream(this.session.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.session.socket.getOutputStream());
        this.key = "vantu".getBytes();
        this.sendingMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }
    
    public void startReadingThread() {
        new Thread(() -> {
            try {
                while (isRunning) {
                    Message message = readMessage();
                    synchronized (this.receivedMessages) {
                        receivedMessages.add(message);
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {}
            session.requestDisconnect();
        }).start();
    }
    
    public void startSendingThread() {
        new Thread(() -> {
            while (isRunning) {
                try {
                    synchronized (sendingMessages) {
                        if (!sendingMessages.isEmpty()) {
                            Message message = sendingMessages.remove(0);
                            doSendMessage(message);
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (IOException | InterruptedException e) {
                }
            }
        }).start();
    }

    public void close() throws IOException {
        isRunning = false;
        if (inputStream != null) inputStream.close();
        if (outputStream != null) outputStream.close();
    }

    private Message readMessage() throws IOException {
        byte cmd = this.inputStream.readByte();
        if (this.isKeyInitialized) {
            cmd = readKey(cmd);
        }
        int size;
        if (this.isKeyInitialized) {
            byte b1 = this.inputStream.readByte();
            byte b2 = this.inputStream.readByte();
            size = (this.readKey(b1) & 255) << 8 | this.readKey(b2) & 255;
        } else {
            size = this.inputStream.readUnsignedShort();
        }
        byte data[] = new byte[size];
        int len = 0;
        int byteRead = 0;
        while (len != -1 && byteRead < size) {
            len = this.inputStream.read(data, byteRead, size - byteRead);
            if (len > 0) {
                byteRead += len;
            }
        }
        if (this.isKeyInitialized) {
            for (int i = 0; i < data.length; i++) {
                data[i] = this.readKey(data[i]);
            }
        }
        this.recvByteCount += 5 + size;
        Message msg = new Message(cmd, data);
        return msg;
    }

    public void doSendMessage(Message message) throws IOException {
        byte[] data = message.getData();
        byte cmd = message.getCommand();
        if (data != null) {
            if (this.isKeyInitialized) {
                this.outputStream.writeByte(writeKey(cmd));
            } else {
                this.outputStream.writeByte(cmd);
            }
            if (cmd == 90) {
                this.outputStream.writeInt(data.length);
                this.outputStream.write(data);
            } else {
                int size = data.length;
                if(this.isKeyInitialized) {
                    this.outputStream.writeByte(writeKey((byte)(size >> 8)));
                    this.outputStream.writeByte(writeKey((byte)(size & 0xFF)));
                } else {
                    this.outputStream.writeShort(size);
                }
                if(this.isKeyInitialized) {
                    for (int i = 0; i < data.length; i++) {
                        data[i] = writeKey(data[i]);
                    }
                }
                this.outputStream.write(data);
            }
            this.sendByteCount += 5 + data.length;
        } else {
            this.sendByteCount += 5;
        }
        this.outputStream.flush();
    }

    private byte readKey(byte b) {
        byte i = (byte)((this.key[this.curR++] & 255) ^ (b & 255));
        if (this.curR >= this.key.length) {
            this.curR %= this.key.length;
        }
        return i;
    }

    private byte writeKey(byte b) {
        byte i = (byte)((this.key[this.curW++] & 255) ^ (b & 255));
        if (this.curW >= this.key.length) {
            this.curW %= this.key.length;
        }
        return i;
    }
    
    public void setKey() throws IOException {
        Message message = new Message(-27);
        message.writer().writeByte(this.key.length);
        message.writer().writeByte(this.key[0]);
        for(int i = 1; i < this.key.length; i++) {
            message.writer().writeByte(this.key[i] ^ this.key[i-1]);
        }
        this.doSendMessage(message);
        this.isKeyInitialized = true;
    }
    
    public Message popMessage() {
        synchronized (this.receivedMessages) {
            return this.receivedMessages.isEmpty() ? null : this.receivedMessages.remove(0);
        }
    }
    
    public void addMessage(Message message) {
        synchronized (this.sendingMessages) {
            this.sendingMessages.add(message);
        }
    }
}
