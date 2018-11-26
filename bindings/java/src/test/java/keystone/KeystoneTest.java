/*
 * Copyright (c) 2018 Jämes Ménétrey <james@menetrey.me>
 *
 * This file is part of the Keystone Java bindings which is released under MIT.
 * See file LICENSE in the Java bindings folder for full license details.
 */

package keystone;

import com.sun.jna.ptr.LongByReference;
import junit.framework.TestCase;
import keystone.exceptions.AssembleFailedKeystoneException;
import keystone.exceptions.OpenFailedKeystoneException;
import keystone.exceptions.SetOptionFailedKeystoneException;
import keystone.utilities.Version;

import java.util.Arrays;
import java.util.LinkedList;

public class KeystoneTest extends TestCase {

    private Keystone keystone;

    protected void setUp() {
        keystone = new Keystone(KeystoneArchitecture.X86, KeystoneMode.Mode64);
    }

    public void test_ctor_ifInvalidArguments_shouldThrowAnException() {
        try {
            new Keystone(KeystoneArchitecture.Ppc, KeystoneMode.SparcV9);
            fail("An exception must be thrown upon invalid arguments are used.");
        } catch (OpenFailedKeystoneException e) {
            assertEquals(KeystoneError.Mode, e.getKeystoneError());
        }
    }

    public void test_assemble_shouldAssembleIncDec() {
        // Arrange
        String assembly = "INC EAX;DEC EAX";
        byte[] expectedMachineCode = new byte[]{(byte) 0xFF, (byte) 0xC0, (byte) 0xFF, (byte) 0xC8};
        int expectedNumberOfStatements = 2;

        // Act
        KeystoneEncoded encoded = keystone.assemble(assembly);

        // Assert
        assertTrue(Arrays.equals(expectedMachineCode, encoded.getMachineCode()));
        assertEquals(expectedNumberOfStatements, encoded.getNumberOfStatements());
    }

    public void test_assemble_withAddress_shouldAssembleDoubleNop() {
        // Arrange
        String assembly = "NOP;NOP";
        byte[] expectedMachineCode = new byte[]{(byte) 0x90, (byte) 0x90};
        int expectedNumberOfStatements = 2;
        int expectedAddress = 0x200;

        // Act
        KeystoneEncoded encoded = keystone.assemble(assembly, expectedAddress);

        // Assert
        assertTrue(Arrays.equals(expectedMachineCode, encoded.getMachineCode()));
        assertEquals(expectedNumberOfStatements, encoded.getNumberOfStatements());
        assertEquals(expectedAddress, encoded.getAddress());
    }

    public void test_assemble_ifAssemblyCodeInvalid_shouldThrowAnException() {
        // Arrange
        String assembly = "UNK";

        // Act and Assert
        try {
            keystone.assemble(assembly, 0);
            fail("The assembly instruction is invalid. It should not pass the unit test.");
        } catch (AssembleFailedKeystoneException e) {
            assertEquals(KeystoneError.AsmMnemonicFail, e.getKeystoneError());
            assertEquals(assembly, e.getAssembly());
        }
    }

    public void test_assemble_withCollectionAndAddress_shouldAssembleIncDec() {
        // Arrange
        LinkedList<String> assembly = new LinkedList<>();
        byte[] expectedMachineCode = new byte[]{(byte) 0xFF, (byte) 0xC0, (byte) 0xFF, (byte) 0xC8};
        int expectedNumberOfStatements = 2;

        // Act
        assembly.add("INC EAX");
        assembly.add("DEC EAX");
        KeystoneEncoded encoded = keystone.assemble(assembly);

        // Assert
        assertTrue(Arrays.equals(expectedMachineCode, encoded.getMachineCode()));
        assertEquals(expectedNumberOfStatements, encoded.getNumberOfStatements());
    }

    public void test_assemble_withSymbolWithoutResolver_shouldFail() {
        // Arrange
        String assembly = "MOV EAX, TEST";

        // Act and Assert
        try {
            keystone.assemble(assembly);
            fail("The assembly instruction is composed of an undefined symbol. It should not pass the unit test");
        } catch (AssembleFailedKeystoneException e) {
            assertEquals(KeystoneError.AsmSymbolMissing, e.getKeystoneError());
        }
    }

    public void test_setAssemblySyntax_withAttSyntax_shouldBeEqualToX86Syntax() {
        // Act
        KeystoneEncoded x86Result = keystone.assemble("INC ECX; DEC EDX");
        keystone.setAssemblySyntax(KeystoneOptionValue.KeystoneOptionSyntax.Att);
        KeystoneEncoded attResult = keystone.assemble("INC %ecx; DEC %edx");

        // Assert
        assertTrue(Arrays.equals(x86Result.getMachineCode(), attResult.getMachineCode()));
    }

    public void test_setOption_ifInvalidArguments_shouldTrowAnException() {
        // Arrange
        KeystoneOptionType expectedType = KeystoneOptionType.Syntax;
        int invalidValue = -1;

        // Act and Assert
        try {
            keystone.setOption(expectedType, invalidValue);
        } catch (SetOptionFailedKeystoneException e) {
            assertEquals(KeystoneError.OptInvalid, e.getKeystoneError());
            assertEquals(expectedType, e.getOptionType());
            assertEquals(invalidValue, e.getOptionValue());
        }
    }

    public void test_setSymbolResolver_assembleCustomSymbol_shouldProduceValidAssemblyCode() {
        // Arrange
        final String expectedSymbol = "TEST";
        final byte expectedValue = (byte) 0x66;
        byte movOpcode = (byte) 0xB8;
        String assembly = "MOV EAX, " + expectedSymbol;
        SymbolResolverCallback symbolResolver = new SymbolResolverCallback() {
            @Override
            public boolean onResolve(String symbol, LongByReference value) {
                assertEquals(expectedSymbol, symbol);

                value.setValue(expectedValue);
                return true;
            }
        };

        // Act
        keystone.setSymbolResolver(symbolResolver);
        KeystoneEncoded assemblyCode = keystone.assemble(assembly);

        // Assert
        assertEquals(1, assemblyCode.getNumberOfStatements());
        assertEquals(movOpcode, assemblyCode.getMachineCode()[0]);
        assertEquals(expectedValue, assemblyCode.getMachineCode()[1]);
    }

    public void test_unsetSymbolResolver_assembleCustomSymbol_shouldfailBecauseTheCallbackHasBeenUnset() {
        // Arrange
        final String expectedSymbol = "TEST";
        final byte expectedValue = (byte) 0x66;
        String assembly = "MOV EAX, " + expectedSymbol;
        SymbolResolverCallback symbolResolver = new SymbolResolverCallback() {
            @Override
            public boolean onResolve(String symbol, LongByReference value) {
                assertEquals(expectedSymbol, symbol);

                value.setValue(expectedValue);
                return true;
            }
        };

        // Act and Assert
        keystone.setSymbolResolver(symbolResolver);
        keystone.unsetSymbolResolver();

        try {
            keystone.assemble(assembly);
            fail("The assembly instruction is composed of an undefined symbol and no resolver should be available.");
        } catch (AssembleFailedKeystoneException e) {
            assertEquals(KeystoneError.AsmSymbolMissing, e.getKeystoneError());
        }
    }

    public void test_isArchitectureSupported_shouldSupportX86Everywhere() {
        assertTrue(Keystone.isArchitectureSupported(KeystoneArchitecture.X86));
    }

    public void test_version_shouldBeDifferentFromZero() {
        assertEquals(1, keystone.version().compareTo(new Version(0, 0)));
    }

    public void test_close_shouldNotThrowAnyException() {
        keystone.close();
    }
}