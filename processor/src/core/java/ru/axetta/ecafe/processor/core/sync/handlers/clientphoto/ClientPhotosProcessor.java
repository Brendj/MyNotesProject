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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        List<ResClientPhotosItem> items = new ArrayList<ResClientPhotosItem>();
        try {
            ResClientPhotosItem resItem;
            for(ClientPhotosItem item : clientPhotos.getItems()){
                Long nextVersion = DAOUtils.nextVersionByClientPhoto(session);
                if(item.getResCode().equals(ClientPhotosItem.ERROR_CODE_ALL_OK)){
                    Client client = (Client) session.load(Client.class, item.getIdOfClient());
                    ClientPhoto clientPhoto = client.getPhoto();
                    if(clientPhoto == null){
                        try {
                            String imageName = ImageUtils
                                    .saveImage(client.getContractId(), client.getIdOfClient(), item.getImageData(), false);
                            clientPhoto = new ClientPhoto(client, null, imageName, false);
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
                            ImageUtils.saveImage(client, item.getImageData(), false);
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
                clientPhotos.getIdOfOrgOwner(), clientPhotos.getMaxVersion());
        Collections.sort(list, new Comparator<ClientPhoto>() {

            public int compare(ClientPhoto cp1, ClientPhoto cp2) {
                return cp1.getVersion().compareTo(cp2.getVersion());
            }
        });
        int count = 1;
        for(ClientPhoto cp : list){
            if(count > clientPhotos.getSyncPhotoCount()){
                break;
            }
            resItem = new ResClientPhotosItem(cp);
            resItem.setImageData(ImageUtils.getPhotoString(cp.getClient(), ImageUtils.ImageSize.MEDIUM.getValue(), false));
            items.add(resItem);
            count++;
        }

        result.setItems(items);
        return result;
    }


    public List<ResClientPhotosItem> getResClientPhotosItems() {
        return resClientPhotosItems;
    }
}
