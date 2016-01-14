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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by arjan on 8-1-2016.
 */
public class SaveAndLoadManager {

    private static String pathPrefix;

    public static void init(){
        File dir = new File(Environment.getExternalStorageDirectory(),"SketchBoard");

        if(!dir.exists()){
            dir.mkdirs();
        }

        pathPrefix = dir.getAbsolutePath() + File.separatorChar;
    }

    public static boolean save(String fileName, Object object) {
        fileName = pathPrefix + fileName;
        FileOutputStream fos = null;
        BufferedOutputStream out = null;
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

    public static List<Object> loadAll(int amount){
        List<Object> tempObjects = new ArrayList<>();
        File tempDir = new File(pathPrefix);
        if (tempDir.exists()){
            File[] tempFiles = tempDir.listFiles();

            Arrays.sort(tempFiles, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });

            if (amount > tempFiles.length){
                amount = tempFiles.length;
            }

            for (int i = 0; i < amount; i++){
                Object o = load(tempFiles[i].getName());
                if (o != null){
                    tempObjects.add(o);
                }
            }
        }
        return tempObjects;
    }
}
