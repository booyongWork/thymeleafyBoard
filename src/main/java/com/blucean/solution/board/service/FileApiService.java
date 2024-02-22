// 게시판(Board)과 관련된 서비스(Service) 클래스
package com.blucean.solution.board.service;

import com.blucean.solution.board.dto.AttchDTO;
import com.blucean.solution.board.dto.AttchVO;
import com.blucean.solution.board.dto.BoardDTO;
import com.blucean.solution.board.dto.BoardVO;
import com.blucean.solution.board.mapper.BoardMapper;
import com.blucean.solution.board.mapper.FileApiMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * [Class 설명]
 *
 * @author 정부용
 * @since 2024.02.05
 * @version
 * @see
 *
 * <pre>
 *
 * << 개정이력(Modification information) >>
 *
 *   수정일     수정자      수정내용
 * -----------  ---------    ------------------------
 *
 *
 * </pre>
 */

@Service
@Slf4j
public class FileApiService {

    @Value("${blucean.file.board}")
    private String filePath;

    @Autowired
    private FileApiMapper fileApiMapper;

    public void apiFileInsert() {
        List<AttchDTO> fileDataAll = fileApiMapper.selectAllData();
        List<AttchDTO> otherdataList = fileApiMapper.otherSelectAllData();

        for (AttchDTO attchDTO : fileDataAll) {
            // 중복 체크
            int count = fileApiMapper.countAttachFileNm(attchDTO.getAttachFileNm());
            if (count == 0) { // 중복되지 않은 경우에만 삽입
                AttchDTO otherTableDTO = new AttchDTO();
                // 필드 복사
                otherTableDTO.setBbsSeq(attchDTO.getBbsSeq());
                otherTableDTO.setAttachFileSeq(attchDTO.getAttachFileSeq());
                otherTableDTO.setAttachFileNm(attchDTO.getAttachFileNm());
                otherTableDTO.setSaveFileNm(attchDTO.getSaveFileNm());
                otherTableDTO.setFilePath(attchDTO.getFilePath());
                otherTableDTO.setUseYn(attchDTO.getUseYn());
                otherTableDTO.setType(attchDTO.getType());

                fileApiMapper.insertOtherAttach(otherTableDTO);

                // 파일 저장
                String savePath = otherTableDTO.getFilePath()+ "/other"; // 저장 경로 생성
                File saveDir = new File(savePath);
                if (!saveDir.exists()) {
                    saveDir.mkdirs(); // 저장 폴더가 없으면 생성
                }
                String saveFilePath = savePath + File.separator + otherTableDTO.getSaveFileNm(); // 저장할 파일 경로 생성

                // 파일을 로컬에 저장하는 코드
                try (FileOutputStream outputStream = new FileOutputStream(saveFilePath)) {
                    // 파일 데이터를 outputStream에 쓰기
                    // 예시: outputStream.write(attchDTO.getFileData());
                } catch (IOException e) {
                    log.error("파일 저장 중 오류 발생: {}", e.getMessage());
                }
            } else { // 중복된 경우에 비교 후 업데이트 로직 수행
                boolean needToUpdate = true;
                for (AttchDTO otherDTO : otherdataList) {
                    if (otherDTO.getSaveFileNm().equals(attchDTO.getSaveFileNm()) &&
                            otherDTO.getFilePath().equals(attchDTO.getFilePath())) {
                        needToUpdate = false; // 동일한 파일명과 경로가 존재하면 업데이트 불필요
                        break;
                    }
                }
                if (needToUpdate) {
                    if (needToUpdate) {
                        // needToUpdate가 true일 때 로그 출력
                        log.info("업데이트 필요: attchDTO.attachFileNm={}, attchDTO.filePath={}", attchDTO.getSaveFileNm(), attchDTO.getFilePath());
                        // 업데이트 로직 수행
                        fileApiMapper.updateOtherAttach(attchDTO);
                    }
                }
            }

            for (AttchDTO otherDTO : otherdataList) {
                int count2 = fileApiMapper.countOtherAttachFileNm(otherDTO.getAttachFileNm());
                if (count2 == 0) {
                    this.apiFileDelete();
                }
            }
        }
    }

    public void apiFileDelete() {
        // 파일 삭제 로직 작성
        List<AttchDTO> otherdataList = fileApiMapper.otherSelectAllData();

        for (AttchDTO otherDTO : otherdataList) {
            // 파일명을 기준으로 해당 파일 정보를 삭제
            int count2 = fileApiMapper.countOtherAttachFileNm(otherDTO.getAttachFileNm());
            if (count2 == 0) {
                fileApiMapper.apiFileDelete(otherDTO.getAttachFileNm());
            }
            log.info("파일 정보 삭제 완료: {}", otherDTO.getAttachFileNm());
        }
    }
}
