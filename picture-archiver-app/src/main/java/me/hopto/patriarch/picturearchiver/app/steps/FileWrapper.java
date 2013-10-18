package me.hopto.patriarch.picturearchiver.app.steps;

import java.io.File;
import com.google.common.io.Files;

public class FileWrapper {
	private final FileType	fileType;
	private final File			file;
	private final String		ext;
	private final int				index;
	private final String		format;
	private final String		newName;

	public FileWrapper(FileType fileType, File file, int index, String format) {
		this.file = file;
		this.fileType = fileType;
		ext = Files.getFileExtension(file.getName());
		this.index = index;
		this.format = format;
		if (fileType == FileType.OTHER) newName = file.getName();
		else {
			String id = String.format(format, index);
			newName = file.getParentFile().getName() + "_" + id + "." + ext;
		}
	}

	public FileWrapper(FileType fileType, File file) {
		this.file = file;
		this.fileType = fileType;
		ext = Files.getFileExtension(file.getName());
		index = 0;
		format = null;
		if (fileType == FileType.OTHER) newName = file.getName();
		else {
			String id = String.format(format, index);
			newName = file.getParentFile().getName() + "_" + id + "." + ext;
		}
	}

	/** @return the fileType */
	public FileType getFileType() {
		return fileType;
	}

	/** @return the file */
	public File getFile() {
		return file;
	}

	public String getNewName() {
		return newName;
	}

	/**
	 * @return the ext
	 */
	public String getExt() {
		return ext;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
}
