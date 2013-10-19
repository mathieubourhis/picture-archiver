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
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileType;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;
import org.apache.log4j.Logger;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.archive.zip.CheckedZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

public class FolderParser {
	private static Logger	logger	= Logger.getLogger(FolderParser.class);

	public FolderParser() {
		TConfig config = TConfig.get();
		config.setOutputPreferences(config.getOutputPreferences().set(FsOutputOption.GROW));
		new TArchiveDetector(TArchiveDetector.ALL, new Object[][] { { "zip", new CheckedZipDriver(IOPoolLocator.SINGLETON) {
			@Override
			public Charset getCharset() {
				return Charset.defaultCharset();
			}
		} } });
	}

	public FileTree tree(FileTree rootTree) throws IOException {
		File rootDir = rootTree.getRootDir();
		if (rootDir.exists() && rootDir.isDirectory()) {
			File[] listFiles = rootDir.listFiles();

			for (File file : listFiles) {
				// delete file.getName().equals("Thumbs.db")
				if (!file.isDirectory()) {
					String probeContentType = Files.probeContentType(file.toPath());
					String type = "other"; // for exotic file ext
					if (probeContentType != null) type = probeContentType.split("/")[0];
					if (type.equals("image")) {
						rootTree.add(new FileWrapper(FileType.PICTURE, file));
					} else if (type.equals("video")) {
						rootTree.add(new FileWrapper(FileType.VIDEO, file));
					} else {
						rootTree.add(new FileWrapper(FileType.OTHER, file));
					}
				} else {
					rootTree.add(new FileWrapper(FileType.DIRECTORY, file));
					FileTree subDir = rootTree.addDir(file);
					tree(subDir);
				}
			}
		}

		return rootTree;
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public void copyTreeTo(FileTree tree, File destDir) throws IOException {
		//		if (!destDir.isDirectory()) throw new RuntimeException("not a dir");
		destDir.mkdirs();
		initArchiveDirs(destDir);
		for (FileWrapper fileWrapper : tree.getFiles()) {
			String separator = FileSystems.getDefault().getSeparator();
			String destPath = destDir.getPath() + (destDir.getPath().endsWith(separator) ? "" : separator);
			Path targetPath = null;
			switch (fileWrapper.getFileType()) {
			//			case DIRECTORY:
			//				processNestedDir(fileWrapper, separator, destPath);
			//				break;
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
		for (FileTree subTree : tree.getDirs()) {
			File nestedDir = Paths.get(destDir.getPath(), subTree.getRootDir().getName()).toFile();
			copyTreeTo(subTree, nestedDir);
		}
		TVFS.umount();
	}

	//	private void processNestedDir(FileWrapper fileWrapper, String separator, String destPath) throws IOException {
	//		FolderParser folderParser = new FolderParser(fileWrapper.getFile().getPath());
	//		folderParser.parseDir();
	//		String nestedDestPath = destPath + fileWrapper.getNewName() + separator;
	//		File nestedDestDir = new File(nestedDestPath);
	//		if (!nestedDestDir.exists()) nestedDestDir.mkdirs();
	//		folderParser.copyTo(nestedDestDir);
	//	}

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
