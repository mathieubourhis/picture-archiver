package me.hopto.patriarch.picturearchiver.core.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class FileTree {
	/**
	 * @return the parent
	 */
	public FileTree getParent() {
		return parent;
	}

	private static Logger						logger								= Logger.getLogger(FileTree.class);
	private final FileResolver			fileResolver;
	private final int								digits;
	private int											index;
	private final String						format;
	private final List<FileWrapper>	files;
	private final List<FileTree>		dirs;
	private FileTree								parent;
	private final File							rootDir;
	private long										pictureSizes					= 0;
	private long										videoSizes						= 0;
	private long										otherSize							= 0;
	private String									picturesExtList				= "";
	private String									videosExtList					= "";
	private String									otherExtList					= "";
	public static long							totalPictureSizes			= 0;
	public static long							totalOthersSizes			= 0;
	public static long							totalVideoSizes				= 0;
	public static String						totalPicturesExtList	= "";
	public static String						totalVideosExtList		= "";
	public static String						totalOtherExtList			= "";

	/**
	 * @return the rootDir
	 */
	public File getRootDir() {
		return rootDir;
	}

	public FileTree(File rootdir) {
		this.files = new ArrayList<FileWrapper>();
		this.dirs = new ArrayList<FileTree>();
		this.fileResolver = new FileResolver();
		this.digits = 2;
		this.index = 0;
		this.format = String.format("%%0%dd", digits);
		this.rootDir = rootdir;
	}

	public FileTree(FileTree parent, File rootdir) {
		this(rootdir);
		this.parent = parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public FileWrapper add(FileWrapper file) {
		fileResolver.resolve(file, index++, format);
		computeStats(file);
		files.add(file);
		return file;
	}

	private void computeStats(FileWrapper file) {
		String ext = file.getExt().toUpperCase();
		switch (file.getFileType()) {
		case OTHER:
			otherSize += file.getSize();
			totalOthersSizes += file.getSize();
			if (!otherExtList.contains(ext)) otherExtList = otherExtList + (otherExtList.equals("") ? "" : ",") + ext;
			if (!totalOtherExtList.contains(ext)) totalOtherExtList = totalOtherExtList + (totalOtherExtList.equals("") ? "" : ",") + ext;
			break;
		case PICTURE:
			if (!picturesExtList.contains(ext)) picturesExtList = picturesExtList + (picturesExtList.equals("") ? "" : ",") + ext;
			if (!totalPicturesExtList.contains(ext)) totalPicturesExtList = totalPicturesExtList + (totalPicturesExtList.equals("") ? "" : ",") + ext;
			pictureSizes += file.getSize();
			totalPictureSizes += file.getSize();
			break;
		case VIDEO:
			if (!videosExtList.contains(ext)) videosExtList = videosExtList + (videosExtList.equals("") ? "" : ",") + ext;
			if (!totalVideosExtList.contains(ext)) totalVideosExtList = totalVideosExtList + (totalVideosExtList.equals("") ? "" : ",") + ext;
			videoSizes += file.getSize();
			totalVideoSizes += file.getSize();
			break;
		default:
			break;
		}
	}

	public FileTree addDir(File rootdir) {
		FileTree fileTree = new FileTree(this, rootdir);
		dirs.add(fileTree);
		return fileTree;
	}

	/**
	 * @return the files
	 */
	public List<FileWrapper> getFiles() {
		return files;
	}

	private String getIndent(FileTree fileTree) {
		if (fileTree.hasParent()) return getIndent(fileTree.parent) + "--";
		return "|--";
	}

	public void prettyPrint() {
		String path = getIndent(this) + rootDir.getName();
		if (logger.isDebugEnabled()) logger.debug(padRight(path, 40) + humanReadableByteCount(pictureSizes, true) + " [" + padRight(videosExtList, 7) + "] " + humanReadableByteCount(videoSizes, true) + " [" + padRight(videosExtList, 7) + "] " + humanReadableByteCount(otherSize, true) + " [" + padRight(otherExtList, 7) + "] ");
		for (FileTree dir : dirs) {
			dir.prettyPrint();
		}
		if (!hasParent() && logger.isDebugEnabled()) logger.debug("[TOTAL] " + humanReadableByteCount(totalPictureSizes, true) + " [" + totalPicturesExtList + "] " + humanReadableByteCount(totalVideoSizes, true) + " [" + totalVideosExtList + "] " + humanReadableByteCount(totalOthersSizes, true) + " [" + totalOtherExtList + "] ");
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return padLeft(bytes + "    B", 10);
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		String formattedSize = String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
		return padLeft(formattedSize, 10);
	}

	/**
	 * @return the dirs
	 */
	public List<FileTree> getDirs() {
		return dirs;
	}

}
