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

  /**
   * 자동완성
   * @param name 접두어
   * @return 완성 단어
   */
  @GetMapping("/autocomplete")
  public Map<String, Double> autoComplete(@RequestParam(required = false) String name) {
    return autoCompleteService.getAuto(name);
  }

}
