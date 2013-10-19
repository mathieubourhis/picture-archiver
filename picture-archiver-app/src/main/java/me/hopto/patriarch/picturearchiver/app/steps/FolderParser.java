package me.hopto.patriarch.picturearchiver.app.steps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import org.apache.log4j.Logger;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.archive.zip.CheckedZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

public class FolderParser {
	private static Logger						logger	= Logger.getLogger(FolderParser.class);
	private final String						rootPath;
	private final List<FileWrapper>	files;

	public FolderParser(String rootPath) {
		this.rootPath = rootPath;
		files = new ArrayList<FileWrapper>();
		TConfig config = TConfig.get();
		config.setOutputPreferences(config.getOutputPreferences().set(FsOutputOption.GROW));
		new TArchiveDetector(TArchiveDetector.ALL, new Object[][] { { "zip", new CheckedZipDriver(IOPoolLocator.SINGLETON) { // check CRC-32
			@Override
			public Charset getCharset() {
				return Charset.defaultCharset();
			}
		} } });
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
					files.add(new FileWrapper(FileType.DIRECTORY, file));
				}
			}
		}
		return files;
	}

	public void copyTo(File destDir) throws IOException {
		if (!destDir.isDirectory()) throw new RuntimeException("not a dir");
		initArchiveDirs(destDir);
		for (FileWrapper fileWrapper : files) {
			String separator = FileSystems.getDefault().getSeparator();
			String destPath = destDir.getPath() + (destDir.getPath().endsWith(separator) ? "" : separator);
			Path targetPath = null;
			switch (fileWrapper.getFileType()) {
			case DIRECTORY:
				processNestedDir(fileWrapper, separator, destPath);
				break;
			case OTHER:
				copyFileToMiscDir(fileWrapper, destPath);
				break;
			case PICTURE:
				targetPath = compressAndRenamePicture(fileWrapper, destPath);
				break;
			case VIDEO:
				targetPath = copyFile(fileWrapper, destPath);
				break;
			default:
				break;
			}
			if (targetPath != null) {
				File lightFile = targetPath.toFile();
				new TFile(lightFile).cp_rp(new TFile(Paths.get(destDir.getPath(), "__archives", "light.zip", lightFile.getName()).toFile()));
				new TFile(fileWrapper.getFile()).cp_rp(new TFile(Paths.get(destDir.getPath(), "__archives", "unedited.zip", fileWrapper.getFile().getName()).toFile()));
			}
		}
		TVFS.umount();
	}

	private void processNestedDir(FileWrapper fileWrapper, String separator, String destPath) throws IOException {
		FolderParser folderParser = new FolderParser(fileWrapper.getFile().getPath());
		folderParser.parseDir();
		String nestedDestPath = destPath + fileWrapper.getNewName() + separator;
		File nestedDestDir = new File(nestedDestPath);
		if (!nestedDestDir.exists()) nestedDestDir.mkdirs();
		folderParser.copyTo(nestedDestDir);
	}

	private Path copyFile(FileWrapper fileWrapper, String destPath) throws IOException {
		return Files.copy(fileWrapper.getFile().toPath(), Paths.get(destPath, fileWrapper.getNewName()));
	}

	private void copyFileToMiscDir(FileWrapper fileWrapper, String destPath) throws IOException {
		File file = Paths.get(destPath, "__misc").toFile();
		if (!file.exists()) file.mkdirs();
		Files.copy(fileWrapper.getFile().toPath(), Paths.get(destPath, "__misc", fileWrapper.getNewName()));
	}

	private void initArchiveDirs(File destDir) {
		File file = Paths.get(destDir.getPath(), "__archives").toFile();
		if (!file.exists()) file.mkdirs();
	}

	private Path compressAndRenamePicture(FileWrapper fileWrapper, String destPath) throws FileNotFoundException, IOException {
		Iterator<?> iter = ImageIO.getImageWritersByFormatName(fileWrapper.getExt());
		ImageWriter writer = (ImageWriter) iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

		iwp.setCompressionQuality(fileWrapper.getQuality());
		Path path = Paths.get(destPath, fileWrapper.getNewName());
		File outputFile = path.toFile();
		FileImageOutputStream output = new FileImageOutputStream(outputFile);
		writer.setOutput(output);
		BufferedImage img = ImageIO.read(fileWrapper.getFile());
		IIOImage image = new IIOImage(img, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
		return path;
	}
}
