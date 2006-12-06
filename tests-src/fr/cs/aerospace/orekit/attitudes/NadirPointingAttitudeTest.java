package fr.cs.aerospace.orekit.attitudes;

import java.io.FileNotFoundException;
import java.text.ParseException;
import org.spaceroots.mantissa.geometry.Vector3D;
import fr.cs.aerospace.orekit.Utils;
import fr.cs.aerospace.orekit.bodies.BodyShape;
import fr.cs.aerospace.orekit.bodies.GeodeticPoint;
import fr.cs.aerospace.orekit.bodies.OneAxisEllipsoid;
import fr.cs.aerospace.orekit.errors.OrekitException;
import fr.cs.aerospace.orekit.frames.Frame;
import fr.cs.aerospace.orekit.models.attitudes.NadirPointingAttitude;
import fr.cs.aerospace.orekit.orbits.CircularParameters;
import fr.cs.aerospace.orekit.orbits.Orbit;
import fr.cs.aerospace.orekit.orbits.OrbitalParameters;
import fr.cs.aerospace.orekit.propagation.KeplerianPropagator;
import fr.cs.aerospace.orekit.propagation.SpacecraftState;
import fr.cs.aerospace.orekit.time.AbsoluteDate;
import fr.cs.aerospace.orekit.time.UTCScale;
import fr.cs.aerospace.orekit.utils.Line;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class NadirPointingAttitudeTest extends TestCase {
  
  public void testSimpleBehaviour() throws ParseException, OrekitException, FileNotFoundException {
    
    // parameters
    final double a = 12000000;
    final double ex = 1e-3;
    final double ey = 1e-3;
    final double OMEGA = 0;
    final double l = 0;
    final double i = Math.PI/2; 
    final double mu = Utils.mu;
    
    final OrbitalParameters op = new CircularParameters(a, ex, ey, i, OMEGA, l, 
                                                  CircularParameters.TRUE_LONGITUDE_ARGUMENT
                                                  , Frame.getJ2000());
    
    final AbsoluteDate initDate = new AbsoluteDate("2001-03-21T00:00:00",
                                         UTCScale.getInstance());
    
    final Orbit o = new Orbit(initDate, op);

    BodyShape earth = new OneAxisEllipsoid(6378136.460, 0.6);
    
    AttitudeKinematicsProvider att = 
      new NadirPointingAttitude(mu, earth);
    
    final SpacecraftState initState = new SpacecraftState(o, 1000, 
                                  att.getAttitudeKinematics(initDate, o.getPVCoordinates(mu), 
                                                            o.getFrame()));
    double period = 2*Math.PI*Math.sqrt(a*a*a/mu);
    
    KeplerianPropagator kep = new KeplerianPropagator(initState, mu);
    kep.setAkProvider(att);
    SpacecraftState medState = new SpacecraftState(o);
    AbsoluteDate medDate;
    
    for (int j=0 ; j<= period; j++) {
      medDate = new AbsoluteDate(initDate , j);
      medState = kep.getSpacecraftState(medDate);
      Vector3D pos = new Vector3D(medState.getPVCoordinates(mu).getPosition());
      Vector3D dir = medState.getAttitude().applyInverseTo(Vector3D.plusI);
      Line orto = new Line(pos , dir);
      GeodeticPoint geo = earth.transform(pos);
      geo = new GeodeticPoint(geo.longitude, geo.latitude, 0);
      assertEquals(0, orto.distance(earth.transform(geo)), 1e-7);
    }
  }
  
  public static Test suite() {
    return new TestSuite(NadirPointingAttitudeTest.class);
  }
  
}