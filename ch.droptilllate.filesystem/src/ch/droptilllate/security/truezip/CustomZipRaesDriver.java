package ch.droptilllate.security.truezip;

import java.util.zip.Deflater;

import de.schlichtherle.truezip.crypto.raes.RaesParameters;
import de.schlichtherle.truezip.fs.FsController;
import de.schlichtherle.truezip.fs.FsModel;
import de.schlichtherle.truezip.fs.archive.zip.raes.SafeZipRaesDriver;
import de.schlichtherle.truezip.key.sl.KeyManagerLocator;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

final class CustomZipRaesDriver extends SafeZipRaesDriver {
    final RaesParameters param;
    
    CustomZipRaesDriver(char[] password) {
        super(IOPoolLocator.SINGLETON, KeyManagerLocator.SINGLETON);
        param = new CustomRaesParameters(password);
    }
    
    @Override
    public int getLevel() {
		return Deflater.BEST_SPEED;	    	
    }
    
    @Override
    protected RaesParameters raesParameters(FsModel model) {
        // If you need the URI of the particular archive file, then call
        // model.getMountPoint().toUri().
        // If you need a more user friendly form of this URI, then call
        // model.getMountPoint().toHierarchicalUri().
        
        // Let's not use the key manager but instead our custom parameters.
        return param;
    }
    
    @Override
    public <M extends FsModel> FsController<M> decorate(
            FsController<M> controller) {
        // This is a minor improvement: The default implementation decorates
        // the default file system controller chain with a package private
        // file system controller which uses the key manager to keep track
        // of the encryption parameters.
        // Because we are not using the key manager, we don't1 need this
        // special purpose file system controller and can simply return the
        // given file system controller chain instead.
        return controller;
    }
} 
