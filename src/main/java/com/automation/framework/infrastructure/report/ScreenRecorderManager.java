package com.automation.framework.infrastructure.report;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_MJPG;
import static org.monte.media.VideoFormatKeys.QualityKey;

/**
 * Grava a tela durante a execução de um cenário e salva o vídeo (.avi, MJPG)
 * na mesma pasta da evidência em PDF.
 *
 * Grava o que estiver visível no monitor principal: mantenha o navegador em primeiro plano.
 * Configurações (System properties):
 *   -Dvideo.recording=false  desliga a gravação
 *   -Dvideo.fps=5            quadros por segundo (padrão 5)
 *   -Dvideo.quality=0.6      qualidade JPEG de 0.1 a 1.0 (padrão 0.6)
 */
public class ScreenRecorderManager {

    private static final int FRAME_RATE = Integer.parseInt(System.getProperty("video.fps", "5"));
    private static final float QUALITY = Float.parseFloat(System.getProperty("video.quality", "0.6"));

    private ScreenRecorder recorder;

    public static boolean isEnabled() {
        return Boolean.parseBoolean(System.getProperty("video.recording", "true"))
                && !GraphicsEnvironment.isHeadless();
    }

    public void start(Path folder, String fileName) throws IOException, AWTException {
        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        Rectangle captureArea = configuration.getBounds();

        recorder = new NamedScreenRecorder(configuration, captureArea,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO,
                        EncodingKey, ENCODING_AVI_MJPG,
                        CompressorNameKey, ENCODING_AVI_MJPG,
                        DepthKey, 24,
                        FrameRateKey, Rational.valueOf(FRAME_RATE),
                        QualityKey, QUALITY,
                        KeyFrameIntervalKey, FRAME_RATE * 60),
                new Format(MediaTypeKey, MediaType.VIDEO,
                        EncodingKey, "black",
                        FrameRateKey, Rational.valueOf(FRAME_RATE)),
                null,
                folder.toFile(),
                fileName);
        recorder.start();
    }

    public Path stop() throws IOException {
        if (recorder == null) {
            return null;
        }
        recorder.stop();
        File file = recorder.getCreatedMovieFiles().isEmpty()
                ? null
                : recorder.getCreatedMovieFiles().get(0);
        recorder = null;
        return file == null ? null : file.toPath();
    }

    // Permite definir o nome do arquivo de vídeo (o padrão da biblioteca usa data/hora)
    private static class NamedScreenRecorder extends ScreenRecorder {

        private final String fileName;

        NamedScreenRecorder(GraphicsConfiguration configuration, Rectangle captureArea,
                            Format fileFormat, Format screenFormat, Format mouseFormat,
                            Format audioFormat, File movieFolder, String fileName)
                throws IOException, AWTException {
            super(configuration, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat,
                    movieFolder);
            this.fileName = fileName;
        }

        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists() && !movieFolder.mkdirs()) {
                throw new IOException("Could not create video folder: " + movieFolder);
            }
            return new File(movieFolder,
                    fileName + "." + Registry.getInstance().getExtension(fileFormat));
        }
    }
}
