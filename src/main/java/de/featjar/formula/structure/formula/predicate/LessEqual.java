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
package de.featjar.formula.structure.formula.predicate;

import de.featjar.formula.structure.term.Term;
import java.util.List;

/**
 * Expresses "A <= B" constraints.
 * Evaluates to {@code true} iff the left child evaluates to a smaller or the same value as the right child.
 *
 * @author Sebastian Krieter
 */
public class LessEqual extends BinaryPredicate {
    protected LessEqual() {
    }

    public LessEqual(Term leftTerm, Term rightTerm) {
        super(leftTerm, rightTerm);
    }

    public LessEqual(List<? extends Term> terms) {
        super(terms);
    }

    @Override
    public String getName() {
        return ">=";
    }

    @Override
    public LessEqual cloneNode() {
        return new LessEqual();
    }

    @Override
    public GreaterThan invert() {
        return new GreaterThan((Term) getLeftFormula(), (Term) getRightFormula());
    }

    @Override
    protected boolean compareDifference(int difference) {
        return difference <= 0;
    }
}