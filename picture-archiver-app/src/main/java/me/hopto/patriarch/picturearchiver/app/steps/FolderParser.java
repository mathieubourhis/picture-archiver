package me.hopto.patriarch.picturearchiver.app.steps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
			String format = String.format("%%0%dd", digits);
			for (File file : listFiles) {
				if (!file.isDirectory()) {
					int index = 0;
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

	public void copyTo(File destDir) {
		if (!destDir.isDirectory()) throw new RuntimeException("not a dir");
		for (FileWrapper fileWrapper : files) {
			String destPath = destDir.getPath() + (destDir.getPath().endsWith("\\") ? "" : "\\");
			if (logger.isDebugEnabled()) logger.debug(destPath + fileWrapper.getNewName());
		}
	}
}
