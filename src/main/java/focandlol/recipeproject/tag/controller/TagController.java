package focandlol.recipeproject.tag.controller;

import focandlol.recipeproject.tag.dto.CreateTagDto;
import focandlol.recipeproject.tag.dto.TagDto;
import focandlol.recipeproject.tag.dto.UpdateTagDto;
import focandlol.recipeproject.tag.service.TagService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  @PostMapping("/tag")
  public List<TagDto> addTag(@RequestBody @Valid CreateTagDto createTagDto) {
    return tagService.add(createTagDto.getTags());
  }

  @DeleteMapping("/tag")
  public void deleteTag(@RequestBody List<String> tags) {
    tagService.delete(tags);
  }

  @PutMapping("/tag")
  public void updateTag(@RequestBody @Valid UpdateTagDto updateTagDto) {
    tagService.update(updateTagDto.getTag(), updateTagDto.getChange());
  }

  @GetMapping("/tag")
  public Page<TagDto> getTags(Pageable pageable) {
    return tagService.getTags(pageable);
  }
}
