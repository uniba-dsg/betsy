package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.bpel.model.assertions.SoapFaultTestAssertion;
import betsy.common.util.CollectionsUtil;

import java.util.Arrays;
import java.util.List;

class StructuredActivityProcesses {

    public static final BPELProcess SEQUENCE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Sequence", "A receive-reply pair enclosed in a sequence.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow", "A receive-reply pair with an intermediate flow that contains two assigns.",
            new BPELTestCase().checkDeployment().sendSync(5, 7)
    );

    public static final BPELProcess FLOW_LINKS_RECEIVE_CREATING_INSTANCES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-ReceiveCreatingInstances", "A flow with a starting activity (receive with createInstance set to yes) and a non-starting activity (assign), where a precedence relationship is defined using links.",
            new BPELTestCase().checkDeployment().sendSync(5, 6)
    );

    public static final BPELProcess FLOW_LINKS = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links", "A receive-reply pair with an intermediate flow that contains two assigns which have a precedence relationship between each other using links.",
            new BPELTestCase().checkDeployment().sendSync(1, 2)
    );

    public static final BPELProcess FLOW_LINKS_TRANSITION_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-TransitionCondition", "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions that do fire only if the input is greater than two.",
            new BPELTestCase().checkDeployment().sendSync(2, 4),
            new BPELTestCase().checkDeployment().sendSync(3, 6)
    );

    public static final BPELProcess FLOW_BOUNDARY_LINKS = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-BoundaryLinks", "A receive-reply pair with an intermediate flow that contains an assign and a sequence with an assign, as well as a link pointing from the former to the later assign. That way the links crosses the boundary of a structured activity, the sequence.",
            new BPELTestCase().checkDeployment().sendSync(1, 2)
    );

    //Permutate all allowed combinations of calls here
    public static final BPELProcess FLOW_GRAPH_EXAMPLE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-GraphExample", "An implementation of the flow graph process defined in Sec. 11.6.4.",
            new BPELTestCase().checkDeployment().
                    sendSync(1, 1).
                    sendSync(1, 1).
                    sendAsync(1).
                    sendSync(1, 1).
                    sendAsync(1),

            new BPELTestCase().checkDeployment().
                    sendSync(1, 1).
                    sendAsync(1).
                    sendSync(1, 1).
                    sendSync(1, 1).
                    sendAsync(1),

            new BPELTestCase().checkDeployment().
                    sendSync(1, 1).
                    sendSync(1, 1).
                    sendAsync(1).
                    sendAsync(1).
                    sendSync(1, 1),

            new BPELTestCase().checkDeployment().
                    sendSync(1, 1).
                    sendAsync(1).
                    sendSync(1, 1).
                    sendAsync(1).
                    sendSync(1, 1)
    );

    public static final BPELProcess FLOW_LINKS_JOIN_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-JoinCondition", "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions and their target a joinCondition defined upon them. A joinFailure should result, given not both of the links are activated.",
            new BPELTestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion("joinFailure")),
            new BPELTestCase().checkDeployment().sendSync(3, 6)
    );

    public static final BPELProcess FLOW_LINKS_SUPPRESS_JOIN_FAILURE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-SuppressJoinFailure", "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions and their target a joinCondition defined upon them. The transitionConditions do never evaluate to true, resulting in a joinFailure on each invocation. However, this joinFailure is suppressed.",
            new BPELTestCase().checkDeployment().sendSync(1, 3),
            new BPELTestCase().checkDeployment().sendSync(3, 5)
    );

    public static final BPELProcess FLOW_LINKS_JOIN_FAILURE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-JoinFailure", "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions and their target a joinCondition defined upon them. The transitionConditions do never evaluate to true, resulting in a joinFailure on each invocation.",
            new BPELTestCase().checkDeployment().sendSync(1, new SoapFaultTestAssertion("joinFailure")),
            new BPELTestCase().checkDeployment().sendSync(3, new SoapFaultTestAssertion("joinFailure"))
    );

    public static final BPELProcess FLOW_TWO_STARTING_ON_MESSAGE_CORRELATION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Two-Starting-OnMessage-Correlation", "A flow that contains two pick activities that can both be start activity and reply 0 or '0'. After the flow a simple synchronous receive-reply pair responses the concatenation of the two starting message inputParts.",
            new BPELTestCase().checkDeployment().sendSync(1, 0).sendSyncString(1, "0").sendSyncString(1, "11"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "0").sendSync(2, 0).sendSyncString(2, "22")
    );

    public static
    final BPELProcess FLOW_STARTING_RECEIVE_ON_MESSAGE_CORRELATION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Starting-Receive-OnMessage-Correlation", "A flow that contains a receive-reply pair in a sequence, replying 0, and a pick activity that replies '0'. Both message activities can be start activity. After the flow a simple synchronous receive-reply pair responses the concatenation of the two starting message inputParts.",
            new BPELTestCase().checkDeployment().sendSync(1, 0).sendSyncString(1, "0").sendSyncString(1, "11"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "0").sendSync(2, 0).sendSyncString(2, "22")
    );

    public static final BPELProcess FLOW_TWO_STARTING_RECEIVE_CORRELATION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Two-Starting-Receive-Correlation", "A flow that contains two receive-reply pair in a sequence that can both be start activity and reply 0 or '0'. After the flow a simple synchronous receive-reply pair responses the concatenation of the two starting message inputParts.",
            new BPELTestCase().checkDeployment().sendSync(1, 0).sendSyncString(1, "0").sendSyncString(1, "11"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "0").sendSync(2, 0).sendSyncString(2, "22")
    );

    public static final List<BPELProcess> STRUCTURED_ACTIVITIES_FLOW = Arrays.asList(
            FLOW,
            FLOW_LINKS,
            FLOW_BOUNDARY_LINKS,
            FLOW_LINKS_JOIN_CONDITION,
            FLOW_LINKS_JOIN_FAILURE,
            FLOW_LINKS_SUPPRESS_JOIN_FAILURE,
            FLOW_LINKS_TRANSITION_CONDITION,
            FLOW_GRAPH_EXAMPLE,
            FLOW_LINKS_RECEIVE_CREATING_INSTANCES,
            FLOW_TWO_STARTING_ON_MESSAGE_CORRELATION,
            FLOW_STARTING_RECEIVE_ON_MESSAGE_CORRELATION,
            FLOW_TWO_STARTING_RECEIVE_CORRELATION
    );

    public static final BPELProcess IF = BPELProcessBuilder.buildStructuredActivityProcess(
            "If", "A receive-reply pair with an intermediate if that checks whether the input is even.",
            new BPELTestCase("Not-If-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1)
    );

    public static final BPELProcess IF_ELSE = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-Else", "A receive-reply pair with an intermediate if-else that checks whether the input is even.",
            new BPELTestCase("Else-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1)
    );

    public static final BPELProcess IF_ELSE_IF = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-ElseIf", "A receive-reply pair with an intermediate if-elseif that checks whether the input is even or divisible by three.",
            new BPELTestCase("Not-If-Or-ElseIf-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1),
            new BPELTestCase("ElseIf-Case").checkDeployment().sendSync(3, 2)
    );

    public static final BPELProcess IF_ELSE_IF_ELSE = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-ElseIf-Else", "A receive-reply pair with an intermediate if-elseif-else that checks whether the input is even or divisible by three.",
            new BPELTestCase("Else-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1),
            new BPELTestCase("ElseIf-Case").checkDeployment().sendSync(3, 2)
    );

    public static final BPELProcess IF_SUBLANGUAGE_EXECUTION_FAULT = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-SubLanguageExecutionFault", "A receive-reply pair with an intermediate if that should throw an subLanguageExecutionFault because of an invalid condition.",
            new BPELTestCase("SubLanguageExecutionFault").checkDeployment().
                    sendSync(1, new SoapFaultTestAssertion("subLanguageExecutionFault"))
    );

    public static
    final BPELProcess IF_SUBLANGUAGE_EXECUTION_FAULT_EMPTY_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-SubLanguageExecutionFault-EmptyCondition", "A receive-reply pair with an intermediate if that should throw an subLanguageExecutionFault because of an empty condition.",
            new BPELTestCase("SubLanguageExecutionFault").checkDeployment().
                    sendSync(1, new SoapFaultTestAssertion("subLanguageExecutionFault"))
    );

    public static final List<BPELProcess> STRUCTURED_ACTIVITIES_IF = Arrays.asList(
            IF,
            IF_ELSE,
            IF_ELSE_IF,
            IF_ELSE_IF_ELSE,
            IF_SUBLANGUAGE_EXECUTION_FAULT,
            IF_SUBLANGUAGE_EXECUTION_FAULT_EMPTY_CONDITION
    );

    public static final BPELProcess WHILE = BPELProcessBuilder.buildStructuredActivityProcess(
            "While", "A receive-reply pair with an intermediate while that loops for n times, where n is equal to the input.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess WHILE_FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "While-Flow", "A receive-reply pair with an intermediate while that loops for n times, where n is equal to the input. The loop contains a flow that links the assignment of 1 to a counter and the assignment of the counter to the reply data.",
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final BPELProcess REPEAT_UNTIL = BPELProcessBuilder.buildStructuredActivityProcess(
            "RepeatUntil", "A receive-reply pair with an intermediate while that loops for n+1 times, where n is equal to the input.",
            new BPELTestCase().checkDeployment().sendSync(2, 3)
    );

    public static final BPELProcess REPEAT_UNTIL_EQUALITY = BPELProcessBuilder.buildStructuredActivityProcess(
            "RepeatUntilEquality", "A receive-reply pair with an intermediate while that loops for n times, where n is equal to the input.",
            new BPELTestCase().checkDeployment().sendSync(2, 2)
    );

    public static final BPELProcess REPEAT_UNTIL_FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "RepeatUntil-Flow", "A receive-reply pair with an intermediate while that loops for n+1 times, where n is equal to the input. The loop contains a flow that links the assignment of 1 to a counter and the assignment of the counter to the reply data.",
            new BPELTestCase().checkDeployment().sendSync(2, 3)
    );

    public static final BPELProcess FOR_EACH = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. Each iteration the current loop number is added to the final result.",
            new BPELTestCase("0-equals-0").checkDeployment().sendSync(0, 0),
            new BPELTestCase("0plus1-equals-0").checkDeployment().sendSync(1, 1),
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().sendSync(2, 3)
    );

    public static final BPELProcess FOR_EACH_READ_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Read-Counter", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. Each iteration the current loop number is added twice to the final result.",
            new BPELTestCase().checkDeployment().sendSync(0, 0),
            new BPELTestCase().checkDeployment().sendSync(1, 2),
            new BPELTestCase().checkDeployment().sendSync(2, 6)
    );

    public static final BPELProcess FOR_EACH_WRITE_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Write-Counter", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. The loop contains an if activity, so each odd iteration of the current loop number is added to the final result, when it also has a successor in range.",
            new BPELTestCase().checkDeployment().sendSync(0, 0),
            new BPELTestCase().checkDeployment().sendSync(2, 1),
            new BPELTestCase().checkDeployment().sendSync(6, 9)
    );

    public static final BPELProcess FOR_EACH_FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Flow", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. Each iteration the current loop number is added to a intermediary and from there to the final result, and these assigns are linked within a flow.",
            new BPELTestCase("0-equals-0").checkDeployment().sendSync(0, 0),
            new BPELTestCase("0plus1-equals-0").checkDeployment().sendSync(1, 1),
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().sendSync(2, 3)
    );

    public static final BPELProcess FOR_EACH_NEGATIVE_STOP_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-NegativeStopCounter", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as finalCounterValue is negative.",
            new BPELTestCase("NegativeStopCounter").checkDeployment().sendSync(1, new SoapFaultTestAssertion("invalidExpressionValue"))
            // NaN or large numbers cannot be sent due to type constraint to xsd:int
    );

    public static final BPELProcess FOR_EACH_NEGATIVE_START_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-NegativeStartCounter", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as startCounterValue is negative.",
            new BPELTestCase("Iterate-Twice").checkDeployment().sendSync(2, new SoapFaultTestAssertion("invalidExpressionValue"))
    );

    public static
    final BPELProcess FOR_EACH_COMPLETION_CONDITION_NEGATIVE_BRANCHES = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition-NegativeBranches", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as branches is initialized with a negative value.",
            new BPELTestCase("Iterate-Twice").checkDeployment().sendSync(2, new SoapFaultTestAssertion("invalidExpressionValue"))
    );

    public static final BPELProcess FOR_EACH_TOO_LARGE_START_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-TooLargeStartCounter", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as startCounterValue is initialized with a value that exceeds xs:unsignedInt.",
            new BPELTestCase("Iterate-Twice").checkDeployment().sendSync(2, new SoapFaultTestAssertion("invalidExpressionValue"))
    );

    public static final BPELProcess FOR_EACH_PARALLEL = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Parallel", "A receive-reply pair with an intermediate forEach that executes its children in parallel.",
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().sendSync(2, 3)
    );

    public static final BPELProcess FOR_EACH_PARALLEL_INVOKE = BPELProcessBuilder.buildStructuredProcessWithPartner(
            "structured/ForEach-Parallel-Invoke", "A receive-reply pair with an intermediate forEach that executes its children in parallel.",
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().buildPartnerConcurrencySetup().
                    sendSync(2, 3).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static final BPELProcess FOR_EACH_COMPLETION_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated. N+1 children are scheduled for execution, where n is equal to the input. If N+1 is less than two, an invalidBranchConditionFault should be thrown.",
            new BPELTestCase("Skipping the third iteration").checkDeployment().sendSync(2, 1),
            new BPELTestCase("Cannot meet completion condition").checkDeployment().
                    sendSync(0, new SoapFaultTestAssertion("invalidBranchCondition"))
    );

    public static final BPELProcess FOR_EACH_COMPLETION_CONDITION_PARALLEL = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition-Parallel", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated. N+1 children are scheduled for execution in parallel, where n is equal to the input. If N+1 is less than two, an invalidBranchConditionFault should be thrown.",
            new BPELTestCase("Skipping the third iteration").checkDeployment().sendSync(2, 1),
            new BPELTestCase("Cannot meet completion condition").checkDeployment().
                    sendSync(0, new SoapFaultTestAssertion("invalidBranchCondition"))
    );

    public static
    final BPELProcess FOR_EACH_COMPLETION_CONDITION_SUCCESSFUL_BRANCHES_ONLY = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition-SuccessfulBranchesOnly", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated successfully. Each child throws a fault, given the current counter value is even. N children are scheduled for execution, where n is equal to the input.",
            new BPELTestCase().checkDeployment().sendSync(5, 6),
            new BPELTestCase().checkDeployment().sendSync(10, 6)
    );

    public static final BPELProcess FOR_EACH_COMPLETION_CONDITION_FAILURE = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionConditionFailure", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated. N+1 children are scheduled for execution in parallel, where n is equal to the input. If N+1 is less than two, an invalidBranchConditionFault should be thrown. This is a seperate test case that tests only for the failure.",
            new BPELTestCase("Expect completionConditionFailure").checkDeployment().
                    sendSync(1, new SoapFaultTestAssertion("completionConditionFailure"))
    );

    public static final List<BPELProcess> STRUCTURED_ACTIVITIES_FOR_EACH = Arrays.asList(
            FOR_EACH,
            FOR_EACH_READ_COUNTER,
            FOR_EACH_WRITE_COUNTER,
            FOR_EACH_FLOW,
            FOR_EACH_NEGATIVE_STOP_COUNTER,
            FOR_EACH_COMPLETION_CONDITION,
            FOR_EACH_COMPLETION_CONDITION_PARALLEL,
            FOR_EACH_COMPLETION_CONDITION_SUCCESSFUL_BRANCHES_ONLY,
            FOR_EACH_COMPLETION_CONDITION_FAILURE,
            FOR_EACH_PARALLEL,
            FOR_EACH_PARALLEL_INVOKE,
            FOR_EACH_NEGATIVE_START_COUNTER,
            FOR_EACH_TOO_LARGE_START_COUNTER,
            FOR_EACH_COMPLETION_CONDITION_NEGATIVE_BRANCHES
    );


    public static final BPELProcess PICK_CORRELATIONS_INIT_ASYNC = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Correlations-InitAsync", "An asynchronous receive that initiates a correlationSet, followed by a pick with a synchronous onMessage that correlates on this set.",
            new BPELTestCase().checkDeployment().sendAsync(1).sendSync(1, 1)
    );

    public static final BPELProcess PICK_CORRELATIONS_INIT_SYNC = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Correlations-InitSync", "A receive-reply pair that initiates a correlationSet, followed by a pick with a synchronous onMessage that correlates on this set.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess PICK_CREATE_INSTANCE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-CreateInstance", "A pick with a synchronous onMessage that has createInstance set to yes.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess PICK_MESSAGE_EXCHANGE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-MessageExchange", "A pick with a synchronous onMessage that has createInstance set to yes and uses messageExchange.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess PICK_MESSAGE_EXCHANGE_SCOPE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-MessageExchange-Scope", "A pick with a synchronous onMessage that has createInstance set to yes and uses messageExchange in a scope.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess PICK_MULTIPLE_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Multiple-MessageExchanges", "A pick with a synchronous onMessage that has createInstance set to yes and a second pick with a synchronous onMessage of the same type and both use messageExchanges to define which reply belongs to which onMessage and the response is the initial value first then the sum of the received values.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess PICK_MULTIPLE_MESSAGE_EXCHANGES_SCOPE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Multiple-MessageExchanges-Scope", "A pick with a synchronous onMessage that has createInstance set to yes and a second pick with a synchronous onMessage of the same type and both use messageExchanges in a scope to define which reply belongs to which onMessage and the response is the initial value first then the sum of the received values.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess PICK_FIFO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-FIFO-MessageExchanges", "Two onMessages of the same operation that use messageExchanges to define which reply belongs to which onMessage and the response is 1 for the reply to the first onMessage and 2 for the second reply to the second onMessage.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess PICK_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-FILO-MessageExchanges", "Two onMessages of the same operation that use messageExchanges to define which reply belongs to which onMessage and the response is 2 for the reply to the second onMessage and 1 for the second reply to the first onMessage.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess RECEIVE_PICK_FIFO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Receive-Pick-FIFO-MessageExchanges", "A receive and a onMessage of the same operation that use messageExchanges to define which reply belongs to which receive/onMessage and the response is 1 for the reply to the onMessage and 2 for the second reply to the receive.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess RECEIVE_PICK_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Receive-Pick-FILO-MessageExchanges", "A receive and a onMessage of the same operation that use messageExchanges to define which reply belongs to which receive/onMessage and the response is 2 for the reply to the onMessage and 1 for the second reply to the receive.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess PICK_RECEIVE_FIFO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Receive-FIFO-MessageExchanges", "A onMessage and a receive of the same operation that use messageExchanges to define which reply belongs to which onMessage/receive and the response is 1 for the reply to the receive and 2 for the second reply to the onMessage.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess PICK_RECEIVE_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Receive-FILO-MessageExchanges", "A onMessage and a receive of the same operation that use messageExchanges to define which reply belongs to which onMessage/receive and the response is 2 for the reply to the receive and 1 for the second reply to the onMessage.",
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final BPELProcess PICK_CREATE_INSTANCE_FROM_PARTS = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-CreateInstance-FromParts", "A pick with a synchronous onMessage that has createInstance set to yes using fromParts.",
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final BPELProcess PICK_ON_ALARM_UNTIL = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-OnAlarm-Until", "A receive-reply pair that initiates a correlationSet and an intermediate pick that contains an onMessage and an onAlarm with an until element. The onAlarm should fire immediately.",
            new BPELTestCase().checkDeployment().sendSync(1, -1)
    );

    public static final BPELProcess PICK_ON_ALARM_FOR = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-OnAlarm-For", "An onAlarm with for test case. The test contains a receive-reply pair that initiates a correlationSet and an intermediate pick that contains an onMessage and an onAlarm with an for element. The onAlarm should fire after two seconds and the process should reply with a default value.",
            new BPELTestCase().checkDeployment().sendSync(1, -1)
    );

    public static final List<BPELProcess> STRUCTURED_ACTIVITIES_PICK = Arrays.asList(
            PICK_CORRELATIONS_INIT_ASYNC,
            PICK_CORRELATIONS_INIT_SYNC,
            PICK_CREATE_INSTANCE,
            PICK_MESSAGE_EXCHANGE,
            PICK_MESSAGE_EXCHANGE_SCOPE,
            PICK_MULTIPLE_MESSAGE_EXCHANGES,
            PICK_MULTIPLE_MESSAGE_EXCHANGES_SCOPE,
            PICK_FIFO_MESSAGE_EXCHANGES,
            PICK_FILO_MESSAGE_EXCHANGES,
            RECEIVE_PICK_FIFO_MESSAGE_EXCHANGES,
            RECEIVE_PICK_FILO_MESSAGE_EXCHANGES,
            PICK_RECEIVE_FIFO_MESSAGE_EXCHANGES,
            PICK_RECEIVE_FILO_MESSAGE_EXCHANGES,
            PICK_CREATE_INSTANCE_FROM_PARTS,
            PICK_ON_ALARM_FOR,
            PICK_ON_ALARM_UNTIL
    );

    public static final List<BPELProcess> STRUCTURED_ACTIVITIES = CollectionsUtil.union(Arrays.asList(
            Arrays.asList(
                    SEQUENCE,
                    WHILE,
                    WHILE_FLOW,
                    REPEAT_UNTIL,
                    REPEAT_UNTIL_EQUALITY,
                    REPEAT_UNTIL_FLOW
            ),
            STRUCTURED_ACTIVITIES_FLOW,
            STRUCTURED_ACTIVITIES_IF,
            STRUCTURED_ACTIVITIES_FOR_EACH,
            STRUCTURED_ACTIVITIES_PICK
    ));

}
