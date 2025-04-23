import java.io.*;

public class Message implements Serializable{
    public String type;
    public String content;
    public String from;

    //message function
    public Message(String type, String content, String from) {
        this.type = type;
        this.content = content;
        this.from = from;
    }
}
