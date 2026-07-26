package model;

import java.util.List;

/**
 * A strategy for finding students matching some kind of query.
 *
 * Deliberately narrow - only the ONE operation genuinely shared by every
 * search strategy (StudentSearchService's string matching, and a future
 * RegexStudentSearchService's pattern matching) belongs here. Grade-range
 * or type-based filtering aren't shared concepts across every possible
 * search strategy, so they stay as extra methods on the concrete classes
 * that actually support them, rather than being forced onto this interface -
 * that's the Interface Segregation Principle applied directly, the same
 * principle this interface exists to demonstrate in the first place.
 */
public interface Searchable {

    /**
     * @param query the search query - meaning depends on the implementation
     *              (e.g. a partial name string, or a regex pattern)
     * @return every student matching the query, or an empty list if none match
     */
    List<Student> search(String query);
}