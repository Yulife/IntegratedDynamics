package org.cyclops.integrateddynamics.core.evaluate.operator;

import org.cyclops.integrateddynamics.core.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.core.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.core.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;

/**
 * Collection of available operators.
 * @author rubensworks
 */
public final class  Operators {

    /**
     * ----------------------------------- BOOLEAN OPERATORS -----------------------------------
     */

    /**
     * Short-circuit logical AND operator with two input booleans and one output boolean.
     */
    public static final LogicalOperator LOGICAL_AND = new LogicalOperator("&&", new BaseOperator.IFunction() {
        @Override
        public IValue evaluate(IVariable... variables) {
            boolean a = ((ValueTypeBoolean.ValueBoolean) variables[0].getValue()).getRawValue();
            if(!a) {
                return ValueTypeBoolean.ValueBoolean.of(false);
            } else {
                return variables[1].getValue();
            }
        }
    });

    /**
     * Short-circuit logical AND operator with two input booleans and one output boolean.
     */
    public static final LogicalOperator LOGICAL_OR = new LogicalOperator("||", new BaseOperator.IFunction() {
        @Override
        public IValue evaluate(IVariable... variables) {
            boolean a = ((ValueTypeBoolean.ValueBoolean) variables[0].getValue()).getRawValue();
            if(a) {
                return ValueTypeBoolean.ValueBoolean.of(true);
            } else {
                return variables[1].getValue();
            }
        }
    });

    /**
     * Logical NOT operator with one input booleans and one output boolean.
     */
    public static final LogicalOperator LOGICAL_NOT = new LogicalOperator("!", 1, new BaseOperator.IFunction() {
        @Override
        public IValue evaluate(IVariable... variables) {
            boolean a = ((ValueTypeBoolean.ValueBoolean) variables[0].getValue()).getRawValue();
            return ValueTypeBoolean.ValueBoolean.of(!a);
        }
    });

    /**
     * ----------------------------------- INTEGER OPERATORS -----------------------------------
     */

    private static final ValueTypeInteger.ValueInteger ZERO = ValueTypeInteger.ValueInteger.of(0);

    /**
     * Arithmetic ADD operator with two input integers and one output integer.
     */
    public static final ArithmeticOperator ARITHMETIC_ADDITION = new ArithmeticOperator("+", new BaseOperator.IFunction() {
        @Override
        public IValue evaluate(IVariable... variables) {
            int a = ((ValueTypeInteger.ValueInteger) variables[0].getValue()).getRawValue();
            if(a == 0) { // If a is neutral element for addition
                return variables[1].getValue();
            } else {
                int b = ((ValueTypeInteger.ValueInteger) variables[1].getValue()).getRawValue();
                if(b == 0) { // If b is neutral element for addition
                    return variables[0].getValue();
                } else {
                    return ValueTypeInteger.ValueInteger.of(a + b);
                }
            }
        }
    });

    /**
     * Arithmetic MINUS operator with two input integers and one output integer.
     */
    public static final ArithmeticOperator ARITHMETIC_SUBTRACTION = new ArithmeticOperator("-", new BaseOperator.IFunction() {
        @Override
        public IValue evaluate(IVariable... variables) {
            int b = ((ValueTypeInteger.ValueInteger) variables[1].getValue()).getRawValue();
            if(b == 0) { // If b is neutral element for subtraction
                return variables[0].getValue();
            } else {
                int a = ((ValueTypeInteger.ValueInteger) variables[0].getValue()).getRawValue();
                return ValueTypeInteger.ValueInteger.of(a - b);
            }
        }
    });

    /**
     * Arithmetic MULTIPLY operator with two input integers and one output integer.
     */
    public static final ArithmeticOperator ARITHMETIC_MULTIPLICATION = new ArithmeticOperator("*", new BaseOperator.IFunction() {
        @Override
        public IValue evaluate(IVariable... variables) {
            int a = ((ValueTypeInteger.ValueInteger) variables[0].getValue()).getRawValue();
            if(a == 0) { // If a is absorbtion element for multiplication
                return variables[0].getValue();
            } else if(a == 1) { // If a is neutral element for multiplication
                return variables[1].getValue();
            } else {
                int b = ((ValueTypeInteger.ValueInteger) variables[1].getValue()).getRawValue();
                if(b == 1) { // If b is neutral element for multiplication
                    return variables[0].getValue();
                } else {
                    return ValueTypeInteger.ValueInteger.of(a * b);
                }
            }
        }
    });

    /**
     * Arithmetic DIVIDE operator with two input integers and one output integer.
     */
    public static final ArithmeticOperator ARITHMETIC_DIVISION = new ArithmeticOperator("/", new BaseOperator.IFunction() {
        @Override
        public IValue evaluate(IVariable... variables) throws EvaluationException {
            int b = ((ValueTypeInteger.ValueInteger) variables[1].getValue()).getRawValue();
            if(b == 0) { // You can not divide by zero
                throw new EvaluationException("Division by zero");
            } else if(b == 1) { // If b is neutral element for division
                return variables[0].getValue();
            } else {
                int a = ((ValueTypeInteger.ValueInteger) variables[0].getValue()).getRawValue();
                return ValueTypeInteger.ValueInteger.of(a / b);
            }
        }
    });

    /**
     * Arithmetic MODULO operator with two input integers and one output integer.
     */
    public static final ArithmeticOperator ARITHMETIC_MODULUS = new ArithmeticOperator("%", new BaseOperator.IFunction() {

        @Override
        public IValue evaluate(IVariable... variables) throws EvaluationException {
            int b = ((ValueTypeInteger.ValueInteger) variables[1].getValue()).getRawValue();
            if(b == 0) { // You can not divide by zero
                throw new EvaluationException("Division by zero");
            } else if(b == 1) { // If b is neutral element for division
                return ZERO;
            } else {
                int a = ((ValueTypeInteger.ValueInteger) variables[0].getValue()).getRawValue();
                return ValueTypeInteger.ValueInteger.of(a % b);
            }
        }
    });

    /**
     * Arithmetic MAX operator with two input integers and one output integer.
     */
    public static final ArithmeticOperator ARITHMETIC_MAX = new ArithmeticOperator("max", new BaseOperator.IFunction() {

        @Override
        public IValue evaluate(IVariable... variables) throws EvaluationException {
            int a = ((ValueTypeInteger.ValueInteger) variables[0].getValue()).getRawValue();
            int b = ((ValueTypeInteger.ValueInteger) variables[1].getValue()).getRawValue();
            return ValueTypeInteger.ValueInteger.of(Math.max(a, b));
        }
    });

    /**
     * Arithmetic MIN operator with two input integers and one output integer.
     */
    public static final ArithmeticOperator ARITHMETIC_MIN = new ArithmeticOperator("min", new BaseOperator.IFunction() {

        @Override
        public IValue evaluate(IVariable... variables) throws EvaluationException {
            int a = ((ValueTypeInteger.ValueInteger) variables[0].getValue()).getRawValue();
            int b = ((ValueTypeInteger.ValueInteger) variables[1].getValue()).getRawValue();
            return ValueTypeInteger.ValueInteger.of(Math.min(a, b));
        }
    });

}