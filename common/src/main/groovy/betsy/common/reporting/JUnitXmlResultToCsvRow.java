package betsy.common.reporting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JUnitXmlResultToCsvRow {

    /**
     * path to junit result xml file (READ)
     */
    private final Path xml;
    /**
     * path to resulting csv file (WRITE)
     */
    private final Path csv;

    public JUnitXmlResultToCsvRow(Path xml, Path csv) {
        this.xml = Objects.requireNonNull(xml);
        this.csv = Objects.requireNonNull(csv);
    }

    public void create() {
        // read
        List<CsvRow> rows = new JUnitXmlResultReader(xml).readRows();

        // sort
        Collections.sort(rows);

        // write
        writeRows(rows);
    }

    private void writeRows(final List<CsvRow> rows) {
        List<String> lines = rows.stream().map(CsvRow::toRow).collect(Collectors.toList());
        try {
            Files.write(csv, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not write file " + csv, e);
        }
    }
}
