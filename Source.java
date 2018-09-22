/*
 * Class representing a laser source image.
 */


public class Source implements OpticsHardware {
	
	/*
	 * Constructs the laser source.
	 */
	public Source(double position) {
		this.position = position;
	}
	
	

	/*
	 * Returns the type of the source.
	 */
	public String getType() {
		return "Source";
	}

	/*
	 * Returns the x position of the source, as measured by where the beam comes out on the right.
	 */
	public double getPosition() {
		return position;
	}
	
	/*
	 * Changes the position of the source.
	 */
	public void setPosition(double position) {
		this.position = position;
	}
	
	/*
	 * Returns the ABCD matrix associated with the source. Should be [1, 0, 0, 1].
	 */
	public ABCDMatrix ABCD() {
		return ABCDMatrix.identity();
	}
	
	/*
	 * Has no effect because you cannot rename the source. Need to retain for compatibility
	 * with interface.
	 */
	public void setName(String name) {};
	
	/*
	 * Returns the name of the optic.
	 */
	public String getName() {
		return "Source";
	}
	
	/*
	 * Returns whether the source is selected. Returns false because cannot select source.
	 */
	public boolean isSelected() {
		return false;
	}
	
	/*
	 * Sets whether the source is selected. Because source cannot be selected, throws an IllegalArgumentException if true,
	 */
	public void setSelected(boolean selected) {
		if (selected) throw new IllegalArgumentException("Source cannot be selected");
	}
	
	/*
	 * Compares the position of two optics. Does not work with .equals()!
	 */
	public int compareTo(OpticsHardware o) {
		if (position == o.getPosition()) return 0;
		if (position > o.getPosition()) return 1;
		return -1;
	}
	
	
	
	/* Instance variables */
	private volatile double position = 0;
	

}
