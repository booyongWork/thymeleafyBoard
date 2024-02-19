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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
        //todo 이제 여기에 같이 값이 존재하는지 로직 만들어서 update 로직 만들기


        List<AttchDTO> fileDataAll = fileApiMapper.selectAllData();
        // 각 AttchDTO를 다른 테이블에 맞게 변환하여 삽입

        for (AttchDTO attchDTO : fileDataAll) {
            AttchDTO otherTableDTO = new AttchDTO();
            // 필드 복사
            otherTableDTO.setBbsSeq(attchDTO.getBbsSeq());
            otherTableDTO.setAttachFileSeq(attchDTO.getAttachFileSeq());
            otherTableDTO.setAttachFileNm(attchDTO.getAttachFileNm());
            otherTableDTO.setSaveFileNm(attchDTO.getSaveFileNm());
            otherTableDTO.setFilePath(attchDTO.getFilePath());
            otherTableDTO.setUseYn(attchDTO.getUseYn());
            otherTableDTO.setType(attchDTO.getType());

            // 다른 테이블에 삽입
            fileApiMapper.insertBoardFileList(otherTableDTO);
        }
    }
}
