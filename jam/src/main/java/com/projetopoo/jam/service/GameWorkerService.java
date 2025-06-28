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

/**
 * Classe worker do RabbitMQ para descompactar os jogos
 */
@Service
public class GameWorkerService {
    private final GameRepository gameRepository;

    @Autowired
    public GameWorkerService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    @RabbitListener(queues = GameExtractRabbitMQConfig.QUEUE_NAME)
    public void extractGame(Map<String, Object> messageBody) {
        // Pega informações passadas pela fila
        Long gameId = ((Number) messageBody.get("gameId")).longValue();
        String gameToken = (String) messageBody.get("gameToken");

        // Verifica se o jogo existe
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();

            // Verifica se o token recebido é igual ao armazenado no banco de dados
            if(game.getGameToken().equals(gameToken)) {

                // Pega o local do arquivo .zip
                String relativePath = game.getGameFile().replace("http://localhost:8080", "");
                File file = new File("src/main/resources/static" + relativePath);

                File destinationDir = file.getParentFile();

                // Descompacta o arquivo
                try (ZipFile zipFile = new ZipFile(file)) {
                    zipFile.extractAll(destinationDir.getAbsolutePath());

                    int lastSlashIndex = relativePath.lastIndexOf('/');

                    String directoryRelativePath = relativePath.substring(0, lastSlashIndex);

                    // Atualiza o local do arquivo no banco de dados para apontar para o index.html
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
