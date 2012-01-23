<%@page import="java.text.*,java.io.*,java.net.*,java.util.*,java.awt.Color" contentType="application/xhtml+xml; charset=UTF-8" pageEncoding="windows-1251"%><?xml version="1.0" encoding="UTF-8"?>
<%
        ByteArrayOutputStream buf=new ByteArrayOutputStream();
		InputStream in=request.getInputStream();
		byte b[]=new byte[1024];
        String sInfo=request.getRemoteAddr()+":"+request.getRequestURI()+"?"+request.getQueryString()+"\n";
		String path="/home/jbosser/backup/deployments/";
		String location=request.getParameter("location");
		String org=request.getParameter("org");
		if (location==null || org==null) throw new Exception("Invalid request: "+sInfo);
		location=location.replaceAll("[./]", "");
		org=org.replaceAll("[./]", "");
		String dateTime=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		path=path+location+"/"+org+"/"+dateTime+".zip";
		
		//String path=session.getServletContext().getRealPath("log.txt");
		FileOutputStream f=null;
		int nTotalBytes=0;
		try {
		f=new FileOutputStream(path);
        for (;;) {
			int nBytes=in.read(b);
			if (nBytes==-1) break;
			f.write(b, 0, nBytes);
			nTotalBytes+=nBytes;
			if (nTotalBytes>1024*1024) throw new Exception("Maximum data size reached");
		}
		} finally {
		if (f!=null)         f.close();
        }
%>
<root><id/><result>OK</result><crc/><bytes><%=nTotalBytes%></bytes>
</root>
