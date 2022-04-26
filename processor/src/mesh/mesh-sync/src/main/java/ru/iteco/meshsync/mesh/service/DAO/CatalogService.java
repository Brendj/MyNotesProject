package ru.iteco.meshsync.mesh.service.DAO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.client.model.EducationForm;
import ru.iteco.client.model.PersonEducation;
import ru.iteco.meshsync.models.TrainingForm;
import ru.iteco.meshsync.repo.TrainingFormRepo;

import java.util.Arrays;
import java.util.List;

@Service
public class CatalogService {
    private final TrainingFormRepo trainingFormRepo;
    private final List<Integer> formsNotInOrgs = Arrays.asList(4,5,6,7);

    public CatalogService(TrainingFormRepo trainingFormRepo){
        this.trainingFormRepo = trainingFormRepo;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public boolean educationFormIsHomeStudy(PersonEducation actualEdu) {
        EducationForm educationForm = actualEdu.getEducationForm();
        if(educationForm == null){
            throw new IllegalArgumentException("Arguments educationForm is NULL");
        }

        TrainingForm form = trainingFormRepo.getByIdAndArchiveIsFalse(educationForm.getId());
        if(form == null){
            return false;
        }

        return formsNotInOrgs.contains(form.getId());
    }
}
