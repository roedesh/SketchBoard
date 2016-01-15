package nl.fhict.sketchboard.layers;

public abstract class BaseLayer implements Layerable {
    private float x, y;
    private int rotation;

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getRotationAngle() {
        return rotation;
    }

    @Override
    public void setRotationAngle(int rotation) {
        this.rotation = rotation;
    }
}