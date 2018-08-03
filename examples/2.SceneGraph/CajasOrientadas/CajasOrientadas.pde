/**
 * Cajas Orientadas.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates some basic Frame properties, particularly how to orient them.
 * Select and move the sphere (holding the right mouse button pressed) to see how the
 * boxes will immediately be oriented towards it. You can also pick and move the boxes
 * and still they will be oriented towards the sphere.
 */

import frames.primitives.*;
import frames.core.*;
import frames.processing.*;

Scene scene;
Box[] cajas;
Sphere esfera;

void setup() {
  size(800, 800, P3D);
  scene = new Scene(this);
  scene.setRadius(200);
  scene.fitBall();
  scene.setType(Graph.Type.ORTHOGRAPHIC);
  esfera = new Sphere();
  esfera.setPosition(new Vector(0.0f, 1.4f, 0.0f));
  esfera.setColor(color(0, 0, 255));

  cajas = new Box[30];
  for (int i = 0; i < cajas.length; i++)
    cajas[i] = new Box();

  scene.fitBallInterpolation();
  scene.setTrackedFrame("keyboard", esfera.iFrame);
}

void draw() {
  background(0);

  esfera.draw(false);
  for (int i = 0; i < cajas.length; i++) {
    cajas[i].setOrientation(esfera.getPosition());
    cajas[i].draw(true);
  }

  String text = "Cajas Orientadas";
  float w = scene.frontBuffer().textWidth(text);
  float h = scene.frontBuffer().textAscent() + scene.frontBuffer().textDescent();
  scene.beginScreenDrawing();
  //textFont(font);
  text(text, 20, 20, w + 1, h);
  scene.endScreenDrawing();
}

void mouseMoved() {
  scene.track();
}

void mouseDragged() {
  if (mouseButton == LEFT)
    scene.spin();
  //scene.lookAround(upVector);
  //scene.mouseCAD();
  else if (mouseButton == RIGHT)
    scene.translate();
  //scene.mousePan();
  else
    //scene.zoom(mouseX - pmouseX);
    scene.scale(mouseX - pmouseX);
}

void mouseWheel(MouseEvent event) {
  //scene.zoom(event.getCount() * 20);
  scene.scale(event.getCount() * 20);
}

void keyPressed() {
  if (key == 'e')
    scene.setType(Graph.Type.ORTHOGRAPHIC);
  if (key == 'E')
    scene.setType(Graph.Type.PERSPECTIVE);
  if (key == 's')
    scene.fitBallInterpolation();
  if (key == 'S')
    scene.fitBall();
  if (key == 'u')
    if (scene.trackedFrame("keyboard") == null)
      scene.setTrackedFrame("keyboard", esfera.iFrame);
    else
      scene.resetTrackedFrame("keyboard");
  if (key == CODED)
    if (keyCode == UP)
      scene.translate("keyboard", 0, -10);
    else if (keyCode == DOWN)
      scene.translate("keyboard", 0, 10);
    else if (keyCode == LEFT)
      scene.translate("keyboard", -10, 0);
    else if (keyCode == RIGHT)
      scene.translate("keyboard", 10, 0);
}
