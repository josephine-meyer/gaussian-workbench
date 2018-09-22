/**
 * Class that represents a lens.
 */

public class Lens implements OpticsHardware {

	/*
	 * Constructor that makes a lens at a specific focal length and position.
	 */
	public Lens(double position, double focalLength, String name) {
		this.focalLength = focalLength;
		this.position = position;
		this.name = name;
	}
	
	/*
	 * Sets or changes the position of the lens.
	 */
	public void setPosition(double position) {
		this.position = position;
	}
	
	/*
	 * Sets or changes the focal length of the lens.
	 */
	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}
	
	
	/*
	 * Returns the current position of the lens.
	 */
	public double getPosition() {
		return position;
	}
	
	/*
	 * Returns the current focal length.
	 */
	public double getFocalLength() {
		return focalLength;
	}
	
	/*
	 * Returns whether the lens is tunable. Returns false.
	 * Overridden in class TunableLens.
	 */
	public boolean isTunable() {
		return false;
	}
	
	/*
	 * Returns ABCD matrix for the lens.
	 */
	public ABCDMatrix ABCD() {
		return ABCDMatrix.lensMatrix(focalLength);
	}
	
	/*
	 * Returns a string describing the lens.
	 */
	public String toString() {
		return "Position: " + position + ", focal length: " + (focalLength);
	}
	
	/*
	 * Returns whether the lens is selected.
	 */
	public boolean isSelected() {
		return selected;
	}
	

	/*
	 * Returns a string indicating the type of the object.
	 */
	public String getType() {
		return "Lens";
	}
	
	/*
	 * Returns the name of the lens.
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Sets the name of the optic.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * Sets whether the optic is selected.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
	/*
	 * Compares the position of two optics. Does not work with .equals()!
	 */
	public int compareTo(OpticsHardware o) {
		if (position == o.getPosition()) return 0;
		if (position > o.getPosition()) return 1;
		return -1;
	}
	
	
	/* Private instance variables */
	protected volatile double position;
	protected volatile double focalLength;
	protected boolean selected = false;
	protected String name;
	
}
