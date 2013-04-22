package edu.msu.project3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DrawingView extends View {
    
    /**
     * Paint to set when different color/line width is selected
     */
    private Paint currentPaint;

    private float x;
    private float y;
	// this list contains all the Drawings that should be shown in the view
	//private ArrayList<Drawing> drawings = new ArrayList<Drawing>();
    
    /**
     * The picture we are drawing
     */
	private Picture picture = new Picture();
	
	private transient Drawing currentDrawing = null;
	
	public DrawingView(Context context) {
		super(context);
		init(context);
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DrawingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	/**
     * Initialize the view
     * @param context
     */
    private void init(Context context) {
    	initializeCurrentPaint(Color.BLACK, (float)8);
    }
    
    /**
     * Create new paint
     */
    private void initializeCurrentPaint(int color, float width) {
    	picture.AddDrawing(currentDrawing);
    	currentDrawing = new Drawing();
    	currentPaint = new Paint();
        currentPaint.setColor(color);
        currentPaint.setStrokeWidth(width);
    	currentDrawing.setLinePaint(currentPaint);
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.translate(picture.getOffsetX(), picture.getOffsetY());
		
		for (Drawing drawing : picture.getDrawings())
			if (drawing != null)
				drawing.DrawLine(canvas);
		if (currentDrawing != null) 
			currentDrawing.DrawLine(canvas);
		
		canvas.scale(picture.getScale(), picture.getScale());
	}
	
	/** 
	 * Handle a move event
	 * @param newx X coordinate
	 * @param newy Y coordinate
	 */
	public void onMove(float newx, float newy) {
		x = newx;
		y = newy;
		// If old picture is null, create one
	    if (currentDrawing == null)
	    {
		    // Create a new drawing
			currentDrawing = new Drawing();
			currentDrawing.setLinePaint(currentPaint);
	    }
	    // Add a point
		currentDrawing.addPoint(x, y);
		
        invalidate();
	}
    
	public int getCurrentPaintColor() {
		return currentPaint.getColor();
	}

	public void setCurrentPaintColor(int color) {
		initializeCurrentPaint(color, currentPaint.getStrokeWidth());
	}
    
	public float getCurrentPaintWidth() {
		return currentPaint.getStrokeWidth();
	}

	public void setCurrentPaintWidth(float width) {
		initializeCurrentPaint(currentPaint.getColor(), width);
	}
	
	/**
	 * Alex's successful attempts at serializing picture
	 */
	public void putDrawings(Intent intent) {
		intent.putExtra("PICTURE", picture);
	}
	
	public void getDrawings(Intent intent) {
		if (intent.getSerializableExtra("PICTURE") != null) {
			picture = (Picture)intent.getSerializableExtra("PICTURE");
		}
	}
	
	/**
     * Save the view state to a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to save to
     */
    public void putToBundle(String key, Bundle bundle) {
    	bundle.putSerializable(key, picture);
    }
    
    /**
     * Get the view state from a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to load from
     */
    public void getFromBundle(String key, Bundle bundle) {
    	picture = (Picture)bundle.getSerializable(key);
    }

    public void clear() {
    	picture.clear();
    	currentDrawing = new Drawing();
    	currentDrawing.setLinePaint(currentPaint);
    	invalidate();
    }
}
