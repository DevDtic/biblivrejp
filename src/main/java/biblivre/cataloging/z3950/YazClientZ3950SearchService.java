package biblivre.cataloging.z3950;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class YazClientZ3950SearchService implements Z3950SearchService {
    private static final Logger logger = LoggerFactory.getLogger(YazClientZ3950SearchService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(45);
    private static final Map<String, Integer> BIB1_ATTRIBUTES =
            Map.of(
                    "title", 4,
                    "author", 1003,
                    "subject", 21,
                    "isbn", 7,
                    "issn", 8,
                    "any", 1016);

    @Override
    public Path search(Z3950AddressDTO address, String attribute, String query, int limit)
            throws Z3950SearchException {
        if (address == null) {
            throw new Z3950SearchException("cataloging.import.error.z3950_server_not_found");
        }

        Integer bib1Attribute = BIB1_ATTRIBUTES.get(attribute);

        if (bib1Attribute == null) {
            throw new Z3950SearchException("error.invalid_parameters");
        }

        try {
            Path marcFile = Files.createTempFile("biblivre-z3950-search", ".mrc");
            Path outputFile = Files.createTempFile("biblivre-z3950-output", ".txt");
            Process process =
                    new ProcessBuilder("yaz-client", "-m", marcFile.toString())
                            .redirectErrorStream(true)
                            .redirectOutput(outputFile.toFile())
                            .start();

            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(
                        commands(address, bib1Attribute, query, limit)
                                .getBytes(StandardCharsets.UTF_8));
            }

            boolean completed = process.waitFor(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                throw new Z3950SearchException("cataloging.import.error.z3950_timeout");
            }

            String output = Files.readString(outputFile, StandardCharsets.UTF_8);

            if (process.exitValue() != 0) {
                logger.warn("yaz-client returned {}: {}", process.exitValue(), output);
                throw new Z3950SearchException("cataloging.import.error.z3950_search");
            }

            if (Files.notExists(marcFile) || Files.size(marcFile) == 0) {
                logger.debug("Z39.50 search returned no MARC data. yaz-client output: {}", output);
            }

            return marcFile;
        } catch (IOException e) {
            throw new Z3950SearchException("cataloging.import.error.z3950_client_not_found", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Z3950SearchException("cataloging.import.error.z3950_search", e);
        }
    }

    private String commands(Z3950AddressDTO address, int bib1Attribute, String query, int limit) {
        StringBuilder builder = new StringBuilder();

        builder.append("open ").append(connectionString(address)).append('\n');
        builder.append("charset utf-8\n");
        builder.append("format usmarc\n");
        builder.append("find @attr 1=")
                .append(bib1Attribute)
                .append(' ')
                .append(yazQuote(query))
                .append('\n');
        builder.append("show 1+").append(Math.max(1, limit)).append('\n');
        builder.append("quit\n");

        return builder.toString();
    }

    private String connectionString(Z3950AddressDTO address) {
        StringBuilder builder = new StringBuilder();

        builder.append(address.getUrl()).append(':').append(address.getPort());

        if (StringUtils.isNotBlank(address.getCollection())) {
            builder.append('/').append(address.getCollection());
        }

        return builder.toString();
    }

    private String yazQuote(String value) {
        String sanitized =
                StringUtils.defaultString(value).replace("\\", "\\\\").replace("\"", "\\\"");

        return "\"%s\"".formatted(sanitized.toLowerCase(Locale.ROOT));
    }
}
