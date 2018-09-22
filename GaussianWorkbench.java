/*
 * Allows user to simulate propagation of Gaussian beams through free space and lenses.
 * Computes beam waist and radius of curvature at arbitrary points within setup.
 * 
 * This software is created using the Stanford ACM (Association for Computing Machinery) libraries. These
 * libraries are subject to change without notice, which could break the code used to operate this program. 
 * The working copy of the ACM libraries used for this project can be found in the folder with the source code. 
 * The software license for the ACM libraries can be found here:
 * http://cs.stanford.edu/people/eroberts/jtf/documents/License.pdf
 * 
 * Roxa Meyer, Stanford University, and ACM shall not be held liable for errors or bugs in the software or for any 
 * incidental damages resulting from the use of this software. Creator offers no guarantees for the accuracy of
 * calculations done using this applet.
 * 
 * Created by: Roxa Meyer, Schleier-Smith lab, Stanford University, August 2017.
 * Version #1.0.2, updated 08/14/2017.
 * Changes: Re-implemented find waist feature for better accuracy and fixed bug in drawing ruler.
 */


import acm.program.*;
import acm.graphics.*;
import acm.gui.*;
import javax.swing.*;
import java.awt.Color;
import java.util.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.text.*;
import java.math.*;
import acm.util.*;

public class GaussianWorkbench extends Program implements ChangeListener, ComponentListener {
	
	/**
	 * Main method that starts the program. Called from externally.
	 * @param args Command line arguments. Currently unused.
	 */
	public static void main(String[] args) {
		(new GaussianWorkbench()).start();
	}	
	
	
	/**
	 * Initializes the canvas and GUI. Called automatically at start of program by superclass.
	 */
	public void init() {
		
		resize(WINDOW_DEFAULT_SIZE_X, WINDOW_DEFAULT_SIZE_Y);
		pause(RESIZE_PAUSE_TIME);
		
		
		add(canvas);
		canvas.setAutoRepaintFlag(false);
		canvas.setIgnoreRepaint(true);
		canvas.addComponentListener(this);
		
		addInteractors(); //Buttons and fields around the side of screen
		addDefaultOptics(); //Adds the optics to be displayed on screen at start. Can remove all but source if desired.
		addActionListeners();
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		
		timer = new javax.swing.Timer(CANVAS_REFRESH_PAUSE_TIME, this); //Creates the timer used to refresh screen
		timer.setActionCommand("timer");
		timer.start();
		propagateABCDMatrices();
		refreshCanvasFlag = true;
	}
	
	
	/**
	 * Adds the interactors around the outside of the screen.
	 */
	private void addInteractors() {
		addWestInteractors();
		addSouthInteractors();
		addEastInteractors();
	}
	
	
	/**
	 * Adds the interactors on the left side of the screen.
	 */
	private void addWestInteractors() {
		//Radio button for selecting mode
		ButtonGroup group = new ButtonGroup();
		group.add(focalLengthModeButton);
		group.add(focalPowerModeButton);
		focalLengthModeButton.setSelected(true);
		focalLengthModeButton.setActionCommand("FocalLengthMode");
		focalLengthModeButton.addActionListener(this);
		focalPowerModeButton.setActionCommand("FocalPowerMode");
		focalPowerModeButton.addActionListener(this);
		add(focalLengthModeButton, WEST);
		add(focalPowerModeButton, WEST);

		//Spacer
		add(new JLabel(" "), WEST);

		//Wavelength input
		add(new JLabel("Wavelength (nm)"), WEST);
		wavelengthField.setActionCommand("Wavelength");
		wavelengthField.addActionListener(this);
		add(wavelengthField, WEST);

		//Collimated waist input
		add(new JLabel("Collimated Waist"), WEST);
		collWaistField.setActionCommand("CollWaist");
		collWaistField.addActionListener(this);
		add(collWaistField, WEST);

		//Spacer
		add(new JLabel(" "), WEST);


		//Scroll buttons
		add(new JLabel("Scroll"), WEST);
		leftScroll = new JButton("<<<<<");
		leftScroll.setActionCommand("LeftScroll");
		leftScroll.addActionListener(this);
		add(leftScroll, WEST);
		rightScroll = new JButton(">>>>>");
		rightScroll.setActionCommand("RightScroll");
		rightScroll.addActionListener(this);
		add(rightScroll, WEST);

		//Spacer
		add(new JLabel(" "), WEST);

		//Zoom buttons
		add(new JLabel("Zoom"), WEST);
		zoomPlus = new JButton(" + ");
		zoomMinus = new JButton(" - ");
		zoomPlus.setActionCommand("ZoomPlus");
		zoomMinus.setActionCommand("ZoomMinus");
		zoomPlus.addActionListener(this);
		zoomMinus.addActionListener(this);
		add(zoomPlus, WEST);
		add(zoomMinus, WEST);

		//Spacer
		add(new JLabel(" "), WEST);


		//Apparent beam diameter
		add(new JLabel("Apparent Beam Diameter"), WEST);
		apparentBeamDiameter.addChangeListener(this);
		add(apparentBeamDiameter, WEST);

		//Spacer
		add(new JLabel(" "), WEST);

		//Find Waist feature
		findWaistButton.setActionCommand("FindWaist");
		findWaistButton.addActionListener(this);
		add(findWaistButton, WEST);
		add(new JLabel("Waist Position"), WEST);
		add(calcWaistPositionLabel, WEST);
		add(new JLabel("Waist Radius"), WEST);
		add(calcWaistLabel, WEST);
	}
	
	
	/**
	 * Adds the interactors on the bottom of the screen.
	 */
	private void addSouthInteractors() {
		//Add POI button
		addPOIButton = new JButton("Add POI");
		addPOIButton.setActionCommand("AddPOI");
		addPOIButton.addActionListener(this);
		add(addPOIButton, SOUTH);
		
		//Spacer
		add(new JLabel("   "), SOUTH);
		
		//Add lens button
		addLensButton = new JButton("Add Lens");
		addLensButton.setActionCommand("AddLens");
		addLensButton.addActionListener(this);
		add(addLensButton, SOUTH);
		
		//Spacer 
		add(new JLabel("   "), SOUTH);
		
		//Add tunable lens button
		addTunableLensButton = new JButton("Add Tunable Lens");
		addTunableLensButton.setActionCommand("AddTunableLens");
		addTunableLensButton.addActionListener(this);
		add(addTunableLensButton, SOUTH);
		
		//Spacer
		add(new JLabel("   "), SOUTH);
		
		//Remove button
		removeButton = new JButton("Remove Optic");
		removeButton.setActionCommand("Remove");
		removeButton.addActionListener(this);
		add(removeButton, SOUTH);
		
		//Spacer
		add(new JLabel("   "), SOUTH);
		
		//Clear button
		clearButton = new JButton("Clear All");
		clearButton.setActionCommand("Clear");
		clearButton.addActionListener(this);
		add(clearButton, SOUTH);
		
		//Spacer
		add(new JLabel("                              "), SOUTH);
		
		//New button
		newButton = new JButton("New");
		newButton.setActionCommand("New");
		newButton.addActionListener(this);
		add(newButton, SOUTH);
		
		//Spacer
		add(new JLabel("   "), SOUTH);
		
		//Open button
		openButton = new JButton("Open");
		openButton.setActionCommand("Open");
		openButton.addActionListener(this);
		add(openButton, SOUTH);
		
		//Spacer
		add(new JLabel("   "), SOUTH);
		
		//Save button
		saveButton = new JButton("Save");
		saveButton.setActionCommand("Save");
		saveButton.addActionListener(this);
		add(saveButton, SOUTH);
		
		//Spacer
		add(new JLabel("   "), SOUTH);
		
		//Save as button
		saveAsButton = new JButton("Save As");
		saveAsButton.setActionCommand("SaveAs");
		saveAsButton.addActionListener(this);
		add(saveAsButton, SOUTH);
	}
	
	
	/**
	 * Adds the interactors on the right side of the screen.
	 */
	private void addEastInteractors() {
		//Field to set name
		add(new JLabel("Name                                  "), EAST);
		nameField.setActionCommand("Name");
		nameField.setText("No name");
		nameField.addActionListener(this);
		add(nameField, EAST);

		//Field to set position
		add(new JLabel("Position"), EAST);
		positionField.setActionCommand("Position");
		positionField.setText(Double.toString(DEFAULT_POSITION));
		positionField.addActionListener(this);
		add(positionField, EAST);

		
		//Field to set focal length
		add(focalLengthLabel, EAST);
		focalLengthField.setActionCommand("FocalLength");
		focalLengthField.setText(Double.toString(DEFAULT_FOCAL_LENGTH));
		focalLengthField.addActionListener(this);
		add(focalLengthField, EAST);
		
		//Spacer
		add(new JLabel(" "), EAST);
		
		//Fields to set min and max focal lengths for tunable lens
		add(new JLabel("For Tunable Lenses:"), EAST);
		add(new JLabel(" "), EAST);
		add(minCurrentFocalLengthLabel, EAST);
		minFocalLengthField.setActionCommand("MinFocalLength");
		minFocalLengthField.setText(Double.toString(DEFAULT_MIN_FOCAL_LENGTH));
		minFocalLengthField.addActionListener(this);
		add(minFocalLengthField, EAST);
		add(maxCurrentFocalLengthLabel, EAST);
		maxFocalLengthField.setActionCommand("MaxFocalLength");
		maxFocalLengthField.setText(Double.toString(DEFAULT_MAX_FOCAL_LENGTH));
		maxFocalLengthField.addActionListener(this);
		add(maxFocalLengthField, EAST);
		
		//Slider to tune lens
		add(new JLabel("Tune Lens"), EAST);
		tuneLens.addChangeListener(this);
		tuneLens.setVisible(false);
		add(tuneLens, EAST);
		
		//Spacer
		add(new JLabel(" "), EAST);
		
		//Labels to display information
		add(new JLabel("Cursor"), EAST);
		cursorPositionField.setActionCommand("CursorPosition");
		cursorPositionField.addActionListener(this);
		add(cursorPositionField, EAST);
		add(new JLabel("Radius of Curvature:"), EAST);
		radiusOfCurvatureLabel = new JLabel("             ");
		add(radiusOfCurvatureLabel, EAST);
		add(new JLabel("Beam Radius:"), EAST);
		radiusLabel = new JLabel("             ");
		add(radiusLabel, EAST);
		add(new JLabel(" "), EAST);
		add(new JLabel("**Note that radius of"), EAST);
		add(new JLabel("curvature diverges at waist**"), EAST);
	}
	
	
	/**
	 * Adds the default optics to the screen when the program is run.
	 */
	private void addDefaultOptics() {
		synchronized (lock) {
			opticsList.add(new Source(0));
			opticsList.add(new Lens(100, 50, "L1"));
			opticsList.add(new Lens(300, 150, "L2"));
		}
	}
	
	
	/**
	 * Returns the scale factor for distances along the x axis.
	 */
	private double getScaleFactor() {
		return (canvas.getWidth() - 2 * RULER_X_FROM_EDGE) / (rightEdge - leftEdge);
	}
	
	
	/**
	 * Draws the ruler at the bottom of the screen.
	 */
	private void drawRuler() {
		//Add label at bottom of screen
		GLabel bottomLabel = new GLabel("Position (mm)");
		canvas.add(bottomLabel, (canvas.getWidth() - bottomLabel.getWidth()) / 2, 
				canvas.getHeight() - RULER_TITLE_Y_FROM_BOTTOM);
		
		//Add horizontal line
		canvas.add(new GLine(RULER_X_FROM_EDGE, canvas.getHeight() - RULER_Y_FROM_BOTTOM, 
				canvas.getWidth() - RULER_X_FROM_EDGE, canvas.getHeight() - RULER_Y_FROM_BOTTOM));
		
		//Draw tick marks
		double smallTickIncrement = Math.pow(10, orderOfMagnitude((rightEdge - leftEdge) / 25));
		double largeTickIncrement = smallTickIncrement * 10;
		double smallTickStart = smallTickIncrement * Math.ceil((leftEdge / smallTickIncrement));
		double largeTickStart = largeTickIncrement * Math.ceil((leftEdge / largeTickIncrement));
		
		for (double position = smallTickStart; position < rightEdge; position += smallTickIncrement) {
			double canvasPosition = spacialToCanvasX(position);
			canvas.add(new GLine(canvasPosition, canvas.getHeight() - RULER_Y_FROM_BOTTOM, canvasPosition,
					canvas.getHeight() - RULER_Y_FROM_BOTTOM - RULER_SMALL_TICK_HEIGHT));
		}
		
		for (double position = largeTickStart; position < rightEdge; position += largeTickIncrement) {
			double canvasPosition = spacialToCanvasX(position);
			canvas.add(new GLine(canvasPosition, canvas.getHeight() - RULER_Y_FROM_BOTTOM, canvasPosition,
					canvas.getHeight() - RULER_Y_FROM_BOTTOM - RULER_LARGE_TICK_HEIGHT));
			GLabel label = new GLabel(Integer.toString((int) position));
			canvas.add(label, canvasPosition - label.getWidth() / 2.0, canvas.getHeight() - LABEL_Y_FROM_BOTTOM);
		}
	}
	
	
	/**
	 * Returns the order of magnitude of a number. Throws exception if number is 0.
	 * @param value The number of interest.
	 * @return An integer corresponding to its order of magnitude as a power of 10.
	 */
	private int orderOfMagnitude(double value) {
		if (value == 0) throw new ArithmeticException("Must be nonzero.");
		int orderOfMagnitude = 0;
		while (value < 1) {
			orderOfMagnitude--;
			value *= 10;
		}
		while (value >= 10) {
			orderOfMagnitude++;
			value /= 10;
		}
		return orderOfMagnitude;
	}
	
	/**
	 * Propagates the ABCD matrices of all the optics to get the ABCD matrix at each optic.
	 */
	private void propagateABCDMatrices() {
		synchronized (lock) { //Ensures that the matrix list and optics list are thread safe
			matrixList = new ArrayList<ABCDMatrix>();
			OpticsHardware source = opticsList.get(0); //The source
			matrixList.add(source.ABCD());
			for (int i = 1; i < opticsList.size(); i++) { //Intentionally skips the source
				matrixList.add((opticsList.get(i).ABCD()).times(ABCDMatrix.freeSpace(opticsList.get(i).getPosition() -
						opticsList.get(i-1).getPosition())).times(matrixList.get(i-1)));
			}
		}
	}

	/**
	 * Returns a pair of numbers representing the radius of curvature and radius of beam
	 * at any point in space.
	 * @param point The point at which we are interested.
	 * @param q_in The q parameter at the start of the beam.
	 * @return A pair of numbers representing the radius of curvature and beam radius.
	 */
	private GPoint beamParametersAtPoint(double point, Complex q_in) {
		Complex q_out = getMatrixForPoint(point).transformQ(q_in);
		double radiusOfCurvature = 1.0 / (q_out.reciprocal().real());;
		double radius = Math.sqrt(-1 * (wavelength * Math.pow(10, -6) / (Math.PI * q_out.reciprocal().imag())));
		return new GPoint(radiusOfCurvature, radius);
	}
	
	/**
	 * Returns the ABCD Matrix corresponding to a point in space.
	 * Returns null in the event that there is no beam.
	 * @param point The point at which we are interested.
	 * @return The matrix corresponding to that point. Null if no beam.
	 */
	private ABCDMatrix getMatrixForPoint(double point) {
		if (point < 0) return null;
		int index = 0;
		synchronized (lock) {
			for(int i = 0; i < opticsList.size(); i++) {
				double opticPosition = opticsList.get(i).getPosition();
				if (opticPosition <= point) {
					index = i;
				} else {
					break;
				}
			}
			return ABCDMatrix.freeSpace(point - opticsList.get(index).getPosition()).
					times(matrixList.get(index));
		}
	}

	/**
	 * Scrolls the screen left or right.
	 * @param numClicks The net number of clicks. Positive indicates scroll to right,
	 * negative indicates scroll to left.
	 */
	private void scroll(int numClicksScroll) {
		if (numClicksScroll != 0) {
			double scrollIncrement = (rightEdge - leftEdge) * numClicksScroll * SCROLL_FRACTION;
			rightEdge += scrollIncrement;
			leftEdge += scrollIncrement;
			refreshCanvasFlag = true;
		}
	}
	
	/**
	 * Zooms the screen in or out from center.
	 * @param numClicksZoom The net number of clicks. Positive indicates zoom in.
	 */
	private void zoom(int numClicksZoom) {
		if (numClicksZoom != 0) {
			double currentWidth = rightEdge - leftEdge;
			if ((currentWidth >= MIN_SCREEN_WIDTH && numClicksZoom > 0) || 
					(currentWidth <= MAX_SCREEN_WIDTH && numClicksZoom < 0)) {
				double newWidth = currentWidth * Math.pow(2, -numClicksZoom);
				double center = (rightEdge + leftEdge) / 2;
				rightEdge = center + newWidth / 2.0;
				leftEdge = center - newWidth / 2.0;
				refreshCanvasFlag = true;
			}
		}
	}
	
	
	/**
	 * Refreshes the canvas to update graphics.
	 */
	private void refreshCanvas() {
		canvas.removeAll();
		findWaistButton.setSelected(false);
		ROI = null;
		drawRuler();
		drawBeams();
		drawOptics();
		drawOpticsLabels();
		canvas.repaint();
	}
	
	/**
	 * Draws the optics on the screen.
	 */
	private void drawOptics() {
		synchronized (lock) {
			for (OpticsHardware o: opticsList) {
				if (o instanceof Source) {
					GRoundRect rect = new GRoundRect(SOURCE_WIDTH, SOURCE_HEIGHT);
					rect.setFilled(true);
					rect.setColor(Color.LIGHT_GRAY);
					canvas.add(rect, spacialToCanvasX(o.getPosition()) - SOURCE_WIDTH, 
							canvas.getHeight() - CENTERLINE_HEIGHT - SOURCE_HEIGHT / 2.0);
				} else if (o instanceof Lens) {
					GOval oval = new GOval(LENS_WIDTH, LENS_HEIGHT);
					if (o.isSelected()) {
						oval.setColor(Color.RED);
					} else {
						oval.setColor(Color.BLACK);
					}
					oval.setFilled(true);
					oval.setFillColor(Color.CYAN);
					canvas.add(oval, spacialToCanvasX(o.getPosition()) - LENS_WIDTH / 2.0,
							canvas.getHeight() - CENTERLINE_HEIGHT - LENS_HEIGHT / 2.0);		
				} else if (o instanceof POI) {
					GRect rect = new GRect(POI_WIDTH, POI_HEIGHT);
					rect.setFilled(true);
					if (o.isSelected()) {
						rect.setColor(Color.RED);
					} else {
						rect.setColor(Color.BLACK);
					}
					canvas.add(rect, spacialToCanvasX(o.getPosition()) - POI_WIDTH / 2.0,
							canvas.getHeight() - CENTERLINE_HEIGHT - POI_HEIGHT / 2.0);
				}
			}
		}
	}
	
	
	/**
	 * Draws the labels for each optic on the screen.
	 */
	private void drawOpticsLabels() {
		synchronized (lock) {
			for (int i = 0; i < opticsList.size(); i++) {
				OpticsHardware o = opticsList.get(i);
				ArrayList<GLabel> labelList = getLabelList(o);
				double startHeight;
				double position = spacialToCanvasX(o.getPosition()) - OPTICS_LABEL_X_OFFSET;
				if (o instanceof Source) position -= SOURCE_WIDTH / 2.0;
				if (i % 2 == 0) { //if i is even
					startHeight = canvas.getHeight() - CENTERLINE_HEIGHT - LABEL_SEPARATION_FROM_CENTERLINE 
							- (labelList.size() - 1) * LABEL_SEPARATION;
				} else {
					startHeight = canvas.getHeight() - CENTERLINE_HEIGHT + LABEL_SEPARATION_FROM_CENTERLINE;
				}

				for (int j = 0; j < labelList.size(); j++) {
					canvas.add(labelList.get(j), position, startHeight + j * LABEL_SEPARATION);
				}
			}

		}
	}
	
	
	/**
	 * Returns a list of the labels to be displayed for a given optic.
	 * @param o The optic to be displayed.
	 * @return An ArrayList containing the necessary labels.
	 */
	private ArrayList<GLabel> getLabelList(OpticsHardware o) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.HALF_UP);
		ArrayList<GLabel> labelList = new ArrayList<GLabel>();
		labelList.add(new GLabel("Name: " + o.getName()));
		labelList.add(new GLabel("Type: " + o.getType()));
		labelList.add(new GLabel("Position: " + df.format((o.getPosition()))));
		if (o instanceof Lens) {
			if (focalPowerMode) {
				labelList.add(new GLabel("Focal power: " + df.format(1000.0 / ((Lens) o).getFocalLength())));
			} else {
				labelList.add(new GLabel("f: " + df.format(((Lens) o).getFocalLength())));
			}
		}
		if (o instanceof TunableLens) {
			TunableLens t = (TunableLens) o;
			if (focalPowerMode) {
				labelList.add(new GLabel("Min focal power: " + df.format(1000.0 / t.getMaxFocalLength())));
				labelList.add(new GLabel("Max focal power: " + df.format(1000.0 / t.getMinFocalLength())));
			} else {
				labelList.add(new GLabel("f at min current: " + df.format(t.getMinFocalLength())));
				labelList.add(new GLabel("f at max current: " + df.format(t.getMaxFocalLength())));
			}
		}
		double radius = beamParametersAtPoint(o.getPosition(), getQIn()).getY();
		labelList.add(new GLabel("Beam radius: " + df.format(radius)));
		return labelList;
	}
	
	
	/**
	 * Draws the beams on the screen.
	 */
	private void drawBeams() {
		Complex q_in = getQIn();
		int minPixel = (1 + (int) spacialToCanvasX(0));
		int prevPixel = minPixel;
		double prevRadius = beamParametersAtPoint(canvasToSpacialX(prevPixel), q_in).getY() * radiusScaleFactor;
		for (int pixel = minPixel + BEAM_PIXEL_RESOLUTION; pixel <= canvas.getWidth() - 1; 
				pixel += BEAM_PIXEL_RESOLUTION) {
			double radius = beamParametersAtPoint(canvasToSpacialX(pixel), q_in).getY() * radiusScaleFactor;
			GLine line1 = new GLine(prevPixel, canvas.getHeight() - CENTERLINE_HEIGHT - prevRadius, pixel,
					canvas.getHeight() - CENTERLINE_HEIGHT - radius);
			GLine line2 = new GLine(prevPixel, canvas.getHeight() - CENTERLINE_HEIGHT + prevRadius, pixel,
					canvas.getHeight() - CENTERLINE_HEIGHT + radius);
			line1.setColor(Color.RED);
			line2.setColor(Color.RED);
			canvas.add(line1);
			canvas.add(line2);
			prevPixel = pixel;
			prevRadius = radius;
		}
	}
	

	/**
	 * Converts an x coordinate on the canvas to the spacial coordinate.
	 * @param canvasCoordinate The coordinate on the canvas.
	 * @return The coordinate in actual space (mm)
	 */
	private double canvasToSpacialX(double canvasCoordinate) {
		return (canvasCoordinate - RULER_X_FROM_EDGE) / getScaleFactor() + leftEdge;
	}
	
	
	/**
	 * Converts an x coordinate in space to the pixel on the canvas.
	 * @param spacialCoordinate The coordinate in space.
	 * @return The coordinate on the canvas.
	 */
	private double spacialToCanvasX(double spacialCoordinate) {
		return (spacialCoordinate - leftEdge) * getScaleFactor() + RULER_X_FROM_EDGE;
	}
	
	
	/**
	 * Saves the current optics setup as a file.
	 * @param filename The filename at which to save the file. If null, selects location using JFileChooser.
	 * @return Whether save was successful.
	 */
	private boolean saveFile(String filename) {
		if (filename != null) {
			if (saveToFile(new File(filename))) return true;
			saveName = null;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Gaussian Optics files", EXTENSION));
		int saveOption = chooser.showSaveDialog(this);
		if (saveOption != JFileChooser.APPROVE_OPTION) return false;
		File file = chooser.getSelectedFile();
		if (! file.getAbsolutePath().endsWith(EXTENSION)) file = new File(file + "." + EXTENSION);
		return saveToFile(file);
	}

	
	/**
	 * Saves the current optics setup to an open file.
	 * @param file The file to which to save.
	 * @return Whether the save was successful.
	 */
	private boolean saveToFile(File file) {
		try {
			PrintWriter wr = new PrintWriter(new FileWriter(file));
			wr.println(wavelength);
			wr.println(collWaist);
			synchronized (lock) {
				for (OpticsHardware o: opticsList) {
					wr.println();
					writeOptic(wr, o);
				}
			}
			wr.close();
			saveName = file.getAbsolutePath();
			return true;
		} catch (IOException ex) {
			saveName = null;
			return false;
		}
	}

	
	/**
	 * Writes a single optic to a file.
	 * @param wr A PrintWriter to the file.
	 * @param o The optic to print.
	 */
	private void writeOptic(PrintWriter wr, OpticsHardware o) {
		if (o instanceof Source) {
			wr.println("Source");
			wr.println(o.getName());
			wr.println(o.getPosition());
		} else if (o instanceof POI) {
			wr.println("POI");
			wr.println(o.getName());
			wr.println(o.getPosition());
		} else if (o instanceof TunableLens) {
			wr.println("TunableLens");
			TunableLens lens = (TunableLens) o;
			wr.println(lens.getName());
			wr.println(lens.getPosition());
			wr.println(lens.getFocalLength());
			wr.println(lens.getMinFocalLength());
			wr.println(lens.getMaxFocalLength());
		} else if (o instanceof Lens) { //Not tunable lens
			wr.println("Lens");
			Lens lens = (Lens) o;
			wr.println(lens.getName());
			wr.println(lens.getPosition());
			wr.println(lens.getFocalLength());
		}
	}
	
	
	/**
	 * Opens a file.
	 * @return Whether the open was successful.
	 */
	private boolean openFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Gaussian Optics files", EXTENSION));
		int option = chooser.showOpenDialog(this);
		if (option != JFileChooser.APPROVE_OPTION) return false;
		
		try {
			File file = chooser.getSelectedFile();
			BufferedReader rd = new BufferedReader(new FileReader(file));
			double newWavelength = Double.parseDouble(rd.readLine());
			double newCollWaist = Double.parseDouble(rd.readLine());
			ArrayList<OpticsHardware> newOpticsList = new ArrayList<OpticsHardware>();
			while (rd.readLine() != null) {
				addOptic(rd, newOpticsList);
			}
			rd.close();
			wavelength = newWavelength;
			collWaist = newCollWaist;
			opticsList = newOpticsList;
			wavelengthField.setValue(newWavelength);
			collWaistField.setValue(newCollWaist);
			saveName = file.getAbsolutePath();
			propagateABCDMatrices();
			refreshCanvasFlag = true;
			return true;
		} catch (IOException ex) {
			return false;
		}
	}
	
	
	/**
	 * Adds an optic from file to the virtual benchtop.
	 * @param rd A buffered reader for the file.
	 * @param newOpticsList The list to which to add the optics.
	 * @throws IOException
	 */
	private void addOptic(BufferedReader rd, ArrayList<OpticsHardware> newOpticsList) throws IOException {
		double position;
		String name;
		double focalLength;
		switch (rd.readLine()) { //Determines type of optic
		case "Source":
			rd.readLine(); //Name not needed, always "source"
			position = Double.parseDouble(rd.readLine());
			newOpticsList.add(new Source(position));
			break;
		case "POI":
			name = rd.readLine();
			position = Double.parseDouble(rd.readLine());
			newOpticsList.add(new POI(position, name));
			break;
		case "Lens":
			name = rd.readLine();
			position = Double.parseDouble(rd.readLine());
			focalLength = Double.parseDouble(rd.readLine());
			newOpticsList.add(new Lens(position, focalLength, name));
			break;
		case "TunableLens":
			name = rd.readLine();
			position = Double.parseDouble(rd.readLine());
			focalLength = Double.parseDouble(rd.readLine());
			newOpticsList.add(new TunableLens(position, Double.parseDouble(rd.readLine()), 
					Double.parseDouble(rd.readLine()), focalLength, name));
			break;
		default:
			throw new IOException("Reached end of switch statement without hitting name of optic");
		}
	}

	
	/**
	 * Responds to moved sliders. Called externally.
	 */
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == apparentBeamDiameter) {
			radiusScaleFactor = Math.pow(10, apparentBeamDiameter.getValue() / 50.0);
			refreshCanvasFlag = true;
		} else if (source == tuneLens) {
			if (ignoreChangeEventFlag) { //Ignores one change event from the tuneLens slider
				ignoreChangeEventFlag = false;
			} else if (selectedOptic != null && selectedOptic instanceof TunableLens) {
				TunableLens o = (TunableLens) selectedOptic;
				double minFocalPower = 1000.0 / o.getMinFocalLength();
				double maxFocalPower = 1000.0 / o.getMaxFocalLength();
				double focalPower = minFocalPower + (maxFocalPower - minFocalPower) / 100 * tuneLens.getValue();
				o.setFocalLength(1000.0 / focalPower);
				setFieldFocalLengthValue(focalLengthField, 1000.0 / focalPower);
				propagateABCDMatrices();
				refreshCanvasFlag = true;
				
			}
		}
	}
	
	
	/**
	 * Responds to interactors that are pressed or updated by user, or signals sent by timer. Directs appropriate
	 * action to take place based on signal sent from source. Called externally in events thread.
	 */
	public void actionPerformed(ActionEvent e) {
		
		switch (e.getActionCommand()) { //Gets command thrown by the source to identify the required action.
		
		case "timer": //Timer has told screen to refresh
			respondToTimer();
			break;
			
		case "Wavelength": //User has changed the wavelength
			wavelength = wavelengthField.getValue();
			refreshCanvasFlag = true;
			break;
			
		case "CollWaist": //User has changed the collimated waist
			collWaist = collWaistField.getValue();
			refreshCanvasFlag = true;
			break;
		
		case "LeftScroll": //User has pressed left scroll button
			scroll(-1);
			refreshCanvasFlag = true;
			break;
			
		case "RightScroll": //User has pressed right scroll button
			scroll(1);
			refreshCanvasFlag = true;
			break;
			
		case "ZoomPlus": //User has pressed zoom in button
			zoom(1);
			refreshCanvasFlag = true;
			break;
			
		case "ZoomMinus": //User has pressed zoom out button
			zoom(-1);
			refreshCanvasFlag = true;
			break;
			
		case "Save": //User has pressed save button
			saveFile(saveName);
			break;
			
		case "SaveAs": //User has pressed save as button
			saveFile(null);
			break;
			
		case "Open": //User has pressed open button
			if (openFile()) { //If file was opened successfully
				refreshForNewFile();
			}
			break;
			
		case "Remove": //User has pressed the remove optic button
			removeSelectedOptic();
			break;
		
		case "Name": //User has pressed enter in the name field
			changeName();
			break;
			
		case "Position": //User has pressed enter in the position field
			changePosition();
			break;
			
		case "FocalLength": //User has pressed enter in the focal length field
			changeFocalLength();
			break;
			
		case "MinFocalLength": //User has pressed enter in the focal length at min current field
			changeMinFocalLength();
			break;
			
		case "MaxFocalLength": //User has pressed enter in the focal length at max current field
			changeMaxFocalLength();
			break;
			
		case "AddLens": //User has pressed the add lens button
			addLens();
			break;
			
		case "AddTunableLens": //User has pressed the add tunable lens button
			addTunableLens();
			break;
		
		case "CursorPosition": //User has input a value into the cursor position box and pressed enter	
			updateParamsAtPointLabels(cursorPositionField.getValue());
			break;
			
		case "FindWaist": //User has pressed the find waist button
			respondToClickedFindWaistButton();
			break;
			
		case "FocalPowerMode": //User has pressed the focal power mode button
			respondToFocalPowerModeButton();
			break;
			
		case "FocalLengthMode": //User has pressed the focal length mode button
			respondToFocalLengthModeButton();
			break;
			
		case "AddPOI": //User has pressed the add POI button
			addPOI();
			break;
			
		case "Clear": //User has pressed the clear optics button
			clearAll();
			break;
			
		case "New": //User has pressed the new button
			respondToNewButton();
			break;
			
		default:
			throw new ErrorException("Unrecognized command");
		}
	}
	
	
	/**
	 * Responds to the "new" button being pressed by restoring screen to default and clearing optics.
	 */
	private void respondToNewButton() {
		leftEdge = DEFAULT_LEFT_EDGE;
		rightEdge = DEFAULT_RIGHT_EDGE;
		clearAll();
		saveName = null;
		wavelength = DEFAULT_WAVELENGTH;
		collWaist = DEFAULT_COLLWAIST;
		wavelengthField.setValue(DEFAULT_WAVELENGTH);
		collWaistField.setValue(DEFAULT_COLLWAIST);
		nameField.setText("No name");
		focalLengthField.setValue(DEFAULT_FOCAL_LENGTH);
		minFocalLengthField.setValue(DEFAULT_MIN_FOCAL_LENGTH);
		maxFocalLengthField.setValue(DEFAULT_MAX_FOCAL_LENGTH);
	}
	
	
	/** 
	 * Adds a POI at the selected location if there are no naming or position collisions.
	 */
	private void addPOI() {
		deselectAll();
		String name = nameField.getText().trim();
		double position = positionField.getValue();
		//Exit if we can't add a POI due to distance or naming collisions.
		if (! checkIfFarEnoughFromOtherOptics(position, null)) return;
		if (! checkForNamingCollision(name, null)) return;
		
		if (selectedOptic != null) selectedOptic.setSelected(false);
		POI poi = new POI(position, name);
		synchronized (lock) {
			opticsList.add(poi);
		}
		sortOpticsList();
		propagateABCDMatrices();
		selectedOptic = poi;
		poi.setSelected(true);
		refreshCanvasFlag = true;
	}
	
	
	/**
	 * Changes to focal length mode if we press the focal length button while in focal power mode.
	 */
	private void respondToFocalLengthModeButton() {
		if (focalPowerMode) {
			focalPowerMode = false;
			focalLengthLabel.setText("Focal length");
			minCurrentFocalLengthLabel.setText("Focal Length at Min Current");
			maxCurrentFocalLengthLabel.setText("Focal Length at Max Current");
			invertValuesInFields();
			refreshCanvasFlag = true;
		}
	}
	
	
	/**
	 * Changes to focal power mode if we press the focal power mode button while in focal length mode.
	 */
	private void respondToFocalPowerModeButton() {
		if (! focalPowerMode) { //If we changed state
			focalPowerMode = true;
			focalLengthLabel.setText("Focal power (diopters)");
			minCurrentFocalLengthLabel.setText("Min focal power (diopters)");
			maxCurrentFocalLengthLabel.setText("Max focal power (diopters)");
			invertValuesInFields();
			refreshCanvasFlag = true;
		}
	}
	
	
	/**
	 * Called when the find waist button is clicked to remove the ROI if the button is deselected and we have a ROI.
	 */
	private void respondToClickedFindWaistButton() {
		if ((! findWaistButton.isSelected()) && (ROI != null)) { //If we've just deselected the button but have a ROI
			canvas.remove(ROI);
			ROI = null;
			canvas.repaint();
		}
		
	}
	
	
	/**
	 * Changes the focal length at max current
	 * of the selected optic, if the optic is a tunable lens and this is a valid change.
	 */
	private void changeMaxFocalLength() {
		if (! (selectedOptic instanceof TunableLens)) return;
		TunableLens lens = (TunableLens) selectedOptic;
		double maxCurrentFocalLength = getDoubleFromField(maxFocalLengthField);
		
		switch (checkFocalLengthRange(lens.getMinFocalLength(), maxCurrentFocalLength, lens.getFocalLength())) {
		case OK:
			break; //No need to do anything
		case INFINITY_CROSSING:
			setFieldFocalLengthValue(maxFocalLengthField, lens.getMaxFocalLength()); //Return to previous value
			return; //Such a lens is physically impossible because it goes through infinite focal power. Lens not added.
		case OUTSIDE_MIN_FOCAL_POWER:
			assert(false); //Should be an impossible outcome
		case OUTSIDE_MAX_FOCAL_POWER:
			setFieldFocalLengthValue(focalLengthField, maxCurrentFocalLength); 
			lens.setFocalLength(maxCurrentFocalLength);
			propagateABCDMatrices();
			break;
		}
		
		lens.setMaxFocalLength(maxCurrentFocalLength);
		setTuneLensBar(lens);
		refreshCanvasFlag = true;
	}
	
	
	/**
	 * Fills focal length fields according to the correct mode.
	 * @param field The field to fill.
	 * @param value The focal length to display in the field.
	 */
	private void setFieldFocalLengthValue(DoubleField field, double value) {
		if (focalPowerMode) {
			field.setValue(1000.0 / value);
		} else {
			field.setValue(value);
		}
	}
	
	
	/**
	 * Changes the minimum focal length of the selected lens, if a tunable lens is selected and the change is valid.
	 */
	private void changeMinFocalLength() {
		if (! (selectedOptic instanceof TunableLens)) return;
		
		TunableLens lens = (TunableLens) selectedOptic;
		double minCurrentFocalLength = getDoubleFromField(minFocalLengthField);
		
		switch (checkFocalLengthRange(minCurrentFocalLength, lens.getMaxFocalLength(), lens.getFocalLength())) {
		case OK:
			break; //We don't need to change anything
		case INFINITY_CROSSING:
			setFieldFocalLengthValue(minFocalLengthField, lens.getMinFocalLength()); //restore previous value
			return; //Cannot add the lens
		case OUTSIDE_MIN_FOCAL_POWER:
			setFieldFocalLengthValue(focalLengthField, minCurrentFocalLength);
			lens.setFocalLength(minCurrentFocalLength);
			propagateABCDMatrices();
			break;
		case OUTSIDE_MAX_FOCAL_POWER:
			assert(false); //Should be impossible
		}
		
		lens.setMinFocalLength(minCurrentFocalLength);
		setTuneLensBar(lens);
		refreshCanvasFlag = true;
	}
	
	
	/**
	 * Changes the focal length of the selected optic, if we have a lens selected.
	 */
	private void changeFocalLength() {
		if (! (selectedOptic instanceof Lens)) return; //We can't set the focal length if we don't have a lens selected
		double focalLength = getDoubleFromField(focalLengthField);
		
		if (selectedOptic instanceof TunableLens) {
			TunableLens lens = (TunableLens) selectedOptic;
			
			switch (checkFocalLengthRange(lens.getMinFocalLength(), lens.getMaxFocalLength(), focalLength)) {
			case OK:
				break; //We don't need to change anything
			case INFINITY_CROSSING:
				setFieldFocalLengthValue(focalLengthField, lens.getFocalLength()); //Restore previous value in box
				return; //Cannot change the focal length to that value
			case OUTSIDE_MIN_FOCAL_POWER:
				setFieldFocalLengthValue(focalLengthField, lens.getMinFocalLength());
				focalLength = lens.getMinFocalLength();
				break;
			case OUTSIDE_MAX_FOCAL_POWER:
				setFieldFocalLengthValue(focalLengthField, lens.getMaxFocalLength());
				focalLength = lens.getMaxFocalLength();
				break;
			}
		}
		((Lens) selectedOptic).setFocalLength(focalLength);
		
		if (selectedOptic instanceof TunableLens) setTuneLensBar((TunableLens) selectedOptic);
		propagateABCDMatrices();
		refreshCanvasFlag = true;
	}
	
	
	/**
	 * Changes the position of the selected optic, if one is selected and this is a valid move.
	 */
	private void changePosition() {
		if ((selectedOptic != null) && (! (selectedOptic instanceof Source)) 
				&& checkIfFarEnoughFromOtherOptics(positionField.getValue(), selectedOptic)) {
			selectedOptic.setPosition(positionField.getValue());
			sortOpticsList();
			propagateABCDMatrices();
			refreshCanvasFlag = true;
		}
	}

	
	/**
	 * Changes the name of the selected optic.
	 */
	private void changeName() {
		if ((selectedOptic != null) && (! (selectedOptic instanceof Source))) {
			String name = nameField.getText().trim();
			if (checkForNamingCollision(name, null)) { //If we are good and have no naming collisions
				selectedOptic.setName(name);
				refreshCanvasFlag = true;
			}
		}
	}
	
	
	/**
	 * Removes the selected optic, if there is an optic to remove, from the screen.
	 */
	private void removeSelectedOptic() {
		if (selectedOptic != null && (! (selectedOptic instanceof Source))) {
			synchronized (lock) {
				opticsList.remove(selectedOptic);
			}
			deselectAll();
			propagateABCDMatrices();
			refreshCanvasFlag = true;
		}
	}
	
	
	/**
	 * Called whenever a new file is opened to prepare settings.
	 */
	private void refreshForNewFile() {
		selectedOptic = null;
		leftEdge = DEFAULT_LEFT_EDGE;
		rightEdge = DEFAULT_RIGHT_EDGE;
		propagateABCDMatrices();
		refreshCanvasFlag = true;
	}
	
	
	/**
	 * Called whenever timer triggers a screen refresh. 
	 * Checks if screen needs to be refreshed and refreshes if necessary.
	 */
	private void respondToTimer() {
		if (refreshCanvasFlag) {
			timer.stop();
			canvas.removeAll();
			refreshCanvas();
			refreshCanvasFlag = false;
			timer.restart();
		}
	}
	
	
	/**
	 * Sets the tune lens bar.
	 * @param lens The lens of interest.
	 */
	private void setTuneLensBar(TunableLens lens) {
		double maxFocalPower = 1000.0 / lens.getMaxFocalLength();
		double minFocalPower = 1000.0 / lens.getMinFocalLength();
		double currentFocalPower = 1000.0 / lens.getFocalLength();
		ignoreChangeEventFlag = true;
		int value = (int) (100 * (currentFocalPower - minFocalPower) / (maxFocalPower - minFocalPower));
		tuneLens.setValue(value);
	}
	
	
	/**
	 * Resets all values in fields corresponding to focal length or focal power to correspond to change in mode.
	 */
	private void invertValuesInFields() {
		if (selectedOptic instanceof Lens) {
			setFieldFocalLengthValue(focalLengthField, ((Lens) selectedOptic).getFocalLength());
		} else {
			setFieldFocalLengthValue(focalLengthField, DEFAULT_FOCAL_LENGTH);
		}
		
		if (selectedOptic instanceof TunableLens) {
			TunableLens t = (TunableLens) selectedOptic;
			setFieldFocalLengthValue(minFocalLengthField, t.getMinFocalLength());
			setFieldFocalLengthValue(maxFocalLengthField, t.getMaxFocalLength());
		} else {
			setFieldFocalLengthValue(minFocalLengthField, DEFAULT_MIN_FOCAL_LENGTH);
			setFieldFocalLengthValue(maxFocalLengthField, DEFAULT_MAX_FOCAL_LENGTH);
		}
	}
	
	
	/**
	 * Calculates value of q_in at beginning of setup.
	 * @return q_in
	 */
	private Complex getQIn() {
		return new Complex(0, Math.PI * Math.pow(collWaist,2) / (wavelength * Math.pow(10, -6)));
	}
	
	
	/**
	 * Checks whether the optics are spaced far enough apart.
	 * @param position The position at which to add a new optic.
	 * @param name The name for which to check for naming collisions.
	 * @param toIgnore One OpticsHardware to ignore (typically the optic in question if already added to list). Can be 
	 * null if you wish to consider all optics in list.
	 * @return Whether the position is valid.
	 */
	private boolean checkIfFarEnoughFromOtherOptics(double position, OpticsHardware toIgnore) {
		synchronized (lock) {
			for (OpticsHardware o: opticsList) {
				if (( o != toIgnore)) {
					if (Math.abs(position - o.getPosition()) < MIN_DISTANCE_BETWEEN_OPTICS + Math.pow(10, -8)) 
						return false;
				}
			}
			return true;
		}
	}
	
	
	/**
	 * Checks for a naming collision between optics.
	 * @param name The name to check.
	 * @param toIgnore An optic to ignore, or null if you want all optics considered.
	 * @return True if safe to add optic, false if naming collision.
	 */
	private boolean checkForNamingCollision(String name, OpticsHardware toIgnore) {
		name = name.trim();
		if (name == "") return false;
		synchronized (lock) {
			for (OpticsHardware o: opticsList) {
				if (o != toIgnore && o.getName().equals(name)) return false;
			}
			return true;
		}
	}
	
	
	/**
	 * Sorts the optics list by position.
	 */
	private void sortOpticsList() {
		synchronized (lock) {
			Collections.sort(opticsList);
		}
	}

	
	/**
	 * Called whenever the mouse is moved while being held down. We care about this only when an optic is selected
	 * or we are trying to get a ROI.
	 */
	public void mouseDragged(MouseEvent e) {
		if (findWaistButton.isSelected()) {
			updateROI(e);
		}
	}
	
	
	/**
	 * Called whenever the mouse button is released. We care about this only when user is trying to select the ROI.
	 */
	public void mouseReleased(MouseEvent e) {
		if (findWaistButton.isSelected() && ROI != null) {
			double waistPosition = findWaist();
			if (Double.isNaN(waistPosition)) { //Sentinel returned if no waist detected
				calcWaistPositionLabel.setText("No waist");
				calcWaistLabel.setText("No waist");
			} else {
				calcWaistPositionLabel.setText(Double.toString(waistPosition));
				calcWaistLabel.setText(Double.toString(beamParametersAtPoint(waistPosition, getQIn()).getY()));
			}
		}
	}
	
	
	/**
	 * Locates the position of the waist to within tolerance of MIN_RESOLUTION_FOR_WAIST_POSITION
	 * @return The location of the waist within the ROI, or NaN if there is no waist.
	 */
	private double findWaist() {
		assert (ROI != null); //Throws exception if for some reason we don't have a ROI.
		double start = canvasToSpacialX(ROI.getX());
		double end = canvasToSpacialX(ROI.getX() + ROI.getWidth());
		end += ((double) (end-start)) / (CALC_WAIST_ARRAY_SIZE - 1);
		Complex q_in = getQIn();
		
		while (end - start > MIN_RESOLUTION_FOR_WAIST_POSITION) {
			double[] arr = new double[CALC_WAIST_ARRAY_SIZE];
			double interval = ((double)(end - start)) / (CALC_WAIST_ARRAY_SIZE - 1);
			double position = start;
			for (int i = 0; i < CALC_WAIST_ARRAY_SIZE; i++) {
				arr[i] = beamParametersAtPoint(position, q_in).getX(); //Beam radius of curvature
				position += interval;
			}
			int index = getIndexOfSignChangeInArray(arr);
			if (index == -1) return Double.NaN; //We have no waist
			start = start + index * interval;
			end = start + interval;
		}
		
		double finalWaist = start + (end - start) / 2.0;
		
		//Check to ensure we aren't just finding a negative focal length lens.
		synchronized (lock) {
			for (OpticsHardware o: opticsList) {
				if (Math.abs(o.getPosition() - finalWaist) <= MIN_RESOLUTION_FOR_WAIST_POSITION) return Double.NaN;
			}
		}
		
		return finalWaist;
	}
	
	
	/**
	 * Locates the index at which the sign of values in the array changes from negative to positive.
	 * @param arr The array which to analyze.
	 * @return The index at which the sign changes, or -1 if no such sign change.
	 */
	private int getIndexOfSignChangeInArray(double[] arr) {
		for (int i = 0; i < arr.length - 1; i++) {
			if ((Math.signum(arr[i]) == -1) && (Math.signum(arr[i+1]) == 1)) return i;
		}
		return -1;
	}
	
	
	/**
	 * Called when the mouse is clicked on screen.
	 */
	public void mouseClicked(MouseEvent e) {
		if (ROI != null) { //Clears the ROI
			canvas.remove(ROI);
			ROI = null;
			findWaistButton.setSelected(false);
			canvas.repaint();
		}
		OpticsHardware o = getHardwareAt(e.getX());
		if (o == null || o instanceof Source || o.isSelected()) {
			boolean needToRefresh = (selectedOptic != null);
			if (o instanceof TunableLens) {
				tuneLens.setVisible(false);
			}
			deselectAll();
			if (needToRefresh) {
				refreshCanvasFlag = true;
			}
		} else {
			deselectAll();
			o.setSelected(true);
			selectedOptic = o;
			refreshCanvasFlag = true;
			fillFields(selectedOptic);
			if (o instanceof TunableLens) {
				tuneLens.setVisible(true);
			}
			cursorPositionField.setValue(o.getPosition());
			updateParamsAtPointLabels(o.getPosition());
			if (o instanceof Lens) radiusOfCurvatureLabel.setText(" "); //Radius of curvature discontinuous at lens!
		}
	}
	
	
	/**
	 * Updates the ROI when user moves mouse.
	 * @param e The MouseEvent called by moving the mouse
	 */
	private void updateROI(MouseEvent e) {
		if (ROI == null) {
			ROI = new GRect(e.getX(), canvas.getHeight() - CENTERLINE_HEIGHT - SELECT_BOX_HEIGHT / 2.0,
					SELECT_BOX_DEFAULT_WIDTH, SELECT_BOX_HEIGHT);
			ROI.setFilled(true);
			ROI.setColor(Color.YELLOW);
			canvas.add(ROI);
			ROI.sendToBack();
		} else { //If ROI already exists
			double position = e.getX();
			double leftEdge = ROI.getX();
			if (position > leftEdge + SELECT_BOX_DEFAULT_WIDTH) {
				ROI.setBounds(ROI.getX(), ROI.getY(), (position - leftEdge), SELECT_BOX_HEIGHT);
			}
		}
		canvas.repaint();
	}
	
	
	/**
	 * Fills the relevant fields with information for an optic.
	 * @param o
	 */
	private void fillFields(OpticsHardware o) {
		nameField.setText(o.getName());
		positionField.setValue(o.getPosition());
		if (o instanceof Lens) { //Includes tunable lenses
			setFieldFocalLengthValue(focalLengthField, ((Lens) o).getFocalLength());
		}
		if (o instanceof TunableLens) {
			TunableLens t = (TunableLens) o;
			setFieldFocalLengthValue(minFocalLengthField, t.getMinFocalLength());
			setFieldFocalLengthValue(maxFocalLengthField, t.getMaxFocalLength());
			setTuneLensBar(t);
		}
	}
	
	
	/**
	 * Deselects all objects on the canvas.
	 */
	private void deselectAll() {
		if (selectedOptic != null) {
			if (selectedOptic instanceof TunableLens) tuneLens.setVisible(false);
			selectedOptic.setSelected(false);
			selectedOptic = null;
		}
	}
	
	
	/**
	 * Gets an OpticsHardware object closest to the current position.
	 * @param position The position of the click.
	 * @return The OpticsHardware closest to that position, or null if no hardware clicked.
	 */
	private OpticsHardware getHardwareAt(double position) {
		GObject obj = canvas.getElementAt(position, canvas.getHeight() - CENTERLINE_HEIGHT);
		if (obj == null || obj instanceof GLabel || obj instanceof GLine) return null; 
		//Return null if we clicked somewhere that isn't a piece of hardware
		GRectangle bounds = obj.getBounds();
		double opticsPosition = canvasToSpacialX(bounds.getX() + bounds.getWidth() / 2.0);
		//Find optics closest to either side of the clicked point
		return locateClosestOptic(opticsPosition);
	}
	
	
	/**
	 * Locates the closest optic to a position in space
	 * @param spacialPosition The position clicked in spacial coordinates
	 * @return The closest optic
	 */
	private OpticsHardware locateClosestOptic(double spacialPosition) {
		double smallestDistance = Double.POSITIVE_INFINITY;
		OpticsHardware closestOptic = null;
		synchronized (lock) {
			for (OpticsHardware o: opticsList) {
				if (Math.abs(spacialPosition - o.getPosition()) < smallestDistance) {
					closestOptic = o;
					smallestDistance = Math.abs(spacialPosition - o.getPosition());
				}
			}
			return closestOptic;
		}
	}
	

	/**
	 * Updates cursor position labels when mouse is moved.
	 */
	public void mouseMoved(MouseEvent e) {
		double point = canvasToSpacialX(e.getX());
		cursorPositionField.setValue(point);
		updateParamsAtPointLabels(point);
	}
	
	
	/**
	 * Updates the radius and radius of curvature labels to selected point.
	 * @param position The point in space.
	 */
	private void updateParamsAtPointLabels(double position) {
		if (position < 0) {
			radiusOfCurvatureLabel.setText(" ");
			radiusLabel.setText(" ");
		} else {
			GPoint params = beamParametersAtPoint(position, getQIn());
			radiusOfCurvatureLabel.setText(Double.toString(params.getX()));
			radiusLabel.setText(Double.toString(params.getY()));
		}
	}
	
	
	/**
	 * Clears the entire canvas.
	 */
	private void clearAll() {
		deselectAll();
		calcWaistLabel.setText(" ");
		calcWaistPositionLabel.setText(" ");
		ROI = null;
		synchronized (lock) {
			opticsList.clear();
			opticsList.add(new Source(0));
		}
		propagateABCDMatrices();
		refreshCanvas();
	}
	
	
	/**
	 * Gets the value of a double from a DoubleField object. Used for the focal length fields because existing reads
	 * from the DoubleField class do not adequately handle infinities that may arise. Also appropriately handles the fact
	 * that the text in the field is changed when we go from focal length mode to focal power mode.
	 * @param field The DoubleField from which to read the text
	 * @return The value from the field.
	 */
	private double getDoubleFromField(DoubleField field) {
		String text = field.getText();
		double value;
		if (text.equalsIgnoreCase("inf") || text.equalsIgnoreCase("infinity")) {
			value = Double.POSITIVE_INFINITY;
		} else {
			value = field.getValue();
		}
		
		if (focalPowerMode) return 1000.0 / value;
		return value;
	}
	
	
	/**
	 * Checks bounds given focal power.
	 * @param minFocalPower The minimum focal power of a tunable lens.
	 * @param maxFocalPower The maximum focal power of a tunable lens.
	 * @param focalPower The current focal power of the lens.
	 * @return OK if there are no problems, error code otherwise.
	 */
	private BoundsCheck checkFocalPowerRange(double minFocalPower, double maxFocalPower, double focalPower) {
		if (minFocalPower > maxFocalPower) return BoundsCheck.INFINITY_CROSSING;
		if (focalPower < minFocalPower) return BoundsCheck.OUTSIDE_MIN_FOCAL_POWER;
		if (focalPower > maxFocalPower) return BoundsCheck.OUTSIDE_MAX_FOCAL_POWER;
		return BoundsCheck.OK;
	}

	
	/**
	 * Checks bounds given focal lengths.
	 * @param minCurrentFocalLength The focal length at minimum current.
	 * @param maxCurrentFocalLength The focal length at maximum current.
	 * @param focalLength The current focal length of the lens.
	 * @return OK if there are no problems, error code otherwise.
	 */
	private BoundsCheck checkFocalLengthRange(double minCurrentFocalLength, 
			double maxCurrentFocalLength, double focalLength) {
		return checkFocalPowerRange(1.0 / minCurrentFocalLength, 1.0 / maxCurrentFocalLength, 1.0 / focalLength);
	}

	
	/**
	 * Adds a new tunable lens to the screen and optics list
	 */
	private void addTunableLens() {	
		deselectAll();
		refreshCanvasFlag = true;
		double position  = positionField.getValue();
		double minCurrentFocalLength = getDoubleFromField(minFocalLengthField);
		double maxCurrentFocalLength = getDoubleFromField(maxFocalLengthField);
		double focalLength = getDoubleFromField(focalLengthField);
		String name = nameField.getText().trim();
		if (checkIfFarEnoughFromOtherOptics(position, null) && checkForNamingCollision(name, null)) {
			switch (checkFocalLengthRange(minCurrentFocalLength, maxCurrentFocalLength, focalLength)) {
			case OK:
				break; //We don't need to change anything
			case INFINITY_CROSSING:
				return; //We cannot add a lens because such a lens is impossible
			case OUTSIDE_MAX_FOCAL_POWER:
				setFieldFocalLengthValue(focalLengthField, maxCurrentFocalLength);
				focalLength = maxCurrentFocalLength;
				break;
			case OUTSIDE_MIN_FOCAL_POWER:
				setFieldFocalLengthValue(focalLengthField, minCurrentFocalLength);
				focalLength = minCurrentFocalLength;
				break;
			}
			
			TunableLens lens = new TunableLens(position, minCurrentFocalLength, maxCurrentFocalLength, focalLength, name);
			synchronized (lock) {
				opticsList.add(lens);
			}
			lens.setSelected(true);
			selectedOptic = lens;
			sortOpticsList();
			propagateABCDMatrices();
			setTuneLensBar(lens);
			tuneLens.setVisible(true);
			refreshCanvasFlag = true;
		}
	}
	
	
	/**
	 * Adds a fixed lens to screen and optics list.
	 */
	private void addLens() {
		deselectAll();
		refreshCanvasFlag = true;
		double position = positionField.getValue();
		String name = nameField.getText();
		if (checkIfFarEnoughFromOtherOptics(position, null) && (checkForNamingCollision(name, null))) {
			Lens lens = new Lens(position, getDoubleFromField(focalLengthField), name);
			synchronized (lock) {
				opticsList.add(lens);
			}
			sortOpticsList();
			propagateABCDMatrices();
			lens.setSelected(true);
			selectedOptic = lens;
			refreshCanvasFlag = true;
		}
	}
	
	
	/**
	 * Called externally when the canvas is resized.
	 */
	public void componentResized(ComponentEvent e) {
		if (e.getSource() == canvas) refreshCanvasFlag = true;
	}
	
	
	/**
	 * Empty methods within ComponentListener interface. Program should do nothing when these ComponentEvents occur.
	 */
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	

	
	/* Enumerated types */
	
	/* Codes that checkFocalLengthRange method can return */
	public enum BoundsCheck {
		OK,  //Valid range
		INFINITY_CROSSING, //Invalid range, focal power range crosses infinity. Equivalent to focal length crossing 0.
		OUTSIDE_MIN_FOCAL_POWER, //Invalid range, focal power outside minimum allowed by bounds
		OUTSIDE_MAX_FOCAL_POWER //Invalid range, focal power outside maximum allowed by bounds
		}
	
	
	/* Private instance variables */
	
	/* The canvas */
	private GCanvas canvas = new GCanvas();
	
	/* Interactors */
	private DoubleField wavelengthField = new DoubleField(DEFAULT_WAVELENGTH);
	private DoubleField collWaistField = new DoubleField(DEFAULT_COLLWAIST);
	private JButton leftScroll;
	private JButton rightScroll;
	private JButton zoomPlus;
	private JButton zoomMinus;
	private JButton addLensButton;
	private JButton addTunableLensButton;
	private JButton addPOIButton;
	private JButton removeButton;
	private JTextField nameField = new JTextField(TEXT_FIELD_SIZE);
	private DoubleField focalLengthField = new DoubleField();
	private DoubleField positionField = new DoubleField();
	private DoubleField minFocalLengthField = new DoubleField();
	private DoubleField maxFocalLengthField = new DoubleField();
	private JButton saveButton;
	private JButton saveAsButton;
	private JButton openButton;
	private JSlider apparentBeamDiameter = new JSlider();
	private JSlider tuneLens = new JSlider();
	private DoubleField cursorPositionField = new DoubleField();
	private JLabel radiusOfCurvatureLabel;
	private JLabel radiusLabel;
	private JToggleButton findWaistButton = new JToggleButton("Find Waist");
	private JLabel calcWaistPositionLabel = new JLabel(" ");
	private JLabel calcWaistLabel = new JLabel(" ");
	private JRadioButton focalLengthModeButton = new JRadioButton("Focal length mode");
	private JRadioButton focalPowerModeButton = new JRadioButton("Focal power mode");
	private JLabel focalLengthLabel = new JLabel("Focal length");
	private JLabel minCurrentFocalLengthLabel = new JLabel("Focal Length at Min Current");
	private JLabel maxCurrentFocalLengthLabel = new JLabel("Focal Length at Max Current");
	private javax.swing.Timer timer;
	private JButton clearButton;
	private JButton newButton;
	
	
	/* List of optics */
	private ArrayList<OpticsHardware> opticsList = new ArrayList<OpticsHardware>();
	
	
	/* Lists the ABCD matrix of the entire system immediately past the optic */
	private ArrayList<ABCDMatrix> matrixList = new ArrayList<ABCDMatrix>();
	
	
	/* Beam radius at collimated source */
	private volatile double collWaist = DEFAULT_COLLWAIST;
	
	/* Wavelength */
	private volatile double wavelength = DEFAULT_WAVELENGTH;
	
	/* Area on screen */
	private volatile double leftEdge = DEFAULT_LEFT_EDGE;
	private volatile double rightEdge = DEFAULT_RIGHT_EDGE;
	
	/* Flag that indicates whether the screen needs to be refreshed.
	 * To ensure speed, we don't want to refresh screen too frequently. */
	private boolean refreshCanvasFlag = false;
	
	/* Current scale factor for beam radius, pixels per mm */
	private volatile double radiusScaleFactor = DEFAULT_RADIUS_SCALE_FACTOR;
	
	/* The selected optic, or null if none is selected */
	private OpticsHardware selectedOptic = null;
	
	/* The name to save the current file to. Defaults to null. */
	private String saveName = null;
	
	/* The rectangle holding the region of interest for finding the waist. */
	private GRect ROI = null;
	
	/* Flag indicating whether to ignore ChangeEvents for the tunable lens slider. */
	private boolean ignoreChangeEventFlag = false;
	
	/* Whether the program is in focalpower mode */
	//True: in focalpower mode 1/f
	//False: in focal length mode
	private boolean focalPowerMode = false;
	
	
	
	/* Constants */
	private static final int TEXT_FIELD_SIZE = 10;
	private static final double DEFAULT_WAVELENGTH = 780;
	private static final double DEFAULT_COLLWAIST = 1.0;
	private static final int WINDOW_DEFAULT_SIZE_X = 1350;
	private static final int WINDOW_DEFAULT_SIZE_Y = 700;
	private static final int RESIZE_PAUSE_TIME = 10; //Allow screen to resize before adding GUI
	private static final double DEFAULT_LEFT_EDGE = -20;
	private static final double DEFAULT_RIGHT_EDGE = 1020;
	private static final double SCROLL_FRACTION = 0.3; //Fraction of screen to scroll
	private static final int CANVAS_REFRESH_PAUSE_TIME = 10; //ms
	private static final int RULER_Y_FROM_BOTTOM = 85; //pixels from bottom
	private static final int LABEL_Y_FROM_BOTTOM = 65; //pixels from bottom
	private static final int RULER_TITLE_Y_FROM_BOTTOM = 45; //pixels from bottom
	private static final int RULER_X_FROM_EDGE = 50; //pixels from left and right edges
	private static final int RULER_LARGE_TICK_HEIGHT = 20; //pixels
	private static final int RULER_SMALL_TICK_HEIGHT = 8; //pixels
	private static final int CENTERLINE_HEIGHT = 350; //pixels
	private static final int BEAM_PIXEL_RESOLUTION = 2; //pixels
	private static final int DEFAULT_RADIUS_SCALE_FACTOR = 10; //pixels per mm
	private static final double MIN_SCREEN_WIDTH = 10; //mm
	private static final double MAX_SCREEN_WIDTH = 10000; //mm
	private static final String EXTENSION = "gwb"; //Extension at end of filename
	private static final double MIN_DISTANCE_BETWEEN_OPTICS = 0.4; //mm
	private static final int SOURCE_WIDTH = 50;
	private static final int SOURCE_HEIGHT = 150;
	private static final int LENS_WIDTH = 10;
	private static final int LENS_HEIGHT = 150;
	private static final int POI_WIDTH = 4;
	private static final int POI_HEIGHT = 150;
	private static final int SELECT_BOX_HEIGHT = 170;
	private static final int SELECT_BOX_DEFAULT_WIDTH = 20;
	private static final double MIN_RESOLUTION_FOR_WAIST_POSITION = Math.pow(10, -10); //mm
	private static final int CALC_WAIST_ARRAY_SIZE = 20;
	private static final double DEFAULT_FOCAL_LENGTH = 100;
	private static final double DEFAULT_POSITION = 100;
	private static final double DEFAULT_MIN_FOCAL_LENGTH = 150;
	private static final double DEFAULT_MAX_FOCAL_LENGTH = 50;
	private static final int LABEL_SEPARATION_FROM_CENTERLINE = 100;
	private static final int LABEL_SEPARATION = 20;
	private static final int OPTICS_LABEL_X_OFFSET = 30; //pixels
	
	/* Serial Version UID */
	public static final long serialVersionUID = 1L;
	
	/* Locks */
	private Object lock = new Object();
	
}
