package ru.axetta.ecafe.processor.web.partner.meals.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.meals.MealsController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class AupdPublicKey {
    private Logger logger = LoggerFactory.getLogger(AupdPublicKey.class);
    public static final String PUBLIC_KEY_URL_FOR_MEALS = "ecafe.processor.meals.aupd.public.key.url";
    public static final String PUBLIC_KEY_URL_FOR_MEALS_DEFAULT = "https://school-dev.mos.ru/v1/token/public/key";

    private byte[] key = null;

    public byte[] getKey() {
        if (key == null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                byte[] chunk = new byte[4096];
                int bytesRead;
                String url = RuntimeContext.getInstance().getConfigProperties().getProperty(PUBLIC_KEY_URL_FOR_MEALS, PUBLIC_KEY_URL_FOR_MEALS_DEFAULT);
                InputStream stream = new URL(url).openStream();
                while ((bytesRead = stream.read(chunk)) > 0) {
                    outputStream.write(chunk, 0, bytesRead);
                }
            } catch (IOException e) {
                logger.error("Can not get public key from server: ", e);
                return null;
            }
            key = outputStream.toByteArray();
        }
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
