package nl.fhict.sketchboard.layers;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Stan on 8-1-2016.
 */
public class LayerWrapper implements Serializable {
    private List<Layerable> layers;

    public LayerWrapper(List<Layerable> layers){
        this.layers = layers;
    }

    public List<Layerable> getLayers(){
        return this.layers;
    }
}
