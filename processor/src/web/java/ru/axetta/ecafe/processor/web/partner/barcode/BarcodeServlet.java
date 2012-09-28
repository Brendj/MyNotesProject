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
import com.onbarcode.barcode.AbstractBarcode;
import com.onbarcode.barcode.Code128;
import com.onbarcode.barcode.IBarcode;
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



public class BarcodeServlet extends HttpServlet
{
    Logger logger= LoggerFactory.getLogger(BarcodeServlet.class);
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        try {
            String data=request.getParameter("data");

            String set= request.getParameter("SET");

            int rotate=Integer.parseInt(request.getParameter("rotate"));

            int SET;
            if(set==null){
                SET=Code128.SET_B;} else{

            SET=Integer.parseInt(set);  }


            Code128 barcode = new Code128();


            barcode.setCodeSet(SET);


            if(data!=null){
                barcode.setData(data); }
            else{
                barcode.setData("2724567890L20204010");
            }



            ServletOutputStream servletoutputstream = response.getOutputStream();

            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            barcode.setRotate(rotate);

            barcode.drawBarcode(servletoutputstream);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}