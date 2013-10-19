package me.hopto.patriarch.picturearchiver.core.files;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.google.common.io.Files;

public class FileWrapper {
	private static Logger			logger				= Logger.getLogger(FileWrapper.class);
	private final static long	EXPECTED_SIZE	= 250000;
	private final FileType		fileType;
	private final File				file;
	private final String			ext;
	private final int					index;
	private final String			format;
	private String						newName;
	private long							size;
	private float							quality;

	/**
	 * @return the quality
	 */
	public float getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality() {
		if (size != 0) {
			quality = (float) ((60.f - 10.8f * Math.log(size / EXPECTED_SIZE)) / 100.f);
		}
		if (quality == .0f || quality > 1.0f) quality = 1.0f;
		else if (quality < .30f) quality = .30f;
	}

	public FileWrapper(FileType fileType, File file, int index, String format) {
		this.file = file;
		this.fileType = fileType;
		this.index = index;
		this.format = format;
		ext = Files.getFileExtension(file.getName());
		setNewName();
		setSize();
		setQuality();
	}

	public FileWrapper(FileType fileType, File file) {
		this(fileType, file, 0, null);
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

	/**
	 * @param newName the newName to set
	 */
	private void setNewName() {
		if (fileType == FileType.OTHER || fileType == FileType.DIRECTORY) {
			newName = file.getName();
		} else {
			String id = String.format(format, index);
			newName = file.getParentFile().getName() + "_" + id + "." + ext;
		}
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	private void setSize() {
		this.size = 0;
		try {
			this.size = java.nio.file.Files.size(file.toPath());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
