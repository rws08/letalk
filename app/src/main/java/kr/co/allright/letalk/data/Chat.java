package kr.co.allright.letalk.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class Chat {
    public String keyid;
    public String lastMessage;
    public boolean visible;
    public long createtime = 0;
    public int userCount = 1;
    public Map<String, Boolean> userIds = new HashMap<>();
    public List<Message> messages = new ArrayList<>();

    public Chat() {
    }

    public Chat(String keyid) {
        this.keyid = keyid;
    }
}
