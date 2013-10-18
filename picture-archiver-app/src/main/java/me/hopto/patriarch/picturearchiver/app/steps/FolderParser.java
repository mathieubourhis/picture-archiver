package me.hopto.patriarch.picturearchiver.app.steps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import org.apache.log4j.Logger;

public class FolderParser {
	private static Logger						logger	= Logger.getLogger(FolderParser.class);
	private final String						rootPath;
	private final List<FileWrapper>	files;

	public FolderParser(String rootPath) {
		this.rootPath = rootPath;
		files = new ArrayList<FileWrapper>();
	}

	/** @return the rootPath */
	public String getRootPath() {
		return rootPath;
	}

	public List<FileWrapper> parseDir() throws IOException {
		files.clear();
		File rootDir = new File(rootPath);
		if (rootDir.exists() && rootDir.isDirectory()) {
			File[] listFiles = rootDir.listFiles();
			int digits = 2;
			int index = 0;
			String format = String.format("%%0%dd", digits);
			for (File file : listFiles) {
				if (!file.isDirectory()) {
					if (logger.isDebugEnabled()) logger.debug(file.getPath());
					String probeContentType = Files.probeContentType(file.toPath());
					String type = probeContentType.split("/")[0];
					if (type.equals("image")) files.add(new FileWrapper(FileType.PICTURE, file, index++, format));
					else if (type.equals("video")) files.add(new FileWrapper(FileType.VIDEO, file, index++, format));
					else files.add(new FileWrapper(FileType.OTHER, file));
				} else {
					if (logger.isDebugEnabled()) logger.debug("We don't treat nested folders for now");
				}
			}
		}
		return files;
	}

	public void copyTo(File destDir) throws IOException {
		if (!destDir.isDirectory()) throw new RuntimeException("not a dir");
		for (FileWrapper fileWrapper : files) {
			String separator = FileSystems.getDefault().getSeparator();
			String destPath = destDir.getPath() + (destDir.getPath().endsWith(separator) ? "" : separator);
			if (logger.isDebugEnabled()) logger.debug(destPath + fileWrapper.getNewName());
			if (fileWrapper.getFileType() == FileType.PICTURE) {
				Iterator<?> iter = ImageIO.getImageWritersByFormatName(fileWrapper.getExt());
				ImageWriter writer = (ImageWriter) iter.next();
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwp.setCompressionQuality(0.30f);   // an integer between 0 and 1
				// 1 specifies minimum compression and maximum quality
				File file = new File(destPath + fileWrapper.getNewName());
				FileImageOutputStream output = new FileImageOutputStream(file);
				writer.setOutput(output);
				BufferedImage img = ImageIO.read(fileWrapper.getFile());
				IIOImage image = new IIOImage(img, null, null);
				writer.write(null, image, iwp);
				writer.dispose();
			}
		}
	}

	public void copyTo2(File destDir) throws IOException {
		if (!destDir.isDirectory()) throw new RuntimeException("not a dir");
		for (FileWrapper fileWrapper : files) {
			String separator = FileSystems.getDefault().getSeparator();
			String destPath = destDir.getPath() + (destDir.getPath().endsWith(separator) ? "" : separator);
			if (logger.isDebugEnabled()) logger.debug(destPath + fileWrapper.getNewName());
			if (fileWrapper.getFileType() == FileType.PICTURE) {

			}
		}
	}
}
