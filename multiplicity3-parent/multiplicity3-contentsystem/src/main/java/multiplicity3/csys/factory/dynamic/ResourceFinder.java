package multiplicity3.csys.factory.dynamic;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class ResourceFinder {

	private static final Logger log = Logger.getLogger(ResourceFinder.class.getName());

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		List<String> classResources = find("multiplicity.csysng.items", true, new IResourceFinderFilter() {
			@Override
			public boolean accept(String pathname) {
				String[] components = pathname.split("\\.");
				if(components.length < 2) return false;
				String extension = components[components.length - 1];
				String name = components[components.length - 2];
				return 	extension.equals("class") &&
						name.startsWith("I");
			}			
		});
		
		log.fine("Results:");
		for(String c : classResources) {
			int index = c.indexOf(".class");
			log.fine("   " + c.substring(0,index));
		}
	}

	/**
	 * 
	 * @param rootPackageName
	 * @param recursive
	 * @param filter
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static List<String> find(String rootPackageName, boolean recursive, IResourceFinderFilter filter) throws ClassNotFoundException, IOException {
		List<String> resourceList = new ArrayList<String>();
		find(rootPackageName, recursive, resourceList, filter);
		return resourceList;
	}

	private static void find(String dottedPackageName, boolean recursive, List<String> resourceList, IResourceFinderFilter filter) throws ClassNotFoundException, IOException {
		String packageName = convertDottedToSlashed(dottedPackageName);
		URL url = ResourceFinder.class.getResource(packageName);
		log.info("Dealing with URL: " + url);
		File directory = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
		if(directory.exists()) {
			log.fine("finding classes on filesystem");
			find(dottedPackageName, directory, recursive, resourceList, filter);
		}else{
			log.fine("Will now attempt in-jar lookup.");
			URLConnection urlConnection = url.openConnection();
			if (urlConnection instanceof JarURLConnection) {
				JarURLConnection conn = (JarURLConnection) urlConnection;
				JarFile jfile = conn.getJarFile();
				Enumeration<JarEntry> e = jfile.entries();
				while (e.hasMoreElements()) {
					JarEntry entry = (JarEntry) e.nextElement();	
					String resourcePath = convertSlashedToDotted(entry.getName());
					if(filter.accept(resourcePath)) {
						resourceList.add(resourcePath);
					}
				}
			}
		}
	}

	private static void find(String dottedPackageName, File directory, boolean recursive, List<String> resourceList, IResourceFinderFilter filter) throws ClassNotFoundException {
		File[] files = directory.listFiles();
		for(File f : files) {				
			if(f.isDirectory()) {
				if(recursive) {
					String subPackageName = dottedPackageName + "." + f.getName();					
					find(subPackageName, f, recursive, resourceList, filter);
				}
			}else{
				log.fine("Checking " + dottedPackageName + "." + f.getName());
				String resourcePath = dottedPackageName + "." + f.getName();
				if(filter.accept(resourcePath)) {
					resourceList.add(resourcePath);
				}				
			}
		}
	}
	
	private static String convertDottedToSlashed(String dottedPackageName) {
		String name = dottedPackageName;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}
		name = name.replace('.', '/');
		return name;
	}
	
	private static String convertSlashedToDotted(String slashedPackageName) {
		return slashedPackageName.replace('/', '.');
	}
}
