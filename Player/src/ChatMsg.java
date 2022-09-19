import java.io.Serializable;

public class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public String code;
    public String UserName;
    public String data;

    public ChatMsg(String UserName, String code, String msg) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
    }
}