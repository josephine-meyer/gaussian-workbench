/*
 * Represents an ABCD matrix. Also contains several useful static methods for performing
 * calculations involving Gaussian beams. Note that an ABCDMatrix object is immutable.
 */

public class ABCDMatrix {
	
	/*
	 * Constructs an ABCD matrix with the specified entries.
	 */
	public ABCDMatrix(double A, double B, double C, double D) {
		entries[0] = A;
		entries[1] = B;
		entries[2] = C;
		entries[3] = D;
	}
	
	/*
	 * Returns the base array containing the entries [A, B, C, D].
	 */
	public double[] baseArray() {
		return entries;
	}
	
	/*
	 * Returns a string representation of the matrix.
	 */
	public String toString() {
		return "[[" + entries[0] + "," + entries[1] + "][" + entries[2] + "," + entries[3] + "]]";
	}
	
	/*
	 * Tests whether two ABCD matrices are equal.
	 */
	public boolean equals(Object o) {
		if (o == this) return true;
		if (! (o instanceof ABCDMatrix)) return false;
		double[] arr = ((ABCDMatrix) o).baseArray();
		return ((entries[0] == arr[0]) && (entries[1] == arr[1]) &&
				(entries[2] == arr[2]) && (entries[3] == arr[3]));
	}
	
	/*
	 * Multiplies two ABCD matrices.
	 */
	public ABCDMatrix times(ABCDMatrix m) {
		double[] arr = m.baseArray();
		return new ABCDMatrix(entries[0] * arr[0] + entries[1] * arr[2],
							  entries[0] * arr[1] + entries[1] * arr[3],
							  entries[2] * arr[0] + entries[3] * arr[2],
							  entries[2] * arr[1] + entries[3] * arr[3]);
	}
	
	/*
	 * Multiplies an ABCD matrix by a real number.
	 */
	public ABCDMatrix times(double c) {
		return new ABCDMatrix(entries[0] * c, entries[1] * c, entries[2] * c, entries[3] * c);
	}
	
	/*
	 * Transforms q via action by an ABCDMatrix.
	 */
	public Complex transformQ(Complex q) {
		return ((q.times(entries[0]).plus(entries[1]))).
				divideBy((q.times(entries[2]).plus(entries[3])));
		}
	
	
	//Static methods
	
	/*
	 * Returns an ABCD matrix representing a lens of given focal length.
	 */
	public static ABCDMatrix lensMatrix(double focalLength) {
		return new ABCDMatrix(1, 0, -1 / focalLength, 1);
	}
	
	/*
	 * Returns an ABCD matrix representing free space of a given length.
	 */
	public static ABCDMatrix freeSpace(double length) {
		return new ABCDMatrix(1, length, 0, 1);
	}
	
	/*
	 * Returns the identity.
	 */
	public static ABCDMatrix identity() {
		return new ABCDMatrix(1,0,0,1);
	}
	
	
	
	
	//Private instance variables
	
	private double[] entries = new double[4];
	
}
