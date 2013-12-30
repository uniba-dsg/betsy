package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.SoapFaultTestAssertion

class ScopeProcesses {

    static ProcessBuilder builder = new ProcessBuilder()

    public static final BetsyProcess COMPENSATE = builder.buildScopeProcess(
            "Scope-Compensate", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensate.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public static final BetsyProcess COMPENSATE_SCOPE = builder.buildScopeProcess(
            "Scope-CompensateScope", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensateScope.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public static final BetsyProcess SCOPE_REPEATED_COMPENSATION = builder.buildScopeProcess(
            "Scope-RepeatedCompensation", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw. The process-level faultHandler that catches the fault contains two subsequent compensates the second of which should be treated as empty.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public static final BetsyProcess SCOPE_COMPLEX_COMPENSATION = builder.buildScopeProcess(
            "Scope-ComplexCompensation", "Complex scope compensation test case that implements the scenario described in Sec. 12.4.2.",
            [
                    new TestCase().checkDeployment().sendSync(1, 3)
            ]
    )

    public static final BetsyProcess SCOPE_MESSAGE_EXCHANGES = builder.buildScopeProcess(
            "Scope-MessageExchanges", "A scope with a receive-reply pair and a scope-level definition of messageExchanges.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public static final BetsyProcess SCOPE_MULTIPLE_MESSAGE_EXCHANGES = builder.buildScopeProcess(
            "Scope-Multiple-MessageExchanges", "A scope with a receive-reply pair followed by a receive-reply pair of the same operation that use scope-level definition of messageExchanges to define which reply belongs to which receive and the response is the initial value first then the sum of the received values.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
            ]
    )

    public static final BetsyProcess SCOPE_CORRELATION_SETS_INIT_ASYNC = builder.buildScopeProcess(
            "Scope-CorrelationSets-InitAsync", "A scope with an asynchronous receive which initiates the correlation set and a receive-reply pair, as well as a scope-level definition of a correlationSet that is used by the messaging activities.",
            [
                    new TestCase().checkDeployment().sendAsync(1).sendSync(1, 2)
            ]
    )

    public static final BetsyProcess SCOPE_CORRELATION_INIT_SYNC = builder.buildScopeProcess(
            "Scope-CorrelationSets-InitSync", "A scope with two subsequent receive-reply pairs and a scope-level definition of a correlationSet that is used by the messaging activities.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLERS_ON_ALARM_FOR = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-For", "A receive-reply pair and a process-level onAlarm eventHandler. The receive is followed by a wait that pauses execution for five seconds. The eventHandler waits for two seconds and replies to the receive.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            [
                    new TestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 2)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_FOR = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-For", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after one second, so the handler should repeat exactly once. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            [
                    new TestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 1)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_UNTIL = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-Until", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after a date in the past, so the handler should execute immediately. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            [
                    new TestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 2)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLERS_ON_ALARM_UNTIL = builder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-Until", "A receive followed by a scope with an onAlarm eventHandler and a wait. The onAlarm waits until a date in the past and should therefore execute immediately. Its body contains the reply to the initial receive.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLERS_PARTS = builder.buildScopeProcess(
            "Scope-EventHandlers-Parts", "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. Furthermore, the onMessage uses the fromPart syntax.",
            [
                    new TestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_INIT_ASYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-InitAsync", "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_INIT_SYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-InitSync", "A receive-reply pair followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_ASYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_SYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
            ]
    )


    public static final BetsyProcess SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_ASYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-Scope-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_SYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-Scope-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES = builder.buildScopeProcess(
            "Scope-EventHandlers-FILO-MessageExchanges", "A receive-reply pair marked with messageExchange followed by a wait and a process-level onEvent eventHandler that uses messageExchange with a reply. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES_PICK = builder.buildScopeProcess(
            "Scope-EventHandlers-FILO-MessageExchanges-Pick", "A onMessage-reply pair marked with messageExchange followed by a wait and a process-level onEvent eventHandler that uses messageExchange with a reply. The onMessage initiates a correlationSet on which the onEvent correlates with a synchronous operation.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_ELEMENT_INIT_ASYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-Element-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation, initializing the inputData with a element variable.",
            [
                    new TestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_EVENT_HANDLER_ELEMENT_INIT_SYNC = builder.buildScopeProcess(
            "Scope-EventHandlers-Element-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation, initializing the inputData with a element variable.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 1)
            ]
    )

    public static final List<BetsyProcess> SCOPES_EVENT_HANDLERS = [
            SCOPE_EVENT_HANDLER_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_ELEMENT_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_ELEMENT_INIT_SYNC,
            SCOPE_EVENT_HANDLER_INIT_SYNC,
            SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_SYNC,
            SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES,
            SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES_PICK,
            SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_SYNC,
            SCOPE_EVENT_HANDLERS_ON_ALARM_FOR,
            SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY,
            SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_FOR,
            SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_UNTIL,
            SCOPE_EVENT_HANDLERS_ON_ALARM_UNTIL,
            SCOPE_EVENT_HANDLERS_PARTS
    ].flatten() as List<BetsyProcess>

    public static final BetsyProcess SCOPE_EXIT_ON_STANDARD_FAULT = builder.buildScopeProcess(
            "Scope-ExitOnStandardFault", "A scope with receive-reply pair and an intermediate throw. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes.",
            [
                    new TestCase().checkDeployment().sendSync(5, new ExitAssertion())
            ]
    )

    public static final BetsyProcess SCOPE_EXIT_ON_STANDARD_FAULT_JOIN_FAILURE = builder.buildScopeProcess(
            "Scope-ExitOnStandardFault-JoinFailure", "A scope with a receive-reply pair and an intermediate throw that throws a joinFailure. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes. However, the exitOnStandardFault sematics do not apply to joinFailures.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "joinFailure"))
            ]
    )

    public static final BetsyProcess SCOPE_FAULT_HANDLERS_CATCH_ALL = builder.buildScopeProcess(
            "Scope-FaultHandlers-CatchAll", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_FAULT_HANDLERS_CATCH_ORDER = builder.buildScopeProcess(
            "Scope-FaultHandlers-CatchOrder", "A scope with a receive followed by a intermediate throw. The scope is associated with mulitple faultHandlers. A specific one of these should catch the fault and only inside this faultHandler is the reply to the initial receive. The process is adapted from the example in Spec. 12.5.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public static final BetsyProcess SCOPE_FAULT_HANDLERS_FAULT_ELEMENT = builder.buildScopeProcess(
            "Scope-FaultHandlers-FaultElement", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultElement configuration. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_FAULT_HANDLERS_FAULT_MESSAGE_TYPE = builder.buildScopeProcess(
            "Scope-FaultHandlers-FaultMessageType", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessageType configuration. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public static final BetsyProcess SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA = builder.buildScopeProcess(
            "Scope-FaultHandlers-VariableData", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessage configuration. Inside this faultHandler is the reply to the initial receive and the data replied is the content of the faultVariable.",
            [
                    new TestCase().checkDeployment().sendSync(1, 0)
            ]
    )

    public static final BetsyProcess SCOPE_FAULT_HANDLER = builder.buildScopeProcess(
            "Scope-FaultHandlers", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler by its faultName. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public static final List<BetsyProcess> SCOPES_FAULT_HANDLERS = [
            SCOPE_FAULT_HANDLER,
            SCOPE_FAULT_HANDLERS_CATCH_ALL,
            SCOPE_FAULT_HANDLERS_FAULT_ELEMENT,
            SCOPE_FAULT_HANDLERS_FAULT_MESSAGE_TYPE,
            SCOPE_EXIT_ON_STANDARD_FAULT,
            SCOPE_EXIT_ON_STANDARD_FAULT_JOIN_FAILURE,
            SCOPE_FAULT_HANDLERS_CATCH_ORDER,
            SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA
    ].flatten() as List<BetsyProcess>

    public static final BetsyProcess SCOPE_PARTNER_LINKS = builder.buildProcessWithPartner(
            "scopes/Scope-PartnerLinks", "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public static final BetsyProcess SCOPE_VARIABLES = builder.buildScopeProcess(
            "Scope-Variables", "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public static final BetsyProcess SCOPE_VARIABLES_OVERWRITING = builder.buildScopeProcess(
            "Scope-Variables-Overwriting", "A scope with a receive-reply pair and another nested scope. The nested scope overwrites a variable of the parent scope. Child-level manipulation of this variable should not be visible at the parent scope.",
            [
                    new TestCase().checkDeployment().sendSync(123, 3)
            ]
    )

    public static final BetsyProcess SCOPE_ISOLATED = builder.buildScopeProcess(
            "Scope-Isolated", "A receive-reply pair that encloses a flow with ten isolated scopes which all increment the result by one. As the scopes should not run in parallel, the outcome must be deterministic.",
            [
                    new TestCase().checkDeployment().sendSync(1, 11).sendSync(4, 14).sendSync(123, 133)
            ]
    )

    public static final BetsyProcess SCOPE_TERMINATION_HANDLERS = builder.buildScopeProcess(
            "Scope-TerminationHandlers", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope.",
            [
                    new TestCase().checkDeployment().sendSync(5, -1)
            ]
    )

    public static final BetsyProcess SCOPE_TERMINATION_HANDLERS_FAULT_NOT_PROPAGATING = builder.buildScopeProcess(
            "Scope-TerminationHandlers-FaultNotPropagating", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope. That terminationHandler also throws a fault which should not be propagated.",
            [
                    new TestCase().checkDeployment().sendSync(5, -1)
            ]
    )

    public static final BetsyProcess SCOPE_REPEATABLE_CONSTRUCT_COMPENSATION = builder.buildScopeProcess(
            "Scope-RepeatableConstructCompensation", "A receive followed by a while that contains a scope with a compensationHandler. After the while comes a throw and its fault is caught by the process-level faultHandler. This faultHandler first invokes compensation of all scopes and the replies to the initial receive. The content of the reply depends on the execution of the compensationHandlers.",
            [
                    new TestCase().checkDeployment().sendSync(3, 3)
            ]
    )

    public static final BetsyProcess MISSING_REPLY = builder.buildScopeProcess(
            "MissingReply", "A receive for a synchronous operation with no associated reply.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "missingReply"))
            ]
    )

    public static final BetsyProcess MISSING_REQUEST = builder.buildScopeProcess(
            "MissingRequest", "A receive and a reply which belong to different messageExchanges. On the execution of the reply, a missingRequest fault should be thrown.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "missingRequest"))
            ]
    )

    public static final List<BetsyProcess> SCOPES = [
            COMPENSATE,
            COMPENSATE_SCOPE,
            SCOPE_COMPLEX_COMPENSATION,
            SCOPE_REPEATED_COMPENSATION,
            SCOPE_CORRELATION_SETS_INIT_ASYNC,
            SCOPE_CORRELATION_INIT_SYNC,
            SCOPES_EVENT_HANDLERS,
            SCOPES_FAULT_HANDLERS,
            SCOPE_MESSAGE_EXCHANGES,
            SCOPE_MULTIPLE_MESSAGE_EXCHANGES,
            SCOPE_PARTNER_LINKS,
            SCOPE_VARIABLES,
            SCOPE_VARIABLES_OVERWRITING,
            SCOPE_ISOLATED,
            SCOPE_TERMINATION_HANDLERS,
            SCOPE_TERMINATION_HANDLERS_FAULT_NOT_PROPAGATING,
            SCOPE_REPEATABLE_CONSTRUCT_COMPENSATION,
            MISSING_REPLY,
            MISSING_REQUEST
    ].flatten() as List<BetsyProcess>
}