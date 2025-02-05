package focandlol.recipeproject.autocomplete.controller;

import focandlol.recipeproject.autocomplete.service.AutoCompleteService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AutoCompleteController {

  private final AutoCompleteService autoCompleteService;

  @GetMapping("/autocomplete")
  public Map<String, Double> autoComplete(@RequestParam(required = false) String name) {
    return autoCompleteService.getAuto(name);
  }

}
