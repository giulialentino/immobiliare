package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.FotoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class FotoService {

    @Autowired
    private FotoDao fotoDao;

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${app.upload-url}")
    private String uploadUrl;

    public List<String> getFotoByAnnuncio(Long idAnnuncio) throws SQLException {
        return fotoDao.findUrlsByAnnuncio(idAnnuncio);
    }

    public void eliminaFoto(Long idAnnuncio, String url) throws SQLException {
        fotoDao.deleteByUrl(idAnnuncio, url);
    }

    public String uploadFoto(Long idAnnuncio, MultipartFile file) throws SQLException, IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File vuoto");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File troppo grande. Massimo 5MB.");
        }

        List<String> fotoEsistenti = fotoDao.findUrlsByAnnuncio(idAnnuncio);
        if (fotoEsistenti.size() >= 10) {
            throw new IllegalStateException("Massimo 10 foto per annuncio");
        }

        String nomeOriginale = file.getOriginalFilename();
        if (nomeOriginale == null) throw new IllegalArgumentException("File non valido");

        String ext = nomeOriginale.toLowerCase().substring(nomeOriginale.lastIndexOf('.') + 1);
        if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png")) {
            throw new IllegalArgumentException("Formato non supportato. Usa JPG o PNG");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String nomeFile = UUID.randomUUID() + "." + ext;
        Path percorso = Paths.get(uploadDir, nomeFile);
        Files.write(percorso, file.getBytes());

        String url = uploadUrl + nomeFile;
        fotoDao.save(idAnnuncio, url);

        return url;
    }
}
