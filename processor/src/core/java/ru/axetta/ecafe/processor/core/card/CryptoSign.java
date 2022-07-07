/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

import ru.CryptoPro.JCP.JCP;

import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.service.SummaryCardsMSRService;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class CryptoSign {

    public static final String BP160R1 = "brainpoolP160r1";
    public static final String BC_PROV = "BC";
    public static final String ALGORITHM = "SHA1withECDSA";
    public static final String KEY_FACTOR = "ECDSA";
    public static final Integer SIZE_DATE = 12;
    private static Object sync = new Object();
    private static boolean bc_loaded = false;

    public static KeyPair keyPairGen() throws Exception {
        loadProviderBC();
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(BP160R1);
        KeyPairGenerator g = KeyPairGenerator.getInstance(KEY_FACTOR, BC_PROV);
        g.initialize(ecSpec, new SecureRandom());
        KeyPair pair = g.generateKeyPair();
        return pair;
    }

    private static void loadProviderBC() {
        synchronized (sync) {
            if (bc_loaded) return;

            List<Provider> providers = Arrays.asList(Security.getProviders());
            boolean loaded = (providers.stream().filter(prov -> prov.getName().equals(BC_PROV))).collect(Collectors.toList()).size() > 0;
            if (!loaded) {
                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            }
            bc_loaded = true;
        }
    }

    public static byte[] sign(byte[] data, PrivateKey priv) throws Exception {
        Signature dsa = Signature.getInstance(ALGORITHM, BC_PROV);
        dsa.initSign(priv);
        dsa.update(data);
        return dsa.sign();
    }

    //Проверка карты на её наличие в базе
    private static boolean verifyCardforDuble(Long num) {
        Long shortNum = SummaryCardsMSRService.convertCardId(num);
        if (DAOReadonlyService.getInstance().getCardfromNum(shortNum) == null) {
            return true;
        } else {
            return false;
        }
    }


    public static List<ResponseCardSign> createSignforCard(List<RequestCardForSign> cards, CardSign cardSign)
            throws Exception {
        List<ResponseCardSign> responseCardSigns = new ArrayList<ResponseCardSign>();
        boolean sucsess;
        for (RequestCardForSign card : cards) {
            sucsess = true;
            ResponseCardSign responseCardSign = new ResponseCardSign();
            try {
                byte[] sign;
                responseCardSign.setUid(card.getUid());
                if (!verifyCardforDuble(card.getUid())) {
                    responseCardSign.setMessage("Карта с таким номером уже зарегистирована");
                    sucsess = false;
                }
                if (card.getTypeId() != 1 && card.getTypeId() != 9 && card.getTypeId() != 10 && card.getTypeId() != 11
                        && card.getTypeId() != 12 && card.getTypeId() != 13 && card.getTypeId() != 14
                        && card.getTypeId() != 15) {
                    responseCardSign.setMessage("Неверный тип носителя");
                    sucsess = false;
                }
                //card.getMemSize() == 1 - Если тип карты поддерживает данный более 128 байт
                //card.getMemSize() == 2 - Если тип карты не поддерживает данный более 128 байт
                //cardSign.getSignType() == 1 - ECDSA
                //cardSign.getSignType() == 2 - Scrypt
                if (!((card.getMemSize() == 1 && cardSign.getSignType() == 1) || (card.getMemSize() == 2 && cardSign.getSignType() == 0))) {
                    sucsess = false;
                    responseCardSign.setMessage("Тип подписи для карты задан некорректно");
                }
                //Маленькие карты и тип Тройка-Москвенок
                if (card.getMemSize() == 2 && (card.getTypeId() == 12 || card.getTypeId() == 13 || card.getTypeId() == 14)) {
                    sucsess = false;
                    responseCardSign.setMessage("Неверный тип носителя");
                }
                if (sucsess) {//Подписываем карту только если пройдены проверки
                    //Подготавливаем данные для подписи
                    byte[] fiz = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(card.getUid()).array();
                    byte[] type = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(card.getTypeId()).array();
                    byte[] num = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(card.getPrinted_no()).array();
                    byte[] kod = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(cardSign.getManufacturerCode())
                            .array();
                    byte[] data = new byte[3];
                    data[0] = (byte) (card.getIssuedate().get(Calendar.YEAR) - 2000);
                    data[1] = (byte) (card.getIssuedate().get(Calendar.MONTH) + 1);
                    data[2] = (byte) (card.getIssuedate().get(Calendar.DAY_OF_MONTH));
                    byte[] sert = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(cardSign.getIdOfCardSign())
                            .array();

                    //Здесь сформируется конечный вариант
                    byte[] card_data = new byte[32];
                    System.arraycopy(fiz, 1, card_data, 0, 7);
                    System.arraycopy(type, 1, card_data, 7, 1);
                    System.arraycopy(num, 3, card_data, 8, 5);
                    System.arraycopy(kod, 3, card_data, 13, 1);
                    System.arraycopy(data, 0, card_data, 14, 3);
                    System.arraycopy(sert, 2, card_data, 17, 2);

                    //Если тип карты поддерживает данный более 128 байт
                    if (card.getMemSize() == 1) {
                        //Достаем приватный ключ для подписи
                        PrivateKey pk = loadPrivKey(cardSign.getPrivatekeycard());
                        //Подписывание
                        sign = CryptoSign.sign(card_data, pk);
                    } else {
                        if (card.getMemSize() == 2) {
                            byte[] pk = loadPrivKeySCRIPT(cardSign.getPrivatekeycard());
                            sign = SCrypt.generate(pk, card_data, //данные карты используем как "соль"
                                    16384, 8, 1, 20);
                        } else {
                            sign = new byte[]{0};
                            responseCardSign.setMessage("Тип карты задан некорректно");
                            sucsess = false;
                        }
                    }
                    //Размер ответа фиксированный
                    byte[] allData = new byte[SIZE_DATE + sign.length];

                    //Здесь возвращаем тип подписи
                    responseCardSign.setMemSize(card.getMemSize());

                    //Сохраняем сами подписи
                    System.arraycopy(card_data, 7, allData, 0, SIZE_DATE);
                    System.arraycopy(sign, 0, allData, SIZE_DATE, sign.length);
                    responseCardSign.setAllDate(bytesToHex(allData));
                }
            } catch (Exception e) {
                responseCardSign.setInsideError("Неизвестная ошибка при подписании карты");
                sucsess = false;
            } finally {
                if (!sucsess) {
                    responseCardSign.setAllDate(null);
                }
                responseCardSigns.add(responseCardSign);
            }
        }
        return responseCardSigns;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] createAllDate(List<RequestCardForSign> cards, CardSign cardSign) {
        //17 байт для хранений информации о одной карте + 1 байт для id поставщика
        byte[] allDate = new byte[17 * cards.size() + 1];
        byte[] kod = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(cardSign.getManufacturerCode()).array();
        System.arraycopy(kod, 3, allDate, 0, 1);
        //17 байт информация об одной карте
        byte[] card_data = new byte[17];
        for (int i = 0; i < cards.size(); i++) {
            byte[] fiz = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(cards.get(i).getUid()).array();
            byte[] type = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(cards.get(i).getTypeId()).array();
            byte[] num = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(cards.get(i).getPrinted_no()).array();
            byte[] data = new byte[3];
            data[0] = (byte) (cards.get(i).getIssuedate().get(Calendar.YEAR) - 2000);
            data[1] = (byte) (cards.get(i).getIssuedate().get(Calendar.MONTH) + 1);
            data[2] = (byte) (cards.get(i).getIssuedate().get(Calendar.DAY_OF_MONTH));
            byte[] memSize = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort((short) (cards.get(i).getMemSize()))
                    .array();
            //17 байт информация об одной карте
            System.arraycopy(num, 3, card_data, 0, 5);
            System.arraycopy(type, 1, card_data, 5, 1);
            System.arraycopy(fiz, 1, card_data, 6, 7);
            System.arraycopy(data, 0, card_data, 13, 3);
            System.arraycopy(memSize, 1, card_data, 16, 1);
            //Добавляем в общий массив данных
            System.arraycopy(card_data, 0, allDate, 17 * i + 1, 17);
        }
        return allDate;
    }

    public static boolean verifySignforProvider(List<RequestCardForSign> requestCardForSigns, byte[] signProvider,
            CardSign cardSign) throws Exception {
        byte[] date = createAllDate(requestCardForSigns, cardSign);
        if (cardSign.getSigntypeprov() == 0) {
            return CryptoSign.verifySCRIPT(date, signProvider, cardSign);
        }
        if (cardSign.getSigntypeprov() == 1) {
            return CryptoSign.verifyECDSA(date, signProvider, cardSign);
        }
        if (cardSign.getSigntypeprov() == 2) {
            return CryptoSign.verifyGOST2012(date, signProvider, cardSign);
        }
        return false;
    }


    private static boolean verifyGOST2012(byte[] data, byte[] sign, CardSign cardSign) throws Exception {
        Signature signature = Signature.getInstance(JCP.GOST_EL_SIGN_NAME, JCP.PROVIDER_NAME);
        signature.initVerify(loadPubKey(cardSign.getPublickeyprovider()));
        signature.update(MessageDigest.getInstance(JCP.GOST_DIGEST_NAME, JCP.PROVIDER_NAME).digest(data));
        return signature.verify(sign);
    }

    private static boolean verifyECDSA(byte[] data, byte[] sign, CardSign cardSign) throws Exception {
        loadProviderBC();
        Signature dsa = Signature.getInstance(ALGORITHM, BC_PROV);
        dsa.initVerify(loadPubKey(cardSign.getPublickeyprovider()));
        dsa.update(data);
        return dsa.verify(sign);
    }

    private static boolean verifySCRIPT(byte[] dateCards, byte[] sign, CardSign cardSign) throws Exception {

        byte[] varsign = SCrypt.generate(loadPrivKeySCRIPT(cardSign.getPublickeyprovider()), dateCards,
                //данные карты используем как "соль"
                16384, 8, 1, 20);
        return Arrays.equals(varsign, sign);
    }

    public static PublicKey loadPubKey(byte[] data) throws Exception {
        loadProviderBC();
        List<String> arrayKey = Arrays.asList(StringUtils.split(new String(data), '\n'));
        String rezult = "";
        for (int i = 1; i < arrayKey.size() - 1; i++) {
            rezult += arrayKey.get(i) + "\n";
        }
        rezult = rezult.substring(0, rezult.length() - 1);
        byte[] newFormatData = DatatypeConverter.parseBase64Binary(rezult);
        KeySpec ks = new X509EncodedKeySpec(newFormatData);
        KeyFactory key_f = KeyFactory.getInstance(KEY_FACTOR);
        return key_f.generatePublic(ks);
    }

    public static PrivateKey loadPrivKey(byte[] data) throws Exception {
        loadProviderBC();
        KeySpec ks = new PKCS8EncodedKeySpec(data);
        KeyFactory key_f = KeyFactory.getInstance(KEY_FACTOR);
        PrivateKey res = key_f.generatePrivate(ks);
        return res;
    }

    public static byte[] loadPrivKeySCRIPT(byte[] data) throws Exception {
        KeySpec ks;
        if  (data.length < 65) {
           return data;
        }
        else
        {
            byte[] privKeyCard = new byte[64];
            System.arraycopy(data, 0, privKeyCard, 0, 64);
            return privKeyCard;
        }
    }
}
