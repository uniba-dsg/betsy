package betsy.bpmn.model;

import pebl.benchmark.test.steps.vars.Variable;

enum BPMNTestInput {

    INPUT_A("a"), INPUT_B("b"), INPUT_C("c"), INPUT_AB("ab"), INPUT_AC("ac"), INPUT_BC("bc"), INPUT_ABC("abc");

    private final String value;

    BPMNTestInput(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Variable getVariable() {
        return new Variable("test", "String", getValue());
    }
}
