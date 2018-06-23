import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Main{
	
	static int screenHeight=720;
	static int screenWidth=1280;
	static JFrame Frame = new JFrame("Ablak");
	public static BufferedImage FrameBuffer;

	static JPanel panel = new JPanel() {
		private static final long serialVersionUID = 6552813087930574789L;

		long prevTime=0;
		@Override
		public void paintComponent(Graphics g) {
			
			Graphics fb = FrameBuffer.createGraphics();
			// ablak törlése
			fb.setColor(Color.lightGray);
			fb.fillRect(0, 0, this.getWidth(), this.getHeight());
			fb.dispose();
			//rajzolás elõtt a polygonokat közelségi sorrendbe is kéne tenni!
			tmpoly.update();
			tmpoly.draw(FrameBuffer);
			
			g.drawImage(FrameBuffer, 0, 0, null);
			
			//fps számláló
			g.setColor(Color.BLACK);
			double fps = 1000000000d/(System.nanoTime()-prevTime);
			g.drawString("FPS: "+(int)fps, 100, 100);
			prevTime=System.nanoTime();
			
			repaint();
			
			
		}
	};
	

	static Vector CameraPos = new Vector(0, -10, 0); // kezdeti kamera pozíció
	static Vector CameraAim = new Vector(0, 1, 0); // kezdeti kamera irány
	
	static Polygon3D tmpoly;
	
	public static void main(String[] args) {


		
		BufferedImage texture;
		try {
			texture = ImageIO.read(new File("grid.jpg"));
		} catch (Exception e) {
			
			e.printStackTrace();
			return;
		}
		
		
				
				
		// egyszerû polygon3d beállítás
		ArrayList<Vertex> vtmp = new ArrayList<>(4);
		
		vtmp.add(new Vertex(-1, 5,   1, 0, 0)); 
		vtmp.add(new Vertex(1, 15,  1, texture.getWidth()-1, 0)); 
		vtmp.add(new Vertex(1, 15, -1, texture.getWidth()-1, texture.getHeight()-1));
		vtmp.add(new Vertex(-1, 5 , -1, 0, texture.getHeight()-1));
		
		tmpoly=new Polygon3D(vtmp, texture);
		
		setupWindow();
		
		
		
	}
	
	public static void setupWindow() {
		Frame.setUndecorated(true);
		Frame.setSize(screenWidth, screenHeight);
		Frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Frame.add(panel);
		
		Frame.setVisible(true);
		Frame.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_S) {
					CameraPos.y -= 0.5f;
				}
				if(e.getKeyCode() == KeyEvent.VK_W) {
					CameraPos.y += 0.5;
				}
				
				if(e.getKeyCode() == KeyEvent.VK_A) {
					CameraPos.x -= 0.5;
				}
				if(e.getKeyCode() == KeyEvent.VK_D) {
					CameraPos.x += 0.5;
				}
				
				
				if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
					CameraPos.z -= 0.5;
				}
				if(e.getKeyCode() == KeyEvent.VK_SPACE) {
					CameraPos.z += 0.5;
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			
			
		});
		
		FrameBuffer=new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
	}

}
