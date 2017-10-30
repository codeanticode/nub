/**************************************************************************************
 * ProScene (version 3.0.0)
 * Copyright (c) 2014-2017 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive scenes
 * in Processing, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.proscene;

import processing.core.PGraphics;
import processing.core.PMatrix2D;
import remixlab.geom.MatrixHandler;
import remixlab.primitives.Matrix;
import remixlab.primitives.Quaternion;
import remixlab.primitives.Vec;

/**
 * Internal {@link MatrixHandler} based on PGraphicsJava2D graphics
 * transformations.
 */
class Java2DMatrixHandler extends MatrixHandler {
  protected PGraphics pgr;

  public Java2DMatrixHandler(Scene scn, PGraphics renderer) {
    super(scn);
    pgr = renderer;
  }

  public PGraphics pg() {
    return pgr;
  }

  // Comment the above line and uncomment this one to develop the driver:
  // public PGraphicsJava2D pg() { return (PGraphicsJava2D) pg; }

  @Override
  public void bind() {
    cacheProjection(scene().computeProjection());
    cacheView(scene().computeView());
    cacheProjectionView(Matrix.multiply(cacheProjection(), cacheView()));
    Vec pos = gScene.eye().position();
    //TODO needs test
    Quaternion o = gScene.eye().orientation();
    translate(gScene.width() / 2, gScene.height() / 2);
    if (gScene.isRightHanded())
      scale(1, -1);
    scale(1 / gScene.eye().magnitude(), 1 / gScene.eye().magnitude());
    rotate(-o.angle());
    translate(-pos.x(), -pos.y());
  }

  @Override
  public void applyModelView(Matrix source) {
    pg().applyMatrix(Scene.toPMatrix2D(source));
  }

  @Override
  public void beginScreenDrawing() {
    Vec pos = gScene.eye().position();
    //TODO needs test
    Quaternion o = gScene.eye().orientation();

    pushModelView();
    translate(pos.x(), pos.y());
    rotate(o.angle());
    scale(gScene.eye().magnitude(), gScene.eye().magnitude());
    if (gScene.isRightHanded())
      scale(1, -1);
    translate(-gScene.width() / 2, -gScene.height() / 2);
  }

  @Override
  public void endScreenDrawing() {
    popModelView();
  }

  @Override
  public void pushModelView() {
    pg().pushMatrix();
  }

  @Override
  public void popModelView() {
    pg().popMatrix();
  }

  @Override
  public Matrix modelView() {
    return Scene.toMat(new PMatrix2D(pg().getMatrix()));
  }

  @Override
  public void bindModelView(Matrix source) {
    pg().setMatrix(Scene.toPMatrix2D(source));
  }

  @Override
  public void translate(float tx, float ty) {
    pg().translate(tx, ty);
  }

  @Override
  public void translate(float tx, float ty, float tz) {
    pg().translate(tx, ty, tz);
  }

  @Override
  public void rotate(float angle) {
    pg().rotate(angle);
  }

  @Override
  public void rotateX(float angle) {
    pg().rotateX(angle);
  }

  @Override
  public void rotateY(float angle) {
    pg().rotateY(angle);
  }

  @Override
  public void rotateZ(float angle) {
    pg().rotateZ(angle);
  }

  @Override
  public void rotate(float angle, float vx, float vy, float vz) {
    pg().rotate(angle, vx, vy, vz);
  }

  @Override
  public void scale(float s) {
    pg().scale(s);
  }

  @Override
  public void scale(float sx, float sy) {
    pg().scale(sx, sy);
  }

  @Override
  public void scale(float x, float y, float z) {
    pg().scale(x, y, z);
  }
}