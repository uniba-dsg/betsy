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

class BasicActivityProcesses {

    private static final FeatureSet EMPTY_CONSTRUCT = new FeatureSet(Groups.BASIC, "Empty", "An activity which does nothing.");
    private static final FeatureSet EXIT_CONSTRUCT = new FeatureSet(Groups.BASIC, "Exit", "The <exit> activity is used to immediately end the business process instance. (p. 96, BPEL)");
    private static final FeatureSet VALIDATE_CONSTRUCT = new FeatureSet(Groups.BASIC, "Validate", "The <validate> activity can be used to ensure that values of variables are valid against their associated XML data definition, including XML Schema simple type, complex type, element definition and XML definitions of WSDL parts. (p. 48, BPEL)");
    private static final FeatureSet VARIABLES_CONSTRUCT = new FeatureSet(Groups.BASIC, "Variables", "Groups together several features regarding initialization of variables.");
    private static final FeatureSet WAIT_CONSTRUCT = new FeatureSet(Groups.BASIC, "Wait", "The <wait> activity specifies a delay for a certain period of time or until a certain deadline is reached (p. 95, BPEL)");
    private static final FeatureSet THROW_CONSTRUCT = new FeatureSet(Groups.BASIC, "Throw", "The <throw> activity is used when a business process needs to signal an internal fault explicitly. (p. 94, BPEL)");
    private static final FeatureSet RETHROW_CONSTRUCT = new FeatureSet(Groups.BASIC, "Rethrow", "The <rethrow> activity is used in fault handlers to rethrow the fault they caught, i.e. the fault name and, where present, the fault data of the original fault. (p. 96, BPEL)");
    private static final FeatureSet RECEIVE_CONSTRUCT = new FeatureSet(Groups.BASIC, "Receive", "A <receive> is a blocking activity in that it will not complete until a matching message is received by the process instance. (p.91, BPEL)");
    private static final FeatureSet RECEIVE_REPLY_CONSTRUCT = new FeatureSet(Groups.BASIC, "ReceiveReply", "The <reply> activity is used to send a response to a request previously accepted through an inbound message activity such as the <receive> activity. (p. 92, BPEL)");
    private static final FeatureSet INVOKE_CONSTRUCT = new FeatureSet(Groups.BASIC, "Invoke", "The <invoke> activity is used to call Web Services offered by service providers. (p. 84, BPEL)");
    private static final FeatureSet ASSIGN_CONSTRUCT = new FeatureSet(Groups.BASIC, "Assign", "Variable update occurs through the <assign> activity. (p. 94, BPEL)");

    public static final Test EMPTY = BPELProcessBuilder.buildBasicActivityProcess(
            "Empty", "A receive-reply pair with an intermediate empty.",
            new Feature(EMPTY_CONSTRUCT, "Empty"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(5, 5)
    );

    public static final Test EXIT = BPELProcessBuilder.buildBasicActivityProcess(
            "Exit", "A receive-reply pair with an intermediate exit. There should not be a normal response.",
            new Feature(EXIT_CONSTRUCT, "Exit"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, new AssertExit())
    );

    public static final Test VALIDATE = BPELProcessBuilder.buildBasicProcessWithXsd(
            "Validate", "A receive-reply pair with an intermediate variable validation. The variable to be validated describes a month, so only values in the range of 1 and 12 should validate successfully.",
            new Feature(VALIDATE_CONSTRUCT, "Validate"),
            new BPELTestCase("Input Value 13 should return validation fault").checkDeployment().sendSync(13, new AssertSoapFault("invalidVariables"))
    );

    public static final Test VALIDATE_INVALID_VARIABLES = BPELProcessBuilder.buildBasicProcessWithXsd(
            "Validate-InvalidVariables", "A receive-reply pair with an intermediate variable validation. The variable to be validated is of type xs:int and xs:boolean is copied into it.",
            new Feature(VALIDATE_CONSTRUCT, "Validate-InvalidVariables"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, new AssertSoapFault("invalidVariables"))
    );

    public static final Test VARIABLES_UNINITIALIZED_VARIABLE_FAULT_REPLY = BPELProcessBuilder.buildBasicActivityProcess(
            "Variables-UninitializedVariableFault-Reply", "A receive-reply pair where the variable of the reply is not initialized.",
            new Feature(VARIABLES_CONSTRUCT, "Variables-UninitializedVariableFault-Reply"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, new AssertSoapFault("uninitializedVariable"))
    );

    public static final Test VARIABLES_UNINITIALIZED_VARIABLE_FAULT_INVOKE = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Variables-UninitializedVariableFault-Invoke", "A receive-reply pair with intermediate invoke. The inputVariable of the invoke is not initialized.",
            new Feature(VARIABLES_CONSTRUCT, "Variables-UninitializedVariableFault-Invoke"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, new AssertSoapFault("uninitializedVariable"))
    );
    public static final Test VARIABLES_DEFAULT_INITIALIZATION = BPELProcessBuilder.buildBasicActivityProcess(
            "Variables-DefaultInitialization", "A receive-reply pair where the variable of the reply is assigned with a default value.",
            new Feature(VARIABLES_CONSTRUCT, "Variables-DefaultInitialization"),
            new BPELTestCase("DefaultValue-10-Should-Be-Returned").checkDeployment().sendSync(5, 10)
    );

    public static final Test WAIT_FOR = BPELProcessBuilder.buildBasicActivityProcess(
            "Wait-For", "A receive-reply pair with an intermediate wait that pauses execution for five seconds.",
            new Feature(WAIT_CONSTRUCT, "Wait-For"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test WAIT_FOR_INVALID_EXPRESSION_VALUE = BPELProcessBuilder.buildBasicActivityProcess(
            "Wait-For-InvalidExpressionValue", "A receive-reply pair with an intermediate wait. The for element is assigned a value of xs:int, but only xs:duration is allowed.",
            new Feature(WAIT_CONSTRUCT, "Wait-For-InvalidExpressionValue"),
            new BPELTestCase().checkDeployment().sendSync(5, new AssertSoapFault("invalidExpressionValue"))
    );

    public static final Test WAIT_UNTIL = BPELProcessBuilder.buildBasicActivityProcess(
            "Wait-Until", "A receive-reply pair with an intermediate wait that pauses the execution until a date in the past. Therefore, the wait should complete instantly.",
            new Feature(WAIT_CONSTRUCT, "Wait-Until"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final List<Test> BASIC_ACTIVITIES_WAIT = Arrays.asList(
            WAIT_FOR,
            WAIT_FOR_INVALID_EXPRESSION_VALUE,
            WAIT_UNTIL
    );

    public static final Test THROW = BPELProcessBuilder.buildBasicActivityProcess(
            "Throw", "A receive-reply pair with an intermediate throw. The response should a soap fault containing the bpel fault.",
            new Feature(THROW_CONSTRUCT, "Throw"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("completionConditionFailure"))
    );
    public static final Test THROW_WITHOUT_NAMESPACE = BPELProcessBuilder.buildBasicActivityProcess(
            "Throw-WithoutNamespace", "A receive-reply pair with an intermediate throw that uses a bpel fault without explicitly using the bpel namespace. The respone should be a soap fault containing the bpel fault.",
            new Feature(THROW_CONSTRUCT, "Throw-WithoutNamespace"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("completionConditionFailure"))
    );

    public static final Test THROW_CUSTOM_FAULT = BPELProcessBuilder.buildBasicActivityProcess(
            "Throw-CustomFault", "A receive-reply pair with an intermediate throw that throws a custom fault that undefined in the given namespace. The response should be a soap fault containing the custom fault.",
            new Feature(THROW_CONSTRUCT, "Throw-CustomFault"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("testFault"))
    );

    public static final Test THROW_CUSTOM_FAULT_IN_WSDL = BPELProcessBuilder.buildBasicActivityProcess(
            "Throw-CustomFaultInWsdl", "A receive-reply pair with an intermediate throw that throws a custom fault defined in the myRole WSDL. The response should be a soap fault containing the custom fault.",
            new Feature(THROW_CONSTRUCT, "Throw-CustomFaultInWsdl"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("syncFault"))
    );

    public static final Test THROW_FAULT_DATA = BPELProcessBuilder.buildBasicActivityProcess(
            "Throw-FaultData", "A receive-reply pair with an intermediate throw that also uses a faultVariable. The content of the faultVariable should be contained in the response.",
            new Feature(THROW_CONSTRUCT, "Throw-FaultData"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, 1, new AssertSoapFault("completionConditionFailure"))
    );

    public static final Test RETHROW = BPELProcessBuilder.buildBasicActivityProcess(
            "Rethrow",
            "A receive activity with an intermediate throw and a fault handler with a catchAll. The fault handler rethrows the fault.",
            new Feature(RETHROW_CONSTRUCT, "Rethrow"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, new AssertSoapFault("completionConditionFailure"))
    );
    public static final Test RETHROW_FAULT_DATA_UNMODIFIED = BPELProcessBuilder.buildBasicActivityProcess(
            "Rethrow-FaultDataUnmodified",
            "A receive activity with an intermediate throw that uses a faultVariable. A fault handler catches the fault, changes the data, and rethrows the fault. The fault should be the response with unchanged data.",
            new Feature(RETHROW_CONSTRUCT, "Rethrow-FaultDataUnmodified"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, 1, new AssertSoapFault("completionConditionFailure"))
    );

    public static final Test RETHROW_FAULT_DATA = BPELProcessBuilder.buildBasicActivityProcess(
            "Rethrow-FaultData",
            "A receive activity with an intermediate throw that uses a faultVariable. A fault handler catches and rethrows the fault. The fault should be the response along with the data.",
            new Feature(RETHROW_CONSTRUCT, "Rethrow-FaultData"),
            new BPELTestCase().
                    checkDeployment().
                    sendSync(1, 1, new AssertSoapFault("completionConditionFailure"))
    );

    public static final List<Test> BASIC_ACTIVITIES_THROW = Arrays.asList(
            THROW,
            THROW_WITHOUT_NAMESPACE,
            THROW_FAULT_DATA,
            THROW_CUSTOM_FAULT,
            THROW_CUSTOM_FAULT_IN_WSDL,
            RETHROW,
            RETHROW_FAULT_DATA_UNMODIFIED,
            RETHROW_FAULT_DATA
    );

    public static final Test RECEIVE = BPELProcessBuilder.buildBasicActivityProcess(
            "Receive", "A single asynchronous receive.",
            new Feature(RECEIVE_CONSTRUCT, "Receive"),
            new BPELTestCase().checkDeployment().sendAsync(1)
    );

    public static final Test RECEIVE_CORRELATION_INIT_ASYNC = BPELProcessBuilder.buildBasicActivityProcess(
            "Receive-Correlation-InitAsync", "Two asynchronous receives, followed by a receive-reply pair, and bound to a single correlationSet.",
            new Feature(RECEIVE_CONSTRUCT, "Receive-Correlation-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(1).waitFor(1000).sendAsync(1).waitFor(1000).sendSync(1, 1)
    );

    public static final Test RECEIVE_CORRELATION_INIT_SYNC = BPELProcessBuilder.buildBasicActivityProcess(
            "Receive-Correlation-InitSync", "One synchronous receive, one asynchronous receive, followed by a receive-reply pair, and bound to a single correlationSet.",
            new Feature(RECEIVE_CONSTRUCT, "Receive-Correlation-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 0).waitFor(1000).sendAsync(1).waitFor(1000).sendSync(1, 1)
    );
    public static final Test RECEIVE_AMBIGUOUS_RECEIVE_FAULT = BPELProcessBuilder.buildBasicActivityProcess(
            "Receive-AmbiguousReceiveFault", "An asynchronous receive that initiates two correlationSets, followed by a flow with two sequences that contain synchronous receive-reply pairs for the same operation but differnet correlationSets. Should trigger an ambiguousReceive fault.",
            new Feature(RECEIVE_CONSTRUCT, "Receive-AmbiguousReceiveFault"),
            new BPELTestCase().checkDeployment().sendAsync(1).waitFor(1000).sendSync(1, new AssertSoapFault("ambiguousReceive"))
    );

    public static final Test RECEIVE_CONFLICTING_RECEIVE_FAULT = BPELProcessBuilder.buildBasicActivityProcess(
            "Receive-ConflictingReceiveFault", "An asynchronous receive that initiates a correlationSet, followed by a flow with two sequences that contain synchronous receive-reply pair for the same operation and correlationSet. Should trigger a conflictingReceive fault.",
            new Feature(RECEIVE_CONSTRUCT, "Receive-ConflictingReceiveFault"),
            new BPELTestCase().checkDeployment().sendSync(1).waitFor(1000).sendSync(1, new AssertSoapFault("conflictingReceive"))
    );

    public static final Test RECEIVE_REPLY_CONFLICTING_REQUEST_FAULT = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-ConflictingRequestFault", "A synchronous interaction, followed by intermediate while that subsequently enables multiple receives that correspond to a single synchronous message exchange. Should trigger a conflictingRequest fault.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-ConflictingRequestFault"),
            new BPELTestCase().checkDeployment().
                    sendSync(1, 1).waitFor(1000). // start up, should complete normally
                    sendSyncString(1).waitFor(1000). // no reply, also normal, we need to open the message exchange
                    sendSyncString(1, new AssertSoapFault("conflictingRequest")) // now, there should be the fault
    );

    public static final Test RECEIVE_REPLY = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply", "A simple receive-reply pair.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test RECEIVE_REPLY_MESSAGE_EXCHANGES = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-MessageExchanges", "A simple receive-reply pair that uses a messageExchange.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test RECEIVE_REPLY_MULTIPLE_MESSAGE_EXCHANGES = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-Multiple-MessageExchanges", "A receive-reply pair followed by a receive-reply pair of the same operation that use messageExchanges to define which reply belongs to which receive and the response is the initial value first then the sum of the received values.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-Multiple-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test RECEIVE_REPLY_FIFO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-FIFO-MessageExchanges", "Two receives of the same operation that use messageExchanges to define which reply belongs to which receive and the response is 1 for the reply to the first receive and 2 for the second reply to the second receive.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-FIFO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test RECEIVE_REPLY_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-FILO-MessageExchanges", "Two receives of the same operation that use messageExchanges to define which reply belongs to which receive and the response is 2 for the reply to the second receive and 1 for the second reply to the first receive.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-FILO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test RECEIVE_REPLY_CORRELATION_INIT_ASYNC = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-Correlation-InitAsync", "An asynchronous receive that initiates a correlationSet followed by a receive-reply pair that uses this set.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-Correlation-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(5).waitFor(1000).sendSync(5, 5)
    );

    public static final Test RECEIVE_REPLY_CORRELATION_INIT_SYNC = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-Correlation-InitSync", "A synchronous recieve that initiates a correlationSet followed by a receive-reply pair that uses this set.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-Correlation-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(5, 0).waitFor(1000).sendSync(5, 5)
    );

    public static final Test RECEIVE_REPLY_CORRELATION_VIOLATION_NO = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-CorrelationViolation-No", "A receive-reply pair that uses an uninitiated correlationSet and sets initiate to no. Should trigger a correlationViolation fault.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-CorrelationViolation-No"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("correlationViolation"))
    );

    public static final Test RECEIVE_REPLY_CORRELATION_VIOLATION_YES = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-CorrelationViolation-Yes", "Two subsequent receive-reply pairs which share a correlationSet and where both receives have initiate set to yes.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-CorrelationViolation-Yes"),
            new BPELTestCase().checkDeployment().
                    sendSync(1, 1).waitFor(1000).
                    sendSync(1, new AssertSoapFault("correlationViolation"))
    );

    public static final Test RECEIVE_REPLY_CORRELATION_VIOLATION_JOIN = BPELProcessBuilder.buildBasicProcessWithPartner(
            "ReceiveReply-CorrelationViolation-Join", "A receive-reply pair that initates a correlationSet with an intermediate invoke that tries to join the correlationSet. The join operation should only work if the correlationSet was initiate with a certain value.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-CorrelationViolation-Join"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("correlationViolation")),
            new BPELTestCase().checkDeployment().sendSync(2, 2)
    );

    public static final Test RECEIVE_REPLY_FROM_PARTS = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-FromParts", "A receive-reply pair that uses the fromPart syntax instead of a variable.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-FromParts"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test RECEIVE_REPLY_TO_PARTS = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-ToParts", "A receive-reply pair that uses the toPart syntax instead of a variable.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-ToParts"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test RECEIVE_REPLY_FAULT = BPELProcessBuilder.buildBasicActivityProcess(
            "ReceiveReply-Fault", "A receive-reply pair replies with a fault instead of a variable.",
            new Feature(RECEIVE_REPLY_CONSTRUCT, "ReceiveReply-Fault"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("syncFault"))
    );
    public static final List<Test> BASIC_ACTIVITIES_RECEIVE = Arrays.asList(
            RECEIVE,
            RECEIVE_CORRELATION_INIT_ASYNC,
            RECEIVE_CORRELATION_INIT_SYNC,
            RECEIVE_REPLY_MESSAGE_EXCHANGES,
            RECEIVE_REPLY_MULTIPLE_MESSAGE_EXCHANGES,
            RECEIVE_REPLY_FIFO_MESSAGE_EXCHANGES,
            RECEIVE_REPLY_FILO_MESSAGE_EXCHANGES,
            RECEIVE_AMBIGUOUS_RECEIVE_FAULT,
            RECEIVE_CONFLICTING_RECEIVE_FAULT,
            RECEIVE_REPLY_CONFLICTING_REQUEST_FAULT,
            RECEIVE_REPLY_CORRELATION_VIOLATION_NO,
            RECEIVE_REPLY_CORRELATION_VIOLATION_YES,
            RECEIVE_REPLY_CORRELATION_VIOLATION_JOIN,
            RECEIVE_REPLY,
            RECEIVE_REPLY_CORRELATION_INIT_ASYNC,
            RECEIVE_REPLY_CORRELATION_INIT_SYNC,
            RECEIVE_REPLY_FROM_PARTS,
            RECEIVE_REPLY_TO_PARTS,
            RECEIVE_REPLY_FAULT
    );

    public static final Test INVOKE_ASYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Async", "A receive-reply pair with an intermediate asynchronous invoke.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Async"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test INVOKE_SYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Sync", "A receive-reply pair with an intermediate synchronous invoke.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Sync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test INVOKE_SYNC_FAULT = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Sync-Fault", "A receive-reply pair with an intermediate synchronous invoke that should trigger a fault.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Sync-Fault"),
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.UNDECLARED_FAULT, new AssertSoapFault("CustomFault"))
    );

    public static final Test INVOKE_TO_PARTS = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-ToParts", "A receive-reply pair with an intermediate synchronous invoke that uses the toParts syntax.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-ToParts"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test INVOKE_FROM_PARTS = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-FromParts", "A receive-reply pair with an intermediate synchronous invoke that uses the fromParts syntax.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-FromParts"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test INVOKE_EMPTY = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Empty", "A receive-reply pair with an intermediate invoke of an operation that has no message associated with it. No definition of inputVariable or outputVariable is required.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Empty"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test INVOKE_CORRELATION_PATTERN_INIT_ASYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Correlation-Pattern-InitAsync", "An asynchronous receive that initiates a correlationSet used by a subsequent invoke that also uses a request-response pattern and is thereafter followed by receive-reply pair that also uses the correlationSet.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Correlation-Pattern-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(1).waitFor(1000).sendSync(1, 1)
    );

    public static final Test INVOKE_CORRELATION_PATTERN_INIT_SYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Correlation-Pattern-InitSync", "A synchronous receive that initiates a correlationSet used by a subsequent invoke that also uses a request-response pattern and is thereafter followed by receive-reply pair that also uses the correlationSet.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Correlation-Pattern-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 0).waitFor(1000).sendSync(1, 1)
    );

    public static final Test INVOKE_CATCH = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Catch", "A receive-reply pair with an intermediate invoke that results in a fault for certain input, but catches that fault and replies.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Catch"),
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, 0)
    );

    public static final Test INVOKE_CATCH_UNDECLARED_FAULT = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-Catch-UndeclaredFault", "A receive-reply pair with an intermediate invoke that results in a fault for certain input, but catches that fault and replies. The fault is not declared in the Web Service Definition of the partner service.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-Catch-UndeclaredFault"),
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.UNDECLARED_FAULT, 0)
    );

    public static final Test INVOKE_CATCHALL = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-CatchAll", "A receive-reply pair with an intermediate invoke that results in a fault for certain input, but catches all faults and replies.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-CatchAll"),
            new BPELTestCase("Enter-CatchAll").checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1)
    );

    public static final Test INVOKE_CATCHALL_UNDECLARED_FAULT = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-CatchAll-UndeclaredFault", "A receive-reply pair with an intermediate invoke that results in a fault for certain input, but catches all faults and replies.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-CatchAll-UndeclaredFault"),
            new BPELTestCase("Enter-CatchAll").checkDeployment().sendSync(BPELProcessBuilder.UNDECLARED_FAULT, 0)
    );

    public static final Test INVOKE_COMPENSATION_HANDLER = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-CompensationHandler", "A receive-reply pair combined with an invoke that has a compensationHandler, followed by a throw. The fault is caught by the process-level faultHandler. That faultHandler triggers the compensationHandler of the invoke which contains the reply.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-CompensationHandler"),
            new BPELTestCase().checkDeployment().sendSync(1, 0)
    );

    public static final Test INVOKE_COMPENSATE_SCOPE_COMPENSATION_HANDLER = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-CompensateScope-CompensationHandler", "A receive-reply pair combined with an invoke that has a compensationHandler, followed by a throw. The fault is caught by the process-level faultHandler containing a compensateScope. That faultHandler triggers the compensationHandler of the invoke which contains the reply.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-CompensateScope-CompensationHandler"),
            new BPELTestCase().checkDeployment().sendSync(1, 0)
    );

    public static final Test INVOKE_INITIALIZE_PARTNER_ROLE_YES_ASYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-InitializePartnerRole-Yes-Async", "A receive-reply pair with an intermediate asynchronous invoke. The invoke has a partnerLink with initializePartnerRole attribute set to yes.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-InitializePartnerRole-Yes-Async"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test INVOKE_INITIALIZE_PARTNER_ROLE_YES_SYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-InitializePartnerRole-Yes-Sync", "A receive-reply pair with an intermediate synchronous invoke. The invoke has a partnerLink with initializePartnerRole attribute set to yes.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-InitializePartnerRole-Yes-Sync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test INVOKE_INITIALIZE_PARTNER_ROLE_NO_ASYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-InitializePartnerRole-No-Async", "A receive-reply pair with an intermediate asynchronous invoke. The invoke has a partnerLink with initializePartnerRole attribute set to no.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-InitializePartnerRole-No-Async"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test INVOKE_INITIALIZE_PARTNER_ROLE_NO_SYNC = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Invoke-InitializePartnerRole-No-Sync", "A receive-reply pair with an intermediate synchronous invoke. The invoke has a partnerLink with initializePartnerRole attribute set to no.",
            new Feature(INVOKE_CONSTRUCT, "Invoke-InitializePartnerRole-No-Sync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final List<Test> BASIC_ACTIVITIES_INVOKE = Arrays.asList(
            INVOKE_ASYNC,
            INVOKE_SYNC,
            INVOKE_INITIALIZE_PARTNER_ROLE_YES_ASYNC,
            INVOKE_INITIALIZE_PARTNER_ROLE_YES_SYNC,
            INVOKE_INITIALIZE_PARTNER_ROLE_NO_ASYNC,
            INVOKE_INITIALIZE_PARTNER_ROLE_NO_SYNC,
            INVOKE_SYNC_FAULT,
            INVOKE_EMPTY,
            INVOKE_TO_PARTS,
            INVOKE_FROM_PARTS,
            INVOKE_CORRELATION_PATTERN_INIT_ASYNC,
            INVOKE_CORRELATION_PATTERN_INIT_SYNC,
            INVOKE_CATCH,
            INVOKE_CATCH_UNDECLARED_FAULT,
            INVOKE_CATCHALL,
            INVOKE_CATCHALL_UNDECLARED_FAULT,
            INVOKE_COMPENSATE_SCOPE_COMPENSATION_HANDLER,
            INVOKE_COMPENSATION_HANDLER
    );
    public static final Test ASSIGN_VALIDATE = BPELProcessBuilder.buildBasicProcessWithXsd(
            "Assign-Validate", "A receive-reply pair with an intermediate assign that has validate set to yes. The assign copies to a variable that represents a month and the validation should fail for values not in the range of one to twelve.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Validate"),
            new BPELTestCase("Input Value 13 should return validation fault").checkDeployment().
                    sendSync(13, new AssertSoapFault("invalidVariables"))
    );

    public static final Test ASSIGN_PROPERTY = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Property", "A receive-reply pair with an intermediate assign that copies from a property instead of a variable.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Property"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_TO_PROPERTY = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-To-Property", "A receive-reply pair with an intermediate assign that copies to a property instead of a variable.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-To-Property"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_ELEMENT_VARIABLE = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Element-Variable", "A receive-reply pair with an intermediate assign that copies the input to a element variable and from there to the output variable.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Element-Variable"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_PARTNERLINK = BPELProcessBuilder.buildBasicProcessWithRegularAndDummyPartner(
            "Assign-PartnerLink", "A receive-reply pair with an intermediate assign that assigns a WS-A EndpointReference to a partnerLink which is used in a subsequent invoke.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-PartnerLink"),
            new BPELTestCase().checkDeployment().sendSync(5, 0)
    );


    public static final Test ASSIGN_PARTNERLINK_PARTNER_ROLE = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Assign-PartnerLink-PartnerRole", "A receive-reply pair with an intermediate assign that assigns an existing partnerLink to another partnerLink of the same type which is used in a subsequent invoke.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-PartnerLink-PartnerRole"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_PARTNERLINK_UNSUPPORTED_REFERENCE = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Assign-PartnerLink-UnsupportedReference", "A receive-reply pair with an intermediate assign that assigns a bogus reference to a partnerLink which is used in a subsequent invoke. The reference scheme should not be supported by any engine and fail with a corresponding fault.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-PartnerLink-UnsupportedReference"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("unsupportedReference"))
    );

    public static final Test ASSIGN_MISMATCHED_ASSIGNMENT_FAILURE = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-MismatchedAssignmentFailure", "An assignment between two incompatible types. A mismatchedAssignmentFailure should be thrown.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-MismatchedAssignmentFailure"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("mismatchedAssignment"))
    );

    public static final Test ASSIGN_LITERAL = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Literal", "A receive-reply pair with an intermediate assign that copies a literal.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Literal"),
            new BPELTestCase().checkDeployment().sendSync(5, 1)
    );

    public static final Test ASSIGN_EXPRESSION_FROM = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Expression-From", "A receive-reply pair with an intermediate assign that uses an expression in a from element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Expression-From"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_EXPRESSION_TO = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Expression-To", "A receive-reply pair with an intermediate assign that uses an expression in a to element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Expression-To"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_EXPRESSION_LANGUAGE_FROM = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-ExpressionLanguage-From", "A receive-reply pair with an intermediate assign that uses an expression with expressionLanguage declaration in a from element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-ExpressionLanguage-From"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_EXPRESSION_LANGUAGE_TO = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-ExpressionLanguage-To", "A receive-reply pair with an intermediate assign that uses an expression with expressionLanguage declaration in a to element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-ExpressionLanguage-To"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_INT = BPELProcessBuilder.buildBasicProcessWithPartner(
            "Assign-Int", "A receive-reply pair combined with an assign and an invoke in between. The assign copies an int value as an expression to the inputVariable of the invoke. The invocation fails if the value copied is not an int (but, for instance, a float).",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Int"),
            new BPELTestCase().checkDeployment().sendSync(1, 10)
    );

    public static final Test ASSIGN_SELECTION_FAILURE = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-SelectionFailure", "A receive-reply pair with an intermediate assign that uses a from that retuns zero nodes. This should trigger a selectionFailure.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-SelectionFailure"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("selectionFailure"))
    );

    public static final Test ASSIGN_COPY_QUERY = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Copy-Query", "A process with a receive-reply pair with an intermediate assign that uses a query in a from element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-Query"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_COPY_QUERY_LANGUAGE = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Copy-QueryLanguage", "A process with a receive-reply pair with an intermediate assign that uses a query with explicit language declaration in a from element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-QueryLanguage"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_TO_QUERY = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-To-Query", "A process with a receive-reply pair with an intermediate assign that uses a query in a to element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-To-Query"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_TO_QUERY_LANGUAGE = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-To-QueryLanguage", "A process with a receive-reply pair with an intermediate assign that uses a query with explicit language declaration in a to element.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-To-QueryLanguage"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_COPY_KEEP_SRC_ELEMENT_NAME = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Copy-KeepSrcElementName", "A receive-reply pair with an intermediate assign with a copy that has keepSrcElementName set to yes. This should trigger a fault.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-KeepSrcElementName"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("mismatchedAssignmentFailure"))
    );

    public static final Test ASSIGN_COPY_IGNORE_MISSING_FROM_DATA = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Copy-IgnoreMissingFromData", "A receive-reply pair with an intermediate assign with a copy that has ignoreMissingFromData set to yes and contains a from element with an erroneous xpath statement. Therefore, the assign should be ignored.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-IgnoreMissingFromData"),
            new BPELTestCase().checkDeployment().sendSync(5, -1)
    );

    public static final Test ASSIGN_COPY_GET_VARIABLE_PROPERTY = BPELProcessBuilder.buildBasicActivityProcess(
            "Assign-Copy-GetVariableProperty", "A receive-reply pair with an intermediate assign that uses the getVariableProperty function.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-GetVariableProperty"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_COPY_DO_XSL_TRANSFORM = BPELProcessBuilder.buildBasicProcessWithXslt(
            "Assign-Copy-DoXslTransform", "A receive-reply pair with an intermediate assign that uses the doXslTransform function.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-DoXslTransform"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test ASSIGN_COPY_DO_XSL_TRANSFORM_INVALID_SOURCE_FAULT = BPELProcessBuilder.buildBasicProcessWithXslt(
            "Assign-Copy-DoXslTransform-InvalidSourceFault", "A receive-reply pair with an intermediate assign that uses the doXslTransform function without a proper source for the script.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-DoXslTransform-InvalidSourceFault"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("xsltInvalidSource"))
    );

    public static final Test ASSIGN_COPY_DO_XSL_TRANSFORM_STYLESHEET_NOT_FOUND = BPELProcessBuilder.buildBasicProcessWithXslt(
            "Assign-Copy-DoXslTransform-XsltStylesheetNotFound", "A receive-reply pair with an intermediate assign that uses the doXslTransform function, but where the stylesheet does not exist.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-DoXslTransform-XsltStylesheetNotFound"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("xsltStylesheetNotFound"))
    );

    public static
    final Test ASSIGN_COPY_DO_XSL_TRANSFORM_SUB_LANGUAGE_EXECUTION_FAULT = BPELProcessBuilder.buildBasicProcessWithXslt(
            "Assign-Copy-DoXslTransform-SubLanguageExecutionFault", "A receive-reply pair with an intermediate assign that uses the doXslTransform function, but where the actual stylesheet has errors.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-Copy-DoXslTransform-SubLanguageExecutionFault"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("subLanguageExecutionFault"))
    );

    public static final Test ASSIGN_VARIABLES_UNCHANGED_INSPITE_OF_FAULT = BPELProcessBuilder.buildBasicProcessWithXslt(
            "Assign-VariablesUnchangedInspiteOfFault", "A receive-reply pair with two intermediate assigns, the second of which produces a fault that is handled by the process-level faultHandler to send the response. Because of the fault, the second assign should have no impact on the response.",
            new Feature(ASSIGN_CONSTRUCT, "Assign-VariablesUnchangedInspiteOfFault"),
            new BPELTestCase().checkDeployment().sendSync(1, -1)
    );

    public static final List<Test> BASIC_ACTIVITIES_ASSIGN = Arrays.asList(
            ASSIGN_VALIDATE,
            ASSIGN_PROPERTY,
            ASSIGN_TO_PROPERTY,
            ASSIGN_PARTNERLINK,
            ASSIGN_ELEMENT_VARIABLE,
            ASSIGN_PARTNERLINK_PARTNER_ROLE,
            ASSIGN_PARTNERLINK_UNSUPPORTED_REFERENCE,
            ASSIGN_MISMATCHED_ASSIGNMENT_FAILURE,
            ASSIGN_LITERAL,
            ASSIGN_EXPRESSION_FROM,
            ASSIGN_EXPRESSION_TO,
            ASSIGN_EXPRESSION_LANGUAGE_FROM,
            ASSIGN_EXPRESSION_LANGUAGE_TO,
            ASSIGN_INT,
            ASSIGN_SELECTION_FAILURE,
            ASSIGN_COPY_QUERY,
            ASSIGN_COPY_QUERY_LANGUAGE,
            ASSIGN_TO_QUERY,
            ASSIGN_TO_QUERY_LANGUAGE,
            ASSIGN_COPY_KEEP_SRC_ELEMENT_NAME,
            ASSIGN_COPY_IGNORE_MISSING_FROM_DATA,
            ASSIGN_COPY_GET_VARIABLE_PROPERTY,
            ASSIGN_COPY_DO_XSL_TRANSFORM,
            ASSIGN_COPY_DO_XSL_TRANSFORM_INVALID_SOURCE_FAULT,
            ASSIGN_COPY_DO_XSL_TRANSFORM_STYLESHEET_NOT_FOUND,
            ASSIGN_COPY_DO_XSL_TRANSFORM_SUB_LANGUAGE_EXECUTION_FAULT,
            ASSIGN_VARIABLES_UNCHANGED_INSPITE_OF_FAULT
    );

    public static final List<Test> BASIC_ACTIVITIES = CollectionsUtil.union(Arrays.asList(
                    Arrays.asList(
                            EMPTY,
                            EXIT,
                            VALIDATE,
                            VALIDATE_INVALID_VARIABLES,
                            VARIABLES_UNINITIALIZED_VARIABLE_FAULT_REPLY,
                            VARIABLES_UNINITIALIZED_VARIABLE_FAULT_INVOKE,
                            VARIABLES_DEFAULT_INITIALIZATION
                    ),
                    BASIC_ACTIVITIES_ASSIGN,
                    BASIC_ACTIVITIES_INVOKE,
                    BASIC_ACTIVITIES_RECEIVE,
                    BASIC_ACTIVITIES_THROW,
                    BASIC_ACTIVITIES_WAIT)
    );

}
