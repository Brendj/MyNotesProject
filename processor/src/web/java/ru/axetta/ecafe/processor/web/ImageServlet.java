/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web;

import ru.axetta.ecafe.processor.core.image.ImageUtils;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 18.07.16
 * Time: 10:37
 */
@WebServlet(
        name = "ImageServlet",
        description = "ImageServlet",
        urlPatterns = {"/image/*"}
)
public class ImageServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        FileInputStream fin;
        try {
            fin = new FileInputStream(ImageUtils.getImagePathByURL(request.getPathInfo()));
        } catch (FileNotFoundException e) {
            fin = new FileInputStream(ImageUtils.getNotFoundImagePath());
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        BufferedImage bi = ImageIO.read(fin);
        ImageIO.write(bi, "jpg", out);
        fin.close();
        out.close();
    }
}
