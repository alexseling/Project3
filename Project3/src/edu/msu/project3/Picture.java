package edu.msu.project3;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Picture implements Serializable {

	/**
	 * Compiler generated serialID
	 */
	private static final long serialVersionUID = -7698564561406540923L;
	
	/**
	 *  An array of the Drawings that make up the Picture
	 */
	private ArrayList<Drawing> drawings = new ArrayList<Drawing>();
	
	private float angle = 0;
    private float scale = 1;
    private float offsetX = 0;
    private float offsetY = 0;
	
	public Picture() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Add drawing to array of drawings
	 * @param drawing to be added to array
	 */
	public void AddDrawing(Drawing drawing) {
		drawings.add(drawing);
	}
	
	/**
	 * Get drawing from the drawings array
	 * @param index of were the drawing is in the array
	 * @return the drawing at the given index
	 */
	public Drawing GetDrawing(int index) {
		return drawings.get(index);
	}

	/**
	 * Get array containing drawing
	 * @return drawings the array containing the drawings
	 */
	public ArrayList<Drawing> getDrawings() {
		return drawings;
	}

	/**
	 * Set the array of drawings
	 * @param drawings the array of drawings
	 */
	public void setDrawings(ArrayList<Drawing> drawings) {
		this.drawings = drawings;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		//in.registerValidation(this, 0);
		in.defaultReadObject();
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getOffsetX() {
		return offsetX;
	}
	
	public void clear() {
		drawings.clear();
	}
}
