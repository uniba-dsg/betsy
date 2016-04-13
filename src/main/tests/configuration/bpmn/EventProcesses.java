package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.EngineIndependentProcess;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;

import java.util.Arrays;
import java.util.List;

class EventProcesses {

    public static final EngineIndependentProcess CANCEL = BPMNProcessBuilder.buildEventProcess(
            "Cancel_Event", "A simple test for canceling a transaction. This test uses the two allowed cancel event types:" +
                    "CancelEndEvent (within the transaction) and CancelBoundaryEvent (interrupting, attached to the transaction).",
            new Feature(new Construct(Groups.EVENTS, "Cancel_Event"), "Cancel_Event"),
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess COMPENSATION_BOUNDARY_EVENT_SUBPROCESS = BPMNProcessBuilder.buildEventProcess(
            "Compensation_BoundaryEvent_SubProcess", "Tests whether the compensation boundary event can be attached to a" +
                    "sub process.",
            new Feature(new Construct(Groups.EVENTS, "Compensation_Event"), "Compensation_BoundaryEvent_SubProcess"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess COMPENSATION_END_EVENT_SUBPROCESS = BPMNProcessBuilder.buildEventProcess(
            "Compensation_EndEvent_SubProcess", "A test with a compensation end event placed in a sub process which " +
                    "should trigger the compensation of the task performed before.",
            new Feature(new Construct(Groups.EVENTS, "Compensation_Event"), "Compensation_EndEvent_SubProcess"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess COMPENSATION_END_EVENT_TOP_LEVEL = BPMNProcessBuilder.buildEventProcess(
            "Compensation_EndEvent_TopLevel", "A test with a top level compensation end event which should trigger the " +
                    "compensation of the task performed before.",
            new Feature(new Construct(Groups.EVENTS, "Compensation_Event"), "Compensation_EndEvent_TopLevel"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess COMPENSATION_INTERMEDIATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Compensation_IntermediateEvent", "A test with a top level compensation intermediate event which should " +
                    "trigger the compensation of the task performed before.",
            new Feature(new Construct(Groups.EVENTS, "Compensation_Event"), "Compensation_IntermediateEvent"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess COMPENSATION_START_EVENT_EVENT_SUBPROCESS = BPMNProcessBuilder.buildEventProcess(
            "Compensation_StartEvent_EventSubProcess", "A test with an event SubProcess, triggered by an Compensation StartEvent." +
                    "The compensation is triggered by an Intermediate Throw Event placed outside the subprocess.",
            new Feature(new Construct(Groups.EVENTS, "Compensation_Event"), "Compensation_StartEvent_EventSubProcess"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess COMPENSATION_TRIGGERED_BY_CANCEL = BPMNProcessBuilder.buildEventProcess(
            "Compensation_TriggeredByCancel", "A test with a transaction which ends with a CancelEnd Event. In the " +
                    "course of canceling the transaction all successful executed tasks have to be compensated. Therefore, the " +
                    "compensational task Task1 has to be executed.",
            new Feature(new Construct(Groups.EVENTS, "Compensation_Event"), "Compensation_TriggeredByCancel"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Conditional_BoundaryEvent_SubProcess_Interrupting", "A test for an conditional boundary event attached to a" +
                    "sub process which is marked as interrupting.",
            new Feature(new Construct(Groups.EVENTS, "Conditional_Event"), "Conditional_BoundaryEvent_SubProcess_Interrupting"),
            new BPMNTestCase().inputA().assertTask3(),
            new BPMNTestCase().inputB().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Conditional_BoundaryEvent_SubProcess_NonInterrupting", "A test for an conditional boundary event attached to a" +
                    "sub process which is marked as non interrupting.",
            new Feature(new Construct(Groups.EVENTS, "Conditional_Event"), "Conditional_BoundaryEvent_SubProcess_NonInterrupting"),
            new BPMNTestCase().inputA().assertTask1().assertTask2().assertTask3(),
            new BPMNTestCase().inputB().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess CONDITIONAL_INTERMEDIATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Conditional_IntermediateEvent", "A test for an intermediate conditional event: ConditionIntermediate checks " +
                    "a condition set at process instantiation. If the condition is fulfilled the process completes, if not the " +
                    "process is locked at the event and should not complete.",
            new Feature(new Construct(Groups.EVENTS, "Conditional_Event"), "Conditional_IntermediateEvent"),
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB()
    );

    /*
    * Since we know no engine-independent way to set condition prior process instantiation this is postponed to
    * future work
    * Using the current test, camunda and activiti simply ignore the conditionalEventDefinition, jBPM rejects the
    * deployment of the process
    public static final EngineIndependentProcess CONDITIONAL_START_EVENT = builder.buildEventProcess(
            "Conditional_StartEvent", "A test with a conditional start event",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB()
            ]
    )
    */

    public static final EngineIndependentProcess CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Conditional_StartEvent_EventSubProcess_Interrupting", "A test for an interrupting conditional start event " +
                    "in an event sub process",
            new Feature(new Construct(Groups.EVENTS, "Conditional_Event"), "Conditional_StartEvent_EventSubProcess_Interrupting"),
            new BPMNTestCase().inputA().assertTask1()
    );

    public static final EngineIndependentProcess CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Conditional_StartEvent_EventSubProcess_NonInterrupting", "A test for an conditional start event in an event " +
                    "sub process which is marked as non interrupting.",
            new Feature(new Construct(Groups.EVENTS, "Conditional_Event"), "Conditional_StartEvent_EventSubProcess_NonInterrupting"),
            new BPMNTestCase().inputA().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess ERROR_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Error_BoundaryEvent_SubProcess_Interrupting", "A test for the error boundary event attached to a sub process." +
                    "The task (task2) following the SequenceFlow originating from the boundary event should be executed. The Task (task3) " +
                    "following the normal outgoing sequence flow after the SubProcess must not be executed.",
            new Feature(new Construct(Groups.EVENTS, "Error_Event"), "Error_BoundaryEvent_SubProcess_Interrupting"),
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess ERROR_BOUNDARY_EVENT_TRANSACTION_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Error_BoundaryEvent_Transaction_Interrupting", "A test for the error boundary event attached to a transaction." +
                    "The task (task2) following the SequenceFlow originating from the boundary event should be executed. The Task (task3) " +
                    "following the normal outgoing sequence flow after the Transaction must not be executed.",
            new Feature(new Construct(Groups.EVENTS, "Error_Event"), "Error_BoundaryEvent_Transaction_Interrupting"),
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess ERROR_END_EVENT_TOPLEVEL = BPMNProcessBuilder.buildEventProcess(
            "Error_EndEvent_TopLevel", "A simple test for the ErrorEndEvent in a top level process.",
            new Feature(new Construct(Groups.EVENTS, "Error_Event"), "Error_EndEvent_TopLevel"),
            new BPMNTestCase().assertTask1().assertErrorThrownErrorEvent()
    );

    public static final EngineIndependentProcess ERROR_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Error_StartEvent_EventSubProcess_Interrupting", "A test for the error start event in an event sub process. " +
                    "After the execution of the EventSubProcess the flow should continue normally.",
            new Feature(new Construct(Groups.EVENTS, "Error_Event"), "Error_StartEvent_EventSubProcess_Interrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess ESCALATION_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Escalation_BoundaryEvent_SubProcess_Interrupting", "A test for an escalation event interrupting a subprocess." +
                    "The task (task2) following the Intermediate EscalationEvent and the Task (task4) following the normal " +
                    "outgoing sequence flow after the SubProcess must not be executed. " +
                    "The SequenceFlow originating from the boundary event is activated and therefore Task3 should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Escalation_Event"), "Escalation_BoundaryEvent_SubProcess_Interrupting"),
            new BPMNTestCase().assertTask1().assertTask3()
    );

    public static final EngineIndependentProcess ESCALATION_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Escalation_BoundaryEvent_SubProcess_NonInterrupting", "A test for an escalation event NOT interrupting a subprocess." +
                    "All tasks (Task1-4) should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Escalation_Event"), "Escalation_BoundaryEvent_SubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4()
    );

    public static final EngineIndependentProcess ESCALATION_END_EVENT_SUBPROCESS = BPMNProcessBuilder.buildEventProcess(
            "Escalation_EndEvent_SubProcess", "A test for an escalation end event defined in a SubProcess." +
                    "Only the task attached to the BoundaryEvent should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Escalation_Event"), "Escalation_EndEvent_SubProcess"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess ESCALATION_END_EVENT_TOPLEVEL = BPMNProcessBuilder.buildEventProcess(
            "Escalation_EndEvent_TopLevel", "A test for an escalation end event in a top level process.",
            new Feature(new Construct(Groups.EVENTS, "Escalation_Event"), "Escalation_EndEvent_TopLevel"),
            new BPMNTestCase().assertTask1().assertErrorThrownEscalationEvent()
    );

    public static final EngineIndependentProcess ESCALATION_INTERMEDIATE_THROW_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Escalation_IntermediateThrowEvent", "A test for an escalation intermediate throw event: " +
                    "Task1 can only be executed if the event has been thrown (and caught).",
            new Feature(new Construct(Groups.EVENTS, "Escalation_Event"), "Escalation_IntermediateThrowEvent"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess ESCALATION_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Escalation_StartEvent_EventSubProcess_Interrupting", "A test for the interrupting escalation start event in " +
                    "an event SubProcess. Task1 within in the (normal) SubProcess should not be executed. Task 2 should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Escalation_Event"), "Escalation_StartEvent_EventSubProcess_Interrupting"),
            new BPMNTestCase().assertTask2()
    );

    public static final EngineIndependentProcess ESCALATION_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Escalation_StartEvent_EventSubProcess_NonInterrupting", "A test for the escalation start event in an event " +
                    "sub process which is marked as \"non interrupting\". Task2 within in the (normal) SubProcess and Task3 " +
                    "which is defined after the SubProcess should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Escalation_Event"), "Escalation_StartEvent_EventSubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess LINK_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Link_Event", "A simple test for link events",
            new Feature(new Construct(Groups.EVENTS, "Link_Event"), "Link_Event"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess MESSAGE_START_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Message_StartEvent", "A test with two processes. The first starts the second via a message sent in an intermediate event.",
            new Feature(new Construct(Groups.EVENTS, "Message_Event"), "Message_StartEvent"),
            new BPMNTestCase().assertTask1().assertMarkerExists()
    );

    public static final EngineIndependentProcess MESSAGE_INTERMEDIATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Message_IntermediateEvent", "A test with two processes, which start in parallel. " +
                    "The first one sends a message to the second one, which is caught in an intermediate event.",
            new Feature(new Construct(Groups.EVENTS, "Message_Event"), "Message_IntermediateEvent"),
            new BPMNTestCase().useParallelProcess().assertTask1().assertMarkerExists()
    );

    public static final EngineIndependentProcess MESSAGE_END_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Message_EndEvent", "A test with two processes. The first starts the second via a message sent in an end event.",
            new Feature(new Construct(Groups.EVENTS, "Message_Event"), "Message_EndEvent"),
            new BPMNTestCase().assertTask1().assertMarkerExists()
    );

    public static final EngineIndependentProcess SIGNAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Signal_BoundaryEvent_SubProcess_NonInterrupting", "A test for a signal boundary event NOT interrupting a subprocess." +
                    "All tasks (Task1-4) should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_BoundaryEvent_SubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4()
    );

    public static final EngineIndependentProcess SIGNAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Signal_BoundaryEvent_SubProcess_Interrupting", "A test for a signal boundary event interrupting a subprocess." +
                    "The task (task2) following the Intermediate SignalEvent and the Task (task4) following the normal " +
                    "outgoing sequence flow after the SubProcess must not be executed. " +
                    "The SequenceFlow originating from the boundary event is activated and therefore Task3 should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_BoundaryEvent_SubProcess_Interrupting"),
            new BPMNTestCase().assertTask1().assertTask3()
    );

    public static final EngineIndependentProcess SIGNAL_END_EVENT_SUBPROCESS = BPMNProcessBuilder.buildEventProcess(
            "Signal_EndEvent_SubProcess", "A test to test a signal end event placed in a SubProcess. " +
                    "The thrown signal is caught by an attached boundary event.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_EndEvent_SubProcess"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess SIGNAL_INTERMEDIATE_EVENT_THROW_AND_CATCH = BPMNProcessBuilder.buildEventProcess(
            "Signal_IntermediateEvent_ThrowAndCatch", "A test for signal intermediate events: After a parallel split one" +
                    "branch of the process awaits a signal which is thrown by the other branch.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_IntermediateEvent_ThrowAndCatch"),
            new BPMNTestCase().assertTask1().optionDelay(10000)
    );


    public static final EngineIndependentProcess SIGNAL_START_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Signal_StartEvent", "A test with two processes. The first one starts the second by throwing a signal in an intermediate event.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_StartEvent"),
            new BPMNTestCase().assertMarkerExists().assertTask1()
    );

    public static final EngineIndependentProcess SIGNAL_END_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Signal_EndEvent", "A test with two processes. The first one starts the second by throwing a signal in its end event.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_EndEvent"),
            new BPMNTestCase().assertTask1().assertMarkerExists()
    );

    public static final EngineIndependentProcess SIGNAL_INTERMEDIATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Signal_IntermediateEvent", "A test with two processes, which start in parallel. The first one throws a signal, which the second one catches.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_IntermediateEvent"),
            new BPMNTestCase().useParallelProcess().assertTask1().assertMarkerExists()
    );

    public static final EngineIndependentProcess SIGNAL_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Signal_StartEvent_EventSubProcess_Interrupting", "A test for the interrupting signal start event in " +
                    "an event SubProcess. Task1 within in the (normal) SubProcess should not be executed. Task 2 should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_StartEvent_EventSubProcess_Interrupting"),
            new BPMNTestCase().assertTask2()
    );

    public static final EngineIndependentProcess SIGNAL_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Signal_StartEvent_EventSubProcess_NonInterrupting", "A test for the signal start event in an event " +
                    "sub process which is marked as \"non interrupting\". Task2 within in the (normal) SubProcess and Task3 " +
                    "which is defined after the SubProcess should be executed.",
            new Feature(new Construct(Groups.EVENTS, "Signal_Event"), "Signal_StartEvent_EventSubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess TERMINATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Terminate_Event", "A test for a terminate end event",
            new Feature(new Construct(Groups.EVENTS, "Terminate_Event"), "Terminate_Event"),
            new BPMNTestCase()
    );

    public static final EngineIndependentProcess TIMER_INTERMEDIATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Timer_IntermediateEvent", "A process with two scriptTasks. There is a intermediateCatchEvent in between the tasks that delay the execution for a short period of time.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_IntermediateEvent"),
            new BPMNTestCase().assertTask1().optionDelay(5000)
    );

    public static final EngineIndependentProcess TIMER_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Timer_BoundaryEvent_SubProcess_NonInterrupting", "A process with multiple scriptTasks and a subProcess with timer events. " +
                    "The execution of the subProcess is delayed by an intermediate timer event for a short amount of time." +
                    "In the meantime, a boundary timer event should fire without interrupting the subProcess.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_BoundaryEvent_SubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().optionDelay(5000)
    );

    public static final EngineIndependentProcess TIMER_BOUNDARY_EVENT_SUBPROCESS_TIMECYCLE = BPMNProcessBuilder.buildEventProcess(
            "Timer_BoundaryEvent_SubProcess_TimeCycle", "A process with multiple scriptTasks and a subProcess with timer events. " +
                    "The execution of the subProcess is delayed by an intermediate timer event for a short amount of time." +
                    "In the meantime a boundary timer event with the attribute timecycle should fire four times without interrupting "
                    + "the subProcess, which consists of a timer event and a scriptTask.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_BoundaryEvent_SubProcess_TimeCycle"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask3().assertTask3().assertTask3().optionDelay(65000)
    );

    public static final EngineIndependentProcess TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Timer_BoundaryEvent_SubProcess_Interrupting", "A process with multiple scriptTasks and a subProcess with timer events. " +
                    "The execution of the subProcess is delayed by an intermediate timer event for a short amount of time." +
                    "In the meantime, a boundary timer event should fire and interrupt the subProcess.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_BoundaryEvent_SubProcess_Interrupting"),
            new BPMNTestCase().assertTask3().optionDelay(5000)
    );

    public static final EngineIndependentProcess TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING_ACTIVITIY = BPMNProcessBuilder.buildEventProcess(
            "Timer_BoundaryEvent_SubProcess_Interrupting_Activity", "A process with multiple scriptTasks and a subProcess with an activity which "
                    + "needs more time for executing than the boundary timer. " +
                    "The execution of the subProcess is therefore longer than the boundary timer event." +
                    "It is expected that the boundary timer event fires before the task is completed and interrupts the subProcess.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_BoundaryEvent_SubProcess_Interrupting_Activity"),
            new BPMNTestCase().assertTask2().optionDelay(12000)
    );

    public static final EngineIndependentProcess TIMER_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Timer_StartEvent_EventSubProcess_NonInterrupting", "A process with an ordinary subProcess and an event subProcess." +
                    "The subProcess encloses the event subProcess and the latter is started by a timer startEvent. " +
                    "All activities should be executed without interruption.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_StartEvent_EventSubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().optionDelay(5000)
    );

    public static final EngineIndependentProcess TIMER_START_EVENT_TIMECYCLE_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Timer_StartEvent_TimeCycle_EventSubProcess_NonInterrupting", "A process with an ordinary subProcess and an event subProcess." +
                    "The subProcess encloses the event subProcess and the latter is started by a timer startEvent with the attribute timeCycle. " +
                    "The event SubProcess should be executed four times." +
                    "All activities should be executed without interruption.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_StartEvent_TimeCycle_EventSubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask2().assertTask2().assertTask2().assertTask3().optionDelay(35000)
    );

    public static final EngineIndependentProcess TIMER_INTERMEDIATE_TIMECYCLE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Timer_IntermediateTimeCycleEvent", "A process with two scriptTasks. There is an intermediateCatchEvent with the attribute timeCycle in between" +
                    " the tasks that triggers the second task four times.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_IntermediateTimeCycleEvent"),
            new BPMNTestCase().assertTask1().assertTask1().assertTask1().assertTask1().optionDelay(5000)
    );

    public static final EngineIndependentProcess TIMER_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "Timer_StartEvent_EventSubProcess_Interrupting", "A process with an ordinary subProcess and an event subProcess." +
                    "The subProcess encloses the event subProcess and the latter is started by a timer startEvent. " +
                    "The event subProcess interrupts the activities of its parent subProcess.",
            new Feature(new Construct(Groups.EVENTS, "Timer_Event"), "Timer_StartEvent_EventSubProcess_Interrupting"),
            new BPMNTestCase().assertTask2().optionDelay(5000)
    );

    public static final EngineIndependentProcess MULTIPLE_PARALLEL_INTERMEDIATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Multiple_Parallel_IntermediateEvent", "A process with a multiple parallel event." +
                    "After a parallel split one branch of the process awaits two signals which are thrown by the other branch. " +
                    "The multiple parallel event is thrown.",
            new Feature(new Construct(Groups.EVENTS, "Multiple_Events"), "Multiple_Parallel_IntermediateEvent"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess MULTIPLE_INTERMEDIATE_EVENT_THROW_FIRST_EVENTDEFINITION = BPMNProcessBuilder.buildEventProcess(
            "Multiple_IntermediateEvent_ThrowFirstEventDefinition", "A process with a multiple event." +
                    "After a parallel split one branch of the process awaits only one of the two events defined in the multiple event." +
                    "The event thrown by the other branch, is the fist event definition of the multiple event. The multiple event is thrown.",
            new Feature(new Construct(Groups.EVENTS, "Multiple_Events"), "Multiple_IntermediateEvent_ThrowFirstEventDefinition"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess MULTIPLE_INTERMEDIATE_EVENT_THROW_LAST_EVENTDEFINITION = BPMNProcessBuilder.buildEventProcess(
            "Multiple_IntermediateEvent_ThrowLastEventDefinition", "A process with a multiple event." +
                    "After a parallel split one branch of the process awaits only one of the two events defined in the multiple event." +
                    "The event thrown by the other branch, is the last event definition of the multiple event. The multiple event is thrown.",
            new Feature(new Construct(Groups.EVENTS, "Multiple_Events"), "Multiple_IntermediateEvent_ThrowLastEventDefinition"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess MULTIPLE_INTERMEDIATE_THROW_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Multiple_IntermediateThrowEvent", "A process with a multiple throw event." +
                    "After a parallel split into three branches two of the branches await each one event." +
                    "Both events are thrown in a multiple event on the third branch.",
            new Feature(new Construct(Groups.EVENTS, "Multiple_Events"), "Multiple_IntermediateThrowEvent"),
            new BPMNTestCase().assertTask1().assertTask2()
    );

    public static final EngineIndependentProcess MULTIPLE_INTERMEDIATE_EVENT_MISSING_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Multiple_IntermediateEvent_MissingEvent", "A process with a multiple event." +
                    "After a parallel split one branch of the process awaits only one of the two events defined in the multiple event." +
                    "This event is never thrown. The multiple event is never thrown and thus the process is never finished.",
            new Feature(new Construct(Groups.EVENTS, "Multiple_Events"), "Multiple_IntermediateEvent_MissingEvent"),
            new BPMNTestCase()
    );

    public static final EngineIndependentProcess MULTIPLE_PARALLEL_INTERMEDIATE_EVENT_MISSING_EVENT = BPMNProcessBuilder.buildEventProcess(
            "Multiple_Parallel_IntermediateEvent_MissingEvent", "A process with a multiple parallel event." +
                    "After a parallel split one branch of the process awaits two signals of which only one is thrown by the other branch. " +
                    "The multiple parallel event is never thrown and thus the process is never finished.",
            new Feature(new Construct(Groups.EVENTS, "Multiple_Events"), "Multiple_Parallel_IntermediateEvent_MissingEvent"),
            new BPMNTestCase()
    );

    public static final EngineIndependentProcess EVENT_DEFINITION_REF_ERROR_END_EVENT_TOPLEVEL = BPMNProcessBuilder.buildEventProcess(
            "EventDefinitionRef_Error_EndEvent_TopLevel", "A simple test for the usage of eventDefinitionRef with an ErrorEndEvent in a top level process.",
            new Feature(new Construct(Groups.EVENTS, "EventDefinitionRef"), "EventDefinitionRef_Error_EndEvent_TopLevel"),
            new BPMNTestCase().assertTask1().assertErrorThrownErrorEvent()
    );

    public static final EngineIndependentProcess EVENT_DEFINITION_REF_ERROR_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "EventDefinitionRef_Error_StartEvent_EventSubProcess_Interrupting", "A test for the usage of eventDefinitionRef with an error start event in an event sub process. " +
                    "After the execution of the EventSubProcess the flow should continue normally.",
            new Feature(new Construct(Groups.EVENTS, "EventDefinitionRef"), "EventDefinitionRef_Error_StartEvent_EventSubProcess_Interrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess EVENT_DEFINITION_REF_SIGNAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = BPMNProcessBuilder.buildEventProcess(
            "EventDefinitionRef_Signal_BoundaryEvent_SubProcess_NonInterrupting", "A test for the usage of eventDefinitionRef with a signal boundary event NOT interrupting a subprocess." +
                    "All tasks (Task1-4) should be executed.",
            new Feature(new Construct(Groups.EVENTS, "EventDefinitionRef"), "EventDefinitionRef_Signal_BoundaryEvent_SubProcess_NonInterrupting"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4()
    );

    public static final EngineIndependentProcess EVENT_DEFINITION_REF_TIMER_INTERMEDIATE_EVENT = BPMNProcessBuilder.buildEventProcess(
            "EventDefinitionRef_Timer_IntermediateEvent", "A process with two scriptTasks. There is a intermediateCatchEvent in between "
                    + "the tasks that delay the execution for a short period of time.",
            new Feature(new Construct(Groups.EVENTS, "EventDefinitionRef"), "EventDefinitionRef_Timer_IntermediateEvent"),
            new BPMNTestCase().assertTask1().optionDelay(5000)
    );

    public static final List<EngineIndependentProcess> EVENTS = Arrays.asList(
            CANCEL,

            COMPENSATION_BOUNDARY_EVENT_SUBPROCESS,
            COMPENSATION_END_EVENT_SUBPROCESS,
            COMPENSATION_END_EVENT_TOP_LEVEL,
            COMPENSATION_INTERMEDIATE_EVENT,
            COMPENSATION_START_EVENT_EVENT_SUBPROCESS,
            COMPENSATION_TRIGGERED_BY_CANCEL,

            CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            CONDITIONAL_INTERMEDIATE_EVENT,
            //CONDITIONAL_START_EVENT,
            CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING,

            ERROR_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            ERROR_BOUNDARY_EVENT_TRANSACTION_INTERRUPTING,
            ERROR_END_EVENT_TOPLEVEL,
            ERROR_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,

            ESCALATION_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            ESCALATION_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            ESCALATION_END_EVENT_SUBPROCESS,
            ESCALATION_END_EVENT_TOPLEVEL,
            ESCALATION_INTERMEDIATE_THROW_EVENT,
            ESCALATION_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            ESCALATION_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING,

            LINK_EVENT,

            MESSAGE_START_EVENT,
            MESSAGE_INTERMEDIATE_EVENT,
            MESSAGE_END_EVENT,

            SIGNAL_END_EVENT_SUBPROCESS,
            SIGNAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            SIGNAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            SIGNAL_INTERMEDIATE_EVENT_THROW_AND_CATCH,
            SIGNAL_START_EVENT,
            SIGNAL_END_EVENT,
            SIGNAL_INTERMEDIATE_EVENT,
            SIGNAL_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            SIGNAL_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING,

            TERMINATE_EVENT,

            TIMER_INTERMEDIATE_EVENT,
            TIMER_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING_ACTIVITIY,
            TIMER_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING,
            TIMER_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            TIMER_INTERMEDIATE_TIMECYCLE_EVENT,
            TIMER_BOUNDARY_EVENT_SUBPROCESS_TIMECYCLE,
            TIMER_START_EVENT_TIMECYCLE_EVENT_SUBPROCESS_NON_INTERRUPTING,

            MULTIPLE_PARALLEL_INTERMEDIATE_EVENT,
            MULTIPLE_INTERMEDIATE_EVENT_THROW_FIRST_EVENTDEFINITION,
            MULTIPLE_INTERMEDIATE_EVENT_THROW_LAST_EVENTDEFINITION,
            MULTIPLE_INTERMEDIATE_THROW_EVENT,
            MULTIPLE_INTERMEDIATE_EVENT_MISSING_EVENT,
            MULTIPLE_PARALLEL_INTERMEDIATE_EVENT_MISSING_EVENT,

            EVENT_DEFINITION_REF_ERROR_END_EVENT_TOPLEVEL,
            EVENT_DEFINITION_REF_ERROR_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            EVENT_DEFINITION_REF_SIGNAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            EVENT_DEFINITION_REF_TIMER_INTERMEDIATE_EVENT
    );

}
