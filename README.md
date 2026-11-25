# Deep Research Agent

A completed research aggregation and summarization application with a Java/Spring Boot backend and React frontend. It combines multiple external sources, filters relevant results and supports AI-assisted summaries and saved research reports.

## Features

- Topic-based research across news, academic and forum sources.
- Configurable query types, time ranges and source selection.
- Parallel source requests and aggregated research results.
- AI-assisted summarization, with provider configuration required.
- MySQL persistence for saved research reports.

Response time and source coverage depend on the configured providers, network access and query. The application does not promise exhaustive coverage of every topic.

## Stack and structure

Java 17, Spring Boot 3.5, Spring Data JPA/Hibernate, MySQL and React/TypeScript.

- `DeepResearchAgent/`: Maven backend project.
- `DeepResearchAgent/src/main/java/`: controllers, models and research services.
- `DeepResearchAgent/frontend/`: React frontend.

## Local setup

1. Install Java 17, MySQL and Node.js/npm.
2. Create a local MySQL database and configure `spring.datasource.url`, `spring.datasource.username` and `spring.datasource.password` using Spring Boot external configuration or local environment settings.
3. Configure the source providers you intend to use. The source configuration includes News API, SerpAPI, Reddit, arXiv, OpenAlex and GNews, plus `openai.api.key` and `openai.api.url` for AI summarization. Use your own credentials; do not publish them.
4. Start the backend from `DeepResearchAgent/`:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   On macOS/Linux, use `./mvnw spring-boot:run`.

5. In another terminal, start the frontend:

   ```sh
   cd DeepResearchAgent/frontend
   npm install
   npm start
   ```

The controllers allow the frontend at `http://localhost:3000`. The API port follows `server.port` in your Spring Boot configuration.

## API routes

| Method | Route | Purpose |
| --- | --- | --- |
| GET | `/api/research?topic=...` | Research a topic |
| POST | `/api/research/reports/save` | Save a report |
| GET | `/api/research/reports/saved` | List saved reports |
| DELETE | `/api/research/reports/{reportId}` | Delete a saved report |
| POST | `/api/dynamic/research` | Run a configured research query |
| GET | `/api/dynamic/sources` | List source choices |
| GET | `/api/dynamic/query-types` | List query types |
| GET | `/api/dynamic/time-ranges` | List time ranges |
| GET | `/api/summary?topic=...` | Generate a summary |

## Portfolio

[Priyanshu Sharma](https://priyanshu-sharma.tech/) - Java backend development, projects and contributions.
