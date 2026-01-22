package com.adyen.workshop.controllers.views;

import com.adyen.workshop.configurations.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ViewController {

    private final ApplicationConfiguration applicationConfiguration;

    public ViewController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    // The main entry point that will be shown, see resources/static/templates/index.html
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("clientKey", applicationConfiguration.getAdyenClientKey());
        return "index";
    }
}
