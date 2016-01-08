package nl.fhict.sketchboard.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by arjan on 8-1-2016.
 */
public class SaveAndLoadManager {

    public static boolean save(String projectName, Object object) {
        FileOutputStream fos= null;
        BufferedOutputStream out= null;
        ObjectOutputStream outobject = null;

        try{
            fos = new FileOutputStream(projectName);
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
