package synergynet3.additionalitems.jme;

import java.util.ArrayList;
import java.util.UUID;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import synergynet3.additionalUtils.IgnoreDoubleClick;
import synergynet3.additionalitems.interfaces.ISimpleKeyboard;
import synergynet3.fonts.FontColour;
import synergynet3.fonts.FontUtil;
import synergynet3.keyboard.KeyboardOutput;
import synergynet3.keyboard.KeyboardSpecialKeys;

import multiplicity3.csys.annotations.ImplementsContentItem;
import multiplicity3.csys.behaviours.RotateTranslateScaleBehaviour;
import multiplicity3.csys.factory.ContentTypeNotBoundException;
import multiplicity3.csys.items.border.IRoundedBorder; 
import multiplicity3.csys.items.image.IImage;
import multiplicity3.csys.items.item.IItem;
import multiplicity3.csys.items.mutablelabel.IMutableLabel;
import multiplicity3.csys.items.shapes.IColourRectangle;
import multiplicity3.csys.stage.IStage;
import multiplicity3.input.MultiTouchEventAdapter;
import multiplicity3.input.events.MultiTouchCursorEvent;
import multiplicity3.jme3csys.items.IInitable;
import multiplicity3.jme3csys.items.container.JMEContainer;

@ImplementsContentItem(target = ISimpleKeyboard.class)
public class SimpleKeyboard extends JMEContainer implements IInitable, ISimpleKeyboard{
	
	private static final String keyboardLoc = "synergynet3/additionalitems/keyboard/";
	
	private static final float BUTTON_SIZE = 40;
	private static final float BUTTON_SPACING = 50;
	
	private float width = BUTTON_SPACING * 15;
	private float height = BUTTON_SPACING * 6;
	
	private KeyboardOutput keyboardOutput;
	private ColorRGBA bgColour = ColorRGBA.Black;
	private ColorRGBA keyColour = ColorRGBA.Black;
	private ColorRGBA keyBorderColour = ColorRGBA.Gray;
	private ColorRGBA boardBorderColour = ColorRGBA.Gray;
	
	private boolean movable = true;
	private boolean caps = false;
	
	private IStage stage;
	
	private RotateTranslateScaleBehaviour rts;
	private float minScale, maxScale = 1;
	private boolean scaleLimitsSet = false;
	
	private float x = -width/2;
	private float y = height/2;
	
	private IImage listener;
	
	private FontColour fontColour = FontColour.White;
	
	private ArrayList<IMutableLabel> capKeys= new ArrayList<IMutableLabel>();
	private ArrayList<IMutableLabel> lowerKeys = new ArrayList<IMutableLabel>();

	private ArrayList<IItem> toAddAtEndOfSetup = new ArrayList<IItem>();
	
	public SimpleKeyboard(String name, UUID uuid) {
		super(name, uuid);			
	}
	
	@Override
	public void generateKeys(final IStage stage, final KeyboardOutput keyboardOutput){
		this.stage = stage;
		this.keyboardOutput = keyboardOutput;
		
		try{						
			IColourRectangle background = stage.getContentFactory().create(IColourRectangle.class, "keyboardBg", UUID.randomUUID());
			background.enableTransparency();
			background.setSolidBackgroundColour(bgColour);
			background.setSize(width, height);
			
			IRoundedBorder keyboardBorder = stage.getContentFactory().create(IRoundedBorder.class, "keyboardBorder", UUID.randomUUID());		
			keyboardBorder.setBorderWidth(15f);
			keyboardBorder.setSize(width, height);
			keyboardBorder.setColor(boardBorderColour);
			
			this.addItem(background);
			this.addItem(keyboardBorder);	
			
			if (keyColour.getAlpha() > 0){
				IImage keyBackgrounds = stage.getContentFactory().create(IImage.class, "keyBackgrounds", UUID.randomUUID());
				keyBackgrounds.setImage(getKeyboardKeyBackgroundColour(keyColour));
				keyBackgrounds.setSize(696, 246);				
				this.addItem(keyBackgrounds);	
			}
			
			if (keyBorderColour.getAlpha() > 0){
				IImage keyBorders = stage.getContentFactory().create(IImage.class, "keyBorders", UUID.randomUUID());
				keyBorders.setImage(getKeyboardKeyBorderColour(keyBorderColour));
				keyBorders.setSize(696, 246);				
				this.addItem(keyBorders);	
			}

			IImage specialKeys = stage.getContentFactory().create(IImage.class, "specialKey", UUID.randomUUID());
			specialKeys.setImage(getKeyboardKeySpecialColour(fontColour));
			specialKeys.setSize(696, 246);				
			this.addItem(specialKeys);	
			
			x += BUTTON_SPACING;
			y -= BUTTON_SPACING;
			
			generateNonAlphabeticalKey("1", "!");			
			generateNonAlphabeticalKey("2", "\"");			
			generateNonAlphabeticalKey("3", "#");			
			generateNonAlphabeticalKey("4", "$");			
			generateNonAlphabeticalKey("5", "%");			
			generateNonAlphabeticalKey("6", "~");			
			generateNonAlphabeticalKey("7", "&");			
			generateNonAlphabeticalKey("8", "*");			
			generateNonAlphabeticalKey("9", "(");			
			generateNonAlphabeticalKey("0", ")");			
			generateNonAlphabeticalKey("-", "_");			
			generateNonAlphabeticalKey("=", "+");			
			generateBackspaceKey();	
			
			x = -width/2;
			x += BUTTON_SPACING + (BUTTON_SPACING * 0.5);			
			y -= BUTTON_SPACING;
			
			generateAlphabeticalKey("q");
			generateAlphabeticalKey("w");
			generateAlphabeticalKey("e");
			generateAlphabeticalKey("r");
			generateAlphabeticalKey("t");
			generateAlphabeticalKey("y");
			generateAlphabeticalKey("u");
			generateAlphabeticalKey("i");
			generateAlphabeticalKey("o");
			generateAlphabeticalKey("p");			
			generateNonAlphabeticalKey("[", "{");			
			generateNonAlphabeticalKey("]", "}");			
			
			y -= BUTTON_SPACING/2;
			
			generateEnterKey();
			
			x = -width/2;
			x += BUTTON_SPACING + (BUTTON_SPACING * 0.75);			
			y -= BUTTON_SPACING/2;
			
			generateAlphabeticalKey("a");
			generateAlphabeticalKey("s");
			generateAlphabeticalKey("d");
			generateAlphabeticalKey("f");
			generateAlphabeticalKey("g");
			generateAlphabeticalKey("h");
			generateAlphabeticalKey("j");
			generateAlphabeticalKey("k");
			generateAlphabeticalKey("l");	
			generateNonAlphabeticalKey(";", ":");		
			generateNonAlphabeticalKey("@", "'");	
			
			x = -width/2;
			x += BUTTON_SPACING * 2;			
			y -= BUTTON_SPACING;			
			
			generateNonAlphabeticalKey("\\", "|");
			generateAlphabeticalKey("z");
			generateAlphabeticalKey("x");
			generateAlphabeticalKey("c");
			generateAlphabeticalKey("v");
			generateAlphabeticalKey("b");
			generateAlphabeticalKey("n");
			generateAlphabeticalKey("m");
			generateNonAlphabeticalKey(",", "<");		
			generateNonAlphabeticalKey(".", ">");	
			generateNonAlphabeticalKey("/", "?");			
			generateShiftKey();			
			x = 0;			
			y -= BUTTON_SPACING;	
					
			generateSpaceKey();			

			listener = stage.getContentFactory().create(IImage.class, "listenBlock", UUID.randomUUID());
			listener.setSize(width + 30f, height + 30f);				
			this.addItem(listener);	
			
			final IgnoreDoubleClick clicker = new IgnoreDoubleClick(500){
				@Override
				public void onAction(MultiTouchCursorEvent event) {
					Vector2f eventLoc = new Vector2f(event.getPosition().x * stage.getDisplayWidth(), event.getPosition().y * stage.getDisplayHeight());

					String closest = "";
					float distance = stage.getDisplayWidth();
					if (caps){
						for (IMutableLabel key : capKeys){
							float currentDistance = eventLoc.distance(key.getWorldLocation());
							if (currentDistance < distance){
								closest = key.getText();
								distance = currentDistance;
							}
						}
					}else{
						for (IMutableLabel key : lowerKeys){
							float currentDistance = eventLoc.distance(key.getWorldLocation());
							if (currentDistance < distance){
								closest = key.getText();
								distance = currentDistance;
							}
						}
					}
					if (!closest.equals("")){
						if (distance < BUTTON_SIZE/2){
							keyboardOutput.onKeyboardOutput(closest);
						}
					}
				}
			};
			
			listener.getMultiTouchDispatcher().addListener(new MultiTouchEventAdapter() {
				@Override
				public void cursorPressed(MultiTouchCursorEvent event) {	
					clicker.click(event);
				}
			});	
			
			for (IItem item : toAddAtEndOfSetup){
				this.addItem(item);
			}
			
			this.zOrderManager.setAutoBringToTop(false);
			
			if (movable){				
				rts = stage.getBehaviourMaker().addBehaviour(listener, RotateTranslateScaleBehaviour.class);				
				rts.setItemActingOn(this);
				if (scaleLimitsSet){
					rts.setScaleLimits(minScale, maxScale);
				}
			}

		}catch(ContentTypeNotBoundException e){}	
	}
	
	private String getKeyboardKeyBackgroundColour(ColorRGBA colour) {
		String toReturn = keyboardLoc + "keybackgrounds/";
		toReturn += colourRGBAToString(colour);
		toReturn += "_keyboard_key_background.png";
		return toReturn;
	}
	
	private String getKeyboardKeyBorderColour(ColorRGBA colour) {
		String toReturn = keyboardLoc + "keyborders/";
		toReturn += colourRGBAToString(colour);
		toReturn += "_keyboard_key_borders.png";
		return toReturn;
	}

	private String colourRGBAToString(ColorRGBA colour) {
		String toReturn = "white";	
		if (colour.equals(ColorRGBA.Black)){
			toReturn = "black";
		}else if (colour.equals(ColorRGBA.Blue)){
			toReturn = "blue";
		}else if (colour.equals(ColorRGBA.Cyan)){
			toReturn = "cyan";	
		}else if (colour.equals(ColorRGBA.Green)){
			toReturn = "green";
		}else if (colour.equals(ColorRGBA.Magenta)){
				toReturn = "magenta";
		}else if (colour.equals(ColorRGBA.Orange)){
				toReturn = "orange";
		}else if (colour.equals(ColorRGBA.Red)){
			toReturn = "red";
		}else if (colour.equals(ColorRGBA.White)){
			toReturn = "white";
		}else if (colour.equals(ColorRGBA.Yellow)){
			toReturn = "yellow";
		}else if (colour.equals(ColorRGBA.Gray)){
			toReturn = "grey";
		}else if (colour.equals(ColorRGBA.DarkGray)){
			toReturn = "dark_grey";
		}else{
			toReturn = "white";
		}
		return toReturn;
	}
	
	private String getKeyboardKeySpecialColour(FontColour colour) {
		String toReturn = keyboardLoc + "specialkeys/";
		toReturn += fontColourToString(colour);
		toReturn += "_keyboard_special_keys.png";
		return toReturn;
	}

	private String fontColourToString(FontColour colour) {
		String toReturn = "white";
		switch (colour){
			case Black:  toReturn = "black";
				break;
			case Blue:  toReturn = "blue";
				break;
			case Cyan:  toReturn = "cyan";
				break;
			case Green:  toReturn = "green";
				break;
			case Magenta:  toReturn = "magenta";
				break;
			case Orange:  toReturn = "orange";
				break;
			case Red:  toReturn = "red";
				break;
			case White:  toReturn = "white";
				break;
			case Yellow:  toReturn = "yellow";
				break;
			case Grey:  toReturn = "grey";
				break;
			case Dark_Grey:  toReturn = "dark_grey";
				break;
			default: toReturn = "white";
				break;
		}
		return toReturn;
	}

	private void generateNonAlphabeticalKey(String keyLower, String keyHigher) throws ContentTypeNotBoundException{
		generateKey(keyLower, keyHigher);
		x += BUTTON_SPACING;
	}
	
	private void generateAlphabeticalKey(String key) throws ContentTypeNotBoundException{
		generateKey(key.toLowerCase(), key.toUpperCase());
		x += BUTTON_SPACING;
	}
	
	
	private void generateKey(final String lowerLetter, final String upperLetter) throws ContentTypeNotBoundException{
				
		IMutableLabel textLabelOff = stage.getContentFactory().create(IMutableLabel.class, "textLabel", UUID.randomUUID());
		textLabelOff.setFont(FontUtil.getFont(fontColour));
		textLabelOff.setRelativeScale(0.8f);
		textLabelOff.setBoxSize(BUTTON_SIZE, BUTTON_SIZE);	
		textLabelOff.setText(lowerLetter);		
		textLabelOff.setRelativeLocation(new Vector2f(x, y));		
		
		IMutableLabel textLabelOn = stage.getContentFactory().create(IMutableLabel.class, "textLabel", UUID.randomUUID());
		textLabelOn.setFont(FontUtil.getFont(fontColour));
		textLabelOn.setRelativeScale(0.8f);
		textLabelOn.setBoxSize(BUTTON_SIZE, BUTTON_SIZE);		
		textLabelOn.setText(upperLetter);	
		textLabelOn.setVisible(false);
		textLabelOn.setRelativeLocation(new Vector2f(x, y));				

		this.addItem(textLabelOn);	
		this.addItem(textLabelOff);	
		
		capKeys.add(textLabelOn);
		lowerKeys.add(textLabelOff);
	}
	
	private void generateBackspaceKey() throws ContentTypeNotBoundException{
		float backSpaceKeyWidth = BUTTON_SIZE + BUTTON_SPACING;		
		float gap = BUTTON_SPACING - BUTTON_SIZE;		
		x += (((backSpaceKeyWidth/2)) + gap + (BUTTON_SIZE/2)) - BUTTON_SPACING;

		IImage button = stage.getContentFactory().create(IImage.class, "button", UUID.randomUUID());
		button.setSize(backSpaceKeyWidth, BUTTON_SIZE);
		
		final IgnoreDoubleClick clicker = new IgnoreDoubleClick(500){
			@Override
			public void onAction(MultiTouchCursorEvent event) {
				keyboardOutput.onKeyboardOutput(KeyboardSpecialKeys.BACKSPACE.toString());
			}
		};
		
		button.getMultiTouchDispatcher().addListener(new MultiTouchEventAdapter() {
			@Override
			public void cursorPressed(MultiTouchCursorEvent event) {					
				clicker.click(event);
			}
		});		
		button.setRelativeLocation(new Vector2f(x, y));
		
		toAddAtEndOfSetup.add(button);
	}
	
	private void generateEnterKey() throws ContentTypeNotBoundException{
		
		float enterKeyWidth = BUTTON_SIZE + (BUTTON_SPACING * 0.5f);		
		float gap = BUTTON_SPACING - BUTTON_SIZE;		
		x += (((enterKeyWidth/2)) + gap + (BUTTON_SIZE/2)) - BUTTON_SPACING;		
		
		IImage button = stage.getContentFactory().create(IImage.class, "button", UUID.randomUUID());
		button.setSize(enterKeyWidth, (BUTTON_SIZE * 2) + gap);
		
		final IgnoreDoubleClick clicker = new IgnoreDoubleClick(500){
			@Override
			public void onAction(MultiTouchCursorEvent event) {
				keyboardOutput.onKeyboardOutput(KeyboardSpecialKeys.ENTER.toString());
			}
		};
		
		button.getMultiTouchDispatcher().addListener(new MultiTouchEventAdapter() {
			@Override
			public void cursorPressed(MultiTouchCursorEvent event) {					
				clicker.click(event);
			}
		});		
		button.setRelativeLocation(new Vector2f(x, y));
		
		toAddAtEndOfSetup.add(button);
	}
	
	private void generateShiftKey() throws ContentTypeNotBoundException{
		float capsKeyWidth = BUTTON_SIZE + BUTTON_SPACING;
		
		float gap = BUTTON_SPACING - BUTTON_SIZE;
		x += (capsKeyWidth/2) + gap + (BUTTON_SIZE/2) - BUTTON_SPACING;
		
		IImage button = stage.getContentFactory().create(IImage.class, "button", UUID.randomUUID());
		button.setSize(capsKeyWidth, BUTTON_SIZE);
		
		final IgnoreDoubleClick clicker = new IgnoreDoubleClick(500){
			@Override
			public void onAction(MultiTouchCursorEvent event) {
				caps = !caps;
				for (IMutableLabel key : capKeys){
					key.setVisible(caps);
				}
				for (IMutableLabel key : lowerKeys){
					key.setVisible(!caps);
				}
			}
		};
		
		button.getMultiTouchDispatcher().addListener(new MultiTouchEventAdapter() {
			@Override
			public void cursorPressed(MultiTouchCursorEvent event) {					
				clicker.click(event);
			}
		});		
		button.setRelativeLocation(new Vector2f(x, y));	
		toAddAtEndOfSetup.add(button);
	}
	
	private void generateSpaceKey() throws ContentTypeNotBoundException{
		IImage button = stage.getContentFactory().create(IImage.class, "button", UUID.randomUUID());
		button.setSize(BUTTON_SIZE * 8, BUTTON_SIZE);
		
		final IgnoreDoubleClick clicker = new IgnoreDoubleClick(500){
			@Override
			public void onAction(MultiTouchCursorEvent event) {
				keyboardOutput.onKeyboardOutput(" ");
			}
		};
		
		button.getMultiTouchDispatcher().addListener(new MultiTouchEventAdapter() {
			@Override
			public void cursorPressed(MultiTouchCursorEvent event) {	
				clicker.click(event);
			}
		});		

		button.setRelativeLocation(new Vector2f(x, y));
		toAddAtEndOfSetup.add(button);
	}
	
	public void setScaleLimits(float minScale, float maxScale){
		this.minScale = minScale;
		this.maxScale = maxScale;
		if (rts != null){
			rts.setScaleLimits(minScale, maxScale);
		}
		scaleLimitsSet = true;		
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}


	@Override
	public void setColours(ColorRGBA bgColour, ColorRGBA keyColour, ColorRGBA keyBorderColour, ColorRGBA boardBorderColour, FontColour fontColour) {
		this.bgColour = bgColour;
		this.keyColour = keyColour;
		this.keyBorderColour = keyBorderColour;
		this.boardBorderColour = boardBorderColour;
		this.fontColour = fontColour;
	}

	@Override
	public void setMovable(boolean movable) {
		this.movable = movable;		
	}

	/**
	 * @return the listener
	 */
	public IImage getListener() {
		return listener;
	}

}
