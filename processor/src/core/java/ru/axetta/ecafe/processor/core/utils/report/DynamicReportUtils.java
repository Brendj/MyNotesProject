/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.report;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.grid.ColumnGridComponentBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.definition.datatype.DRIDataType;

import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class DynamicReportUtils {

    private static final StyleBuilder DETAIL_STYLE = stl.style().setFontSize(10)
            .setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE).setBorder(stl.pen1Point());
    private static final StyleBuilder HEADER_STYLE = stl.style().setFontSize(10)
            .setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE).setBold(true)
            .setBorder(stl.pen1Point());

    public static <T> TextColumnBuilder<T> createColumn(String title, String fieldName,
            DRIDataType<? super T, T> dataType, StyleBuilder detailStyle, StyleBuilder headerStyle, Integer width) {
        return col.column(title, fieldName, dataType).setStyle(detailStyle).setWidth(width).setTitleStyle(headerStyle);
    }

    public static <T> TextColumnBuilder<T> createColumn(JasperReportBuilder reportBuilder, String title,
            String fieldName, DRIDataType<? super T, T> dataType, StyleBuilder detailStyle, StyleBuilder headerStyle,
            Integer width) {
        TextColumnBuilder<T> columnBuilder = createColumn(title, fieldName, dataType, detailStyle, headerStyle, width);
        reportBuilder.columns(columnBuilder);
        return columnBuilder;
    }

    public static <T> TextColumnBuilder<T> createColumn(String title, String fieldName,
            DRIDataType<? super T, T> dataType, StyleBuilder detailStyle, StyleBuilder headerStyle, Integer width,
            Integer fixedHeight, Integer titleFixedHeight) {
        TextColumnBuilder<T> columnBuilder = createColumn(title, fieldName, dataType, detailStyle, headerStyle, width);
        return columnBuilder.setFixedHeight(fixedHeight).setTitleFixedHeight(titleFixedHeight);
    }

    public static void applyColumnGrtid(JasperReportBuilder reportBuilder,
            List<ColumnGridComponentBuilder> columnGridComponentBuilderList) {
        reportBuilder.columnGrid(columnGridComponentBuilderList.toArray(new ColumnGridComponentBuilder[0]))
                .setColumnTitleStyle(DynamicReportUtils.headerStyle());
    }

    public static StyleBuilder detailStyle() {
        return DETAIL_STYLE;
    }

    public static StyleBuilder headerStyle() {
        return HEADER_STYLE;
    }

    public static Object convertFieldValue(Integer value, Class clazz) {
        if (Long.class == clazz) {
            return value.longValue();
        }
        if (Integer.class == clazz) {
            return value;
        }
        if (Double.class == clazz) {
            return value.doubleValue();
        }
        return value.toString();
    }
}
