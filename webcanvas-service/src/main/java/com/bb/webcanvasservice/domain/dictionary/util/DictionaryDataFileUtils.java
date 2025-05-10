package com.bb.webcanvasservice.domain.dictionary.util;

import com.bb.webcanvasservice.domain.dictionary.exception.DictionaryFileDownloadFailedException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 사전 파일 처리에 대한 유틸 클래스
 */
@Slf4j
public class DictionaryDataFileUtils {

    /**
     * 로컬 경로에 미리 저장해둔 경로 리턴
     * @return
     */
    public static Path getLocalFilesDirectoryPath() {
        Path classpathRoot = null;
        Path parentOfProjectRoot = null;
        try {
            classpathRoot = Paths.get(ClassLoader.getSystemResource("").toURI());
            parentOfProjectRoot = classpathRoot.getParent().getParent().getParent().getParent().getParent();
        } catch (URISyntaxException e) {
            log.error("파일 경로를 찾지 못했습니다.");
            throw new DictionaryFileDownloadFailedException();
        }
        return parentOfProjectRoot.resolve(Paths.get("words-json"));
    }

    /**
     * 파일을 URL을 이용해 타겟 savePath에 파일을 다운로드한다.
     * @param fileURL
     */
    public Path downloadFile(String fileURL) {
        try {
            Path tempFilePath = Files.createTempFile("words_", ".zip");
            URL url = new URL(fileURL);
            try (InputStream in = url.openStream()) {
                Files.createDirectories(tempFilePath.getParent()); // 디렉터리 생성
                Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return tempFilePath;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DictionaryFileDownloadFailedException();
        }
    }

}
