package com.ezo.assessment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ezo.assessment.calc.CalculatorSimple;

@Controller
public class CalcController {
	
	@GetMapping("/")
    public String index(Model model) {		            
          return "calc";
    }
	
	@PostMapping("/")
    public String listaLivros(@ModelAttribute("expression") String expression, Model model) {
		model.addAttribute("expression", expression);
		  try {
			  Double value = new CalculatorSimple().calculate(expression);
			  model.addAttribute("result", value);
		  } catch(Exception e) {
			  model.addAttribute("result", e.getMessage());
		  }          
          return "calc";
    }	
}
