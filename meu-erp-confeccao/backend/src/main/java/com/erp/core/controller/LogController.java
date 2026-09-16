package com.erp.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/admin/logs")
public class LogController {

    private static final String LOG_FILE_PATH = "./logs/application.json";

    @GetMapping
    public ResponseEntity<List<String>> getLogs(@RequestParam(defaultValue = "100") int lines) {
        Path path = Paths.get(LOG_FILE_PATH);
        if (!Files.exists(path)) {
            return ResponseEntity.ok(Collections.singletonList("{\"message\": \"Arquivo de log não encontrado.\"}"));
        }

        try (Stream<String> stream = Files.lines(path)) {
            // Read all lines, get the last N lines, and reverse them to show newest first
            List<String> allLines = stream.collect(Collectors.toList());
            int start = Math.max(0, allLines.size() - lines);
            List<String> tail = new ArrayList<>(allLines.subList(start, allLines.size()));
            Collections.reverse(tail);
            return ResponseEntity.ok(tail);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonList("{\"message\": \"Erro ao ler arquivo de log.\"}"));
        }
    }
}
