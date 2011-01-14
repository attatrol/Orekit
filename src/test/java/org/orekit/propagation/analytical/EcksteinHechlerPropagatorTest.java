/* Copyright 2002-2011 CS Communication & Systèmes
 * Licensed to CS Communication & Systèmes (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orekit.propagation.analytical;


import java.util.Collection;

import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.orekit.Utils;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.EquinoctialOrbit;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.events.DateDetector;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.NodeDetector;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.PVCoordinatesProvider;


public class EcksteinHechlerPropagatorTest {

    @Test
    public void sameDateCartesian() throws OrekitException {

        // Definition of initial conditions with position and velocity
        // ------------------------------------------------------------
        // with e around e = 1.4e-4 and i = 1.7 rad
        Vector3D position = new Vector3D(3220103., 69623., 6449822.);
        Vector3D velocity = new Vector3D(6414.7, -2006., -3180.);

        AbsoluteDate initDate = AbsoluteDate.J2000_EPOCH.shiftedBy(584.);
        Orbit initialOrbit = new EquinoctialOrbit(new PVCoordinates(position, velocity),
                                                  FramesFactory.getEME2000(), initDate, mu);

        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at the initial date
        // ---------------------------------
        double delta_t = 0.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);

        SpacecraftState finalOrbit = extrapolator.propagate(extrapDate);

        Assert.assertEquals(finalOrbit.getDate().durationFrom(extrapDate), 0.0, Utils.epsilonTest);
        Assert.assertEquals(finalOrbit.getA(), initialOrbit.getA(), Utils.epsilonTest
                     * initialOrbit.getA());
        Assert.assertEquals(finalOrbit.getEquinoctialEx(), initialOrbit.getEquinoctialEx(), Utils.epsilonE
                     * initialOrbit.getE());
        Assert.assertEquals(finalOrbit.getEquinoctialEy(), initialOrbit.getEquinoctialEy(), Utils.epsilonE
                     * initialOrbit.getE());
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getHx(), initialOrbit.getHx()),
                     initialOrbit.getHx(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getI()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getHy(), initialOrbit.getHy()),
                     initialOrbit.getHy(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getI()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getLv(), initialOrbit.getLv()),
                     initialOrbit.getLv(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getLv()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getLM(), initialOrbit.getLM()),
                     initialOrbit.getLM(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getLM()));

    }

    @Test
    public void sameDateKeplerian() throws OrekitException {

        // Definition of initial conditions with keplerian parameters
        // -----------------------------------------------------------
        AbsoluteDate initDate = AbsoluteDate.J2000_EPOCH.shiftedBy(584.);
        Orbit initialOrbit = new KeplerianOrbit(7209668.0, 0.5e-4, 1.7, 2.1, 2.9,
                                                6.2, KeplerianOrbit.TRUE_ANOMALY, 
                                                FramesFactory.getEME2000(), initDate, mu);
 
        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at the initial date
        // ---------------------------------
        double delta_t = 0.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);

        SpacecraftState finalOrbit = extrapolator.propagate(extrapDate);

        Assert.assertEquals(finalOrbit.getDate().durationFrom(extrapDate), 0.0, Utils.epsilonTest);
        Assert.assertEquals(finalOrbit.getA(), initialOrbit.getA(), Utils.epsilonTest
                     * initialOrbit.getA());
        Assert.assertEquals(finalOrbit.getEquinoctialEx(), initialOrbit.getEquinoctialEx(), Utils.epsilonE
                     * initialOrbit.getE());
        Assert.assertEquals(finalOrbit.getEquinoctialEy(), initialOrbit.getEquinoctialEy(), Utils.epsilonE
                     * initialOrbit.getE());
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getHx(), initialOrbit.getHx()),
                     initialOrbit.getHx(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getI()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getHy(), initialOrbit.getHy()),
                     initialOrbit.getHy(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getI()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getLv(), initialOrbit.getLv()),
                     initialOrbit.getLv(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getLv()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getLE(), initialOrbit.getLE()),
                     initialOrbit.getLE(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getLE()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbit.getLM(), initialOrbit.getLM()),
                     initialOrbit.getLM(), Utils.epsilonAngle
                     * FastMath.abs(initialOrbit.getLM()));

    }

    @Test
    public void almostSphericalBody() throws OrekitException {

        // Definition of initial conditions
        // ---------------------------------
        // with e around e = 1.4e-4 and i = 1.7 rad
        Vector3D position = new Vector3D(3220103., 69623., 6449822.);
        Vector3D velocity = new Vector3D(6414.7, -2006., -3180.);

        AbsoluteDate initDate = AbsoluteDate.J2000_EPOCH.shiftedBy(584.);
        Orbit initialOrbit = new EquinoctialOrbit(new PVCoordinates(position, velocity),
                                                  FramesFactory.getEME2000(), initDate, mu);

        // Initialisation to simulate a keplerian extrapolation
        // To be noticed: in order to simulate a keplerian extrapolation with the
        // analytical
        // extrapolator, one should put the zonal coefficients to 0. But due to
        // numerical pbs
        // one must put a non 0 value.
        double zc20 = 0.1e-10;
        double zc30 = 0.1e-13;
        double zc40 = 0.1e-13;
        double zc50 = 0.1e-14;
        double zc60 = 0.1e-14;

        // Extrapolators definitions
        // -------------------------
        EcksteinHechlerPropagator extrapolatorAna =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, zc20, zc30, zc40, zc50, zc60);
        KeplerianPropagator extrapolatorKep = new KeplerianPropagator(initialOrbit);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = 100.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);

        SpacecraftState finalOrbitAna = extrapolatorAna.propagate(extrapDate);
        SpacecraftState finalOrbitKep = extrapolatorKep.propagate(extrapDate);

        Assert.assertEquals(finalOrbitAna.getDate().durationFrom(extrapDate), 0.0,
                     Utils.epsilonTest);
        // comparison of each orbital parameters
        Assert.assertEquals(finalOrbitAna.getA(), finalOrbitKep.getA(), 10
                     * Utils.epsilonTest * finalOrbitKep.getA());
        Assert.assertEquals(finalOrbitAna.getEquinoctialEx(), finalOrbitKep.getEquinoctialEx(), Utils.epsilonE
                     * finalOrbitKep.getE());
        Assert.assertEquals(finalOrbitAna.getEquinoctialEy(), finalOrbitKep.getEquinoctialEy(), Utils.epsilonE
                     * finalOrbitKep.getE());
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getHx(), finalOrbitKep.getHx()),
                     finalOrbitKep.getHx(), Utils.epsilonAngle
                     * FastMath.abs(finalOrbitKep.getI()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getHy(), finalOrbitKep.getHy()),
                     finalOrbitKep.getHy(), Utils.epsilonAngle
                     * FastMath.abs(finalOrbitKep.getI()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getLv(), finalOrbitKep.getLv()),
                     finalOrbitKep.getLv(), Utils.epsilonAngle
                     * FastMath.abs(finalOrbitKep.getLv()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getLE(), finalOrbitKep.getLE()),
                     finalOrbitKep.getLE(), Utils.epsilonAngle
                     * FastMath.abs(finalOrbitKep.getLE()));
        Assert.assertEquals(MathUtils.normalizeAngle(finalOrbitAna.getLM(), finalOrbitKep.getLM()),
                     finalOrbitKep.getLM(), Utils.epsilonAngle
                     * FastMath.abs(finalOrbitKep.getLM()));

    }

    @Test
    public void propagatedCartesian() throws OrekitException {
        // Definition of initial conditions with position and velocity
        // ------------------------------------------------------------
        // with e around e = 1.4e-4 and i = 1.7 rad
        Vector3D position = new Vector3D(3220103., 69623., 6449822.);
        Vector3D velocity = new Vector3D(6414.7, -2006., -3180.);

        AbsoluteDate initDate = AbsoluteDate.J2000_EPOCH.shiftedBy(584.);
        Orbit initialOrbit = new EquinoctialOrbit(new PVCoordinates(position, velocity),
                                                  FramesFactory.getEME2000(), initDate, mu);

        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = 100000.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);

        SpacecraftState finalOrbit = extrapolator.propagate(extrapDate);

        Assert.assertEquals(0.0, finalOrbit.getDate().durationFrom(extrapDate), 1.0e-9);

        // computation of M final orbit
        double LM = finalOrbit.getLE() - finalOrbit.getEquinoctialEx()
        * FastMath.sin(finalOrbit.getLE()) + finalOrbit.getEquinoctialEy()
        * FastMath.cos(finalOrbit.getLE());

        Assert.assertEquals(LM, finalOrbit.getLM(), Utils.epsilonAngle
                     * FastMath.abs(finalOrbit.getLM()));

        // test of tan ((LE - Lv)/2) :
        Assert.assertEquals(FastMath.tan((finalOrbit.getLE() - finalOrbit.getLv()) / 2.),
                     tangLEmLv(finalOrbit.getLv(), finalOrbit.getEquinoctialEx(), finalOrbit
                               .getEquinoctialEy()), Utils.epsilonAngle);

        // test of evolution of M vs E: LM = LE - ex*sin(LE) + ey*cos(LE)
        double deltaM = finalOrbit.getLM() - initialOrbit.getLM();
        double deltaE = finalOrbit.getLE() - initialOrbit.getLE();
        double delta = finalOrbit.getEquinoctialEx() * FastMath.sin(finalOrbit.getLE())
        - initialOrbit.getEquinoctialEx() * FastMath.sin(initialOrbit.getLE())
        - finalOrbit.getEquinoctialEy() * FastMath.cos(finalOrbit.getLE())
        + initialOrbit.getEquinoctialEy() * FastMath.cos(initialOrbit.getLE());

        Assert.assertEquals(deltaM, deltaE - delta, Utils.epsilonAngle
                     * FastMath.abs(deltaE - delta));

        // for final orbit
        double ex = finalOrbit.getEquinoctialEx();
        double ey = finalOrbit.getEquinoctialEy();
        double hx = finalOrbit.getHx();
        double hy = finalOrbit.getHy();
        double LE = finalOrbit.getLE();

        double ex2 = ex * ex;
        double ey2 = ey * ey;
        double hx2 = hx * hx;
        double hy2 = hy * hy;
        double h2p1 = 1. + hx2 + hy2;
        double beta = 1. / (1. + FastMath.sqrt(1. - ex2 - ey2));

        double x3 = -ex + (1. - beta * ey2) * FastMath.cos(LE) + beta * ex * ey
        * FastMath.sin(LE);
        double y3 = -ey + (1. - beta * ex2) * FastMath.sin(LE) + beta * ex * ey
        * FastMath.cos(LE);

        Vector3D U = new Vector3D((1. + hx2 - hy2) / h2p1, (2. * hx * hy) / h2p1,
                                  (-2. * hy) / h2p1);

        Vector3D V = new Vector3D((2. * hx * hy) / h2p1, (1. - hx2 + hy2) / h2p1,
                                  (2. * hx) / h2p1);

        Vector3D r = new Vector3D(finalOrbit.getA(), (new Vector3D(x3, U, y3, V)));

        Assert.assertEquals(finalOrbit.getPVCoordinates().getPosition().getNorm(), r.getNorm(),
                     Utils.epsilonTest * r.getNorm());

    }

    @Test
    public void propagatedKeplerian() throws OrekitException {
        // Definition of initial conditions with keplerian parameters
        // -----------------------------------------------------------
        AbsoluteDate initDate = AbsoluteDate.J2000_EPOCH.shiftedBy(584.);
        Orbit initialOrbit = new KeplerianOrbit(7209668.0, 0.5e-4, 1.7, 2.1, 2.9,
                                              6.2, KeplerianOrbit.TRUE_ANOMALY, 
                                              FramesFactory.getEME2000(), initDate, mu);

        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = 100000.0; // extrapolation duration in seconds
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);

        SpacecraftState finalOrbit = extrapolator.propagate(extrapDate);

        Assert.assertEquals(0.0, finalOrbit.getDate().durationFrom(extrapDate), 1.0e-9);

        // computation of M final orbit
        double LM = finalOrbit.getLE() - finalOrbit.getEquinoctialEx()
        * FastMath.sin(finalOrbit.getLE()) + finalOrbit.getEquinoctialEy()
        * FastMath.cos(finalOrbit.getLE());

        Assert.assertEquals(LM, finalOrbit.getLM(), Utils.epsilonAngle);

        // test of tan((LE - Lv)/2) :
        Assert.assertEquals(FastMath.tan((finalOrbit.getLE() - finalOrbit.getLv()) / 2.),
                     tangLEmLv(finalOrbit.getLv(), finalOrbit.getEquinoctialEx(), finalOrbit
                               .getEquinoctialEy()), Utils.epsilonAngle);

        // test of evolution of M vs E: LM = LE - ex*sin(LE) + ey*cos(LE)
        // with ex and ey the same for initial and final orbit
        double deltaM = finalOrbit.getLM() - initialOrbit.getLM();
        double deltaE = finalOrbit.getLE() - initialOrbit.getLE();
        double delta = finalOrbit.getEquinoctialEx() * FastMath.sin(finalOrbit.getLE())
        - initialOrbit.getEquinoctialEx() * FastMath.sin(initialOrbit.getLE())
        - finalOrbit.getEquinoctialEy() * FastMath.cos(finalOrbit.getLE())
        + initialOrbit.getEquinoctialEy() * FastMath.cos(initialOrbit.getLE());

        Assert.assertEquals(deltaM, deltaE - delta, Utils.epsilonAngle
                     * FastMath.abs(deltaE - delta));

        // for final orbit
        double ex = finalOrbit.getEquinoctialEx();
        double ey = finalOrbit.getEquinoctialEy();
        double hx = finalOrbit.getHx();
        double hy = finalOrbit.getHy();
        double LE = finalOrbit.getLE();

        double ex2 = ex * ex;
        double ey2 = ey * ey;
        double hx2 = hx * hx;
        double hy2 = hy * hy;
        double h2p1 = 1. + hx2 + hy2;
        double beta = 1. / (1. + FastMath.sqrt(1. - ex2 - ey2));

        double x3 = -ex + (1. - beta * ey2) * FastMath.cos(LE) + beta * ex * ey
        * FastMath.sin(LE);
        double y3 = -ey + (1. - beta * ex2) * FastMath.sin(LE) + beta * ex * ey
        * FastMath.cos(LE);

        Vector3D U = new Vector3D((1. + hx2 - hy2) / h2p1, (2. * hx * hy) / h2p1,
                                  (-2. * hy) / h2p1);

        Vector3D V = new Vector3D((2. * hx * hy) / h2p1, (1. - hx2 + hy2) / h2p1,
                                  (2. * hx) / h2p1);

        Vector3D r = new Vector3D(finalOrbit.getA(), (new Vector3D(x3, U, y3, V)));

        Assert.assertEquals(finalOrbit.getPVCoordinates().getPosition().getNorm(), r.getNorm(),
                     Utils.epsilonTest * r.getNorm());

    }

    @Test
    public void propagatedEquinoctial() throws OrekitException {

        // Comparison with a given extrapolated orbit
        // -----------------------------------------
        AbsoluteDate initDate = AbsoluteDate.FIFTIES_EPOCH.shiftedBy(12584. * Constants.JULIAN_DAY);

        double a = 7200000.;
        double exp = .9848e-4; // e * cos(pom)
        double eyp = .17367e-4; // e * sin(pom)
        double i = 1.710423;
        double gom = 1.919862;
        double pso_M = 0.5236193; // M + pom

        double e = FastMath.sqrt(exp * exp + eyp * eyp);
        double pom = FastMath.atan2(eyp, exp);
        double ex = e * FastMath.cos(pom + gom);
        double ey = e * FastMath.sin(pom + gom);
        Orbit initialOrbit = new EquinoctialOrbit(a, ex, ey,
                                                FastMath.tan(i / 2) * FastMath.cos(gom),
                                                FastMath.tan(i / 2) * FastMath.sin(gom),
                                                pso_M + gom,
                                                EquinoctialOrbit.MEAN_LATITUDE_ARGUMENT, 
                                                FramesFactory.getEME2000(), initDate, mu);
        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at a final date different from initial date
        // ---------------------------------------------------------
        double delta_t = (12587. - 12584.) * Constants.JULIAN_DAY; // extrapolation duration in
        // seconds
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);

        SpacecraftState finalOrbit = extrapolator.propagate(extrapDate);
        // the final orbit

        a = 7187990.1979844316;
        exp = 7.766165990293499E-4; // e * cos(pom)
        eyp = 1.054283074113609E-3; // e * sin(pom)
        i = 1.7105407051081795;
        gom = 1.9674147913622104;
        pso_M = 4.42298640282359; // M + pom

        e = FastMath.sqrt(exp * exp + eyp * eyp);
        pom = FastMath.atan2(eyp, exp);
        ex = e * FastMath.cos(pom + gom);
        ey = e * FastMath.sin(pom + gom);
        Assert.assertEquals(0.0, finalOrbit.getDate().durationFrom(extrapDate), Utils.epsilonTest * delta_t);
        Assert.assertEquals(finalOrbit.getA(), a, 10. * Utils.epsilonTest * finalOrbit.getA());
        Assert.assertEquals(finalOrbit.getEquinoctialEx(), ex, Utils.epsilonE * finalOrbit.getE());
        Assert.assertEquals(finalOrbit.getEquinoctialEy(), ey, Utils.epsilonE * finalOrbit.getE());
        Assert.assertEquals(finalOrbit.getHx(), FastMath.tan(i / 2.) * FastMath.cos(gom),
                     Utils.epsilonAngle * FastMath.abs(finalOrbit.getHx()));
        Assert.assertEquals(finalOrbit.getHy(), FastMath.tan(i / 2.) * FastMath.sin(gom),
                     Utils.epsilonAngle * FastMath.abs(finalOrbit.getHy()));
        Assert.assertEquals(finalOrbit.getLM(), pso_M + gom, Utils.epsilonAngle
                     * FastMath.abs(finalOrbit.getLM()));

    }

    @Test(expected = PropagationException.class)
    public void undergroundOrbit() throws PropagationException {

        // for a semi major axis < equatorial radius
        Vector3D position = new Vector3D(7.0e6, 1.0e6, 4.0e6);
        Vector3D velocity = new Vector3D(-500.0, 800.0, 100.0);
        AbsoluteDate initDate = AbsoluteDate.J2000_EPOCH;
        Orbit initialOrbit = new EquinoctialOrbit(new PVCoordinates(position, velocity),
                                                  FramesFactory.getEME2000(), initDate, mu);
        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at the initial date
        // ---------------------------------
        double delta_t = 0.0;
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);
        extrapolator.propagate(extrapDate);
    }

    @Test(expected = PropagationException.class)
    public void tooEllipticalOrbit() throws PropagationException {
        // for an eccentricity too big for the model
        Vector3D position = new Vector3D(7.0e6, 1.0e6, 4.0e6);
        Vector3D velocity = new Vector3D(-500.0, 8000.0, 1000.0);
        AbsoluteDate initDate = AbsoluteDate.J2000_EPOCH;
        Orbit initialOrbit = new EquinoctialOrbit(new PVCoordinates(position, velocity),
                                                  FramesFactory.getEME2000(), initDate, mu);
        // Extrapolator definition
        // -----------------------
        EcksteinHechlerPropagator extrapolator =
            new EcksteinHechlerPropagator(initialOrbit,
                                          ae, mu, c20, c30, c40, c50, c60);

        // Extrapolation at the initial date
        // ---------------------------------
        double delta_t = 0.0;
        AbsoluteDate extrapDate = initDate.shiftedBy(delta_t);
        extrapolator.propagate(extrapDate);
    }

    @Test(expected = PropagationException.class)
    public void hyperbolic() throws PropagationException {
        KeplerianOrbit hyperbolic =
            new KeplerianOrbit(-1.0e10, 2, 0, 0, 0, 0, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, 3.986004415e14);
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(hyperbolic, ae, mu, c20, c30, c40, c50, c60);
        propagator.propagate(AbsoluteDate.J2000_EPOCH.shiftedBy(10.0));
    }

    @Test(expected = PropagationException.class)
    public void wrongAttitude() throws PropagationException {
        KeplerianOrbit orbit =
            new KeplerianOrbit(1.0e10, 1.0e-4, 1.0e-2, 0, 0, 0, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, 3.986004415e14);
        AttitudeProvider wrongLaw = new AttitudeProvider() {
            private static final long serialVersionUID = 5918362126173997016L;
            public Attitude getAttitude(PVCoordinatesProvider pvProv, AbsoluteDate date, Frame frame) throws OrekitException {
                throw new OrekitException(new DummyLocalizable("gasp"), new RuntimeException());
            }
        };
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(orbit, wrongLaw,
                                          ae, mu, c20, c30, c40, c50, c60);
        propagator.propagate(AbsoluteDate.J2000_EPOCH.shiftedBy(10.0));
    }

    @Test
    public void ascendingNode() throws OrekitException {
        final KeplerianOrbit orbit =
            new KeplerianOrbit(7.8e6, 0.032, 0.4, 0.1, 0.2, 0.3, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, mu);
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(orbit, ae, mu, c20, c30, c40, c50, c60);
        NodeDetector detector = new NodeDetector(orbit, FramesFactory.getITRF2005());
        Assert.assertTrue(FramesFactory.getITRF2005() == detector.getFrame());
        propagator.addEventDetector(detector);
        AbsoluteDate farTarget = AbsoluteDate.J2000_EPOCH.shiftedBy(10000.0);
        SpacecraftState propagated = propagator.propagate(farTarget);
        PVCoordinates pv = propagated.getPVCoordinates(FramesFactory.getITRF2005());
        Assert.assertTrue(farTarget.durationFrom(propagated.getDate()) > 3500.0);
        Assert.assertTrue(farTarget.durationFrom(propagated.getDate()) < 4000.0);
        Assert.assertEquals(0, pv.getPosition().getZ(), 1.0e-6);
        Assert.assertTrue(pv.getVelocity().getZ() > 0);
        Collection<EventDetector> detectors = propagator.getEventsDetectors();
        Assert.assertEquals(1, detectors.size());
        propagator.clearEventsDetectors();
        Assert.assertEquals(0, propagator.getEventsDetectors().size());
    }

    @Test
    public void stopAtTargetDate() throws OrekitException {
        final KeplerianOrbit orbit =
            new KeplerianOrbit(7.8e6, 0.032, 0.4, 0.1, 0.2, 0.3, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, 3.986004415e14);
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(orbit, ae, mu, c20, c30, c40, c50, c60);
        propagator.addEventDetector(new NodeDetector(orbit, FramesFactory.getITRF2005()) {
            private static final long serialVersionUID = 8805264185199866748L;
            public int eventOccurred(final SpacecraftState s, final boolean increasing) {
                return CONTINUE;
            }
        });
        AbsoluteDate farTarget = orbit.getDate().shiftedBy(10000.0);
        SpacecraftState propagated = propagator.propagate(farTarget);
        Assert.assertEquals(0.0, FastMath.abs(farTarget.durationFrom(propagated.getDate())), 1.0e-3);
    }

    @Test
    public void perigee() throws OrekitException {
        final KeplerianOrbit orbit =
            new KeplerianOrbit(7.8e6, 0.032, 0.4, 0.1, 0.2, 0.3, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, mu);
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(orbit, ae, mu, c20, c30, c40, c50, c60);
        propagator.addEventDetector(new ApsideDetector(orbit));
        AbsoluteDate farTarget = AbsoluteDate.J2000_EPOCH.shiftedBy(10000.0);
        SpacecraftState propagated = propagator.propagate(farTarget);
        PVCoordinates pv = propagated.getPVCoordinates(FramesFactory.getITRF2005());
        Assert.assertTrue(farTarget.durationFrom(propagated.getDate()) > 3000.0);
        Assert.assertTrue(farTarget.durationFrom(propagated.getDate()) < 3500.0);
        Assert.assertEquals(orbit.getA() * (1.0 - orbit.getE()), pv.getPosition().getNorm(), 400);
    }

    @Test
    public void date() throws OrekitException {
        final KeplerianOrbit orbit =
            new KeplerianOrbit(7.8e6, 0.032, 0.4, 0.1, 0.2, 0.3, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, 3.986004415e14);
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(orbit, ae, mu, c20, c30, c40, c50, c60);
        final AbsoluteDate stopDate = AbsoluteDate.J2000_EPOCH.shiftedBy(500.0);
        propagator.addEventDetector(new DateDetector(stopDate));
        AbsoluteDate farTarget = AbsoluteDate.J2000_EPOCH.shiftedBy(10000.0);
        SpacecraftState propagated = propagator.propagate(farTarget);
        Assert.assertEquals(0, stopDate.durationFrom(propagated.getDate()), 1.0e-10);
    }

    @Test
    public void fixedStep() throws OrekitException {
        final KeplerianOrbit orbit =
            new KeplerianOrbit(7.8e6, 0.032, 0.4, 0.1, 0.2, 0.3, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, 3.986004415e14);
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(orbit, ae, mu, c20, c30, c40, c50, c60);
        final double step = 100.0;
        propagator.setMasterMode(step, new OrekitFixedStepHandler() {
            private static final long serialVersionUID = 5343978335581094125L;
            private AbsoluteDate previous;
            public void handleStep(SpacecraftState currentState, boolean isLast)
            throws PropagationException {
                if (previous != null) {
                    Assert.assertEquals(step, currentState.getDate().durationFrom(previous), 1.0e-10);
                }
                previous = currentState.getDate();
            }
        });
        AbsoluteDate farTarget = AbsoluteDate.J2000_EPOCH.shiftedBy(10000.0);
        propagator.propagate(farTarget);
    }

    @Test
    public void setting() throws OrekitException {
        final KeplerianOrbit orbit =
            new KeplerianOrbit(7.8e6, 0.032, 0.4, 0.1, 0.2, 0.3, KeplerianOrbit.TRUE_ANOMALY,
                               FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH, 3.986004415e14);
        EcksteinHechlerPropagator propagator =
            new EcksteinHechlerPropagator(orbit, ae, mu, c20, c30, c40, c50, c60);
        final OneAxisEllipsoid earthShape =
            new OneAxisEllipsoid(6378136.460, 1 / 298.257222101, FramesFactory.getITRF2005());
        final TopocentricFrame topo =
            new TopocentricFrame(earthShape, new GeodeticPoint(0.389, -2.962, 0), null);
        ElevationDetector detector = new ElevationDetector(60, 0.09, topo);
        Assert.assertEquals(0.09, detector.getElevation(), 1.0e-12);
        Assert.assertTrue(topo == detector.getTopocentricFrame());
        propagator.addEventDetector(detector);
        AbsoluteDate farTarget = AbsoluteDate.J2000_EPOCH.shiftedBy(10000.0);
        SpacecraftState propagated = propagator.propagate(farTarget);
        final double elevation = topo.getElevation(propagated.getPVCoordinates().getPosition(),
                                                   propagated.getFrame(),
                                                   propagated.getDate());
        final double zVelocity = propagated.getPVCoordinates(topo).getVelocity().getZ();
        Assert.assertTrue(farTarget.durationFrom(propagated.getDate()) > 7800.0);
        Assert.assertTrue(farTarget.durationFrom(propagated.getDate()) < 7900.0);
        Assert.assertEquals(0.09, elevation, 1.0e-11);
        Assert.assertTrue(zVelocity < 0);
    }

    private static double tangLEmLv(double Lv, double ex, double ey) {
        // tan ((LE - Lv) /2)) =
        return (ey * FastMath.cos(Lv) - ex * FastMath.sin(Lv))
        / (1 + ex * FastMath.cos(Lv) + ey * FastMath.sin(Lv) + FastMath.sqrt(1 - ex * ex
                                                                 - ey * ey));
    }

    @Before
    public void setUp() {
        Utils.setDataRoot("regular-data");
        mu  = 3.9860047e14;
        ae  = 6.378137e6;
        c20 = -1.08263e-3;
        c30 = 2.54e-6;
        c40 = 1.62e-6;
        c50 = 2.3e-7;
        c60 = -5.5e-7;
    }

    @After
    public void tearDown() {
        mu   = Double.NaN;
        ae   = Double.NaN;
        c20  = Double.NaN;
        c30  = Double.NaN;
        c40  = Double.NaN;
        c50  = Double.NaN;
        c60  = Double.NaN;
    }

    private double mu;
    private double ae;
    private double c20;
    private double c30;
    private double c40;
    private double c50;
    private double c60;

}
