package configuration.processes

import betsy.data.Process
import betsy.data.TestCase
import betsy.data.TestStep
import betsy.data.WsdlOperation
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.data.assertions.ExitAssertion

class ScopeProcesses {

    ProcessBuilder builder = new ProcessBuilder()

    public final Process COMPENSATE = builder.buildScopeProcess(
            "Scope-Compensate", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensate.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process COMPENSATE_SCOPE = builder.buildScopeProcess(
            "Scope-CompensateScope", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensateScope.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_REPEATED_COMPENSATION = builder.buildScopeProcess(
            "Scope-RepeatedCompensation", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw. The process-level faultHandler that catches the fault contains two subsequent compensates the second of which should be treated as empty.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_COMPLEX_COMPENSATION = builder.buildScopeProcess(
            "Scope-ComplexCompensation", "Complex scope compensation test case that implements the scenario described in Sec. 12.4.2.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "3", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_MESSAGE_EXCHANGES = builder.buildScopeProcess(
            "Scope-MessageExchanges", "A scope with a receive-reply pair and a scope-level definition of messageExchanges.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_CORRELATION_SETS_INIT_ASYNC = builder.buildScopeProcess(
            "Scope-CorrelationSets-InitAsync", "A scope with an asynchronous receive which initiates the correlation set and a receive-reply pair, as well as a scope-level definition of a correlationSet that is used by the messaging activities.",
            [
                    new TestCase(testSteps: [
                            new TestStep(input: "1", operation: WsdlOperation.ASYNC),
                            new TestStep(input: "1", output: "2", operation: WsdlOperation.SYNC)
                    ])
            ]
    )

    public final Process SCOPE_CORRELATION_INIT_SYNC = builder.buildScopeProcess(
            "Scope-CorrelationSets-InitSync", "A scope with two subsequent receive-reply pairs and a scope-level definition of a correlationSet that is used by the messaging activities.",
            [
                    new TestCase(testSteps: [
                            new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC),
                            new TestStep(input: "1", output: "2", operation: WsdlOperation.SYNC)
                    ])
            ]
    )

    public final Process SCOPE_EVENT_HANDLERS_ON_ALARM_FOR = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-For", "A receive-reply pair and a process-level onAlarm eventHandler. The receive is followed by a wait that pauses execution for five seconds. The eventHandler waits for two seconds and replies to the receive.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", outputAsLeast: "2", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_FOR = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-For", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after one second, so the handler should repeat exactly once. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", outputAsLeast: "1", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_UNTIL = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-Until",  "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after a date in the past, so the handler should execute immediately. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", outputAsLeast: "2", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_EVENT_HANDLERS_ON_ALARM_UNTIL = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-Until", "A receive followed by a scope with an onAlarm eventHandler and a wait. The onAlarm waits until a date in the past and should therefore execute immediately. Its body contains the reply to the initial receive.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_EVENT_HANDLERS_PARTS = builder.buildScopeProcess(
            "Scope-EventHandlers-Parts", "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. Furthermore, the onMessage uses the fromPart syntax.",
            [
                    new TestCase(testSteps: [
                            new TestStep(input: "5", operation: WsdlOperation.ASYNC),
                            new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)
                    ])
            ]
    )

    public final Process SCOPE_EVENT_HANDLER_INIT_ASYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-InitAsync",  "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase(testSteps: [
                            new TestStep(input: "5", operation: WsdlOperation.ASYNC),
                            new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)
                    ])
            ]
    )

    public final Process SCOPE_EVENT_HANDLER_INIT_SYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-InitSync", "A receive-reply pair followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase(testSteps: [
                            new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC, timeToWaitAfterwards: 3000),
                            new TestStep(input: "1", output: "2", operation: WsdlOperation.SYNC)
                    ])
            ]
    )

    public final List<Process> SCOPE_EVENT_HANDLERS = [
            SCOPE_EVENT_HANDLER_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_INIT_SYNC,
            SCOPE_EVENT_HANDLERS_ON_ALARM_FOR,
            SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY,
            SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_FOR,
            SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_UNTIL,
            SCOPE_EVENT_HANDLERS_ON_ALARM_UNTIL,
            SCOPE_EVENT_HANDLERS_PARTS
    ].flatten() as List<Process>

    public final Process SCOPE_EXIT_ON_STANDARD_FAULT = builder.buildScopeProcess(
            "Scope-ExitOnStandardFault", "A scope with receive-reply pair and an intermediate throw. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", assertions: [new ExitAssertion()], operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_EXIT_ON_STANDARD_FAULT_JOIN_FAILURE = builder.buildScopeProcess(
            "Scope-ExitOnStandardFault-JoinFailure", "A scope with a receive-reply pair and an intermediate throw that throws a joinFailure. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes. However, the exitOnStandardFault sematics do not apply to joinFailures." ,
            [
                    new TestCase(testSteps: [new TestStep(input: "1", assertions: [new SoapFaultTestAssertion(faultString: "joinFailure")], operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_FAULT_HANDLERS_CATCH_ALL = builder.buildScopeProcess(
            "Scope-FaultHandlers-CatchAll", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive." ,
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_FAULT_HANDLERS_CATCH_ORDER = builder.buildScopeProcess(
            "Scope-FaultHandlers-CatchOrder", "A scope with a receive followed by a intermediate throw. The scope is associated with mulitple faultHandlers. A specific one of these should catch the fault and only inside this faultHandler is the reply to the initial receive. The process is adapted from the example in Spec. 12.5.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_FAULT_HANDLERS_FAULT_ELEMENT = builder.buildScopeProcess(
            "Scope-FaultHandlers-FaultElement",  "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultElement configuration. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_FAULT_HANDLERS_FAULT_MESSAGE_TYPE = builder.buildScopeProcess(
            "Scope-FaultHandlers-FaultMessageType",    "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessageType configuration. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA = builder.buildScopeProcess(
            "Scope-FaultHandlers-VariableData", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessage configuration. Inside this faultHandler is the reply to the initial receive and the data replied is the content of the faultVariable.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "0", operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_FAULT_HANDLER = builder.buildScopeProcess(
            "Scope-FaultHandlers",   "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler by its faultName. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "5", operation: WsdlOperation.SYNC)])
            ]
    )

    public final List<Process> SCOPE_FAULT_HANDLERS = [
            SCOPE_FAULT_HANDLER,
            SCOPE_FAULT_HANDLERS_CATCH_ALL,
            SCOPE_FAULT_HANDLERS_FAULT_ELEMENT,
            SCOPE_FAULT_HANDLERS_FAULT_MESSAGE_TYPE,
            SCOPE_EXIT_ON_STANDARD_FAULT,
            SCOPE_EXIT_ON_STANDARD_FAULT_JOIN_FAILURE,
            SCOPE_FAULT_HANDLERS_CATCH_ORDER,
            SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA
    ].flatten() as List<Process>

    public final Process SCOPE_PARTNER_LINKS = builder.buildProcessWithPartner(
            "scopes/Scope-PartnerLinks",  "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_VARIABLES = builder.buildScopeProcess(
            "Scope-Variables",  "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", output: "1", operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_VARIABLES_OVERWRITING = builder.buildScopeProcess(
            "Scope-Variables-Overwriting",  "A scope with a receive-reply pair and another nested scope. The nested scope overwrites a variable of the parent scope. Child-level manipulation of this variable should not be visible at the parent scope.",
            [
                    new TestCase(testSteps: [new TestStep(input: "123", output: "3", operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_ISOLATED = builder.buildScopeProcess(
            "Scope-Isolated",  "A receive-reply pair that encloses a flow with ten isolated scopes which all increment the result by one. As the scopes should not run in parallel, the outcome must be deterministic.",
            [
                    new TestCase(testSteps: [
                            new TestStep(input: "1", output: "11", operation: WsdlOperation.SYNC),
                            new TestStep(input: "4", output: "14", operation: WsdlOperation.SYNC),
                            new TestStep(input: "123", output: "133", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process SCOPE_TERMINATION_HANDLERS = builder.buildScopeProcess(
            "Scope-TerminationHandlers", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "-1", operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_TERMINATION_HANDLERS_FAULT_NOT_PROPAGATING = builder.buildScopeProcess(
            "Scope-TerminationHandlers-FaultNotPropagating", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope. That terminationHandler also throws a fault which should not be propagated.",
            [
                    new TestCase(testSteps: [new TestStep(input: "5", output: "-1", operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process SCOPE_REPEATABLE_CONSTRUCT_COMPENSATION = builder.buildScopeProcess(
            "Scope-RepeatableConstructCompensation", "A receive followed by a while that contains a scope with a compensationHandler. After the while comes a throw and its fault is caught by the process-level faultHandler. This faultHandler first invokes compensation of all scopes and the replies to the initial receive. The content of the reply depends on the execution of the compensationHandlers.",
            [
                    new TestCase(testSteps: [new TestStep(input: "3", output: "3", operation: WsdlOperation.SYNC)])
            ]
    )

    public final Process MISSING_REPLY = builder.buildScopeProcess(
            "MissingReply",  "A receive for a synchronous operation with no associated reply.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", assertions: [new SoapFaultTestAssertion(faultString: "missingReply")], operation: WsdlOperation.SYNC)]),
            ]
    )

    public final Process MISSING_REQUEST = builder.buildScopeProcess(
            "MissingRequest", "A receive and a reply which belong to different messageExchanges. On the execution of the reply, a missingRequest fault should be thrown.",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", assertions: [new SoapFaultTestAssertion(faultString: "missingRequest")], operation: WsdlOperation.SYNC)]),
            ]
    )

    public final List<Process> SCOPES = [
            COMPENSATE,
            COMPENSATE_SCOPE,
            SCOPE_COMPLEX_COMPENSATION,
            SCOPE_REPEATED_COMPENSATION,
            SCOPE_CORRELATION_SETS_INIT_ASYNC,
            SCOPE_CORRELATION_INIT_SYNC,
            SCOPE_EVENT_HANDLERS,
            SCOPE_FAULT_HANDLERS,
            SCOPE_MESSAGE_EXCHANGES,
            SCOPE_PARTNER_LINKS,
            SCOPE_VARIABLES,
            SCOPE_VARIABLES_OVERWRITING,
            SCOPE_ISOLATED,
            SCOPE_TERMINATION_HANDLERS,
            SCOPE_TERMINATION_HANDLERS_FAULT_NOT_PROPAGATING,
            SCOPE_REPEATABLE_CONSTRUCT_COMPENSATION,
            MISSING_REPLY,
            MISSING_REQUEST
    ].flatten() as List<Process>
}