package ru.axetta.ecafe.processor.core.proactive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.proactive.service.PersonBenefitCategoryService;

@Configuration
@Service
public class BenefitSchedulerConfig {

    private final PersonBenefitCategoryService personBenefitCategoryService;

    public BenefitSchedulerConfig(PersonBenefitCategoryService personBenefitCategoryService) {
        this.personBenefitCategoryService = personBenefitCategoryService;
    }

    @Scheduled(cron = "0 0 01 * * *")
    public void checkEndDateForBenefitCategory() {
        personBenefitCategoryService.checkEndDateForBenefitCategory();
    }
}
