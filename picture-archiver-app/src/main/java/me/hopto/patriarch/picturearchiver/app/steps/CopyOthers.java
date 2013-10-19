package me.hopto.patriarch.picturearchiver.app.steps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileType;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;

public class CopyOthers {
	public void copyTreeTo(FileTree tree, File destDir, boolean shouldPutOthersInAMiscDir) throws IOException {
		destDir.mkdirs();
		for (FileWrapper fileWrapper : tree.getFiles()) {
			if (fileWrapper.getFileType() == FileType.OTHER) {
				Path newPath = Paths.get(destDir.getPath(), fileWrapper.getFile().getName());
				if (shouldPutOthersInAMiscDir) {
					initMiscDir(destDir);
					newPath = Paths.get(destDir.getPath(), "__misc", fileWrapper.getFile().getName());
				}
				copyFile(fileWrapper, newPath);
			}
		}
		for (FileTree subTree : tree.getDirs()) {
			File nestedDir = Paths.get(destDir.getPath(), subTree.getRootDir().getName()).toFile();
			copyTreeTo(subTree, nestedDir, shouldPutOthersInAMiscDir);
		}
	}

	private void initMiscDir(File destDir) {
		File file = Paths.get(destDir.getPath(), "__misc").toFile();
		if (!file.exists()) file.mkdir();
	}

	private void copyFile(FileWrapper fileWrapper, Path newPath) throws IOException {
		Files.copy(fileWrapper.getFile().toPath(), newPath);
	}
}
