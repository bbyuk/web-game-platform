package com.bb.webcanvasservice.domain.dictionary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Dictionary 관련 기능 API
 * 메소드별 권한관리 필요 - Dictionary 업데이트는 관리자만 수행할 수 있어야한다.
 *
 * TODO ADMIN 계정으로만 접근 가능하도록 권한관리 필요
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("dictionary")
@Tag(name = "Dictionary API", description = "랜덤 제시어 생성 및 랜덤 닉네임 생성 관련 API")
public class DictionaryController {

    private final DictionaryService dictionaryService;
    private final DictionaryBatchService dictionaryBatchService;

    @PostMapping("word/{fileIndex}")
    @Operation(summary = "파일 파싱 및 적용", description = "단일 파일을 파싱해 word 테이블에 저장한다.")
    public ResponseEntity<Integer> applyFile(@PathVariable("fileIndex") int fileIndex) {
        return ResponseEntity.ok(dictionaryService.parseFileAndSaveWithIndex(fileIndex));
    }

    @PostMapping("batch/word")
    @Operation(summary = "전체 파일 파싱 및 적용 배치 수행", description = "전체 파일을 순회하며 word 테이블에 저장하는 비동기 배치 실행")
    public ResponseEntity<Void> applyAllFileBatch() {
        dictionaryBatchService.batchInsertWordData();
        return ResponseEntity.ok(null);
    }


}
