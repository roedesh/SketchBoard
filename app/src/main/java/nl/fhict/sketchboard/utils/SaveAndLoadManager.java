package nl.fhict.sketchboard.utils;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by arjan on 8-1-2016.
 */
public class SaveAndLoadManager {

    private static String pathPrefix;

    public static void init(){
        File dir=new File(Environment.getExternalStorageDirectory(),"SketchBoard");

        if(!dir.exists()){
            dir.mkdirs();
        }

        pathPrefix = dir.getAbsolutePath() + File.separatorChar;
    }

    public static boolean save(String fileName, Object object) {
        fileName = pathPrefix + fileName;
        FileOutputStream fos= null;
        BufferedOutputStream out= null;
        ObjectOutputStream outobject = null;

        try{
            fos = new FileOutputStream(fileName);
            out = new BufferedOutputStream(fos);
            outobject = new ObjectOutputStream(out);

            outobject.writeObject(object);
            return true;
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (outobject != null){
                    outobject.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static Object load(String fileName){
        fileName = pathPrefix + fileName;
        FileInputStream fis = null;
        BufferedInputStream in = null;
        ObjectInputStream inobject = null;

        try{
            fis = new FileInputStream(fileName);
            in = new BufferedInputStream(fis);
            inobject = new ObjectInputStream(in);

            return inobject.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inobject != null){
                    inobject.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
