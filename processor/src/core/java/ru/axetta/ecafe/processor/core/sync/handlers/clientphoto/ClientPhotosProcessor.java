/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientphoto;

import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientPhoto;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 14:01
 */

public class ClientPhotosProcessor extends AbstractProcessor<ResClientPhotos>{
    private static final Logger logger = LoggerFactory.getLogger(ClientPhotosProcessor.class);
    private final ClientsPhotos clientPhotos;
    private final List<ResClientPhotosItem> resClientPhotosItems;

    public ClientPhotosProcessor(Session persistenceSession, ClientsPhotos clientPhotos) {
        super(persistenceSession);
        this.clientPhotos = clientPhotos;
        resClientPhotosItems = new ArrayList<ResClientPhotosItem>();
    }

    @Override
    public ResClientPhotos process() throws Exception {
        ResClientPhotos result = new ResClientPhotos();
        List<ResClientPhotosItem> items = new LinkedList<>();
        try {
            List<Long> clientIdsList = new LinkedList<>();
            for(ClientPhotosItem item : clientPhotos.getItems()) {
                clientIdsList.add(item.getIdOfClient());
            }

            List<Client> clientList = DAOUtils.findClients(session, clientIdsList);
            Map<Long, Client> clientMap = new HashMap<Long, Client>();
            for(Client client : clientList) {
                clientMap.put(client.getIdOfClient(), client);
            }

            List<ClientPhoto> clientPhotosList = ImageUtils.findClientPhotos(session, clientIdsList);
            Map<Long, ClientPhoto> clientPhotoMap = new HashMap<Long, ClientPhoto>();
            for(ClientPhoto clientPhoto : clientPhotosList) {
                clientPhotoMap.put(clientPhoto.getIdOfClient(), clientPhoto);
            }

            ResClientPhotosItem resItem;
            Long nextVersion = DAOUtils.nextVersionByClientPhoto(session);

            for(ClientPhotosItem item : clientPhotos.getItems()){
                if(item.getResCode().equals(ClientPhotosItem.ERROR_CODE_ALL_OK)) {
                    Client client = clientMap.get(item.getIdOfClient());
                    ClientPhoto clientPhoto = clientPhotoMap.get(client.getIdOfClient());
                    if(clientPhoto == null){
                        try {
                            String imageName = ImageUtils
                                    .saveImage(client.getContractId(), client.getIdOfClient(),
                                            ImageUtils.getImageFromString(item.getImage()), false);
                            clientPhoto = new ClientPhoto(client.getIdOfClient(), null, imageName, false);
                            clientPhoto.setIsApproved(true);
                            clientPhoto.setVersion(nextVersion);
                            session.save(clientPhoto);
                        } catch (IOException e) {
                            logger.error("Error saving ClientPhotos:", e);
                            item.setResCode(ClientPhotosItem.ERROR_CODE_PHOTO_NOT_SAVED);
                            item.setErrorMessage("Не удалось сохранить фото");
                        }
                    } else {
                        try {
                            ImageUtils.saveImage(client, clientPhoto,
                                    ImageUtils.getImageFromString(item.getImage()), false);
                            clientPhoto.setIsApproved(true);
                            clientPhoto.setVersion(nextVersion);
                            session.update(clientPhoto);
                        } catch (IOException e) {
                            logger.error("Error saving ClientPhotos:", e);
                            item.setResCode(ClientPhotosItem.ERROR_CODE_PHOTO_NOT_SAVED);
                            item.setErrorMessage("Не удалось сохранить фото");
                        }
                    }

                    if(item.getResCode().equals(ClientPhotosItem.ERROR_CODE_ALL_OK)){
                        resItem = new ResClientPhotosItem(clientPhoto);
                        resItem.setResCode(item.getResCode());
                    } else {
                        resItem = new ResClientPhotosItem();
                        resItem.setIdOfClient(item.getIdOfClient());
                        resItem.setResCode(item.getResCode());
                        resItem.setErrorMessage(item.getErrorMessage());
                    }
                } else {
                    resItem = new ResClientPhotosItem();
                    resItem.setIdOfClient(item.getIdOfClient());
                    resItem.setResCode(item.getResCode());
                    resItem.setErrorMessage(item.getErrorMessage());
                }
                items.add(resItem);
                session.flush();
            }
        } catch (Exception e) {
            logger.error("Error saving ClientPhotos:", e);
            return null;
        }
        result.setItems(items);

        return result;
    }

    public ClientPhotosData processData() throws Exception {
        ClientPhotosData result = new ClientPhotosData();
        List<ResClientPhotosItem> items = new ArrayList<ResClientPhotosItem>();
        ResClientPhotosItem resItem;
        List<ClientPhoto> list = DAOUtils.getClientPhotosForFriendlyOrgsSinceVersion(session,
                clientPhotos.getIdOfOrgOwner(), clientPhotos.getMaxVersion(), clientPhotos.getSyncPhotoCount());
        List<Long> clientsIds = new ArrayList<Long>();
        for(ClientPhoto clientPhoto : list) {
            clientsIds.add(clientPhoto.getIdOfClient());
        }
        List<Client> clientList = DAOUtils.findClients(session, clientsIds);
        Map<Long, Client> clientMap = new HashMap<Long, Client>();
        for(Client client : clientList) {
            clientMap.put(client.getIdOfClient(), client);
        }

        for(ClientPhoto cp : list){
            resItem = new ResClientPhotosItem(cp);
            resItem.setImageData(ImageUtils.getPhotoString(clientMap.get(cp.getIdOfClient()), cp,
                    ImageUtils.ImageSize.MEDIUM.getValue(), false));
            items.add(resItem);
        }

        result.setItems(items);
        return result;
    }


    public List<ResClientPhotosItem> getResClientPhotosItems() {
        return resClientPhotosItems;
    }
}
