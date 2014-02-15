package ch.droptilllate.filesystem.truezip;

import de.schlichtherle.truezip.crypto.raes.RaesKeyException;
import de.schlichtherle.truezip.crypto.raes.Type0RaesParameters;

final class CustomRaesParameters implements Type0RaesParameters {
    final char[] password;
    
    CustomRaesParameters(final char[] password) 
    {
        this.password = password.clone();
    }
    
    @Override
    public char[] getWritePassword() throws RaesKeyException 
    {
        return password.clone();
    }
    
    @Override
    public char[] getReadPassword(boolean invalid) throws RaesKeyException 
    {
        if (invalid)
            throw new RaesKeyException("Invalid password!");
        return password.clone();
    }
    
    @Override
    public KeyStrength getKeyStrength() throws RaesKeyException 
    {
        return KeyStrength.BITS_256;
    }
    
    @Override
    public void setKeyStrength(KeyStrength keyStrength) throws RaesKeyException 
    {
        assert KeyStrength.BITS_256 == keyStrength;
    }
} 
