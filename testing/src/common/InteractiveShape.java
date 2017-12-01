package common;


import processing.core.PApplet;
import remixlab.input.event.KeyEvent;
import remixlab.input.event.MotionEvent;
import remixlab.proscene.Scene;
import remixlab.proscene.Shape;

public class InteractiveShape extends Shape {
    public InteractiveShape(Scene scene) {
        super(scene);
        //this.setPrecision(Precision.EXACT);
    }

    public InteractiveShape(InteractiveShape shape) {
        super(shape);
        //this.setPrecision(Precision.EXACT);
    }

    // this one gotta be overridden because we want a copied frame (e.g., line 141 above, i.e.,
    // scene.eye().get()) to have the same behavior as its original.
    protected InteractiveShape(Scene otherScene, InteractiveShape otherShape) {
        super(otherScene, otherShape);
    }

    @Override
    public InteractiveShape get() {
        return new InteractiveShape(this.scene(), this);
    }

    // behavior is here :P
    @Override
    public void interact(MotionEvent event) {
        switch (event.shortcut().id()) {
            case PApplet.LEFT:
                rotate(event);
                break;
            case PApplet.RIGHT:
                translate(event);
                break;
            case processing.event.MouseEvent.WHEEL:
                if(isEye() && graph().is3D())
                    translateZ(event);
                else
                    scale(event);
                break;
        }
    }

    @Override
    public void interact(KeyEvent event) {
        if (event.id() == PApplet.UP)
            translateYPos();
        if (event.id() == PApplet.DOWN)
            translateYNeg();
        if (event.id() == PApplet.LEFT)
            translateXNeg();
        if (event.id() == PApplet.RIGHT)
            translateXPos();
    }
}
