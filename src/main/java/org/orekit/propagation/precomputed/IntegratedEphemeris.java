/* Copyright 2002-2010 CS Communication & Systèmes
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
package org.orekit.propagation.precomputed;

import java.util.List;

import org.apache.commons.math.exception.MathUserException;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitMessages;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.AbstractPropagator;
import org.orekit.propagation.BoundedPropagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.AdditionalStateAndEquations;
import org.orekit.propagation.numerical.ModeHandler;
import org.orekit.propagation.numerical.StateMapper;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;

/** This class stores sequentially generated orbital parameters for
 * later retrieval.
 *
 * <p>
 * Instances of this class are built and then must be fed with the results
 * provided by {@link org.orekit.propagation.Propagator Propagator} objects
 * configured in {@link org.orekit.propagation.Propagator#setEphemerisMode()
 * ephemeris generation mode}. Once propagation is o, random access to any
 * intermediate state of the orbit throughout the propagation range is possible.
 * </p>
 * <p>
 * A typical use case is for numerically integrated orbits, which can be used by
 * algorithms that need to wander around according to their own algorithm without
 * cumbersome tight links with the integrator.
 * </p>
 * <p>
 * Another use case is for persistence, as this class is serializable.
 * </p>
 * <p>
 * As this class implements the {@link org.orekit.propagation.Propagator Propagator}
 * interface, it can itself be used in batch mode to build another instance of the
 * same type. This is however not recommended since it would be a waste of resources.
 * </p>
 * <p>
 * Note that this class stores all intermediate states along with interpolation
 * models, so it may be memory intensive.
 * </p>
 *
 * @see org.orekit.propagation.numerical.NumericalPropagator
 * @author Mathieu Rom&eacute;ro
 * @author Luc Maisonobe
 * @author V&eacute;ronique Pommier-Maurussane
 * @version $Revision:1698 $ $Date:2008-06-18 16:01:17 +0200 (mer., 18 juin 2008) $
 */
public class IntegratedEphemeris
    extends AbstractPropagator implements BoundedPropagator, ModeHandler, StepHandler {

    /** Serializable UID. */
    private static final long serialVersionUID = -3921028924201745331L;

    /** Mapper between spacecraft state and simple array. */
    private StateMapper mapper;

    /** Reference date. */
    private AbsoluteDate initializedReference;

    /** Frame. */
    private Frame initializedFrame;

    /** Central body gravitational constant. */
    private double initializedMu;

    /** Start date of the integration (can be min or max). */
    private AbsoluteDate startDate;

    /** First date of the range. */
    private AbsoluteDate minDate;

    /** Last date of the range. */
    private AbsoluteDate maxDate;

    /** Underlying raw mathematical model. */
    private ContinuousOutputModel model;

    /** Flag for handler . */
    private boolean activate;

    /** Creates a new instance of IntegratedEphemeris which must be
     *  filled by the propagator.
     */
    public IntegratedEphemeris() {
        super(DEFAULT_LAW);
        this.model = new ContinuousOutputModel();
    }

    /** {@inheritDoc} */
    public void initialize(final StateMapper stateMapper, final List <AdditionalStateAndEquations> addStateAndEqu,
                           final boolean activateHandlers, final AbsoluteDate reference,
                           final Frame frame, final double mu) {
        this.mapper               = stateMapper;
        this.activate             = activateHandlers;
        this.initializedReference = reference;
        this.initializedFrame     = frame;
        this.initializedMu        = mu;

        // dates will be set when last step is handled
        startDate        = null;
        minDate          = null;
        maxDate          = null;

    }

    /** {@inheritDoc} */
    protected SpacecraftState basicPropagate(final AbsoluteDate date)
        throws PropagationException {
        try {
            if ((date.compareTo(minDate) < 0) || (date.compareTo(maxDate) > 0)) {
                throw new PropagationException(OrekitMessages.OUT_OF_RANGE_EPHEMERIDES_DATE,
                                               date, minDate, maxDate);
            }
            model.setInterpolatedTime(date.durationFrom(startDate));
            return mapper.mapArrayToState(model.getInterpolatedState(), date,
                                          initializedMu, initializedFrame);
        } catch (MathUserException mue) {
            throw new PropagationException(mue, mue.getGeneralPattern(), mue.getArguments());
        } catch (OrekitException oe) {
            throw new PropagationException(oe);
        }
    }

    /** {@inheritDoc} */
    protected Orbit propagateOrbit(final AbsoluteDate date)
        throws PropagationException {
        return basicPropagate(date).getOrbit();
    }

    /** {@inheritDoc} */
    protected double getMass(final AbsoluteDate date) throws PropagationException {
        return basicPropagate(date).getMass();
    }

    /** {@inheritDoc} */
    public PVCoordinates getPVCoordinates(final AbsoluteDate date, final Frame frame)
        throws OrekitException {
        return propagate(date).getPVCoordinates(frame);
    }

    /** Get the first date of the range.
     * @return the first date of the range
     */
    public AbsoluteDate getMinDate() {
        return minDate;
    }

    /** Get the last date of the range.
     * @return the last date of the range
     */
    public AbsoluteDate getMaxDate() {
        return maxDate;
    }

    /** {@inheritDoc} */
    public void handleStep(final StepInterpolator interpolator, final boolean isLast) {
        if (activate) {
            model.handleStep(interpolator, isLast);
            if (isLast) {
                final double tI = model.getInitialTime();
                final double tF = model.getFinalTime();
                startDate = initializedReference.shiftedBy(tI);
                maxDate   = initializedReference.shiftedBy(tF);
                if (tF < tI) {
                    minDate = maxDate;
                    maxDate = startDate;
                } else {
                    minDate = startDate;
                }
            }
        }
    }

    /** {@inheritDoc} */
    public boolean requiresDenseOutput() {
        return model.requiresDenseOutput();
    }

    /** {@inheritDoc} */
    public void reset() {
        model.reset();
    }


    /** {@inheritDoc} */
    public void resetInitialState(final SpacecraftState state)
        throws PropagationException {
        throw new PropagationException(OrekitMessages.NON_RESETABLE_STATE);
    }

    /** {@inheritDoc} */
    public SpacecraftState getInitialState() throws OrekitException {
        return basicPropagate(getMinDate());
    }

}
