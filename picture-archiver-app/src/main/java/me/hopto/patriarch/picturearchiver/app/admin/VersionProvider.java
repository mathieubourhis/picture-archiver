package me.hopto.patriarch.pictureupdater.app.admin;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public final class VersionProvider {

	private static Logger									logger		= Logger.getLogger(VersionProvider.class);

	private static final VersionProvider	INSTANCE	= new VersionProvider();
	private String												version;

	private VersionProvider() {
		ResourceBundle rb;
		try {
			rb = ResourceBundle.getBundle("version");
			version = rb.getString("application.version");
		} catch (MissingResourceException e) {
			logger.warn("Resource bundle 'version' was not found or error while reading current version.");
		}
	}

	public static String getVersion() {
		return INSTANCE.version;
	}
}
