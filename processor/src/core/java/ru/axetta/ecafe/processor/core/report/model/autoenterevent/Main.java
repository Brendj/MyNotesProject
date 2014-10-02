package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

/**
 * User: shamil
 * Date: 23.09.14
 * Time: 15:55
 */
public class Main {
           /*
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    private Properties reportProperties = new Properties();
    private static final int DEFAULT_REPORT_WIRTH =  550;


    public static void main(String[] args) throws Exception {


        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(tempData());
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(1366549199000L);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(1366894799000L);
        JasperPrint jp = JasperFillManager.fillReport(
                "C:\\project\\ecafe\\branches\\jboss7\\processor\\src\\report\\AutoEnterEventV2Report.jasper", null,
                createDataSource(null, new BasicReportJob.OrgShortItem(0L),start.getTime() ,end.getTime() , null, null));
        int orgCount = getFriendlyOrgs(0L).size() - 1;
        jp.setPageWidth(DEFAULT_REPORT_WIRTH + 400*orgCount );
        JasperViewer.viewReport(jp);

    }
    //Тестовые данные для jasper designer
    public static List<StClass> tempData() {

        final List<ShortBuilding> shortBuildingList = new LinkedList<ShortBuilding>() {{
            add(new ShortBuilding("B3", "2"));
            add(new ShortBuilding("B1", "2"));
            add(new ShortBuilding("B2", "2"));
            add(new ShortBuilding("B4", "2"));
        }};

        final List<Data> dataList = new ArrayList<Data>() {{
            add(new Data("nam1e", "B4"));
            add(new Data("nam1e", "03.01.2014", "B4"));
            add(new Data("name", "B1"));
            add(new Data("name", "B2"));
            add(new Data("name", "B3"));
            add(new Data("name", "02.01.2014", "B3"));
        }};

        List<StClass> classList = new LinkedList<StClass>() {{
            add(new StClass("1A", shortBuildingList, dataList));
            add(new StClass("2A", shortBuildingList, dataList));
            add(new StClass("3Б", shortBuildingList, dataList));
        }};

        return classList;
    }

    private static JRDataSource createDataSource(Session session, BasicReportJob.OrgShortItem org, Date startTime,
            Date endTime, Calendar calendar, Map<String, Object> parameterMap) throws Exception {
        startTime = CalendarUtils.truncateToDayOfMonth(startTime);
        endTime = CalendarUtils.truncateToDayOfMonth(endTime);

        //Список организаций
        List<ShortBuilding> friendlyOrgs = getFriendlyOrgs(org.getIdOfOrg());
        String friendlyOrgsIds = "" + org.getIdOfOrg();
        for(ShortBuilding building : friendlyOrgs) {
            friendlyOrgsIds += "," + building.getId();
        }

        //данные для отчета
        Connection connection = null;
        ResultSet rs = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/moscow_141_14_07", "postgres", "postgres");
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(
                    "SELECT  ee.idofenterevent, ee.idoforg, ee.passdirection, ee.eventcode, ee.idofclient,ee.evtdatetime, "
                            + "    pn.firstname, pn.surname, pn.secondname, cg.groupname, os.officialname "
                            + "    FROM cf_enterevents ee "
                            + "    LEFT JOIN cf_clients cs ON ee.idoforg = cs.idoforg AND ee.idofclient = cs.idofclient "
                            + "    LEFT JOIN cf_persons pn ON pn.idofperson = cs.idofperson "
                            + "    LEFT JOIN cf_clientgroups cg ON cg.idofclientgroup = cs.idofclientgroup AND ee.idoforg = cg.idoforg "
                            + "    LEFT JOIN  cf_orgs os ON ee.idoforg = os.idoforg " + "     WHERE ee.idoforg IN (" + friendlyOrgsIds +") "
                            + "     AND ee.evtdatetime BETWEEN " + startTime.getTime() + " AND " + endTime.getTime()
                            + "     AND ee.idofclient IS NOT null "
                            + "     AND cs.idofclientgroup BETWEEN 1000000000 AND 1100000000 "
                            + "     ORDER BY os.officialname, cg.groupname, ee.idofclient,ee.evtdatetime     --limit 100");

        } catch (Exception i) {
        } finally {
            if (connection != null) {
                connection.close();
            }
        }


        //Query query = session.createSQLQuery(      "");

        //query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
        //query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
        //query.setParameter("startTime", startTime.getTime());
        //query.setParameter("endTime", endTime.getTime());
        //query.setParameter("idOfOrg", org.getIdOfOrg());
        //
        //List resultList = query.list();

        Map<String, StClass> stClassMap = new HashMap<String, StClass>(); //class, List<ClientEnter>
        final ResultSet finalRs = rs;
        Long currentClient = -1L;

        //парсим данные
        while (finalRs.next()) {
            if (!stClassMap.containsKey(finalRs.getString("groupname"))) {
                stClassMap.put(finalRs.getString("groupname"),
                        new StClass(finalRs.getString("groupname"), friendlyOrgs, new LinkedList<Data>()));
            }
            List<Data> currentClassList = stClassMap.get(finalRs.getString("groupname")).getDataList();

            if (!currentClient.equals(rs.getLong("idofclient"))) {
                currentClassList.addAll(prepareDataList(rs, friendlyOrgs, startTime, endTime));
                currentClient = rs.getLong("idofclient");
            }
            for (Data event : currentClassList) {
                if ((event.getF01().equals(""+finalRs.getLong("idofclient")))
                        && (event.getF03().equals(finalRs.getString("groupname")))
                        && (event.getF04().equals(CalendarUtils.dateShortToString(new Date(rs.getLong("evtdatetime")))))
                        && (event.getF05().equals(finalRs.getString("officialname")))) {
                    updateEventData(event, rs);
                }
            }
        }
        //заполняем время внутри
        List<StClass> stClassList = new LinkedList<StClass>(stClassMap.values());
        for(StClass stClass : stClassList){
            for (Data data : stClass.getDataList()){
                updateInsideTime(data);
            }
        }

        Collections.sort(stClassList);
        return new JRBeanCollectionDataSource(stClassList);
    }

    private static void updateInsideTime(Data data) throws ParseException {
        if( (data.getF06() != null) && (data.getF07() != null) ) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date date1 = format.parse(data.getF06());
            Date date2 = format.parse(data.getF07());
            long difference = date2.getTime() - date1.getTime();
            long minutes = difference / (60 * 1000) % 60;
            data.setF08("" + difference/(60 * 60 * 1000) + ":" + (minutes < 10 ? "0" + minutes : minutes)  );
        }
    }

    //возвращает список Data с заполненными дата-корпусами
    private static List<Data> prepareDataList(ResultSet rs, List<ShortBuilding> friendlyOrgs, Date begin, Date end)
            throws SQLException {
        List<Data> resultList = new LinkedList<Data>();
        List<String> dateList = new LinkedList<String>();
        dateList.add(CalendarUtils.dateShortToString(begin));
        while (!begin.equals(end)) {
            begin = CalendarUtils.addDays(begin, 1);
            dateList.add(CalendarUtils.dateShortToString(begin));
        } ;

        for (String date : dateList) {
            for (ShortBuilding building : friendlyOrgs) {
                Data eventData = new Data();
                eventData.setEventId(rs.getLong("idofenterevent"));
                eventData.setF01("" + rs.getLong("idofclient"));
                eventData.setF02(rs.getString("surname") + " " + rs.getString("firstname") + " " + rs
                        .getString("secondname"));
                eventData.setF03(rs.getString("groupname"));
                eventData.setF04(date);
                eventData.setF05(building.getF05());
                resultList.add(eventData);
            }
        }

        return resultList;
    }

    //Добавляет событие прохода к записи
    private static void updateEventData(Data data, ResultSet rs) throws SQLException {
        Data newData = new Data(rs);

        if (newData.getF06() != null) {
            if (data.getF06() != null) {
                if (CalendarUtils.timeEquals(data.getF06(), newData.getF06())) {
                    data.setF06(newData.getF06());
                    data.setF09(newData.getF09() + "," + data.getF09());
                } else {
                    data.setF09(data.getF09() + "," + newData.getF09());
                }
            } else {
                data.setF06(newData.getF06());
                if (data.getF09() != null) {
                    data.setF09(data.getF09() + "," + newData.getF09());
                } else {
                    data.setF09(newData.getF09());
                }
            }
        }

        if (newData.getF07() != null) {
            if (data.getF07() != null) {

                if (!CalendarUtils.timeEquals(data.getF07(), newData.getF07())) {
                    data.setF07(newData.getF07());
                }
                data.setF09(data.getF09() + "," + newData.getF09());
            } else {
                data.setF07(newData.getF07());
                if (data.getF09() != null) {
                    data.setF09(data.getF09() + "," + newData.getF09());
                } else {
                    data.setF09(newData.getF09());
                }
            }

        }
    }


    public Properties getReportProperties() {
        return reportProperties;
    }

    //находим список корпусов
    private static List<ShortBuilding> getFriendlyOrgs(Long idOfOrg) {
        List<ShortBuilding> resultList = new LinkedList<ShortBuilding>();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/moscow_141_14_07", "postgres", "postgres");
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT o.idoforg, o.officialname  " + "FROM cf_friendly_organization f "
                    + "LEFT JOIN cf_orgs o ON o.idoforg = f.friendlyorg WHERE currentorg = " + idOfOrg + " ORDER BY o.officialname");

            while (resultSet.next()) {
                resultList.add(new ShortBuilding(resultSet.getLong("idoforg"),resultSet.getString("officialname"), "2"));
            }
        } catch (Exception ignore) {

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultList;
    }                                   */
}
