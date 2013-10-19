package me.hopto.patriarch.picturearchiver.core.files;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.google.common.io.Files;

public class FileResolver {
	private static Logger			logger				= Logger.getLogger(FileResolver.class);
	private final static long	EXPECTED_SIZE	= 250000;

	public void resolve(FileWrapper file, int index, String format) {
		this.resolveExt(file);
		this.resolveSize(file);
		this.resolveQuality(file);
		this.resolveName(file, index, format);
	}

	private void resolveName(FileWrapper file, int index, String format) {
		FileType fileType = file.getFileType();
		File sourceFile = file.getFile();
		String newName = sourceFile.getName();
		if (!(fileType == FileType.OTHER || fileType == FileType.DIRECTORY)) {
			String id = String.format(format, index);
			newName = sourceFile.getParentFile().getName() + "_" + id + "." + file.getExt();
		}
		file.setNewName(newName);
	}

	private void resolveQuality(FileWrapper file) {
		long size = file.getSize();
		float quality = .0f;
		if (size != 0) {
			quality = (float) ((60.f - 10.8f * Math.log(size / EXPECTED_SIZE)) / 100.f);
		}
		if (quality == .0f || quality > 1.0f) quality = 1.0f;
		else if (quality < .30f) quality = .30f;
		file.setQuality(quality);
	}

	private void resolveExt(FileWrapper file) {
		String ext = Files.getFileExtension(file.getFile().getName());
		file.setExt(ext);
	}

	private void resolveSize(FileWrapper file) {
		long size = 0;
		try {
			size = java.nio.file.Files.size(file.getFile().toPath());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		file.setSize(size);
	}
}
