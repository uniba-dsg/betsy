package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.bpel.model.assertions.ExitAssertion;
import betsy.bpel.model.assertions.SoapFaultTestAssertion;
import betsy.common.util.CollectionsUtil;

import java.util.Arrays;
import java.util.List;

class ScopeProcesses {

    public static final BPELProcess COMPENSATE = BPELProcessBuilder.buildScopeProcess(
            "Scope-Compensate", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensate.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess COMPENSATE_FLOW = BPELProcessBuilder.buildScopeProcess(
            "Scope-Compensate-Flow", "A scope with a receive-reply pair where the reply data assignment and the reply are linked in a flow nested in compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensate.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess COMPENSATE_SCOPE = BPELProcessBuilder.buildScopeProcess(
            "Scope-CompensateScope", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensateScope.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess SCOPE_REPEATED_COMPENSATION = BPELProcessBuilder.buildScopeProcess(
            "Scope-RepeatedCompensation", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw. The process-level faultHandler that catches the fault contains two subsequent compensates the second of which should be treated as empty.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess SCOPE_COMPLEX_COMPENSATION = BPELProcessBuilder.buildScopeProcess(
            "Scope-ComplexCompensation", "Complex scope compensation test case that implements the scenario described in Sec. 12.4.2.",
            new BPELTestCase().checkDeployment().sendSync(1, 3)
    );

    public static final BPELProcess SCOPE_MESSAGE_EXCHANGES = BPELProcessBuilder.buildScopeProcess(
            "Scope-MessageExchanges", "A scope with a receive-reply pair and a scope-level definition of messageExchanges.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess SCOPE_MULTIPLE_MESSAGE_EXCHANGES = BPELProcessBuilder.buildScopeProcess(
            "Scope-Multiple-MessageExchanges", "A scope with a receive-reply pair followed by a receive-reply pair of the same operation that use scope-level definition of messageExchanges to define which reply belongs to which receive and the response is the initial value first then the sum of the received values.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_CORRELATION_SETS_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-CorrelationSets-InitAsync", "A scope with an asynchronous receive which initiates the correlation set and a receive-reply pair, as well as a scope-level definition of a correlationSet that is used by the messaging activities.",
            new BPELTestCase().checkDeployment().sendAsync(1).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_CORRELATION_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-CorrelationSets-InitSync", "A scope with two subsequent receive-reply pairs and a scope-level definition of a correlationSet that is used by the messaging activities.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLERS_ON_ALARM_FOR = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-For", "A receive-reply pair and a process-level onAlarm eventHandler. The receive is followed by a wait that pauses execution for five seconds. The eventHandler waits for two seconds and replies to the receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            new BPELTestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_FOR = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-For", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after one second, so the handler should repeat exactly once. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            new BPELTestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 1)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_UNTIL = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-Until", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after a date in the past, so the handler should execute immediately. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            new BPELTestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLERS_ON_ALARM_UNTIL = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-Until", "A receive followed by a scope with an onAlarm eventHandler and a wait. The onAlarm waits until a date in the past and should therefore execute immediately. Its body contains the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLERS_PARTS = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Parts", "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. Furthermore, the onMessage uses the fromPart syntax.",
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_ASYNC_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Async-InitSync", "A receive-reply pair followed by a wait in a scope and an onEvent eventHandler on this level. A second receive-reply pair which responses the 'event' (initialized in the onEvent), follows the scope. The first receive initiates a correlationSet on which the onEvent correlates with an asynchronous operation and the second receive correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendSync(1, 2).waitFor(3000).sendAsync(1).sendSyncString(1, "event")
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-InitAsync", "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-InitSync", "A receive-reply pair followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_FLOW_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Flow-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation. The onEvent contains a assign linked to a reply in a flow.",
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_FLOW_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Flow-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation. The onEvent contains a assign linked to a reply in a flow.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Scope-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static
    final BPELProcess SCOPE_EVENT_HANDLER_INTERNAL_MESSAGE_EXCHANGE_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Internal-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. The messageExchange is defined in the associated scope.",
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_INTERNAL_MESSAGE_EXCHANGE_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Internal-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. The messageExchange is defined in the associated scope.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Scope-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-FILO-MessageExchanges", "A receive-reply pair marked with messageExchange followed by a wait and a process-level onEvent eventHandler that uses messageExchange with a reply. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES_PICK = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-FILO-MessageExchanges-Pick", "A onMessage-reply pair marked with messageExchange followed by a wait and a process-level onEvent eventHandler that uses messageExchange with a reply. The onMessage initiates a correlationSet on which the onEvent correlates with a synchronous operation.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_ELEMENT_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Element-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation, initializing the inputData with a element variable.",
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_EVENT_HANDLER_ELEMENT_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Element-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation, initializing the inputData with a element variable.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 1)
    );

    public static final List<BPELProcess> SCOPES_EVENT_HANDLERS = Arrays.asList(
            SCOPE_EVENT_HANDLER_ASYNC_INIT_SYNC,
            SCOPE_EVENT_HANDLER_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_ELEMENT_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_ELEMENT_INIT_SYNC,
            SCOPE_EVENT_HANDLER_INIT_SYNC,
            SCOPE_EVENT_HANDLER_FLOW_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_FLOW_INIT_SYNC,
            SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_SYNC,
            SCOPE_EVENT_HANDLER_INTERNAL_MESSAGE_EXCHANGE_INIT_ASYNC,
            SCOPE_EVENT_HANDLER_INTERNAL_MESSAGE_EXCHANGE_INIT_SYNC,
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
    );

    public static final BPELProcess SCOPE_EXIT_ON_STANDARD_FAULT = BPELProcessBuilder.buildScopeProcess(
            "Scope-ExitOnStandardFault", "A scope with receive-reply pair and an intermediate throw. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes.",
            new BPELTestCase().checkDeployment().sendSync(5, new ExitAssertion())
    );

    public static final BPELProcess SCOPE_EXIT_ON_STANDARD_FAULT_JOIN_FAILURE = BPELProcessBuilder.buildScopeProcess(
            "Scope-ExitOnStandardFault-JoinFailure", "A scope with a receive-reply pair and an intermediate throw that throws a joinFailure. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes. However, the exitOnStandardFault sematics do not apply to joinFailures.",
            new BPELTestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion("joinFailure"))
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_CATCH_ALL = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-CatchAll", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE = BPELProcessBuilder.buildScopeProcessWithPartner(
            "scopes/Scope-FaultHandlers-CatchAll-Invoke", "A receive followed by a scope with fault handlers and an invoke activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE_VALIDATE = BPELProcessBuilder.buildScopeProcessWithPartner(
            // only used for error processes. but may also be used as a test
            "scopes/Scope-FaultHandlers-CatchAll-Invoke-Validate", "A receive followed by a scope with fault handlers and an invoke as well as a validate activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_OUTBOUND_LINK_CATCH_ALL = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-OutboundLink-CatchAll", "A scope in a flow with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level catchAll faultHandler. Inside this faultHandler is a assign that is linked outbound to the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess PROCESS_FAULT_HANDLERS_CATCH_ORDER = BPELProcessBuilder.buildScopeProcess(
            "Process-FaultHandlers-CatchOrder", "A process with a receive followed by a intermediate throw. The scope is associated with mulitple faultHandlers. A specific one of these should catch the fault and only inside this faultHandler is the reply to the initial receive. The process is adapted from the example in Spec. 12.5.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_CATCH_ORDER = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-CatchOrder", "A scope with a receive followed by a intermediate throw. The scope is associated with mulitple faultHandlers. A specific one of these should catch the fault and only inside this faultHandler is the reply to the initial receive. The process is adapted from the example in Spec. 12.5.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess PROCESS_FAULT_HANDLERS_FAULT_ELEMENT = BPELProcessBuilder.buildScopeProcess(
            "Process-FaultHandlers-FaultElement", "A process with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultElement configuration. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_FAULT_ELEMENT = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-FaultElement", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultElement configuration. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_FAULT_MESSAGE_TYPE = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-FaultMessageType", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessageType configuration. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-VariableData", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessage configuration. Inside this faultHandler is the reply to the initial receive and the data replied is the content of the faultVariable.",
            new BPELTestCase().checkDeployment().sendSync(1, 0)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLER = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler by its faultName. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess SCOPE_FAULT_HANDLER_OUTBOUND_LINK = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-OutboundLink", "A scope in a flow with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler by its faultName.  Inside this faultHandler is a assign that is linked outbound to the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final List<BPELProcess> SCOPES_FAULT_HANDLERS = Arrays.asList(
            SCOPE_FAULT_HANDLER,
            SCOPE_FAULT_HANDLER_OUTBOUND_LINK,
            SCOPE_FAULT_HANDLERS_CATCH_ALL,
            SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE,
            SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE_VALIDATE,
            SCOPE_FAULT_HANDLERS_OUTBOUND_LINK_CATCH_ALL,
            PROCESS_FAULT_HANDLERS_FAULT_ELEMENT,
            SCOPE_FAULT_HANDLERS_FAULT_ELEMENT,
            SCOPE_FAULT_HANDLERS_FAULT_MESSAGE_TYPE,
            SCOPE_EXIT_ON_STANDARD_FAULT,
            SCOPE_EXIT_ON_STANDARD_FAULT_JOIN_FAILURE,
            PROCESS_FAULT_HANDLERS_CATCH_ORDER,
            SCOPE_FAULT_HANDLERS_CATCH_ORDER,
            SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA
    );

    public static final BPELProcess SCOPE_PARTNER_LINKS = BPELProcessBuilder.buildScopeProcessWithPartner(
            "scopes/Scope-PartnerLinks", "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess SCOPE_VARIABLES = BPELProcessBuilder.buildScopeProcess(
            "Scope-Variables", "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess SCOPE_VARIABLES_OVERWRITING = BPELProcessBuilder.buildScopeProcess(
            "Scope-Variables-Overwriting", "A scope with a receive-reply pair and another nested scope. The nested scope overwrites a variable of the parent scope. Child-level manipulation of this variable should not be visible at the parent scope.",
            new BPELTestCase().checkDeployment().sendSync(123, 3)
    );

    public static final BPELProcess SCOPE_ISOLATED = BPELProcessBuilder.buildScopeProcess(
            "Scope-Isolated", "A receive-reply pair that encloses a flow with ten isolated scopes which all increment the result by one. As the scopes should not run in parallel, the outcome must be deterministic.",
            new BPELTestCase().checkDeployment().sendSync(1, 11).sendSync(4, 14).sendSync(123, 133)
    );

    public static final BPELProcess SCOPE_TERMINATION_HANDLERS = BPELProcessBuilder.buildScopeProcess(
            "Scope-TerminationHandlers", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope.",
            new BPELTestCase().checkDeployment().sendSync(5, -1)
    );

    public static final BPELProcess SCOPE_TERMINATION_HANDLERS_OUTBOUND_LINK = BPELProcessBuilder.buildScopeProcess(
            "Scope-TerminationHandlers-OutboundLink", "A receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope. The input value is assigned the reply first, in the terminationHandler -1 is assigned to it and in th outbound linked assign -2.",
            new BPELTestCase().checkDeployment().sendSync(5, -2)
    );

    public static final BPELProcess SCOPE_TERMINATION_HANDLERS_FAULT_NOT_PROPAGATING = BPELProcessBuilder.buildScopeProcess(
            "Scope-TerminationHandlers-FaultNotPropagating", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope. That terminationHandler also throws a fault which should not be propagated.",
            new BPELTestCase().checkDeployment().sendSync(5, -1)
    );

    public static final BPELProcess SCOPE_REPEATABLE_CONSTRUCT_COMPENSATION = BPELProcessBuilder.buildScopeProcess(
            "Scope-RepeatableConstructCompensation", "A receive followed by a while that contains a scope with a compensationHandler. After the while comes a throw and its fault is caught by the process-level faultHandler. This faultHandler first invokes compensation of all scopes and the replies to the initial receive. The content of the reply depends on the execution of the compensationHandlers.",
            new BPELTestCase().checkDeployment().sendSync(3, 3)
    );

    public static final BPELProcess MISSING_REPLY = BPELProcessBuilder.buildScopeProcess(
            "MissingReply", "A receive for a synchronous operation with no associated reply.",
            new BPELTestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion("missingReply"))
    );

    public static final BPELProcess MISSING_REQUEST = BPELProcessBuilder.buildScopeProcess(
            "MissingRequest", "A receive and a reply which belong to different messageExchanges. On the execution of the reply, a missingRequest fault should be thrown.",
            new BPELTestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion("missingRequest"))
    );

    public static final List<BPELProcess> SCOPES = CollectionsUtil.union(Arrays.asList(
            Arrays.asList(
                    COMPENSATE,
                    COMPENSATE_FLOW,
                    COMPENSATE_SCOPE,
                    SCOPE_COMPLEX_COMPENSATION,
                    SCOPE_REPEATED_COMPENSATION,
                    SCOPE_CORRELATION_SETS_INIT_ASYNC,
                    SCOPE_CORRELATION_INIT_SYNC,
                    SCOPE_MESSAGE_EXCHANGES,
                    SCOPE_MULTIPLE_MESSAGE_EXCHANGES,
                    SCOPE_PARTNER_LINKS,
                    SCOPE_VARIABLES,
                    SCOPE_VARIABLES_OVERWRITING,
                    SCOPE_ISOLATED,
                    SCOPE_TERMINATION_HANDLERS,
                    SCOPE_TERMINATION_HANDLERS_OUTBOUND_LINK,
                    SCOPE_TERMINATION_HANDLERS_FAULT_NOT_PROPAGATING,
                    SCOPE_REPEATABLE_CONSTRUCT_COMPENSATION,
                    MISSING_REPLY,
                    MISSING_REQUEST
            ),
            SCOPES_EVENT_HANDLERS,
            SCOPES_FAULT_HANDLERS
    ));
}
