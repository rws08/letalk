package kr.co.allright.letalk.data;

/**
 * Created by MacPro on 2017. 1. 5..
 */

public class Message {
    static public String CONTENTTYPE_TEXT = "text";
    static public String CONTENTTYPE_IMG = "img";

    public String keyid;
    public String userid;
    public String chatid;
    public String contenttype;
    public String contents;
    public int unreadcount;
    public long createtime = 0;

    public Message() {
    }

    public Message(String keyid) {
        this.keyid = keyid;
    }

    public void updateData(Message _message){
        userid = _message.userid;
        chatid = _message.userid;
        contenttype = _message.contenttype;
        contents = _message.contents;
        unreadcount = _message.unreadcount;
        createtime = _message.createtime;
    }
}
