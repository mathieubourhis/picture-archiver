package me.hopto.patriarch.picturearchiver.app.steps;

public class FolderParser {
	private final String	rootPath;

	public FolderParser(String rootPath) {
		this.rootPath = rootPath;
	}

	/** @return the rootPath */
	public String getRootPath() {
		return rootPath;
	}
}
