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

    public boolean save(String projectName, Object object) {
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
                outobject.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public Object load(String fileName){
        FileInputStream fos = null;
        BufferedInputStream out = null;
        ObjectInputStream outobject = null;

        try{
            fos = new FileInputStream(fileName);
            out = new BufferedInputStream(fos);
            outobject = new ObjectInputStream(out);

            Object object = outobject.readObject();

            return object;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                outobject.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
