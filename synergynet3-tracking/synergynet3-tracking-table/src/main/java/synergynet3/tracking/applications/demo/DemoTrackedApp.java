package synergynet3.tracking.applications.demo;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import com.jme3.math.Vector2f;

import synergynet3.additionalUtils.AdditionalSynergyNetUtilities;
import synergynet3.feedbacksystem.defaultfeedbacktypes.AudioFeedback;
import synergynet3.feedbacksystem.defaultfeedbacktypes.SimpleTrafficLightFeedback;
import synergynet3.feedbacksystem.defaultfeedbacktypes.SmilieFeedback;
import synergynet3.tracking.applications.TrackedApp;

import multiplicity3.appsystem.IQueueOwner;
import multiplicity3.appsystem.MultiplicityClient;
import multiplicity3.config.identity.IdentityConfigPrefsItem;
import multiplicity3.csys.factory.ContentTypeNotBoundException;
import multiplicity3.csys.items.border.IRoundedBorder;
import multiplicity3.input.MultiTouchInputComponent;
import multiplicity3.input.events.MultiTouchCursorEvent;

public class DemoTrackedApp extends TrackedApp{	
	
	private HashMap<Long, IRoundedBorder> cursors = new HashMap<Long, IRoundedBorder>();
		
	@Override
	public void shouldStart(MultiTouchInputComponent input, IQueueOwner iqo) {
		input.registerMultiTouchEventListener(this);
		super.shouldStart(input, iqo);
	}
	
	@Override
	protected String getSpecificFriendlyAppName(){
		return "TrackingDemo";
	}
	
	@Override
	protected void loadDefaultContent() throws IOException, ContentTypeNotBoundException {			
		feedbackTypes.add(SimpleTrafficLightFeedback.class);
		feedbackTypes.add(AudioFeedback.class);
		feedbackTypes.add(SmilieFeedback.class);				
	}

	@Override
	public void cursorPressed(MultiTouchCursorEvent event) {
		super.cursorPressed(event);		
		try{	
			int uniqueID = 0;
			if (touches.containsKey(event.getCursorID()))uniqueID = touches.get(event.getCursorID());			
			if (!frozen || isTeacher(uniqueID)){			
				IRoundedBorder touchBorder = contentFactory.create(IRoundedBorder.class, "border", UUID.randomUUID());		
				touchBorder.setBorderWidth(10);
				touchBorder.setSize(50, 50);					
				touchBorder.setRelativeLocation(new Vector2f((event.getPosition().x * displayWidth) - displayWidth/2, (event.getPosition().y * displayHeight)-displayHeight/2));				
				touchBorder.setColor(getUserColour(uniqueID));				
				stage.addItem(touchBorder);				
				stage.getZOrderManager().bringToTop(touchBorder);
				cursors.put(event.getCursorID(), touchBorder);
			}			
		}catch(ContentTypeNotBoundException e){
			AdditionalSynergyNetUtilities.log(Level.SEVERE, "Content Type Not Bound", e );
		}	
	}

	@Override
	public void cursorReleased(MultiTouchCursorEvent event) {
		super.cursorReleased(event);
		if (cursors.containsKey(event.getCursorID())){
			stage.removeItem(cursors.get(event.getCursorID()));
			cursors.remove(event.getCursorID());
		}
	}

	@Override
	public void cursorChanged(MultiTouchCursorEvent event) {
		if (cursors.containsKey(event.getCursorID())){
			cursors.get(event.getCursorID()).setRelativeLocation(new Vector2f((event.getPosition().x * displayWidth) - displayWidth/2,
					(event.getPosition().y * displayHeight)-displayHeight/2));
			stage.getZOrderManager().bringToTop(cursors.get(event.getCursorID()));
		}
	}
	
	public static void main(String[] args) throws SocketException {
		if(args.length > 0) {
			IdentityConfigPrefsItem idprefs = new IdentityConfigPrefsItem();			
			idprefs.setID(args[0]);
		}
		
		TrackedApp.initialiseTrackingAppArgs(args);
		
		MultiplicityClient client = MultiplicityClient.get();
		client.start();
		DemoTrackedApp app = new DemoTrackedApp();
		client.setCurrentApp(app);		
	}

}