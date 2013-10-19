package me.hopto.patriarch.picturearchiver.app;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.IOException;
import me.hopto.patriarch.picturearchiver.app.steps.CopyPictures;
import me.hopto.patriarch.picturearchiver.app.steps.ParseTree;
import me.hopto.patriarch.picturearchiver.app.steps.LightArchive;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import org.apache.log4j.Logger;

public class PictureArchiver {
	private static Logger	logger	= Logger.getLogger(PictureArchiver.class);

	public static void main(String[] args) {
		try {
			checkArgument(args.length == 6, new IllegalArgumentException("Must have four arguments, a source root dir, then a dest dir, then four booleans (shouldRename ? shouldCompressPictures ? shouldCompressVideos ? shouldPutOthersInAMiscDir ?)"));

			String rootPath = args[0];
			File rootDir = new File(rootPath);
			checkArgument(rootDir.exists(), new IllegalArgumentException("Source root dir must exist : " + rootPath));
			checkArgument(rootDir.isDirectory(), new IllegalArgumentException("Source Root dir must be a directory" + rootPath));

			String destPath = args[1];
			File destDir = new File(destPath);
			checkArgument(destDir.exists(), new IllegalArgumentException("Dest dir must exist : " + destPath));
			checkArgument(destDir.isDirectory(), new IllegalArgumentException("Dest dir must be a directory" + destPath));
			checkArgument(destDir.listFiles().length == 0, new IllegalArgumentException("Dest dir must be empty : " + destPath));

			boolean shouldRename = Boolean.valueOf(args[2]);
			logger.info("Should rename files : " + (shouldRename ? "ON" : "OFF"));
			boolean shouldCompressPictures = Boolean.valueOf(args[3]);
			logger.info("Should compress pictures : " + (shouldCompressPictures ? "ON" : "OFF"));
			boolean shouldCompressVideos = Boolean.valueOf(args[4]);
			logger.info("Should compress videos : " + (shouldCompressVideos ? "ON" : "OFF"));
			boolean shouldPutOthersInAMiscDir = Boolean.valueOf(args[5]);
			logger.info("Should move other files to a misc dir : " + (shouldPutOthersInAMiscDir ? "ON" : "OFF"));

			logger.info("Begin sourcing");
			FileTree tree = new FileTree(rootDir);
			new ParseTree().tree(tree);
			tree.prettyPrint();

			logger.info("Copying pictures");
			new CopyPictures().copyTreeTo(tree, destDir, shouldRename, shouldCompressPictures);
			FileTree.resetGlobalStats();
			FileTree destTree = new FileTree(destDir);
			new ParseTree().tree(destTree);
			destTree.prettyPrint();

			logger.info("Archiving Copied Pictures");
			new LightArchive().lightArchiveDestTree(destTree);
			FileTree.resetGlobalStats();
			destTree = new FileTree(destDir);
			new ParseTree().tree(destTree);
			destTree.prettyPrint();

		} catch (IOException | IllegalArgumentException e) {
			logger.error(e.getMessage());
		}
	}
}
