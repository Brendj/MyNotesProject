package ru.axetta.ecafe.processor.beans.authentication.provider;

import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.AppConfigurationEntry;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component("processingJaasAuthenticationProvider")
public class ProcessingJaasAuthenticationProvider extends DefaultJaasAuthenticationProvider {
    @PostConstruct
    public void init(){
        Map<String, AppConfigurationEntry[]> mappedConfigurations = new HashMap<>();
        AppConfigurationEntry entry = new AppConfigurationEntry("ru.axetta.ecafe.processor.web.login.ProcessingLoginModule",
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, new HashMap<>());
        mappedConfigurations.put("SPRINGSECURITY", new AppConfigurationEntry[]{entry});

        InMemoryConfiguration inMemoryConfiguration = new InMemoryConfiguration(mappedConfigurations);
        this.setConfiguration(inMemoryConfiguration);

        this.setAuthorityGranters(new AuthorityGranter[] {new ProcessingRoleGranter()});
    }

    public static class ProcessingRoleGranter implements AuthorityGranter {

        @Override
        public Set<String> grant(Principal principal) {
            if (principal.getName().equals("admin"))
                return Collections.singleton("ADMIN");
            else
                return Collections.singleton("CUSTOMER");
        }
    }
}
