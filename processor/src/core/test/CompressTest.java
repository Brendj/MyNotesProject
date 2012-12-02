/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

import ru.axetta.ecafe.processor.core.utils.CompressUtils;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class CompressTest {
    @Test
    public void testCompress() throws Exception {
        File f = new File("c:\\tmp\\menu.test");
        int l=(int)f.length();
        FileInputStream in = new FileInputStream(f);
        byte[] byteData=new byte[l];
        in.read(byteData);
        in.close();
        String data=new String(byteData, "UTF-8");
        String compressed = CompressUtils.compressDataInBase64(data);
        System.out.println("COMPRESSED: "+compressed.length());
        String decompressed = CompressUtils.decompressDataFromBase64(compressed);
        System.out.println("DECOMPRESSED: "+decompressed.length());
        assert(data.equals(decompressed));
    }

}
