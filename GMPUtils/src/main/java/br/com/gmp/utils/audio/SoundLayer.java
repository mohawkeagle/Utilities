package br.com.gmp.utils.audio;

import br.com.gmp.utils.audio.file.AudioFile;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.JavaLayerException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1Tag;

/**
 * Reprodutor de audio
 *
 * @author kaciano
 * @version 1.0
 */
public class SoundLayer implements Runnable {

    private final Logger LOGGER = Logger.getLogger(SoundLayer.class.getName());
    private AudioFile audioFile;
    private String filePath;
    private GAudioPlayer player;
    private Thread playerThread;
    private String namePlayerThread = "AudioPlayerThread";
    private PlaybackListener playbackListener = new PlaybackListener();
    private ID3v1Tag tag;
    private final String breakLine = "\n---------------------------------------------------";

    /**
     * Cria nova instancia de SoundLayer
     *
     * @param filePath {@code String} Caminho do arquivo
     * @throws java.io.IOException Exceção de Java I/O
     */
    public SoundLayer(String filePath) throws IOException {
        this.filePath = filePath;
        playerInitialize();
        loadTag();
    }

    /**
     * Cria nova instancia de SoundLayer
     *
     * @param audioFile {@code String} Caminho do arquivo
     * @throws java.io.IOException Exceção de Java I/O
     */
    public SoundLayer(AudioFile audioFile) throws IOException {
        this.audioFile = audioFile;
        this.filePath = audioFile.getFile().getPath();
        this.tag = audioFile.getTag();
        playerInitialize();
        printData();
    }

    /**
     * * Cria nova instancia de SoundLayer
     *
     * @param filePath {@code String} Caminho do arquivo
     * @param namePlayerThread {@code String} Nome da Thread do Player
     * @throws java.io.IOException Exceção de Java I/O
     */
    public SoundLayer(String filePath, String namePlayerThread) throws IOException {
        this.filePath = filePath;
        this.namePlayerThread = namePlayerThread;
        playerInitialize();
        loadTag();
    }

    /**
     * Carrega os dados da faixa
     */
    private void loadTag() {
        try {
            this.tag = new MP3File(new File(filePath)).getID3v1Tag();
        } catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
            Logger.getLogger(SoundLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        printData();
    }

    /**
     * Imprime dados
     */
    private void printData() {
        System.out.println(breakLine
                + "\n TITLE:\t" + tag.getFirst(FieldKey.TITLE)
                + "\n ARTIST:\t" + tag.getFirst(FieldKey.ARTIST)
                + "\n ALBUM:\t" + tag.getFirst(FieldKey.ALBUM)
                + "\n TRACK:\t" + tag.getFirst(FieldKey.TRACK)
                + "\n LENGTH:\t" + tag.getFirst(FieldKey.TEMPO)
                + breakLine);
    }

    /**
     * Retorna o titutlo da faixa
     *
     * @return {@code String} Titulo da faixa
     */
    public String getTitle() {
        return tag.getFirst(FieldKey.TITLE);
    }

    /**
     * Retorna o artista da faixa
     *
     * @return {@code String} Artista da faixa
     */
    public String getArtist() {
        return tag.getFirst(FieldKey.ARTIST);
    }

    /**
     * Retorna o numero da faixa
     *
     * @return {@code String} Numero da faixa
     */
    public String getTrack() {
        return tag.getFirst(FieldKey.TRACK);
    }

    /**
     * Retorna o da album faixa
     *
     * @return {@code String} Album da faixa
     */
    public String getAlbum() {
        return tag.getFirst(FieldKey.ALBUM);
    }

    /**
     * Inicia reprodução
     *
     * @throws javazoom.jl.decoder.BitstreamException BitstreamException
     * @throws java.io.IOException Exceção de Java I/O
     */
    public void play() throws BitstreamException, IOException, JavaLayerException {
        if (this.player == null) {
            this.playerInitialize();
        } else if (this.player.isPaused()) {
            this.pauseToggle();
            this.playerInitialize();
        } else if (!this.player.isPaused() || this.player.isComplete()
                || this.player.isStopped()) {
            this.stop();
            this.playerInitialize();
        }
        if (playerThread != null && playerThread.isAlive()) {
            playerThread.stop();
            playerThread.destroy();
            playerThread = null;
        }
        this.playerThread = new Thread(this, namePlayerThread);
        this.playerThread.setDaemon(true);
        this.playerThread.start();
    }

    /**
     * Pausa reprodução
     *
     * @throws javazoom.jl.decoder.BitstreamException BitstreamException
     */
    public void pause() throws BitstreamException {
        if (this.player != null) {
            this.player.pause();
            if (this.playerThread != null) {
                this.playerThread = null;
            }
        }
    }

    /**
     * Continua reprodução
     *
     * @throws javazoom.jl.decoder.BitstreamException BitstreamException
     * @throws java.io.IOException Exceção de Java I/O
     */
    public void resume() throws BitstreamException, JavaLayerException, IOException {
        if (this.player != null) {
            if (this.player.isPaused() && !this.player.isStopped()) {
                this.player.resume();
                this.playerInitialize();
            }
        }
    }

    /**
     * Troca o status de pausa
     *
     * @throws javazoom.jl.decoder.BitstreamException BitstreamException
     * @throws java.io.IOException Exceção de Java I/O
     */
    public void pauseToggle() throws BitstreamException, IOException, JavaLayerException {
        if (this.player != null) {
            if (this.player.isPaused() && !this.player.isStopped()) {
                this.resume();
            } else {
                this.pause();
            }
        }
    }

    /**
     * Para reprodução
     *
     * @throws javazoom.jl.decoder.BitstreamException BitstreamException
     */
    public void stop() throws BitstreamException {
        if (this.player != null) {
            this.player.stop();
            if (this.playerThread != null) {
                this.playerThread = null;
            }
        }
    }

    /**
     * Inicializa o player
     */
    private void playerInitialize() throws IOException {
        try {
            this.player = new GAudioPlayer(this.filePath);
            this.player.setPlaybackListener(this.playbackListener);
        } catch (JavaLayerException e) {
            Logger.getLogger(SoundLayer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void run() {
        try {
            this.player.resume();
        } catch (javazoom.jl.decoder.JavaLayerException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Listener privado para reprodução
     */
    private static class PlaybackListener extends GAudioPlayer.PlaybackAdapter {

        @Override
        public void playbackStarted(GAudioPlayer.PlaybackEvent playbackEvent) {
            System.err.println("PlaybackStarted()");
        }

        @Override
        public void playbackPaused(GAudioPlayer.PlaybackEvent playbackEvent) {
            System.err.println("PlaybackPaused()");
        }

        @Override
        public void playbackFinished(GAudioPlayer.PlaybackEvent playbackEvent) {
            System.err.println("PlaybackStopped()");
        }
    }

    /**
     * Dados da faixa de audio
     *
     * @return {@code ID3v1Tag} Dados da faixa
     */
    public ID3v1Tag getTag() {
        return tag;
    }

    /**
     * Retorna o player de audio
     *
     * @return {@code GAudioPlayer} Player de audio
     */
    public GAudioPlayer getPlayer() {
        return player;
    }

}
