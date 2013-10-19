package me.hopto.patriarch.picturearchiver.app.steps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileType;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;

public class CopyPictures {
	public void copyTreeTo(FileTree tree, File destDir, boolean shouldRename, boolean shouldCompress) throws IOException {
		if (!destDir.exists()) destDir.mkdir();
		// TODO Use predicate ?
		for (FileWrapper fileWrapper : tree.getFiles()) {
			if (fileWrapper.getFileType() == FileType.PICTURE) {
				Path newPath = Paths.get(destDir.getPath(), fileWrapper.getFile().getName());
				if (shouldRename) newPath = Paths.get(destDir.getPath(), fileWrapper.getNewName());
				if (shouldCompress) compressAndRenamePicture(fileWrapper, newPath);
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

	private void compressAndRenamePicture(FileWrapper fileWrapper, Path newPath) throws FileNotFoundException, IOException {
		Iterator<?> iter = ImageIO.getImageWritersByFormatName(fileWrapper.getExt());
		ImageWriter writer = (ImageWriter) iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

		iwp.setCompressionQuality(fileWrapper.getQuality());

		File outputFile = newPath.toFile();
		FileImageOutputStream output = new FileImageOutputStream(outputFile);
		writer.setOutput(output);
		BufferedImage img = ImageIO.read(fileWrapper.getFile());
		IIOImage image = new IIOImage(img, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
	}
}
