package server.utilities;
import common.models.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

public class FileManager {
    private final String fileName;


    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public void load(CollectionManager manager) throws IOException {
        File file = new File(this.fileName);
        if (!file.exists()) {
            manager.clear();
            return; // Start with empty collection
        }

        StringBuilder content = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                content.append((char) ch);
            }
        }
        manager.clear();
        String xml = content.toString();
        parseBands(xml, manager);

    }

    public void save(CollectionManager manager) throws IOException {
        Hashtable<String, MusicBand> collection = manager.getCollection();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<bands>\n");

        for (Map.Entry<String, MusicBand> entry : collection.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.getName(), b.getName())))
                .collect(Collectors.toList())) {
            String key = entry.getKey();
            MusicBand band = entry.getValue();

            xml.append("  <band key=\"").append(escapeXml(key)).append("\">\n");
            xml.append("    <id>").append(band.getId()).append("</id>\n");
            xml.append("    <name>").append(escapeXml(band.getName())).append("</name>\n");

            xml.append("    <coordinates>\n");
            xml.append("      <x>").append(band.getCoordinates().getX()).append("</x>\n");
            xml.append("      <y>").append(band.getCoordinates().getY()).append("</y>\n");
            xml.append("    </coordinates>\n");

            xml.append("    <creationDate>").append(band.getCreationDate().getTime()).append("</creationDate>\n");
            xml.append("    <numberOfParticipants>").append(band.getNumberOfParticipants()).append("</numberOfParticipants>\n");

            //  nullable fields
            if (band.getSinglesCount() != null) {
                xml.append("    <singlesCount>").append(band.getSinglesCount()).append("</singlesCount>\n");
            } else {
                xml.append("    <singlesCount></singlesCount>\n");
            }

            if (band.getEstablishmentDate() != null) {
                xml.append("    <establishmentDate>").append(band.getEstablishmentDate()).append("</establishmentDate>\n");
            } else {
                xml.append("    <establishmentDate></establishmentDate>\n");
            }

            if (band.getGenre() != null) {
                xml.append("    <genre>").append(band.getGenre()).append("</genre>\n");
            } else {
                xml.append("    <genre></genre>\n");
            }

            if (band.getStudio() != null && band.getStudio().getName() != null) {
                xml.append("    <studio>").append(escapeXml(band.getStudio().getName())).append("</studio>\n");
            } else {
                xml.append("    <studio></studio>\n");
            }

            xml.append("  </band>\n");
        }

        xml.append("</bands>");

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            bos.write(xml.toString().getBytes(StandardCharsets.UTF_8));
            bos.flush();
        }
    }

    private void parseBands(String xml, CollectionManager manager) {
        String[] bandChunks = xml.split("<band");
        long maxId = 0;

        for (String chunk : bandChunks) {
            if (!chunk.contains("</band>")) continue;

            try {

                String key = unescapeXml(extractAttribute(chunk, "key"));


                long id = Long.parseLong(extractTag(chunk, "id"));
                String name = unescapeXml(extractTag(chunk, "name"));


                String coordsXml = extractTag(chunk, "coordinates");
                double x = Double.parseDouble(extractTag(coordsXml, "x"));
                int y = Integer.parseInt(extractTag(coordsXml, "y"));


                long creationDateMillis = Long.parseLong(extractTag(chunk, "creationDate"));
                Long participants = Long.parseLong(extractTag(chunk, "numberOfParticipants"));

                // Handle nullable fields
                String singlesStr = extractTag(chunk, "singlesCount");
                Integer singles = singlesStr.isEmpty() ? null : Integer.parseInt(singlesStr);

                String estStr = extractTag(chunk, "establishmentDate");
                LocalDate estDate = estStr.isEmpty() ? null : LocalDate.parse(estStr);

                String genreStr = extractTag(chunk, "genre");
                MusicGenre genre = genreStr.isEmpty() ? null : MusicGenre.valueOf(genreStr);

                String studioStr = unescapeXml(extractTag(chunk, "studio"));
                Studio studio = studioStr.isEmpty() ? null : new Studio(studioStr);

                // Create objects and add to collection
                Coordinates coords = new Coordinates(x, y);
                MusicBand band = new MusicBand(name, coords, participants, singles,
                        estDate, genre, studio);
                band.setId(id); // Override auto-generated ID
                band.setCreationDate(new Date(creationDateMillis));
                manager.add(key, band);

                if (id > maxId) {
                    maxId = id;
                }

            } catch (Exception e) {
                System.err.println("Error parsing band: " + e.getMessage());
            }
        }

        MusicBand.setNextId(maxId);
    }

    private String extractTag(String xml, String tagName) {
        String openTag = "<" + tagName + ">";
        String closeTag = "</" + tagName + ">";

        int start = xml.indexOf(openTag);
        if (start == -1) return "";

        start += openTag.length();
        int end = xml.indexOf(closeTag, start);
        if (end == -1) return "";

        return xml.substring(start, end);
    }

    private String extractAttribute(String xml, String attritbuteName) {
        String search = attritbuteName + "=\"";

        int start = xml.indexOf(search);

        if (start == -1) return "";
        start += search.length();

        int end = xml.indexOf("\"", start);
        if (end == -1) return "";

        return xml.substring(start, end);

    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String unescapeXml(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value
                .replace("&apos;", "'")
                .replace("&quot;", "\"")
                .replace("&gt;", ">")
                .replace("&lt;", "<")
                .replace("&amp;", "&");
    }
}

