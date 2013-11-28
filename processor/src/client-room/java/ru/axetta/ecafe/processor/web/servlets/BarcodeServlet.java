/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.servlets;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 25.09.12
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BarcodeServlet extends HttpServlet {

    Logger logger = LoggerFactory.getLogger(BarcodeServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            response.setContentType("image/png");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            ServletOutputStream servletoutputstream = response.getOutputStream();
            try {
                String data = request.getParameter("data");
                String set = request.getParameter("SET");
                int rotate = Integer.parseInt(request.getParameter("rotate"));
                int width = 242;
                int height = 48;
                BitMatrix bitMatrix = new Code128Writer().encode(data, BarcodeFormat.CODE_128,width,height,null);
                MatrixToImageWriter.writeToStream(bitMatrix, "png",servletoutputstream);
            } catch (Exception e) {
                BufferedImage bimg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                Graphics g = bimg.getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 1, 1);
                ImageIO.write(bimg, "jpeg", servletoutputstream);
                logger.error(e.getMessage(), e);
                throw new ServletException(e);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ServletException(ex);
        }
    }
}