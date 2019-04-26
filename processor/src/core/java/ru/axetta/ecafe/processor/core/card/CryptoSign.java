/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

import ru.CryptoPro.JCP.JCP;

import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CryptoSign
{
    public static final String BP160R1 = "brainpoolP160r1";
    public static final String BC_PROV = "BC";
    public static final String ALGORITHM =  "SHA1withECDSA";
    public static final String KEY_FACTOR =  "ECDSA";

    public static KeyPair keyPairGen() throws Exception
    {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(BP160R1);
        KeyPairGenerator g = KeyPairGenerator.getInstance(KEY_FACTOR, BC_PROV);
        g.initialize(ecSpec, new SecureRandom());
        KeyPair pair = g.generateKeyPair();
        return pair;
    }

    private static byte[] sign(byte[] data, PrivateKey priv) throws Exception
    {
        Signature dsa = Signature.getInstance(ALGORITHM, BC_PROV);
        dsa.initSign(priv);
        dsa.update(data);
        return dsa.sign();
    }

    //Проверка карты на её наличие в базе
    private static boolean verifyCardforDuble (Long num)
    {
        byte[] res = new byte[8];
        byte[] fiz = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(num).array();
        res[4] = fiz[7];
        res[5] = fiz[6];
        res[6] = fiz[5];
        res[7] = fiz[4];
        Long longRes = ByteBuffer.wrap(res).getLong();
        if (DAOReadonlyService.getInstance().getCardfromNum(longRes) == null)
            return true;
        else
            return false;
    }


    public static List<ResponseCardSign> createSignforCard (List<RequestCardForSign> cards, CardSign cardSign) throws Exception {
        List<ResponseCardSign> responseCardSigns = new ArrayList<ResponseCardSign>();
        //Достаем приватный ключ для подписи
        PrivateKey pk = loadPrivKey(cardSign.getPrivatekeycard());
        boolean sucsess;
        for (RequestCardForSign card : cards) {
            sucsess = true;
            ResponseCardSign responseCardSign = new ResponseCardSign();
            try {
                byte[] sign;
                responseCardSign.setCardno(card.getPrinted_no());
                if (!verifyCardforDuble(card.getPrinted_no())) {
                    responseCardSign.setMessage("Карта с таким номером уже зарегистирована");
                    sucsess = false;
                }
                if (card.getTypeId() != 1 && card.getTypeId() != 9 && card.getTypeId() != 10 && card.getTypeId() != 11 && card.getTypeId() != 15)
                {
                    responseCardSign.setMessage("Неверный тип носителя");
                    sucsess = false;
                }
                //Подготавливаем данные для подписи
                byte[] fiz = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(card.getUid()).array();
                byte[] type = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(card.getTypeId()).array();
                byte[] num = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(card.getPrinted_no()).array();
                byte[] kod = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(cardSign.getManufacturerCode()).array();
                byte[] data = new byte[3];
                data[0] = (byte)(card.getIssuedate().get(Calendar.YEAR)-2000);
                data[1] = (byte)(card.getIssuedate().get(Calendar.MONTH)+1);
                data[2] = (byte)(card.getIssuedate().get(Calendar.DAY_OF_MONTH));
                byte[] sert = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(cardSign.getIdOfCardSign()).array();

                //Здесь сформируется конечный вариант
                byte[] card_data = new byte[32];
                System.arraycopy(fiz, 1, card_data, 0, 7);
                System.arraycopy(type, 1, card_data, 7, 1);
                System.arraycopy(num, 3, card_data, 8, 5);
                System.arraycopy(kod, 3, card_data, 13, 1);
                System.arraycopy(data, 0, card_data, 14, 3);
                System.arraycopy(sert, 2, card_data, 17, 2);

                //Если тип карты поддерживает данный более 128 байт
                if (sucsess) {//Подписываем карту только если пройдены проверки
                    if (card.getMemSize() == 1) {
                        //Шифрование по SHA-1
                        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                        card_data = Base64.encodeBase64(messageDigest.digest(card_data));
                        sign = CryptoSign.sign(card_data, pk);
                    }
                    else {
                        if (card.getMemSize() == 2) {
                            sign = SCrypt.generate(pk.getEncoded(), card_data, //данные карты используем как "соль"
                                    16384, 8, 1, 20);

                        }
                        else  {
                            sign = new byte[]{0};
                            responseCardSign.setMessage("Тип подписи для карты задан некорректно");
                            sucsess = false;
                        }
                    }
                    byte[] allData = new byte[card_data.length + sign.length + 3];

                    //1 байт для хранения типа подписи
                    byte[] typeCard = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort((short) (card.getMemSize()))
                            .array();
                    System.arraycopy(typeCard, 1, allData, 0, 1);

                    responseCardSign.setSizeDate((short)card_data.length);
                    responseCardSign.setSizeSign((short)sign.length);

                    //Сохраняем сами подписи
                    System.arraycopy(card_data, 0, allData, 1, card_data.length);
                    System.arraycopy(sign, 0, allData, card_data.length + 1, sign.length);
                    responseCardSign.setAllDate(allData);
                }
            }
            catch (Exception e)
            {
                responseCardSign.setInsideError("Неизвестная ошибка при подписании карты");
                sucsess = false;
            }
            finally {
                if (!sucsess)
                    responseCardSign.setAllDate(null);
                responseCardSigns.add(responseCardSign);
            }
        }
        return responseCardSigns;
    }

    public static byte[] createAllDate (List<RequestCardForSign> cards, CardSign cardSign)
    {
        //17 байт для хранений информации о одной карте + 1 байт для id поставщика
        byte[] allDate = new byte[17*cards.size() + 1];
        byte[] kod = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(cardSign.getManufacturerCode()).array();
        System.arraycopy(kod, 3, allDate, 0, 1);
        //17 байт информация об одной карте
        byte[] card_data = new byte[17];
        for (int i = 0; i<cards.size(); i++)
        {
            byte[] num = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(cards.get(i).getUid()).array();
            byte[] type = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(cards.get(i).getTypeId()).array();
            byte[] fiz = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(cards.get(i).getPrinted_no()).array();
            byte[] data = new byte[3];
            data[0] = (byte)(cards.get(i).getIssuedate().get(Calendar.YEAR)-2000);
            data[1] = (byte)(cards.get(i).getIssuedate().get(Calendar.MONTH)+1);
            data[2] = (byte)(cards.get(i).getIssuedate().get(Calendar.DAY_OF_MONTH));
            byte[] memSize = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort((short) (cards.get(i).getMemSize())).array();
            //17 байт информация об одной карте
            System.arraycopy(fiz, 1, card_data, 0, 7);
            System.arraycopy(type, 1, card_data, 7, 1);
            System.arraycopy(num, 3, card_data, 8, 5);
            System.arraycopy(data, 0, card_data, 13, 3);
            System.arraycopy(memSize, 1, card_data, 16, 1);
            //Добавляем в общий массив данных
            System.arraycopy(card_data, 0, allDate, 17*i+1, 17);
        }
        return allDate;
    }

    public static boolean verifySignforProvider (List<RequestCardForSign> requestCardForSigns, byte[] signProvider, CardSign cardSign) throws Exception {
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



    private static boolean verifyGOST2012(byte[] data, byte [] sign, CardSign cardSign ) throws Exception
    {
        Signature signature = Signature.getInstance(JCP.GOST_EL_SIGN_NAME, JCP.PROVIDER_NAME);
        signature.initVerify(loadPubKey(cardSign.getPublickeyprovider()));
        signature.update(MessageDigest.getInstance(JCP.GOST_DIGEST_NAME, JCP.PROVIDER_NAME).digest(data));
        return signature.verify(sign);
    }

    private static boolean verifyECDSA(byte[] data, byte [] sign, CardSign cardSign ) throws Exception
    {
        Signature dsa = Signature.getInstance(ALGORITHM, BC_PROV);
        dsa.initVerify(loadPubKey(cardSign.getPublickeyprovider()));
        dsa.update(data);
        return dsa.verify(sign);
    }

    private static boolean verifySCRIPT(byte [] dateCards, byte [] sign, CardSign cardSign) throws Exception
    {

        byte[] varsign = SCrypt.generate
                (loadPrivKey(cardSign.getPublickeyprovider()).getEncoded(),
                        dateCards, //данные карты используем как "соль"
                        16384,
                        8,
                        1,
                        20);
        return Arrays.equals(varsign, sign);
    }

    public static PublicKey loadPubKey(byte [] data) throws Exception
    {
        KeySpec ks = new X509EncodedKeySpec(data);
        KeyFactory key_f = KeyFactory.getInstance(KEY_FACTOR);
        return key_f.generatePublic(ks);
    }

    public static PrivateKey loadPrivKey(byte [] data) throws Exception
    {
        KeySpec ks = new PKCS8EncodedKeySpec(data);
        KeyFactory key_f = KeyFactory.getInstance(KEY_FACTOR);
        PrivateKey res = key_f.generatePrivate(ks);
        return res;
    }
}
