package keystone;

import junit.framework.TestCase;
import keystone.natives.DirectMappingKeystoneNative;
import org.apache.commons.logging.LogFactory;

public class ArmTest extends TestCase {

    public void testArm64() {
        LogFactory.getLog(DirectMappingKeystoneNative.class).debug("abc");
        assertTrue(Keystone.isArchitectureSupported(KeystoneArchitecture.Arm));
        assertTrue(Keystone.isArchitectureSupported(KeystoneArchitecture.Arm64));
        try (Keystone keystone = new Keystone(KeystoneArchitecture.Arm64, KeystoneMode.LittleEndian)) {
            KeystoneEncoded encoded = keystone.assemble("NOP");
            assertNotNull(encoded.getMachineCode());
        }
    }

}
