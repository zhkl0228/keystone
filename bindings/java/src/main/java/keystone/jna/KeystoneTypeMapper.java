/*
 * Copyright (c) 2018 Jämes Ménétrey <james@menetrey.me>
 *
 * This file is part of the Keystone Java bindings which is released under MIT.
 * See file LICENSE in the Java bindings folder for full license details.
 */

package keystone.jna;

import keystone.*;

/**
 * Extends the numeration type mapper in order to register the enumeration used by Keystone.
 */
public class KeystoneTypeMapper extends EnumTypeMapper {
    public KeystoneTypeMapper() {
        addTypeConverter(KeystoneError.class, new Function() {
            @Override
            public Object apply(int nativeValue) {
                return KeystoneError.fromValue(nativeValue);
            }
        });
        addTypeConverter(KeystoneArchitecture.class, new Function() {
            @Override
            public Object apply(int nativeValue) {
                return KeystoneArchitecture.fromValue(nativeValue);
            }
        });
        addTypeConverter(KeystoneMode.class, new Function() {
            @Override
            public Object apply(int nativeValue) {
                return KeystoneMode.fromValue(nativeValue);
            }
        });
        addTypeConverter(KeystoneOptionType.class, new Function() {
            @Override
            public Object apply(int nativeValue) {
                return KeystoneOptionType.fromValue(nativeValue);
            }
        });
        addTypeConverter(KeystoneOptionValue.KeystoneOptionSyntax.class, new Function() {
            @Override
            public Object apply(int nativeValue) {
                return KeystoneOptionValue.KeystoneOptionSyntax.fromValue(nativeValue);
            }
        });
    }
}
