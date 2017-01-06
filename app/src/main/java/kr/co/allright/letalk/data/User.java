package kr.co.allright.letalk.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class User {
    static public String SEX_MAN = "man";
    static public String SEX_WOMAN = "woman";

    public String keyid;
    public String loginId;
    public String email;
    public String name = "";
    public int age = 0;
    public String sex = "";
    public String profile = "";
    public long logintime = 0;
    public double lon = 0;
    public double lat = 0;
    public String myroomId = "";
    public String myroomTitle = "";
    public double pointMile = 0;
    public double pointBuy = 0;
    public Map<String, Boolean> roomIds = new HashMap<>();

    public User() {
    }

    public User(String email) {
        this.email = email;
    }
}
