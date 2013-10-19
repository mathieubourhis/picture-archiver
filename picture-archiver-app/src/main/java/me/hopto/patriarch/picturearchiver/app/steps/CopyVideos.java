package me.hopto.patriarch.picturearchiver.app.steps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileType;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;

public class CopyVideos {
	public void copyTreeTo(FileTree tree, File destDir, boolean shouldRename, boolean shouldCompress) throws IOException {
		if (!destDir.exists()) destDir.mkdir();
		// TODO Use predicate ?
		for (FileWrapper fileWrapper : tree.getFiles()) {
			if (fileWrapper.getFileType() == FileType.VIDEO) {
				Path newPath = Paths.get(destDir.getPath(), fileWrapper.getFile().getName());
				if (shouldRename) newPath = Paths.get(destDir.getPath(), fileWrapper.getNewName());
				// TODO Find a way to compress videos
				if (shouldCompress) copyFile(fileWrapper, newPath);
				else copyFile(fileWrapper, newPath);
			}
		}
		for (FileTree subTree : tree.getDirs()) {
			File nestedDir = Paths.get(destDir.getPath(), subTree.getRootDir().getName()).toFile();
			copyTreeTo(subTree, nestedDir, shouldRename, shouldCompress);
		}
	}

	private void copyFile(FileWrapper fileWrapper, Path newPath) throws IOException {
		Files.copy(fileWrapper.getFile().toPath(), newPath);
	}
}
