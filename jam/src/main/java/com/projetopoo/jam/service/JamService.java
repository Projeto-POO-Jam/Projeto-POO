package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.jam.*;
import com.projetopoo.jam.model.*;
import com.projetopoo.jam.repository.JamRepository;
import com.projetopoo.jam.repository.SubscribeRepository;
import com.projetopoo.jam.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.projetopoo.jam.util.FileUtil;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe service para classe Jam, responsável pela lógica de negócios
 */
@Service
public class JamService {
    private final JamRepository jamRepository;
    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final ModelMapper modelMapper;
    private final SseNotificationService sseNotificationService;
    private final JamProducerService rabbitMQProducerService;
    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/upload/jam";

    /**
     * Constrói uma nova instância de JamService com suas dependências
     * @param jamRepository Repository para comunicação com o banco de dados da classe Jam
     * @param userRepository Repository para comunicação com o banco de dados da classe User
     * @param subscribeRepository Repository para comunicação com o banco de dados da classe Subscribe
     * @param modelMapper Classe para mapear transformações entre models e DTOs
     * @param sseNotificationService Classe para envio de eventos via SSE
     * @param rabbitMQProducerService Classe para agendar jams na fila do RabbitMQ
     */
    @Autowired
    public JamService(JamRepository jamRepository,
                      UserRepository userRepository,
                      SubscribeRepository subscribeRepository,
                      ModelMapper modelMapper,
                      SseNotificationService sseNotificationService,
                      JamProducerService rabbitMQProducerService) {
        this.jamRepository = jamRepository;
        this.userRepository = userRepository;
        this.subscribeRepository = subscribeRepository;
        this.modelMapper = modelMapper;
        this.sseNotificationService = sseNotificationService;
        this.rabbitMQProducerService = rabbitMQProducerService;
    }

    /**
     * Função para criar uma nova jam
     * @param jamRequestDTO Informações sobre a jam
     * @param identifier Identificador do usuário
     * @throws IOException Pode gerar exceção no caso de erro ao salvar alguma imagem
     */
    @Transactional
    public void createJam(JamRequestDTO jamRequestDTO, String identifier) throws IOException {

        // Passa as informações recebidas para o formato de Jam
        Jam jam = modelMapper.map(jamRequestDTO, Jam.class);

        // Coloca o valor do jamId como null para evitar conflito no banco de dados
        jam.setJamId(null);

        // Busca usuário que fez a requisição
        jam.setJamUser(userRepository.findByIdentifier(identifier));

        // Grava as imagens
        String uuid = UUID.randomUUID().toString();
        String directoryCover = UPLOAD_DIRECTORY + "/" + uuid + "/cover";
        jam.setJamCover(FileUtil.createFile(jamRequestDTO.getJamCover(), directoryCover, "/upload/jam/" + uuid + "/cover/"));

        String directoryWallpaper = UPLOAD_DIRECTORY + "/" + uuid + "/wallpaper";
        jam.setJamWallpaper(FileUtil.createFile(jamRequestDTO.getJamWallpaper(), directoryWallpaper, "/upload/jam/" + uuid + "/wallpaper/"));

        String directoryBanner = UPLOAD_DIRECTORY + "/" + uuid + "/banner";
        jam.setJamBanner(FileUtil.createFile(jamRequestDTO.getJamBanner(), directoryBanner, "/upload/jam/" + uuid + "/banner/"));

        // Gera um token de versão para o worker do RabbitMQ
        jam.setJamToken(UUID.randomUUID().toString());

        // Salva a jam
        jamRepository.save(jam);

        // Agenda as mudanças de status da jam
        scheduleJamStatusUpdate(jam);

        // Passa a jam para o formato de resposta do SSE
        JamSseDTO jamSseDTO = modelMapper.map(jam, JamSseDTO.class);
        jamSseDTO.setJamTotalSubscribers(0L);

        // Envia um evento SSE avisando que uma nova jam foi criada
        sseNotificationService.sendEventToTopic("jams-list-update", "jam-insert", jamSseDTO);
    }

    /**
     * Função para atualizar as informações sobre uma jam
     * @param jamUpdateRequestDTO Novas informações sobre a jam
     * @param identifier Identificador do usuário
     * @throws IOException Pode gerar exceção no caso de erro ao salvar alguma imagem
     */
    @Transactional
    public void updateJam(JamUpdateRequestDTO jamUpdateRequestDTO, String identifier) throws IOException {

        // Verifica se a jam existe
        Optional<Jam> optionalJam = jamRepository.findByJamId(jamUpdateRequestDTO.getJamId());
        if (optionalJam.isPresent()) {
            Jam existingJam = optionalJam.get();

            // Busca usuário que fez a requisição
            User user = userRepository.findByIdentifier(identifier);

            // Verifica se o usuário que fez a requisição é o dono da jam
            if(user.equals(existingJam.getJamUser())) {

                //Verifica se as imagens mudaram e atualiza elas caso necessário
                if (jamUpdateRequestDTO.getJamCover() != null && !jamUpdateRequestDTO.getJamCover().isEmpty()) {
                    String oldPhotoPath = existingJam.getJamCover();

                    String uuid = UUID.randomUUID().toString();
                    String directoryCover = UPLOAD_DIRECTORY + "/" + uuid + "/cover";
                    existingJam.setJamCover(FileUtil.createFile(jamUpdateRequestDTO.getJamCover(), directoryCover, "/upload/jam/" + uuid + "/cover/"));

                    FileUtil.deleteFile(oldPhotoPath);
                    jamUpdateRequestDTO.setJamCover(null);
                }

                if (jamUpdateRequestDTO.getJamWallpaper() != null && !jamUpdateRequestDTO.getJamWallpaper().isEmpty()) {
                    String oldPhotoPath = existingJam.getJamWallpaper();

                    String uuid = UUID.randomUUID().toString();
                    String directoryCover = UPLOAD_DIRECTORY + "/" + uuid + "/wallpaper";
                    existingJam.setJamWallpaper(FileUtil.createFile(jamUpdateRequestDTO.getJamWallpaper(), directoryCover, "/upload/jam/" + uuid + "/wallpaper/"));

                    FileUtil.deleteFile(oldPhotoPath);
                    jamUpdateRequestDTO.setJamWallpaper(null);
                }

                if (jamUpdateRequestDTO.getJamBanner() != null && !jamUpdateRequestDTO.getJamBanner().isEmpty()) {
                    String oldPhotoPath = existingJam.getJamBanner();

                    String uuid = UUID.randomUUID().toString();
                    String directoryCover = UPLOAD_DIRECTORY + "/" + uuid + "/banner";
                    existingJam.setJamBanner(FileUtil.createFile(jamUpdateRequestDTO.getJamBanner(), directoryCover, "/upload/jam/" + uuid + "/banner/"));

                    FileUtil.deleteFile(oldPhotoPath);
                    jamUpdateRequestDTO.setJamBanner(null);
                }

                // Verifica se houve atualização na data e hora de início e fim e se é necessário mudar o status atual
                boolean updateDate = false;
                LocalDateTime now = LocalDateTime.now();

                if(jamUpdateRequestDTO.getJamStartDate() != null && !jamUpdateRequestDTO.getJamStartDate().isEqual(existingJam.getJamStartDate())) {
                    updateDate = true;
                    if(jamUpdateRequestDTO.getJamStartDate().isAfter(now)){
                        existingJam.setJamStatus(JamStatus.SCHEDULED);
                    }
                }

                if(jamUpdateRequestDTO.getJamEndDate() != null && !jamUpdateRequestDTO.getJamEndDate().isEqual(existingJam.getJamEndDate())) {
                    updateDate = true;
                    if(jamUpdateRequestDTO.getJamEndDate().isAfter(now)){
                        if(jamUpdateRequestDTO.getJamStartDate().isBefore(now)){
                            existingJam.setJamStatus(JamStatus.ACTIVE);
                        } else {
                            existingJam.setJamStatus(JamStatus.SCHEDULED);
                        }

                    }
                }

                // Se for atualizada alguma data, um novo token é gerado
                if(updateDate) {
                    existingJam.setJamToken(UUID.randomUUID().toString());
                }

                // Passa as informações recebidas para o formato de Jam
                modelMapper.map(jamUpdateRequestDTO, existingJam);
                jamRepository.save(existingJam);

                // Se for atualizada alguma data, reagenda as mudanças de status da jam
                if(updateDate) {
                    scheduleJamStatusUpdate(existingJam);
                }
            } else {
                throw new AccessDeniedException("Usuário não autorizado a alterar a jam.");
            }
        } else {
            throw new EntityNotFoundException("Jam com o ID " + jamUpdateRequestDTO.getJamId() + " não encontrada.");
        }
    }

    /**
     * Função para buscar uma jam pelo id
     * @param jamId Id da jam que está sendo usado na consulta
     * @param identifier Identificador do usuário
     * @return Informações sobre a jam
     */
    @Transactional
    public JamResponse findJam(Long jamId, String identifier) {

        // Verifica se a jam existe
        Optional<Jam> optionalJam = jamRepository.findByJamId(jamId);
        if (optionalJam.isEmpty()) {
            throw new EntityNotFoundException("Jam com o ID " + jamId + " não encontrado.");
        }

        // Passa Jam para o formato de resposta
        JamResponse jamResponse = modelMapper.map(optionalJam.get(), JamResponse.class);

        // Busca o total de inscritos
        jamResponse.setJamTotalSubscribers(subscribeRepository.countBySubscribeJam_JamId(jamResponse.getJamId()));

        // Busca usuário que fez a requisição
        User user = userRepository.findByIdentifier(identifier);

        // Verifica se o usuário é dono da jam
        jamResponse.getJamUser().setUserCurrent(user.getUserId().equals(jamResponse.getJamUser().getUserId()));

        return jamResponse;
    }

    /**
     * Função para buscar todas as jams de um mês de forma paginada
     * @param yearMonth Mês e ano que está sendo usado na consulta
     * @param offset Offset para a busca
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações das jams, com o total de jams que podem ser buscados
     */
    @Transactional(readOnly = true)
    public JamPaginatedResponseDTO findJamsList(String yearMonth, int offset, int limit) {
        // Lê a data
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

        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Busca a lista paginada de jams
        Page<Jam> jamPage = jamRepository.findByYearAndMonth(year, month, pageable);

        // Adiciona a informação do total de inscritos em cada jam
        return getJamPaginatedResponseDTO(jamPage);
    }

    /**
     * Função para buscar as jams com maior número de inscritos.
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações das jams
     */
    @Transactional(readOnly = true)
    public JamPaginatedResponseDTO findJamsBanner(int limit) {
        // Define qual é a pagina de interesse
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Verifica se a jam existe
        List<JamStatus> statuses = Arrays.asList(JamStatus.ACTIVE, JamStatus.SCHEDULED);
        Page<Jam> jamPage = jamRepository.findTopJamsByJamStatus(statuses, pageable);

        return getJamPaginatedResponseDTO(jamPage);
    }

    /**
     * Função para buscar as jams de um usuário
     * @param userId Id do usuário que está sendo usado na consulta
     * @param offset Offset para a busca
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações das jams
     */
    @Transactional
    public JamPaginatedResponseDTO findJamListByUserId(Long userId, int offset, int limit){
        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Busca a lista paginada de jams
        Page<Jam> jamPage = jamRepository.findByUserIdOrderByJamId(userId,pageable);

        return getJamPaginatedResponseDTO(jamPage);
    }

    /**
     * Função para buscar as jams que um usuário participou
     * @param userId Id do usuário que está sendo usado na consulta
     * @param offset Offset para a busca
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações das jams
     */
    @Transactional
    public JamPaginatedResponseDTO findMyJamListByUserId(Long userId, int offset, int limit){
        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Busca a lista paginada de jams
        Page<Jam> jamPage = jamRepository.findByUserIdOrderByJamSubscribes(userId,pageable);

        return getJamPaginatedResponseDTO(jamPage);
    }

    /**
     * Função para agendar jams para mudança de status
     * @param jam Limite de itens a serem retornados
     * @return Lista com informações das jams
     */
    private void scheduleJamStatusUpdate(Jam jam){
        LocalDateTime now = LocalDateTime.now();

        // Verifica se a data de ínicio é antes da data atual
        if (jam.getJamStartDate().isAfter(now)) {
            long startDelay = Duration.between(now, jam.getJamStartDate()).toMillis();
            rabbitMQProducerService.scheduleJamStatusUpdate(jam.getJamId(), startDelay, JamStatus.ACTIVE.name(), jam.getJamToken());
        }

        // Verifica se a data de fim é depois da data atual
        if (jam.getJamEndDate().isAfter(now)) {
            long endDelay = Duration.between(now, jam.getJamEndDate()).toMillis();
            rabbitMQProducerService.scheduleJamStatusUpdate(jam.getJamId(), endDelay, JamStatus.FINISHED.name(), jam.getJamToken());
        }
    }

    /**
     * Função para adicionar o total de inscritos em cada jam
     * @param jamPage Lista paginada de jams
     * @return Lista com informações das jam, com o total de jams que podem ser buscados e o total de inscrições
     */
    private JamPaginatedResponseDTO getJamPaginatedResponseDTO(Page<Jam> jamPage) {
        // Passa a lista paginada para o formato da resposta
        List<JamSummaryDTO> jamSummaryDTOList = jamPage.getContent().stream()
                .map(jam -> {
                    JamSummaryDTO jamSummaryDTO = modelMapper.map(jam, JamSummaryDTO.class);

                    //Adiciona o total de inscrições
                    jamSummaryDTO.setJamTotalSubscribers(subscribeRepository.countBySubscribeJam_JamId(jam.getJamId()));
                    return jamSummaryDTO;
                })
                .collect(Collectors.toList());

        return new JamPaginatedResponseDTO(jamSummaryDTOList, jamPage.getTotalElements());
    }

}
