
package generated.nsiws;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class SmevReference
    extends JAXBElement<ReferenceType>
{

    protected final static QName NAME = new QName("http://smev.gosuslugi.ru/rev110801", "Reference");

    public SmevReference(ReferenceType value) {
        super(NAME, ((Class) ReferenceType.class), null, value);
    }

}
