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

  /**
   * 사용자가 레시피 등록, ai레시피 생성 시 사용한 태그가 자동 저장되도록 바꿨으므로
   * 태그 추가 deprecated
   */
//  @PostMapping("/tag")
//  public List<TagDto> addTag(@RequestBody @Valid CreateTagDto createTagDto) {
//    return tagService.add(createTagDto.getTags());
//  }

  /**
   * 태그 삭제
   * @param tags : 삭제할 태그명 리스트
   */
  @DeleteMapping("/tag")
  public void deleteTag(@RequestBody List<String> tags) {
    tagService.delete(tags);
  }

  /**
   * 태그 수정
   * @param updateTagDto 원래 태그명, 수정할 태그명
   */
  @PutMapping("/tag")
  public void updateTag(@RequestBody @Valid UpdateTagDto updateTagDto) {
    tagService.update(updateTagDto.getTag(), updateTagDto.getChange());
  }

  /**
   * 태그 조회
   * @param pageable
   * @return
   */
  @GetMapping("/tag")
  public Page<TagDto> getTags(Pageable pageable) {
    return tagService.getTags(pageable);
  }
}
