/*
 * Class representing a POI (point of interest) marker. It has no effect on the beam.
 */
public class POI implements OpticsHardware {
	
	/*
	 * Constructor
	 */
	public POI(double position, String name) {
		this.position = position;
		this.name = name;
	}

	/*
	 * Returns the type.
	 */
	public String getType() {
		return "POI";
	}

	/*
	 * Returns the position.
	 */
	public double getPosition() {
		return position;
	}
	
	/*
	 * Sets the position.
	 */
	public void setPosition(double position) {
		this.position = position;
	}

	/*
	 * Returns ABCD Matrix representing the POI. Returns identity because POI doesn't affect beam.
	 */
	public ABCDMatrix ABCD() {
		return ABCDMatrix.identity();
	}
	
	/*
	 * Returns the name of the POI.
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Sets the name of the POI
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * Returns whether the optic is selected.
	 */
	public boolean isSelected() {
		return selected;
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
	
	/* Instance variables */
	private volatile double position;
	private String name;
	private boolean selected = false;

}
