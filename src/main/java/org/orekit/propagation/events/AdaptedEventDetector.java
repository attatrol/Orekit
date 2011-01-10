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
package org.orekit.propagation.events;

import java.io.Serializable;

import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.StateMapper;
import org.orekit.time.AbsoluteDate;

/** Adapt an {@link org.orekit.propagation.events.EventDetector}
 * to commons-math {@link org.apache.commons.math.ode.events.EventHandler} interface.
 * @author Fabien Maussion
 * @version $Revision$ $Date$
 */
public class AdaptedEventDetector implements EventHandler, Serializable {

    /** Serializable UID. */
    private static final long serialVersionUID = 5270333983406326215L;

    /** Mapper between spacecraft state and simple array. */
    private final StateMapper mapper;

    /** Underlying event detector. */
    private final EventDetector detector;

    /** Occurred event observer. */
    private final EventObserver observer;

    /** Reference date from which t is counted. */
    private final AbsoluteDate referenceDate;

    /** Central body attraction coefficient (m<sup>3</sup>/s<sup>2</sup>). */
    private final double mu;

    /** integrationFrame frame in which integration is performed. */
    private final Frame integrationFrame;

    /** Build a wrapped event detector.
     * @param detector event detector to wrap
     * @param observer occurred event observer
     * @param mapper mapper between spacecraft state and simple array
     * @param referenceDate reference date from which t is counted
     * @param mu central body attraction coefficient (m<sup>3</sup>/s<sup>2</sup>)
     * @param integrationFrame frame in which integration is performed
     */
    public AdaptedEventDetector(final EventDetector detector, final EventObserver observer,
                                final StateMapper mapper, final AbsoluteDate referenceDate,
                                final double mu, final Frame integrationFrame) {
        this.detector         = detector;
        this.observer         = observer;
        this.mapper           = mapper;
        this.referenceDate    = referenceDate;
        this.mu               = mu;
        this.integrationFrame = integrationFrame;
    }

    /** {@inheritDoc} */
    public double g(final double t, final double[] y)
        throws EventException {
        try {
            final AbsoluteDate currentDate = referenceDate.shiftedBy(t);
            return detector.g(mapper.mapArrayToState(y, currentDate, mu, integrationFrame));
        } catch (OrekitException oe) {
            throw new EventException(oe);
        }
    }

    /** {@inheritDoc} */
    public int eventOccurred(final double t, final double[] y, final boolean increasing)
        throws EventException {
        try {

            final AbsoluteDate currentDate = referenceDate.shiftedBy(t);
            final SpacecraftState state = mapper.mapArrayToState(y, currentDate, mu, integrationFrame);
            final int whatNext = detector.eventOccurred(state, increasing);
            observer.notify(state, detector);

            switch (whatNext) {
            case EventDetector.STOP :
                return STOP;
            case EventDetector.RESET_STATE :
                return RESET_STATE;
            case EventDetector.RESET_DERIVATIVES :
                return RESET_DERIVATIVES;
            default :
                return CONTINUE;
            }
        } catch (OrekitException oe) {
            throw new EventException(oe);
        }
    }

    /** {@inheritDoc} */
    public void resetState(final double t, final double[] y)
        throws EventException {
        try {
            final AbsoluteDate currentDate = referenceDate.shiftedBy(t);
            final SpacecraftState oldState = mapper.mapArrayToState(y, currentDate, mu, integrationFrame);
            final SpacecraftState newState = detector.resetState(oldState);
            mapper.mapStateToArray(newState, y);
        } catch (OrekitException oe) {
            throw new EventException(oe);
        }
    }

}
