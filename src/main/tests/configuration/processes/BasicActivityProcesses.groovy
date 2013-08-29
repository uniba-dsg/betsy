package configuration.processes

import betsy.data.Process
import betsy.data.TestCase

import betsy.data.TestStep
import betsy.data.WsdlOperation
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.data.assertions.ExitAssertion

class BasicActivityProcesses {

    ProcessBuilder builder = new ProcessBuilder()

    public final Process EMPTY = builder.buildBasicActivityProcess(
            "Empty", "A receive-reply pair with an intermediate empty.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(5,5)
            ]
    )

    public final Process EXIT = builder.buildBasicActivityProcess(
            "Exit", "A receive-reply pair with an intermediate exit. There should not be a normal response.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1,new ExitAssertion())
            ]
    )

    public final Process VALIDATE = builder.buildProcessWithXsd(
            "basic-activities/Validate", "A receive-reply pair with an intermediate variable validation. The variable to be validated describes a month, so only values in the range of 1 and 12 should validate successfully.",
            [
                    new TestCase(name: "Input Value 13 should return validation fault").
                            checkDeployment().
                            sendSync(13, new SoapFaultTestAssertion(faultString: "invalidVariables"))
            ]
    )

    public final Process VALIDATE_INVALID_VARIABLES = builder.buildProcessWithXsd(
            "basic-activities/Validate-InvalidVariables", "A receive-reply pair with an intermediate variable validation. The variable to be validated is of type xs:int and xs:boolean is copied into it.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1, new SoapFaultTestAssertion(faultString: "invalidVariables"))
            ]
    )

    public final Process VARIABLES_UNINITIALIZED_VARIABLE_FAULT_REPLY = builder.buildBasicActivityProcess(
            "Variables-UninitializedVariableFault-Reply", "A receive-reply pair where the variable of the reply is not initialized.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1, new SoapFaultTestAssertion(faultString: "uninitializedVariable"))
            ]
    )

    public final Process VARIABLES_UNINITIALIZED_VARIABLE_FAULT_INVOKE = builder.buildProcessWithPartner(
            "basic-activities/Variables-UninitializedVariableFault-Invoke", "A receive-reply pair with intermediate invoke. The inputVariable of the invoke is not initialized.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1, new SoapFaultTestAssertion(faultString: "uninitializedVariable"))
            ]
    )

    public final Process VARIABLES_DEFAULT_INITIALIZATION = builder.buildBasicActivityProcess(
            "Variables-DefaultInitialization", "A receive-reply pair where the variable of the reply is assigned with a default value.",
            [
                    new TestCase(name: "DefaultValue-10-Should-Be-Returned").checkDeployment().sendSync(5,10)
            ]
    )

    public final Process WAIT_FOR = builder.buildBasicActivityProcess(
            "Wait-For", "A receive-reply pair with an intermediate wait that pauses execution for five seconds.",
            [
                    new TestCase().checkDeployment().sendSync(5,5)
            ]
    )

    public final Process WAIT_FOR_INVALID_EXPRESSION_VALUE = builder.buildBasicActivityProcess(
            "Wait-For-InvalidExpressionValue",  "A receive-reply pair with an intermediate wait. The for element is assigned a value of xs:int, but only xs:duration is allowed.",
            [
                    new TestCase().checkDeployment().sendSync(5, new SoapFaultTestAssertion(faultString: "invalidExpressionValue"))
            ]
    )

    public final Process WAIT_UNTIL = builder.buildBasicActivityProcess(
            "Wait-Until", "A receive-reply pair with an intermediate wait that pauses the execution until a date in the past. Therefore, the wait should complete instantly.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final List<Process> BASIC_ACTIVITIES_WAIT = [
            WAIT_FOR,
            WAIT_FOR_INVALID_EXPRESSION_VALUE,
            WAIT_UNTIL
    ]

    public final Process THROW = builder.buildBasicActivityProcess(
            "Throw",  "A receive-reply pair with an intermediate throw. The response should a soap fault containing the bpel fault.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "completionConditionFailure"))
            ]
    )

    public final Process THROW_WITHOUT_NAMESPACE = builder.buildBasicActivityProcess(
            "Throw-WithoutNamespace", "A receive-reply pair with an intermediate throw that uses a bpel fault without explicitly using the bpel namespace. The respone should be a soap fault containing the bpel fault.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "completionConditionFailure"))
            ]
    )

    public final Process THROW_CUSTOM_FAULT = builder.buildBasicActivityProcess(
            "Throw-CustomFault", "A receive-reply pair with an intermediate throw that throws a custom fault that undefined in the given namespace. The response should be a soap fault containing the custom fault.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "testFault"))
            ]
    )

    public final Process THROW_CUSTOM_FAULT_IN_WSDL = builder.buildBasicActivityProcess(
            "Throw-CustomFaultInWsdl",  "A receive-reply pair with an intermediate throw that throws a custom fault defined in the myRole WSDL. The response should be a soap fault containing the custom fault.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "syncFault"))
            ]
    )

    public final Process THROW_FAULT_DATA = builder.buildBasicActivityProcess(
            "Throw-FaultData",  "A receive-reply pair with an intermediate throw that also uses a faultVariable. The content of the faultVariable should be contained in the response.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1, 1, new SoapFaultTestAssertion(faultString: "completionConditionFailure"))
            ]
    )

    public final Process RETHROW = builder.buildBasicActivityProcess(
            "Rethrow",
            "A receive activity with an intermediate throw and a fault handler with a catchAll. The fault handler rethrows the fault.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1, new SoapFaultTestAssertion(faultString: "completionConditionFailure"))
            ]
    )

    public final Process RETHROW_FAULT_DATA_UNMODIFIED = builder.buildBasicActivityProcess(
            "Rethrow-FaultDataUnmodified",
            "A receive activity with an intermediate throw that uses a faultVariable. A fault handler catches the fault, changes the data, and rethrows the fault. The fault should be the response with unchanged data.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1, 1, new SoapFaultTestAssertion(faultString: "completionConditionFailure"))
            ]
    )

    public final Process RETHROW_FAULT_DATA = builder.buildBasicActivityProcess(
            "Rethrow-FaultData",
            "A receive activity with an intermediate throw that uses a faultVariable. A fault handler catches and rethrows the fault. The fault should be the response along with the data.",
            [
                    new TestCase().
                            checkDeployment().
                            sendSync(1, 1, new SoapFaultTestAssertion(faultString: "completionConditionFailure"))
            ]
    )

    public final List<Process> BASIC_ACTIVITIES_THROW = [
            THROW,
            THROW_WITHOUT_NAMESPACE,
            THROW_FAULT_DATA,
            THROW_CUSTOM_FAULT,
            THROW_CUSTOM_FAULT_IN_WSDL,
            RETHROW,
            RETHROW_FAULT_DATA_UNMODIFIED,
            RETHROW_FAULT_DATA
    ]

    public final Process RECEIVE = builder.buildBasicActivityProcess(
            "Receive",  "A single asynchronous receive.",
            [
                    new TestCase().checkDeployment().sendAsync(1)
            ]
    )

    public final Process RECEIVE_CORRELATION_INIT_ASYNC = builder.buildBasicActivityProcess(
            "Receive-Correlation-InitAsync",  "Two asynchronous receives, followed by a receive-reply pair, and bound to a single correlationSet.",
            [
                    new TestCase().checkDeployment().sendAsync(1).waitFor(1000).sendAsync(1).waitFor(1000).sendSync(1,1)
            ]
    )

    public final Process RECEIVE_CORRELATION_INIT_SYNC = builder.buildBasicActivityProcess(
            "Receive-Correlation-InitSync",  "One synchronous receive, one asynchronous receive, followed by a receive-reply pair, and bound to a single correlationSet.",
            [
                    new TestCase().checkDeployment().sendSync(1,0).waitFor(1000).sendAsync(1).waitFor(1000).sendSync(1,1)
            ]
    )

    public final Process RECEIVE_AMBIGUOUS_RECEIVE_FAULT = builder.buildBasicActivityProcess(
            "Receive-AmbiguousReceiveFault",  "An asynchronous receive that initiates two correlationSets, followed by a flow with two sequences that contain synchronous receive-reply pairs for the same operation but differnet correlationSets. Should trigger an ambiguousReceive fault." ,
            [
                    new TestCase().checkDeployment().sendAsync(1).waitFor(1000).sendSync(1, new SoapFaultTestAssertion(faultString: "ambiguousReceive"))
            ]
    )

    public final Process RECEIVE_CONFLICTING_RECEIVE_FAULT = builder.buildBasicActivityProcess(
            "Receive-ConflictingReceiveFault",  "An asynchronous receive that initiates a correlationSet, followed by a flow with two sequences that contain synchronous receive-reply pair for the same operation and correlationSet. Should trigger a conflictingReceive fault.",
            [
                    // TODO muss das hier nicht ein sendAsync sein als erstes?
                    new TestCase().checkDeployment().sendSync(1).waitFor(1000).sendSync(1, new SoapFaultTestAssertion(faultString: "conflictingReceive"))
            ]
    )

    public final Process RECEIVE_REPLY_CONFLICTING_REQUEST_FAULT = builder.buildBasicActivityProcess(
            "ReceiveReply-ConflictingRequestFault", "A synchronous interaction, followed by intermediate while that subsequently enables multiple receives that correspond to a single synchronous message exchange. Should trigger a conflictingRequest fault.",
            [
                    new TestCase().checkDeployment().
                            sendSync(1,1).waitFor(1000). // start up, should complete normally
                            sendSyncString(1).waitFor(1000). // no reply, also normal, we need to open the message exchange
                            sendSyncString(1, new SoapFaultTestAssertion(faultString: "conflictingRequest")) // now, there should be the fault
            ]
    )

    public final Process RECEIVE_REPLY = builder.buildBasicActivityProcess(
            "ReceiveReply", "A simple receive-reply pair.",
            [
                    new TestCase().checkDeployment().sendSync(5,5)
            ]
    )

    public final Process RECEIVE_REPLY_MESSAGE_EXCHANGES = builder.buildBasicActivityProcess(
            "ReceiveReply-MessageExchanges",  "A simple receive-reply pair that uses a messageExchange.",
            [
                    new TestCase().checkDeployment().sendSync(1,1)
            ]
    )

    public final Process RECEIVE_REPLY_CORRELATION_INIT_ASYNC = builder.buildBasicActivityProcess(
            "ReceiveReply-Correlation-InitAsync", "An asynchronous receive that initiates a correlationSet followed by a receive-reply pair that uses this set.",
            [
                    new TestCase().checkDeployment().sendAsync(5).waitFor(1000).sendSync(5,5)
            ]
    )

    public final Process RECEIVE_REPLY_CORRELATION_INIT_SYNC = builder.buildBasicActivityProcess(
            "ReceiveReply-Correlation-InitSync", "A synchronous recieve that initiates a correlationSet followed by a receive-reply pair that uses this set.",
            [
                    new TestCase().checkDeployment().sendSync(5,0).waitFor(1000).sendSync(5,5)
            ]
    )

    public final Process RECEIVE_REPLY_CORRELATION_VIOLATION_NO = builder.buildBasicActivityProcess(
            "ReceiveReply-CorrelationViolation-No", "A receive-reply pair that uses an uninitiated correlationSet and sets initiate to no. Should trigger a correlationViolation fault.",
            [
                    new TestCase().checkDeployment().sendSync(1,new SoapFaultTestAssertion(faultString: "correlationViolation"))
            ]
    )

    public final Process RECEIVE_REPLY_CORRELATION_VIOLATION_YES = builder.buildBasicActivityProcess(
            "ReceiveReply-CorrelationViolation-Yes", "Two subsequent receive-reply pairs which share a correlationSet and where both receives have initiate set to yes.",
            [
                    new TestCase().checkDeployment().
                            sendSync(1,1).waitFor(1000).
                            sendSync(1, new SoapFaultTestAssertion(faultString: "correlationViolation"))
            ]
    )

    public final Process RECEIVE_REPLY_CORRELATION_VIOLATION_JOIN = builder.buildProcessWithPartner(
            "basic-activities/ReceiveReply-CorrelationViolation-Join", "A receive-reply pair that initates a correlationSet with an intermediate invoke that tries to join the correlationSet. The join operation should only work if the correlationSet was initiate with a certain value.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "correlationViolation")),
                    new TestCase().checkDeployment().sendSync(2, 2)
            ]
    )

    public final Process RECEIVE_REPLY_FROM_PARTS = builder.buildBasicActivityProcess(
            "ReceiveReply-FromParts", "A receive-reply pair that uses the fromPart syntax instead of a variable.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public final Process RECEIVE_REPLY_TO_PARTS = builder.buildBasicActivityProcess(
            "ReceiveReply-ToParts", "A receive-reply pair that uses the toPart syntax instead of a variable.",
            [
                    new TestCase().checkDeployment().sendSync(1,1)
            ]
    )

    public final Process RECEIVE_REPLY_FAULT = builder.buildBasicActivityProcess(
            "ReceiveReply-Fault", "A receive-reply pair replies with a fault instead of a variable.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "syncFault"))
            ]
    )

    public final List<Process> BASIC_ACTIVITIES_RECEIVE = [
            RECEIVE,
            RECEIVE_CORRELATION_INIT_ASYNC,
            RECEIVE_CORRELATION_INIT_SYNC,
            RECEIVE_REPLY_MESSAGE_EXCHANGES,
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
            RECEIVE_REPLY_FAULT,
    ]

    public final Process INVOKE_ASYNC = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Async",  "A receive-reply pair with an intermediate asynchronous invoke.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process INVOKE_SYNC = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Sync", "A receive-reply pair with an intermediate synchronous invoke.",
            [
                    new TestCase().checkDeployment().sendSync(1, 1)
            ]
    )

    public final Process INVOKE_SYNC_FAULT = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Sync-Fault", "A receive-reply pair with an intermediate synchronous invoke that should trigger a fault.",
            [
                    new TestCase().checkDeployment().sendSync(builder.DECLARED_FAULT_CODE, new SoapFaultTestAssertion(faultString: "CustomFault"))
            ]
    )

    public final Process INVOKE_TO_PARTS = builder.buildProcessWithPartner(
            "basic-activities/Invoke-ToParts","A receive-reply pair with an intermediate synchronous invoke that uses the toParts syntax.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process INVOKE_FROM_PARTS = builder.buildProcessWithPartner(
            "basic-activities/Invoke-FromParts", "A receive-reply pair with an intermediate synchronous invoke that uses the fromParts syntax.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process INVOKE_EMPTY = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Empty", "A receive-reply pair with an intermediate invoke of an operation that has no message associated with it. No definition of inputVariable or outputVariable is required.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process INVOKE_CORRELATION_PATTERN_INIT_ASYNC = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Correlation-Pattern-InitAsync",  "An asynchronous receive that initiates a correlationSet used by a subsequent invoke that also uses a request-response pattern and is thereafter followed by receive-reply pair that also uses the correlationSet.",
            [
                    new TestCase().checkDeployment().sendAsync(1).waitFor(1000).sendSync(1, 1)
            ]
    )

    public final Process INVOKE_CORRELATION_PATTERN_INIT_SYNC = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Correlation-Pattern-InitSync",  "A synchronous receive that initiates a correlationSet used by a subsequent invoke that also uses a request-response pattern and is thereafter followed by receive-reply pair that also uses the correlationSet.",
            [
                    new TestCase().checkDeployment().sendSync(1, 0).waitFor(1000).sendSync(1,1)
            ]
    )

    public final Process INVOKE_CATCH = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Catch",  "A receive-reply pair with an intermediate invoke that results in a fault for certain input, but catches that fault and replies.",
            [
                    new TestCase().checkDeployment().sendSync(builder.DECLARED_FAULT_CODE, 0)
            ]
    )

    public final Process INVOKE_CATCH_UNDECLARED_FAULT = builder.buildProcessWithPartner(
            "basic-activities/Invoke-Catch-UndeclaredFault",  "A receive-reply pair with an intermediate invoke that results in a fault for certain input, but catches that fault and replies. The fault is not declared in the Web Service Definition of the partner service.",
            [
                    new TestCase().checkDeployment().sendSync(builder.UNDECLARED_FAULT_CODE, 0)
            ]
    )

    public final Process INVOKE_CATCHALL = builder.buildProcessWithPartner(
            "basic-activities/Invoke-CatchAll",  "A receive-reply pair with an intermediate invoke that results in a fault for certain input, but catches all faults and replies.",
            [
                    new TestCase(name: "Enter-CatchAll").checkDeployment().sendSync(builder.UNDECLARED_FAULT_CODE, 0)
            ]
    )

    public final Process INVOKE_COMPENSATION_HANDLER = builder.buildProcessWithPartner(
            "basic-activities/Invoke-CompensationHandler",  "A receive-reply pair combined with an invoke that has a compensationHandler, followed by a throw. The fault is caught by the process-level faultHandler. That faultHandler triggers the compensationHandler of the invoke which contains the reply.",
            [
                    new TestCase().checkDeployment().sendSync(1, 0)
            ]
    )

    public final List<Process> BASIC_ACTIVITIES_INVOKE = [
            INVOKE_ASYNC,
            INVOKE_SYNC,
            INVOKE_SYNC_FAULT,
            INVOKE_EMPTY,
            INVOKE_TO_PARTS,
            INVOKE_FROM_PARTS,
            INVOKE_CORRELATION_PATTERN_INIT_ASYNC,
            INVOKE_CORRELATION_PATTERN_INIT_SYNC,
            INVOKE_CATCH,
            INVOKE_CATCH_UNDECLARED_FAULT,
            INVOKE_CATCHALL,
            INVOKE_COMPENSATION_HANDLER
    ]


    public final Process ASSIGN_VALIDATE = builder.buildProcessWithXsd(
            "basic-activities/Assign-Validate",  "A receive-reply pair with an intermediate assign that has validate set to yes. The assign copies to a variable that represents a month and the validation should fail for values not in the range of one to twelve.",
            [
                    new TestCase(name: "Input Value 13 should return validation fault").checkDeployment().
                            sendSync(13, new SoapFaultTestAssertion(faultString: "invalidVariables"))
            ]
    )

    public final Process ASSIGN_PROPERTY = builder.buildBasicActivityProcess(
            "Assign-Property", "A receive-reply pair with an intermediate assign that copies from a property instead of a variable." ,
            [
                    new TestCase().checkDeployment().sendSync(5,5)
            ]
    )

    public final Process ASSIGN_PARTNERLINK = builder.buildProcessWithPartner(
            "basic-activities/Assign-PartnerLink", "A receive-reply pair with an intermediate assign that assigns a WS-A EndpointReference to a partnerLink which is used in a subsequent invoke.",
            [
                    new TestCase().checkDeployment().sendSync(5, 0)
            ]
    )

    public final Process ASSIGN_PARTNERLINK_UNSUPPORTED_REFERENCE = builder.buildProcessWithPartner(
            "basic-activities/Assign-PartnerLink-UnsupportedReference", "A receive-reply pair with an intermediate assign that assigns a bogus refernce to a partnerLink which is used in a subsequent invoke. The reference scheme should not be supported by any engine and fail with a corresponding fault.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "unsupportedReference"))
            ]
    )

    public final Process ASSIGN_MISMATCHED_ASSIGNMENT_FAILURE = builder.buildBasicActivityProcess(
            "Assign-MismatchedAssignmentFailure", "An assignment between two incompatible types. A mismatchedAssignmentFailure should be thrown.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "mismatchedAssignment"))
            ]
    )

    public final Process ASSIGN_LITERAL = builder.buildBasicActivityProcess(
            "Assign-Literal", "A receive-reply pair with an intermediate assign that copies a literal.",
            [
                    new TestCase().checkDeployment().sendSync(5, 1)
            ]
    )

    public final Process ASSIGN_EXPRESSION_FROM = builder.buildBasicActivityProcess(
            "Assign-Expression-From", "A receive-reply pair with an intermediate assign that uses an expression in a from element.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process ASSIGN_EXPRESSION_TO = builder.buildBasicActivityProcess(
            "Assign-Expression-To", "A receive-reply pair with an intermediate assign that uses an expression in a to element.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process ASSIGN_INT = builder.buildProcessWithPartner(
            "basic-activities/Assign-Int",  "A receive-reply pair combined with an assign and an invoke inbetween. The assign copies an int value as an expression to the inputVariable of the invoke. The invocation fails if the value copied is not an int (but, for instance, a float).",
            [
                    new TestCase().checkDeployment().sendSync(1, 10)
            ]
    )

    public final Process ASSIGN_SELECTION_FAILURE = builder.buildBasicActivityProcess(
            "Assign-SelectionFailure", "A receive-reply pair with an intermediate assign that uses a from that retuns zero nodes. This should trigger a selectionFailure.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "selectionFailure"))
            ]
    )

    public final Process ASSIGN_COPY_QUERY = builder.buildBasicActivityProcess(
            "Assign-Copy-Query",  "A process with a receive-reply pair with an intermediate assign that uses a query in a from element.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process ASSIGN_COPY_KEEP_SRC_ELEMENT_NAME = builder.buildBasicActivityProcess(
            "Assign-Copy-KeepSrcElementName", "A receive-reply pair with an intermediate assign with a copy that has keepSrcElementName set to yes. This should trigger a fault.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "mismatchedAssignmentFailure"))
            ]
    )

    public final Process ASSIGN_COPY_IGNORE_MISSING_FROM_DATA = builder.buildBasicActivityProcess(
            "Assign-Copy-IgnoreMissingFromData",  "A receive-reply pair with an intermediate assign with a copy that has ignoreMissingFromData set to yes and contains a from element with an erroneous xpath statement. Therefore, the assign should be ignored.",
            [
                    new TestCase().checkDeployment().sendSync(5, -1)
            ]
    )

    public final Process ASSIGN_COPY_GET_VARIABLE_PROPERTY = builder.buildBasicActivityProcess(
            "Assign-Copy-GetVariableProperty", "A receive-reply pair with an intermediate assign that uses the getVariableProperty function.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process ASSIGN_COPY_DO_XSL_TRANSFORM = builder.buildProcessWithXslt(
            "basic-activities/Assign-Copy-DoXslTransform", "A receive-reply pair with an intermediate assign that uses the doXslTransform function.",
            [
                    new TestCase().checkDeployment().sendSync(5, 5)
            ]
    )

    public final Process ASSIGN_COPY_DO_XSL_TRANSFORM_INVALID_SOURCE_FAULT = builder.buildProcessWithXslt(
            "basic-activities/Assign-Copy-DoXslTransform-InvalidSourceFault",  "A receive-reply pair with an intermediate assign that uses the doXslTransform function without a proper source for the script.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "xsltInvalidSource"))
            ]
    )

    public final Process ASSIGN_COPY_DO_XSL_TRANSFORM_STYLESHEET_NOT_FOUND = builder.buildProcessWithXslt(
            "basic-activities/Assign-Copy-DoXslTransform-XsltStylesheetNotFound", "A receive-reply pair with an intermediate assign that uses the doXslTransform function, but where the stylesheet does not exist.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "xsltStylesheetNotFound"))
            ]
    )

    public final Process ASSIGN_COPY_DO_XSL_TRANSFORM_SUB_LANGUAGE_EXECUTION_FAULT = builder.buildProcessWithXslt(
            "basic-activities/Assign-Copy-DoXslTransform-SubLanguageExecutionFault", "A receive-reply pair with an intermediate assign that uses the doXslTransform function, but where the actual stylesheet has errors.",
            [
                    new TestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion(faultString: "subLanguageExecutionFault"))
            ]
    )

    public final Process ASSIGN_VARIABLES_UNCHANGED_INSPITE_OF_FAULT = builder.buildProcessWithXslt(
            "basic-activities/Assign-VariablesUnchangedInspiteOfFault", "A receive-reply pair with two intermediate assigns, the second of which produces a fault that is handled by the process-level faultHandler to send the response. Because of the fault, the second assign should have no impact on the response.",
            [
                    new TestCase().checkDeployment().sendSync(1, -1)
            ]
    )

    public final List<Process> BASIC_ACTIVITIES_ASSIGN = [
            ASSIGN_VALIDATE,
            ASSIGN_PROPERTY,
            ASSIGN_PARTNERLINK,
            ASSIGN_PARTNERLINK_UNSUPPORTED_REFERENCE,
            ASSIGN_MISMATCHED_ASSIGNMENT_FAILURE,
            ASSIGN_LITERAL,
            ASSIGN_EXPRESSION_FROM,
            ASSIGN_EXPRESSION_TO,
            ASSIGN_INT,
            ASSIGN_SELECTION_FAILURE,
            ASSIGN_COPY_QUERY,
            ASSIGN_COPY_KEEP_SRC_ELEMENT_NAME,
            ASSIGN_COPY_IGNORE_MISSING_FROM_DATA,
            ASSIGN_COPY_GET_VARIABLE_PROPERTY,
            ASSIGN_COPY_DO_XSL_TRANSFORM,
            ASSIGN_COPY_DO_XSL_TRANSFORM_INVALID_SOURCE_FAULT,
            ASSIGN_COPY_DO_XSL_TRANSFORM_STYLESHEET_NOT_FOUND,
            ASSIGN_COPY_DO_XSL_TRANSFORM_SUB_LANGUAGE_EXECUTION_FAULT,
            ASSIGN_VARIABLES_UNCHANGED_INSPITE_OF_FAULT
    ].flatten() as List<Process>

    public final List<Process> BASIC_ACTIVITIES = [
            BASIC_ACTIVITIES_ASSIGN,
            BASIC_ACTIVITIES_INVOKE,
            BASIC_ACTIVITIES_RECEIVE,
            BASIC_ACTIVITIES_THROW,
            BASIC_ACTIVITIES_WAIT,
            EMPTY,
            EXIT,
            VALIDATE,
            VALIDATE_INVALID_VARIABLES,
            VARIABLES_UNINITIALIZED_VARIABLE_FAULT_REPLY,
            VARIABLES_UNINITIALIZED_VARIABLE_FAULT_INVOKE,
            VARIABLES_DEFAULT_INITIALIZATION
    ].flatten() as List<Process>
}
