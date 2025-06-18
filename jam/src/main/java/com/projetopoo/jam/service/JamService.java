package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.CommentResponseDTO;
import com.projetopoo.jam.dto.JamRequestDTO;
import com.projetopoo.jam.dto.JamSseDTO;
import com.projetopoo.jam.model.Comment;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.JamStatus;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.JamRepository;
import com.projetopoo.jam.repository.UserRepository;

import com.projetopoo.jam.util.ImageUtil;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JamService {

    @Autowired
    private JamRepository jamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SseNotificationService sseNotificationService;
    @Autowired
    private JamProducerService rabbitMQProducerService;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/upload/jam";

    @Transactional
    public void createJam(JamRequestDTO jamRequestDTO, String identifier) throws IOException {

        Jam jam = modelMapper.map(jamRequestDTO, Jam.class);

        jam.setJamUser(userRepository.findByIdentifier(identifier));

        String uuid = UUID.randomUUID().toString();
        String directoryCover = UPLOAD_DIRECTORY + "/" + uuid + "/cover";
        jam.setJamCover(ImageUtil.createImage(jamRequestDTO.getJamCover(), directoryCover, "/upload/jam/" + uuid + "/cover/"));

        String directoryWallpaper = UPLOAD_DIRECTORY + "/" + uuid + "/wallpaper";
        jam.setJamWallpaper(ImageUtil.createImage(jamRequestDTO.getJamWallpaper(), directoryWallpaper, "/upload/jam/" + uuid + "/wallpaper/"));

        String directoryBanner = UPLOAD_DIRECTORY + "/" + uuid + "/banner";
        jam.setJamBanner(ImageUtil.createImage(jamRequestDTO.getJamBanner(), directoryBanner, "/upload/jam/" + uuid + "/banner/"));

        jamRepository.save(jam);

        LocalDateTime now = LocalDateTime.now();

        if (jam.getJamStartDate().isAfter(now)) {
            long startDelay = Duration.between(now, jam.getJamStartDate()).toMillis();
            rabbitMQProducerService.scheduleJamStatusUpdate(jam.getJamId(), startDelay, JamStatus.ACTIVE.name());
        }

        // 2. Agenda a mensagem para quando a Jam deve ser FINALIZADA
        if (jam.getJamEndDate().isAfter(now)) {
            long endDelay = Duration.between(now, jam.getJamEndDate()).toMillis();
            rabbitMQProducerService.scheduleJamStatusUpdate(jam.getJamId(), endDelay, JamStatus.FINISHED.name());
        }


        JamSseDTO jamSseDTO = modelMapper.map(jam, JamSseDTO.class);

        sseNotificationService.sendEventToTopic("view-jams", "new-jam", jamSseDTO);
    }

}
