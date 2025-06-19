package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.*;
import com.projetopoo.jam.model.*;
import com.projetopoo.jam.repository.JamRepository;
import com.projetopoo.jam.repository.SubscribeRepository;
import com.projetopoo.jam.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.format.DateTimeParseException;

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
    @Autowired
    private SubscribeRepository subscribeRepository;

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

        if (jam.getJamEndDate().isAfter(now)) {
            long endDelay = Duration.between(now, jam.getJamEndDate()).toMillis();
            rabbitMQProducerService.scheduleJamStatusUpdate(jam.getJamId(), endDelay, JamStatus.FINISHED.name());
        }

        JamSseDTO jamSseDTO = modelMapper.map(jam, JamSseDTO.class);
        jamSseDTO.setJamTotalSubscribers(0L);

        sseNotificationService.sendEventToTopic("jams-list-update", "jam-insert", jamSseDTO);
    }

    @Transactional
    public JamResponse findJam(Long jamId) {

        Optional<Jam> optionalJam = jamRepository.findByJamId(jamId);
        if (optionalJam.isEmpty()) {
            throw new EntityNotFoundException("Jam com o ID " + jamId + " não encontrado.");
        }
        JamResponse jamResponse = modelMapper.map(optionalJam.get(), JamResponse.class);
        jamResponse.setJamTotalSubscribers(subscribeRepository.countBySubscribeJam_JamId(jamResponse.getJamId()));
        return jamResponse;
    }

    @Transactional(readOnly = true)
    public JamPaginatedResponseDTO findJamsList(String yearMonth, int offset, int limit) {
        String[] parts = yearMonth.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de mês inválido. Use YYYY-MM.");
        }

        int year;
        int month;

        try {
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ano ou mês inválido.");
        }

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, "jamStartDate"));

        Page<Jam> jamPage = jamRepository.findByYearAndMonth(year, month, pageable);

        List<JamSummaryDTO> jamSummaryDTOList = jamPage.getContent().stream()
                .map(jam -> {
                    JamSummaryDTO jamSummaryDTO = modelMapper.map(jam, JamSummaryDTO.class);
                    jamSummaryDTO.setJamTotalSubscribers(subscribeRepository.countBySubscribeJam_JamId(jam.getJamId()));
                    return jamSummaryDTO;
                })
                .collect(Collectors.toList());

        return new JamPaginatedResponseDTO(jamSummaryDTOList, jamPage.getTotalElements());
    }

}
