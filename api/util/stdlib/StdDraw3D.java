
package api.util.stdlib;

/******************************************************************************
 * StdDraw3D.java
 * Hayk Martirosyan
 ******************************************************************************
 * 3D Drawing Library
 *
 * Standard Draw 3D is a Java library with the express goal of making it
 * simple to create three-dimensional models, simulations, and games.
 *
 * Introductory Tutorial:
 * http://introcs.cs.princeton.edu/java/stddraw3d
 *
 * Reference manual:
 * http://introcs.cs.princeton.edu/java/stddraw3d-manual.html
 *
 * NOTE: The code below is only partially documented. Refer to the
 * reference manual for complete documentation.
 *
 *****************************************************************************/

// Native Java libraries.
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Group;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.LinearFog;
import javax.media.j3d.Material;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.Node;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Sound;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleFanArray;
import javax.media.j3d.View;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
// Java3D libraries.
import javax.vecmath.Point3f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import api.util.Mathematics;

import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.scenegraph.io.SceneGraphFileReader;
import com.sun.j3d.utils.scenegraph.io.SceneGraphFileWriter;
import com.sun.j3d.utils.scenegraph.io.UnsupportedUniverseException;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * Standard Draw 3D is a Java library with the express goal of making it
 * simple to create three-dimensional models, simulations, and games.
 * Here is a <a href = "http://introcs.cs.princeton.edu/java/stddraw3d">StdDraw3D tutorial</a>
 * and the
 * <a href = "http://introcs.cs.princeton.edu/java/stddraw3d/stddraw3d-manual.html">StdDraw3D reference manual</a>.
 *
 * @author Hayk Martirosyan
 */

public final class StdDraw3D implements
MouseListener, MouseMotionListener, MouseWheelListener,
KeyListener, ActionListener, ChangeListener, ComponentListener, WindowFocusListener
{

	/* ***************************************************************
	 * Global Variables *
	 * ***************************************************************
	 */

	/* Public constant values. */
	// -------------------------------------------------------------------------

	/**
	 * The camera can be controlled with the static functions already listed in
	 * this reference. However, much more advanced control of the camera can be
	 * obtained by manipulating the Camera object. The Camera is basically
	 * equivalent to a Shape with a couple of extra functions, so it can be moved
	 * and rotated just like a Shape. This functionality works well with
	 * point-of-view simulations and games.
	 */
	public static class Camera extends Transformable
	{

		private Shape	pair;

		private Camera(final TransformGroup tg)
		{
			super(tg);
		}

		public void match(final Shape s)
		{
			super.match(s);
		}

		@Override
		public void moveRelative(final Vector3D move)
		{
			if ((StdDraw3D.view.getProjectionPolicy() == View.PARALLEL_PROJECTION))
			{
				StdDraw3D.setScreenScale(StdDraw3D.view.getScreenScale() * (1 + (move.z / StdDraw3D.zoom)));
				super.move(super.relToAbs(move.times(1, 1, 0)));
			}
			else
			{
				super.move(super.relToAbs(move.times(1, 1, -1)));
			}
		}

		public void pair(final Shape s)
		{
			this.pair = s;
		}

		public void rotateFPS(final double xAngle, final double yAngle, final double zAngle)
		{

			double xA = Math.toRadians(xAngle);
			double yA = Math.toRadians(yAngle);
			double zA = Math.toRadians(zAngle);

			Vector3D shift = super.relToAbs(new Vector3D(-yA, xA, zA));
			Vector3D dir = super.getDirection().plus(shift);
			double angle = dir.angle(StdDraw3D.yAxis);
			if (angle > 90)
			{
				angle = 180 - angle;
			}
			if (angle < 5)
			{
				return;
			}
			super.setDirection(super.getDirection().plus(shift));
		}

		public void rotateFPS(final Vector3D angles)
		{
			this.rotateFPS(angles.x, angles.y, angles.z);
		}

		public void unpair()
		{
			this.pair = null;
		}
	}

	/**
	 * When you create a light in StdDraw3D, it returns a Light object. Light
	 * objects can be manipulated just like Shapes, and are useful if you want
	 * moving lights or lights that change color and brightness.
	 */
	public static class Light extends Transformable
	{

		BranchGroup				bg;
		javax.media.j3d.Light	light;

		private Light(final BranchGroup bg, final TransformGroup tg, final javax.media.j3d.Light light)
		{
			super(tg);
			this.light = light;
			this.bg = bg;
		}

		public void hide()
		{
			this.light.setEnable(false);
		}

		public void match(final Camera c)
		{
			super.match(c);
		}

		public void match(final Shape s)
		{
			super.match(s);
		}

		public void scalePower(final double power)
		{

			if (this.light instanceof PointLight)
			{

				double attenuationScale = 1.0 / ((0.999 * power) + 0.001);

				PointLight pl = (PointLight)this.light;
				Point3f attenuation = new Point3f();
				pl.getAttenuation(attenuation);
				attenuation.y *= attenuationScale;
				attenuation.z *= attenuationScale * attenuationScale;

				pl.setAttenuation(attenuation);
			}
			else
			{
				System.err.println("Can only scale power for point lights!");
			}
		}

		public void setColor(final Color col)
		{
			this.light.setColor(new Color3f(col));
		}

		public void unhide()
		{
			this.light.setEnable(true);
		}
	}

	/**
	 * Everything three-dimensional you draw in your scene from spheres to
	 * points to 3D text is actually a Shape object. When you call a drawing
	 * function, it always returns a Shape object. If you keep a reference to
	 * this object, you can manipulate the already drawn Shape instead of
	 * clearing and redrawing it, which is a much more powerful and more
	 * efficient method of animation.
	 */
	public static class Shape extends Transformable
	{

		private final BranchGroup		bg;
		private final TransformGroup	tg;

		private Shape(final BranchGroup bg, final TransformGroup tg)
		{
			super(tg);
			this.bg = bg;
			this.tg = tg;
			tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		}

		public void hide()
		{
			StdDraw3D.offscreenGroup.removeChild(this.bg);
			StdDraw3D.onscreenGroup.removeChild(this.bg);
		}

		public void match(final Camera c)
		{
			super.match(c);
		}

		public void match(final Shape s)
		{
			super.match(s);
		}

		public void scale(final double scale)
		{
			Transform3D t = super.getTransform();

			t.setScale(t.getScale() * scale);

			super.setTransform(t);
		}

		private void setColor(final Appearance ap, final Color c)
		{
			Material m = ap.getMaterial();
			m.setAmbientColor(new Color3f(c));
			m.setDiffuseColor(new Color3f(c));

			float alpha = ((float)c.getAlpha()) / 255;
			if (alpha < 1.0)
			{
				TransparencyAttributes t = new TransparencyAttributes();
				t.setTransparencyMode(TransparencyAttributes.BLENDED);
				t.setTransparency(1 - alpha);
				ap.setTransparencyAttributes(t);
			}
			else
			{
				ap.setTransparencyAttributes(null);
			}
		}

		public void setColor(final Color c)
		{
			this.setColor(this.tg, c);
		}

		public void setColor(final Color c, final int alpha)
		{
			this.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
		}

		private void setColor(final Group g, final Color c)
		{
			for (int i = 0; i < g.numChildren(); i++)
			{
				Node child = g.getChild(i);
				if (child instanceof Shape3D)
				{
					Shape3D shape = (Shape3D)child;
					Appearance ap = shape.getAppearance();
					this.setColor(ap, c);
				}
				else
					if (child instanceof Primitive)
					{
						Primitive primitive = (Primitive)child;
						Appearance ap = primitive.getAppearance();
						this.setColor(ap, c);
					}
					else
						if (child instanceof Group)
						{
							this.setColor((Group)child, c);
						}
			}
		}

		public void unhide()
		{
			this.hide();
			StdDraw3D.offscreenGroup.addChild(this.bg);
		}
	}

	/**
	 * Private class that represents anything that can be moved and rotated.
	 */
	private static class Transformable
	{

		private final TransformGroup	tg;

		private Transformable(final TransformGroup tg0)
		{
			this.tg = tg0;
		}

		private Vector3D absToRel(final Vector3D r)
		{
			Transform3D t = this.getTransform();

			Matrix3d m = new Matrix3d();
			t.get(m);
			Vector3d zero = new Vector3d(0, 0, 0);
			Transform3D rotation = new Transform3D(m, zero, 1.0);
			Vector3f vec = StdDraw3D.createVector3f(r);
			rotation.invert();
			rotation.transform(vec);

			return new Vector3D(vec);
		}

		public Vector3D getDirection()
		{
			return this.relToAbs(StdDraw3D.zAxis.times(-1)).direction();
		}

		public Vector3D getOrientation()
		{
			Transform3D t = this.getTransform();

			Matrix3d mat = new Matrix3d();
			t.get(mat);

			double xA, yA, zA;

			yA = -Math.asin(mat.m20);
			double C = Math.cos(yA);
			if (Math.abs(C) > 0.005)
			{
				xA = -Math.atan2(-mat.m21 / C, mat.m22 / C);
				zA = -Math.atan2(-mat.m10 / C, mat.m00 / C);
			}
			else
			{
				xA = 0;
				zA = -Math.atan2(mat.m01, mat.m11);
			}

			xA = Math.toDegrees(xA);
			yA = Math.toDegrees(yA);
			zA = Math.toDegrees(zA);

			/* return only positive angles in [0,360] */
			if (xA < 0)
			{
				xA += 360;
			}
			if (yA < 0)
			{
				yA += 360;
			}
			if (zA < 0)
			{
				zA += 360;
			}

			return new Vector3D(xA, yA, zA);
		}

		public Vector3D getPosition()
		{
			Transform3D t = this.getTransform();
			Vector3d r = new Vector3d();
			t.get(r);
			return new Vector3D(r);
		}

		private Transform3D getTransform()
		{
			Transform3D t = new Transform3D();
			this.tg.getTransform(t);
			return t;
		}

		public void lookAt(final Vector3D center)
		{
			this.lookAt(center, StdDraw3D.yAxis);
		}

		public void lookAt(final Vector3D center, final Vector3D up)
		{
			Transform3D t = this.getTransform();

			Transform3D t2 = new Transform3D(t);

			Vector3f translation = new Vector3f();
			t2.get(translation);

			Vector3d scales = new Vector3d();
			t2.getScale(scales);

			Point3d trans = new Point3d(translation);
			Point3d c = new Point3d(center.x, center.y, center.z);
			Vector3d u = StdDraw3D.createVector3d(up);
			t2.lookAt(trans, c, u);

			try
			{
				t2.invert();
				t2.setScale(scales);
				this.setTransform(t2);
			}
			catch (SingularMatrixException sme)
			{
				System.out.println("Singular matrix, bad lookAt()!");
			}

		}

		private void match(final Transformable s)
		{
			this.setOrientation(s.getOrientation());
			this.setPosition(s.getPosition());
		}

		public void move(final double x, final double y, final double z)
		{
			this.move(new Vector3D(x, y, z));
		}

		public void move(final Vector3D move)
		{
			Transform3D t = this.getTransform();

			Vector3f r = new Vector3f();
			t.get(r);
			r.add(StdDraw3D.createVector3f(move));
			t.setTranslation(r);

			this.setTransform(t);
		}

		public void moveRelative(final double right, final double up, final double forward)
		{
			this.moveRelative(new Vector3D(right, up, forward));
		}

		public void moveRelative(final Vector3D move)
		{
			this.move(this.relToAbs(move.times(1, 1, -1)));
		}

		private Vector3D relToAbs(final Vector3D r)
		{
			Transform3D t = this.getTransform();

			Matrix3d m = new Matrix3d();
			t.get(m);
			Vector3d zero = new Vector3d(0, 0, 0);
			Transform3D rotation = new Transform3D(m, zero, 1.0);
			Vector3f vec = StdDraw3D.createVector3f(r);
			rotation.transform(vec);

			return new Vector3D(vec);
		}

		public void rotate(final double xAngle, final double yAngle, final double zAngle)
		{
			this.rotate(new Vector3D(xAngle, yAngle, zAngle));
		}

		public void rotate(final Vector3D angles)
		{
			Transform3D t = this.getTransform();

			Transform3D tX = new Transform3D();
			Transform3D tY = new Transform3D();
			Transform3D tZ = new Transform3D();

			Vector3D xR = this.absToRel(StdDraw3D.xAxis);
			Vector3D yR = this.absToRel(StdDraw3D.yAxis);
			Vector3D zR = this.absToRel(StdDraw3D.zAxis);

			Vector3D radians = angles.times(Math.PI / 180.);
			tX.setRotation(new AxisAngle4d(xR.x, xR.y, xR.z, radians.x));
			tY.setRotation(new AxisAngle4d(yR.x, yR.y, yR.z, radians.y));
			tZ.setRotation(new AxisAngle4d(zR.x, zR.y, zR.z, radians.z));

			t.mul(tX);
			t.mul(tY);
			t.mul(tZ);

			this.setTransform(t);
		}

		public void rotateAxis(final Vector3D axis, final double angle)
		{
			if (angle == 0)
			{
				return;
			}
			Transform3D t = this.getTransform();

			Vector3D aRel = this.absToRel(axis);
			AxisAngle4d aa = new AxisAngle4d(aRel.x, aRel.y, aRel.z, Math.toRadians(angle));
			Transform3D t1 = new Transform3D();
			t1.setRotation(aa);
			t.mul(t1);

			this.setTransform(t);
		}

		public void rotateRelative(final double pitch, final double yaw, final double roll)
		{
			this.rotateRelative(new Vector3D(pitch, yaw, roll));
		}

		public void rotateRelative(final Vector3D angles)
		{
			Transform3D t = this.getTransform();

			Transform3D tX = new Transform3D();
			Transform3D tY = new Transform3D();
			Transform3D tZ = new Transform3D();

			Vector3D radians = angles.times(Math.PI / 180.);
			tX.setRotation(new AxisAngle4d(1, 0, 0, radians.x));
			tY.setRotation(new AxisAngle4d(0, 1, 0, radians.y));
			tZ.setRotation(new AxisAngle4d(0, 0, 1, radians.z));

			t.mul(tX);
			t.mul(tY);
			t.mul(tZ);

			this.setTransform(t);
		}

		public void setDirection(final Vector3D direction)
		{
			this.setDirection(direction, StdDraw3D.yAxis);
		}

		public void setDirection(final Vector3D direction, final Vector3D up)
		{
			Vector3D center = this.getPosition().plus(direction);
			this.lookAt(center, up);
		}

		public void setOrientation(final double xAngle, final double yAngle, final double zAngle)
		{
			this.setOrientation(new Vector3D(xAngle, yAngle, zAngle));
		}

		public void setOrientation(final Vector3D angles)
		{
			if (Math.abs(angles.y) == 90)
			{
				System.err.println("Gimbal lock when the y-angle is vertical!");
			}

			Transform3D t = this.getTransform();

			Vector3D radians = angles.times(Math.PI / 180.);
			Transform3D t1 = new Transform3D();
			t1.setEuler(StdDraw3D.createVector3d(radians));
			Vector3d r = new Vector3d();
			t.get(r);
			t1.setTranslation(r);
			t1.setScale(t.getScale());

			this.setTransform(t1);
		}

		public void setPosition(final double x, final double y, final double z)
		{
			this.setPosition(new Vector3D(x, y, z));
		}

		public void setPosition(final Vector3D pos)
		{
			Transform3D t = this.getTransform();

			t.setTranslation(StdDraw3D.createVector3f(pos));

			this.setTransform(t);
		}

		private void setTransform(final Transform3D t)
		{
			this.tg.setTransform(t);
		}
	}

	/******************************************************************************
	 * An immutable three-dimensional vector class with useful vector operations.
	 *
	 * @author Hayk Martirosyan <hmartiro@princeton.edu>
	 * @since 2011.0310
	 * @version 1.0
	 ****************************************************************************/
	public static class Vector3D
	{

		/** X-coordinate - immutable but directly accessible. */
		public final double	x;

		/** Y-coordinate - immutable but directly accessible. */
		public final double	y;

		/** Z-coordinate - immutable but directly accessible. */
		public final double	z;

		// --------------------------------------------------------------------------

		/**
		 * Initializes to zero vector.
		 */
		public Vector3D()
		{

			this.x = 0;
			this.y = 0;
			this.z = 0;
		}

		// --------------------------------------------------------------------------

		/**
		 * Initializes to the given coordinates.
		 *
		 * @param x
		 *            X-coordinate
		 * @param y
		 *            Y-coordinate
		 * @param z
		 *            Z-coordinate
		 */
		public Vector3D(final double x, final double y, final double z)
		{

			this.x = x;
			this.y = y;
			this.z = z;
		}

		/**
		 * Initializes to the given coordinates.
		 *
		 * @param c
		 *            Array length 3 of coordinates (x, y, z,).
		 */
		// --------------------------------------------------------------------------

		public Vector3D(final double[] c)
		{

			if (c.length != 3)
			{
				throw new RuntimeException("Incorrect number of dimensions!");
			}
			this.x = c[0];
			this.y = c[1];
			this.z = c[2];
		}

		// --------------------------------------------------------------------------

		private Vector3D(final Point3d p)
		{

			this.x = p.x;
			this.y = p.y;
			this.z = p.z;

		}

		// --------------------------------------------------------------------------

		private Vector3D(final Vector3d v)
		{

			this.x = v.x;
			this.y = v.y;
			this.z = v.z;
		}

		// --------------------------------------------------------------------------

		private Vector3D(final Vector3f v)
		{

			this.x = v.x;
			this.y = v.y;
			this.z = v.z;
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the smallest angle between this and that vector, in DEGREES.
		 *
		 * @return Angle between the vectors, in DEGREES
		 */
		public double angle(final Vector3D that)
		{
			return Math.toDegrees(Math.acos(this.dot(that) / (this.mag() * that.mag())));
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the cross product of this vector with that vector.
		 *
		 * @return This cross that
		 */
		public Vector3D cross(final Vector3D that)
		{

			Vector3D a = this;
			Vector3D b = that;

			return new Vector3D((a.y * b.z) - (a.z * b.y), (a.z * b.x) - (a.x * b.z), (a.x * b.y) - (a.y * b.x));
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the unit vector in the direction of this vector.
		 *
		 * @return Unit vector with direction of this vector
		 */
		public Vector3D direction()
		{

			if (this.mag() == 0.0)
			{
				throw new RuntimeException("Zero-vector has no direction");
			}
			return this.times(1.0 / this.mag());
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the Euclidian distance between this and that.
		 *
		 * @param that
		 *            Vector to compute distance between
		 * @return Distance between this and that
		 */
		public double distanceTo(final Vector3D that)
		{
			return this.minus(that).mag();
		}

		// --------------------------------------------------------------------------

		// --------------------------------------------------------------------------
		/*
		 * public Vector3D (double min, double max) {
		 *
		 * this.x = min + Math.random() * (max - min);
		 * this.y = min + Math.random() * (max - min);
		 * this.z = min + Math.random() * (max - min);
		 * }
		 */
		// --------------------------------------------------------------------------
		/**
		 * Returns the dot product of this and that.
		 *
		 * @param that
		 *            Vector to dot with
		 * @return This dot that
		 */
		public double dot(final Vector3D that)
		{

			return ((this.x * that.x) + (this.y * that.y) + (this.z * that.z));
		}

		/**
		 * Draws this vector as a point from the origin to StdDraw3D.
		 */
		public void draw()
		{

			StdDraw3D.sphere(this.x, this.y, this.z, 0.01);
		}

		/**
		 * Returns the magnitude of this vector.
		 *
		 * @return Magnitude of vector
		 */
		public double mag()
		{

			return Math.sqrt(this.dot(this));
		}

		public Vector3D minus(final double x, final double y, final double z)
		{
			double cx = this.x - x;
			double cy = this.y - y;
			double cz = this.z - z;
			return new Vector3D(cx, cy, cz);
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the difference of this and that vector.
		 *
		 * @param that
		 *            Vector to compute difference with
		 * @return This minus that
		 */
		public Vector3D minus(final Vector3D that)
		{

			double cx = this.x - that.x;
			double cy = this.y - that.y;
			double cz = this.z - that.z;
			Vector3D c = new Vector3D(cx, cy, cz);
			return c;
		}

		// --------------------------------------------------------------------------

		public Vector3D plus(final double x, final double y, final double z)
		{
			double cx = this.x + x;
			double cy = this.y + y;
			double cz = this.z + z;
			return new Vector3D(cx, cy, cz);
		}

		// --------------------------------------------------------------------------

		// --------------------------------------------------------------------------

		/**
		 * Returns the sum of this and that vector.
		 *
		 * @param that
		 *            Vector to compute sum with
		 * @return This plus that
		 */
		public Vector3D plus(final Vector3D that)
		{

			double cx = this.x + that.x;
			double cy = this.y + that.y;
			double cz = this.z + that.z;
			Vector3D c = new Vector3D(cx, cy, cz);
			return c;
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the projection of this onto the given line.
		 *
		 * @param line
		 *            Direction to project this vector onto
		 * @return This projected onto line
		 */
		public Vector3D proj(final Vector3D line)
		{

			Vector3D normal = line.direction();

			return normal.times(this.dot(normal));
		}

		// --------------------------------------------------------------------------

		/**
		 * Reflects this vector across the direction given by line.
		 *
		 * @param line
		 *            Direction to reflect this vector over
		 * @return This reflected over line
		 */
		public Vector3D reflect(final Vector3D line)
		{

			return this.proj(line).times(2).minus(this);
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the product of this vector and the scalar k.
		 *
		 * @param k
		 *            Scalar to multiply by
		 * @return (this.x * k, this.y * k, this.z * k)
		 */
		public Vector3D times(final double k)
		{

			return this.times(k, k, k);
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns the result (this.x * a, this.y * b, this.z * c).
		 *
		 * @param a
		 *            Scalar to multiply x by
		 * @param b
		 *            Scalar to multiply y by
		 * @param c
		 *            Scalar to multiply z by
		 * @return (this.x * a, this.y * b, this.z * c)
		 */
		public Vector3D times(final double a, final double b, final double c)
		{

			double vx = this.x * a;
			double vy = this.y * b;
			double vz = this.z * c;
			Vector3D v = new Vector3D(vx, vy, vz);
			return v;
		}

		// --------------------------------------------------------------------------

		/**
		 * Returns a string representation of this vector.
		 *
		 * @return "( this.x, this.y, this.z)"
		 */
		@Override
		public String toString()
		{

			DecimalFormat df = new DecimalFormat("0.000000");
			return ("( " + df.format(this.x) + ", " + df.format(this.y) + ", " + df.format(this.z) + " )");
		}

		// --------------------------------------------------------------------------

		/**
		 * Draws a line representation of this vector, from the given origin.
		 *
		 * @param origin
		 *            Origin point to draw from
		 */
		/*
		 * public void drawLine (Vector3D origin) {
		 *
		 * Vector3D end = this.plus(origin);
		 *
		 * StdDraw3D.line(origin.x, origin.y, origin.z, end.x, end.y, end.z);
		 * }
		 */
	}

	public static final int					AIRPLANE_MODE			= 2;

	private static JCheckBoxMenuItem		antiAliasingButton;

	private static double					aspectRatio;

	private static Background				background;

	// Current background color.
	private static Color					bgColor;

	private static Group					bgGroup;

	// Preset colors.
	public static final Color				BLACK					= Color.BLACK;

	public static final Color				BLUE					= Color.BLUE;

	// Camera object
	private static Camera					camera;

	// Camera mode.
	private static int						cameraMode;

	// Drawing canvas.
	private static Canvas3D					canvas;

	private static Panel					canvasPanel;

	// Keeps track of screen clearing.
	private static boolean					clear3D;

	private static boolean					clearOverlay;

	/* Global variables. */
	// -------------------------------------------------------------------------

	public static final Color				CYAN					= Color.CYAN;

	public static final Color				DARK_GRAY				= Color.DARK_GRAY;

	private static final double				DEFAULT_BACK_CLIP		= 10;

	// Default background color.
	private static final Color				DEFAULT_BGCOLOR			= StdDraw3D.BLACK;

	// Default camera mode
	private static final int				DEFAULT_CAMERA_MODE		= StdDraw3D.ORBIT_MODE;

	// Default pen settings.
	private static final Font				DEFAULT_FONT			= new Font("Arial", Font.PLAIN, 16);

	// Default field of vision for perspective projection.
	private static final double				DEFAULT_FOV				= 0.9;

	// Default clipping distances for rendering.
	private static final double				DEFAULT_FRONT_CLIP		= 0.01;

	private static final double				DEFAULT_MAX				= 1.0;

	// Default boundaries of canvas scale.
	private static final double				DEFAULT_MIN				= 0.0;

	private static final int				DEFAULT_NUM_DIVISIONS	= 100;

	private static final Color				DEFAULT_PEN_COLOR		= StdDraw3D.WHITE;

	private static final double				DEFAULT_PEN_RADIUS		= 0.002;

	// Default square canvas dimension in pixels.
	private static final int				DEFAULT_SIZE			= 600;

	private static JMenu					fileMenu, cameraMenu, graphicsMenu;

	public static final int					FIXED_MODE				= 4;

	private static Font						font;

	private static JSpinner					fovSpinner;

	public static final int					FPS_MODE				= 1;

	// GUI Components
	private static JFrame					frame;

	private static boolean					fullscreen				= false;

	public static final Color				GRAY					= Color.GRAY;

	public static final Color				GREEN					= Color.GREEN;

	private static int						height;

	private static boolean					immersive				= false;

	public static final int					IMMERSIVE_MODE			= 5;

	// Infinite bounding sphere.
	private static final BoundingSphere		INFINITE_BOUNDS			=
		new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1e100);

	private static JCheckBox				infoCheckBox;

	private static boolean					infoDisplay;

	private static BufferedImage			infoImage;

	private static Object					keyLock					= new Object();

	// Keyboard states.
	private static TreeSet<Integer>			keysDown				= new TreeSet<Integer>();

	private static LinkedList<Character>	keysTyped				= new LinkedList<Character>();

	public static final Color				LIGHT_GRAY				= Color.LIGHT_GRAY;

	private static JMenuItem				loadButton, saveButton, save3DButton, quitButton;

	public static final int					LOOK_MODE				= 3;

	public static final Color				MAGENTA					= Color.MAGENTA;

	private static JMenuBar					menuBar;

	// Coordinate bounds.
	private static double					min, max, zoom;

	// Mouse states.
	private static boolean					mouse1;

	private static boolean					mouse2;

	private static boolean					mouse3;

	// For event synchronization.
	private static Object					mouseLock				= new Object();

	private static double					mouseX;

	private static double					mouseY;

	// Number of triangles per shape.
	private static int						numDivisions;

	private static JSpinner					numDivSpinner;

	/* Final variables for default values. */
	// -------------------------------------------------------------------------

	// Buffered Images for 2D drawing
	private static BufferedImage			offscreenImage, onscreenImage;

	private static BranchGroup				onscreenGroup, offscreenGroup;

	public static final Color				ORANGE					= Color.ORANGE;

	private static OrbitBehavior			orbit;

	// Camera modes.
	public static final int					ORBIT_MODE				= 0;

	// Center of orbit
	private static Point3d					orbitCenter;

	private static JRadioButtonMenuItem
	orbitModeButton, fpsModeButton, airplaneModeButton, lookModeButton, fixedModeButton;

	// Pen properties.
	private static Color					penColor;

	private static float					penRadius;

	private static JRadioButtonMenuItem		perspectiveButton, parallelButton;

	public static final Color				PINK					= Color.PINK;

	// Default shape flags.
	private static final int				PRIMFLAGS				=
		Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;

	/* ***************************************************************
	 * Saving/Loading Methods *
	 * ***************************************************************
	 */

	public static final Color				RED						= Color.RED;

	private static BranchGroup				rootGroup, lightGroup, soundGroup, fogGroup, appearanceGroup;

	// Singleton for callbacks - avoids generation of extra .class files.
	private static StdDraw3D				std						= new StdDraw3D();

	private static final double				TEXT3D_DEPTH			= 1.5;

	// Scales the size of Text3D.
	private static final double				TEXT3D_SHRINK_FACTOR	= 0.005;

	// Scene groups.
	private static SimpleUniverse			universe;

	private static View						view;

	/* Housekeeping. */
	// -------------------------------------------------------------------------

	public static final Color				WHITE					= Color.WHITE;

	// Canvas dimensions.
	private static int						width;

	// Axis vectors.
	private static final Vector3D			xAxis					= new Vector3D(1, 0, 0);

	/* ***************************************************************
	 * Initialization Methods *
	 * ***************************************************************
	 */

	private static final Vector3D			yAxis					= new Vector3D(0, 1, 0);

	public static final Color				YELLOW					= Color.YELLOW;

	private static final Vector3D			zAxis					= new Vector3D(0, 0, 1);

	// Static initializer.
	static
	{
		// System.setProperty("j3d.rend", "ogl");
		System.setProperty("j3d.audiodevice", "com.sun.j3d.audioengines.javasound.JavaSoundMixer");
		StdDraw3D.setCanvasSize(StdDraw3D.DEFAULT_SIZE, StdDraw3D.DEFAULT_SIZE);
	}

	public static void addFog(final Color col, final double frontDistance, final double backDistance)
	{
		LinearFog fog = new LinearFog(new Color3f(col), frontDistance, backDistance);
		fog.setInfluencingBounds(StdDraw3D.INFINITE_BOUNDS);

		BranchGroup bg = StdDraw3D.createBranchGroup();
		bg.addChild(fog);
		StdDraw3D.fogGroup.addChild(bg);
	}

	/**
	 * Adds ambient light of color col.
	 */
	public static Light ambientLight(final Color col)
	{

		Color3f lightColor = new Color3f(col);
		AmbientLight light = new AmbientLight(lightColor);

		light.setInfluencingBounds(StdDraw3D.INFINITE_BOUNDS);
		light.setCapability(javax.media.j3d.Light.ALLOW_STATE_WRITE);
		light.setCapability(javax.media.j3d.Light.ALLOW_COLOR_WRITE);

		BranchGroup bg = StdDraw3D.createBranchGroup();
		TransformGroup tg = StdDraw3D.createTransformGroup();
		tg.addChild(light);
		bg.addChild(tg);
		StdDraw3D.lightGroup.addChild(bg);

		return new Light(bg, tg, light);
	}

	/**
	 * Draws a box at (x, y, z) with dimensions (w, h, d).
	 */
	public static Shape box(final double x, final double y, final double z, final double w, final double h, final double d)
	{
		return StdDraw3D.box(x, y, z, w, h, d, 0, 0, 0, null);
	}

	/**
	 * Draws a box at (x, y, z) with dimensions (w, h, d) and axial rotations (xA, yA, zA).
	 */
	public static Shape box(
		final double x,
		final double y,
		final double z,
		final double w,
		final double h,
		final double d,
		final double xA,
		final double yA,
		final double zA)
	{
		return StdDraw3D.box(x, y, z, w, h, d, xA, yA, zA, null);
	}

	/**
	 * Draws a box at (x, y, z) with dimensions (w, h, d), axial rotations (xA, yA, zA), and a texture from imageURL.
	 */
	public static Shape box(
		final double x,
		final double y,
		final double z,
		final double w,
		final double h,
		final double d,
		final double xA,
		final double yA,
		final double zA,
		final String imageURL)
	{

		Appearance ap = StdDraw3D.createAppearance(imageURL, true);

		Vector3f dimensions = StdDraw3D.createVector3f(w, h, d);

		com.sun.j3d.utils.geometry.Box box = new
			com.sun.j3d.utils.geometry.Box(dimensions.x, dimensions.y, dimensions.z, StdDraw3D.PRIMFLAGS, ap, StdDraw3D.numDivisions);
		return StdDraw3D.primitive(box, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	/**
	 * Draws a box at (x, y, z) with dimensions (w, h, d) and a texture from imageURL.
	 */
	public static Shape box(final double x, final double y, final double z, final double w, final double h, final double d, final String imageURL)
	{
		return StdDraw3D.box(x, y, z, w, h, d, 0, 0, 0, imageURL);
	}

	/**
	 * Returns the Camera object.
	 */
	public static Camera camera()
	{
		return StdDraw3D.camera;
	}

	/**
	 * Clears the entire on the next call of show().
	 */
	public static void clear()
	{
		StdDraw3D.clear3D();
		StdDraw3D.clearOverlay();
	}

	public static void clear(final Color color)
	{
		StdDraw3D.setBackground(color);
		StdDraw3D.clear();
	}

	/**
	 * Clears the 3D world on the screen for the next call of show();
	 */
	public static void clear3D()
	{
		StdDraw3D.clear3D = true;
		StdDraw3D.offscreenGroup = StdDraw3D.createBranchGroup();
	}

	public static void clearFog()
	{
		StdDraw3D.fogGroup.removeAllChildren();
	}

	/**
	 * Removes all current lighting from the scene.
	 */
	public static void clearLight()
	{
		StdDraw3D.lightGroup.removeAllChildren();
	}

	/**
	 * Clears the 2D overlay for the next call of show();
	 */
	public static void clearOverlay()
	{
		StdDraw3D.clearOverlay = true;
		StdDraw3D.offscreenImage = StdDraw3D.createBufferedImage();
	}

	public static void clearSound()
	{
		StdDraw3D.soundGroup.removeAllChildren();
	}

	public static Shape coloredModel(final String filename)
	{
		return StdDraw3D.model(filename, true, true);
	}

	public static Shape coloredModel(final String filename, final boolean resize)
	{
		return StdDraw3D.model(filename, true, resize);
	}

	/**
	 * Combines any number of shapes into one shape and returns it.
	 */
	public static Shape combine(final Shape... shapes)
	{

		BranchGroup combinedGroup = StdDraw3D.createBranchGroup();
		TransformGroup combinedTransform = new TransformGroup();

		for (Shape shape: shapes)
		{
			BranchGroup bg = shape.bg;
			TransformGroup tg = shape.tg;

			StdDraw3D.offscreenGroup.removeChild(bg);
			StdDraw3D.onscreenGroup.removeChild(bg);

			bg.removeChild(tg);
			combinedTransform.addChild(shape.tg);
		}

		combinedGroup.addChild(combinedTransform);
		StdDraw3D.offscreenGroup.addChild(combinedGroup);
		return new Shape(combinedGroup, combinedTransform);
	}

	/**
	 * Draws a cone at (x, y, z) with radius r and height h.
	 */
	public static Shape cone(final double x, final double y, final double z, final double r, final double h)
	{
		return StdDraw3D.cone(x, y, z, r, h, 0, 0, 0, null);
	}

	/**
	 * Draws a cone at (x, y, z) with radius r, height h, and axial rotations (xA, yA, zA).
	 */
	public static Shape cone(final double x, final double y, final double z, final double r, final double h, final double xA, final double yA, final double zA)
	{
		return StdDraw3D.cone(x, y, z, r, h, xA, yA, zA, null);
	}

	/**
	 * Draws a cone at (x, y, z) with radius r, height h, axial rotations (xA, yA, zA), and a texture from imageURL.
	 */
	public static Shape cone(
		final double x,
		final double y,
		final double z,
		final double r,
		final double h,
		final double xA,
		final double yA,
		final double zA,
		final String imageURL)
	{

		Appearance ap = StdDraw3D.createAppearance(imageURL, true);
		Vector3f dimensions = StdDraw3D.createVector3f(r, h, 0);
		Cone cone = new Cone(dimensions.x, dimensions.y, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions, StdDraw3D.numDivisions, ap);
		return StdDraw3D.primitive(cone, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	/**
	 * Draws a cone at (x, y, z) with radius r, height h, and a texture from imageURL.
	 */
	public static Shape cone(final double x, final double y, final double z, final double r, final double h, final String imageURL)
	{
		return StdDraw3D.cone(x, y, z, r, h, 0, 0, 0, imageURL);
	}

	/* ***************************************************************
	 * Scaling and Screen Methods *
	 * ***************************************************************
	 */

	// *************************************************************************
	/**
	 * Constructs a Point3f array from the given coordinate arrays.
	 */
	private static Point3f[] constructPoint3f(final double[] x, final double[] y, final double[] z)
	{

		int size = x.length;
		Point3f[] coords = new Point3f[size];

		for (int i = 0; i < size; i++)
		{
			coords[i] = new Point3f(StdDraw3D.createVector3f(x[i], y[i], z[i]));
		}

		return coords;
	}

	/**
	 * Returns an identical copy of a Shape that can be controlled independently.
	 * Much more efficient than redrawing a specific shape or model.
	 */
	public static Shape copy(final Shape shape)
	{
		TransformGroup tg = shape.tg;
		TransformGroup tg2 = (TransformGroup)tg.cloneTree();
		BranchGroup bg2 = StdDraw3D.createBranchGroup();

		bg2.addChild(tg2);
		StdDraw3D.offscreenGroup.addChild(bg2);

		return new Shape(bg2, tg2);
	}

	/**
	 * Creates an Appearance.
	 *
	 * @param imageURL
	 *            Wraps a texture around from this image file.
	 * @param fill
	 *            If true, fills in faces. If false, outlines.
	 * @return The created appearance.
	 */
	private static Appearance createAppearance(final String imageURL, final boolean fill)
	{

		Appearance ap = StdDraw3D.createBlankAppearance();

		PolygonAttributes pa = new PolygonAttributes();
		if (!fill)
		{
			pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		}
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(pa);

		if (imageURL != null)
		{

			Texture texture = StdDraw3D.createTexture(imageURL);
			TextureAttributes texAttr = new TextureAttributes();
			texAttr.setTextureMode(TextureAttributes.REPLACE);

			ap.setTexture(texture);
			ap.setTextureAttributes(texAttr);
		}

		Color3f col = new Color3f(StdDraw3D.penColor);
		Color3f black = new Color3f(0, 0, 0);
		Color3f specular = new Color3f(StdDraw3D.GRAY);

		// Material properties
		Material material = new Material(col, black, col, specular, 64);
		material.setCapability(Material.ALLOW_COMPONENT_READ);
		material.setCapability(Material.ALLOW_COMPONENT_WRITE);
		material.setLightingEnable(true);
		ap.setMaterial(material);

		// Transparecy properties
		float alpha = ((float)StdDraw3D.penColor.getAlpha()) / 255;
		if (alpha < 1.0)
		{
			TransparencyAttributes t = new TransparencyAttributes();
			t.setTransparencyMode(TransparencyAttributes.BLENDED);
			t.setTransparency(1 - alpha);
			ap.setTransparencyAttributes(t);
		}

		LineAttributes la = new LineAttributes();
		la.setLineWidth(StdDraw3D.penRadius);
		la.setLineAntialiasingEnable(StdDraw3D.view.getSceneAntialiasingEnable());

		PointAttributes poa = new PointAttributes();
		poa.setPointAntialiasingEnable(StdDraw3D.view.getSceneAntialiasingEnable());

		ColoringAttributes ca = new ColoringAttributes();
		ca.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
		ca.setColor(col);

		ap.setLineAttributes(la);
		ap.setPointAttributes(poa);
		ap.setColoringAttributes(ca);

		return ap;
	}

	/**
	 * Creates a blank Background with the proper capabilities.
	 *
	 * @return The created Background.
	 */
	private static Background createBackground()
	{

		Background background = new Background();
		background.setCapability(Background.ALLOW_COLOR_WRITE);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		background.setCapability(Background.ALLOW_GEOMETRY_WRITE);
		background.setApplicationBounds(StdDraw3D.INFINITE_BOUNDS);
		return background;
	}

	private static Appearance createBlankAppearance()
	{
		Appearance ap = new Appearance();
		ap.setCapability(Appearance.ALLOW_MATERIAL_READ);
		ap.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		ap.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		ap.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		return ap;
	}

	/**
	 * Creates a blank BranchGroup with the proper capabilities.
	 */
	private static BranchGroup createBranchGroup()
	{

		BranchGroup bg = new BranchGroup();
		bg.setCapability(Group.ALLOW_CHILDREN_READ);
		bg.setCapability(Group.ALLOW_CHILDREN_WRITE);
		bg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.setPickable(false);
		bg.setCollidable(false);
		return bg;
	}

	/**
	 * Creates a blank BufferedImage.
	 *
	 * @return The created BufferedImage.
	 */
	private static BufferedImage createBufferedImage()
	{
		return new BufferedImage(StdDraw3D.width, StdDraw3D.height, BufferedImage.TYPE_INT_ARGB);
	}

	// FIX THIS check that adding specular didn't mess up custom shapes (lines, triangles, points)
	private static Appearance createCustomAppearance(final boolean fill)
	{
		Appearance ap = StdDraw3D.createBlankAppearance();

		PolygonAttributes pa = new PolygonAttributes();
		if (!fill)
		{
			pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		}
		pa.setCullFace(PolygonAttributes.CULL_NONE);

		LineAttributes la = new LineAttributes();
		la.setLineWidth(StdDraw3D.penRadius);
		la.setLineAntialiasingEnable(StdDraw3D.view.getSceneAntialiasingEnable());

		PointAttributes poa = new PointAttributes();
		poa.setPointAntialiasingEnable(StdDraw3D.view.getSceneAntialiasingEnable());

		ap.setPolygonAttributes(pa);
		ap.setLineAttributes(la);
		ap.setPointAttributes(poa);

		Color3f col = new Color3f(StdDraw3D.penColor);
		Color3f black = new Color3f(0, 0, 0);
		Color3f specular = new Color3f(StdDraw3D.GRAY);

		// Material properties
		Material material = new Material(col, black, col, specular, 64);
		material.setCapability(Material.ALLOW_COMPONENT_READ);
		material.setCapability(Material.ALLOW_COMPONENT_WRITE);
		material.setLightingEnable(true);
		ap.setMaterial(material);

		return ap;
	}

	/**
	 * Creates a menu bar as a basic GUI.
	 *
	 * @return The created JMenuBar.
	 */
	private static JMenuBar createMenuBar()
	{

		StdDraw3D.menuBar = new JMenuBar();

		StdDraw3D.fileMenu = new JMenu("File");
		StdDraw3D.menuBar.add(StdDraw3D.fileMenu);

		StdDraw3D.loadButton = new JMenuItem(" Load 3D Model..  ");
		StdDraw3D.loadButton.addActionListener(StdDraw3D.std);
		StdDraw3D.loadButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		StdDraw3D.fileMenu.add(StdDraw3D.loadButton);

		StdDraw3D.saveButton = new JMenuItem(" Save Image...  ");
		StdDraw3D.saveButton.addActionListener(StdDraw3D.std);
		StdDraw3D.saveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		StdDraw3D.fileMenu.add(StdDraw3D.saveButton);

		StdDraw3D.save3DButton = new JMenuItem(" Export 3D Scene...  ");
		StdDraw3D.save3DButton.addActionListener(StdDraw3D.std);
		StdDraw3D.save3DButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		// fileMenu.add(save3DButton);

		StdDraw3D.fileMenu.addSeparator();

		StdDraw3D.quitButton = new JMenuItem(" Quit...   ");
		StdDraw3D.quitButton.addActionListener(StdDraw3D.std);
		StdDraw3D.quitButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		StdDraw3D.fileMenu.add(StdDraw3D.quitButton);

		StdDraw3D.cameraMenu = new JMenu("Camera");
		StdDraw3D.menuBar.add(StdDraw3D.cameraMenu);

		JLabel cameraLabel = new JLabel("Camera Mode");
		cameraLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		cameraLabel.setForeground(StdDraw3D.GRAY);
		StdDraw3D.cameraMenu.add(cameraLabel);
		StdDraw3D.cameraMenu.addSeparator();

		ButtonGroup cameraButtonGroup = new ButtonGroup();

		StdDraw3D.orbitModeButton = new JRadioButtonMenuItem("Orbit Mode");
		StdDraw3D.orbitModeButton.setSelected(true);
		cameraButtonGroup.add(StdDraw3D.orbitModeButton);
		StdDraw3D.cameraMenu.add(StdDraw3D.orbitModeButton);
		StdDraw3D.orbitModeButton.addActionListener(StdDraw3D.std);
		StdDraw3D.orbitModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		StdDraw3D.fpsModeButton = new JRadioButtonMenuItem("First-Person Mode");
		cameraButtonGroup.add(StdDraw3D.fpsModeButton);
		StdDraw3D.cameraMenu.add(StdDraw3D.fpsModeButton);
		StdDraw3D.fpsModeButton.addActionListener(StdDraw3D.std);
		StdDraw3D.fpsModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		StdDraw3D.airplaneModeButton = new JRadioButtonMenuItem("Airplane Mode");
		cameraButtonGroup.add(StdDraw3D.airplaneModeButton);
		StdDraw3D.cameraMenu.add(StdDraw3D.airplaneModeButton);
		StdDraw3D.airplaneModeButton.addActionListener(StdDraw3D.std);
		StdDraw3D.airplaneModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		StdDraw3D.lookModeButton = new JRadioButtonMenuItem("Look Mode");
		cameraButtonGroup.add(StdDraw3D.lookModeButton);
		StdDraw3D.cameraMenu.add(StdDraw3D.lookModeButton);
		StdDraw3D.lookModeButton.addActionListener(StdDraw3D.std);
		StdDraw3D.lookModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		StdDraw3D.fixedModeButton = new JRadioButtonMenuItem("Fixed Mode");
		cameraButtonGroup.add(StdDraw3D.fixedModeButton);
		StdDraw3D.cameraMenu.add(StdDraw3D.fixedModeButton);
		StdDraw3D.fixedModeButton.addActionListener(StdDraw3D.std);
		StdDraw3D.fixedModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		StdDraw3D.cameraMenu.addSeparator();
		JLabel projectionLabel = new JLabel("Projection Mode");
		projectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		projectionLabel.setForeground(StdDraw3D.GRAY);
		StdDraw3D.cameraMenu.add(projectionLabel);
		StdDraw3D.cameraMenu.addSeparator();

		SpinnerNumberModel snm = new SpinnerNumberModel(StdDraw3D.DEFAULT_FOV, 0.5, 3.0, 0.05);
		StdDraw3D.fovSpinner = new JSpinner(snm);
		JPanel fovPanel = new JPanel();
		fovPanel.setLayout(new BoxLayout(fovPanel, BoxLayout.X_AXIS));
		JLabel fovLabel = new JLabel("Field of View:");
		fovPanel.add(javax.swing.Box.createRigidArea(new Dimension(30, 5)));
		fovPanel.add(fovLabel);
		fovPanel.add(javax.swing.Box.createRigidArea(new Dimension(10, 5)));
		fovPanel.add(StdDraw3D.fovSpinner);

		final ButtonGroup projectionButtons = new ButtonGroup();
		StdDraw3D.perspectiveButton = new JRadioButtonMenuItem("Perspective Projection");
		StdDraw3D.parallelButton = new JRadioButtonMenuItem("Parallel Projection");

		StdDraw3D.fovSpinner.addChangeListener(StdDraw3D.std);

		StdDraw3D.perspectiveButton.addActionListener(StdDraw3D.std);

		StdDraw3D.parallelButton.addActionListener(StdDraw3D.std);

		StdDraw3D.cameraMenu.add(StdDraw3D.parallelButton);
		StdDraw3D.cameraMenu.add(StdDraw3D.perspectiveButton);
		StdDraw3D.cameraMenu.add(fovPanel);

		projectionButtons.add(StdDraw3D.parallelButton);
		projectionButtons.add(StdDraw3D.perspectiveButton);
		StdDraw3D.perspectiveButton.setSelected(true);

		StdDraw3D.graphicsMenu = new JMenu("Graphics");
		// Leaving out graphics menu for now!!
		// menuBar.add(graphicsMenu);

		JLabel graphicsLabel = new JLabel("Polygon Count");
		graphicsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		graphicsLabel.setForeground(StdDraw3D.GRAY);
		StdDraw3D.graphicsMenu.add(graphicsLabel);
		StdDraw3D.graphicsMenu.addSeparator();

		SpinnerNumberModel snm2 =
			new SpinnerNumberModel(StdDraw3D.DEFAULT_NUM_DIVISIONS, 4, 4000, 5);
		StdDraw3D.numDivSpinner = new JSpinner(snm2);

		JPanel numDivPanel = new JPanel();
		numDivPanel.setLayout(new BoxLayout(numDivPanel, BoxLayout.X_AXIS));
		JLabel numDivLabel = new JLabel("Triangles:");
		numDivPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
		numDivPanel.add(numDivLabel);
		numDivPanel.add(javax.swing.Box.createRigidArea(new Dimension(15, 5)));
		numDivPanel.add(StdDraw3D.numDivSpinner);
		StdDraw3D.graphicsMenu.add(numDivPanel);

		StdDraw3D.numDivSpinner.addChangeListener(StdDraw3D.std);

		StdDraw3D.graphicsMenu.addSeparator();
		JLabel graphicsLabel2 = new JLabel("Advanced Rendering");
		graphicsLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
		graphicsLabel2.setForeground(StdDraw3D.GRAY);
		StdDraw3D.graphicsMenu.add(graphicsLabel2);
		StdDraw3D.graphicsMenu.addSeparator();

		StdDraw3D.antiAliasingButton = new JCheckBoxMenuItem("Enable Anti-Aliasing");
		StdDraw3D.antiAliasingButton.setSelected(false);
		StdDraw3D.antiAliasingButton.addActionListener(StdDraw3D.std);

		StdDraw3D.graphicsMenu.add(StdDraw3D.antiAliasingButton);

		StdDraw3D.infoCheckBox = new JCheckBox("Show Info Display");
		StdDraw3D.infoCheckBox.setFocusable(false);
		StdDraw3D.infoCheckBox.addActionListener(StdDraw3D.std);
		StdDraw3D.menuBar.add(javax.swing.Box.createRigidArea(new Dimension(50, 5)));
		StdDraw3D.menuBar.add(StdDraw3D.infoCheckBox);

		return StdDraw3D.menuBar;
	}

	/** Converts three double scalars to a Point3f. */
	private static Point3f createPoint3f(final double x, final double y, final double z)
	{
		return new Point3f((float)x, (float)y, (float)z);
	}

	/* ***************************************************************
	 * Pen Properties Methods *
	 * ***************************************************************
	 */

	/**
	 * Creates a scanner from the filename or website name.
	 */
	private static Scanner createScanner(final String s)
	{

		Scanner scanner;
		String charsetName = "ISO-8859-1";
		java.util.Locale usLocale = new java.util.Locale("en", "US");
		try
		{
			// first try to read file from local file system
			File file = new File(s);
			if (file.exists())
			{
				scanner = new Scanner(file, charsetName);
				scanner.useLocale(usLocale);
				return scanner;
			}

			// next try for website
			URL url = new URL(s);

			URLConnection site = url.openConnection();
			InputStream is = site.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), charsetName);
			scanner.useLocale(usLocale);
			return scanner;
		}
		catch (IOException ioe)
		{
			System.err.println("Could not open " + s + ".");
			return null;
		}
	}

	private static Shape3D createShape3D(final Geometry geom)
	{
		Shape3D shape = new Shape3D(geom);
		shape.setPickable(false);
		shape.setCollidable(false);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);

		return shape;
	}

	/**
	 * Creates a texture from the given filename.
	 *
	 * @param imageURL
	 *            Image file for creating texture.
	 * @return The created Texture.
	 * @throws RuntimeException
	 *             if the file could not be read.
	 */
	private static Texture createTexture(final String imageURL)
	{

		TextureLoader loader;
		try
		{
			loader = new TextureLoader(imageURL, "RGBA", TextureLoader.Y_UP, new Container());
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not read from the file '" + imageURL + "'");
		}

		Texture texture = loader.getTexture();
		texture.setBoundaryModeS(Texture.WRAP);
		texture.setBoundaryModeT(Texture.WRAP);
		texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));

		return texture;
	}

	/**
	 * Creates a blank TransformGroup with the proper capabilities.
	 */
	private static TransformGroup createTransformGroup()
	{

		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.setPickable(false);
		tg.setCollidable(false);
		return tg;
	}

	/** Converts a Vector3D to a Vector3d. */
	private static Vector3d createVector3d(final Vector3D v)
	{
		return new Vector3d(v.x, v.y, v.z);
	}

	/** Converts three double scalars to a Vector3f. **/
	private static Vector3f createVector3f(final double x, final double y, final double z)
	{
		return new Vector3f((float)x, (float)y, (float)z);
	}

	/** Converts a Vector3D to a Vector3f. */
	private static Vector3f createVector3f(final Vector3D v)
	{
		return StdDraw3D.createVector3f(v.x, v.y, v.z);
	}

	// *********************************************************************************************
	/**
	 * Draws a cube at (x, y, z) with radius r.
	 */
	public static Shape cube(final double x, final double y, final double z, final double r)
	{
		return StdDraw3D.cube(x, y, z, r, 0, 0, 0, null);
	}

	/**
	 * Draws a cube at (x, y, z) with radius r and axial rotations (xA, yA, zA).
	 */
	public static Shape cube(final double x, final double y, final double z, final double r, final double xA, final double yA, final double zA)
	{
		return StdDraw3D.cube(x, y, z, r, xA, yA, zA, null);
	}

	/**
	 * Draws a cube at (x, y, z) with radius r, axial rotations (xA, yA, zA), and a texture from imageURL.
	 */
	public static Shape cube(
		final double x,
		final double y,
		final double z,
		final double r,
		final double xA,
		final double yA,
		final double zA,
		final String imageURL)
	{
		return StdDraw3D.box(x, y, z, r, r, r, xA, yA, zA, imageURL);
	}

	/**
	 * Draws a cube at (x, y, z) with radius r and a texture from imageURL.
	 */
	public static Shape cube(final double x, final double y, final double z, final double r, final String imageURL)
	{
		return StdDraw3D.cube(x, y, z, r, 0, 0, 0, imageURL);
	}

	private static Shape customShape(final Shape3D shape)
	{
		return StdDraw3D.shape(shape, true, null, true);
	}

	/* ***************************************************************
	 * View Properties Methods *
	 * ***************************************************************
	 */

	/**
	 * Draws a cylinder at (x, y, z) with radius r and height h.
	 */
	public static Shape cylinder(final double x, final double y, final double z, final double r, final double h)
	{
		return StdDraw3D.cylinder(x, y, z, r, h, 0, 0, 0, null);
	}

	/**
	 * Draws a cylinder at (x, y, z) with radius r, height h, and axial rotations (xA, yA, zA).
	 */
	public static Shape cylinder(
		final double x,
		final double y,
		final double z,
		final double r,
		final double h,
		final double xA,
		final double yA,
		final double zA)
	{
		return StdDraw3D.cylinder(x, y, z, r, h, xA, yA, zA, null);
	}

	/**
	 * Draws a cylinder at (x, y, z) with radius r, height h, axial rotations (xA, yA, zA), and a texture from imageURL.
	 */
	public static Shape cylinder(
		final double x,
		final double y,
		final double z,
		final double r,
		final double h,
		final double xA,
		final double yA,
		final double zA,
		final String imageURL)
	{

		Appearance ap = StdDraw3D.createAppearance(imageURL, true);
		Vector3f dimensions = StdDraw3D.createVector3f(r, h, 0);
		Cylinder cyl = new Cylinder(dimensions.x, dimensions.y, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions, StdDraw3D.numDivisions, ap);
		return StdDraw3D.primitive(cyl, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	/**
	 * Draws a cylinder at (x, y, z) with radius r, height h, and a texture from imageURL.
	 */
	public static Shape cylinder(final double x, final double y, final double z, final double r, final double h, final String imageURL)
	{
		return StdDraw3D.cylinder(x, y, z, r, h, 0, 0, 0, imageURL);
	}

	/**
	 * Adds a directional light of color col which shines in the direction vector (x, y, z)
	 */
	public static Light directionalLight(final double x, final double y, final double z, final Color col)
	{

		DirectionalLight light = new DirectionalLight();
		light.setColor(new Color3f(col));

		light.setInfluencingBounds(StdDraw3D.INFINITE_BOUNDS);
		light.setCapability(javax.media.j3d.Light.ALLOW_STATE_WRITE);
		light.setCapability(javax.media.j3d.Light.ALLOW_COLOR_WRITE);
		light.setEnable(true);

		BranchGroup bg = StdDraw3D.createBranchGroup();
		TransformGroup tg = StdDraw3D.createTransformGroup();
		tg.addChild(light);
		bg.addChild(tg);
		StdDraw3D.lightGroup.addChild(bg);

		Light l = new Light(bg, tg, light);
		l.setDirection(new Vector3D(x, y, z));
		return l;
	}

	/* ***************************************************************
	 * Primitive 3D Drawing Methods *
	 * ***************************************************************
	 */

	/**
	 * Adds a directional light of color col which appears to come from (x, y, z).
	 */
	public static Light directionalLight(final Vector3D dir, final Color col)
	{
		return StdDraw3D.directionalLight(dir.x, dir.y, dir.z, col);
	}

	private static Shape drawOBJ(final String filename, final boolean colored, final boolean resize)
	{

		int params = 0;
		if (resize)
		{
			params = ObjectFile.RESIZE | Loader.LOAD_ALL;
		}

		ObjectFile loader = new ObjectFile(params);
		try
		{
			BranchGroup bg = loader.load(filename).getSceneGroup();
			bg.setCapability(Group.ALLOW_CHILDREN_READ);
			bg.setCapability(Group.ALLOW_CHILDREN_WRITE);
			bg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
			bg.setCapability(BranchGroup.ALLOW_DETACH);

			// System.out.println("Children: " + bg.numChildren());

			for (int i = 0; i < bg.numChildren(); i++)
			{
				Node child = bg.getChild(i);
				if (child instanceof Shape3D)
				{
					Shape3D shape = (Shape3D)child;
					// System.out.println("shape3d");
					// Appearance ap = shape.getAppearance();
					// PolygonAttributes pa = ap.getPolygonAttributes();
					// if (pa == null) pa = new PolygonAttributes();
					// pa.setCullFace(PolygonAttributes.CULL_NONE);
					// ap.setPolygonAttributes(pa);
					// Material m = ap.getMaterial();
					// m.setSpecularColor(new Color3f(GRAY));
					// m.setShininess(64);
					if (colored)
					{
						shape.setAppearance(StdDraw3D.createAppearance(null, true));
					}
					else
					{
						Appearance ap = shape.getAppearance();
						PolygonAttributes pa = ap.getPolygonAttributes();
						if (pa == null)
						{
							pa = new PolygonAttributes();
						}
						pa.setCullFace(PolygonAttributes.CULL_NONE);
						ap.setPolygonAttributes(pa);
					}
					// for (int j = 0; j < shape.numGeometries(); j++) {
					// Geometry g = shape.getGeometry(j);
					// if (g instanceof GeometryArray) {
					// GeometryArray ga = (GeometryArray) g;
					// System.out.println("GeometryArray");
					// System.out.println("format: " + ga.getVertexFormat());
					// float[] colors = ga.getInterleavedVertices();
					// for (int k = 0; k < colors.length; k++)
					// System.out.println(colors[k]);
					// }
					// }
				}
			}

			TransformGroup transGroup = new TransformGroup();
			transGroup.addChild(bg);
			BranchGroup bg2 = StdDraw3D.createBranchGroup();
			bg2.addChild(transGroup);
			StdDraw3D.offscreenGroup.addChild(bg2);
			return new Shape(bg2, transGroup);
		}
		catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		return null;
	}

	/**
	 * Draws a .ply file from a filename or website name.
	 */
	private static Shape drawPLY(final String filename, final boolean colored)
	{

		Scanner scanner = StdDraw3D.createScanner(filename);

		int vertices = -1;
		int triangles = -1;
		int properties = -1;

		while (true)
		{
			String s = scanner.next();
			if (s.equals("vertex"))
			{
				vertices = scanner.nextInt();
			}
			else
				if (s.equals("face"))
				{
					triangles = scanner.nextInt();
				}
				else
					if (s.equals("property"))
					{
						properties++;
						scanner.next();
						scanner.next();
					}
					else
						if (s.equals("end_header"))
						{
							break;
						}
		}

		System.out.println(vertices + " " + triangles + " " + properties);

		if ((vertices == -1) || (triangles == -1) || (properties == -1))
		{
			throw new RuntimeException("Cannot read format of .ply file!");
		}

		double[][] parameters = new double[properties][vertices];

		for (int i = 0; i < vertices; i++)
		{
			if ((i % 10000) == 0)
			{
				System.out.println("vertex " + i);
			}
			for (int j = 0; j < properties; j++)
			{
				parameters[j][i] = scanner.nextDouble();
			}
		}

		double[][] points = new double[triangles][9];

		for (int i = 0; i < triangles; i++)
		{
			int edges = scanner.nextInt();
			if (edges != 3)
			{
				throw new RuntimeException("Only triangular faces supported!");
			}

			if ((i % 10000) == 0)
			{
				System.out.println("face " + i);
			}

			int index = scanner.nextInt();
			points[i][0] = parameters[0][index];
			points[i][1] = parameters[1][index];
			points[i][2] = parameters[2][index];

			index = scanner.nextInt();
			points[i][3] = parameters[0][index];
			points[i][4] = parameters[1][index];
			points[i][5] = parameters[2][index];

			index = scanner.nextInt();
			points[i][6] = parameters[0][index];
			points[i][7] = parameters[1][index];
			points[i][8] = parameters[2][index];
		}

		return StdDraw3D.triangles(points);
	}

	/**
	 * Draws an ellipsoid at (x, y, z) with dimensions (w, h, d).
	 */
	public static Shape ellipsoid(final double x, final double y, final double z, final double w, final double h, final double d)
	{
		return StdDraw3D.ellipsoid(x, y, z, w, h, d, 0, 0, 0, null);
	}

	/**
	 * Draws an ellipsoid at (x, y, z) with dimensions (w, h, d) and axial rotations (xA, yA, zA).
	 */
	public static Shape ellipsoid(
		final double x,
		final double y,
		final double z,
		final double w,
		final double h,
		final double d,
		final double xA,
		final double yA,
		final double zA)
	{
		return StdDraw3D.ellipsoid(x, y, z, w, h, d, xA, yA, zA, null);
	}

	/**
	 * Draws an ellipsoid at (x, y, z) with dimensions (w, h, d), axial rotations (xA, yA, zA), and a texture from imageURL.
	 */
	public static Shape ellipsoid(
		final double x,
		final double y,
		final double z,
		final double w,
		final double h,
		final double d,
		final double xA,
		final double yA,
		final double zA,
		final String imageURL)
	{

		Sphere sphere = new Sphere(1, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions);
		sphere.setAppearance(StdDraw3D.createAppearance(imageURL, true));
		return StdDraw3D.primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), new Vector3d(w, h, d));
	}

	/**
	 * Draws an ellipsoid at (x, y, z) with dimensions (w, h, d) and a texture from imageURL.
	 */
	public static Shape ellipsoid(final double x, final double y, final double z, final double w, final double h, final double d, final String imageURL)
	{
		return StdDraw3D.ellipsoid(x, y, z, w, h, d, 0, 0, 0, imageURL);
	}

	/**
	 * Scales the given width from user coordinates into 2D pixel coordinates.
	 */
	private static double factorX(final double w)
	{

		double scaleDist = StdDraw3D.width;
		if (StdDraw3D.width > StdDraw3D.height)
		{
			scaleDist = StdDraw3D.height;
		}

		return scaleDist * (w / (2 * StdDraw3D.zoom));
	}

	/**
	 * Scales the given height from user coordinates into 2D pixel coordinates.
	 */
	private static double factorY(final double h)
	{

		double scaleDist = StdDraw3D.height;
		if (StdDraw3D.height > StdDraw3D.width)
		{
			scaleDist = StdDraw3D.width;
		}

		return scaleDist * (h / (2 * StdDraw3D.zoom));
	}

	/**
	 * Allows for camera navigation of a scene without redrawing. Useful when
	 * drawing a complicated scene once and then exploring without redrawing.
	 * Call only as the last line of a program.
	 */
	public static void finished()
	{

		StdDraw3D.show(1000000000);
	}

	public static void fullscreen()
	{

		StdDraw3D.frame.setResizable(true);
		StdDraw3D.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		int w = StdDraw3D.frame.getSize().width;
		int h = StdDraw3D.frame.getSize().height;

		// int w = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		// int h = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		int borderY = StdDraw3D.frame.getInsets().top + StdDraw3D.frame.getInsets().bottom;
		int borderX = StdDraw3D.frame.getInsets().left + StdDraw3D.frame.getInsets().right;

		StdDraw3D.setCanvasSize(w - borderX, h - borderY - StdDraw3D.menuBar.getHeight(), true);
		StdDraw3D.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	/* ***************************************************************
	 * Mouse Listener Methods *
	 * ***************************************************************
	 */

	/**
	 * Returns true if anti-aliasing is enabled.
	 */
	public static boolean getAntiAliasing()
	{
		return StdDraw3D.antiAliasingButton.isSelected();
	}

	public static Vector3D getCameraDirection()
	{
		return StdDraw3D.camera.getDirection();
	}

	/**
	 * Gets the current camera mode.
	 *
	 * @return The current camera mode.
	 */
	public static int getCameraMode()
	{
		return StdDraw3D.cameraMode;
	}

	public static Vector3D getCameraOrientation()
	{
		return StdDraw3D.camera.getOrientation();
	}

	public static Vector3D getCameraPosition()
	{
		return StdDraw3D.camera.getPosition();
	}

	/**
	 * Gets the current drawing Font.
	 *
	 * @return The current Font.
	 */
	public static Font getFont()
	{
		return StdDraw3D.font;
	}

	private static Graphics2D getGraphics2D(final BufferedImage image)
	{

		Graphics2D graphics = (Graphics2D)image.getGraphics();
		graphics.setColor(StdDraw3D.penColor);
		graphics.setFont(StdDraw3D.font);
		BasicStroke stroke = new BasicStroke(StdDraw3D.penRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		graphics.setStroke(stroke);

		// if (getAntiAliasing())
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		return graphics;
	}

	/**
	 * Gets an image from the given filename.
	 */
	private static Image getImage(final String filename)
	{

		// to read from file
		ImageIcon icon = new ImageIcon(filename);

		// try to read from URL
		if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE))
		{
			try
			{
				URL url = new URL(filename);
				icon = new ImageIcon(url);
			}
			catch (Exception e)
			{ /* not a url */
			}
		}

		// in case file is inside a .jar
		if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE))
		{
			URL url = StdDraw3D.class.getResource(filename);
			if (url == null)
			{
				throw new RuntimeException("image " + filename + " not found");
			}
			icon = new ImageIcon(url);
		}

		return icon.getImage();
	}

	/**
	 * Gets the number of triangular divisons of curved objects.
	 *
	 * @return The number of divisions.
	 */
	public static int getNumDivisions()
	{
		return StdDraw3D.numDivisions;
	}

	public static Vector3D getOrbitCenter()
	{
		return new Vector3D(StdDraw3D.orbitCenter);
	}

	/**
	 * Returns the current pen color.
	 *
	 * @return The current pen color.
	 */
	public static Color getPenColor()
	{
		return StdDraw3D.penColor;
	}

	/**
	 * Gets the current pen radius.
	 *
	 * @return The current pen radius.
	 */
	public static float getPenRadius()
	{
		return StdDraw3D.penRadius / 500f;
	}

	/**
	 * Has the user typed a key?
	 *
	 * @return True if the user has typed a key, false otherwise.
	 */
	public static boolean hasNextKeyTyped()
	{
		synchronized (StdDraw3D.keyLock)
		{
			return !StdDraw3D.keysTyped.isEmpty();
		}
	}

	private static void infoDisplay()
	{

		if (!StdDraw3D.infoDisplay)
		{
			StdDraw3D.infoImage = StdDraw3D.createBufferedImage();
			return;
		}

		BufferedImage bi = StdDraw3D.createBufferedImage();
		Graphics2D g = (Graphics2D)bi.getGraphics();
		g.setFont(new Font("Courier", Font.PLAIN, 11));
		g.setStroke(new BasicStroke(
			1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		double center = (StdDraw3D.min + StdDraw3D.max) / 2;
		double b = StdDraw3D.zoom * 0.1f;

		DecimalFormat df = new DecimalFormat(" 0.000;-0.000");

		Vector3D pos = StdDraw3D.camera.getPosition();
		String s = "(" + df.format(pos.x) + "," + df.format(pos.y) + "," + df.format(pos.z) + ")";
		g.setColor(StdDraw3D.BLACK);
		g.drawString("Position: " + s, 21, 26);
		g.setColor(StdDraw3D.LIGHT_GRAY);
		g.drawString("Position: " + s, 20, 25);

		Vector3D rot = StdDraw3D.camera.getOrientation();
		String s2 = "(" + df.format(rot.x) + "," + df.format(rot.y) + "," + df.format(rot.z) + ")";
		g.setColor(StdDraw3D.BLACK);
		g.drawString("Rotation: " + s2, 21, 41);
		g.setColor(StdDraw3D.LIGHT_GRAY);
		g.drawString("Rotation: " + s2, 20, 40);

		String mode;
		if (StdDraw3D.cameraMode == StdDraw3D.ORBIT_MODE)
		{
			mode = "Camera: ORBIT_MODE";
		}
		else
			if (StdDraw3D.cameraMode == StdDraw3D.FPS_MODE)
			{
				mode = "Camera: FPS_MODE";
			}
			else
				if (StdDraw3D.cameraMode == StdDraw3D.AIRPLANE_MODE)
				{
					mode = "Camera: AIRPLANE_MODE";
				}
				else
					if (StdDraw3D.cameraMode == StdDraw3D.LOOK_MODE)
					{
						mode = "Camera: LOOK_MODE";
					}
					else
						if (StdDraw3D.cameraMode == StdDraw3D.FIXED_MODE)
						{
							mode = "Camera: FIXED_MODE";
						}
						else
						{
							throw new RuntimeException("Unknown camera mode!");
						}

		g.setColor(StdDraw3D.BLACK);
		g.drawString(mode, 21, 56);
		g.setColor(StdDraw3D.LIGHT_GRAY);
		g.drawString(mode, 20, 55);

		double d = b / 4;
		g.draw(new Line2D.Double(StdDraw3D.scaleX(d + center), StdDraw3D.scaleY(0 + center), StdDraw3D.scaleX(-d + center), StdDraw3D.scaleY(0 + center)));
		g.draw(new Line2D.Double(StdDraw3D.scaleX(0 + center), StdDraw3D.scaleY(d + center), StdDraw3D.scaleX(0 + center), StdDraw3D.scaleY(-d + center)));

		StdDraw3D.infoImage = bi;
	}

	/**
	 * Initializes the 3D engine.
	 */
	private static void initialize()
	{

		StdDraw3D.numDivisions = StdDraw3D.DEFAULT_NUM_DIVISIONS;

		StdDraw3D.onscreenImage = StdDraw3D.createBufferedImage();
		StdDraw3D.offscreenImage = StdDraw3D.createBufferedImage();
		StdDraw3D.infoImage = StdDraw3D.createBufferedImage();

		StdDraw3D.initializeCanvas();

		if (StdDraw3D.frame != null)
		{
			StdDraw3D.frame.setVisible(false);
		}
		StdDraw3D.frame = new JFrame();
		StdDraw3D.frame.setVisible(false);
		StdDraw3D.frame.setResizable(StdDraw3D.fullscreen);
		StdDraw3D.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		StdDraw3D.frame.setTitle("Standard Draw 3D");
		StdDraw3D.frame.add(StdDraw3D.canvasPanel);
		StdDraw3D.frame.setJMenuBar(StdDraw3D.createMenuBar());
		StdDraw3D.frame.addComponentListener(StdDraw3D.std);
		StdDraw3D.frame.addWindowFocusListener(StdDraw3D.std);
		// frame.getContentPane().setCursor(new Cursor(Cursor.MOVE_CURSOR));
		StdDraw3D.frame.pack();

		StdDraw3D.rootGroup = StdDraw3D.createBranchGroup();
		StdDraw3D.lightGroup = StdDraw3D.createBranchGroup();
		StdDraw3D.bgGroup = StdDraw3D.createBranchGroup();
		StdDraw3D.soundGroup = StdDraw3D.createBranchGroup();
		StdDraw3D.fogGroup = StdDraw3D.createBranchGroup();
		StdDraw3D.appearanceGroup = StdDraw3D.createBranchGroup();

		StdDraw3D.onscreenGroup = StdDraw3D.createBranchGroup();
		StdDraw3D.offscreenGroup = StdDraw3D.createBranchGroup();

		StdDraw3D.rootGroup.addChild(StdDraw3D.onscreenGroup);
		StdDraw3D.rootGroup.addChild(StdDraw3D.lightGroup);
		StdDraw3D.rootGroup.addChild(StdDraw3D.bgGroup);
		StdDraw3D.rootGroup.addChild(StdDraw3D.soundGroup);
		StdDraw3D.rootGroup.addChild(StdDraw3D.fogGroup);
		StdDraw3D.rootGroup.addChild(StdDraw3D.appearanceGroup);

		StdDraw3D.universe = new SimpleUniverse(StdDraw3D.canvas, 2);
		StdDraw3D.universe.addBranchGraph(StdDraw3D.rootGroup);

		StdDraw3D.setDefaultLight();

		Viewer viewer = StdDraw3D.universe.getViewer();
		viewer.createAudioDevice();

		StdDraw3D.view = viewer.getView();
		StdDraw3D.view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
		StdDraw3D.view.setScreenScalePolicy(View.SCALE_EXPLICIT);
		StdDraw3D.view.setLocalEyeLightingEnable(true);
		StdDraw3D.setAntiAliasing(false);

		// view.setMinimumFrameCycleTime(long minimumTime);

		ViewingPlatform viewingPlatform = StdDraw3D.universe.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();

		StdDraw3D.orbit = new OrbitBehavior(StdDraw3D.canvas, OrbitBehavior.REVERSE_ALL ^ OrbitBehavior.STOP_ZOOM);
		BoundingSphere bounds = StdDraw3D.INFINITE_BOUNDS;
		StdDraw3D.orbit.setMinRadius(0);
		StdDraw3D.orbit.setSchedulingBounds(bounds);
		StdDraw3D.setOrbitCenter(new Point3d(0, 0, 0));

		viewingPlatform.setViewPlatformBehavior(StdDraw3D.orbit);
		TransformGroup cameraTG = viewingPlatform.getViewPlatformTransform();

		Transform3D cameraTrans = new Transform3D();
		cameraTG.getTransform(cameraTrans);

		viewingPlatform.detach();
		cameraTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		cameraTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		StdDraw3D.camera = new Camera(cameraTG);
		StdDraw3D.universe.addBranchGraph(viewingPlatform);

		StdDraw3D.setPerspectiveProjection();
		StdDraw3D.setCameraMode();
		StdDraw3D.setPenColor();
		StdDraw3D.setPenRadius();
		StdDraw3D.setFont();
		StdDraw3D.setScale();
		StdDraw3D.setInfoDisplay(true);

		StdDraw3D.setBackground(StdDraw3D.DEFAULT_BGCOLOR);

		StdDraw3D.frame.setVisible(true);
		StdDraw3D.frame.toFront();
		StdDraw3D.frame.setState(Frame.NORMAL);

	}

	/* ***************************************************************
	 * Keyboard Listener Methods *
	 * ***************************************************************
	 */

	/**
	 * Adds a Canvas3D to the given Panel p.
	 */
	private static void initializeCanvas()
	{

		Panel p = new Panel();

		GridBagLayout gl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		p.setLayout(gl);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 5;
		gbc.gridheight = 5;

		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		StdDraw3D.canvas = new Canvas3D(config)
		{

			/**
			 *
			 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void postRender()
			{
				J3DGraphics2D graphics = this.getGraphics2D();

				graphics.drawRenderedImage(StdDraw3D.onscreenImage, new AffineTransform());
				if (StdDraw3D.infoDisplay)
				{
					graphics.drawRenderedImage(StdDraw3D.infoImage, new AffineTransform());
				}

				graphics.flush(false);

				/*
				 * while (!showedOnce) {
				 *
				 * try { Thread.currentThread().sleep(0); }
				 * catch (InterruptedException e) { System.out.println("Error sleeping"); }
				 * }
				 * showedOnce = false;
				 */

				Thread.yield();
			}

			@Override
			public void preRender()
			{
				if (StdDraw3D.camera.pair != null)
				{
					StdDraw3D.camera.setPosition(StdDraw3D.camera.pair.getPosition());
					StdDraw3D.camera.setOrientation(StdDraw3D.camera.pair.getOrientation());
				}
			}

			/*
			 * public void postSwap () {
			 * while (!showedOnce) {
			 *
			 * try { Thread.currentThread().sleep(5); }
			 * catch (InterruptedException e) { System.out.println("Error sleeping"); }
			 * }
			 * //if (!showedOnce) {
			 * // try { Thread.currentThread().sleep(30); }
			 * // catch (InterruptedException e) { System.out.println("Error sleeping"); }
			 * //}
			 * //StdOut.println("showed once is false, rendered once");
			 * showedOnce = false;
			 * //renderedOnce = true;
			 *
			 * //Thread.yield();
			 * }
			 */
		};

		StdDraw3D.canvas.addKeyListener(StdDraw3D.std);
		StdDraw3D.canvas.addMouseListener(StdDraw3D.std);
		StdDraw3D.canvas.addMouseMotionListener(StdDraw3D.std);
		StdDraw3D.canvas.addMouseWheelListener(StdDraw3D.std);

		StdDraw3D.canvas.setSize(StdDraw3D.width, StdDraw3D.height);
		p.add(StdDraw3D.canvas, gbc);

		StdDraw3D.canvasPanel = p;

		// canvas.stopRenderer();
	}

	/**
	 * Is the given key currently pressed down? The keys correnspond
	 * to physical keys, not characters. For letters, use the
	 * uppercase character to refer to the key. For arrow keys and
	 * modifiers such as shift and ctrl, refer to the KeyEvent
	 * constants, such as KeyEvent.VK_SHIFT.
	 *
	 * @param key
	 *            The given key
	 * @return True if the given character is pressed.
	 */
	public static boolean isKeyPressed(final int key)
	{
		synchronized (StdDraw3D.keyLock)
		{
			return StdDraw3D.keysDown.contains(key);
		}
	}

	/**
	 * Draws a single line from (x1, y1, z1) to (x2, y2, z2).
	 */
	public static Shape line(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2)
	{

		return StdDraw3D.lines(new double[] {x1, x2}, new double[] {y1, y2}, new double[] {z1, z2});
	}

	/**
	 * Draws a set of connected lines. For example, the first line is from (x[0], y[0], z[0]) to
	 * (x[1], y[1], z[1]), and the second line is from (x[1], y[1], z[1]) to (x[2], y[2], z[2]).
	 * Much more efficient than drawing individual lines.
	 */
	public static Shape lines(final double[] x, final double[] y, final double[] z)
	{

		Point3f[] coords = StdDraw3D.constructPoint3f(x, y, z);

		GeometryArray geom = new LineStripArray
			(coords.length, GeometryArray.COORDINATES, new int[] {coords.length});
		geom.setCoordinates(0, coords);

		Shape3D shape = StdDraw3D.createShape3D(geom);

		return StdDraw3D.shape(shape);
	}

	/**
	 * Draws a set of connected lines. For example, the first line is from (x[0], y[0], z[0]) to
	 * (x[1], y[1], z[1]), and the second line is from (x[1], y[1], z[1]) to (x[2], y[2], z[2]).
	 * Much more efficient than drawing individual lines. Vertex colors are specified by the given
	 * array, and line colors are blends of its two vertex colors.
	 */
	public static Shape lines(final double[] x, final double[] y, final double[] z, final Color[] colors)
	{

		Point3f[] coords = StdDraw3D.constructPoint3f(x, y, z);

		GeometryArray geom = new LineStripArray
			(coords.length, GeometryArray.COORDINATES | GeometryArray.COLOR_4, new int[] {coords.length});
		geom.setCoordinates(0, coords);

		for (int i = 0; i < x.length; i++)
		{
			geom.setColor(i, colors[i].getComponents(null));
		}

		Shape3D shape = StdDraw3D.createShape3D(geom);

		return StdDraw3D.customShape(shape);
	}

	private static void loadAction()
	{

		FileDialog chooser = new FileDialog(StdDraw3D.frame, "Pick a .obj or .ply file to load.", FileDialog.LOAD);
		chooser.setVisible(true);
		String filename = chooser.getDirectory() + chooser.getFile();
		StdDraw3D.model(filename);

		StdDraw3D.keysDown.remove(KeyEvent.VK_META);
		StdDraw3D.keysDown.remove(KeyEvent.VK_CONTROL);
		StdDraw3D.keysDown.remove(KeyEvent.VK_L);
	}

	public static void loadScene3D(final String filename)
	{

		File file = new File(filename);

		try
		{
			SceneGraphFileReader reader = new SceneGraphFileReader(file);
			System.out.println("Branch graph count = " + reader.getBranchGraphCount());
			// BranchGroup[] bgs = reader.readAllBranchGraphs();
			// BranchGroup bg = (BranchGroup) reader.getNamedObject("rendered");
			BranchGroup bg = reader.readBranchGraph(0)[0];
			StdDraw3D.offscreenGroup = bg;
			// reader.dereferenceBranchGraph(offscreenGroup);
			System.out.println("Scene successfully loaded from " + filename + "!");
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		// catch (ObjectNotLoadedException onle) { onle.printStackTrace(); }
		// catch (NamedObjectException noe) { noe.printStackTrace(); }
	}

	/* ***************************************************************
	 * Action Listener Methods *
	 * ***************************************************************
	 */

	public static void main(final String[] args)
	{

		// Sets the scale
		StdDraw3D.setScale(-1, 1);

		// Turns off the default info HUD display.
		StdDraw3D.setInfoDisplay(false);

		// Draws the white square border.
		StdDraw3D.setPenColor(StdDraw3D.WHITE);
		StdDraw3D.overlaySquare(0, 0, 0.98);

		// Draws the two red circles.
		StdDraw3D.setPenRadius(0.06);
		StdDraw3D.setPenColor(StdDraw3D.RED, 220);
		StdDraw3D.overlayCircle(0, 0, 0.8);
		StdDraw3D.setPenColor(StdDraw3D.RED, 220);
		StdDraw3D.overlayCircle(0, 0, 0.6);

		// Draws the information text.
		StdDraw3D.setPenColor(StdDraw3D.WHITE);
		StdDraw3D.overlayText(0, 0.91, "Standard Draw 3D - Test Program");
		StdDraw3D.overlayText(0, -0.95, "You should see rotating text. Drag the mouse to orbit.");

		// Creates the 3D text object and centers it.
		StdDraw3D.setPenColor(StdDraw3D.YELLOW);
		StdDraw3D.setFont(new Font("Arial", Font.BOLD, 16));
		StdDraw3D.Shape text = StdDraw3D.text3D(0, 0, 0, "StdDraw3D");
		text.scale(3.5);
		text.move(-0.7, -0.1, 0);
		text = StdDraw3D.combine(text);

		while (true)
		{

			// Rotates the 3D text by 1.2 degrees along the y-axis.
			text.rotate(0, 1.2, 0);

			// Shows the frame for 20 milliseconds.
			StdDraw3D.show(20);
		}
	}

	public static Shape model(final String filename)
	{
		return StdDraw3D.model(filename, false);
	}

	public static Shape model(final String filename, final boolean resize)
	{
		return StdDraw3D.model(filename, false, resize);
	}

	private static Shape model(final String filename, final boolean colored, final boolean resize)
	{

		if (filename == null)
		{
			return null;
		}
		String suffix = filename.substring(filename.lastIndexOf('.') + 1);
		suffix = suffix.toLowerCase();

		if (suffix.equals("ply"))
		{
			return StdDraw3D.drawPLY(filename, colored);
		}
		else
			if (suffix.equals("obj"))
			{
				return StdDraw3D.drawOBJ(filename, colored, resize);
				// else if (suffix.equals("lws"))
				// return drawLWS(filename);
			}
			else
			{
				throw new RuntimeException("Format not supported!");
			}
	}

	/* ***************************************************************
	 * Light and Background Methods *
	 * ***************************************************************
	 */

	/**
	 * Is the mouse1 button pressed down?
	 */
	public static boolean mouse1Pressed()
	{
		synchronized (StdDraw3D.mouseLock)
		{
			return StdDraw3D.mouse1;
		}
	}

	/**
	 * Is the mouse2 button pressed down?
	 */
	public static boolean mouse2Pressed()
	{
		synchronized (StdDraw3D.mouseLock)
		{
			return StdDraw3D.mouse2;
		}
	}

	/**
	 * Is the mouse3 button pressed down?
	 */
	public static boolean mouse3Pressed()
	{
		synchronized (StdDraw3D.mouseLock)
		{
			return StdDraw3D.mouse3;
		}
	}

	// *********************************************************************************************

	private static void mouseMotionEvents(final MouseEvent e, final double newX, final double newY, final boolean dragged)
	{

		// System.out.println("x = " + mouseX() + " y = " + mouseY());

		if (StdDraw3D.cameraMode == StdDraw3D.FIXED_MODE)
		{
			return;
		}

		if (StdDraw3D.cameraMode == StdDraw3D.FPS_MODE)
		{
			if (dragged || StdDraw3D.immersive)
			{
				StdDraw3D.camera.rotateFPS((StdDraw3D.mouseY - newY) / 4, (StdDraw3D.mouseX - newX) / 4, 0);
			}
			return;
		}

		if ((StdDraw3D.cameraMode == StdDraw3D.AIRPLANE_MODE))
		{
			if (dragged || StdDraw3D.immersive)
			{
				StdDraw3D.camera.rotateRelative((StdDraw3D.mouseY - newY) / 4, (StdDraw3D.mouseX - newX) / 4, 0);
			}
			return;
		}

		if ((StdDraw3D.cameraMode == StdDraw3D.LOOK_MODE))
		{
			if (dragged || StdDraw3D.immersive)
			{
				StdDraw3D.camera.rotateFPS((StdDraw3D.mouseY - newY) / 4, (StdDraw3D.mouseX - newX) / 4, 0);
			}
			return;
		}

		if ((StdDraw3D.cameraMode == StdDraw3D.ORBIT_MODE)
			&& (dragged && StdDraw3D.isKeyPressed(KeyEvent.VK_ALT))
			&& (StdDraw3D.view.getProjectionPolicy() == View.PARALLEL_PROJECTION))
		{
			StdDraw3D.camera.moveRelative(0, 0, ((newY - StdDraw3D.mouseY) * StdDraw3D.zoom) / 50);
			return;
		}
	}

	/** Is any mouse button pressed? */
	public static boolean mousePressed()
	{
		synchronized (StdDraw3D.mouseLock)
		{
			return (StdDraw3D.mouse1Pressed() || StdDraw3D.mouse2Pressed() || StdDraw3D.mouse3Pressed());
		}
	}

	/**
	 * Where is the mouse?
	 *
	 * @return The value of the X-coordinate of the mouse.
	 */
	public static double mouseX()
	{
		synchronized (StdDraw3D.mouseLock)
		{
			return StdDraw3D.unscaleX(StdDraw3D.mouseX);
		}
	}

	/**
	 * Where is the mouse?
	 *
	 * @return The value of the Y-coordinate of the mouse.
	 */
	public static double mouseY()
	{
		synchronized (StdDraw3D.mouseLock)
		{
			return StdDraw3D.unscaleY(StdDraw3D.mouseY);
		}
	}

	// public static void playPointSound (String filename, Vector3D position, double volume, boolean loop) {
	//
	// MediaContainer mc = new MediaContainer("file:" + filename);
	// mc.setCacheEnable(true);
	//
	// PointSound sound = new PointSound();
	// sound.setInitialGain((float)volume);
	// sound.setSoundData(mc);
	// sound.setBounds(INFINITE_BOUNDS);
	// sound.setSchedulingBounds(INFINITE_BOUNDS);
	// if (loop == true) sound.setLoop(Sound.INFINITE_LOOPS);
	// sound.setPosition(createPoint3f(position));
	// Point2f[] distanceGain = new Point2f[2];
	// distanceGain[0] = new Point2f(0, 1);
	// distanceGain[1] = new Point2f(20, 0);
	// sound.setDistanceGain(distanceGain);
	// sound.setEnable(true);
	//
	// BranchGroup bg = createBranchGroup();
	// bg.addChild(sound);
	// soundGroup.addChild(bg);
	// }
	// *********************************************************************************************

	/**
	 * Processes one frame of keyboard events.
	 *
	 * @param time
	 *            The amount of time for this frame.
	 */
	private static void moveEvents(final int time)
	{

		StdDraw3D.infoDisplay();

		if (StdDraw3D.isKeyPressed(KeyEvent.VK_CONTROL))
		{
			return;
		}

		if (StdDraw3D.cameraMode == StdDraw3D.FPS_MODE)
		{
			double move = 0.00015 * time * (StdDraw3D.zoom);
			if (StdDraw3D.isKeyPressed('W') || StdDraw3D.isKeyPressed(KeyEvent.VK_UP))
			{
				StdDraw3D.camera.moveRelative(0, 0, move * 3);
			}
			if (StdDraw3D.isKeyPressed('S') || StdDraw3D.isKeyPressed(KeyEvent.VK_DOWN))
			{
				StdDraw3D.camera.moveRelative(0, 0, -move * 3);
			}
			if (StdDraw3D.isKeyPressed('A') || StdDraw3D.isKeyPressed(KeyEvent.VK_LEFT))
			{
				StdDraw3D.camera.moveRelative(-move, 0, 0);
			}
			if (StdDraw3D.isKeyPressed('D') || StdDraw3D.isKeyPressed(KeyEvent.VK_RIGHT))
			{
				StdDraw3D.camera.moveRelative(move, 0, 0);
			}
			if (StdDraw3D.isKeyPressed('Q') || StdDraw3D.isKeyPressed(KeyEvent.VK_PAGE_UP))
			{
				StdDraw3D.camera.moveRelative(0, move, 0);
			}
			if (StdDraw3D.isKeyPressed('E') || StdDraw3D.isKeyPressed(KeyEvent.VK_PAGE_DOWN))
			{
				StdDraw3D.camera.moveRelative(0, -move, 0);
			}
		}
		if (StdDraw3D.cameraMode == StdDraw3D.AIRPLANE_MODE)
		{
			double move = 0.00015 * time * (StdDraw3D.zoom);
			if (StdDraw3D.isKeyPressed('W') || StdDraw3D.isKeyPressed(KeyEvent.VK_UP))
			{
				StdDraw3D.camera.moveRelative(0, 0, move * 3);
			}
			if (StdDraw3D.isKeyPressed('S') || StdDraw3D.isKeyPressed(KeyEvent.VK_DOWN))
			{
				StdDraw3D.camera.moveRelative(0, 0, -move * 3);
			}
			if (StdDraw3D.isKeyPressed('A') || StdDraw3D.isKeyPressed(KeyEvent.VK_LEFT))
			{
				StdDraw3D.camera.moveRelative(-move, 0, 0);
			}
			if (StdDraw3D.isKeyPressed('D') || StdDraw3D.isKeyPressed(KeyEvent.VK_RIGHT))
			{
				StdDraw3D.camera.moveRelative(move, 0, 0);
			}
			if (StdDraw3D.isKeyPressed('Q') || StdDraw3D.isKeyPressed(KeyEvent.VK_PAGE_UP))
			{
				StdDraw3D.camera.rotateRelative(0, 0, (move * 250) / StdDraw3D.zoom);
			}
			if (StdDraw3D.isKeyPressed('E') || StdDraw3D.isKeyPressed(KeyEvent.VK_PAGE_DOWN))
			{
				StdDraw3D.camera.rotateRelative(0, 0, (-move * 250) / StdDraw3D.zoom);
			}
		}
	}

	/**
	 * What is the next key that was typed by the user?
	 *
	 * @return The next key typed.
	 */
	public static char nextKeyTyped()
	{
		synchronized (StdDraw3D.keyLock)
		{
			return StdDraw3D.keysTyped.removeLast();
		}
	}

	// *********************************************************************************************

	/**
	 * Draws an arc of radius r, centered on (x, y), from angle1 to angle2 (in degrees).
	 */
	public static void overlayArc(final double x, final double y, final double r, final double angle1, double angle2)
	{
		if (r < 0)
		{
			throw new RuntimeException("arc radius can't be negative");
		}
		while (angle2 < angle1)
		{
			angle2 += 360;
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * r);
		double hs = StdDraw3D.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).draw(new Arc2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs, angle1, angle2 - angle1, Arc2D.OPEN));
		}
	}

	/**
	 * Draws a circle of radius r, centered on (x, y).
	 */
	public static void overlayCircle(final double x, final double y, final double r)
	{

		if (r < 0)
		{
			throw new RuntimeException("circle radius can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * r);
		double hs = StdDraw3D.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).draw(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws an ellipse with given semimajor and semiminor axes, centered on (x, y).
	 */
	public static void overlayEllipse(final double x, final double y, final double semiMajorAxis, final double semiMinorAxis)
	{
		if (semiMajorAxis < 0)
		{
			throw new RuntimeException("ellipse semimajor axis can't be negative");
		}
		if (semiMinorAxis < 0)
		{
			throw new RuntimeException("ellipse semiminor axis can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * semiMajorAxis);
		double hs = StdDraw3D.factorY(2 * semiMinorAxis);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).draw(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws a filled circle of radius r, centered on (x, y).
	 */
	public static void overlayFilledCircle(final double x, final double y, final double r)
	{

		if (r < 0)
		{
			throw new RuntimeException("circle radius can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * r);
		double hs = StdDraw3D.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).fill(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws a filled ellipse with given semimajor and semiminor axes, centered on (x, y).
	 */
	public static void overlayFilledEllipse(final double x, final double y, final double semiMajorAxis, final double semiMinorAxis)
	{
		if (semiMajorAxis < 0)
		{
			throw new RuntimeException("ellipse semimajor axis can't be negative");
		}
		if (semiMinorAxis < 0)
		{
			throw new RuntimeException("ellipse semiminor axis can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * semiMajorAxis);
		double hs = StdDraw3D.factorY(2 * semiMinorAxis);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).fill(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws a filled polygon with the given (x[i], y[i]) coordinates.
	 */
	public static void overlayFilledPolygon(final double[] x, final double[] y)
	{
		int N = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo(StdDraw3D.scaleX(x[0]), StdDraw3D.scaleY(y[0]));
		for (int i = 0; i < N; i++)
		{
			path.lineTo(StdDraw3D.scaleX(x[i]), StdDraw3D.scaleY(y[i]));
		}
		path.closePath();
		StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).fill(path);
	}

	/**
	 * Draws a filled rectangle of given half width and half height, centered on (x, y).
	 */
	public static void overlayFilledRectangle(final double x, final double y, final double halfWidth, final double halfHeight)
	{
		if (halfWidth < 0)
		{
			throw new RuntimeException("half width can't be negative");
		}
		if (halfHeight < 0)
		{
			throw new RuntimeException("half height can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * halfWidth);
		double hs = StdDraw3D.factorY(2 * halfHeight);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).fill(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws a filled square of side length 2r, centered on (x, y).
	 */
	public static void overlayFilledSquare(final double x, final double y, final double r)
	{
		if (r < 0)
		{
			throw new RuntimeException("square side length can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * r);
		double hs = StdDraw3D.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).fill(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws a line from (x0, y0) to (x1, y1).
	 */
	public static void overlayLine(final double x0, final double y0, final double x1, final double y1)
	{
		StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).draw(
			new Line2D.Double(StdDraw3D.scaleX(x0), StdDraw3D.scaleY(y0), StdDraw3D.scaleX(x1), StdDraw3D.scaleY(y1)));
	}

	/**
	 * Draws a picture (gif, jpg, or png) centered on (x, y).
	 */
	public static void overlayPicture(final double x, final double y, final String s)
	{
		Image image = StdDraw3D.getImage(s);
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		int ws = image.getWidth(null);
		int hs = image.getHeight(null);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}
		StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).drawImage(image, (int)Math.round(xs - (ws / 2.0)), (int)Math.round(ys - (hs / 2.0)), null);
	}

	/**
	 * Draws a picture (gif, jpg, or png) centered on (x, y),
	 * rotated given number of degrees.
	 */
	public static void overlayPicture(final double x, final double y, final String s, final double degrees)
	{
		Image image = StdDraw3D.getImage(s);
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		int ws = image.getWidth(null);
		int hs = image.getHeight(null);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}

		Graphics2D graphics = StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage);
		graphics.rotate(Math.toRadians(-degrees), xs, ys);
		graphics.drawImage(image, (int)Math.round(xs - (ws / 2.0)), (int)Math.round(ys - (hs / 2.0)), null);
		graphics.rotate(Math.toRadians(+degrees), xs, ys);
	}

	/**
	 * Draw picture (gif, jpg, or png) centered on (x, y), rescaled to w-by-h.
	 */
	public static void overlayPicture(final double x, final double y, final String s, final double w, final double h)
	{
		Image image = StdDraw3D.getImage(s);
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		if (w < 0)
		{
			throw new RuntimeException("width is negative: " + w);
		}
		if (h < 0)
		{
			throw new RuntimeException("height is negative: " + h);
		}
		double ws = StdDraw3D.factorX(w);
		double hs = StdDraw3D.factorY(h);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).drawImage(image, (int)Math.round(xs - (ws / 2.0)),
				(int)Math.round(ys - (hs / 2.0)),
				(int)Math.round(ws),
				(int)Math.round(hs), null);
		}
	}

	/* ***************************************************************
	 * Animation Frame Methods *
	 * ***************************************************************
	 */

	/**
	 * Draw picture (gif, jpg, or png) centered on (x, y), rotated
	 * given number of degrees, rescaled to w-by-h.
	 */
	public static void overlayPicture(final double x, final double y, final String s, final double w, final double h, final double degrees)
	{
		Image image = StdDraw3D.getImage(s);
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(w);
		double hs = StdDraw3D.factorY(h);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}

		Graphics2D graphics = StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage);
		graphics.rotate(Math.toRadians(-degrees), xs, ys);
		graphics.drawImage(image, (int)Math.round(xs - (ws / 2.0)),
			(int)Math.round(ys - (hs / 2.0)),
			(int)Math.round(ws),
			(int)Math.round(hs), null);
		graphics.rotate(Math.toRadians(+degrees), xs, ys);
	}

	/**
	 * Draws one pixel at (x, y).
	 */
	public static void overlayPixel(final double x, final double y)
	{
		StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).fillRect(Math.round(StdDraw3D.scaleX(x)), Math.round(StdDraw3D.scaleY(y)), 1, 1);
	}

	/**
	 * Draws a point at (x, y).
	 */
	public static void overlayPoint(final double x, final double y)
	{
		float r = StdDraw3D.penRadius;
		if (r <= 1)
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).fill(new Ellipse2D.Double(StdDraw3D.scaleX(x) - (r / 2), StdDraw3D.scaleY(y) - (r / 2), r, r));
		}
	}

	/**
	 * Draws a polygon with the given (x[i], y[i]) coordinates.
	 */
	public static void overlayPolygon(final double[] x, final double[] y)
	{
		int N = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo(StdDraw3D.scaleX(x[0]), StdDraw3D.scaleY(y[0]));
		for (int i = 0; i < N; i++)
		{
			path.lineTo(StdDraw3D.scaleX(x[i]), StdDraw3D.scaleY(y[i]));
		}
		path.closePath();
		StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).draw(path);
	}

	/**
	 * Draws a rectangle of given half width and half height, centered on (x, y).
	 */
	public static void overlayRectangle(final double x, final double y, final double halfWidth, final double halfHeight)
	{
		if (halfWidth < 0)
		{
			throw new RuntimeException("half width can't be negative");
		}
		if (halfHeight < 0)
		{
			throw new RuntimeException("half height can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * halfWidth);
		double hs = StdDraw3D.factorY(2 * halfHeight);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).draw(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws a square of side length 2r, centered on (x, y).
	 */
	public static void overlaySquare(final double x, final double y, final double r)
	{
		if (r < 0)
		{
			throw new RuntimeException("square side length can't be negative");
		}
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		double ws = StdDraw3D.factorX(2 * r);
		double hs = StdDraw3D.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			StdDraw3D.overlayPixel(x, y);
		}
		else
		{
			StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage).draw(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
	}

	/**
	 * Draws the given text as stationary on the window at (x, y).
	 * This is useful for titles and HUD-style text.
	 */
	public static void overlayText(final double x, final double y, final String text)
	{
		Graphics2D graphics = StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage);
		FontMetrics metrics = graphics.getFontMetrics();
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		int ws = metrics.stringWidth(text);
		int hs = metrics.getDescent();
		graphics.drawString(text, (float)(xs - (ws / 2.0)), (float)(ys + hs));
	}

	/**
	 * Writes the given text string in the current font, centered on (x, y) and
	 * rotated by the specified number of degrees.
	 */
	public static void overlayText(final double x, final double y, final String text, final double degrees)
	{
		Graphics2D graphics = StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage);
		FontMetrics metrics = graphics.getFontMetrics();
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		int ws = metrics.stringWidth(text);
		int hs = metrics.getDescent();
		graphics.rotate(Math.toRadians(-degrees), xs, ys);
		graphics.drawString(text, (float)(xs - (ws / 2.0)), (float)(ys + hs));
		graphics.rotate(Math.toRadians(+degrees), xs, ys);
	}

	/**
	 * Write the given text string in the current font, left-aligned at (x, y).
	 */
	public static void overlayTextLeft(final double x, final double y, final String text)
	{
		Graphics2D graphics = StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage);
		FontMetrics metrics = graphics.getFontMetrics();
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		metrics.stringWidth(text);
		int hs = metrics.getDescent();
		graphics.drawString(text, (float)(xs), (float)(ys + hs));
	}

	/**
	 * Write the given text string in the current font, right-aligned at (x, y).
	 */
	public static void overlayTextRight(final double x, final double y, final String text)
	{
		Graphics2D graphics = StdDraw3D.getGraphics2D(StdDraw3D.offscreenImage);
		FontMetrics metrics = graphics.getFontMetrics();
		double xs = StdDraw3D.scaleX(x);
		double ys = StdDraw3D.scaleY(y);
		int ws = metrics.stringWidth(text);
		int hs = metrics.getDescent();
		graphics.drawString(text, (float)(xs - ws), (float)(ys + hs));
	}

	/**
	 * Pauses for a given amount of milliseconds.
	 *
	 * @param time
	 *            The number of milliseconds to pause for.
	 */
	public static void pause(final int time)
	{

		int t = time;
		int dt = 15;

		while (t > dt)
		{
			StdDraw3D.moveEvents(dt);
			Toolkit.getDefaultToolkit().sync();
			try
			{
				Thread.currentThread();
				Thread.sleep(dt);
			}
			catch (InterruptedException e)
			{
				System.out.println("Error sleeping");
			}

			t -= dt;
		}

		StdDraw3D.moveEvents(t);
		if (t == 0)
		{
			return;
		}
		try
		{
			Thread.currentThread();
			Thread.sleep(t);
		}
		catch (InterruptedException e)
		{
			System.out.println("Error sleeping");
		}

		// while (!renderedOnce) {
		// try { Thread.currentThread().sleep(1); }
		// catch (InterruptedException e) { System.out.println("Error sleeping"); }
		//
		// renderedOnce = false;
		// showedOnce = true;
	}

	public static void playAmbientSound(final String filename)
	{
		StdDraw3D.playAmbientSound(filename, 1.0, false);
	}

	public static void playAmbientSound(final String filename, final boolean loop)
	{
		StdDraw3D.playAmbientSound(filename, 1.0, loop);
	}

	private static void playAmbientSound(final String filename, final double volume, final boolean loop)
	{

		MediaContainer mc = new MediaContainer("file:" + filename);
		mc.setCacheEnable(true);

		BackgroundSound sound = new BackgroundSound();
		sound.setInitialGain((float)volume);
		sound.setSoundData(mc);
		sound.setBounds(StdDraw3D.INFINITE_BOUNDS);
		sound.setSchedulingBounds(StdDraw3D.INFINITE_BOUNDS);
		if (loop == true)
		{
			sound.setLoop(Sound.INFINITE_LOOPS);
		}
		sound.setEnable(true);

		BranchGroup bg = StdDraw3D.createBranchGroup();
		bg.addChild(sound);
		StdDraw3D.soundGroup.addChild(bg);
	}

	/**
	 * Draws a single point at (x, y, z).
	 */
	public static Shape point(final double x, final double y, final double z)
	{

		return StdDraw3D.points(new double[] {x}, new double[] {y}, new double[] {z});
	}

	public static Light pointLight(final double x, final double y, final double z, final Color col)
	{
		return StdDraw3D.pointLight(x, y, z, col, 1.0);
	}

	public static Light pointLight(final double x, final double y, final double z, final Color col, final double power)
	{

		PointLight light = new PointLight();
		light.setColor(new Color3f(col));

		light.setInfluencingBounds(StdDraw3D.INFINITE_BOUNDS);
		light.setCapability(javax.media.j3d.Light.ALLOW_STATE_WRITE);
		light.setCapability(javax.media.j3d.Light.ALLOW_COLOR_WRITE);
		light.setCapability(PointLight.ALLOW_ATTENUATION_WRITE);

		float scale = (float)StdDraw3D.zoom;
		float linearFade = 0.03f;
		float quadraticFade = 0.03f;
		light.setAttenuation(1.0f, linearFade / scale, quadraticFade / (scale * scale));

		BranchGroup bg = StdDraw3D.createBranchGroup();
		TransformGroup tg = StdDraw3D.createTransformGroup();
		tg.addChild(light);
		bg.addChild(tg);
		StdDraw3D.lightGroup.addChild(bg);

		Light l = new Light(bg, tg, light);
		l.setPosition(x, y, z);
		l.scalePower(power);

		return l;
	}

	public static Light pointLight(final Vector3D origin, final Color col)
	{
		return StdDraw3D.pointLight(origin.x, origin.y, origin.z, col, 1.0);
	}

	public static Light pointLight(final Vector3D origin, final Color col, final double power)
	{
		return StdDraw3D.pointLight(origin.x, origin.y, origin.z, col, power);
	}

	/**
	 * Draws a set of points at the given coordinates. For example,
	 * the first point is at (x[0], y[0], z[0]).
	 * Much more efficient than drawing individual points.
	 */
	public static Shape points(final double[] x, final double[] y, final double[] z)
	{

		Point3f[] coords = StdDraw3D.constructPoint3f(x, y, z);

		GeometryArray geom = new PointArray(coords.length, GeometryArray.COORDINATES);
		geom.setCoordinates(0, coords);

		Shape3D shape = StdDraw3D.createShape3D(geom);

		return StdDraw3D.shape(shape);
	}

	// *********************************************************************************************

	/**
	 * Draws a set of points at the given coordinates with the given colors.
	 * For example, the first point is at (x[0], y[0], z[0]) with color colors[0].
	 * Much more efficient than drawing individual points.
	 */
	public static Shape points(final double[] x, final double[] y, final double[] z, final Color[] colors)
	{

		Point3f[] coords = StdDraw3D.constructPoint3f(x, y, z);

		GeometryArray geom = new PointArray(coords.length, GeometryArray.COORDINATES | GeometryArray.COLOR_4);
		geom.setCoordinates(0, coords);

		for (int i = 0; i < x.length; i++)
		{
			geom.setColor(i, colors[i].getComponents(null));
		}

		Shape3D shape = StdDraw3D.createShape3D(geom);

		return StdDraw3D.customShape(shape);
	}

	/**
	 * Draws a filled polygon with the given vertices. The vertices should be planar for a
	 * proper 2D polygon to be drawn.
	 */
	public static Shape polygon(final double[] x, final double[] y, final double[] z)
	{
		return StdDraw3D.polygon(x, y, z, true);
	}

	/**
	 * Draws a polygon with the given vertices, which is filled or outlined based on the argument.
	 *
	 * @param filled
	 *            Is the polygon filled?
	 */
	private static Shape polygon(final double[] x, final double[] y, final double[] z, final boolean filled)
	{

		Point3f[] coords = StdDraw3D.constructPoint3f(x, y, z);
		GeometryArray geom = new TriangleFanArray(coords.length, GeometryArray.COORDINATES, new int[] {coords.length});
		geom.setCoordinates(0, coords);

		GeometryInfo geoinfo = new GeometryInfo(geom);
		NormalGenerator normalGenerator = new NormalGenerator();
		normalGenerator.generateNormals(geoinfo);

		Shape3D shape = StdDraw3D.createShape3D(geoinfo.getIndexedGeometryArray());

		if (filled)
		{
			return StdDraw3D.shape(shape);
		}
		else
		{
			return StdDraw3D.wireShape(shape);
		}
	}

	/**
	 * Draws a Java 3D Primitive object at (x, y, z) with axial rotations (xA, yA, zA).
	 */
	private static Shape primitive(final Primitive shape, final double x, final double y, final double z, final Vector3d angles, final Vector3d scales)
	{

		shape.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
		shape.setPickable(false);
		shape.setCollidable(false);

		TransformGroup tgScale = StdDraw3D.createTransformGroup();
		Transform3D scaleTransform = new Transform3D();
		if (scales != null)
		{
			scaleTransform.setScale(scales);
		}
		tgScale.setTransform(scaleTransform);
		tgScale.addChild(shape);

		TransformGroup tgShape = StdDraw3D.createTransformGroup();
		Transform3D transform = new Transform3D();
		if (angles != null)
		{
			angles.scale(Math.PI / 180);
			transform.setEuler(angles);
		}
		Vector3f vector = StdDraw3D.createVector3f(x, y, z);
		transform.setTranslation(vector);
		tgShape.setTransform(transform);
		tgShape.addChild(tgScale);

		BranchGroup bg = StdDraw3D.createBranchGroup();
		bg.addChild(tgShape);
		StdDraw3D.offscreenGroup.addChild(bg);

		return new Shape(bg, tgShape);
	}

	private static void quitAction()
	{

		WindowEvent wev = new WindowEvent(StdDraw3D.frame, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

		StdDraw3D.keysDown.remove(KeyEvent.VK_META);
		StdDraw3D.keysDown.remove(KeyEvent.VK_CONTROL);
		StdDraw3D.keysDown.remove(KeyEvent.VK_Q);
	}

	/**
	 * Returns a randomly generated color.
	 */
	public static Color randomColor()
	{
		return new Color(Mathematics.getRandomInteger(0, Integer.MAX_VALUE, false));
	}

	/**
	 * Returns a randomly generated normalized Vector3D.
	 */
	public static Vector3D randomDirection()
	{

		double theta = Math.random() * Math.PI * 2;
		double phi = Math.random() * Math.PI;
		return new Vector3D(Math.cos(theta) * Math.sin(phi), Math.sin(theta) * Math.sin(phi), Math.cos(phi));
	}

	/**
	 * Returns a randomly generated color on the rainbow.
	 */
	public static Color randomRainbowColor()
	{
		return Color.getHSBColor((float)Math.random(), 1.0f, 1.0f);
	}

	private static void render3D()
	{

		StdDraw3D.rootGroup.addChild(StdDraw3D.offscreenGroup);

		if (StdDraw3D.clear3D)
		{
			StdDraw3D.clear3D = false;
			StdDraw3D.rootGroup.removeChild(StdDraw3D.onscreenGroup);
			StdDraw3D.onscreenGroup = StdDraw3D.offscreenGroup;
		}
		else
		{
			Enumeration<?> children = StdDraw3D.offscreenGroup.getAllChildren();
			while (children.hasMoreElements())
			{
				Node child = (Node)children.nextElement();
				StdDraw3D.offscreenGroup.removeChild(child);
				StdDraw3D.onscreenGroup.addChild(child);
			}
		}

		StdDraw3D.offscreenGroup = StdDraw3D.createBranchGroup();
		// System.out.println("off = " + offscreenGroup.numChildren());
		// System.out.println("on  = " + onscreenGroup.numChildren());

		StdDraw3D.rootGroup.removeChild(StdDraw3D.offscreenGroup);

		// StdOut.println("showed once = true");
	}

	private static void renderOverlay()
	{
		if (StdDraw3D.clearOverlay)
		{
			StdDraw3D.clearOverlay = false;
			StdDraw3D.onscreenImage = StdDraw3D.offscreenImage;
		}
		else
		{
			Graphics2D graphics = (Graphics2D)StdDraw3D.onscreenImage.getGraphics();
			graphics.drawRenderedImage(StdDraw3D.offscreenImage, new AffineTransform());
		}
		StdDraw3D.offscreenImage = StdDraw3D.createBufferedImage();
	}

	/**
	 * Saves to file - suffix must be png, jpg, or gif. gif??
	 *
	 * @param filename
	 *            The name of the file with one of the required suffixes.
	 */
	public static void save(final String filename)
	{

		// canvas.setVisible(false);
		int oldCameraMode = StdDraw3D.getCameraMode();
		StdDraw3D.setCameraMode(StdDraw3D.FIXED_MODE);

		GraphicsContext3D context = StdDraw3D.canvas.getGraphicsContext3D();

		BufferedImage buf = StdDraw3D.createBufferedImage();
		ImageComponent2D imageComp = new ImageComponent2D(ImageComponent.FORMAT_RGB, buf);
		javax.media.j3d.Raster ras = new javax.media.j3d.Raster(
			new Point3f(-1.0f, -1.0f, -1.0f),
			javax.media.j3d.Raster.RASTER_COLOR,
			0, 0,
			StdDraw3D.width, StdDraw3D.height,
			imageComp, null);

		context.readRaster(ras);

		BufferedImage image = (ras.getImage()).getImage();

		File file = new File(filename);
		String suffix = filename.substring(filename.lastIndexOf('.') + 1);

		String extension = suffix.toLowerCase();

		// png files
		if (extension.equals("png"))
		{
			try
			{
				ImageIO.write(image, suffix, file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		// need to change from ARGB to RGB for jpeg
		// reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
		else
			if (extension.equals("jpg"))
			{
				WritableRaster raster = image.getRaster();
				WritableRaster newRaster;
				newRaster = raster.createWritableChild(0, 0, StdDraw3D.width, StdDraw3D.height, 0, 0, new int[] {0, 1, 2});
				BufferedImage rgbBuffer = new BufferedImage(image.getColorModel(), newRaster, false, null);
				try
				{
					ImageIO.write(rgbBuffer, suffix, file);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Invalid image file type: " + suffix);
			}

		StdDraw3D.setCameraMode(oldCameraMode);
		// canvas.setVisible(true);
	}

	private static void save3DAction()
	{

		FileDialog chooser = new FileDialog(StdDraw3D.frame, "Save as a 3D file for loading later.", FileDialog.SAVE);
		chooser.setVisible(true);
		String filename = chooser.getFile();
		if (filename != null)
		{
			StdDraw3D.saveScene3D(chooser.getDirectory() + File.separator + chooser.getFile());
		}

		StdDraw3D.keysDown.remove(KeyEvent.VK_META);
		StdDraw3D.keysDown.remove(KeyEvent.VK_CONTROL);
		StdDraw3D.keysDown.remove(KeyEvent.VK_E);
	}

	// *********************************************************************************************

	private static void saveAction()
	{

		FileDialog chooser = new FileDialog(StdDraw3D.frame, "Use a .png or .jpg extension.", FileDialog.SAVE);
		chooser.setVisible(true);
		String filename = chooser.getFile();

		if (filename != null)
		{
			StdDraw3D.save(chooser.getDirectory() + File.separator + chooser.getFile());
		}

		StdDraw3D.keysDown.remove(KeyEvent.VK_META);
		StdDraw3D.keysDown.remove(KeyEvent.VK_CONTROL);
		StdDraw3D.keysDown.remove(KeyEvent.VK_S);
	}

	public static void saveScene3D(final String filename)
	{

		File file = new File(filename);
		// System.out.println("on: " + onscreenGroup.numChildren() + " off: " + offscreenGroup.numChildren());

		try
		{
			SceneGraphFileWriter writer = new SceneGraphFileWriter(file, StdDraw3D.universe, false, "3D scene saved from StdDraw3D.", null);
			writer.writeBranchGraph(StdDraw3D.offscreenGroup);
			writer.close();
			System.out.println("Scene successfully written to " + filename + "!");
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch (UnsupportedUniverseException uue)
		{
			uue.printStackTrace();
		}
		// catch (NamedObjectException noe) { noe.printStackTrace(); }
	}

	/**
	 * Scales the given x-coordinate from user coordinates into 2D pixel coordinates.
	 */
	private static float scaleX(final double x)
	{

		double scale = 1;
		if (StdDraw3D.width > StdDraw3D.height)
		{
			scale = 1 / StdDraw3D.aspectRatio;
		}

		return (float)((StdDraw3D.width * ((x * scale) - StdDraw3D.min)) / (2 * StdDraw3D.zoom));
	}

	/**
	 * Scales the given y-coordinate from user coordinates into 2D pixel coordinates.
	 */
	private static float scaleY(final double y)
	{

		double scale = 1;
		if (StdDraw3D.height > StdDraw3D.width)
		{
			scale = StdDraw3D.aspectRatio;
		}

		return (float)((StdDraw3D.height * (StdDraw3D.max - (y * scale))) / (2 * StdDraw3D.zoom));
	}

	/**
	 * Sets anti-aliasing on or off. Anti-aliasing makes graphics
	 * look much smoother, but is very resource heavy. It is good
	 * for saving images that look professional. The default is off.
	 *
	 * @param enabled
	 *            The state of anti-aliasing.
	 */
	public static void setAntiAliasing(final boolean enabled)
	{
		StdDraw3D.view.setSceneAntialiasingEnable(enabled);
		StdDraw3D.antiAliasingButton.setSelected(enabled);
		// System.out.println("Anti aliasing enabled: " + enabled);
	}

	/**
	 * Sets the background color to the given color.
	 *
	 * @param color
	 *            The color to set the background as.
	 */
	public static void setBackground(final Color color)
	{

		if (!color.equals(StdDraw3D.bgColor))
		{
			StdDraw3D.bgColor = color;

			StdDraw3D.rootGroup.removeChild(StdDraw3D.bgGroup);
			StdDraw3D.bgGroup.removeChild(StdDraw3D.background);

			StdDraw3D.background = StdDraw3D.createBackground();
			StdDraw3D.background.setColor(new Color3f(StdDraw3D.bgColor));

			StdDraw3D.bgGroup.addChild(StdDraw3D.background);
			StdDraw3D.rootGroup.addChild(StdDraw3D.bgGroup);
		}
	}

	// *********************************************************************************************

	/**
	 * Sets the background image to the given filename, scaled to fit the window.
	 *
	 * @param imageURL
	 *            The filename for the background image.
	 */
	public static void setBackground(final String imageURL)
	{

		StdDraw3D.rootGroup.removeChild(StdDraw3D.bgGroup);
		StdDraw3D.bgGroup.removeChild(StdDraw3D.background);

		StdDraw3D.background = StdDraw3D.createBackground();

		BufferedImage bi = null;
		try
		{
			bi = ImageIO.read(new File(imageURL));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		if (bi == null)
		{
			try
			{
				ImageIO.read(new URL(imageURL));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		ImageComponent2D imageComp = new ImageComponent2D(ImageComponent.FORMAT_RGB, bi);
		StdDraw3D.background.setImage(imageComp);
		StdDraw3D.background.setImageScaleMode(Background.SCALE_FIT_ALL);

		StdDraw3D.bgGroup.addChild(StdDraw3D.background);
		StdDraw3D.rootGroup.addChild(StdDraw3D.bgGroup);
	}

	/**
	 * Sets the background to the given image file. The file gets
	 * wrapped around as a spherical skybox.
	 *
	 * @param imageURL
	 *            The background image to use.
	 */
	public static void setBackgroundSphere(final String imageURL)
	{

		Sphere sphere = new Sphere(1.1f, Primitive.GENERATE_NORMALS
			| Primitive.GENERATE_NORMALS_INWARD
			| Primitive.GENERATE_TEXTURE_COORDS, StdDraw3D.numDivisions);

		Appearance ap = sphere.getAppearance();

		Texture texture = StdDraw3D.createTexture(imageURL);

		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.REPLACE);

		ap.setTexture(texture);
		ap.setTextureAttributes(texAttr);

		sphere.setAppearance(ap);

		BranchGroup backGeoBranch = StdDraw3D.createBranchGroup();
		backGeoBranch.addChild(sphere);

		StdDraw3D.rootGroup.removeChild(StdDraw3D.bgGroup);
		StdDraw3D.bgGroup.removeChild(StdDraw3D.background);

		StdDraw3D.background = StdDraw3D.createBackground();
		StdDraw3D.background.setGeometry(backGeoBranch);

		StdDraw3D.bgGroup.addChild(StdDraw3D.background);
		StdDraw3D.rootGroup.addChild(StdDraw3D.bgGroup);

	}

	public static void setCamera(final double x, final double y, final double z, final double xAngle, final double yAngle, final double zAngle)
	{
		StdDraw3D.camera.setPosition(x, y, z);
		StdDraw3D.camera.setOrientation(xAngle, yAngle, zAngle);
	}

	public static void setCamera(final Vector3D position, final Vector3D angles)
	{
		StdDraw3D.camera.setPosition(position);
		StdDraw3D.camera.setOrientation(angles);
	}

	public static void setCameraDirection(final double x, final double y, final double z)
	{
		StdDraw3D.setCameraDirection(new Vector3D(x, y, z));
	}

	public static void setCameraDirection(final Vector3D direction)
	{
		StdDraw3D.camera.setDirection(direction);
	}

	// *********************************************************************************************

	/**
	 * Sets the default camera mode.
	 */
	public static void setCameraMode()
	{
		StdDraw3D.setCameraMode(StdDraw3D.DEFAULT_CAMERA_MODE);
	}

	/**
	 * Sets the current camera mode. <br>
	 * -------------------------------------- <br>
	 * StdDraw3D.ORBIT_MODE: <br>
	 * Rotates and zooms around a central point. <br>
	 * <br>
	 * Mouse Click Left - Orbit <br>
	 * Mouse Click Right - Pan <br>
	 * Mouse Wheel / Alt-Click - Zoom <br>
	 * -------------------------------------- <br>
	 * StdDraw3D.FPS_MODE: <br>
	 * First-person-shooter style controls. <br>
	 * Up is always the positive y-axis. <br>
	 * <br>
	 * W/Up - Forward <br>
	 * S/Down - Backward <br>
	 * A/Left - Left <br>
	 * D/Right - Right <br>
	 * Q/Page Up - Up <br>
	 * E/Page Down - Down <br>
	 * Mouse - Look <br>
	 * -------------------------------------- <br>
	 * StdDraw3D.AIRPLANE_MODE: <br>
	 * Similar to fps_mode, but can be oriented <br>
	 * with up in any direction. <br>
	 * <br>
	 * W/Up - Forward <br>
	 * S/Down - Backward <br>
	 * A/Left - Left <br>
	 * D/Right - Right <br>
	 * Q/Page Up - Rotate CCW <br>
	 * E/Page Down - Rotate CW <br>
	 * Mouse - Look <br>
	 * -------------------------------------- <br>
	 * StdDraw3D.LOOK_MODE: <br>
	 * No movement, but uses mouse to look around. <br>
	 * <br>
	 * Mouse - Look <br>
	 * -------------------------------------- <br>
	 * StdDraw3D.FIXED_MODE: <br>
	 * No movement or looking. <br>
	 * -------------------------------------- <br>
	 *
	 * @param mode
	 *            The camera mode.
	 */
	public static void setCameraMode(final int mode)
	{

		StdDraw3D.cameraMode = mode;

		if (StdDraw3D.cameraMode == StdDraw3D.ORBIT_MODE)
		{
			StdDraw3D.orbit.setRotateEnable(true);
			if (StdDraw3D.view.getProjectionPolicy() != View.PARALLEL_PROJECTION)
			{
				StdDraw3D.orbit.setZoomEnable(true);
			}
			StdDraw3D.orbit.setTranslateEnable(true);
			StdDraw3D.orbit.setRotationCenter(StdDraw3D.orbitCenter);
			StdDraw3D.orbitModeButton.setSelected(true);
		}
		else
		{
			StdDraw3D.orbit.setRotateEnable(false);
			StdDraw3D.orbit.setZoomEnable(false);
			StdDraw3D.orbit.setTranslateEnable(false);
		}
		if (StdDraw3D.cameraMode == StdDraw3D.FPS_MODE)
		{
			StdDraw3D.fpsModeButton.setSelected(true);
			StdDraw3D.camera.rotateFPS(0, 0, 0);
		}
		if (StdDraw3D.cameraMode == StdDraw3D.AIRPLANE_MODE)
		{
			StdDraw3D.airplaneModeButton.setSelected(true);
		}
		if (StdDraw3D.cameraMode == StdDraw3D.LOOK_MODE)
		{
			// System.out.println("Camera in look mode.");
			StdDraw3D.lookModeButton.setSelected(true);
		}
		if (StdDraw3D.cameraMode == StdDraw3D.FIXED_MODE)
		{
			// System.out.println("Camera in fixed mode.");
			StdDraw3D.fixedModeButton.setSelected(true);
		}
		if (StdDraw3D.cameraMode == StdDraw3D.IMMERSIVE_MODE)
		{

			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new java.awt.Point(0, 0), "blank cursor");
			StdDraw3D.frame.getContentPane().setCursor(blankCursor);
			// System.out.println("Camera in fps mode.");
		}
		else
		{
			StdDraw3D.frame.getContentPane().setCursor(Cursor.getDefaultCursor());
		}
	}

	public static void setCameraOrientation(final double xAngle, final double yAngle, final double zAngle)
	{
		StdDraw3D.setCameraOrientation(new Vector3D(xAngle, yAngle, zAngle));
	}

	public static void setCameraOrientation(final Vector3D angles)
	{
		StdDraw3D.camera.setOrientation(angles);
	}

	public static void setCameraPosition(final double x, final double y, final double z)
	{
		StdDraw3D.setCameraPosition(new Vector3D(x, y, z));
	}

	public static void setCameraPosition(final Vector3D position)
	{
		StdDraw3D.camera.setPosition(position);
	}

	// *********************************************************************************************

	/**
	 * Sets the window size to w-by-h pixels.
	 *
	 * @param w
	 *            The width as a number of pixels.
	 * @param h
	 *            The height as a number of pixels.
	 * @throws a
	 *             RuntimeException if the width or height is 0 or negative.
	 */
	public static void setCanvasSize(final int w, final int h)
	{

		StdDraw3D.setCanvasSize(w, h, false);
	}

	/* ***************************************************************
	 * Non-Primitive Drawing Methods *
	 * ***************************************************************
	 */

	private static void setCanvasSize(final int w, final int h, final boolean fs)
	{

		StdDraw3D.fullscreen = fs;

		if ((w < 1) || (h < 1))
		{
			throw new RuntimeException("Dimensions must be positive integers!");
		}
		StdDraw3D.width = w;
		StdDraw3D.height = h;

		StdDraw3D.aspectRatio = (double)StdDraw3D.width / (double)StdDraw3D.height;
		StdDraw3D.initialize();
	}

	/**
	 * Adds the default lighting to the scene, called automatically at startup.
	 */
	public static void setDefaultLight()
	{
		StdDraw3D.clearLight();
		StdDraw3D.directionalLight(-4f, 7f, 12f, StdDraw3D.LIGHT_GRAY);
		StdDraw3D.directionalLight(4f, -7f, -12f, StdDraw3D.WHITE);
		StdDraw3D.ambientLight(new Color(0.1f, 0.1f, 0.1f));
	}

	/**
	 * Sets the default font.
	 */
	public static void setFont()
	{
		StdDraw3D.font = StdDraw3D.DEFAULT_FONT;
	}

	// *********************************************************************************************

	/**
	 * Sets the font to draw text with.
	 *
	 * @param f
	 *            The font to set.
	 */
	public static void setFont(final Font f)
	{
		StdDraw3D.font = f;
	}

	public static void setInfoDisplay(final boolean enabled)
	{
		StdDraw3D.infoDisplay = enabled;
		StdDraw3D.infoCheckBox.setSelected(enabled);
		StdDraw3D.camera.move(0, 0, 0);
		StdDraw3D.infoDisplay();
	}

	/**
	 * Sets the number of triangular divisons of curved objects.
	 * The default is 100 divisons. Decrease this to increase performance.
	 *
	 * @param N
	 *            The number of divisions.
	 */
	public static void setNumDivisions(final int N)
	{
		StdDraw3D.numDivisions = N;
	}

	/**
	 * Sets the center of rotation for the camera mode ORBIT_MODE.
	 */
	public static void setOrbitCenter(final double x, final double y, final double z)
	{
		StdDraw3D.setOrbitCenter(new Point3d(x, y, z));
	}

	private static void setOrbitCenter(final Point3d center)
	{
		StdDraw3D.orbitCenter = center;
		StdDraw3D.orbit.setRotationCenter(StdDraw3D.orbitCenter);
	}

	/**
	 * Sets the center of rotation for the camera mode ORBIT_MODE.
	 */
	public static void setOrbitCenter(final Vector3D v)
	{
		StdDraw3D.setOrbitCenter(new Point3d(v.x, v.y, v.z));
	}

	// *********************************************************************************************

	/**
	 * Sets the projection mode to orthographic projection.
	 * In this mode, parallel lines remain parallel after
	 * projection, and there is no perspective. It is as
	 * looking from infinitely far away with a telescope.
	 * AutoCAD programs use this projection mode.
	 */
	public static void setParallelProjection()
	{

		if (StdDraw3D.view.getProjectionPolicy() == View.PARALLEL_PROJECTION)
		{
			return;
		}
		StdDraw3D.view.setProjectionPolicy(View.PARALLEL_PROJECTION);
		StdDraw3D.orbit.setZoomEnable(false);
		StdDraw3D.parallelButton.setSelected(true);

		StdDraw3D.setScreenScale(0.3 / StdDraw3D.zoom);
	}

	/**
	 * Sets the pen color to the default.
	 */
	public static void setPenColor()
	{
		StdDraw3D.penColor = StdDraw3D.DEFAULT_PEN_COLOR;
	}

	/**
	 * Sets the pen color to the given color.
	 *
	 * @param col
	 *            The given color.
	 */
	public static void setPenColor(final Color col)
	{
		StdDraw3D.penColor = col;
	}

	// *********************************************************************************************

	/**
	 * Sets the pen color to the given color and transparency.
	 *
	 * @param col
	 *            The given opaque color.
	 * @param alpha
	 *            The transparency value (0-255).
	 */
	public static void setPenColor(final Color col, final int alpha)
	{
		StdDraw3D.setPenColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha));
	}

	public static void setPenColor(final int r, final int g, final int b)
	{
		StdDraw3D.penColor = new Color(r, g, b);
	}

	/**
	 * Sets the pen radius to the default value.
	 */
	public static void setPenRadius()
	{
		StdDraw3D.setPenRadius(StdDraw3D.DEFAULT_PEN_RADIUS);
	}

	/**
	 * Sets the pen radius to the given value.
	 *
	 * @param r
	 *            The pen radius.
	 */
	public static void setPenRadius(final double r)
	{
		StdDraw3D.penRadius = (float)r * 500;
	}

	/**
	 * Sets the projection mode to perspective projection with a
	 * default field of view. In this mode, closer objects appear
	 * larger, as in real life.
	 */
	public static void setPerspectiveProjection()
	{
		StdDraw3D.setPerspectiveProjection(StdDraw3D.DEFAULT_FOV);
	}

	/**
	 * Sets the projection mode to perspective projection with the
	 * given field of view. In this mode, closer objects appear
	 * larger, as in real life. A larger field of view means that
	 * there is more perspective distortion. Reasonable values are
	 * between 0.5 and 3.0.
	 *
	 * @param fov
	 *            The field of view to use. Try [0.5-3.0].
	 */
	public static void setPerspectiveProjection(final double fov)
	{

		StdDraw3D.view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		StdDraw3D.view.setWindowEyepointPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);
		StdDraw3D.view.setFieldOfView(fov);
		StdDraw3D.setScreenScale(1);
		StdDraw3D.orbit.setZoomEnable(true);
		StdDraw3D.perspectiveButton.setSelected(true);
		if ((Double)StdDraw3D.fovSpinner.getValue() != fov)
		{
			StdDraw3D.fovSpinner.setValue(fov);
			// if (view.getProjectionPolicy() == View.PERSPECTIVE_PROJECTION) return;
		}
	}

	// *********************************************************************************************

	/**
	 * Sets the default scale for all three dimensions.
	 */
	public static void setScale()
	{
		StdDraw3D.setScale(StdDraw3D.DEFAULT_MIN, StdDraw3D.DEFAULT_MAX);
	}

	/**
	 * Sets the scale for all three dimensions.
	 *
	 * @param minimum
	 *            The minimum value of each scale.
	 * @param maximum
	 *            The maximum value of each scale.
	 */
	public static void setScale(final double minimum, final double maximum)
	{

		StdDraw3D.min = minimum;
		StdDraw3D.max = maximum;
		StdDraw3D.zoom = (StdDraw3D.max - StdDraw3D.min) / 2;
		double center = StdDraw3D.min + StdDraw3D.zoom;

		// double nominalDist = camera.getPosition().z;
		StdDraw3D.camera.setPosition(center, center, StdDraw3D.zoom * (2 + Math.sqrt(2)));

		double orbitScale = 0.5 * StdDraw3D.zoom;

		StdDraw3D.orbit.setZoomFactor(orbitScale);
		StdDraw3D.orbit.setTransFactors(orbitScale, orbitScale);

		StdDraw3D.setOrbitCenter(new Point3d(center, center, center));

		StdDraw3D.view.setFrontClipDistance(StdDraw3D.DEFAULT_FRONT_CLIP * StdDraw3D.zoom);
		StdDraw3D.view.setBackClipDistance(StdDraw3D.DEFAULT_BACK_CLIP * StdDraw3D.zoom);
	}

	private static void setScreenScale(final double scale)
	{
		double ratio = scale / StdDraw3D.view.getScreenScale();
		StdDraw3D.view.setScreenScale(scale);
		StdDraw3D.view.setFrontClipDistance(StdDraw3D.view.getFrontClipDistance() * ratio);
		StdDraw3D.view.setBackClipDistance(StdDraw3D.view.getBackClipDistance() * ratio);
	}

	/**
	 * Draws a Java 3D Shape3D object and fills it in.
	 *
	 * @param shape
	 *            The Shape3D object to be drawn.
	 */
	private static Shape shape(final Shape3D shape)
	{
		return StdDraw3D.shape(shape, true, null, false);
	}

	/**
	 * Draws a Java3D Shape3D object with the given properties.
	 *
	 * @param polygonFill
	 *            Polygon fill properties, specified by Java 3D.
	 */
	private static Shape shape(final Shape3D shape, final boolean fill, final Transform3D transform, final boolean custom)
	{

		Appearance ap;

		if (custom)
		{
			ap = StdDraw3D.createCustomAppearance(fill);
		}
		else
		{
			ap = StdDraw3D.createAppearance(null, fill);
		}

		shape.setAppearance(ap);

		TransformGroup transGroup = new TransformGroup();
		if (transform != null)
		{
			transGroup.setTransform(transform);
		}

		transGroup.addChild(shape);

		BranchGroup bg = StdDraw3D.createBranchGroup();
		bg.addChild(transGroup);
		StdDraw3D.offscreenGroup.addChild(bg);
		return new Shape(bg, transGroup);
	}

	/**
	 * Renders to the screen, but does not pause.
	 */
	public static void show()
	{
		StdDraw3D.show(0);
	}

	/**
	 * Renders drawn 3D objects to the screen and draws the
	 * 2D overlay, then pauses for the given time.
	 *
	 * @param time
	 *            The number of milliseconds to pause for.
	 */
	public static void show(final int time)
	{

		StdDraw3D.renderOverlay();
		StdDraw3D.render3D();
		StdDraw3D.pause(time);
	}

	public static void show3D()
	{
		StdDraw3D.show3D(0);
	}

	/**
	 * Renders the drawn 3D objects to the screen, but not the overlay.
	 */
	public static void show3D(final int time)
	{

		StdDraw3D.render3D();
		StdDraw3D.pause(time);
	}

	public static void showOverlay()
	{
		StdDraw3D.showOverlay(0);
	}

	/**
	 * Displays the drawn overlay onto the screen.
	 */
	public static void showOverlay(final int time)
	{

		StdDraw3D.renderOverlay();
		StdDraw3D.pause(time);
	}

	/**
	 * Draws a sphere at (x, y, z) with radius r.
	 */
	public static Shape sphere(final double x, final double y, final double z, final double r)
	{
		return StdDraw3D.sphere(x, y, z, r, 0, 0, 0, null);
	}

	/**
	 * Draws a sphere at (x, y, z) with radius r and axial rotations (xA, yA, zA).
	 */
	public static Shape sphere(final double x, final double y, final double z, final double r, final double xA, final double yA, final double zA)
	{
		return StdDraw3D.sphere(x, y, z, r, xA, yA, zA, null);
	}

	/**
	 * Draws a sphere at (x, y, z) with radius r, axial rotations (xA, yA, zA), and a texture from imageURL.
	 */
	public static Shape sphere(
		final double x,
		final double y,
		final double z,
		final double r,
		final double xA,
		final double yA,
		final double zA,
		final String imageURL)
	{

		Vector3f dimensions = StdDraw3D.createVector3f(0, 0, r);
		Sphere sphere = new Sphere(dimensions.z, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions);
		sphere.setAppearance(StdDraw3D.createAppearance(imageURL, true));
		return StdDraw3D.primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	/* ***************************************************************
	 * 2D Overlay Drawing Methods *
	 * ***************************************************************
	 */

	/**
	 * Draws a sphere at (x, y, z) with radius r and a texture from imageURL.
	 */
	public static Shape sphere(final double x, final double y, final double z, final double r, final String imageURL)
	{
		return StdDraw3D.sphere(x, y, z, r, 0, 0, 0, imageURL);
	}

	/**
	 * Draws a 3D text object at (x, y, z). Uses the pen font, color, and transparency.
	 */
	public static Shape text3D(final double x, final double y, final double z, final String text)
	{

		return StdDraw3D.text3D(x, y, z, text, 0, 0, 0);
	}

	/**
	 * Draws a 3D text object at (x, y, z) with Euler rotation angles (xA, yA, zA).
	 * Uses the pen font, color, and transparency.
	 */
	public static Shape text3D(final double x, final double y, final double z, final String text, final double xA, final double yA, final double zA)
	{

		Line2D.Double line = new Line2D.Double(0, 0, StdDraw3D.TEXT3D_DEPTH, 0);
		FontExtrusion extrudePath = new FontExtrusion(line);
		Font3D font3D = new Font3D(StdDraw3D.font, extrudePath);
		new Point3d(x, y, z);
		javax.media.j3d.Text3D t = new javax.media.j3d.Text3D(font3D, text, StdDraw3D.createPoint3f(x, y, z));

		// FIX THIS TO NOT HAVE SCALE INCLUDED
		Transform3D shrinker = new Transform3D();
		shrinker.setEuler(new Vector3d(xA, yA, zA));
		shrinker.setTranslation(new Vector3d(x, y, z));
		shrinker.setScale(StdDraw3D.TEXT3D_SHRINK_FACTOR);
		Shape3D shape = StdDraw3D.createShape3D(t);
		return StdDraw3D.shape(shape, true, shrinker, false);
	}

	/**
	 * Draws triangles which are defined by x1=points[i][0], y1=points[i][1], z1=points[i][2], x2=points[i][3], etc.
	 * All of the points will be the width and color specified by the setPenColor and setPenWidth methods.
	 *
	 * @param points
	 *            an array of the points to be connected. The first dimension is
	 *            unspecified, but the second dimension should be 9 (the 3-space coordinates of each vertex)
	 */
	public static Shape triangles(final double[][] points)
	{
		return StdDraw3D.triangles(points, true);
	}

	private static Shape triangles(final double[][] points, final boolean filled)
	{

		int size = points.length;
		Point3f[] coords = new Point3f[size * 3];

		for (int i = 0; i < size; i++)
		{
			coords[3 * i] = new Point3f(StdDraw3D.createVector3f(points[i][0], points[i][1], points[i][2]));
			coords[(3 * i) + 1] = new Point3f(StdDraw3D.createVector3f(points[i][3], points[i][4], points[i][5]));
			coords[(3 * i) + 2] = new Point3f(StdDraw3D.createVector3f(points[i][6], points[i][7], points[i][8]));
		}

		GeometryArray geom = new TriangleArray(size * 3, GeometryArray.COORDINATES);
		geom.setCoordinates(0, coords);

		GeometryInfo geoinfo = new GeometryInfo(geom);
		NormalGenerator normalGenerator = new NormalGenerator();
		normalGenerator.generateNormals(geoinfo);

		Shape3D shape = StdDraw3D.createShape3D(geoinfo.getIndexedGeometryArray());

		if (filled)
		{
			return StdDraw3D.shape(shape);
		}
		else
		{
			return StdDraw3D.wireShape(shape);
		}
	}

	/**
	 * Draws a set of triangles, each with the specified color. There should be one
	 * color for each triangle. This is much more efficient than drawing individual
	 * triangles.
	 */
	public static Shape triangles(final double[][] points, final Color[] colors)
	{
		return StdDraw3D.triangles(points, colors, true);
	}

	private static Shape triangles(final double[][] points, final Color[] colors, final boolean filled)
	{
		int size = points.length;
		Point3f[] coords = new Point3f[size * 3];

		for (int i = 0; i < size; i++)
		{
			coords[3 * i] = new Point3f(StdDraw3D.createVector3f(points[i][0], points[i][1], points[i][2]));
			coords[(3 * i) + 1] = new Point3f(StdDraw3D.createVector3f(points[i][3], points[i][4], points[i][5]));
			coords[(3 * i) + 2] = new Point3f(StdDraw3D.createVector3f(points[i][6], points[i][7], points[i][8]));
		}

		GeometryArray geom = new TriangleArray(size * 3, GeometryArray.COORDINATES | GeometryArray.COLOR_4);
		geom.setCoordinates(0, coords);

		for (int i = 0; i < colors.length; i++)
		{
			geom.setColor((3 * i) + 0, colors[i].getComponents(null));
			geom.setColor((3 * i) + 1, colors[i].getComponents(null));
			geom.setColor((3 * i) + 2, colors[i].getComponents(null));
		}

		GeometryInfo geoinfo = new GeometryInfo(geom);
		NormalGenerator normalGenerator = new NormalGenerator();
		normalGenerator.generateNormals(geoinfo);

		Shape3D shape = StdDraw3D.createShape3D(geoinfo.getIndexedGeometryArray());

		if (filled)
		{
			return StdDraw3D.shape(shape);
		}
		else
		{
			return StdDraw3D.wireShape(shape);
		}
	}

	/**
	 * Draws a cylindrical tube of radius r from vertex (x1, y1, z1) to vertex (x2, y2, z2).
	 */
	public static Shape tube(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final double r)
	{

		Vector3D mid = new Vector3D(x1 + x2, y1 + y2, z1 + z2).times(0.5);
		Vector3D line = new Vector3D(x2 - x1, y2 - y1, z2 - z1);

		Shape s = StdDraw3D.cylinder(mid.x, mid.y, mid.z, r, line.mag());

		Vector3D yAxis = new Vector3D(0, 1, 0);
		Vector3D cross = line.cross(yAxis);
		double angle = line.angle(yAxis);
		s.rotateAxis(cross, -angle);

		return StdDraw3D.combine(s);
	}

	/**
	 * Draws a series of cylindrical tubes of radius r, with vertices (x, y, z).
	 */
	public static Shape tubes(final double[] x, final double[] y, final double[] z, final double r)
	{

		StdDraw3D.Shape[] shapes = new StdDraw3D.Shape[(x.length - 1) * 2];

		for (int i = 0; i < (x.length - 1); i++)
		{
			shapes[i] = StdDraw3D.tube(x[i], y[i], z[i], x[i + 1], y[i + 1], z[i + 1], r);
			shapes[(i + x.length) - 1] = StdDraw3D.sphere(x[i + 1], y[i + 1], z[i + 1], r);
		}

		return StdDraw3D.combine(shapes);
	}

	/**
	 * Draws a series of cylindrical tubes of radius r, with vertices (x, y, z) and the given colors.
	 * No more efficient than drawing individual tubes.
	 */
	public static Shape tubes(final double[] x, final double[] y, final double[] z, final double r, final Color[] colors)
	{

		StdDraw3D.Shape[] shapes = new StdDraw3D.Shape[(x.length - 1) * 2];

		for (int i = 0; i < (x.length - 1); i++)
		{
			StdDraw3D.setPenColor(colors[i]);
			shapes[i] = StdDraw3D.tube(x[i], y[i], z[i], x[i + 1], y[i + 1], z[i + 1], r);
			shapes[(i + x.length) - 1] = StdDraw3D.sphere(x[i + 1], y[i + 1], z[i + 1], r);
		}

		return StdDraw3D.combine(shapes);
	}

	/**
	 * Scales the given x-coordinate from 2D pixel coordinates into user coordinates.
	 */
	private static double unscaleX(final double xs)
	{

		double scale = 1;
		if (StdDraw3D.width > StdDraw3D.height)
		{
			scale = 1 / StdDraw3D.aspectRatio;
		}

		return (((xs * (2 * StdDraw3D.zoom)) / StdDraw3D.width) + StdDraw3D.min) / scale;
	}

	/**
	 * Scales the given y-coordinate from 2D pixel coordinates into user coordinates.
	 */
	private static double unscaleY(final double ys)
	{

		double scale = 1;
		if (StdDraw3D.height > StdDraw3D.width)
		{
			scale = StdDraw3D.aspectRatio;
		}
		// System.out.println("unscaleY scale = " + scale);
		return (StdDraw3D.max - ((ys * (2 * StdDraw3D.zoom)) / StdDraw3D.height)) / scale;
	}

	/**
	 * Draws a wireframe box at (x, y, z) with dimensions (w, h, d).
	 */
	public static Shape wireBox(final double x, final double y, final double z, final double w, final double h, final double d)
	{
		return StdDraw3D.wireBox(x, y, z, w, h, d, 0, 0, 0);
	}

	/**
	 * Draws a wireframe box at (x, y, z) with dimensions (w, h, d) and axial rotations (xA, yA, zA).
	 */
	public static Shape wireBox(
		final double x,
		final double y,
		final double z,
		final double w,
		final double h,
		final double d,
		final double xA,
		final double yA,
		final double zA)
	{

		Appearance ap = StdDraw3D.createAppearance(null, false);

		Vector3f dimensions = StdDraw3D.createVector3f(w, h, d);

		com.sun.j3d.utils.geometry.Box box = new
			com.sun.j3d.utils.geometry.Box(dimensions.x, dimensions.y, dimensions.z, StdDraw3D.PRIMFLAGS, ap, StdDraw3D.numDivisions);
		return StdDraw3D.primitive(box, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	/**
	 * Draws a wireframe cone at (x, y, z) with radius r and height h.
	 */
	public static Shape wireCone(final double x, final double y, final double z, final double r, final double h)
	{
		return StdDraw3D.wireCone(x, y, z, r, h, 0, 0, 0);
	}

	/**
	 * Draws a wireframe cone at (x, y, z) with radius r, height h, and axial rotations (xA, yA, zA).
	 */
	public static Shape wireCone(
		final double x,
		final double y,
		final double z,
		final double r,
		final double h,
		final double xA,
		final double yA,
		final double zA)
	{

		Appearance ap = StdDraw3D.createAppearance(null, false);
		Vector3f dimensions = StdDraw3D.createVector3f(r, h, 0);
		Cone cone = new Cone(dimensions.x, dimensions.y, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions, StdDraw3D.numDivisions, ap);
		return StdDraw3D.primitive(cone, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	/**
	 * Draws a wireframe cube at (x, y, z) with radius r.
	 */
	public static Shape wireCube(final double x, final double y, final double z, final double r)
	{

		double[] xC = new double[]
			{x + r, x + r, x - r, x - r, x + r, x + r, x + r, x - r, x - r, x + r, x + r, x + r, x - r, x - r, x - r, x - r};
		double[] yC = new double[]
			{y + r, y - r, y - r, y + r, y + r, y + r, y - r, y - r, y + r, y + r, y - r, y - r, y - r, y - r, y + r, y + r};
		double[] zC = new double[]
			{z + r, z + r, z + r, z + r, z + r, z - r, z - r, z - r, z - r, z - r, z - r, z + r, z + r, z - r, z - r, z + r};

		return StdDraw3D.lines(xC, yC, zC);
	}

	/**
	 * Draws a wireframe cube at (x, y, z) with radius r and axial rotations (xA, yA, zA).
	 */
	public static Shape wireCube(final double x, final double y, final double z, final double r, final double xA, final double yA, final double zA)
	{

		return StdDraw3D.wireBox(x, y, z, r, r, r, 0, 0, 0);
	}

	/**
	 * Draws a wireframe cylinder at (x, y, z) with radius r and height h.
	 */
	public static Shape wireCylinder(final double x, final double y, final double z, final double r, final double h)
	{
		return StdDraw3D.wireCylinder(x, y, z, r, h, 0, 0, 0);
	}

	/**
	 * Draws a wireframe cylinder at (x, y, z) with radius r, height h, and axial rotations (xA, yA, zA).
	 */
	public static Shape wireCylinder(
		final double x,
		final double y,
		final double z,
		final double r,
		final double h,
		final double xA,
		final double yA,
		final double zA)
	{

		Appearance ap = StdDraw3D.createAppearance(null, false);
		Vector3f dimensions = StdDraw3D.createVector3f(r, h, 0);
		Cylinder cyl = new Cylinder(dimensions.x, dimensions.y, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions, StdDraw3D.numDivisions, ap);
		return StdDraw3D.primitive(cyl, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	/**
	 * Draws a wireframe ellipsoid at (x, y, z) with dimensions (w, h, d).
	 */
	public static Shape wireEllipsoid(final double x, final double y, final double z, final double w, final double h, final double d)
	{
		return StdDraw3D.wireEllipsoid(x, y, z, w, h, d, 0, 0, 0);
	}

	/**
	 * Draws a wireframe ellipsoid at (x, y, z) with dimensions (w, h, d) and axial rotations (xA, yA, zA).
	 */
	public static Shape wireEllipsoid(
		final double x,
		final double y,
		final double z,
		final double w,
		final double h,
		final double d,
		final double xA,
		final double yA,
		final double zA)
	{

		Sphere sphere = new Sphere(1, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions);
		sphere.setAppearance(StdDraw3D.createAppearance(null, false));
		return StdDraw3D.primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), new Vector3d(w, h, d));
	}

	/**
	 * Draws the triangulated outline of a polygon with the given vertices.
	 */
	public static Shape wirePolygon(final double[] x, final double[] y, final double[] z)
	{
		return StdDraw3D.polygon(x, y, z, false);
	}

	/**
	 * Draws a wireframe Java 3D Shape3D object and fills it in.
	 *
	 * @param shape
	 *            The Shape3D object to be drawn.
	 */
	private static Shape wireShape(final Shape3D shape)
	{
		return StdDraw3D.shape(shape, false, null, false);
	}

	/**
	 * Draws a wireframe sphere at (x, y, z) with radius r.
	 */
	public static Shape wireSphere(final double x, final double y, final double z, final double r)
	{
		return StdDraw3D.wireSphere(x, y, z, r, 0, 0, 0);
	}

	/**
	 * Draws a wireframe sphere at (x, y, z) with radius r and axial rotations (xA, yA, zA).
	 */
	public static Shape wireSphere(final double x, final double y, final double z, final double r, final double xA, final double yA, final double zA)
	{

		Vector3f dimensions = StdDraw3D.createVector3f(0, 0, r);
		Sphere sphere = new Sphere(dimensions.z, StdDraw3D.PRIMFLAGS, StdDraw3D.numDivisions);
		sphere.setAppearance(StdDraw3D.createAppearance(null, false));
		return StdDraw3D.primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), null);
	}

	public static Shape wireTriangles(final double[][] points)
	{
		return StdDraw3D.triangles(points, false);
	}

	/**
	 * Draws a set of wireframetriangles, each with the specified color. There should be one
	 * color for each triangle. This is much more efficient than drawing individual
	 * triangles.
	 */
	public static Shape wireTriangles(final double[][] points, final Color[] colors)
	{
		return StdDraw3D.triangles(points, colors, false);
	}

	/* ***************************************************************
	 * Shape Methods *
	 * ***************************************************************
	 */

	// Blank constructor.
	private StdDraw3D()
	{
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		Object source = e.getSource();

		if (source == StdDraw3D.saveButton)
		{
			StdDraw3D.saveAction();
		}
		else
			if (source == StdDraw3D.loadButton)
			{
				StdDraw3D.loadAction();
			}
			else
				if (source == StdDraw3D.save3DButton)
				{
					StdDraw3D.save3DAction();
				}
				else
					if (source == StdDraw3D.quitButton)
					{
						StdDraw3D.quitAction();
					}
					else
						if (source == StdDraw3D.orbitModeButton)
						{
							StdDraw3D.setCameraMode(StdDraw3D.ORBIT_MODE);
						}
						else
							if (source == StdDraw3D.fpsModeButton)
							{
								StdDraw3D.setCameraMode(StdDraw3D.FPS_MODE);
							}
							else
								if (source == StdDraw3D.airplaneModeButton)
								{
									StdDraw3D.setCameraMode(StdDraw3D.AIRPLANE_MODE);
								}
								else
									if (source == StdDraw3D.lookModeButton)
									{
										StdDraw3D.setCameraMode(StdDraw3D.LOOK_MODE);
									}
									else
										if (source == StdDraw3D.fixedModeButton)
										{
											StdDraw3D.setCameraMode(StdDraw3D.FIXED_MODE);
										}
										else
											if (source == StdDraw3D.perspectiveButton)
											{
												StdDraw3D.setPerspectiveProjection((Double)StdDraw3D.fovSpinner.getValue());
											}
											else
												if (source == StdDraw3D.parallelButton)
												{
													StdDraw3D.setParallelProjection();
												}
												else
													if (source == StdDraw3D.antiAliasingButton)
													{
														StdDraw3D.setAntiAliasing(StdDraw3D.antiAliasingButton.isSelected());
													}
													else
														if (source == StdDraw3D.infoCheckBox)
														{
															StdDraw3D.setInfoDisplay(StdDraw3D.infoCheckBox.isSelected());
														}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void componentHidden(final ComponentEvent e)
	{
		StdDraw3D.keysDown = new TreeSet<Integer>();
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void componentMoved(final ComponentEvent e)
	{
		StdDraw3D.keysDown = new TreeSet<Integer>();
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void componentResized(final ComponentEvent e)
	{
		StdDraw3D.keysDown = new TreeSet<Integer>();
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void componentShown(final ComponentEvent e)
	{
		StdDraw3D.keysDown = new TreeSet<Integer>();
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void keyPressed(final KeyEvent e)
	{
		synchronized (StdDraw3D.keyLock)
		{
			StdDraw3D.keysDown.add(e.getKeyCode());
			// System.out.println((int)e.getKeyCode() + " pressed");
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void keyReleased(final KeyEvent e)
	{
		synchronized (StdDraw3D.keyLock)
		{
			StdDraw3D.keysDown.remove(e.getKeyCode());
			// System.out.println((int)e.getKeyCode() + " released");
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void keyTyped(final KeyEvent e)
	{
		synchronized (StdDraw3D.keyLock)
		{

			char c = e.getKeyChar();
			StdDraw3D.keysTyped.addFirst(c);

			if (c == '`')
			{
				StdDraw3D.setCameraMode((StdDraw3D.getCameraMode() + 1) % 5);
			}
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mouseClicked(final MouseEvent e)
	{
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mouseDragged(final MouseEvent e)
	{
		synchronized (StdDraw3D.mouseLock)
		{

			StdDraw3D.mouseMotionEvents(e, e.getX(), e.getY(), true);
			StdDraw3D.mouseX = e.getX();
			StdDraw3D.mouseY = e.getY();
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mouseEntered(final MouseEvent e)
	{
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mouseExited(final MouseEvent e)
	{

	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mouseMoved(final MouseEvent e)
	{
		synchronized (StdDraw3D.mouseLock)
		{

			// System.out.println(e.getX() + " " + e.getY());
			StdDraw3D.mouseMotionEvents(e, e.getX(), e.getY(), false);
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mousePressed(final MouseEvent e)
	{
		synchronized (StdDraw3D.mouseLock)
		{
			StdDraw3D.mouseX = e.getX();
			StdDraw3D.mouseY = e.getY();
			if (e.getButton() == 1)
			{
				StdDraw3D.mouse1 = true;
			}
			if (e.getButton() == 2)
			{
				StdDraw3D.mouse2 = true;
			}
			if (e.getButton() == 3)
			{
				StdDraw3D.mouse3 = true;
				// System.out.println("Mouse button = " + e.getButton());
			}
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mouseReleased(final MouseEvent e)
	{
		synchronized (StdDraw3D.mouseLock)
		{
			if (e.getButton() == 1)
			{
				StdDraw3D.mouse1 = false;
			}
			if (e.getButton() == 2)
			{
				StdDraw3D.mouse2 = false;
			}
			if (e.getButton() == 3)
			{
				StdDraw3D.mouse3 = false;
			}
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void mouseWheelMoved(final MouseWheelEvent e)
	{

		double notches = e.getWheelRotation();
		// System.out.println(notches);
		if ((StdDraw3D.cameraMode == StdDraw3D.ORBIT_MODE) && (StdDraw3D.view.getProjectionPolicy() == View.PARALLEL_PROJECTION))
		{
			StdDraw3D.camera.moveRelative(0, 0, (notches * StdDraw3D.zoom) / 20);
		}
	}

	@Override
	public void stateChanged(final ChangeEvent e)
	{
		Object source = e.getSource();

		if (source == StdDraw3D.numDivSpinner)
		{
			StdDraw3D.numDivisions = (Integer)StdDraw3D.numDivSpinner.getValue();
		}
		if (source == StdDraw3D.fovSpinner)
		{
			StdDraw3D.setPerspectiveProjection((Double)StdDraw3D.fovSpinner.getValue());
			StdDraw3D.perspectiveButton.setSelected(true);
		}
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void windowGainedFocus(final WindowEvent e)
	{
		StdDraw3D.keysDown = new TreeSet<Integer>();
	}

	/**
	 * This method cannot be called directly.
	 *
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void windowLostFocus(final WindowEvent e)
	{
		StdDraw3D.keysDown = new TreeSet<Integer>();
	}
}