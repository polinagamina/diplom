package bg.webaudioportal.app.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.Files;

@RestController
@RequestMapping("/makeNoise")
public class MakeNoiseController {
    private static String UPLOAD_FOLDER = "Uploads/";
    private static String NOISE_FOLDER = "Noise/";
    @RequestMapping("/noise")
    public void makeNoise(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) throws Exception {
        String path = UPLOAD_FOLDER + fileName +".wav";
        File musicFile = new File(path);
        byte[] bytes = Files.readAllBytes(musicFile.toPath());
        byte[] bytesWithNoise = new byte[bytes.length];
        for (int i=0; i<bytes.length; i++){
            bytesWithNoise[i]= (byte) (bytes[i]+getNoise());
        }
        writeAudioToWavFile(bytesWithNoise,new AudioFormat(44100, 16, 2, true, true),NOISE_FOLDER+fileName+".wav");
        File noiseFile = new File(NOISE_FOLDER+fileName+".wav");
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName + "\""));
        response.setContentLength((int) noiseFile.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(noiseFile));
        FileCopyUtils.copy(inputStream, response.getOutputStream());

    }

    public double getNoise(){
        java.util.Random r = new java.util.Random();
        double noise = r.nextGaussian() * Math.sqrt(0.1);
        return noise;
    }

    public static void writeAudioToWavFile(byte[] data, AudioFormat format, String fn) throws Exception {
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(fn));
    }
}
