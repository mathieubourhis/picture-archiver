package me.hopto.patriarch.picturearchiver.app.steps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.archive.zip.CheckedZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

public class HeavyArchive {
	public HeavyArchive() {
		//TODO do i really need this shit ? 
		TConfig config = TConfig.get();
		config.setOutputPreferences(config.getOutputPreferences().set(FsOutputOption.GROW));
		new TArchiveDetector(TArchiveDetector.ALL, new Object[][] { { "zip", new CheckedZipDriver(IOPoolLocator.SINGLETON) {
			@Override
			public Charset getCharset() {
				return Charset.defaultCharset();
			}
		} } });
	}

	public void heavyArchiveTreeTo(FileTree tree, File destDir) throws IOException {
		for (FileWrapper fileWrapper : tree.getFiles()) {
			new TFile(fileWrapper.getFile()).cp_rp(new TFile(Paths.get(destDir.getPath(), "__archives", "unedited.zip", fileWrapper.getFile().getName()).toFile()));
		}
		for (FileTree subTree : tree.getDirs()) {
			File nestedDir = Paths.get(destDir.getPath(), subTree.getRootDir().getName()).toFile();
			heavyArchiveTreeTo(subTree, nestedDir);
		}
		TVFS.umount();
	}
}
