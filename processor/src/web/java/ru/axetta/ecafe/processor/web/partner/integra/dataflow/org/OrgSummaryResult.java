package ru.axetta.ecafe.processor.web.partner.integra.dataflow.org;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ResultConst;

/**
 * User: Shamil
 * Date: 08.09.14
 */
public class OrgSummaryResult extends Result {
    public OrgSummury orgSummury;

    public OrgSummaryResult() {
        resultCode = ResultConst.CODE_OK;
        description = ResultConst.DESCR_OK;
    }
}
