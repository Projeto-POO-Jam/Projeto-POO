package com.projetopoo.jam.service;

import com.projetopoo.jam.config.rabbitmq.GameExtractRabbitMQConfig;
import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.repository.GameRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class GameWorkerService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private SseNotificationService sseNotificationService; // Opcional, para notificar o front-end

    @Transactional
    @RabbitListener(queues = GameExtractRabbitMQConfig.QUEUE_NAME)
    public void extractGame(Map<String, Object> messageBody) {
        Long gameId = ((Number) messageBody.get("gameId")).longValue();
        String gameToken = (String) messageBody.get("gameToken");

        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            if(game.getGameToken().equals(gameToken)) {

                String relativePath = game.getGameFile().replace("http://localhost:8080", "");
                File file = new File("src/main/resources/static" + relativePath);

                File destinationDir = file.getParentFile();

                try (ZipFile zipFile = new ZipFile(file)) {
                    zipFile.extractAll(destinationDir.getAbsolutePath());

                    int lastSlashIndex = relativePath.lastIndexOf('/');

                    String directoryRelativePath = relativePath.substring(0, lastSlashIndex);
                    String gameDirectoryUrl = "http://localhost:8080" + directoryRelativePath + "/index.html";
                    game.setGameFile(gameDirectoryUrl);
                    gameRepository.save(game);
                    System.out.println("Jogo " + gameId + " (ZIP) descompactado com sucesso usando zip4j.");

                } catch (IOException e) {
                    System.err.println("Erro ao descompactar o jogo (ZIP) " + gameId + ": " + e.getMessage());
                }
            }

        }

    }
}
