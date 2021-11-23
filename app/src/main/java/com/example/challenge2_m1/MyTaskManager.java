package com.example.challenge2_m1;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.challenge2_m1.Model.Note;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyTaskManager {
    private static final String FILENAME = ".txt";
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Context myContext;

    public interface Callback {
        void onSaveToTxt();
        void onLoadFromTxt();
    }

    public MyTaskManager(Context context){ this.myContext = context; }

    public void executeSave(ArrayList<Note> notes, Callback callback){
        executor.execute(() ->{
            FileOutputStream fos;
            Note currentNote;

            try{
                for(int i= 0; i < notes.size(); i++){
                    currentNote = notes.get(i);
                    fos = myContext.openFileOutput(currentNote.getName() + FILENAME, MODE_PRIVATE);
                    fos.write(currentNote.getContent().getBytes());
                    System.out.println(myContext.getFilesDir());
                    fos.close();
                }
            }catch(Exception e){ e.printStackTrace(); }

            handler.post(callback::onSaveToTxt);
        });
    }

    public void executeLoad(ArrayList<Note> notes, Callback callback){
        String ret;

        try {
            for(int i = 0; i < notes.size(); i++) {
                InputStream inputStream = myContext.openFileInput("" + notes.get(i).getName() + ".txt");

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                        stringBuilder.append("\n");
                    }

                    stringBuilder.deleteCharAt(stringBuilder.length() -1);

                    inputStream.close();
                    ret = stringBuilder.toString();
                    notes.get(i).setContent(ret);

                    System.out.println(inputStream.toString());
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

}
