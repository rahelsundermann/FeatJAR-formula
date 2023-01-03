package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Maps;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.formula.analysis.IAssignment;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.bool.BooleanAssignment;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.io.value.ValueAssignmentFormat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Assigns values of any type to string-identified {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to represent a set of equalities for use in an SMT {@link ISolver}.
 *
 * @author Elias Kuiter
 */
public class ValueAssignment implements IAssignment<String>, IValueRepresentation {
    final LinkedHashMap<String, Object> variableValuePairs;

    public ValueAssignment() {
        this(Maps.empty());
    }

    public ValueAssignment(LinkedHashMap<String, Object> variableValuePairs) {
        this.variableValuePairs = variableValuePairs;
    }

    public ValueAssignment(Object... variableValuePairs) {
        this.variableValuePairs = Maps.empty();
        if (variableValuePairs.length % 2 == 1)
            throw new IllegalArgumentException("expected a list of variable-value pairs for this value assignment");
        for (int i = 0; i < variableValuePairs.length; i += 2) {
            this.variableValuePairs.put((String) variableValuePairs[i], variableValuePairs[i + 1]);
        }
    }

    public ValueAssignment(ValueAssignment valueAssignment) {
        this(new LinkedHashMap<>(valueAssignment.variableValuePairs));
    }

    @Override
    public ValueAssignment toAssignment() {
        return new ValueAssignment(variableValuePairs);
    }

    @Override
    public ValueClause toClause() {
        return new ValueClause(variableValuePairs);
    }

    @Override
    public ValueSolution toSolution() {
        return new ValueSolution(variableValuePairs);
    }

    @Override
    public Result<? extends BooleanAssignment> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<? extends BooleanAssignment> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<? extends BooleanAssignment>) IValueRepresentation.super.toBoolean(variableMap);
    }

    @Override
    public LinkedHashSet<String> getVariableNames() {
        return new LinkedHashSet<>(variableValuePairs.keySet());
    }

    @Override
    public LinkedHashMap<String, Object> getAll() {
        return variableValuePairs;
    }

    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    protected ValueAssignment clone() {
        return toAssignment();
    }

    public String print() {
        try {
            return IO.print(this, new ValueAssignmentFormat());
        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("ValueAssignment[%s]", print());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueAssignment that = (ValueAssignment) o;
        return Objects.equals(variableValuePairs, that.variableValuePairs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableValuePairs);
    }
}