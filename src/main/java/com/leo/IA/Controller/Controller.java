package com.leo.IA.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leo.IA.DTO.AudioDTO;
import com.leo.IA.DTO.ImagemDTO;
import com.leo.IA.DTO.PdfDTO;
import com.leo.IA.Service.IAService;
import com.leo.IA.Tools.VagaTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
public class Controller {
    @Autowired
    IAService service;
    @Autowired
    private VagaTools vagaTools;
    @PostMapping("/pergunta")
    public ResponseEntity<String> pergunta(@RequestBody String pergunta) {
        try {
            return ResponseEntity.ok(service.perguntaIA(pergunta));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/pergunta-imagem")
    public String perguntaComImagem(@RequestBody ImagemDTO imagem){
         return service.perguntaComImagem(imagem);
    }

    @PostMapping("/pergunta-pdf")
        public String perguntaComPdf(@RequestBody PdfDTO pdf) throws IOException {
        return service.perguntaComPdf(pdf);
    }
    @PostMapping("/pergunta-audio")
        public String perguntaComAudio(@RequestBody AudioDTO audioDTO) throws JsonProcessingException {
        return service.perguntaComAudio(audioDTO);
    }
    @PostMapping("/testar-vagas")
    public String testarVagas() {
        return vagaTools.buscarEEnviarVagas();
    }
}
