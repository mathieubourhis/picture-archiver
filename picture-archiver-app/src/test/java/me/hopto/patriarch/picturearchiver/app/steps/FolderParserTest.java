package me.hopto.patriarch.picturearchiver.app.steps;

import static org.assertj.core.api.Assertions.assertThat;
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

	@Before
	public void setup() {
		if (logger.isDebugEnabled()) logger.debug("[BEGIN] " + name.getMethodName());
		folderParser = new FolderParser("src/test/resources/aPictureFolder");
	}

	@After
	public void tearDown() throws Exception {
		if (logger.isDebugEnabled()) logger.debug("[ END ] " + name.getMethodName());
	}

	@Test
	public void checkStuff() {
		// Setup

		// Test

		// Assert
		assertThat(true).isTrue();
	}
}
