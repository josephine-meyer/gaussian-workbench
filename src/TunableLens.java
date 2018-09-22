/*
 * Class that represents a tunable lens. Inherits from class Lens.
 */

public class TunableLens extends Lens {
	
	/*
	 * Constructor for a tunable lens. Start focal length is the focal length is focal length at
	 * which lens starts.
	 */
	public TunableLens(double position, double minFocalLength, double maxFocalLength, 
			double startFocalLength, String name) {
		super(position, startFocalLength, name);
		this.minFocalLength = minFocalLength;
		this.maxFocalLength = maxFocalLength;
	}
	
	/*
	 * Constructor for a tunable lens. Lens automatically starts halfway through its range
	 * of focal length.
	 */
	public TunableLens(double position, double minFocalLength, double maxFocalLength, 
			String name) {
		super(position, (minFocalLength + maxFocalLength) / 2.0, name);
		this.minFocalLength = minFocalLength;
		this.maxFocalLength = maxFocalLength;
	}
	
	/*
	 * Indicates whether the lens is tunable. Overrides method in Lens class to return true.
	 */
	public boolean isTunable() {
		return true;
	}
	
	/*
	 * Sets the focal length of the lens at minimum current.
	 */
	public void setMinFocalLength(double minFocalLength) {
		this.minFocalLength = minFocalLength;
	}
	
	/*
	 * Sets the focal length of the lens at maximum current.
	 */
	public void setMaxFocalLength(double maxFocalLength) {
		this.maxFocalLength = maxFocalLength;
	}
	
	/*
	 * Returns the focal length of the lens at minimum current.
	 */
	public double getMinFocalLength() {
		return minFocalLength;
	}
	
	/*
	 * Returns the type of the lens.
	 */
	public String getType() {
		return "Tunable Lens";
	}
	
	/*
	 * Returns the focal length of the lens at maximum current.
	 */
	public double getMaxFocalLength() {
		return maxFocalLength;
	}
	
	/*
	 * Sets the current focal length of the lens, if within range allowed by lens.
	 */
	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}
	
	
	//Instance variables. Note that it also carries instance variables from class Lens.

	private volatile double maxFocalLength;
	private volatile double minFocalLength;
}
