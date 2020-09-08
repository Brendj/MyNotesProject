package ru.iteco.meshsync.mesh.service.DAO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.client.model.EducationForm;
import ru.iteco.meshsync.models.TrainingForm;
import ru.iteco.meshsync.repo.TrainingFormRepo;

@Service
public class CatalogService {
    private final TrainingFormRepo trainingFormRepo;

    public CatalogService(TrainingFormRepo trainingFormRepo){
        this.trainingFormRepo = trainingFormRepo;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public boolean isHomeStudy(EducationForm educationForm, Integer educationFormId) throws Exception {
        if(educationForm == null && educationFormId == null){
            throw new IllegalArgumentException("Arguments educationForm and educationFormId is NULL");
        }
        Integer id = educationForm == null ? educationFormId : educationForm.getId();

        TrainingForm form = trainingFormRepo.getByIdAndArchiveIsFalse(id);
        if(form == null){
            throw new Exception(String.format("TrainingForm by ID %d does exists", id));
        }
        return form.getEducationForm().contains("Вне");
    }
}
