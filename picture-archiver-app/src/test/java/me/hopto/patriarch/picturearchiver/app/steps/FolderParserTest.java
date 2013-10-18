package me.hopto.patriarch.picturearchiver.app.steps;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FolderParserTest {
	@Rule
	public TestName				name		= new TestName();
	private static Logger	logger	= Logger.getLogger(FolderParserTest.class);
	private FolderParser	folderParser;
	private File					destDir;

	@Before
	public void setup() {
		if (logger.isDebugEnabled()) logger.debug("[BEGIN] " + name.getMethodName());
		folderParser = new FolderParser("src/test/resources/sample/aPictureFolder/");
		destDir = new File("target/aPictureFolder/");
		if (!destDir.exists()) destDir.mkdirs();
	}

	@After
	public void tearDown() throws Exception {
		if (logger.isDebugEnabled()) logger.debug("[ END ] " + name.getMethodName());
	}

	@Test
	public void checkStuff() throws IOException {
		// Setup

		// Test
		List<FileWrapper> files = folderParser.parseDir();

		// Assert
		assertThat(files).isNotNull().hasSize(7);
		assertThat(files.get(0).getFileType()).isEqualTo(FileType.OTHER);
		assertThat(files.get(1).getFileType()).isEqualTo(FileType.VIDEO);
		assertThat(files.get(2).getFileType()).isEqualTo(FileType.PICTURE);
		assertThat(files.get(3).getFileType()).isEqualTo(FileType.PICTURE);
		assertThat(files.get(4).getFileType()).isEqualTo(FileType.PICTURE);
		assertThat(files.get(5).getFileType()).isEqualTo(FileType.OTHER);
		assertThat(files.get(6).getFileType()).isEqualTo(FileType.PICTURE);
	}

	@Test
	public void copyFiles() throws IOException {
		// Setup
		folderParser.parseDir();

		// Test
		folderParser.copyTo(destDir);

		// Assert
	}
}
