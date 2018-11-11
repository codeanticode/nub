package ik.interactive;

import frames.core.Frame;
import frames.core.Graph;
import frames.core.constraint.BallAndSocket;
import frames.core.constraint.Hinge;
import frames.primitives.Vector;
import frames.processing.Scene;
import frames.processing.Shape;
import ik.common.Joint;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebchaparr on 27/10/18.
 */
public class SkeletonBuilder extends PApplet{
    Scene scene, focus;
    OptionPanel panel;
    PGraphics canvas1;
    float radius = 5;
    int w = 1000, h = 700;

    /*Create different skeletons to interact with*/
    //Choose FX2D, JAVA2D, P2D or P3D
    String renderer = P3D;

    /*Constraint Parameters*/
    float minAngle = radians(60);
    float maxAngle = radians(60);

    public static void main(String args[]) {
        PApplet.main(new String[]{"ik.interactive.SkeletonBuilder"});
    }

    public void settings() {
        size(w, h, renderer);
    }

    public void setup(){
        canvas1 = createGraphics((int)(0.7f*w), h, renderer);
        scene = new Scene(this, canvas1);
        scene.fitBallInterpolation();
        if(scene.is3D())scene.setType(Graph.Type.ORTHOGRAPHIC);
        Joint f  = createInteractiveJoint();
        f.setRoot(true);
        panel = new OptionPanel(this, 0.7f * width, 0, (int)(0.3f * width), h );
    }

    void handleMouse() {
        focus = mouseX > 0.7f * w ? panel._scene : scene ;
    }

    public void draw() {
        scene.beginDraw();
        canvas1.background(0);
        scene.drawAxes();
        scene.traverse();
        handleMouse();
        scene.drawAxes();
        scene.traverse();
        scene.endDraw();
        scene.display();

        panel._scene.beginDraw();
        panel._scene.frontBuffer().background(0);
        if(panel._frame != null)
            panel._scene.traverse();
        panel._scene.endDraw();
        panel._scene.display();
    }


    //mouse events
    @Override
    public void mouseMoved() {
        focus.track();
    }

    public void mouseDragged(MouseEvent event) {
        if(focus == scene) {
            if (mouseButton == RIGHT && event.isControlDown()) {
                Vector mouse = new Vector(scene.mouse().x(), scene.mouse().y());
                scene.defaultFrame().interact("OnAdding", scene.location(mouse));
                return;
            } else if (mouseButton == LEFT) {
                scene.spin();
            } else if (mouseButton == RIGHT) {
                scene.translate();
                multipleTranslate();
            } else {
                scene.zoom(scene.mouseDX());
            }
            scene.defaultFrame().interact("Reset");
        } else {
            panel._scene.defaultFrame().interact();
        }
        if(focus == scene)panel.updateFrameOptions();
    }

    public void mouseReleased(){
        Vector mouse = new Vector(scene.mouse().x(), scene.mouse().y());
        mouse = scene.location(mouse);
        mouse = Vector.projectVectorOnPlane(mouse, scene.viewDirection());

        mouse.add(scene.defaultFrame().position());

        scene.defaultFrame().interact("Add", mouse);
    }

    public void mouseWheel(MouseEvent event) {
        scene.scale(event.getCount() * 20);
    }

    public void mouseClicked(MouseEvent event) {
        if (event.getButton() == LEFT) {
            if (event.getCount() == 1) {
                panel.setFrame(scene.trackedFrame());
                if(event.isControlDown()) scene.defaultFrame().interact("KeepSelected");
            }
            else if (event.getCount() == 2) {
                if (event.isControlDown())
                    scene.defaultFrame().interact("Remove");
                else
                    scene.focus();
            }
            else {
                scene.align();
            }
        }
    }

    public void keyPressed(){
        if(key == '+'){
            createInteractiveJoint().setRoot(true);
        }
        if(key == 'A' || key == 'a'){
            addTreeSolver();
        }
        if(key == 'C' || key == 'c'){
            addConstraint(scene.trackedFrame());
        }
        if(key == 'S' || key == 's'){
            minAngle += radians(5);
            if(minAngle >= radians(170) ) minAngle = radians(170);
            System.out.println("minAngle : " + degrees(minAngle));
        }
        if(key == 'D' || key == 'd'){
            minAngle -= radians(5);
            if(minAngle <= radians(0) ) minAngle = radians(0);
            System.out.println("minAngle : " + degrees(minAngle));
        }
        if(key == 'F' || key == 'f'){
            maxAngle += radians(5);
            if(maxAngle >= radians(170) ) maxAngle = radians(170);
            System.out.println("maxAngle : " + degrees(maxAngle));
        }
        if(key == 'G' || key == 'g'){
            maxAngle -= radians(5);
            if(maxAngle <= radians(0) ) maxAngle = radians(0);
            System.out.println("maxAngle : " + degrees(maxAngle));
        }

    }

    //------------------------------------

    //Interactive actions - same method found in Graph Class
    public Vector translateDesired(Frame frame){
        float dx = mouseX - scene.screenLocation(frame.position()).x();
        float dy = mouseY - scene.screenLocation(frame.position()).y();

        dy = scene.isRightHanded() ? -dy : dy;
        if(scene.type() == Graph.Type.PERSPECTIVE){
            float k = (float) Math.tan(scene.fieldOfView() / 2.0f) * Math.abs(
                    scene.eye().location(scene.isEye(frame) ? scene.anchor() : frame.position())._vector[2] * scene.eye().magnitude());
            dx *= 2.0 * k / scene.height();
            dy *= 2.0 * k / scene.height();
        }
        else {
            float[] wh = scene.boundaryWidthHeight();
            dx *= 2.0 * wh[0] / scene.width();
            dy *= 2.0 * wh[1] / scene.height();
        }
        Vector eyeVector = new Vector(dx / scene.eye().magnitude(), dy / scene.eye().magnitude(), 0);
        return frame.reference() == null ? scene.eye().worldDisplacement(eyeVector) : frame.reference().displacement(eyeVector, scene.eye());
    }

    public Joint createInteractiveJoint(){
        return new Joint(scene, radius){
            Vector desiredTranslation;
            @Override
            public void interact(Object... gesture){
                String command = (String) gesture[0];
                if(command.matches("Add")){
                    if(desiredTranslation != null) {
                        addChild();
                    }
                    desiredTranslation = null;
                } else if(command.matches("OnAdding")){
                    desiredTranslation = translateDesired(this);
                } else if(command.matches("Reset")){
                    desiredTranslation = null;
                } else if(command.matches("Remove")){
                    removeChild();
                }
            }
            @Override
            public void visit(){
                super.visit();
                //Draw desired position
                Scene scene = (Scene) this._graph;
                PGraphics pg = scene.frontBuffer();
                if(desiredTranslation != null){
                    pg.pushStyle();
                    pg.stroke(pg.color(0,255,0));
                    pg.strokeWeight(_radius/2);
                    pg.line(0,0,0, desiredTranslation.x(), desiredTranslation.y(), desiredTranslation.z());
                    pg.popStyle();
                }
            }
        };
    }

    public Shape createTarget(Vector position){
        PShape redBall =
                scene.is3D() ? createShape(SPHERE, ((Joint) scene.trackedFrame()).radius() * 2f) :
                        createShape(ELLIPSE, 0,0, ((Joint) scene.trackedFrame()).radius() * 4f, ((Joint) scene.trackedFrame()).radius() * 4f);
        redBall.setStroke(false);
        redBall.setFill(color(255, 0, 0));
        Shape target = new Shape(scene, redBall) {
            @Override
            public void interact(Object... gesture) {
                String command = (String) gesture[0];
                if (command.matches("KeepSelected")) {
                    if(!multipleFrames.contains(this)){
                        redBall.setFill(color(0,255,0));
                        multipleFrames.add(this);
                    }
                    else{
                        redBall.setFill(color(255,0,0));
                        multipleFrames.remove(this);
                    }
                }
            }
        };
        target.setReference(scene.trackedFrame().reference());
        target.setPosition(position);
        return target;
    }

    public void findEndEffectors(Frame frame, List<Frame> endEffectors){
        if(frame.children().isEmpty()){
            endEffectors.add(frame);
            return;
        }
        for(Frame child : frame.children()){
            findEndEffectors(child, endEffectors);
        }
    }

    public void addConstraint(Frame frame){
        //If has a child
        if(frame == null) return;
        if(frame.children().size() != 1) return;
        if(scene.is3D()) {
            BallAndSocket constraint = new BallAndSocket(minAngle, minAngle, maxAngle, maxAngle);
            Vector twist = frame.children().get(0).translation().get();
            constraint.setRestRotation(frame.rotation().get(), Vector.orthogonalVector(twist), twist);
            frame.setConstraint(constraint);
        } else{
            Hinge constraint = new Hinge(true, minAngle, maxAngle);
            constraint.setRestRotation(frame.rotation().get());
            frame.setConstraint(constraint);
        }

    }

    public void addTreeSolver(){
        if(scene.trackedFrame() == null) return;
        scene.registerTreeSolver(scene.trackedFrame());
        //add target
        //get leaf nodes
        ArrayList<Frame> endEffectors = new ArrayList<Frame>();
        findEndEffectors(scene.trackedFrame(), endEffectors);
        for(Frame endEffector : endEffectors) {
            scene.addIKTarget(endEffector, createTarget(endEffector.position()));
        }
    }

    public void addChild(){
        Joint j = createInteractiveJoint();
        j.setReference(scene.trackedFrame());
        j.setTranslation(translateDesired(j));
    }

    public void removeChild(){
        scene.pruneBranch(scene.trackedFrame());
    }


    //----Experimental : Multiple selection
    ArrayList<Frame> multipleFrames = new ArrayList<>();
    public void multipleTranslate(){
        for(Frame frame : multipleFrames){
            if(scene.defaultFrame() != frame)
            scene.translate(frame);
        }
    }
}

