package engine.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class GraphicsContent
{
	private HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	private HashMap<String, BufferedImage[]> spriteSheets = new HashMap<String, BufferedImage[]>();
	
	public void
	loadImage(String name, String path)
	{
		BufferedImage image = null;
		try 
		{
		    image = ImageIO.read(new File(path));
		    
		    images.put(name, image);
		} 
		catch (IOException e) 
		{
			System.err.println("File not found! Path: " + path);
		}
	}
	
	public void
	loadSpriteSheet(String name, String path, int xorigin, int yorigin, int width, int height, int rows, int columns, int count)
	{
		try 
		{
		    BufferedImage image = ImageIO.read(new File(path));
		    BufferedImage[] sprites = new BufferedImage[count];
		    
//		    int width = image.getWidth() / columns;
//		    int height = image.getHeight() / rows;
		    
			for (int i = 0; i < rows; i++)
			{
				for (int j = 0; j < columns; j++)
				{
					int index = i * columns + j;
					if (index >= count)
						break;
					
					sprites[index] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = (Graphics2D)sprites[index].getGraphics();
					g2d.drawImage(
						image, 
						0, 0, width, height, 
						xorigin + j * width, yorigin + i * height, xorigin + (j + 1) * width, yorigin + (i + 1) * height,
						null);
				}
			}
			
			spriteSheets.put(name, sprites);
		} 
		catch (IOException e) 
		{
			System.err.println("File not found! Path: " + path);
		}
	}
	
	public void
	unloadImage(String name)
	{
		images.remove(name);
	}
	
	public void
	unloadSpriteSheet(String name)
	{
		spriteSheets.remove(name);
	}
	
	public void
	unloadAllImages()
	{
		images.clear();
	}
	
	public void
	unloadAllSpriteSheets()
	{
		spriteSheets.clear();
	}
	
	public void
	unloadAllContent()
	{
		unloadAllImages();
		unloadAllSpriteSheets();
	}
	
	public BufferedImage
	getImage(String name)
	{
		BufferedImage image = images.get(name);
		
		if (image == null)
		{
		}
		
		return image;
	}
	
	public BufferedImage[]
	getSpriteSheet(String name)
	{
		BufferedImage[] spriteSheet = spriteSheets.get(name);
		
		if (spriteSheet == null)
		{
			System.err.println("SpriteSheet not found in graphics content! Name: " + name);
		}
		
		return spriteSheet;
	}
	
}
