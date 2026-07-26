package service;

import model.Searchable;
import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A second, genuinely different implementer of Searchable - matches
 * student names and emails against a regular expression pattern, rather
 * than the simple substring matching StudentSearchService uses.
 *
 * This is the concrete proof that Searchable earns its keep: other code
 * can hold a Searchable reference and call search(query) without caring
 * or knowing whether it's talking to this class or StudentSearchService.
 */
public class RegexStudentSearchService implements Searchable {

    private final StudentManager studentManager;

    public RegexStudentSearchService(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    /**
     * Matches the given regex pattern against each student's name AND
     * email - a student matches if either field matches the pattern.
     *
     * @param query a regular expression, e.g. "^STU00[1-3]$" or ".*@university\\.edu$"
     * @return every student whose name or email matches the pattern
     * @throws IllegalArgumentException if the query isn't a valid regex pattern
     */
    @Override
    public List<Student> search(String query) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid search pattern: " + e.getMessage());
        }

        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (pattern.matcher(s.getName()).find() || pattern.matcher(s.getEmail()).find()) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Convenience method matching only against student ID, using the
     * standard STU### format the rest of the system generates.
     *
     * @return every student whose ID matches the given pattern
     */
    public List<Student> searchByIdPattern(String idPattern) {
        Pattern pattern = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (pattern.matcher(s.getStudentId()).find()) {
                result.add(s);
            }
        }
        return result;
    }
}