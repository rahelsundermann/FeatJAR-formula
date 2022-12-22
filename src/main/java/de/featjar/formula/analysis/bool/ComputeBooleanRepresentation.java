/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.analysis.bool;

import de.featjar.base.Feat;
import de.featjar.base.computation.*;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.Traversable;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.*;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.predicate.Literal;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Transforms a formula, which is assumed to be in conjunctive normal form, into an indexed CNF representation.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class ComputeBooleanRepresentation<T extends ValueRepresentation, U extends BooleanRepresentation>
        extends Computation<Pair<U, VariableMap>> implements Analysis<T, Pair<U, VariableMap>> {
    protected final static Dependency<?> VALUE_REPRESENTATION = newDependency();

    public ComputeBooleanRepresentation(Computable<T> valueRepresentation) {
        dependOn(VALUE_REPRESENTATION);
        setInput(valueRepresentation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Dependency<T> getInputDependency() {
        return (Dependency<T>) VALUE_REPRESENTATION;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FutureResult<Pair<U, VariableMap>> compute() {
        return getInput().get().thenComputeResult(((t, monitor) -> {
            Feat.log().debug("initializing variable map for " + t.getClass().getName());
            VariableMap variableMap = VariableMap.of(t);
            Feat.log().debug(variableMap);
            return t.toBoolean(variableMap).map(u -> new Pair<>((U) u, variableMap));
        }));
    }

    public static class OfAssignment extends ComputeBooleanRepresentation<ValueAssignment, BooleanAssignment> {
        public OfAssignment(Computable<ValueAssignment> valueRepresentation) {
            super(valueRepresentation);
        }

        @Override
        public Traversable<Computable<?>> cloneNode() {
            return new OfAssignment(getInput());
        }
    }

    public static class OfClause extends ComputeBooleanRepresentation<ValueClause, BooleanClause> {
        public OfClause(Computable<ValueClause> valueRepresentation) {
            super(valueRepresentation);
        }

        @Override
        public Traversable<Computable<?>> cloneNode() {
            return new OfClause(getInput());
        }
    }

    public static class OfSolution extends ComputeBooleanRepresentation<ValueSolution, BooleanSolution> {
        public OfSolution(Computable<ValueSolution> valueRepresentation) {
            super(valueRepresentation);
        }

        @Override
        public Traversable<Computable<?>> cloneNode() {
            return new OfSolution(getInput());
        }
    }

    public static class OfFormula extends ComputeBooleanRepresentation<Formula, BooleanClauseList> { // todo: assumption: is in CNF
        public OfFormula(Computable<Formula> valueRepresentation) {
            super(valueRepresentation);
        }

        public static Result<BooleanClauseList> toBooleanClauseList(Formula formula, VariableMap variableMap) {
            final BooleanClauseList clauseList = new BooleanClauseList();
            //final Object formulaValue = formula.evaluate();
//                    if (formulaValue != null) { //TODO
//                        if (formulaValue == Boolean.FALSE) {
//                            clauseList.add(new LiteralList());
//                        }
//                    } else {
            formula.getChildren().stream()
                    .map(expression -> getClause((Formula) expression, variableMap))
                    .filter(Objects::nonNull)
                    .forEach(clauseList::add);
            //}
            return Result.of(clauseList); //todo: better error handling when index cannot be found
        }

        protected static BooleanClause getClause(Formula formula, VariableMap variableMap) {
            if (formula instanceof Literal) {
                final Literal literal = (Literal) formula;
                final int index = variableMap.get(literal.getExpression().getName()).orElseThrow(RuntimeException::new);
                return new BooleanClause(literal.isPositive() ? index : -index);
            } else {
                final List<? extends Expression> children = formula.getChildren();
                if (children.stream().anyMatch(literal -> literal == Expressions.True)) {
                    return null;
                } else {
                    final int[] literals = children.stream()
                            .filter(literal -> literal != Expressions.False)
                            .filter(literal -> literal instanceof Literal)
                            .mapToInt(literal -> {
                                final int variable = variableMap.get(((Literal) literal).getExpression().getName())
                                        .orElseThrow(RuntimeException::new);
                                return ((Literal) literal).isPositive() ? variable : -variable;
                            })
                            .toArray();
                    return new BooleanClause(literals);
                }
            }
        }

        @Override
        public Traversable<Computable<?>> cloneNode() {
            return new OfFormula(getInput());
        }
    }
}
