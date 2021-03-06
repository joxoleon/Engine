package engine.components;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import engine.core.Actor;
import engine.core.God;
import engine.datastructures.Vector3;
import engine.graphics.Model;

public class RenderingComponent
extends GameComponent
 {
	//Fields
	boolean isLastRender = false;
	boolean drawForwardVector = true;
	
	private static Vector3 position;
	private static double rotation;
	private static Vector3 scale;
	
	public Model model;
	
	//Methods
	
	/** Initializes the Component, after removing it from the pool. */
	void 
	initialize(Actor parent, Model model)
	{
		this.parent = parent;
		this.model = model;
		this.setEnabled(true);
	}
	
	/** 
	 * Is called by the RenderingComponentManager.
	 * Calls the draw method from the Containing model.
	 * Saves and restores the graphics context during the drawing.
	 * 
	 * @param g2d
	 */
	public void
	render(Graphics2D g2d)
	{
		Model m = model;
		if (m != null)
		{
			AffineTransform aft = g2d.getTransform();
			
			position = parent.getTransformComponent().getPosition();
			rotation = parent.getTransformComponent().getRotation();
			scale = parent.getTransformComponent().getScale();
			
			g2d.translate(position.x, position.y);
			g2d.rotate(rotation);
			g2d.scale(scale.x, scale.y);
			
			
			m.render(g2d);
			
			if(drawForwardVector == true)
			{
				g2d.setPaint(Color.red);
				g2d.drawLine(0, 0, 0, -100);
			}
			g2d.setTransform(aft);
		}
	}
	
	@Override
	public String 
	getName()
	{
		return "RenderingComponent";
	}

	@Override
	protected void 
	destroyComponent()
	{
		God.RenderingManager.destroy(this);
		//Ukloni reference
	}
	
	protected final void 
	setLastRender()
	{
		isLastRender = true;
	}
	
	public final boolean
	isLastRender()
	{
		return isLastRender;
	}

	public Model
	getModel()
	{
		return model;
	}
	
	public void
	setModel(Model model)
	{
		this.model = model;
	}
 
	public static void
	reverseCurrentRotation(Graphics2D g2d)
	{
		g2d.rotate(-rotation);
	}
	
	public static void
	restoreCurrentRotation(Graphics2D g2d)
	{
		g2d.rotate(rotation);
	}
	

	
 }
