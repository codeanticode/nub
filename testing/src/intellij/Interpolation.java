package intellij;

import nub.core.Interpolator;
import nub.core.Node;
import nub.processing.Scene;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 * This example introduces the three different interpolations offered
 * by the Graph.
 */
public class Interpolation extends PApplet {
  Scene scene;
  Interpolator interpolator, eyeInterpolator1, eyeInterpolator2;
  Node shape;
  boolean showEyePath = true;
  float speed = 1;

  //Choose P3D for a 3D scene, or P2D or JAVA2D for a 2D scene
  String renderer = P3D;

  public void settings() {
    size(1000, 800, renderer);
  }

  public void setup() {
    rectMode(CENTER);
    scene = new Scene(this);
    scene.setRadius(150);

    // interpolation 1. Default eye interpolations
    scene.fit(1);

    // interpolation 2. Custom eye interpolations
    eyeInterpolator1 = new Interpolator(scene.eye());
    eyeInterpolator2 = new Interpolator(scene.eye());

    // interpolation 3. Custom (arbitrary) node interpolations

    shape = new Node(scene) {
      // Note that within render() geometry is defined at the
      // node local coordinate system.
      @Override
      public void graphics(PGraphics pg) {
        pg.pushStyle();
        pg.fill(0, 255, 255, 125);
        pg.stroke(0, 0, 255);
        pg.strokeWeight(2);
        if (pg.is2D())
          pg.rect(0, 0, 100, 100);
        else
          pg.box(30);
        pg.popStyle();
      }
    };
    interpolator = new Interpolator(shape);
    interpolator.enableRecurrence();
    // Create an initial path
    for (int i = 0; i < random(4, 10); i++)
      interpolator.addKeyFrame(scene.randomNode(), i % 2 == 1 ? 1 : 4);
    interpolator.run();
  }

  public void draw() {
    background(0);
    scene.render();

    pushStyle();
    stroke(255);
    // same as:scene.drawPath(interpolator, 5);
    scene.drawPath(interpolator);
    popStyle();
    for (Node node : interpolator.keyFrames().values()) {
      pushMatrix();
      scene.applyTransformation(node);
      scene.drawAxes(scene.tracks(node) ? 40 : 20);
      popMatrix();
    }
    if (showEyePath) {
      pushStyle();
      fill(255, 0, 0);
      stroke(0, 255, 0);
      // same as:
      // scene.drawPath(eyeInterpolator1, 3);
      // scene.drawPath(eyeInterpolator2, 3);
      scene.drawPath(eyeInterpolator1);
      scene.drawPath(eyeInterpolator2);
      popStyle();
    }
  }

  public void mouseMoved() {
    scene.track();
  }

  public void mouseDragged() {
    if (mouseButton == LEFT)
      scene.spin();
    else if (mouseButton == RIGHT)
      scene.translate();
    else
      scene.scale(mouseX - pmouseX);
  }

  public void mouseWheel(MouseEvent event) {
    if (scene.is3D())
      scene.moveForward(event.getCount() * 20);
    else
      scene.scale(scene.eye(), event.getCount() * 20);
  }

  public void keyPressed() {
    if (key == ' ')
      showEyePath = !showEyePath;

    if (key == '1')
      eyeInterpolator1.addKeyFrame();
    if (key == 'a')
      eyeInterpolator1.toggle();
    if (key == 'b')
      eyeInterpolator1.clear();

    if (key == '2')
      eyeInterpolator2.addKeyFrame();
    if (key == 'c')
      eyeInterpolator2.toggle();
    if (key == 'd')
      eyeInterpolator2.clear();

    if (key == '-' || key == '+') {
      if (key == '-')
        speed -= 0.25f;
      else
        speed += 0.25f;
      interpolator.run(speed);
    }

    if (key == 's')
      scene.fit(1);
    if (key == 'f')
      scene.fit();
  }

  public static void main(String[] args) {
    PApplet.main(new String[]{"intellij.Interpolation"});
  }
}
