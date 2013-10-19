package me.hopto.patriarch.picturearchiver.core.files;

import java.io.File;

public class FileWrapper {
	private final FileType	fileType;
	private final File			file;
	private String					ext;
	private String					newName;
	private long						size;
	private float						quality;

	public FileWrapper(FileType fileType, File file) {
		this.file = file;
		this.fileType = fileType;
	}

	/**
	 * @return the quality
	 */
	public float getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(float quality) {
		this.quality = quality;
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
	 * @param newName the newName to set
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
