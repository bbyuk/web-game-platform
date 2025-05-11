package com.bb.webcanvasservice.domain.dictionary.util;

import com.bb.webcanvasservice.domain.dictionary.exception.DictionaryFileDownloadFailedException;
import com.bb.webcanvasservice.domain.dictionary.parser.DictionaryParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

        return parentOfProjectRoot.resolve(Paths.get("words-json", System.getProperty("dictionary.source.directory")));
    }

    /**
     * 파일을 URL을 이용해 타겟 savePath에 파일을 다운로드한다.
     * @param fileURL
     */
    public static Path downloadFile(String fileURL) {
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

    /**
     * 주어진 압축파일이 있는 경로에 압축파일명으로 디렉터리를 만들어 압축을 해제한다.
     * @param zipFilePath
     * @return
     */
    public static Path unzipFile(Path zipFilePath) {
        // zip 파일 이름 (확장자 제외)로 디렉터리 생성
        String zipFileName = zipFilePath.getFileName().toString();
        String folderName = zipFileName.replaceFirst("[.][^.]+$", ""); // 확장자 제거
        Path destinationDir = zipFilePath.getParent().resolve(folderName).toAbsolutePath().normalize();

        // 디렉터리 생성
        try {
            Files.createDirectories(destinationDir);
        }
        catch(IOException e) {
            log.error(e.getMessage(), e);
            throw new DictionaryFileDownloadFailedException();
        }

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;

            while ((entry = zipIn.getNextEntry()) != null) {
                Path resolvedPath = destinationDir.resolve(entry.getName()).normalize();

                // zip slip 보안 체크
                if (!resolvedPath.startsWith(destinationDir)) {
                    log.error("zip slip 보안 체크 실패");
                    throw new DictionaryFileDownloadFailedException("압축을 해제하는 과정에서 문제가 발생했습니다.");
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    try (OutputStream out = Files.newOutputStream(resolvedPath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zipIn.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        }
        catch(IOException e) {
            log.error(e.getMessage(), e);
            throw new DictionaryFileDownloadFailedException();
        }

        return destinationDir;
    }

}
