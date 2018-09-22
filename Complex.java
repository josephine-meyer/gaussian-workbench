/*
 * Represents a complex number of form a + ib. Note that this is an immutable object.
 */

public class Complex {
	
	/*
	 * Constructor for form a + ib.
	 */
	public Complex(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	/*
	 * Constructor for real-valued complex number.
	 */
	public Complex(double a) {
		this.a = a;
		b = 0;
	}
	
	/*
	 * Returns the real part of the complex number.
	 */
	public double real() {
		return a;
	}
	
	/*
	 * Returns the imaginary part of the complex number.
	 */
	public double imag() {
		return b;
	}
	
	/*
	 * Returns the modulus of the complex number.
	 */
	public double modulus() {
		return Math.sqrt(modulusSquared());
	}
	
	/*
	 * Returns the modulus squared of the complex number.
	 */
	private double modulusSquared() {
		return a * a + b * b;
	}
	
	/*
	 * Returns the conjugate of the complex number.
	 */
	public Complex conjugate() {
		return new Complex(a, -b);
	}
	
	/*
	 * Returns the sum of two complex numbers.
	 */
	public Complex plus(Complex c) {
		return new Complex(a + c.real(), b + c.imag());
	}
	
	/*
	 * Returns the sum of a complex number and a real number.
	 */
	public Complex plus(double c) {
		return new Complex(a + c, b);
	}
	
	/*
	 * Returns the difference of two complex numbers.
	 */
	public Complex minus(Complex c) {
		return new Complex(a - c.real(), b - c.imag());
	}
	
	/*
	 * Returns the difference of a complex number and a real number.
	 */
	public Complex minus(double c) {
		return new Complex(a - c, b);
	}
	
	/*
	 * Returns the product of two complex numbers.
	 */
	public Complex times(Complex c) {
		return new Complex(a * c.real() - b * c.imag(), b * c.real() + a * c.imag());
	}
	
	/*
	 * Returns the product of a complex number and a real number.
	 */
	public Complex times(double c) {
		return new Complex(a * c, b * c);
	}
	
	/*
	 * Returns a complex number divided by another.
	 */
	public Complex divideBy(Complex c) {
		return times(c.reciprocal());
	}
	
	/*
	 * Returns a complex number divided by a double.
	 */
	public Complex divideBy(double c) {
		if (c == 0) throw new ArithmeticException("Divide by zero");
		return new Complex(a / c, b / c);
	}
	
	/*
	 * Outputs a string representation of the number.
	 */
	public String toString() {
		return "" + a + " +" + b + "i";
	}
	
	/*
	 * Returns the reciprocal of a complex number.
	 */
	public Complex reciprocal() {
		return conjugate().divideBy(modulusSquared());
	}
	
	/*
	 * Returns whether a complex number is real.
	 */
	public boolean isReal() {
		return b == 0;
	}
	
	
	/*
	 * Returns a Complex object equal to the imaginary number i.
	 */
	public static Complex i() {
		return new Complex(0,1);
	}
	
	
	//Private instance variables
	private double a; //real part
	private double b; //imaginary part

}
