package test;

import engine.components.ScriptComponent;
import engine.core.Actor;
import engine.core.GameTime;
import engine.datastructures.Vector3;
import engine.input.Input;
import engine.input.Keys;
import engine.utility.MathHelper;

public class PlayerControl
extends ScriptComponent
{

	private float moveSpeed = 50.0f;
	private boolean relative = true;
	
	public
	PlayerControl(Actor parent)
	{
		super(parent);
	}

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(GameTime gameTime)
	{
		float forward = 0;
		float right = 0;
		
		if (Input.isKeyDown(Keys.W))
			forward -= 1;
		if (Input.isKeyDown(Keys.S))
			forward += 1;
		if (Input.isKeyDown(Keys.A))
			right -= 1;
		if (Input.isKeyDown(Keys.D))
			right += 1;
		if (forward * forward + right * right > 1)
		{
			forward /= Math.sqrt(2);
			right /= Math.sqrt(2);
		}
		
		System.out.println(forward +", " + right);
		
		float rotation = 0;
		if (Input.isKeyDown(Keys.Q))
			rotation -= 0.1;
		if (Input.isKeyDown(Keys.E))
			rotation += 0.1;
		if(rotation != 0)
			parent.getTransformComponent().rotate(rotation);
		
		
		Vector3 moveVector = null;
		
		if(relative == true)
		{
			Vector3 forwardMoveVector = parent.getTransformComponent().getFront();
			Vector3 rightMoveVector = parent.getTransformComponent().getRight();
			
			forwardMoveVector.mul(-forward);
			rightMoveVector.mul(right);
			moveVector = forwardMoveVector.add(rightMoveVector);
			moveVector.normalize();
		}
		else
		{
			moveVector = new Vector3(right, forward, 0);
		}
		
		moveVector.mul(moveSpeed * gameTime.dt_s());
		parent.getTransformComponent().translate(moveVector);
		
//		moveDistance.rotate(-(float)parent.getTransformComponent().getRotation());
//		moveDistance.mul(moveSpeed * gameTime.dt_s());
//		parent.getTransformComponent().translate(moveDistance);
		
//		System.out.println(moveDistance);
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
