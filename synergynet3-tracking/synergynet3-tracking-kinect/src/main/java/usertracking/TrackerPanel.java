// Utilises Andrew Davison's ArniesTracker (http://fivedots.coe.psu.ac.th/~ad/jg/nui1610/index.html)

package usertracking;

import java.awt.*;

import javax.swing.*;
import java.awt.image.*;
import java.awt.color.*;

import org.OpenNI.*;

import synergynet3.cluster.SynergyNetCluster;
import synergynet3.tracking.network.shared.UserColourUtils;
import usertracking.networking.TeacherControlPanel;
import usertracking.networking.TrackerNetworking;

import java.nio.ShortBuffer;

public class TrackerPanel extends JPanel implements Runnable{
	
	private static final long serialVersionUID = -5864253125817851472L;
	private static final int MAX_DEPTH_SIZE = 12000;  
	
	private TeacherControlPanel teacherControlPanel;
	
	private byte[] imgbytes;
	private int imWidth, imHeight;
	private float histogram[];        // for the depth values
	private int maxDepth = 0;         // largest depth value
	
	public static volatile boolean isRunning;
	
	private Font msgFont;
	
	// OpenNI
	private Context context;
	private DepthMetaData depthMD;
	
    // used to create a labelled depth map, where each pixel holds a kinect ID (1, 2, etc.), or 0 to mean it is part of the background
	private SceneMetaData sceneMD;
	
	private Skeletons skels;   // the users' skeletons
	
	public TrackerPanel(TeacherControlPanel teacherControlPanel){
			
		this.teacherControlPanel  = teacherControlPanel;
		
		setBackground(Color.WHITE);

		msgFont = new Font("SansSerif", Font.BOLD, 18);
		
		configOpenNI();
		histogram = new float[MAX_DEPTH_SIZE];
		
		imWidth = depthMD.getFullXRes();
		imHeight = depthMD.getFullYRes();
		System.out.println("Image dimensions (" + imWidth + ", " + imHeight + ")");
		
		// create empty image bytes array of correct size and type
		imgbytes = new byte[imWidth * imHeight * 3];
		
		new Thread(this).start();   // start updating the panel
	}
	
	private void configOpenNI(){  // create context, depth generator, depth meta-data, user generator, scene meta-data, and skeletons
		try {
			context = new Context();
			
			// add the NITE Licence 
			
			License license = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");   // vendor, key
			context.addLicense(license); 
			
			DepthGenerator depthGen = DepthGenerator.create(context);
			MapOutputMode mapMode = new MapOutputMode(UserTracker.VIEW_WIDTH, UserTracker.VIEW_HEIGHT, 30);   // xRes, yRes, FPS
			depthGen.setMapOutputMode(mapMode); 
			
			context.setGlobalMirror(true);         // set mirror mode 
			depthMD = depthGen.getMetaData();      // use depth meta-data to access depth info (avoids bug with DepthGenerator)
			
			UserGenerator userGen = UserGenerator.create(context);
			sceneMD = userGen.getUserPixels(0);
			// used to return a map containing kinect IDs (or 0) at each depth location
			
			skels = new Skeletons(userGen, depthGen, this);
			
			context.startGeneratingAll(); 
			System.out.println("Started context generating..."); 
		}catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}
	
	public void clearSequences(){
		skels.clearSequences();
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(imWidth, imHeight);
	}
	
	public void closeDown(){
		isRunning = false;
	} 
	
	public void run(){ // update and display the users-coloured depth image and skeletons whenever the context is updated.
		isRunning = true;
		while (isRunning) {
			try {
				context.waitAnyUpdateAll();
			}catch(StatusException e){
				System.out.println(e); 
				System.exit(1);
			}
			updateUserDepths();
			skels.update();
			repaint();
		}
		
		// close down
		try {
			context.stopGeneratingAll();
		}catch (StatusException e) {}
		context.release();
		TrackerNetworking.broadcastClearUserLocations();
		SynergyNetCluster.get().shutdown();
		System.exit(0);
	}
	
	private void updateUserDepths(){  // build a histogram of 8-bit depth values, and convert it to depth image bytes where each user is coloured differently
		ShortBuffer depthBuf = depthMD.getData().createShortBuffer();
		calcHistogram(depthBuf);
		depthBuf.rewind();
		
		// use kinect IDs to colour the depth map
		ShortBuffer usersBuf = sceneMD.getData().createShortBuffer();
		
		// usersBuf is a labelled depth map, where each pixel holds an kinect ID (e.g. 1, 2, 3), or 0 to denote that the pixel is part of the background.
		
		while (depthBuf.remaining() > 0) {
			int pos = depthBuf.position();
			short depthVal = depthBuf.get();
			short userID = usersBuf.get();
			
			imgbytes[3*pos] = 0;     // default colour is black when there's no depth data
			imgbytes[3*pos + 1] = 0;
			imgbytes[3*pos + 2] = 0;
			
			if (depthVal != 0) {   // there is depth data			
				
				Color colour = UserColourUtils.getColour(TrackerNetworking.uniqueIDs[userID]);
				
				// convert histogram value (0.0-1.0f) to a RGB colour
				float histValue = histogram[depthVal];
				imgbytes[3*pos] = (byte) (histValue * colour.getRed());
				imgbytes[3*pos + 1] = (byte) (histValue * colour.getGreen());
				imgbytes[3*pos + 2] = (byte) (histValue * colour.getBlue());
			}
		}
	}
	
	private void calcHistogram(ShortBuffer depthBuf){  // reset histogram
		for (int i = 0; i <= maxDepth; i++) histogram[i] = 0;
		
		// record number of different depths in histogram[]
		int numPoints = 0;
		maxDepth = 0;
		while (depthBuf.remaining() > 0) {
			short depthVal = depthBuf.get();
			if (depthVal > maxDepth)maxDepth = depthVal;
			if ((depthVal != 0)  && (depthVal < MAX_DEPTH_SIZE)){      // skip histogram[0]
				histogram[depthVal]++;
				numPoints++;
			}
		}
		
		// convert into a cumulative depth count (skipping histogram[0])
		for (int i = 1; i <= maxDepth; i++) histogram[i] += histogram[i-1];
		
		// convert cumulative depth into the range 0.0 - 1.0f which will later be used to modify a colour from USER_COLORS[]
		if (numPoints > 0) {
			for (int i = 1; i <= maxDepth; i++)    // skipping histogram[0]
				histogram[i] = 1.0f - (histogram[i] / (float) numPoints);
		}
	} 
	
	public void paintComponent(Graphics g){ // Draw the depth image with coloured users, skeletons, and statistics info
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		drawUserDepths(g2d);
		g2d.setFont(msgFont);    // for user status and statistics
		skels.draw(g2d);
		
		if (UserTracker.GESTURING_USER > 0){
					
			Color c = UserColourUtils.getColour(UserTracker.GESTURING_USER);
			
			g2d.setColor(c);
			g2d.setStroke(new BasicStroke(20));
			g2d.draw(new Rectangle(10, 10, UserTracker.VIEW_WIDTH - 25, UserTracker.VIEW_HEIGHT - 20));
		}		
		
	}
	
	private void drawUserDepths(Graphics2D g2d){ // Create BufferedImage using the depth image bytes and a colour model, then draw it
		// define an 8-bit RGB channel colour model
		ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8},
                     false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
		
		// fill the raster with the depth image bytes
		DataBufferByte dataBuffer = new DataBufferByte(imgbytes, imWidth*imHeight*3);
		
		WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, imWidth, imHeight, imWidth*3, 3, new int[] { 0, 1, 2}, null);
		
		// combine colour model and raster to create a BufferedImage
		BufferedImage image = new BufferedImage(colorModel, raster, false, null);
		
		g2d.drawImage(image, 0, 0, null);
	}


	public void pose(int userID, PoseName gest, boolean isActivated){				
		if (teacherControlPanel.isTeacher(userID))GestureActions.performGesture(userID, gest, isActivated);
	}
	
	public TeacherControlPanel getTeacherControlPanel(){
		return teacherControlPanel;
	}

}