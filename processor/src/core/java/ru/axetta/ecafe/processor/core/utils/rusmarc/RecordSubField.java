package ru.axetta.ecafe.processor.core.utils.rusmarc;

public class RecordSubField extends FieldDecoder {
    /**
     * ИДЕНТИФИКАТОР ПОДПОЛЯ, Subfield Identifier - код, идентифицирующий отдельные подполя внутри переменного поля. Состоит из двух символов. Первый символ - разделитель (Delimiter), всегда один и тот же уникальный символ, установленный по ISO 2709, второй символ - код подполя (Subfield code), который может быть цифровым (Numeric) или буквенным (Alphabetic).
     */
    public char subFieldCode;

    public RecordSubField(char subFieldCode, byte[] data) {
        this.subFieldCode = subFieldCode;
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecordSubField))
            return false;
        RecordSubField rsf = (RecordSubField) obj;
        return subFieldCode == rsf.subFieldCode && ((dataString == null && rsf.dataString == null) || (dataString != null && dataString.equals(rsf.dataString)));
    }
}
