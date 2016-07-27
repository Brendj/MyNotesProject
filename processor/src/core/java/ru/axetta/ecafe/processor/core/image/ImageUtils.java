/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.image;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 18.07.16
 * Time: 10:37
 */
public class ImageUtils {

    private static final String IMAGE_DIRECTORY = RuntimeContext.getInstance().getConfigProperties()
            .getProperty(RuntimeContext.IMAGE_DIRECTORY, "/image");
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private static final String DELIMITER = "/";
    private static final String JPG = ".jpg";
    private static final String DEFAULT = "default";
    private static final String NOT_FOUND = "not_found";
    private static final String ISNEW = "n/";

    public static String getImagePathByURL(String URL){
        return IMAGE_DIRECTORY + DELIMITER + URL + JPG;
    }

    public static String getNotFoundImagePath(){
        return IMAGE_DIRECTORY + DELIMITER + NOT_FOUND + JPG;
    }

    public static String formImagePath(Long contractId, boolean isNew, String hashFileName, ImageSize size) {
        return IMAGE_DIRECTORY + DELIMITER + size.getDescription() + DELIMITER +
                generateFileName(contractId, isNew, hashFileName) + JPG;
    }

    public static String getPhotoURL(Client client, int size, boolean isNew) throws NoPhotoException, NoNewPhotoException, NoSuchImageSizeException {
        if(client.getPhoto() == null){
            throw new NoPhotoException("У клиента нет фото.");
        }
        if(!client.getPhoto().getIsNew() && isNew){
            throw new NoNewPhotoException();
        }
        StringBuilder tmp = new StringBuilder();
        tmp.append(ImageSize.fromInteger(size));
        tmp.append(DELIMITER);
        if(isNew){
            tmp.append(ISNEW);
        }
        tmp.append(client.getContractId().toString());
        tmp.append(client.getPhoto().getName());
        return tmp.toString();
    }

    public static int getPhotoStatus(Client client){
        return client.getPhoto().getIsNew() ? 1 : 0;
    }

    public static String getDefaultImageURL(){
        return IMAGE_DIRECTORY + DELIMITER + DEFAULT;
    }

    public static String generateHashFileName(int length){
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static String generateFileName(Long contractId, boolean isNew, String hashFileName){
        StringBuilder tmp = new StringBuilder();
        if(isNew){
            tmp.append(ISNEW);
        }
        tmp.append(contractId.toString());
        tmp.append(hashFileName);
        return tmp.toString();
    }

    private static BufferedImage toBufferedImage(Image image){
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB );

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    private static BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight) {
        Object hint = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        int type = (image.getTransparency() == Transparency.OPAQUE)
                        ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = image;

        do {
            if (width > targetWidth) {
                width /= 2;
                if (width < targetWidth) {
                    width = targetWidth;
                }
            }
            if (height > targetHeight) {
                height /= 2;
                if (height < targetHeight) {
                    height = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(width, height, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(result, 0, 0, width, height, null);
            g2.dispose();

            result = tmp;
        } while (width != targetWidth || height != targetHeight);

        return result;
    }

    public static BufferedImage resizeImage(BufferedImage image) {
        return resizeImage(image, (int) (image.getWidth() * ClientPhotoConfig.COMPRESSION),
                (int) (image.getHeight() * ClientPhotoConfig.COMPRESSION));
    }

    public static void saveImage(String path, BufferedImage image) throws IOException {
        ImageIO.write(image, "jpg", new File(path));
    }

    public static String saveImage(Long contractId, Image image, boolean isNew) throws IOException, ImageUtilsException {
        String hashFileName = generateHashFileName(16);
        saveImage(contractId, image, isNew, hashFileName);
        return hashFileName;
    }

    public static void saveImage(Long contractId, Image image, boolean isNew, String hashFileName) throws IOException, ImageUtilsException {
        BufferedImage bimage = toBufferedImage(image);
        validateImage(bimage, contractId, hashFileName);
        BufferedImage sbimage = resizeImage(bimage);
        saveImage(formImagePath(contractId, isNew, hashFileName, ImageSize.MEDIUM), bimage);
        saveImage(formImagePath(contractId, isNew, hashFileName, ImageSize.SMALL), sbimage);
    }

    public static void saveImage(Client client, Image image, boolean isNew) throws IOException, ImageUtilsException {
        if(getPhotoStatus(client) == 2){

        }
        saveImage(client.getContractId(), image, isNew, client.getPhoto().getName());
    }

    public static boolean deleteImage(Long contractId, String hashFileName, boolean isNew) {
        boolean result = deleteImage(formImagePath(contractId, isNew, hashFileName, ImageSize.MEDIUM));
        if(result) {
            deleteImage(formImagePath(contractId, isNew, hashFileName, ImageSize.SMALL));
        }
        return result;
    }

    private static boolean deleteImage(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static boolean checkImageExists(Long contractId, String hashFileName, boolean isNew) throws SmallPhotoNotFoundException {
        boolean medium = checkImageExists(contractId, hashFileName, isNew, ImageSize.MEDIUM);
        boolean small = checkImageExists(contractId, hashFileName, isNew, ImageSize.SMALL);
        if(medium && !small){
            throw new SmallPhotoNotFoundException("Image with small size not found while medium exists!");
        }
        return medium;
    }

    public static boolean checkImageExists(Long contractId, String hashFileName, boolean isNew, ImageSize size) {
        File file = new File(formImagePath(contractId, isNew, hashFileName, size));
        return file.exists();
    }

    private static void validateImage(BufferedImage image, Long contractId, String hashFileName) throws IOException, ImageUtilsException {
        validateSize(image);
        //compareImage неправильно работает
        //if(ClientPhotoConfig.CHECK_UNIQUE) {
        //    validateUnique(image, contractId, hashFileName);
        //}
    }


    private static void validateUnique(BufferedImage image, Long contractId, String hashFileName) throws IOException, ImageUtilsException {
        BufferedImage oldImage = null;
        try {
            oldImage = ImageIO.read(new FileInputStream(formImagePath(contractId, true, hashFileName, ImageSize.MEDIUM)));
        } catch (FileNotFoundException ignore){}
        try {
            oldImage = ImageIO.read(new FileInputStream(formImagePath(contractId, false, hashFileName, ImageSize.MEDIUM)));
        } catch (FileNotFoundException ignore){}
        if(oldImage != null) {
            if(compareImage(image, oldImage)){
                throw new PhotoIsNotNewException("Фото совпадает с уже имеющимся фото клиента.");
            }
        }
    }

    private static void validateSize(BufferedImage image) throws IOException, ImageUtilsException {
        if(image.getHeight() > ClientPhotoConfig.MAX_HEIGHT || image.getWidth() > ClientPhotoConfig.MAX_WIDTH){
            throw new IllegalPhotoSizeException(String.format("Недопустимый размер изображения. Максимальный размер %s х %s пикселей.",
                    ClientPhotoConfig.MAX_WIDTH, ClientPhotoConfig.MAX_HEIGHT));
        }
        if(image.getHeight() < ClientPhotoConfig.MIN_HEIGHT || image.getWidth() < ClientPhotoConfig.MIN_WIDTH){
            throw new IllegalPhotoSizeException(String.format("Недопустимый размер изображения. Минимальный размер %s х %s пикселей.",
                    ClientPhotoConfig.MAX_WIDTH, ClientPhotoConfig.MAX_HEIGHT));
        }
        double whratio = ((double) image.getHeight()) / ((double) image.getWidth());
        if(whratio > ClientPhotoConfig.MAX_H_W_RATIO || whratio < ClientPhotoConfig.MIN_H_W_RATIO){
            throw new IllegalPhotoSizeException(String.format("Недопустимое соотношение сторон изображения. Допустимые значения от %s до %s.",
                    ClientPhotoConfig.MIN_H_W_RATIO, ClientPhotoConfig.MAX_H_W_RATIO));
        }
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", tmp);
        tmp.close();
        int contentLength = tmp.size();
        int maxContentLength = ClientPhotoConfig.MAX_SIZE * 1024 * 8;
        if(contentLength > maxContentLength){
            throw new IllegalPhotoSizeException(String.format("Недопустимый размер файла. Максимальный размер %s Кб.",
                    ClientPhotoConfig.MAX_SIZE));
        }
    }

    private static boolean compareImage(BufferedImage biA, BufferedImage biB) {
        DataBuffer dbA = biA.getData().getDataBuffer();
        int sizeA = dbA.getSize();
        DataBuffer dbB = biB.getData().getDataBuffer();
        int sizeB = dbB.getSize();

        if(sizeA == sizeB) {
            for(int i = 0; i < sizeA; i++) {
                if(dbA.getElem(i) != dbB.getElem(i)) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private static class ClientPhotoConfig {
        private static final int MAX_HEIGHT;
        private static final int MAX_WIDTH;
        private static final int MIN_HEIGHT;
        private static final int MIN_WIDTH;
        private static final int MAX_SIZE;
        private static final double COMPRESSION;
        private static final double MAX_H_W_RATIO;
        private static final double MIN_H_W_RATIO;
        //private static final boolean CHECK_UNIQUE;
        static {
            String[] config = RuntimeContext.getInstance().getConfigProperties()
                    .getProperty(RuntimeContext.IMAGE_VALIDATION, "640,480,320,240,200,0.375,1.4,1.2,1").split(",");
            MAX_HEIGHT = Integer.parseInt(config[0]);
            MAX_WIDTH = Integer.parseInt(config[1]);
            MIN_HEIGHT = Integer.parseInt(config[2]);
            MIN_WIDTH = Integer.parseInt(config[3]);
            MAX_SIZE = Integer.parseInt(config[4]);
            COMPRESSION = Double.parseDouble(config[5]);
            MAX_H_W_RATIO = Double.parseDouble(config[6]);
            MIN_H_W_RATIO = Double.parseDouble(config[7]);
            //CHECK_UNIQUE = (Integer.parseInt(config[8]) != 0);
        }
    }

    public enum ImageSize {

        SMALL(0, "small"),
        MEDIUM(1, "medium");

        private final int value;
        private final String description;
        private static final Map<Integer, String> map;
        static {
            map = new HashMap<Integer, String>();
            for (ImageSize imageSize : ImageSize.values()) {
                map.put(imageSize.getValue(), imageSize.getDescription());
            }
        }

        private ImageSize(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static Map<Integer, String> getMap() {
            return map;
        }

        public static String getFormatsString() {
            StringBuilder sb = new StringBuilder();
            for(ImageSize imageSize : ImageSize.values()) {
                sb.append(String.format("%d - %s, ", imageSize.getValue(), imageSize.getDescription()));
            }
            return sb.toString().substring(0, sb.length() - 2);
        }

        public static String fromInteger(int value) throws NoSuchImageSizeException {
            String result = map.get(value);
            if(result == null){
                throw new NoSuchImageSizeException("Допустимые значения: " + getFormatsString() + ".");
            }
            return result;
        }
    }

    public static class ImageUtilsException extends Exception {
        public ImageUtilsException() {
        }

        public ImageUtilsException(String message) {
            super(message);
        }
    }

    public static class NoSuchImageSizeException extends ImageUtilsException {

        public NoSuchImageSizeException() {
        }

        public NoSuchImageSizeException(String message) {
            super(message);
        }
    }

    public static class NoPhotoException extends ImageUtilsException {

        public NoPhotoException() {
        }

        public NoPhotoException(String message) {
            super(message);
        }
    }

    public static class NoNewPhotoException extends ImageUtilsException {

        public NoNewPhotoException() {
        }

        public NoNewPhotoException(String message) {
            super(message);
        }
    }

    public static class PhotoUnderRegistryException extends ImageUtilsException {

        public PhotoUnderRegistryException() {
        }

        public PhotoUnderRegistryException(String message) {
            super(message);
        }
    }

    public static class PhotoIsNotNewException extends ImageUtilsException {

        public PhotoIsNotNewException() {
        }

        public PhotoIsNotNewException(String message) {
            super(message);
        }
    }

    public static class IllegalPhotoSizeException extends ImageUtilsException {

        public IllegalPhotoSizeException() {
        }

        public IllegalPhotoSizeException(String message) {
            super(message);
        }
    }

    public static class SmallPhotoNotFoundException extends ImageUtilsException {

        public SmallPhotoNotFoundException() {
        }

        public SmallPhotoNotFoundException(String message) {
            super(message);
        }
    }
}
