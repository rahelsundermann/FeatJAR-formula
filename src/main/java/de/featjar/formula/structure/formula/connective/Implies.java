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
package de.featjar.formula.structure.formula.connective;

import de.featjar.formula.structure.BinaryFormula;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.NonTerminalFormula;

import java.util.List;

/**
 * Expresses "if A, then B" constraints (i.e., implication).
 * Evaluates to {@code true} iff the left child evaluates to {@code false} or
 * the right child evaluates to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Implies extends NonTerminalFormula implements Connective, BinaryFormula {

    protected Implies() {
    }

    public Implies(Formula leftFormula, Formula rightFormula) {
        super(leftFormula, rightFormula);
    }

    public Implies(List<? extends Formula> formulas) {
        super(formulas);
    }

    @Override
    public String getName() {
        return "implies";
    }

    @Override
    public Object evaluate(List<?> values) {
        return !(boolean) values.get(0) || (boolean) values.get(1);
    }

    @Override
    public Implies cloneNode() {
        return new Implies();
    }
}