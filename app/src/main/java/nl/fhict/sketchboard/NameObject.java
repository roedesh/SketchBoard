package nl.fhict.sketchboard;

/**
 * Created by Stan on 14-1-2016.
 */
public class NameObject {

    private String name;
    private Object object;

    public NameObject(String name, Object object){
        this.name = name;
        this.object = object;
    }

    public String getName(){
        return this.name;
    }

    public Object getObject(){
        return this.object;
    }
}
