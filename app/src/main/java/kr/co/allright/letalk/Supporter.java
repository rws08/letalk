package kr.co.allright.letalk;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Supporter {
    static String SENDER_ID = "239651645520";
    private static int PLAY_SERVICES_RESULT_CODE;
    
	private static final String TAG = "Supporter";
    private static Supporter s_instance;
    public Context context;

    private static SoundPool sound_pool;
    private static int sound_beep = -1;
    private static int sound_beep_stream = -1;
    private static boolean startSound = false;
    private static int sound_beep_paycial;

    public Supporter(Context context) {
        this.context = context;
        s_instance = this;
    }

    public static synchronized Supporter getInstance(Context context) {
        if (s_instance == null) {
            s_instance = new Supporter(context);
        }
        return s_instance;
    }

    public static Supporter instance() {
        return s_instance;
    }

	public void playSystemSound(){
        if (sound_beep_stream != -1) return;

        stopSystemSound();
        sound_pool = buildSoundPool();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sound_beep_stream = sound_pool.play(sound_beep, 1f, 1f, 0, -1, 1f);
            }
        }, 1000);
    }

    public void stopSystemSound(){
        if (sound_pool == null) return;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sound_pool.stop(sound_beep_stream);
                sound_beep_stream = -1;
            }
        }, 1000);
    }

    @SuppressWarnings("deprecation")
    private SoundPool buildSoundPool() {
        if (sound_pool != null) return sound_pool;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "Initialize Audio Attributes.");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            Log.d(TAG, "Set AudioAttributes for SoundPool.");
            sound_pool = new SoundPool.Builder()
                    .setMaxStreams(7)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            buildBeforeAPI21();
        }

//        sound_beep = sound_pool.load(context, R.raw.noti_effect, 1);

        return sound_pool;
    }

    private void buildBeforeAPI21() {
        Log.i(TAG, "buildBeforeAPI21");
        sound_pool = new SoundPool(7, AudioManager.STREAM_MUSIC, 0);
    }

    private static String getFilenameToUrl(String url){
        String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
        String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
        return fileNameWithoutExtn;
    }

    public static Bitmap loadBitmaptoJpeg(String url){
        Bitmap bitmap = null;
        if (url == null || url.length() == 0){
            return bitmap;
        }

        String fileName = getFilenameToUrl(url);

        String ex_storage = Supporter.instance().context.getFilesDir().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String folder_name = "/"+"menuimg"+"/";
        String file_name = fileName+".jpg";
        String string_path = ex_storage+folder_name;

        bitmap = BitmapFactory.decodeFile(string_path + file_name);

        return bitmap;
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap, String url){
        String fileName = getFilenameToUrl(url);

        String ex_storage = Supporter.instance().context.getFilesDir().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String folder_name = "/"+"menuimg"+"/";
        String file_name = fileName+".jpg";
        String string_path = ex_storage+folder_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

    public static final int getColor(Context context, int id){
        return getColor(context, id, false);
    }

    public static final int getColor(Context context, int id, boolean isAttr){
        if (isAttr){
            int[] attribute = new int[] { id };
            TypedArray array = context.getTheme().obtainStyledAttributes(attribute);
            int color = array.getColor(0, Color.TRANSPARENT);
            return color;
        }else{
            final int version = Build.VERSION.SDK_INT;
            if (version >= 23) {
                return ContextCompat.getColor(context, id);
            } else {
                return context.getResources().getColor(id);
            }
        }
    }
}
