package com.leo.IA.Tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class LembreteTools {
    private List<String> lembretes = new ArrayList<>();

    @Tool("Usar para adicionar lembretes")
    public String adicionarLembretes(String lembrete){
        lembretes.add(lembrete);
        return "Lembrete adicionado: " + lembrete;
    }

    @Tool("Usar para listar todos os lembretes salvos")
    public String listarLembretes() {
        if (lembretes.isEmpty()) {
            return "Nenhum lembrete salvo ainda.";
        }
        return "Seus lembretes: " + String.join(", ", lembretes);
    }
}
