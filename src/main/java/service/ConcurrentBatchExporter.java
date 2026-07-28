package service;

import exception.ReportExportException;
import model.Exportable;
import model.Grade;
import model.Student;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrentBatchExporter {

    private final GradeManager gradeManager;
    private final List<Exportable> exporters;

    public ConcurrentBatchExporter(GradeManager gradeManager, List<Exportable> exporters) {
        this.gradeManager = gradeManager;
        this.exporters = exporters;
    }

    public BatchExportResult exportAll(List<Student> students, int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<StudentExportOutcome>> futures = new java.util.ArrayList<>();

        for (Student student : students) {
            Future<StudentExportOutcome> future = executor.submit(() -> exportOneStudent(student));
            futures.add(future);
        }

        BatchExportResult result = new BatchExportResult();
        for (Future<StudentExportOutcome> future : futures) {
            try {
                StudentExportOutcome outcome = future.get();
                result.recordOutcome(outcome);
            } catch (Exception e) {
                result.recordFailure(e.getMessage());
            }
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        return result;
    }

    private StudentExportOutcome exportOneStudent(Student student) {
        List<Grade> grades = gradeManager.getGradesByStudent(student.getStudentId());
        String filename = student.getStudentId() + "_grades";

        for (Exportable exporter : exporters) {
            try {
                exporter.export(grades, filename);
            } catch (ReportExportException e) {
                return new StudentExportOutcome(student.getStudentId(), false, e.getMessage());
            }
        }

        return new StudentExportOutcome(student.getStudentId(), true, null);
    }
}