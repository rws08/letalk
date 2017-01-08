package kr.co.allright.letalk.etc;

import kr.co.allright.letalk.manager.UserManager;

/**
 * Created by rws on 2017. 1. 9..
 */

public class Utils {
    private static long TIME_SEC = 1000;
    private static long TIME_MIN = 60;
    private static long TIME_HOUR = 60 * 60;
    private static long TIME_DAY = 60 * 60 * 24;
    private static long TIME_MONTH = 60 * 60 * 24 * 30;
    private static long TIME_YEAR = 60 * 60 * 24 * 365;

    public static String getDurationTime(long _befortime, long _aftertime){
        String strTime;
        long time = _aftertime - _befortime;
        time /= TIME_SEC;

        if (time < 0){
            strTime = "-";
        }else if (time / TIME_MIN <= 0){
            // 초처리
            strTime = time + "초전";
        }else if(time / TIME_HOUR <= 0){
            // 분처리
            strTime = time / TIME_MIN + "분전";
        }else if(time / TIME_DAY <= 0){
            // 시처리
            strTime = time / TIME_HOUR + "시간전";
        }else if(time / TIME_MONTH <= 0){
            // 일처리
            strTime = time / TIME_DAY + "일전";
        }else if(time / TIME_YEAR <= 0){
            // 월처리
            strTime = time / TIME_MONTH + "개월전";
        }else{
            // 연처리
            strTime = time / TIME_YEAR + "년전";
        }
        return strTime;
    }
}
