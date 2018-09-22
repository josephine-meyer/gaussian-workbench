/*
 * Interface implemented by optics hardware (lenses, tunable lenses, POI, etc.)
 */
public interface OpticsHardware extends Comparable<OpticsHardware> {
	
	/*
	 * Returns a string describing the type of optic.
	 */
	public String getType();
	
	/*
	 * Returns the position of the optic.
	 */
	public double getPosition();
	
	/*
	 * Returns the ABCD matrix representing the optic.
	 */
	public ABCDMatrix ABCD();
	
	
	/*
	 * Sets the position of the optic.
	 */
	public void setPosition(double position);
	
	/*
	 * Sets the name of the optic.
	 */
	public void setName(String name);
	
	/*
	 * Returns the name of the optic.
	 */
	public String getName();
	
	/*
	 * Sets whether an optic is selected.
	 */
	public void setSelected(boolean selected);
	
	/*
	 * Returns whether an optic is selected.
	 */
	public boolean isSelected();
}
