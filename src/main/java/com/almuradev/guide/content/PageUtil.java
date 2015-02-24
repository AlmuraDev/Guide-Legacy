package com.almuradev.guide.content;

import com.almuradev.guide.Guide;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PageUtil {
    public static final Path PATH_CONFIG = Paths.get("config", Guide.MOD_ID);
    public static final Path PATH_PAGES = Paths.get(PATH_CONFIG.toString(), "pages");
    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");

    static {
        if (Files.notExists(PATH_PAGES)) {
            try {
                Files.createDirectories(PATH_PAGES);

            } catch (Exception e) {
                throw new RuntimeException("Failed to create pages directory!", e);
            }
        }
    }

    public static void loadPages(Path path, DirectoryStream.Filter<Path> filter) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter)) {
            for (Path p : stream) {
                final ConfigurationNode root = YAMLConfigurationLoader.builder().setFile(p.toFile()).build().load();
                try {
                    PageRegistry.putPage(createPage(p.getFileName().toString().replace(".yml", ""), root));
                } catch (ParseException e) {
                    Guide.LOGGER.warn("Failed parsing file [" + p.getFileName() + "] due to invalid date format. Should be MM/dd/yyyy");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed filtering page files from [" + path + "].", e);
        }
    }

    public static Page createPage(String identifier, ConfigurationNode root) throws ParseException {
        final String name = root.getChild("name").getString("No name");
        final Date created = DATE_FORMATTER.parse(root.getChild("created").getString("1/1/1900"));
        final String author = root.getChild("author").getString("Unknown");
        final Date lastModified = DATE_FORMATTER.parse(root.getChild("last-modified").getString("1/1/1900"));
        final String lastContributor = root.getChild("last-contributor").getString("Unknown");
        final String contents = root.getChild("contents").getString("");

        return new Page(identifier, name, created, author, lastModified, lastContributor, contents);
    }

    private PageUtil() {}
}
