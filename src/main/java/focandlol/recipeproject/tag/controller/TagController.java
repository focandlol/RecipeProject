package focandlol.recipeproject.tag.controller;

import focandlol.recipeproject.tag.dto.TagDto;
import focandlol.recipeproject.tag.service.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  @PostMapping("/tag")
  public List<TagDto> addTag(@RequestBody List<String> names) {
    return tagService.add(names);
  }
}
