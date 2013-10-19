package me.hopto.patriarch.picturearchiver.app.steps;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.util.List;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileType;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;
import org.apache.log4j.Logger;
import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParseTreeTest {
	@Rule
	public TestName				name		= new TestName();
	private static Logger	logger	= Logger.getLogger(ParseTreeTest.class);
	private ParseTree	folderParser;
	private File					destDir;
	private FileTree			tree;

	@Before
	public void setup() {
		if (logger.isDebugEnabled()) logger.debug("[BEGIN] " + name.getMethodName());
		folderParser = new ParseTree();
		tree = new FileTree(new File("src/test/resources/sample/aPictureFolder/"));
		destDir = new File("target/aPictureFolder/");
		if (destDir.exists()) Files.delete(destDir);
		destDir.mkdirs();
	}

	@After
	public void tearDown() throws Exception {
		if (logger.isDebugEnabled()) logger.debug("[ END ] " + name.getMethodName());
	}

	@Test
	public void checkStuff() throws IOException {
		// Setup

		// Test
		folderParser.tree(tree);

		// Assert
		assertThat(tree).isNotNull();
		assertThat(tree.getParent()).isNull();
		List<FileWrapper> rootDirFiles = tree.getFiles();
		assertThat(rootDirFiles).hasSize(7);
		assertThat(rootDirFiles.get(0).getFileType()).isEqualTo(FileType.OTHER);
		assertThat(rootDirFiles.get(1).getFileType()).isEqualTo(FileType.VIDEO);
		assertThat(rootDirFiles.get(2).getFileType()).isEqualTo(FileType.PICTURE);
		assertThat(rootDirFiles.get(3).getFileType()).isEqualTo(FileType.PICTURE);
		assertThat(rootDirFiles.get(4).getFileType()).isEqualTo(FileType.PICTURE);
		assertThat(rootDirFiles.get(5).getFileType()).isEqualTo(FileType.OTHER);
		assertThat(rootDirFiles.get(6).getFileType()).isEqualTo(FileType.PICTURE);
		assertThat(tree.getDirs()).hasSize(1);
		FileTree subTree = tree.getDirs().get(0);
		assertThat(subTree).isNotNull();
		assertThat(subTree.getParent()).isNotNull().isEqualTo(tree);
		assertThat(subTree.getFiles()).hasSize(1);
		assertThat(subTree.getFiles().get(0).getFileType()).isEqualTo(FileType.PICTURE);
	}

	@Test
	public void copyFiles() throws IOException {
		// Setup
		logger.debug("Source");
		FileTree.resetGlobalStats();
		new ParseTree().tree(tree);
		tree.prettyPrint();

		// Test
		//		logger.debug("Copy");
		//		new FolderCopy().copyTreeTo(tree, destDir);
		//		FileTree.resetGlobalStats();
		//		FileTree destTree = new FileTree(destDir);
		//		new FolderParser().tree(destTree);
		//		destTree.prettyPrint();

		logger.debug("CopyPictures");
		new CopyPictures().copyTreeTo(tree, destDir, true, true);
		FileTree.resetGlobalStats();
		FileTree destTree = new FileTree(destDir);
		new ParseTree().tree(destTree);
		destTree.prettyPrint();

		logger.debug("CopyVideos");
		new CopyVideos().copyTreeTo(tree, destDir, true, true);
		FileTree.resetGlobalStats();
		destTree = new FileTree(destDir);
		new ParseTree().tree(destTree);
		destTree.prettyPrint();

		logger.debug("CopyPictures");
		new CopyOthers().copyTreeTo(tree, destDir, true);
		FileTree.resetGlobalStats();
		destTree = new FileTree(destDir);
		new ParseTree().tree(destTree);
		destTree.prettyPrint();

		logger.debug("LightArchive");
		new LightArchive().lightArchiveDestTree(destTree);
		FileTree.resetGlobalStats();
		destTree = new FileTree(destDir);
		new ParseTree().tree(destTree);
		destTree.prettyPrint();

		logger.debug("HeavyArchive");
		new HeavyArchive().heavyArchiveTreeTo(tree, destDir);
		FileTree.resetGlobalStats();
		destTree = new FileTree(destDir);
		new ParseTree().tree(destTree);
		destTree.prettyPrint();
	}
}
