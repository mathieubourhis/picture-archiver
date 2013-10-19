package me.hopto.patriarch.picturearchiver.app;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.IOException;
import me.hopto.patriarch.picturearchiver.app.steps.FolderParser;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import org.apache.log4j.Logger;

public class PictureArchiver {
	private static Logger	logger	= Logger.getLogger(PictureArchiver.class);

	public static void main(String[] args) {
		try {
			checkArgument(args.length > 0, new IllegalArgumentException("Must have one argument"));
			File rootDir = new File(args[0]);
			checkArgument(rootDir.exists(), new IllegalArgumentException("Root must exist"));
			checkArgument(rootDir.isDirectory(), new IllegalArgumentException("Root must be a directory"));

			FolderParser folderParser = new FolderParser();
			FileTree tree = new FileTree(rootDir);
			folderParser.tree(tree);
			tree.prettyPrint();
		} catch (IOException | IllegalArgumentException e) {
			logger.error(e.getMessage());
		}
	}
}
