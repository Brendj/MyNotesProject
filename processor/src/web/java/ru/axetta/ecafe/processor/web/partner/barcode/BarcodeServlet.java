/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.barcode;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 25.09.12
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */

//import com.onbarcode.barcode.AbstractBarcode;
//import com.onbarcode.barcode.Code128;
//import com.onbarcode.barcode.IBarcode;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;


public class BarcodeServlet extends HttpServlet {

    Logger logger = LoggerFactory.getLogger(BarcodeServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            ServletOutputStream servletoutputstream = response.getOutputStream();

            try {
                String data = request.getParameter("data");
                String set = request.getParameter("SET");
                int rotate = Integer.parseInt(request.getParameter("rotate"));
                Barcode barcode  = BarcodeFactory.createCode128B(data);
                BufferedImage image = new BufferedImage(515, 44, BufferedImage.TYPE_INT_RGB);
                // We need to cast the Graphics from the Image to a Graphics2D - this is OK
                Graphics2D g = (Graphics2D) image.getGraphics();
                barcode.draw(g, 0, 0);
                ImageIO.write(image, "jpeg", servletoutputstream);
                //int SET;
                //if (set == null) {
                //    SET = Code128.SET_B;
                //} else {
                //    SET = Integer.parseInt(set);
                //}
                //Code128 barcode = new Code128();
                //barcode.setCodeSet(SET);
                //if (data != null) {
                //    barcode.setData(data);
                //} else {
                //    throw new Exception("no data");
                //}
                //barcode.setRotate(rotate);
                //barcode.drawBarcode(servletoutputstream);

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