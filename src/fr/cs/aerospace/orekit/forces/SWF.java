package fr.cs.aerospace.orekit.forces;

import fr.cs.aerospace.orekit.errors.OrekitException;
import fr.cs.aerospace.orekit.frames.Frame;
import fr.cs.aerospace.orekit.time.AbsoluteDate;
import fr.cs.aerospace.orekit.utils.PVCoordinates;

/** This interface represents the switching function of the set of force 
 * models.
 *
 * <p>It should be implemented by all real force models before they
 * can be taken into account by the orbit extrapolation methods.</p>
 *
 * <p>Switching functions are a useful solution to meet the requirements of 
 * integrators concerning discontinuities problems.</p>
 *
 * @version $Id: SWF.java 1032 2006-09-28 08:25:21 +0000 (jeu., 28 sept. 2006) fabien $
 * @author E. Delente
 */


public interface SWF {

    /** Compute the value of the switching function. 
     */    
    public double g(AbsoluteDate t, PVCoordinates pvCoordinates, Frame frame)
      throws OrekitException;
    
    /** Handle an event and choose what to do next.
     */
    public void eventOccurred(AbsoluteDate t, PVCoordinates pvCoordinates, Frame frame);
    
    /** Get the convergence threshold in the event time search.
     */
    public double getThreshold();
    
    /** Get maximal time interval between switching function checks.
     */
    public double getMaxCheckInterval();
    
}