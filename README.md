# Jarvis — Personal AI Assistant

> Assistente pessoal de IA desenvolvido do zero com arquitetura multi-agente, suporte multimodal e guardrails de segurança.

---

## Visão Geral

O Jarvis é um assistente pessoal end-to-end construído com Java, Spring Boot e LangChain4j no backend, e Next.js no frontend. O projeto foi desenvolvido para explorar na prática conceitos avançados de IA: RAG, multi-agents, prompt engineering, guardrails e processamento multimodal (texto, imagem, PDF e áudio).

---

## Funcionalidades

- **Chat com memória** — conversa contínua com histórico via `MessageWindowChatMemory`
- **RAG (Retrieval-Augmented Generation)** — respostas baseadas em documentos pessoais (`notas.txt`, `safra.txt`, `sobre_mim.txt`)
- **Multimodal** — suporte a texto, imagem, PDF e áudio na mesma interface
- **Transcrição de áudio** — via Whisper (Groq), com gravação direta no navegador via MediaRecorder API
- **Multi-agents com orquestrador** — roteamento automático entre agentes especializados (web search, clima, RAG, lembretes)
- **Guardrails** — detecção e bloqueio de prompt injection no input
- **Automação de vagas** — busca diária de vagas e envio automático por email via `@Scheduled`
- **Toggle dark/light mode** — tema escuro (azulado) e claro (areia de Copacabana)

---

## Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                     Frontend                        │
│          Next.js + TypeScript + Tailwind            │
│   Orbe animado · Dark/Light · Upload multimodal     │
└───────────────────────┬─────────────────────────────┘
                        │ HTTP REST
┌───────────────────────▼─────────────────────────────┐
│                     Backend                         │
│              Java + Spring Boot                     │
│                                                     │
│  ┌─────────────┐    ┌──────────────────────────┐   │
│  │  Controller │───▶│       IAService           │   │
│  └─────────────┘    │  - validarInput()         │   │
│                     │  - perguntaIA()           │   │
│                     │  - perguntaComImagem()    │   │
│                     │  - perguntaComPdf()       │   │
│                     │  - perguntaComAudio()     │   │
│                     └────────────┬─────────────┘   │
│                                  │                  │
│                     ┌────────────▼─────────────┐   │
│                     │       Orquestrador        │   │
│                     │  ┌─────────────────────┐ │   │
│                     │  │   AgenteWeb (Tavily) │ │   │
│                     │  │   AgenteClima       │ │   │
│                     │  │   AgenteRAG         │ │   │
│                     │  │   AgenteLembretes   │ │   │
│                     │  └─────────────────────┘ │   │
│                     └──────────────────────────┘   │
└─────────────────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
   Groq API         Groq Whisper    Gmail SMTP
  (LLM Chat)      (Transcrição)   (Email Tools)
```

---

## Stack Técnica

**Backend**
- Java 24 + Spring Boot 4
- LangChain4j 0.36.2
- Apache PDFBox 3.0.3
- Spring Mail (JavaMailSender)

**Frontend**
- Next.js (App Router) + TypeScript
- Tailwind CSS
- MediaRecorder API (gravação de áudio nativa)

**IA & APIs**
- Groq API (LLM + Whisper)
- Tavily (web search)
- OpenWeatherMap (clima)

---

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/pergunta` | Chat com texto (multi-agent + RAG + guardrail) |
| POST | `/pergunta-imagem` | Análise de imagem (base64 + mimeType) |
| POST | `/pergunta-pdf` | Leitura e análise de PDF (base64) |
| POST | `/pergunta-audio` | Transcrição + resposta (base64) |
| POST | `/testar-vagas` | Dispara busca e envio de vagas por email |

---

## Como Rodar

### Pré-requisitos
- Java 24+
- Node.js 18+
- Conta Groq (API key gratuita em [console.groq.com](https://console.groq.com))
- Conta Tavily (API key gratuita em [app.tavily.com](https://app.tavily.com))

### Backend

```bash
# Clone o repositório
git clone https://github.com/leonardoferreiraads/Jarvis.git
cd Jarvis

# Configure as variáveis no application.properties
groq.api.key=SUA_CHAVE_GROQ
tavily.api.key=SUA_CHAVE_TAVILY
spring.mail.username=seu@gmail.com
spring.mail.password=sua_senha_app

# Rode o projeto
./mvnw spring-boot:run
# Backend disponível em http://localhost:8082
```

### Frontend

```bash
cd jarvis-frontend

# Configure a URL do backend
echo "NEXT_PUBLIC_API_URL=http://localhost:8082" > .env.local

# Instale as dependências e rode
npm install
npm run dev
# Frontend disponível em http://localhost:3000
```

---

## Segurança

O Jarvis implementa um **input guardrail** que detecta e bloqueia tentativas de prompt injection antes de qualquer chamada ao LLM, incluindo padrões como `ignore suas instruções`, `act as`, `forget everything`, entre outros.

---

## Autor

**Leonardo Ferreira**
- AI Engineer Estagiário — Banco Safra (Renda Variável)
- Estudante de ADS — FIAP São Paulo (conclusão DEZ/2026)
- [LinkedIn](https://www.linkedin.com/in/leonardoferreiraads) · [GitHub](https://github.com/leonardoferreiraads)
