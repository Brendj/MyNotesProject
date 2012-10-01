/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.rusmarc;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Библиографическая запись формата RUSMARC.
 * Флаги полей в БД
 * 1 - обязательное поле
 * 2 - может повторяться
 * 4 - подполе
 * 8 - устарело
 * 16 - одно из этих полей обязательное
 */
public class Record {

    public static final String EMPTY_STRING = "";
    /**
     * Маркер записи.
     */
    public RecordMarker marker;

    /**
     * РАЗДЕЛИТЕЛЬ ЗАПИСЕЙ, Record Terminator - управляющий символ, используемый в конце записи, для отделения ее от следующей.
     */
    public static char RECORD_TERMINATOR = '';
    /**
     * РАЗДЕЛИТЕЛЬ ПОЛЕЙ, Field Separator - управляющий символ, используемый в конце каждого переменного поля для отделения его от следующего, а также в конце справочника (Directory).
     */
    public static char FIELD_SEPARATOR = '';

    /**
     * РАЗДЕЛИТЕЛЬ ПОДПОЛЕЙ, Delimiter - всегда один и тот же уникальный символ, идентифицирующий начало подполя, установленный по ISO 2709.
     */
    public static char DELIMITER = '';

    public List<RecordField> fields;

    public Record() {
        marker = new RecordMarker('n', 'a', 'm', ' ', '3', ' ');
        fields = new LinkedList<RecordField>();
    }

    public Record(DataInput dataInput, String G1, String G2, String G3, boolean forceCustomG) throws IOException {
        boolean brokenAddresses = false;
        marker = new RecordMarker(dataInput);

        StringBuilder directory = parseDirectory(dataInput);
        if (marker.dataAddress != -1 && marker.dataAddress != 24 + directory.length() + 1) {
            marker.dataAddress = -1;
            brokenAddresses = true;
        }

        byte[] recordData = retrieveDataBlock(dataInput);
        if (marker.recordLength != -1 && marker.recordLength != 24 + directory.length() + 1 + recordData.length + 1) {
            marker.recordLength = -1;
            brokenAddresses = true;
        }

        if (brokenAddresses || Integer.parseInt(directory.toString().substring(directory.length() - 5, directory.length())) + Integer.parseInt(directory.toString().substring(directory.length() - 9, directory.length() - 5)) > recordData.length)
            restoreAddressing(directory, recordData);

        createFields(directory, recordData);
        String[] enc = getCharsetArray(G1, G2, G3, forceCustomG);

        readFields(enc);
        for (RecordField rf : fields) {
            if (rf.tag.equals("100")) {
                String str = rf.subFields.get(0).dataString;
                str = (str.length() < 26 ? expand(str, 26) : str.substring(0, 26)) + "50      " +
                        (str.length() < 36 ? "ca" : str.substring(34));
                rf.subFields.get(0).dataString = str;
            }
        }
    }

    private static String expand(String str, int needLen) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < needLen)
            sb.append("|");
        return sb.toString();
    }

    public Record(char recordStatus, char recordType, char bibliographicLevel, char hierarchicalLevel, char codingLevel, char formOfCatalogingDescription, String data)//при чтении из бд
    {
        marker = new RecordMarker(recordStatus, recordType, bibliographicLevel, hierarchicalLevel, codingLevel, formOfCatalogingDescription);
        int directoryEnd = data.indexOf(FIELD_SEPARATOR);
        String directory = data.substring(0, directoryEnd);
        data = data.substring(directoryEnd + 1);

        fields = new LinkedList<RecordField>();

        String tag = "";
        int len, off;
        RecordField rf;
        RecordSubField rsf;
        boolean fieldWithLinkedData = false;//поле со связанными данными, как в блоке 4-- или в поле 604 (и там и там признак - поле 1)
        for (int i = 0; i < directory.length(); i += 12) {
            tag = directory.substring(i, i + 3);
            len = Integer.parseInt(directory.substring(i + 3, i + 7));
            off = Integer.parseInt(directory.substring(i + 7, i + 12));
            rf = new RecordField(tag);
            if (tag.startsWith("00"))
                rf.dataString = data.substring(off, off + len - 1);
            else {
                fieldWithLinkedData = data.charAt(off + 3) == '1';
                int end = off + len;
                rf.indicator1 = data.charAt(off++);
                rf.indicator2 = data.charAt(off++);
                while (off < end) {
                    off++;//skip DELIMITER
                    for (int j = off + 1; j < end; ++j) {
                        if (data.charAt(j) == FIELD_SEPARATOR ||
                                (!fieldWithLinkedData && data.charAt(j) == DELIMITER) ||
                                (fieldWithLinkedData && data.charAt(j) == DELIMITER && data.charAt(j + 1) == '1')) {
                            rsf = new RecordSubField(data.charAt(off), null);
                            rf.addSub(rsf);
                            rsf.dataString = data.substring(off + 1, j);
                            if (fieldWithLinkedData && data.charAt(j) != FIELD_SEPARATOR) {
                                j += 2;
                                off = j - 1;
                            } else {
                                j++;
                                off = j;
                            }
                            fieldWithLinkedData = off < end && data.charAt(off) == '1';
                        }
                    }
                }
            }
            fields.add(rf);
        }
    }

    private void readFields(String[] enc) {
        for (RecordField rf : fields)
            rf.readSub(enc);
    }

    private String[] getCharsetArray(String G1, String G2, String G3, boolean forceCustomG) {
        if (!isPossibleEncoding(G1)) G1 = "  ";
        if (!isPossibleEncoding(G2)) G2 = "  ";
        if (!isPossibleEncoding(G3)) G3 = "  ";
        String[] enc = new String[]{"01", G1, G2, G3};
        if (forceCustomG)
            return enc;
        for (int i = 0, max = fields.size(); i != max; ++i) {
            if (fields.get(i).tag.equals("100")) {
                try {
                    byte[] data = fields.get(i).getSub('a');
                    String coding = (char) (0xFF & data[26]) + "" + (char) (0xFF & data[27]);
                    //new String(data, 26, 2, "ASCII");
                    if (isPossibleEncoding(coding)) {
                        if (!coding.equals(possibleCharset[0])) {
                            enc[0] = coding;
                            coding = (char) (0xFF & data[28]) + "" + (char) (0xFF & data[29]);
                            //new String(data, 28, 2, "ASCII");
                            if (isPossibleEncoding(coding)) {
                                if (!coding.equals(possibleCharset[0])) {
                                    enc[1] = coding;
                                    coding = (char) (0xFF & data[30]) + "" + (char) (0xFF & data[31]);
                                    //new String(data, 30, 2, "ASCII");
                                    if (isPossibleEncoding(coding)) {
                                        if (!coding.equals(possibleCharset[0])) {
                                            enc[2] = coding;
                                            coding = (char) (0xFF & data[32]) + "" + (char) (0xFF & data[33]);
                                            //new String(data, 32, 2, "ASCII");
                                            if (isPossibleEncoding(coding)) {
                                                if (!coding.equals(possibleCharset[0])) {
                                                    enc[3] = coding;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                return enc;
            }
        }
        addDefault100Field();
        return enc;
    }

    public void addDefault100Field() {
        RecordField rf = new RecordField("100");
        rf.subFields = new LinkedList<RecordSubField>();
        RecordSubField rsf = new RecordSubField('a', null);
        rsf.dataString = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "u||||||||u  u0rusy50      ca";
        rf.subFields.add(rsf);
        fields.add(rf);
    }

    private static final String[] possibleCharset = {"  ", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "50", "79", "89", "99"};

    private static boolean isPossibleEncoding(String s) {
        for (String ss : possibleCharset)
            if (ss.equals(s)) return true;
        return false;
    }

    public Record(DataInput dataInput) throws IOException {
        this(dataInput, "  ", "  ", "  ", false);
    }

    /// <summary>
    /// Восстанавливает адресацию справочника, если запись "убитая"
    /// </summary>
    /// <param name="directory">справочник</param>
    /// <param name="recordData">raw данные полей</param>
    private void restoreAddressing(StringBuilder directory, byte[] recordData) {
        int currPos = 0, prevPos = 0;
        for (int i = 0; i < directory.length(); i += 12) {
            while ((0xFF & recordData[currPos]) != FIELD_SEPARATOR)
                currPos++;
            currPos++;

            directory.delete(i + 3, i + 12);
            directory.insert(i + 3, numberToString(currPos - prevPos, 4).append(numberToString(prevPos, 5)));
            prevPos = currPos;
        }
    }

    private void createFields(StringBuilder directory, byte[] recordData) {
        fields = new LinkedList<RecordField>();

        String tag = "";
        int len, off;
        RecordField rf;
        boolean fieldWithLinkedData = false;//поле со связанными данными, как в блоке 4-- или в поле 604 (и там и там признак - поле 1)
        for (int i = 0; i < directory.length(); i += 12) {
            tag = directory.toString().substring(i, i + 3);
            len = Integer.parseInt(directory.toString().substring(i + 3, i + 7));
            off = Integer.parseInt(directory.toString().substring(i + 7, i + 12));
            rf = new RecordField(tag);
            if (tag.startsWith("00")) {
                rf.data = new byte[len - 1];
                System.arraycopy(recordData, off, rf.data, 0, len - 1);//-1, т.к. еще разделитель
            } else {
                fieldWithLinkedData = (char) (0xFF & recordData[off + 3]) == '1';
                int end = off + len;
                rf.indicator1 = (char) (0xFF & recordData[off++]);
                rf.indicator2 = (char) (0xFF & recordData[off++]);
                while (off < end) {
                    off++;//skip DELIMITER
                    for (int j = off + 1; j < end; ++j) {
                        if ((0xFF & recordData[j]) == FIELD_SEPARATOR ||
                                (!fieldWithLinkedData && (0xFF & recordData[j]) == DELIMITER) ||
                                (fieldWithLinkedData && (0xFF & recordData[j]) == DELIMITER && (0xFF & recordData[j + 1]) == '1')) {
                            byte[] d = new byte[j - (off + 1)];
                            System.arraycopy(recordData, off + 1, d, 0, d.length);
                            rf.addSub(new RecordSubField((char) (0xFF & recordData[off]), d));
                            if (fieldWithLinkedData && (0xFF & recordData[j]) != FIELD_SEPARATOR) {
                                j += 2;
                                off = j - 1;
                            } else {
                                j++;
                                off = j;
                            }
                            fieldWithLinkedData = off < end && (char) (0xFF & recordData[off]) == '1';
                        }
                    }
                }
            }
            fields.add(rf);
        }
    }

    private static byte[] retrieveDataBlock(DataInput dataInput) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        byte b = dataInput.readByte();
        while (b != RECORD_TERMINATOR) {
            ms.write(b);
            b = dataInput.readByte();
        }
        ms.close();
        return ms.toByteArray();
    }

    private StringBuilder parseDirectory(DataInput dataInput) throws IOException {
        StringBuilder directory = new StringBuilder();
        byte b = dataInput.readByte();
        while ((0xFF & b) != FIELD_SEPARATOR) {
            directory.append((char) (0xFF & b));
            b = dataInput.readByte();
        }
        return directory;
    }

    public String getMarker() {
        return marker.getFullMarker().toString();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean getHTML) {
        if (marker.recordLength == -1)
            calcMarker();
        if (getHTML) {
            StringBuilder sb = new StringBuilder("<html><head><style type=\"text/css\">body, table, p {font-size: 12pt;padding:0px;margin:0px;}.out_head{background-color:#C9C9C9} .ovr_head{background-color:#E5E5E5} .out_text{background-color:#B1C1D1} .ovr_text{background-color:#C3D5E6}.icon {background: url(\"c:\\r.png\") no-repeat scroll transparent;float:left;border: 0 none; display: inline; height: 16px; padding: 0; width: 16px;}.icon_edit {background-position: 0 0px;}.icon_up {background-position: 0 -16px;}.icon_down {background-position: 0 -32px;}.icon_add {background-position: 0 -48px;}.icon_del {background-position: 0 -64px;}</style><script>function ovr(e){el=e.srcElement||e.target;while(el.tagName!=\"TD\" && el.parentNode!=null)el=el.parentNode;if(el.tagName!=\"TD\") return;el.parentNode.childNodes[0].className=\"ovr_head\";el.parentNode.childNodes[1].className=\"ovr_text\";}function out(e){el=e.srcElement||e.target;while(el.tagName!=\"TD\" && el.parentNode!=null)el=el.parentNode;if(el.tagName!=\"TD\") return;el.parentNode.childNodes[0].className=\"out_head\";el.parentNode.childNodes[1].className=\"out_text\";}</script></head><body><table onmouseover=\"ovr(event)\" onmouseout=\"out(event)\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" margin=\"0\">");

            sb.append("<tr><td align=\"right\" valign=\"top\" class=\"out_head\" width=\"110\"><p>MARKER</p></td><td class=\"out_text\">").append("<p><b>").append(marker.getFullMarker().toString().replace(' ', '#')).append("</b></p>").append("</td></tr>");
            for (RecordField rf : fields) {
                sb.append("<tr><td align=\"right\" valign=\"top\" class=\"out_head\"><a href=\"edit").append(rf.tag).append("\"><p class=\"icon icon_edit\"></p></a><p class=\"icon icon_up\"></p><p class=\"icon icon_down\"></p><p class=\"icon icon_del\"></p><p>").append(rf.tag).append("</p></td><td class=\"out_text\">");
                if (rf.dataString != null) {
                    sb.append("<p><b>").append(rf.dataString).append("</b></p>");
                } else {
                    sb.append("<p><b>").append(rf.indicator1 == ' ' ? '#' : rf.indicator1).append(rf.indicator2 == ' ' ? '#' : rf.indicator2).append("</b></p>");
                    for (RecordSubField rsf : rf.subFields) {
                        sb.append("<p><b>$").append(rsf.subFieldCode).append("</b>&nbsp;").append(rsf.dataString).append("</p>");
                    }
                    //sb.Length -= 2;
                }
                sb.append("</td></tr>");
            }
            return sb.append("</table></body></html>").toString();
        } else {
            String s = "Marker: " + marker.getFullMarker() + "\r\r";
            for (RecordField rf : fields)
                s += rf.toString();
            s += '\r';
            return s;
        }
    }

    public void calcMarker() {
        int dataLen = 1, referLen = fields.size() * 12 + 1;
        for (RecordField rf : fields)
            dataLen += Charset.forName("UTF8").encode(rf.getStringData().toString()).array().length;
        marker.recordLength = dataLen + referLen + 24;
        marker.dataAddress = 24 + referLen;
    }

    public byte[] getRUSMARCRecord() {
        StringBuilder refer = new StringBuilder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] tmp;
        for (RecordField rf : fields) {
            tmp = Charset.forName("UTF8").encode(rf.getStringData().toString()).array();
            refer.append(rf.tag);
            refer.append(numberToString(tmp.length, 4));
            refer.append(numberToString(outputStream.size(), 5));
            outputStream.write(tmp, 0, tmp.length);
        }
        outputStream.write(RECORD_TERMINATOR);
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }//closing memory stream
        byte[] data = outputStream.toByteArray();
        outputStream = new ByteArrayOutputStream();

        tmp = Charset.forName("UTF8").encode(refer.append(FIELD_SEPARATOR).toString()).array();


        marker.recordLength = data.length + tmp.length + 24;
        marker.dataAddress = 24 + tmp.length;

        outputStream.write(marker.getFullMarkerRaw(), 0, 24);
        outputStream.write(tmp, 0, tmp.length);
        outputStream.write(data, 0, data.length);
        data = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException ignored) {
        }//closing memory stream
        return data;
    }

    public String getDBRecordString()//для записи в бд, адресация посимвольная, а не октетная
    {
        StringBuilder refer = new StringBuilder();
        StringBuilder data = new StringBuilder();
        StringBuilder tmp;
        marker.dataAddress = 25 + fields.size() * 12;
        for (RecordField rf : fields) {
            tmp = rf.getStringData();
            refer.append(rf.tag);
            refer.append(numberToString(tmp.length(), 4));
            refer.append(numberToString(data.length(), 5));
            data.append(tmp);
        }
        refer.append(FIELD_SEPARATOR);
        refer.append(data);
        return refer.toString();
    }

    public static StringBuilder numberToString(int number, int symbols) {
        StringBuilder sb = new StringBuilder("" + Math.max(number, 0));
        while (sb.length() < symbols)
            sb.insert(0, '0');
        return sb;
    }

    public char getRecordStatus() {
        return marker.recordStatus;
    }

    public char getRecordType() {
        return marker.recordType;
    }

    public char getBibliographicLevel() {
        return marker.bibliographicLevel;
    }

    public char getHierarchicalLevel() {
        return marker.hierarchicalLevel;
    }

    public char getCodingLevel() {
        return marker.codingLevel;
    }

    public char getFormOfCatalogingDescription() {
        return marker.formOfCatalogingDescription;
    }

    public List<RecordField> getFields(String tag) {
        List<RecordField> res = new LinkedList<RecordField>();
        for (RecordField rf : fields)
            if (rf.tag.equals(tag))
                res.add(rf);
        return res;
    }

    public RecordField getField(String tag) {
        for (RecordField rf : fields)
            if (rf.tag.equals(tag))
                return rf;
        return null;
    }

    public String getBBK() {
        for (RecordField field : getFields("686")) {
            RecordSubField subField = field.getSubField('2');
            if (subField != null && subField.dataString.equals("rubbk"))
                return field.getSubField('a').dataString == null ? EMPTY_STRING : field.getSubField('a').dataString;
        }
        return EMPTY_STRING;
    }

    public String getUDK() {
        RecordField rf = getField("675");
        if (rf == null)
            return EMPTY_STRING;
        RecordSubField rsf = rf.getSubField('a');
        if (rsf == null)
            return EMPTY_STRING;
        return rsf.dataString == null ? EMPTY_STRING : rsf.dataString;
    }

    public ISBN getISBN() {
        ISBN parentISBN = getParentISBN();
        List<ISBN> isbns = new LinkedList<ISBN>();
        for (RecordField rf : getFields("010")) {
            RecordSubField rsf;
            ISBN isbn = null;
            if ((rsf = rf.getSubField('a')) != null) {
                String dataString = rsf.dataString == null ? "" : rsf.dataString;
                isbn = new ISBN(dataString);
            } else if ((rsf = rf.getSubField('z')) != null) {
                String dataString = rsf.dataString == null ? "" : rsf.dataString;
                isbn = new ISBN(dataString, true);
            }
            if (isbn != null && isbn.getState() != ISBN.StateEnum.Empty && !isbn.equals(parentISBN)) {
                isbns.add(isbn);
            }
        }
        return isbns.size() == 0 ? new ISBN() : isbns.get(isbns.size() - 1);
    }

    public ISBN getParentISBN() {
        List<RecordField> l = getFields("461");
        if (l.size() > 0) {
            RecordField rf = l.get(0);
            List<RecordSubField> lrsf = rf.getSubFields('1');
            if (lrsf.size() > 0) {
                for (RecordSubField sf : lrsf) {
                    if (sf.dataString == null || !sf.dataString.startsWith("010")) continue;
                    //5=tag+(must be ind1+ind2, if it is not from MARC-SQL shit)
                    String[] fields = sf.dataString.substring(3 + (sf.dataString.charAt(3) == DELIMITER ? 0 : 2)).split("" + DELIMITER);
                    for (String s : fields) {
                        if (s.length() > 0 && s.charAt(0) == 'a')
                            return new ISBN(s.substring(1));
                    }
                }
            }
        }
        return new ISBN();
    }

    private static final String Consonants = "бвгджзклмнпрстфхцчшщbcdfghklmnpqrstuvwxyz1234567890";

    private static boolean isConsonant(char c) {
        for (char consonant : Consonants.toCharArray())
            if (consonant == c)
                return true;
        return false;
    }

    public String getStringForHash() {
        StringBuilder sb = new StringBuilder();
        RecordSubField rsf;

        RecordField rf = getField("010");
        if (rf != null) {
            if ((rsf = rf.getSubField('a')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('9')) != null)
                sb.append(rsf.dataString);
        }

        rf = getField("101");
        if (rf != null)
            if ((rsf = rf.getSubField('a')) != null)
                sb.append(rsf.dataString);

        rf = getField("200");
        if (rf != null) {
            if ((rsf = rf.getSubField('a')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('e')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('f')) != null)
                sb.append(rsf.dataString);
        }

        rf = getField("205");
        if (rf != null) {
            if ((rsf = rf.getSubField('a')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('b')) != null)
                sb.append(rsf.dataString);
        }

        rf = getField("210");
        if (rf != null) {
            if ((rsf = rf.getSubField('a')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('c')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('d')) != null)
                sb.append(rsf.dataString);
        }

        rf = getField("225");
        if (rf != null)
            if ((rsf = rf.getSubField('a')) != null)
                sb.append(rsf.dataString);

        rf = getField("461");
        if (rf != null)
            for (RecordSubField subField : rf.subFields)
                sb.append(subField.dataString);

        rf = getField("700");
        if (rf != null) {
            if ((rsf = rf.getSubField('a')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('b')) != null)
                sb.append(rsf.dataString);
            if ((rsf = rf.getSubField('g')) != null)
                sb.append(rsf.dataString);
        }

        for (int i = sb.length() - 1; i >= 0; --i) {
            if (isConsonant(sb.charAt(i))) continue;
            sb.delete(i, i + 1);
        }

        return sb.toString().toLowerCase();
    }

    public String getLang() {
        RecordField rf = getField("101");
        if (rf == null)
            return EMPTY_STRING;
        RecordSubField rsf = rf.getSubField('a');
        if (rsf == null)
            return EMPTY_STRING;
        return rsf.dataString == null ? EMPTY_STRING : rsf.dataString;
    }


    public String[] getInfo() {
        char[] trimMask = new char[]{']', '[', '(', ')'};
        String[] res = new String[5];
        res[0] = "";//title
        res[1] = "";//title2
        res[2] = "";//author
        res[3] = "";//publisher
        res[4] = "";//publication date
        List<RecordField> l = getFields("461");
        if (l.size() > 0) {
            RecordField rf = l.get(0);
            List<RecordSubField> lrsf = rf.getSubFields('1');
            if (lrsf.size() > 0) {
                for (RecordSubField sf : lrsf) {
                    if (sf.dataString.startsWith("200")) {//                                           5=tag+(must be ind1+ind2, if it is not from MARC-SQL shit)
                        String[] fields = sf.dataString.substring(3 + (sf.dataString.charAt(3) == DELIMITER ? 0 : 2)).split("" + DELIMITER);
                        char lastchar = '\0';
                        for (String s : fields) {
                            if (s.length() > 0) {
                                if (s.charAt(0) == 'a' && lastchar == '\0')
                                    res[0] = trim(s.substring(1), trimMask);

                                if (s.charAt(0) == 'b' && lastchar == 'a')
                                    res[0] += (res[0].length() == 0 ? "" : " ") + "[" + trim(s.substring(1), trimMask) + "]";
                                if (s.charAt(0) == 'e' && (lastchar == 'b' || lastchar == 'a' || lastchar == '\0'))
                                    res[0] += (res[0].length() == 0 ? "" : " ") + trim(s.substring(1), trimMask);

                                if (s.charAt(0) == 'v')
                                    res[1] = trim(s.substring(1), trimMask);
                            }
                        }
                    }
                    if (sf.dataString.startsWith("210")) {
                        String[] fields = sf.dataString.substring(3).split("" + DELIMITER);
                        for (String s : fields) {
                            if (s.length() > 0) {
                                if (s.charAt(0) == 'c')
                                    res[3] = trim(s.substring(1), trimMask);
                                if (s.charAt(0) == 'd')
                                    res[4] = trim(s.substring(1), trimMask);
                            }
                        }
                    }
                    if (sf.dataString.startsWith("700")) {
                        String[] fields = sf.dataString.substring(3).split("" + DELIMITER);
                        String a2 = "";
                        for (String s : fields) {
                            if (s.length() > 0) {
                                if (s.charAt(0) == 'a')
                                    res[2] = trim(s.substring(1), trimMask);
                                if (s.charAt(0) == 'g')
                                    a2 = ", " + trim(s.substring(1), trimMask);
                                if (s.charAt(0) == 'b' && a2.length() == 0)
                                    a2 = ", " + trim(s.substring(1), trimMask);
                            }
                        }
                        res[2] += a2;
                    }
                }
            } else {
                RecordSubField rsf = rf.getSubField('t');
                if (rsf != null)
                    res[0] = trim(rsf.dataString, trimMask);
                if ((rsf = rf.getSubField('v')) != null)
                    res[1] = trim(rsf.dataString, trimMask);
                if ((rsf = rf.getSubField('i')) != null)
                    res[1] += (res[1].length() == 0 ? "" : ", ") + trim(rsf.dataString, trimMask);
            }
            {
                l = getFields("200");
                if (l.size() != 0 && res[1] == null)//только если не взяли из уровня набора (461 поле)
                {
                    rf = l.get(0);
                    RecordSubField rsf = rf.getSubField('a');
                    if (rsf != null)
                        res[1] = trim(rsf.dataString, trimMask);
                    if ((rsf = rf.getSubField('b')) != null)
                        res[1] += (res[1].length() == 0 ? "" : " ") + "[" + trim(rsf.dataString, trimMask) + "]";
                    if ((rsf = rf.getSubField('e')) != null)
                        res[1] = trim(rsf.dataString, trimMask);
                    if ((rsf = rf.getSubField('f')) != null)
                        res[2] = trim(rsf.dataString, trimMask);
                }
            }
        } else {
            l = getFields("200");
            if (l.size() != 0) {
                RecordField rf = l.get(0);
                RecordSubField rsf = rf.getSubField('a');
                if (rsf != null)
                    res[0] = trim(rsf.dataString, trimMask);
                if ((rsf = rf.getSubField('b')) != null)
                    res[0] += (res[0].length() == 0 ? "" : " ") + "[" + trim(rsf.dataString, trimMask) + "]";
                if ((rsf = rf.getSubField('e')) != null)
                    res[1] = trim(rsf.dataString, trimMask);
                if ((rsf = rf.getSubField('f')) != null)
                    res[2] = trim(rsf.dataString, trimMask);
            }
        }
        l = getFields("700");
        if (l.size() != 0) {
            RecordField rf = l.get(0);
            RecordSubField rsf = rf.getSubField('a');
            if (rsf != null)
                res[2] = trim(rsf.dataString, trimMask);
            if ((rsf = rf.getSubField('g')) != null)
                res[2] += (res[2].length() == 0 ? "" : ", ") + trim(rsf.dataString, trimMask);
            else if ((rsf = rf.getSubField('b')) != null)
                res[2] += (res[2].length() == 0 ? "" : ", ") + trim(rsf.dataString, trimMask);
        }
        l = getFields("210");
        if (l.size() != 0) {
            RecordField rf = l.get(0);
            RecordSubField rsf = rf.getSubField('c');
            if (rsf != null)
                res[3] = trim(rsf.dataString, trimMask);
            rsf = rf.getSubField('d');
            if (rsf != null)
                res[4] = trim(rsf.dataString, trimMask);
        }
        res[4] = dropNotNumeric(res[4]);
        return res;
    }

    private static String dropNotNumeric(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray())
            if (c >= '0' && c <= '9')
                sb.append(c);
        return sb.toString();
    }

    public void sortFields() {
        for (int i = 0, max = fields.size() - 1; i < max; ++i)
            for (int j = max - 1; j >= i; --j)
                if (Integer.parseInt(fields.get(j).tag) > Integer.parseInt(fields.get(j + 1).tag)) {
                    RecordField f = fields.get(j);
                    fields.remove(j);
                    fields.add(j + 1, f);
                }
    }

    private static String trim(String str, char[] TrimMask) {
        StringBuilder sb = new StringBuilder();
        for (char c : TrimMask)
            sb.append('\\').append(c).append('|');
        sb.setLength(sb.length() - 1);
        return str.replaceAll(sb.toString(), EMPTY_STRING);
    }

    public String generateCard() {
        //[автор1,автор2...] [Заглавие]:[Прод.заглавия][/пер. с др. языка переводчик;худ....] -[место издания]:[издатель1;издатель2...], [год издания]. - [кол-во страниц:ил.] [-(серия).] [инв1,инв2...]
        char[] trimMask = new char[]{']', '[', '(', ')'};
        StringBuilder sb = new StringBuilder();

        //authors
        List<RecordField> fs = getFields("700");
        fs.addAll(getFields("701"));
        if (fs.size() == 0) {
            if (getFields("702").size() == 0) {
                fs = getFields("200");
                if (fs.size() != 0) {
                    RecordSubField rsf = fs.get(0).getSubField('f');
                    if (rsf != null)
                        sb.append(trim(rsf.dataString, trimMask)).append(' ');
                }
            }
        } else {
            boolean isFirst = true;
            for (RecordField rf : fs) {
                String res = isFirst ? "/" : "; ";
                RecordSubField rsf = rf.getSubField('a');
                if (rsf != null)
                    res = trim(rsf.dataString, trimMask);
                if ((rsf = rf.getSubField('b')) != null)
                    res += (res.length() == 0 ? "" : " ") + trim(rsf.dataString, trimMask);
                else if ((rsf = rf.getSubField('g')) != null)
                    res += (res.length() == 0 ? "" : " ") + trim(rsf.dataString, trimMask);
                sb.append(res);
                isFirst = false;
            }
            if (!isFirst)
                sb.append(' ');
        }

        //titles
        fs = getFields("200");
        if (fs.size() != 0) {
            String res = EMPTY_STRING;
            RecordField rf = fs.get(0);
            RecordSubField rsf = rf.getSubField('a');
            if (rsf != null)
                res = trim(rsf.dataString, trimMask);
            if ((rsf = rf.getSubField('e')) != null)
                res += (res.length() == 0 ? "" : " : ") + trim(rsf.dataString, trimMask);
            sb.append(res);
        }

        //second authority
        fs = getFields("702");
        if (fs.size() != 0) {
            boolean isFirst = true;
            for (RecordField rf : fs) {
                StringBuilder res = new StringBuilder();
                RecordSubField rsf = rf.getSubField('a');
                if (rsf != null)
                    res.append(trim(rsf.dataString, trimMask));
                if ((rsf = rf.getSubField('b')) != null)
                    res.append(res.length() == 0 ? "" : " ").append(trim(rsf.dataString, trimMask));
                else if ((rsf = rf.getSubField('g')) != null)
                    res.append(res.length() == 0 ? "" : " ").append(trim(rsf.dataString, trimMask));
                if ((rsf = rf.getSubField('4')) != null) {
                    String s = rsf.dataString;
                    if (s.equals("440"))
                        res.insert(0, "илл. ");
                    else if (s.equals("651"))
                        res.insert(0, "гл. редактор ");
                    else if (s.equals("120") || s.equals("130") || s.equals("140") || s.equals("150"))
                        res.insert(0, "оформ. ");
                    else if (s.equals("730"))
                        res.insert(0, "пер. ");
                    else if (s.equals("340"))
                        res.insert(0, "под ред. ");
                    else if (s.equals("220"))
                        res.insert(0, "сост. ");
                    else if (s.equals("600"))
                        res.insert(0, "фотограф ");
                    else if (s.equals("040"))
                        res.insert(0, "худож. ");
                }
                sb.append(isFirst ? " /" : "; ").append(res);
                isFirst = false;
            }
        }

        //publisher
        fs = getFields("210");
        if (fs.size() != 0) {
            String res = EMPTY_STRING;
            RecordField rf = fs.get(0);
            RecordSubField rsf = rf.getSubField('a');
            if (rsf != null)
                res = trim(rsf.dataString, trimMask);
            if ((rsf = rf.getSubField('c')) != null)
                res += (res.length() == 0 ? "" : ":") + trim(rsf.dataString, trimMask);
            if ((rsf = rf.getSubField('d')) != null)
                res += (res.length() == 0 ? "" : ", ") + dropNotNumeric(trim(rsf.dataString, trimMask)).trim();
            if (res.length() > 0) {
                sb.append(" -");
                res += '.';
            }
            sb.append(res);
        }

        //pages
        fs = getFields("215");
        if (fs.size() != 0) {
            String res = EMPTY_STRING;
            RecordField rf = fs.get(0);
            RecordSubField rsf = rf.getSubField('a');
            if (rsf != null)
                res = trim(rsf.dataString, trimMask);
            if ((rsf = rf.getSubField('c')) != null)
                res += (res.length() == 0 ? "" : ":") + trim(rsf.dataString, trimMask);
            if (res.length() != 0)
                sb.append(' ').append(res);
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ru.axetta.rusmarc.Record))
            return false;
        ru.axetta.rusmarc.Record r = (ru.axetta.rusmarc.Record) obj;
        return fields.equals(r.fields) && marker.equals(r.marker);
    }
}

