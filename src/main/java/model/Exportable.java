package model;

import exception.ReportExportException;

import java.nio.file.Path;
import java.util.List;

/**
 * A format-specific way of exporting grade data to disk.
 *
 * This is one of the three interfaces the Lab 2 brief originally asked
 * for (Searchable, Exportable, Calculable) that were deliberately skipped
 * back then, since each concern only had one implementer at the time -
 * an interface with a single implementer adds indirection with no real
 * benefit (a YAGNI call). Here, Exportable genuinely earns its keep:
 * CSV, JSON, and Binary exporters will each implement it, and other code
 * (a batch exporter, a menu) can hold a List<Exportable> and call
 * export() on each one without caring which specific format it is.
 */
public interface Exportable {

    /**
     * Writes the given grades to disk in this exporter's specific format.
     *
     * @param grades   the grades to export
     * @param filename the target filename, without extension
     * @return the path the file was actually written to
     * @throws ReportExportException if writing fails for any reason
     */
    Path export(List<Grade> grades, String filename) throws ReportExportException;

    /**
     * @return a short label identifying this format, e.g. "CSV", "JSON", "Binary"
     */
    String getFormatName();
}