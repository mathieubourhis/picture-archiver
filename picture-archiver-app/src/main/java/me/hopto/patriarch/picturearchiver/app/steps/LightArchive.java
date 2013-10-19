package me.hopto.patriarch.picturearchiver.app.steps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import me.hopto.patriarch.picturearchiver.core.files.FileTree;
import me.hopto.patriarch.picturearchiver.core.files.FileWrapper;
import com.google.common.base.Preconditions;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.archive.zip.CheckedZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

public class LightArchive {
	public LightArchive() {
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

	public void lightArchiveDestTree(FileTree destTree) throws IOException {
		File destDir = destTree.getRootDir();
		Preconditions.checkArgument(destDir.exists(), "Dest dir should have been created, like, 2ms ago.. wtf are you doing");
		for (FileWrapper fileWrapper : destTree.getFiles()) {
			new TFile(fileWrapper.getFile()).cp_rp(new TFile(Paths.get(destDir.getPath(), "__archives", "light.zip", fileWrapper.getFile().getName()).toFile()));
		}
		for (FileTree subTree : destTree.getDirs()) {
			lightArchiveDestTree(subTree);
		}
		TVFS.umount();
	}
}
