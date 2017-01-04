package kr.co.allright.letalk.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MacPro on 2017. 1. 4..
 */

public class Room {
    public String keyid;
    public String title;
    public Map<String, Boolean> userIds = new HashMap<>();

    public Room() {
    }
}
