package configuration.bpel;

import java.util.Arrays;
import java.util.List;

import betsy.bpel.model.BPELTestCase;
import betsy.common.util.CollectionsUtil;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.assertions.AssertExit;
import pebl.benchmark.test.assertions.AssertSoapFault;

class ScopeProcesses {

    private static final FeatureSet TERMINATION_HANDLERS_CONSTRUCT = new FeatureSet(Groups.SCOPES, "TerminationHandlers", "Termination handlers provide the ability for scopes to control the semantics of forced termination to some degree. (p. 136, BPEL)");
    private static final FeatureSet VARIABLES_CONSTRUCT = new FeatureSet(Groups.SCOPES, "Variables", "Variables can be attached to <scope> or <process> elements.");
    private static final FeatureSet PARTNER_LINKS_CONSTRUCT = new FeatureSet(Groups.SCOPES, "PartnerLinks", "The notion of <partnerLinks> is used to directly model peer-to-peer conversational partner relationships. (p. 36, BPEL)");
    private static final FeatureSet FAULT_HANDLERS_CONSTRUCT = new FeatureSet(Groups.SCOPES, "FaultHandlers", "Fault handling in a business process can be thought of as a mode switch from the normal processing in a scope. Fault handling in WS-BPEL is designed to be treated as \"reverse work,\" in that its aim is to undo the partial and unsuccessful work of a scope in which a fault has occurred. (p. 127, BPEL)");
    private static final FeatureSet SCOPE_ATTRIBUTES_CONSTRUCT = new FeatureSet(Groups.SCOPES, "Scope-Attributes", "All attributes of <scope> or <process>, being exitOnStandardFault and isolated");
    private static final FeatureSet EVENT_HANDLERS_CONSTRUCT = new FeatureSet(Groups.SCOPES, "EventHandlers", "Each scope, including the process scope, can have a set of event handlers. These event handlers can run concurrently and are invoked when the corresponding event occurs. (p. 137, BPEL)");
    private static final FeatureSet CORRELATION_SETS_CONSTRUCT = new FeatureSet(Groups.SCOPES, "CorrelationSets", "Correlation can be used on every messaging activity and are defined on <scope> or <process>. They ensure that the messages are routed to the process instance where the correlation set matches.");
    private static final FeatureSet MESSAGE_EXCHANGES_CONSTRUCT = new FeatureSet(Groups.SCOPES, "MessageExchanges", "Manages message exchanges consisting of inbound message activities (IMA) and optional replies.");
    private static final FeatureSet COMPENSATION_CONSTRUCT = new FeatureSet(Groups.SCOPES, "Compensation", "The ability to declare compensation logic alongside forward-working logic is the underpinning of the application-controlled error-handling framework of WS-BPEL. (p. 118, BPEL)");

    public static final Test COMPENSATE = BPELProcessBuilder.buildScopeProcess(
            "Scope-Compensate", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensate.",
            new Feature(COMPENSATION_CONSTRUCT, "Scope-Compensate"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );
    public static final Test COMPENSATE_FLOW = BPELProcessBuilder.buildScopeProcess(
            "Scope-Compensate-Flow", "A scope with a receive-reply pair where the reply data assignment and the reply are linked in a flow nested in compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensate.",
            new Feature(COMPENSATION_CONSTRUCT, "Scope-Compensate-Flow"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test COMPENSATE_SCOPE = BPELProcessBuilder.buildScopeProcess(
            "Scope-CompensateScope", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw and the compensationHandler is invoked from the process-level faultHandler that catches the fault using compensateScope.",
            new Feature(COMPENSATION_CONSTRUCT, "Scope-CompensateScope"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test SCOPE_REPEATED_COMPENSATION = BPELProcessBuilder.buildScopeProcess(
            "Scope-RepeatedCompensation", "A scope with a receive-reply pair where the reply is located in a compensationHandler. The scope is followed by a throw. The process-level faultHandler that catches the fault contains two subsequent compensates the second of which should be treated as empty.",
            new Feature(COMPENSATION_CONSTRUCT, "Scope-RepeatedCompensation"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );
    public static final Test SCOPE_COMPLEX_COMPENSATION = BPELProcessBuilder.buildScopeProcess(
            "Scope-ComplexCompensation", "Complex scope compensation test case that implements the scenario described in Sec. 12.4.2.",
            new Feature(COMPENSATION_CONSTRUCT, "Scope-ComplexCompensation"),
            new BPELTestCase().checkDeployment().sendSync(1, 3)
    );

    public static final Test SCOPE_MESSAGE_EXCHANGES = BPELProcessBuilder.buildScopeProcess(
            "Scope-MessageExchanges", "A scope with a receive-reply pair and a scope-level definition of messageExchanges.",
            new Feature(MESSAGE_EXCHANGES_CONSTRUCT, "Scope-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test SCOPE_MULTIPLE_MESSAGE_EXCHANGES = BPELProcessBuilder.buildScopeProcess(
            "Scope-Multiple-MessageExchanges", "A scope with a receive-reply pair followed by a receive-reply pair of the same operation that use scope-level definition of messageExchanges to define which reply belongs to which receive and the response is the initial value first then the sum of the received values.",
            new Feature(MESSAGE_EXCHANGES_CONSTRUCT, "Scope-Multiple-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test SCOPE_CORRELATION_SETS_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-CorrelationSets-InitAsync", "A scope with an asynchronous receive which initiates the correlation set and a receive-reply pair, as well as a scope-level definition of a correlationSet that is used by the messaging activities.",
            new Feature(CORRELATION_SETS_CONSTRUCT, "Scope-CorrelationSets-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(1).sendSync(1, 2)
    );

    public static final Test SCOPE_CORRELATION_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-CorrelationSets-InitSync", "A scope with two subsequent receive-reply pairs and a scope-level definition of a correlationSet that is used by the messaging activities.",
            new Feature(CORRELATION_SETS_CONSTRUCT, "Scope-CorrelationSets-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test SCOPE_EVENT_HANDLERS_ON_ALARM_FOR = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-For", "A receive-reply pair and a process-level onAlarm eventHandler. The receive is followed by a wait that pauses execution for five seconds. The eventHandler waits for two seconds and replies to the receive.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-OnAlarm-For"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-OnAlarm-RepeatEvery"),
            new BPELTestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 2)
    );

    public static final Test SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_FOR = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-For", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after one second, so the handler should repeat exactly once. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-OnAlarm-RepeatEvery-For"),
            new BPELTestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 1)
    );

    public static final Test SCOPE_EVENT_HANDLERS_ON_ALARM_REPEAT_EVERY_UNTIL = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-RepeatEvery-Until", "A receive-reply pair with an intermediate wait and a process-level onAlarm eventHandler. The eventHandler repeats execution every second and adds one to the final result. The repetition takes place after a date in the past, so the handler should execute immediately. The intermediate wait pauses execution for 2.2 seconds, after which the current result is replied.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-OnAlarm-RepeatEvery-Until"),
            new BPELTestCase().checkDeployment().buildSyncOperationOutputAsLeast(5, 2)
    );

    public static final Test SCOPE_EVENT_HANDLERS_ON_ALARM_UNTIL = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-OnAlarm-Until", "A receive followed by a scope with an onAlarm eventHandler and a wait. The onAlarm waits until a date in the past and should therefore execute immediately. Its body contains the reply to the initial receive.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-OnAlarm-Until"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLERS_PARTS = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Parts", "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. Furthermore, the onMessage uses the fromPart syntax.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Parts"),
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLER_ASYNC_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Async-InitSync", "A receive-reply pair followed by a wait in a scope and an onEvent eventHandler on this level. A second receive-reply pair which responses the 'event' (initialized in the onEvent), follows the scope. The first receive initiates a correlationSet on which the onEvent correlates with an asynchronous operation and the second receive correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Async-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 2).waitFor(3000).sendAsync(1).sendSyncString(1, "event")
    );

    public static final Test SCOPE_EVENT_HANDLER_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-InitAsync", "An asynchronous receive followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLER_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-InitSync", "A receive-reply pair followed by a wait and a process-level onMessage eventHandler. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final Test SCOPE_EVENT_HANDLER_FLOW_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Flow-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation. The onEvent contains a assign linked to a reply in a flow.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Flow-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLER_FLOW_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Flow-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation. The onEvent contains a assign linked to a reply in a flow.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Flow-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final Test SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-MessageExchange-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLER_MESSAGE_EXCHANGE_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-MessageExchange-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final Test SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Scope-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Scope-MessageExchange-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static
    final Test SCOPE_EVENT_HANDLER_INTERNAL_MESSAGE_EXCHANGE_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Internal-MessageExchange-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. The messageExchange is defined in the associated scope.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Internal-MessageExchange-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLER_INTERNAL_MESSAGE_EXCHANGE_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Internal-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation. The messageExchange is defined in the associated scope.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Internal-MessageExchange-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final Test SCOPE_EVENT_HANDLER_SCOPE_MESSAGE_EXCHANGE_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Scope-MessageExchange-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler that uses messageExchange in a scope. The receive initiates a correlationSet on which the onMessage correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Scope-MessageExchange-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 2)
    );

    public static final Test SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-FILO-MessageExchanges", "A receive-reply pair marked with messageExchange followed by a wait and a process-level onEvent eventHandler that uses messageExchange with a reply. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-FILO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );
    public static final Test SCOPE_EVENT_HANDLER_FILO_MESSAGE_EXCHANGES_PICK = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-FILO-MessageExchanges-Pick", "A onMessage-reply pair marked with messageExchange followed by a wait and a process-level onEvent eventHandler that uses messageExchange with a reply. The onMessage initiates a correlationSet on which the onEvent correlates with a synchronous operation.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-FILO-MessageExchanges-Pick"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test SCOPE_EVENT_HANDLER_ELEMENT_INIT_ASYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Element-InitAsync", "An asynchronous receive followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation, initializing the inputData with a element variable.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Element-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(5).sendSync(5, 5)
    );

    public static final Test SCOPE_EVENT_HANDLER_ELEMENT_INIT_SYNC = BPELProcessBuilder.buildScopeProcess(
            "Scope-EventHandlers-Element-InitSync", "A receive-reply pair followed by a wait and a process-level onEvent eventHandler. The receive initiates a correlationSet on which the onEvent correlates with a synchronous operation, initializing the inputData with a element variable.",
            new Feature(EVENT_HANDLERS_CONSTRUCT, "Scope-EventHandlers-Element-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).waitFor(3000).sendSync(1, 1)
    );
    public static final List<Test> SCOPES_EVENT_HANDLERS = Arrays.asList(
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

    public static final Test SCOPE_EXIT_ON_STANDARD_FAULT = BPELProcessBuilder.buildScopeProcess(
            "Scope-ExitOnStandardFault", "A scope with receive-reply pair and an intermediate throw. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes.",
            new Feature(SCOPE_ATTRIBUTES_CONSTRUCT, "Scope-ExitOnStandardFault"),
            new BPELTestCase().checkDeployment().sendSync(5, new AssertExit())
    );

    public static final Test SCOPE_EXIT_ON_STANDARD_FAULT_JOIN_FAILURE = BPELProcessBuilder.buildScopeProcess(
            "Scope-ExitOnStandardFault-JoinFailure", "A scope with a receive-reply pair and an intermediate throw that throws a joinFailure. There is no faultHandler, but the exitOnStandardFault attribute of the scope is set to yes. However, the exitOnStandardFault sematics do not apply to joinFailures.",
            new Feature(SCOPE_ATTRIBUTES_CONSTRUCT, "Scope-ExitOnStandardFault-JoinFailure"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("joinFailure"))
    );

    public static final Test SCOPE_FAULT_HANDLERS_CATCH_ALL = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-CatchAll", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-CatchAll"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE = BPELProcessBuilder.buildScopeProcessWithPartner(
            "Scope-FaultHandlers-CatchAll-Invoke", "A receive followed by a scope with fault handlers and an invoke activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-CatchAll-Invoke"),
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1)
    );

    public static final Test SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE_VALIDATE = BPELProcessBuilder.buildScopeProcessWithPartner(
            // only used for error processes. but may also be used as a test
            "Scope-FaultHandlers-CatchAll-Invoke-Validate", "A receive followed by a scope with fault handlers and an invoke as well as a validate activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-CatchAll-Invoke-Validate"),
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1)
    );

    public static final Test SCOPE_FAULT_HANDLERS_OUTBOUND_LINK_CATCH_ALL = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-OutboundLink-CatchAll", "A scope in a flow with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level catchAll faultHandler. Inside this faultHandler is a assign that is linked outbound to the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-OutboundLink-CatchAll"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test PROCESS_FAULT_HANDLERS_CATCH_ORDER = BPELProcessBuilder.buildScopeProcess(
            "Process-FaultHandlers-CatchOrder", "A process with a receive followed by a intermediate throw. The scope is associated with mulitple faultHandlers. A specific one of these should catch the fault and only inside this faultHandler is the reply to the initial receive. The process is adapted from the example in Spec. 12.5.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Process-FaultHandlers-CatchOrder"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test SCOPE_FAULT_HANDLERS_CATCH_ORDER = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-CatchOrder", "A scope with a receive followed by a intermediate throw. The scope is associated with mulitple faultHandlers. A specific one of these should catch the fault and only inside this faultHandler is the reply to the initial receive. The process is adapted from the example in Spec. 12.5.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-CatchOrder"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test PROCESS_FAULT_HANDLERS_FAULT_ELEMENT = BPELProcessBuilder.buildScopeProcess(
            "Process-FaultHandlers-FaultElement", "A process with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultElement configuration. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Process-FaultHandlers-FaultElement"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test SCOPE_FAULT_HANDLERS_FAULT_ELEMENT = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-FaultElement", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultElement configuration. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-FaultElement"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test SCOPE_FAULT_HANDLERS_FAULT_MESSAGE_TYPE = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-FaultMessageType", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessageType configuration. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-FaultMessageType"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-VariableData", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler that uses a faultVariable and faultMessage configuration. Inside this faultHandler is the reply to the initial receive and the data replied is the content of the faultVariable.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-VariableData"),
            new BPELTestCase().checkDeployment().sendSync(1, 0)
    );

    public static final Test SCOPE_FAULT_HANDLER = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers", "A scope with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler by its faultName. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test SCOPE_FAULT_HANDLER_INVOKE = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-Invoke",   "A scope with a receive followed by a intermediate invoke of a service which replies with a fault. The fault that is returned from the invocation is caught by the scope-level faultHandler by its faultName. Inside this faultHandler is the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-Invoke"),
            new BPELTestCase().checkDeployment().sendSync(-5, -5)
    );

    public static final Test SCOPE_FAULT_HANDLER_OUTBOUND_LINK = BPELProcessBuilder.buildScopeProcess(
            "Scope-FaultHandlers-OutboundLink", "A scope in a flow with a receive followed by a intermediate throw. The fault that is thrown is caught by the scope-level faultHandler by its faultName.  Inside this faultHandler is a assign that is linked outbound to the reply to the initial receive.",
            new Feature(FAULT_HANDLERS_CONSTRUCT, "Scope-FaultHandlers-OutboundLink"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final List<Test> SCOPES_FAULT_HANDLERS = Arrays.asList(
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
            SCOPE_FAULT_HANDLER_INVOKE,
            SCOPE_FAULT_HANDLERS_FAULT_VARIABLE_DATA
    );
    public static final Test SCOPE_PARTNER_LINKS = BPELProcessBuilder.buildScopeProcessWithPartner(
            "Scope-PartnerLinks", "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            new Feature(PARTNER_LINKS_CONSTRUCT, "Scope-PartnerLinks"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test SCOPE_VARIABLES = BPELProcessBuilder.buildScopeProcess(
            "Scope-Variables", "A scope with a receive-reply pair and an intermediate invoke. The partnerLink which is invoked is defined at scope-level.",
            new Feature(VARIABLES_CONSTRUCT, "Scope-Variables"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test SCOPE_VARIABLES_OVERWRITING = BPELProcessBuilder.buildScopeProcess(
            "Scope-Variables-Overwriting", "A scope with a receive-reply pair and another nested scope. The nested scope overwrites a variable of the parent scope. Child-level manipulation of this variable should not be visible at the parent scope.",
            new Feature(VARIABLES_CONSTRUCT, "Scope-Variables-Overwriting"),
            new BPELTestCase().checkDeployment().sendSync(123, 3)
    );

    public static final Test SCOPE_ISOLATED = BPELProcessBuilder.buildScopeProcess(
            "Scope-Isolated", "A receive-reply pair that encloses a flow with ten isolated scopes which all increment the result by one. As the scopes should not run in parallel, the outcome must be deterministic.",
            new Feature(SCOPE_ATTRIBUTES_CONSTRUCT, "Scope-Isolated"),
            new BPELTestCase().checkDeployment().sendSync(1, 11).sendSync(4, 14).sendSync(123, 133)
    );
    public static final Test SCOPE_TERMINATION_HANDLERS = BPELProcessBuilder.buildScopeProcess(
            "Scope-TerminationHandlers", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope.",
            new Feature(TERMINATION_HANDLERS_CONSTRUCT, "Scope-TerminationHandlers"),
            new BPELTestCase().checkDeployment().sendSync(5, -1)
    );

    public static final Test SCOPE_TERMINATION_HANDLERS_OUTBOUND_LINK = BPELProcessBuilder.buildScopeProcess(
            "Scope-TerminationHandlers-OutboundLink", "A receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope. The input value is assigned the reply first, in the terminationHandler -1 is assigned to it and in th outbound linked assign -2.",
            new Feature(TERMINATION_HANDLERS_CONSTRUCT, "Scope-TerminationHandlers-OutboundLink"),
            new BPELTestCase().checkDeployment().sendSync(5, -2)
    );

    public static final Test SCOPE_TERMINATION_HANDLERS_FAULT_NOT_PROPAGATING = BPELProcessBuilder.buildScopeProcess(
            "Scope-TerminationHandlers-FaultNotPropagating", "A scope with a receive-reply pair and a nested scope in between. That scope in turn contains a flow with two parallel scopes. Both scopes pause execution for a short period. The scope that resumes execution first throws a fault caught by the faultHandler of its parent scope. The should trigger the execution of the terminationHandler of its sibling scope. That terminationHandler also throws a fault which should not be propagated.",
            new Feature(TERMINATION_HANDLERS_CONSTRUCT, "Scope-TerminationHandlers-FaultNotPropagating"),
            new BPELTestCase().checkDeployment().sendSync(5, -1)
    );

    public static final Test SCOPE_REPEATABLE_CONSTRUCT_COMPENSATION = BPELProcessBuilder.buildScopeProcess(
            "Scope-RepeatableConstructCompensation", "A receive followed by a while that contains a scope with a compensationHandler. After the while comes a throw and its fault is caught by the process-level faultHandler. This faultHandler first invokes compensation of all scopes and the replies to the initial receive. The content of the reply depends on the execution of the compensationHandlers.",
            new Feature(COMPENSATION_CONSTRUCT, "Scope-RepeatableConstructCompensation"),
            new BPELTestCase().checkDeployment().sendSync(3, 3)
    );

    public static final Test MISSING_REPLY = BPELProcessBuilder.buildScopeProcess(
            "MissingReply", "A receive for a synchronous operation with no associated reply.",
            new Feature(MESSAGE_EXCHANGES_CONSTRUCT, "MissingReply"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("missingReply"))
    );

    public static final Test MISSING_REQUEST = BPELProcessBuilder.buildScopeProcess(
            "MissingRequest", "A receive and a reply which belong to different messageExchanges. On the execution of the reply, a missingRequest fault should be thrown.",
            new Feature(MESSAGE_EXCHANGES_CONSTRUCT, "MissingRequest"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("missingRequest"))
    );

    public static final List<Test> SCOPES = CollectionsUtil.union(Arrays.asList(
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
