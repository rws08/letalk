package kr.co.allright.letalk.data;

/**
 * Created by MacPro on 2017. 1. 5..
 */

public class Message {
    static public String CONTENTTYPE_TEXT = "text";
    static public String CONTENTTYPE_IMG = "img";

    public String keyid;
    public String userid;
    public String roomid;
    public String contenttype;
    public String contents;
    public int unreadcount;
    public long createtime = 0;

    public Message() {
    }
}
