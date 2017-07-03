/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientPhoto;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 28.07.16
 * Time: 10:37
 */
@Component
@Scope("session")
public class PhotoRegistryPageSpb extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {
    private static final Logger logger = LoggerFactory.getLogger(PhotoRegistryPageSpb.class);
    private static final String CONFIRM_QUESTION = "Изменение фотографий клиентов для выбранной организации будет заблокировано до конца суток или до принятия изменений"
            + " во избежание расхождений в данных. Для разблокировки нажмите кнопку \"Отменить сверку\"";
    private String confirm = CONFIRM_QUESTION;

    private List<PhotoRegistryItem> items = new ArrayList<PhotoRegistryItem>();

    private Org org;
    private String orgName;

    private String errorMessages;
    private String infoMessages;

    public String getOrgName() {
        return orgName;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        errorMessages = "";
        infoMessages = "";
        if (idOfOrg == null) {
            this.orgName = null;
            this.org = null;
        } else {
            this.org = (Org) session.load(Org.class, idOfOrg);
            this.orgName = org.getShortName();
        }
    }

    public String getPageTitle() {
        return "Сверка фотографий клиентов";
    }

    public long getIdOfOrg() {
        if (org == null) {
            return -1L;
        }
        return org.getIdOfOrg();
    }

    public void resetMessages() {
        errorMessages = "";
    }

    public void doUpdate() {
        errorMessages = "";
        infoMessages = "";
        long idOfOrg = getIdOfOrg();
        if (idOfOrg != -1) {
            load();
        } else {
            errorMessages = "Выберите организацию";
        }
    }

    private void load() {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg < 0L) {
            if (items == null) {
                items = new ArrayList<PhotoRegistryItem>();
            }
            items.clear();
            return;
        }

        try {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                getClientPhotoItems(persistenceSession);

                persistenceTransaction.commit();
                persistenceTransaction = null;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            errorMessages = String.format("Не удалось сформировать расхождения: %s", e.getMessage());
            logger.error(errorMessages, e);
        }
    }

    private void getClientPhotoItems(Session persistenceSession) {
        Org org1 = (Org) persistenceSession.load(Org.class, org.getIdOfOrg());
        List<Org> orgs = new ArrayList<Org>();
        orgs.addAll(org1.getFriendlyOrg());
        List<ClientPhoto> clientPhotos = ImageUtils.getNewClientPhotos(persistenceSession, orgs);

        List<Long> clientsIds = new ArrayList<Long>();
        for(ClientPhoto clientPhoto : clientPhotos) {
            clientsIds.add(clientPhoto.getIdOfClient());
        }
        List<Client> clientList = DAOUtils.findClients(persistenceSession, clientsIds);
        Map<Long, Client> clientMap = new HashMap<Long, Client>();
        for(Client client : clientList) {
            clientMap.put(client.getIdOfClient(), client);
        }

        items = new ArrayList<PhotoRegistryItem>();
        for(ClientPhoto clientPhoto : clientPhotos){
            items.add(new PhotoRegistryItem(clientPhoto, clientMap.get(clientPhoto.getIdOfClient())));
        }
    }

    public void doApply() {
        try {
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                int moved = 0;
                int deleted = 0;

                for(PhotoRegistryItem item : items){
                    if(item.selected){
                        if(movePhoto(persistenceSession, item)){
                            moved++;
                        }
                    } else if(item.denied) {
                        if(deleteNewPhoto(persistenceSession, item)){
                            deleted++;
                        }
                    }
                }

                String info = String.format("Изменения приняты. Всего расхождений - %s, из них принято %s, отклонено %s.",
                        items.size(), moved, deleted);

                getClientPhotoItems(persistenceSession);

                persistenceTransaction.commit();
                persistenceTransaction = null;
                infoMessages = info;
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        } catch (Exception e) {
            errorMessages = String.format("Не удалось провести сверку фотографий для Org id=%s: %s", org.getIdOfOrg(), e.getMessage());
            logger.error(errorMessages, e);
        }
    }

    private boolean movePhoto(Session session, PhotoRegistryItem item){
        boolean result = false;
        try {
            int currentPhotoHash = ImageUtils.getPhotoHash(item.getClient(), item.getClientPhoto(),
                    ImageUtils.ImageSize.SMALL.getValue(), true);
            if(currentPhotoHash == item.getNewPhotoHash()) {
                Long nextVersion = DAOUtils.nextVersionByClientPhoto(session);
                ImageUtils.moveImage(item.getClient(), item.getClientPhoto());
                item.getClientPhoto().setIsNew(false);
                item.getClientPhoto().setIsCanceled(false);
                item.getClientPhoto().setIsApproved(true);
                item.getClientPhoto().setLastProceedError(null);
                item.getClientPhoto().setVersion(nextVersion);
                session.update(item.getClientPhoto());
                result = true;
                session.flush();
            } else {
                item.getClientPhoto().setLastProceedError("Фото-расхождение было изменено во время сверки");
                session.update(item.getClientPhoto());
            }
        } catch (Exception e){
            logger.error(e.getMessage());
            String error = "Не удалось принять фото-расхождение: " + e.getMessage();
            if(error.length() > 256){
                error = error.substring(0, 256);
            }
            item.getClientPhoto().setLastProceedError(error);
            session.update(item.getClientPhoto());
        }
        return result;
    }

    private boolean deleteNewPhoto(Session session, PhotoRegistryItem item){
        boolean result = false;
        try {
            int currentPhotoHash = ImageUtils.getPhotoHash(item.getClient(), item.getClientPhoto(),
                    ImageUtils.ImageSize.SMALL.getValue(), true);
            if (currentPhotoHash == item.getNewPhotoHash()) {
                boolean deleted = ImageUtils.deleteImage(item.getClient(), item.getClientPhoto(), true);
                if (!deleted) {
                    logger.error(String.format("Не удалось удалить фото-расхождение для клиента id=%s",
                            item.getClient().getIdOfClient()));
                    item.getClientPhoto().setLastProceedError("Не удалось удалить фото-расхождение");
                    session.update(item.getClientPhoto());
                } else {
                    item.getClientPhoto().setIsNew(false);
                    item.getClientPhoto().setIsCanceled(true);
                    item.getClientPhoto().setLastProceedError(null);
                    session.update(item.getClientPhoto());
                    result = true;
                }
            }
        } catch (Exception e){
            logger.error(e.getMessage());
            String error = "Не удалось удалить фото-расхождение: " + e.getMessage();
            if(error.length() > 256){
                error = error.substring(0, 256);
            }
            item.getClientPhoto().setLastProceedError(error);
            session.update(item.getClientPhoto());
        }
        return result;
    }

    public void doSelectAll() {
        for (PhotoRegistryItem i : items) {
            i.setSelected(true);
            i.setDenied(false);
        }
    }

    public void doDenyAll() {
        for (PhotoRegistryItem i : items) {
            i.setSelected(false);
            i.setDenied(true);
        }
    }

    public void doUnmarkAll() {
        for (PhotoRegistryItem i : items) {
            i.setSelected(false);
            i.setDenied(false);
        }
    }

    @Override
    public String getPageFilename() {
        return "service/photo_registry";
    }

    public String getPageDirectoryRoot() {
        return "/back-office/include";
    }

    public boolean getDisplayOrgSelection() {
        return true;
    }

    public boolean getShowErrorEditPanel () {
        return true;
    }

    public List<PhotoRegistryItem> getItems() {
        return items;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getConfirm() {
        return confirm;
    }

    public int getTotalCount() {
        return items == null ? 0 : items.size();
    }

    public class PhotoRegistryItem {
        private Long idOfOrg;
        private Long idOfClient;
        private Client client;
        private String fullName;
        private String guardianName;
        private String error;
        private String photoContentBase64;
        private int photoHash;
        private String newPhotoContentBase64;
        private int newPhotoHash;
        private boolean selected;
        private boolean denied;
        private ClientPhoto clientPhoto;

        public PhotoRegistryItem(ClientPhoto clientPhoto, Client client) {
            this.idOfOrg = client.getOrg().getIdOfOrg();
            this.idOfClient = client.getIdOfClient();
            this.client = client;
            this.fullName = client.getPerson().getFullName();
            Client guardian = clientPhoto.getGuardian();
            String guardianName = "";
            if(guardian != null){
                guardianName = guardian.getPerson().getFullName();
            }
            this.guardianName = guardianName;
            this.error = clientPhoto.getLastProceedError();
            try {
                ImageUtils.PhotoContent photoContent = ImageUtils.getPhotoContent(client, clientPhoto,
                        ImageUtils.ImageSize.SMALL.getValue(), false);
                this.photoContentBase64 = photoContent.getBase64();
                this.photoHash = photoContent.getHash();
            } catch (Exception e){
                logger.error(e.getMessage(), e);
            }
            try {
                ImageUtils.PhotoContent photoContent = ImageUtils.getPhotoContent(client, clientPhoto,
                        ImageUtils.ImageSize.SMALL.getValue(), true);
                this.newPhotoContentBase64 = photoContent.getBase64();
                this.newPhotoHash = photoContent.getHash();
            } catch (Exception e){
                logger.error(e.getMessage(), e);
            }
            this.selected = false;
            this.clientPhoto = clientPhoto;
        }

        public void onSelectedStatusChange(ValueChangeEvent event) {
            Boolean isChecked = (Boolean) event.getNewValue();
            if(isChecked) {
                selected = true;
                denied = false;
            } else {
                selected = false;
            }
        }

        public void onDeniedStatusChange(ValueChangeEvent event) {
            Boolean isChecked = (Boolean) event.getNewValue();
            if(isChecked) {
                selected = false;
                denied = true;
            } else {
                denied = false;
            }
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public Client getClient() {
            return client;
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getGuardianName() {
            return guardianName;
        }

        public void setGuardianName(String guardianName) {
            this.guardianName = guardianName;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getPhotoContentBase64() {
            return photoContentBase64;
        }

        public void setPhotoContentBase64(String photoContentBase64) {
            this.photoContentBase64 = photoContentBase64;
        }

        public int getPhotoHash() {
            return photoHash;
        }

        public void setPhotoHash(int photoHash) {
            this.photoHash = photoHash;
        }

        public String getNewPhotoContentBase64() {
            return newPhotoContentBase64;
        }

        public void setNewPhotoContentBase64(String newPhotoContentBase64) {
            this.newPhotoContentBase64 = newPhotoContentBase64;
        }

        public int getNewPhotoHash() {
            return newPhotoHash;
        }

        public void setNewPhotoHash(int newPhotoHash) {
            this.newPhotoHash = newPhotoHash;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isDenied() {
            return denied;
        }

        public void setDenied(boolean denied) {
            this.denied = denied;
        }

        public ClientPhoto getClientPhoto() {
            return clientPhoto;
        }

        public void setClientPhoto(ClientPhoto clientPhoto) {
            this.clientPhoto = clientPhoto;
        }
    }
}