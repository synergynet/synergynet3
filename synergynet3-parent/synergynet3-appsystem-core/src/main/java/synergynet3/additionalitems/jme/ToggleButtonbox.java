package synergynet3.additionalitems.jme;

import java.util.UUID;

import synergynet3.additionalitems.interfaces.IToggleButtonbox;
import synergynet3.fonts.FontColour;
import synergynet3.fonts.FontUtil;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import multiplicity3.csys.annotations.ImplementsContentItem;
import multiplicity3.csys.factory.ContentTypeNotBoundException;
import multiplicity3.csys.items.border.IRoundedBorder;
import multiplicity3.csys.items.image.IImage;
import multiplicity3.csys.items.mutablelabel.IMutableLabel;
import multiplicity3.csys.items.shapes.IColourRectangle;
import multiplicity3.csys.stage.IStage;
import multiplicity3.jme3csys.items.IInitable;
import multiplicity3.jme3csys.items.container.JMEContainer;

@ImplementsContentItem(target = IToggleButtonbox.class)
public class ToggleButtonbox extends JMEContainer implements IToggleButtonbox, IInitable {
	
	private IColourRectangle backgroundOn;
	private IMutableLabel textLabelOn;
	private IRoundedBorder textBorderOn;
	private IImage imageOn;
	private IColourRectangle backgroundOff;
	private IMutableLabel textLabelOff;
	private IImage imageOff;
	private IRoundedBorder textBorderOff;
	private IImage listener;
	
	private boolean toggled = false;

	public ToggleButtonbox(String name, UUID uuid) {
		super(name, uuid);		
	}

	@Override
	public void setText(String textOff, ColorRGBA bgColourOff, ColorRGBA borderColourOff, FontColour fontColourOff,
			String textOn, ColorRGBA bgColourOn, ColorRGBA borderColourOn, FontColour fontColourOn,
			float width, float height, IStage stage) {	
		try{			
			
			backgroundOn = stage.getContentFactory().create(IColourRectangle.class, "bg", UUID.randomUUID());
			backgroundOn.setSolidBackgroundColour(bgColourOn);
			backgroundOn.setSize(width, height);
			this.addItem(backgroundOn);	
			
			textLabelOn = stage.getContentFactory().create(IMutableLabel.class, "textLabel", UUID.randomUUID());
			textLabelOn.setFont(FontUtil.getFont(fontColourOn));
			textLabelOn.setRelativeScale(0.8f);
			textLabelOn.setBoxSize(width, height);		
			textLabelOn.setText(textOn);		
			this.addItem(textLabelOn);	
			
			textBorderOn = stage.getContentFactory().create(IRoundedBorder.class, "textBorder", UUID.randomUUID());		
			textBorderOn.setBorderWidth(3);
			textBorderOn.setSize(width, height);
			textBorderOn.setColor(borderColourOn);	
			this.addItem(textBorderOn);	
			
			backgroundOn.setVisible(false);
			textLabelOn.setVisible(false);
			textBorderOn.setVisible(false);
			
			backgroundOff = stage.getContentFactory().create(IColourRectangle.class, "bg", UUID.randomUUID());
			backgroundOff.setSolidBackgroundColour(bgColourOff);
			backgroundOff.setSize(width, height);
			this.addItem(backgroundOff);	
			
			textLabelOff = stage.getContentFactory().create(IMutableLabel.class, "textLabel", UUID.randomUUID());
			textLabelOff.setFont(FontUtil.getFont(fontColourOff));
			textLabelOff.setRelativeScale(0.8f);
			textLabelOff.setBoxSize(width, height);		
			textLabelOff.setText(textOff);		
			this.addItem(textLabelOff);	
			
			textBorderOff = stage.getContentFactory().create(IRoundedBorder.class, "textBorder", UUID.randomUUID());		
			textBorderOff.setBorderWidth(3);
			textBorderOff.setSize(width, height);
			textBorderOff.setColor(borderColourOff);	
			this.addItem(textBorderOff);	
			
			listener = stage.getContentFactory().create(IImage.class, "listenBlock", UUID.randomUUID());
			listener.setSize(width, height);				
			this.addItem(listener);	
				
			this.getZOrderManager().setAutoBringToTop(false);

		}catch(ContentTypeNotBoundException e){}		
	}
	
	public void setImage(IImage imageOff, ColorRGBA bgColourOff, ColorRGBA borderColourOff,
			IImage imageOn, ColorRGBA bgColourOn, ColorRGBA borderColourOn,
			float width, float height, IStage stage) {	
		try{			
			
			backgroundOn = stage.getContentFactory().create(IColourRectangle.class, "bg", UUID.randomUUID());
			backgroundOn.setSolidBackgroundColour(bgColourOn);
			backgroundOn.setSize(width, height);
			this.addItem(backgroundOn);	
			
			this.imageOn = imageOn;
			imageOn.setRelativeLocation(new Vector2f());
			imageOn.setRelativeScale(1);
			imageOn.setRelativeRotation(0);
			imageOn.setSize(width, height);	
			this.addItem(imageOn);	
			
			textBorderOn = stage.getContentFactory().create(IRoundedBorder.class, "textBorder", UUID.randomUUID());		
			textBorderOn.setBorderWidth(3);
			textBorderOn.setSize(width, height);
			textBorderOn.setColor(borderColourOn);	
			this.addItem(textBorderOn);	
			
			backgroundOn.setVisible(false);
			imageOn.setVisible(false);
			textBorderOn.setVisible(false);
			
			backgroundOff = stage.getContentFactory().create(IColourRectangle.class, "bg", UUID.randomUUID());
			backgroundOff.setSolidBackgroundColour(bgColourOff);
			backgroundOff.setSize(width, height);
			this.addItem(backgroundOff);	
			
			this.imageOff = imageOff;
			imageOff.setRelativeLocation(new Vector2f());
			imageOff.setRelativeScale(1);
			imageOff.setRelativeRotation(0);
			imageOff.setSize(width, height);	
			this.addItem(imageOff);	
			
			textBorderOff = stage.getContentFactory().create(IRoundedBorder.class, "textBorder", UUID.randomUUID());		
			textBorderOff.setBorderWidth(3);
			textBorderOff.setSize(width, height);
			textBorderOff.setColor(borderColourOff);	
			this.addItem(textBorderOff);	
			
			listener = stage.getContentFactory().create(IImage.class, "listenBlock", UUID.randomUUID());
			listener.setSize(width, height);				
			this.addItem(listener);	
				
			this.getZOrderManager().setAutoBringToTop(false);

		}catch(ContentTypeNotBoundException e){}		
	}

	public IMutableLabel getTextLabelOn() {
		return textLabelOn;
	}

	public IRoundedBorder getTextBorderOn() {
		return textBorderOn;
	}
	
	public IImage getImageOn() {
		return imageOn;
	}
	
	public IMutableLabel getTextLabelOff() {
		return textLabelOff;
	}

	public IRoundedBorder getTextBorderOff() {
		return textBorderOff;
	}
	
	public IImage getImageOff() {
		return imageOff;
	}

	public void toggle() {
		toggled = !toggled;		
		setButtonVisibility();
	}
	
	private void setButtonVisibility(){
		backgroundOn.setVisible(toggled);
		textBorderOn.setVisible(toggled);		
		backgroundOff.setVisible(!toggled);
		textBorderOff.setVisible(!toggled);
		
		if (textLabelOn != null && textLabelOff != null){
			textLabelOn.setVisible(toggled);
			textLabelOff.setVisible(!toggled);
		}else if(imageOn != null && imageOff != null){
			imageOn.setVisible(toggled);
			imageOff.setVisible(!toggled);
		}
	}
	
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible){
			setButtonVisibility();
		}
	};

	public boolean getToggleStatus() {
		return toggled;
	}

	public IImage getListener() {
		return listener;
	}


}
