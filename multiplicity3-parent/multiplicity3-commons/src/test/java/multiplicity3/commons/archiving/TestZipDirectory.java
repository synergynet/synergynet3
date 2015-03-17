package multiplicity3.commons.archiving;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TestZipDirectory {
	@Test
	public void testZipADirectory() throws IOException {
		File directoryToZip = getTestDirectoryToZip();
		File zipFileToCreate = File.createTempFile("tempziptest", ".zip");
		zipFileToCreate.deleteOnExit();
		boolean result = ZipDirectory.zipDirectory(directoryToZip, zipFileToCreate);
		assertTrue("Did not zip directory properly.", result);
	}

	private File getTestDirectoryToZip() {
		return new File("src");
	}
}
