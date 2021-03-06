package engine.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import engine.datastructures.Vector3;
import engine.input.Input;
import engine.input.Keys;
import engine.terrain.Terrain;

public class GameWrapper extends JPanel
{
	// Fields.
	private static final long serialVersionUID = 1L;

	private JFrame frame;

	private GameLoopThread updateThread;
	private GameLoopThread renderThread;
	
	Object renderSyncObject = new Object();

	boolean waitingOnPaint = false;
	boolean finishedPaint = true;

	BufferedImage backBuffer;
	BufferedImage frontBuffer;
	BufferedImage paintBuffer;

	boolean isFullScreen = false;

	Dimension panelDimension = new Dimension(1, 1);
	Vector3 scaleFactor = new Vector3(1, 1, 1);

	Dimension worldDimension = new Dimension(1920, 1080);
	Dimension desiredDimension = new Dimension(1366, 720);

	// Constructors.
	public GameWrapper()
	{
		updateThread = new UpdateThread(this, 60);
		renderThread = new RenderThread(this, 70);

		RenderStateManager.updateThreadID = updateThread.getId();
		RenderStateManager.renderThreadID = renderThread.getId();

		frame = new JFrame("GameWrapper");

		frame.addWindowListener(new WindowAdapter()
		{

			public void windowClosing(WindowEvent e)
			{
				updateThread.exitThread();
				renderThread.exitThread();

				try
				{
					updateThread.join();
					renderThread.join();
				} catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}

				System.exit(0);
			}
		});

		frame.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				panelDimension = getSize();
				calculateScaleFactor();

				synchronized (renderSyncObject)
				{
					paintBuffer = frontBuffer;
				}
			}
		});

		this.setFocusable(true);
		this.requestFocusInWindow();

		initializeWindow();
		RenderStateManager.initializeStates();
		initializeKeyboardInput();
		initializeMouseInput();

		initializeGraphicsContent();
		initializeAudioContent();

		synchronized (renderSyncObject)
		{
			paintBuffer = frontBuffer;
		}

	}

	// Methods.
	void update(GameTime gameTime)
	{

		RenderStateManager.startUpdatingState();

		// Fizika.

		// Input.
		Input.SwitchStates();

		if (Input.isKeyPressed(Keys.Escape))
		{
			exit();
		}

		God.TransformManager.update(gameTime);
		God.ScriptManager.update(gameTime);

		RenderStateManager.finishUpdatingState();
	}

	public void paint(Graphics g)
	{
		synchronized (renderSyncObject)
		{
			finishedPaint = false;
			paintBuffer = frontBuffer;
		}

		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		if (paintBuffer != null)
			g2d.drawImage(paintBuffer, 0, 0, this);

		synchronized (renderSyncObject)
		{
			finishedPaint = true;

			if (waitingOnPaint)
				renderSyncObject.notifyAll();
		}
	}

	void render(Graphics2D g2d, GameTime gameTime)
	{
		RenderStateManager.startRenderState();

		g2d.scale(scaleFactor.x, scaleFactor.y);

		Terrain.renderTerrain(g2d);
		God.RenderingManager.render(g2d, gameTime);

		RenderStateManager.finishRenderState();

	}

	protected final void start()
	{
		updateThread.start();
		renderThread.start();
	}

	public void exit()
	{
		updateThread.exitThread();
		renderThread.exitThread();

		System.exit(0);
	}

	private void initializeWindow()
	{
		panelDimension = Toolkit.getDefaultToolkit().getScreenSize();

		frame.getContentPane().add(this);

		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLocation(0, 0);
		frame.setSize(desiredDimension.width, desiredDimension.height);

		calculateScaleFactor();

		frame.addKeyListener(Input.Instance());

		frame.setVisible(true);
	}

	private void calculateScaleFactor()
	{
		scaleFactor.x = (float) panelDimension.width
				/ (float) worldDimension.width;
		scaleFactor.y = (float) panelDimension.height
				/ (float) worldDimension.height;
		scaleFactor.z = 1;

		God.setWorldScaleFactor(scaleFactor);
	}

	private void initializeKeyboardInput()
	{
		this.addKeyListener(Input.Instance());

		Input.Instance().setFocusable(true);
		Input.Instance().requestFocusInWindow();
	}

	private void initializeMouseInput()
	{
		this.addMouseListener(Input.Instance());
		this.addMouseMotionListener(Input.Instance());

		Input.Instance().setFocusable(true);
		Input.Instance().requestFocusInWindow();
	}

	private void initializeGraphicsContent()
	{
		God.GraphicsContent.loadSprites("resources/images/images.sprite");
		God.GraphicsContent
				.loadSpriteSheets("resources/spritesheets/spriteSheets.spritesheet");
		God.ModelFactory.loadModels("resources/models/models.model");

		Terrain.initializeTerrain();
	}

	private void initializeAudioContent()
	{

	}

	public int getScreenWidth()
	{
		return panelDimension.width;
	}

	public int getScreenHeight()
	{
		return panelDimension.height;
	}

	public int getWorldWidth()
	{
		return worldDimension.width;
	}

	public int getWorldHeight()
	{
		return worldDimension.height;
	}

}
