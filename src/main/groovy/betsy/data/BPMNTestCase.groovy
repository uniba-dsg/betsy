package betsy.data

import betsy.data.steps.BPMNTestStep

class BPMNTestCase {

    List<BPMNTestStep> testSteps = []

    public BPMNTestCase addStep(BPMNTestStep testStep){
        testSteps.add(testStep)

        this
    }

    public BPMNTestCase buildSimple(){
        addStep(new BPMNTestStep().assertSuccess())
    }

    public BPMNTestCase buildAnd(){
        addStep(new BPMNTestStep().assertAnd())
    }

    public BPMNTestCase buildXorTrue(){
        addStep(new BPMNTestStep().assertXorTrue())
    }

    public BPMNTestCase buildXorFalse(){
        addStep(new BPMNTestStep().assertXorFalse())
    }

    public BPMNTestCase buildOrSingleFlow(){
        addStep(new BPMNTestStep().assertOrSingleFlow())
    }

    public BPMNTestCase buildOrMultiFlow(){
        addStep(new BPMNTestStep().assertOrMultiFlow())
    }

}
