package synergynet3.museum.table.settingsapp.appearance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class AppearanceXmlManager extends DefaultHandler{
	
	private static final String APPEARANCE_FOLDER = "Appearance";
	private static final String APPEARANCE_FILENAME= "appearance.xml";

	public HashMap<String, String> appearanceValues = new HashMap<String, String>();
	
	private File appearanceFile;
	
	private boolean validWorkspace = true;
	
	public AppearanceXmlManager(String workspace){
		
		appearanceFile = new File(workspace + File.separator + APPEARANCE_FOLDER + File.separator + APPEARANCE_FILENAME) ;
		if (!appearanceFile.exists()){		
			if (new File(workspace).isDirectory()){
				File apperanceFolder = new File(workspace + File.separator + APPEARANCE_FOLDER);
				if (!apperanceFolder.isDirectory()){
					apperanceFolder.mkdirs();
				}
			}else{
				validWorkspace = false;
			}
		}
		regenerate();
	}

	public void regenerate(){
		if (appearanceFile.exists()){	

			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				
				AppearanceXmlParser handler = new AppearanceXmlParser();
				saxParser.parse(appearanceFile, handler);
				
				appearanceValues = handler.getAppearanceValues();							
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void saveXML() {
		try {
			if (validWorkspace){
				if (!appearanceFile.exists()){		
					appearanceFile.createNewFile();
				}
	
				OutputStream outputStream = new FileOutputStream(appearanceFile);
		
				XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, "utf-8"));
				out.writeStartDocument();
				out.writeCharacters("\n");
				out.writeStartElement(AppearanceConfigPrefsItem.APPEARANCE_NODE);
				out.writeCharacters("\n");
				
				for (Entry<String, String> entry : appearanceValues.entrySet()){
					out.writeStartElement(entry.getKey());
					out.writeCharacters(entry.getValue());
					out.writeEndElement();
					out.writeCharacters("\n");
				}
				
				out.writeEndElement();
				out.writeEndDocument();
		
				out.close();			
				
				outputStream.close();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	
}