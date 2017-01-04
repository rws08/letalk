package kr.co.allright.letalk.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class User {
    public String keyid;
    public String loginId;
    public String email;
    public String name = "";
    public int age = 0;
    public String profile = "";
    public long lon = 0;
    public long lan = 0;
    public String myroomId = "";
    public String myroomTitle = "";
    public long pointMile = 0;
    public long pointBuy = 0;
    public Map<String, Boolean> roomIds = new HashMap<>();

    public User() {
    }

    public User(String email) {
        this.email = email;
    }
}
