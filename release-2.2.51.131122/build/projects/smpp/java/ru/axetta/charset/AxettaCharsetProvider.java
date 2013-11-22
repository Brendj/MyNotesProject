/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashSet;

public class AxettaCharsetProvider extends java.nio.charset.spi.CharsetProvider {

    // The name of the charset we provide
    private static final String CHARSET_NAME = "x-hex";

    // A handle to the Charset object
    private Charset hexCharset = null;

    public AxettaCharsetProvider() {
        super();
        this.hexCharset = new HexCharset(CHARSET_NAME);
    }

    public Charset charsetForName(String charsetName) {
        if (charsetName.equalsIgnoreCase(CHARSET_NAME)) {
            return (hexCharset);
        }
        return (null);
    }

    public java.util.Iterator<Charset> charsets() {
        HashSet<Charset> set = new HashSet<Charset>(1);
        set.add(hexCharset);
        return (set.iterator());
    }

    private class HexCharset extends Charset {

        private HexCharset(String name) {
            super(name, new String[0]);
        }

        public boolean contains(Charset cs) {
            return true;
        }

        public CharsetEncoder newEncoder() {
            return new Encode(this);
        }

        public CharsetDecoder newDecoder() {
            return new Decode(this);
        }

        private class Encode extends CharsetEncoder {
            final String HEX_SYMBOLS = "0123456789abcdef";

            Encode(Charset cs) {
                super(cs, .5f, 1, new byte[]{0});
            }

            protected CoderResult encodeLoop(CharBuffer cb, ByteBuffer bb) {
                int n, b;
                while (cb.remaining() > 0) {
                    char c = Character.toLowerCase(cb.get());
                    n = HEX_SYMBOLS.indexOf(c);
                    b = n << 4;
                    c = Character.toLowerCase(cb.get());
                    n = HEX_SYMBOLS.indexOf(c);
                    b += n;
                    bb.put((byte) b);
                }
                return CoderResult.UNDERFLOW;
            }
        }

        private class Decode extends CharsetDecoder {
            Decode(Charset cs) {
                super(cs, 1, 2);
            }

            protected CoderResult decodeLoop(ByteBuffer bb, CharBuffer cb) {
                byte b;
                while (bb.remaining() > 0) {
                    b = bb.get();
                    cb.put(Character.forDigit((b >> 4) & 0x0f, 16));
                    cb.put(Character.forDigit(b & 0x0f, 16));
                }
                return CoderResult.UNDERFLOW;
            }
        }
    }

}

