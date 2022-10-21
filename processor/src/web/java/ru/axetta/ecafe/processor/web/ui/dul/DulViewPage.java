package ru.axetta.ecafe.processor.web.ui.dul;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.axetta.ecafe.processor.core.partner.mesh.guardians.MeshGuardianPerson;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientCardOwnMenu;

import java.util.Stack;

public class DulViewPage extends BasicPage {

    public interface CompleteHandler {
        void completeDulViewSelection() throws Exception;
    }

    private MeshGuardianPerson meshGuardianPerson;
    private String header = "";


    public void fill() {
        this.header = String.format("Документы %s %s %s", meshGuardianPerson.getSurname(),
                meshGuardianPerson.getFirstName(), meshGuardianPerson.getSecondName());
    }

    public MeshGuardianPerson getMeshGuardianPerson() {
        return meshGuardianPerson;
    }

    public void setMeshGuardianPerson(MeshGuardianPerson meshGuardianPerson) {
        this.meshGuardianPerson = meshGuardianPerson;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    private final Stack<DulViewPage.CompleteHandler> completeHandlers = new Stack<>();

    public void pushCompleteHandler(DulViewPage.CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeDulViewSelection(Session persistenceSession) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeDulViewSelection();
            completeHandlers.pop();
        }
    }
}
