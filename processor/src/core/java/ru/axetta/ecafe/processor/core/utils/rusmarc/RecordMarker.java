package ru.axetta.ecafe.processor.core.utils.rusmarc;

import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * МАРКЕР ЗАПИСИ (указатель) находится в начале каждой записи Российского коммуникативного формата.<br/>
 * Содержит данные, необходимые при обработке записи. Позиции символов 9, 10, 11, 20-23 содержат специальные, фиксированные на данный момент, значения и могут программно генерироваться компьютером. Позиции символов 0-4 и 12-16 содержат числовые данные, указывающие количество символов в определенных областях записи. Они определяются автоматически компьютером при формировании записи. Значения для позиций символов 5, 6-8, 17-19 могут быть переведены из данных исходной записи программой преобразования или присваиваться вручную, когда Российский коммуникативный формат используется в качестве исходного формата.
 */
public class RecordMarker {
    /**
     * <b>Длина записи (позиции символов 0-4)</b><br/>
     * Пять десятичных цифр, при необходимости выравниваемых вправо начальными нулями, указывают количество символов в записи, включая маркер записи, справочник и переменные поля. Вычисляется автоматически, когда запись окончательно сформирована для обмена.
     */
    public int recordLength;

    /**
     * <b>Статус записи (позиция символа 5)</b><br/>
     * Используются следующие три кода, обозначающие статус обработки записи:<ul>
     * <li>n = новая запись<br/>
     * Новая запись (в т. ч. допубликационная)</li>
     * <li>d = исключенная запись<br/>
     * Запись, участвующая в обмене для указания, что другая запись, имеющая соответствующий контрольный номер, более не действительна.<br/>
     * Запись может содержать только маркер, справочник и поле 001 (идентификатор записи) или может содержать все поля.<br/>
     * В обоих случаях поле 830 ОБЩЕЕ ПРИМЕЧАНИЕ, СОСТАВЛЕННОЕ КАТАЛОГИЗАТОРОМ, может использоваться для объяснения причины исключения записи.</li>
     * <li>с = откорректированная запись<br/>
     * Запись, участвующая в обмене для указания, что данная запись должна заместить другую, имеющую соответствующий контрольный номер.</li></ul>
     */
    public char recordStatus;
    public static char[] recordStatusAllowed = {'n', 'd', 'c'};
    public static String recordStatusDesc = "marker_rs";
    public static String[] recordStatusAllowedDesc = {"marker_rs_n",
            "marker_rs_d",
            "marker_rs_c"};

    /*
    Далее Коды применения (позиции символов 6-9).
    Коды применения названы так потому, что коды в позициях символов 6-9 не определены в стандарте ISO 2709,
    а зависят от особенностей конкретного применения стандарта.
    */

    /**
     * <b>Тип записи (позиция символа 6)</b><br/>
     * Необходимость определяется тем, что внутренние форматы участников обмена могут предполагать разную обработку одних и тех же полей. в зависимости от типа записи.<ul>
     * <li>а = текстовые материалы, кроме рукописных<br/>
     * В том числе печатные текстовые материалы, микроформы печатных текстовых материалов, а также электронные текстовые материалы.</li>
     * <li>b = текстовые материалы, рукописные<br/>
     * В том числе микроформы рукописных текстовых материалов и электронные рукописные текстовые материалы.</li>
     * <li>с = музыкальные партитуры, кроме рукописных<br/>
     * В том числе печатные музыкальные партитуры, микроформы печатных музыкальных партитур, а также электронные музыкальные партитуры.</li>
     * <li>d = музыкальные партитуры, рукописные<br/>
     * В том числе микроформы рукописных музыкальных партитур и электронные рукописные музыкальные партитуры.</li>
     * <li>е = картографические материалы, кроме рукописных<br/>
     * В том числе географические карты, атласы, глобусы, цифровые географические карты, а также другие картографические материалы.</li>
     * <li>f = картографические материалы, рукописные<br/>
     * В том числе микроформы рукописных картографических материалов и электронные рукописные географические карты.</li>
     * <li>g = проекционные и видеоматериалы (кинофильмы, диафильмы, слайды, пленочные материалы, видеозаписи)<br/>
     * В том числе цифровые видеоматериалы (не используется для не-проекционной двухмерной графики: см. ниже код "k").</li>
     * <li>i = звукозаписи, немузыкальные</li>
     * <li>j = звукозаписи, музыкальные</li>
     * <li>k = двухмерная графика (иллюстрации, чертежи и т. п.)<br/>
     * Примеры: графики, схемы, коллажи, компьютерная графика, рисунки, образцы для копирования ("duplication masters" и "spirit masters"), живописные изображения, фотонегативы, фотоотпечатки, почтовые открытки, плакаты, эстампы, технические чертежи, фотомеханические репродукции, а также репродукции перечисленных выше материалов.</li>
     * <li>l = электронный ресурс<br/>
     * Включает следующие классы электронных ресурсов: программное обеспечение (в том числе программы, игры, шрифты), числовые данные, мультимедиа, онлайновые системы или службы. Для этих классов материалов, если существует важный аспект, требующий отнесения материала к иной категории, определяемой значением кода поз.6 маркера, вместо кода "l" используется код, соответствующий данному аспекту (например, картографические векторные изображения кодируются как картографические материалы, а не как числовые данные). Другие классы электронных ресурсов кодируются в соответствии с наиболее важными аспектами ресурса (например, текстовый материал, графика, картографический материал, музыкальная или не-музыкальная звукозапись, движущееся изображение). В случаях, если наиболее важный аспект не может быть определен однозначно, документ кодируется как "электронный ресурс".</li>
     * <li>m = информация на нескольких носителях (например, книга с приложением программ на дискете, CD и т. п.)<br/>
     * Содержит компоненты, относящиеся к двум или более видам; ни один из компонентов не является основным в наборе.</li>
     * <li>r = трехмерные искусственные и естественные объекты<br/>
     * Включает искусственные объекты, такие как: модели, диорамы, игры, головоломки, макеты, скульптуры и другие трехмерные художественные объекты и их репродукции, экспонаты, устройства, предметы одежды, игрушки, а также естественные объекты, например: препараты для микроскопа и другие предметы, смонтированные для визуального изучения.</li></ul>
     * Отдается предпочтение коду вида каталогизируемого материала, а не коду вторичной физической формы, который указывается в поле 106, подполе $a. Поэтому код для микроформ отсутствует. Микроформы, содержащие печатный текст, в поз.6 маркера должны кодироваться как "текстовые материалы, кроме рукописных", и в поле 106, подполе $a – как "микроформы". Для атласа, в котором собраны рукописные картографические материалы, на CD-ROM, используется код "f" (картографические материалы, рукописные) в поз.6 маркера, и код "s" (электронный ресурс) в поле 106, подполе $a. Для звукозаписи, выпущенной на аналоговом носителе, должен использоваться код "i" или "j" в поз.6 маркера.
     */
    public char recordType;
    public static char[] recordTypeAllowed = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'i', 'j', 'k', 'l', 'm', 'r'};
    public static String recordTypeDesc = "marker_rt";
    public static String[] recordTypeAllowedDesc = {"marker_rt_a",
            "marker_rt_b",
            "marker_rt_c",
            "marker_rt_d",
            "marker_rt_e",
            "marker_rt_f",
            "marker_rt_g",
            "marker_rt_i",
            "marker_rt_j",
            "marker_rt_k",
            "marker_rt_l",
            "marker_rt_m",
            "marker_rt_r"};

    /**
     * <b>Библиографический уровень (позиция символа 7)</b><br/>
     * Определяются пять возможных значений:<ul>
     * <li>а = аналитический - документ, является частью физической единицы (составная часть)<br/>
     * Например: статья в журнале, продолжающаяся колонка или заметка внутри журнала, отдельный доклад в сборнике трудов конференции.</li>
     * <li>i = интегрируемый ресурс – ресурс, изменяющийся посредством обновлений (изъятия, вставки или замещения отдельных его частей), которые не публикуются отдельно, а интегрируются в новое единое целое<br/>
     * Интегрируемый ресурс может быть законченным или продолжающимся. Например: обновляемый листовой документ, база данных, веб-сайт.</li>
     * <li>m = монографический - документ, представляет собой физически единое целое или издается в заранее определенном количестве частей<br/>
     * Например: отдельная книга, многотомное издание в целом, том многотомного издания, выпуск сериального издания.</li>
     * <li>s = сериальный - продолжающийся ресурс, выпускаемый последовательными частями (как правило, нумерованными и (или) датированными выпусками) и рассчитанный на издание в течение времени, продолжительность которого заранее не установлена<br/>
     * (Запись высшего уровня, составленная для описания продолжающегося ресурса).<br/>
     * Например: научный журнал, общественно-политический журнал, популярный журнал, электронный журнал, продолжающийся справочник, ежегодный отчет, газета, монографическая серия.</li>
     * <li>с = подборка - библиографическая единица, скомплектованная из отдельных физических единиц<br/>
     * Например: собрание брошюр в коробке или папке, собрание физических единиц на разных носителях в едином контейнере и т.д.<br/>
     * Этот код используется <i>только</i> при составлении библиографического описания подборки.</li></ul>
     */
    public char bibliographicLevel;
    public static char[] bibliographicLevelAllowed = {'m', 'a', 'i', 's', 'c'};
    public static String bibliographicLevelDesc = "marker_bl";
    public static String[] bibliographicLevelAllowedDesc = {"marker_bl_m",
            "marker_bl_a",
            "marker_bl_i",
            "marker_bl_s",
            "marker_bl_c"};

    /**
     * <b>Код иерархического уровня (позиция символа 8)</b><br/>
     * Код определяет иерархическую связь записи с другими записями в том же файле и показывает ее относительное положение в иерархии.<br/>
     * Используются следующие коды:<ul>
     * <li>' ' = иерархическая связь не определена</li>
     * <li>'0' = иерархическая связь отсутствует</li>
     * <li>'1' = запись высшего уровня</li>
     * <li>'2' = запись ниже высшего уровня (любая запись ниже высшего уровня)</li></ul>
     * Организации, не использующие иерархические связи, при подготовке записей всегда должны вводить символ пробела: ‘ ’.<br/>
     * Организации, использующие иерархические связи, должны вводить соответствующий код 0, 1 или 2. В этом случае значение ‘0’ означает, что, хотя в системе используются иерархические связи, для данной конкретной записи не установлены иерархические связи ни с одной записью в данном файле. Коды 1 и 2 должны использоваться, если записи других уровней действительно существуют; связанные записи должны находиться в том же файле.
     */
    public char hierarchicalLevel;
    public static char[] hierarchicalLevelAllowed = {' ', '0', '1', '2'};
    public static String hierarchicalLevelDesc = "marker_hl";
    public static String[] hierarchicalLevelAllowedDesc = {"marker_hl__",
            "marker_hl_0",
            "marker_hl_1",
            "marker_hl_2"};

    /**
     * <b>Не определено (позиция символа 9)</b><br/>
     * Содержит символ пробела: ' '.
     */
    public char undefinedSymbol9 = ' ';

    /**
     * <b>Длина индикатора (позиция символа 10)</b><br/>
     * Содержит цифру "2".
     */
    protected char indicatorLength = '2';

    /**
     * <b>Длина идентификатора подполя (позиция символа 11)</b><br/>
     * Содержит цифру "2".
     */
    protected char subfieldIndicatorLength = '2';

    /**
     * <b>Базовый адрес данных (позиции символов 12-16)</b><br/>
     * Пять десятичных цифр, выровненных вправо начальными нулями, указывающие на начальную символьную позицию первого поля данных относительно начала записи. Это число будет равно общему количеству символов в маркере и справочнике, включая разделитель подполя в конце справочника. В справочнике начальная позиция символов для каждого поля задается относительно первого символа первого поля данных, которое является полем 001, а не от начала записи. Базовый адрес, таким образом, является основой, с помощью которой рассчитывается позиция каждого поля. Должен генерироваться системой.
     */
    public int dataAddress;

    /*
    Далее дополнительное определение записи (позиции символов 17-19)
    Три позиции символов, содержащие коды, которые дают дополнительные сведения, необходимые для обработки записи.
    */

    /**
     * <b>Уровень кодирования (позиция символа 17)</b><br/>
     * Односимвольный код, указывающий на степень полноты машинной записи и на то, просматривался ли сам каталогизируемый документ в процессе создания записи.<ul>
     * <li>' ' = полный уровень<br/>
     * Корректно составленная запись о полностью каталогизированном документе, подготовленная для использования в Электронном каталоге или для обмена.<br/>
     * (Запись, составленная на основе каталогизируемого документа, у которой заполнены все поля и подполя со статусами "обязательное" и "условно обязательное").</li>
     * <li>'1' = подуровень 1<br/>
     * Запись, составленная на основе каталожной карточки (ретроконверсия) или импортированная из другого формата, не предоставляющего достаточно данных для корректного заполнения всех обязательных (в т. ч. условно обязательных) элементов формата, и не откорректированная по документу.</li>
     * <li>'2' = подуровень 2<br/>
     * Опознавательная запись (например, допубликационная запись, запись на документ, не проходивший каталогизацию).</li>
     * <li>3 = подуровень 3<br/>
     * Не полностью каталогизированный документ.<br/>
     * (Запись на документ в процессе каталогизации)</li></ul>
     */
    public char codingLevel;
    public static char[] codingLevelAllowed = {'1', ' ', '2', '3'};
    public static String codingLevelDesc = "marker_cl";
    public static String[] codingLevelAllowedDesc = {"marker_cl_1",
            "marker_cl__",
            "marker_cl_2",
            "marker_cl_3"};

    /**
     * <b>Форма каталогизационного описания (позиция символа 18)</b><br/>
     * Односимвольный код, указывающий форму каталогизационного описания, используемую в записи; показывает, соответствуют ли описательные поля 200-225 требованиям ISBD.<br/>
     * Код принимает следующие значения:<ul>
     * <li>' ' = запись составлена по правилам ISBD.</li>
     * <li>'i' = запись составлена не полностью по правилам ISBD (отдельные поля соответствуют положениям ISBD).</li></ul>
     */
    public char formOfCatalogingDescription;
    public static char[] formOfCatalogingDescriptionAllowed = {'i', ' '};
    public static String formOfCatalogingDescriptionDesc = "marker_fcd";
    public static String[] formOfCatalogingDescriptionAllowedDesc = {"marker_fcd_i",
            "marker_fcd__"};

    /**
     * <b>Не определено (позиция символа 19)</b><br/>
     * Содержит символ пробела: ' '.
     */
    protected char undefinedSymbol19 = ' ';

    /*
    Далее план справочника (позиции символов 20-23)
    Указывается длина и структура статьи справочника для каждого поля Российского коммуникативного формата. Используются 4 позиции.
    */

    /**
     * <b>Длина элемента "длина поля данных" (позиция символа 20)</b><br/>
     * Десятичная цифра, указывающая количество символов в части статьи справочника "длина поля данных". В настоящем формате используется значение 4. Это означает, что максимальная длина поля – 9999 символов.
     */
    protected char lengthOfLengthOfDataField = '4';

    /**
     * <b>Длина элемента "позиция начального символа" (позиция символа 21)</b><br/>
     * Десятичная цифра, указывающая количество символов в части статьи справочника "позиция начального символа". В настоящем формате используется значение 5. Это означает, что максимальная длина записи – около 100000 символов.
     */
    protected char lengthOfStartingCharacterPosition = '5';

    /**
     * <b>Длина элемента "часть, определяемая при применении" (позиция символа 22)</b><br/>
     * Десятичная цифра, указывающая количество символов в части статьи справочника "часть, определяемая при применении". В настоящем формате указанная часть не используется, поэтому поз.22 содержит значение 0.
     */
    protected char lengthOfAdditionalPart = '0';

    /**
     * <b>Не определено (позиция символа 23)</b><br/>
     * Содержит символ пробела: ' '.
     */
    protected char undefinedSymbol23 = ' ';

    /*
   Таким образом, последовательность символов в позициях 20-23 в настоящем формате всегда имеет вид: "450 "
    */

    public RecordMarker(char recordStatus, char recordType, char bibliographicLevel, char hierarchicalLevel, char codingLevel, char formOfCatalogingDescription) {
        this.recordLength = -1;
        this.recordStatus = recordStatus;
        this.recordType = recordType;
        this.bibliographicLevel = bibliographicLevel;
        this.hierarchicalLevel = hierarchicalLevel;
        dataAddress = -1;
        this.codingLevel = codingLevel;
        this.formOfCatalogingDescription = formOfCatalogingDescription;
        check();
    }

    public RecordMarker(DataInput dataInput) throws IOException {
        byte[] c = new byte[24];
        dataInput.readFully(c, 0, 24);
        String fullMarker = new String(c, "ASCII");

        try {
            recordLength = Integer.parseInt(fullMarker.substring(0, 5));
        } catch (java.lang.NumberFormatException ignored) {
            recordLength = -1;
        }

        recordStatus = fullMarker.charAt(5);
        recordType = fullMarker.charAt(6);
        bibliographicLevel = fullMarker.charAt(7);
        hierarchicalLevel = fullMarker.charAt(8);

        try {
            dataAddress = Integer.parseInt(fullMarker.substring(12, 17));
        } catch (java.lang.NumberFormatException ignored) {
            dataAddress = -1;
        }

        codingLevel = fullMarker.charAt(17);
        formOfCatalogingDescription = fullMarker.charAt(18);
        check();
    }

    private void check() {
        if (!contains(recordStatusAllowed, recordStatus))
            recordStatus = recordStatusAllowed[0];

        if (!contains(recordTypeAllowed, recordType))
            recordType = recordTypeAllowed[0];

        if (!contains(bibliographicLevelAllowed, bibliographicLevel))
            bibliographicLevel = bibliographicLevelAllowed[0];

        if (!contains(hierarchicalLevelAllowed, hierarchicalLevel))
            hierarchicalLevel = hierarchicalLevelAllowed[0];

        if (!contains(codingLevelAllowed, codingLevel))
            codingLevel = codingLevelAllowed[0];

        if (!contains(formOfCatalogingDescriptionAllowed, formOfCatalogingDescription))
            formOfCatalogingDescription = formOfCatalogingDescriptionAllowed[0];
    }

    private boolean contains(char[] arr, char key) {
        for (char c : arr)
            if (c == key)
                return true;
        return false;
    }

    public StringBuilder getFullMarker() {
        StringBuilder sb = Record.numberToString(recordLength, 5);
        sb.append(recordStatus);
        sb.append(recordType);
        sb.append(bibliographicLevel);
        sb.append(hierarchicalLevel);
        sb.append(undefinedSymbol9);
        sb.append(indicatorLength);
        sb.append(subfieldIndicatorLength);
        sb.append(Record.numberToString(dataAddress, 5));
        sb.append(codingLevel);
        sb.append(formOfCatalogingDescription);
        sb.append(undefinedSymbol19);
        sb.append(lengthOfLengthOfDataField);
        sb.append(lengthOfStartingCharacterPosition);
        sb.append(lengthOfAdditionalPart);
        sb.append(undefinedSymbol23);
        return sb;
    }

    public byte[] getFullMarkerRaw() {
        return Charset.forName("ASCII").encode(getFullMarker().toString()).array();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecordMarker))
            return false;
        RecordMarker rm = (RecordMarker) obj;
        return recordStatus == rm.recordStatus &&
                recordType == rm.recordType &&
                bibliographicLevel == rm.bibliographicLevel &&
                hierarchicalLevel == rm.hierarchicalLevel &&
                undefinedSymbol9 == rm.undefinedSymbol9 &&
                indicatorLength == rm.indicatorLength &&
                subfieldIndicatorLength == rm.subfieldIndicatorLength &&
                codingLevel == rm.codingLevel &&
                formOfCatalogingDescription == rm.formOfCatalogingDescription &&
                undefinedSymbol19 == rm.undefinedSymbol19 &&
                lengthOfLengthOfDataField == rm.lengthOfLengthOfDataField &&
                lengthOfStartingCharacterPosition == rm.lengthOfStartingCharacterPosition &&
                lengthOfAdditionalPart == rm.lengthOfAdditionalPart &&
                undefinedSymbol23 == rm.undefinedSymbol23;
    }
}
