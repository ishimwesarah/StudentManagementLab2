package model;

import java.util.List;

/**
 * A strategy for reducing a list of some data type down to a single
 * summary number.
 *
 * This is the third and last of the interfaces the Lab 2 brief originally
 * requested (Searchable, Exportable, Calculable). Unlike the other two,
 * this project's four existing calculators (GradeAverageCalculator,
 * StudentAverageCalculator, GPACalculator, ClassStatisticsCalculator)
 * don't share an identical method shape - they take different kinds of
 * lists and compute different things. Generics solve this: <T> is a
 * placeholder type filled in by whoever implements this interface, so
 * one single interface definition can correctly describe "reduce a
 * List<Grade> to a number" AND "reduce a List<Student> to a number"
 * without duplicating the interface or giving up compile-time type safety.
 *
 * @param <T> the type of data this calculator reduces to a single number
 */
public interface Calculable<T> {

    /**
     * @param data the list of items to summarize
     * @return a single number summarizing the given data
     */
    double calculate(List<T> data);
}