package com.codegym.demo.controllers;

import com.codegym.demo.dto.response.OpenWeatherResponse;
import com.codegym.demo.services.OpenWeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final OpenWeatherService openWeatherService;

    public HomeController(OpenWeatherService openWeatherService, RestTemplate restTemplate) {
        this.openWeatherService = openWeatherService;
    }

    @GetMapping
    public String showHomePage(Model model) {
        // Return the name of the view for the home page
        OpenWeatherResponse ow = openWeatherService.getWeatherCurrent("Hanoi");
        double temp = Math.ceil(ow.getMain().getTemp() - 273);
        model.addAttribute("temp", temp);
        return "index"; // This will resolve to /WEB-INF/views/home.jsp
    }
}
