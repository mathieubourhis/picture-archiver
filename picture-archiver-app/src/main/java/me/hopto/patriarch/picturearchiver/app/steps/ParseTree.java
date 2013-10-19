package me.hopto.patriarch.picturearchiver.app.steps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileType;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;

public class ParseTree {
	public FileTree tree(FileTree rootTree) throws IOException {
		File rootDir = rootTree.getRootDir();
		if (rootDir.exists() && rootDir.isDirectory()) {
			File[] listFiles = rootDir.listFiles();

			for (File file : listFiles) {
				if (!file.isDirectory()) {
					String probeContentType = Files.probeContentType(file.toPath());
					String type = "other"; // for exotic file extension
					if (probeContentType != null) type = probeContentType.split("/")[0];
					if (type.equals("image")) {
						rootTree.add(new FileWrapper(FileType.PICTURE, file));
					} else if (type.equals("video")) {
						rootTree.add(new FileWrapper(FileType.VIDEO, file));
					} else {
						rootTree.add(new FileWrapper(FileType.OTHER, file));
					}
				} else {
					tree(rootTree.addDir(file));
				}
			}
		}
		return rootTree;
	}
}
