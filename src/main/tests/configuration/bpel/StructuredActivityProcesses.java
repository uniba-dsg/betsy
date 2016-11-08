package configuration.bpel;

import betsy.bpel.model.BPELTestCase;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.assertions.AssertSoapFault;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.feature.Feature;
import betsy.common.util.CollectionsUtil;

import java.util.Arrays;
import java.util.List;

class StructuredActivityProcesses {

    private static final FeatureSet WHILE_CONSTRUCT = new FeatureSet(Groups.STRUCTURED, "While", "The <while> activity provides for repeated execution of a contained activity. The contained activity is executed as long as the Boolean <condition> evaluates to true at the beginning of each iteration. (p. 99, BPEL)");
    private static final FeatureSet REPEAT_UNTIL_CONSTRUCT = new FeatureSet(Groups.STRUCTURED, "RepeatUntil", "The <repeatUntil> activity provides for repeated execution of a contained activity. The contained activity is executed until the given Boolean <condition> becomes true. (p. 100, BPEL)");
    private static final FeatureSet PICK_CONSTRUCT = new FeatureSet(Groups.STRUCTURED, "Pick", "The <pick> activity waits for the occurrence of exactly one event from a set of events, then executes the activity associated with that event. After an event has been selected, the other events are no longer accepted by that <pick>. (p. 100, BPEL)");
    private static final FeatureSet FOR_EACH_CONSTRUCT = new FeatureSet(Groups.STRUCTURED, "ForEach", "The <forEach> activity will execute its contained <scope> activity exactly N+1 times where N equals the <finalCounterValue> minus the <startCounterValue>. (p. 112, BPEL)");
    private static final FeatureSet SEQUENCE_CONSTRUCT = new FeatureSet(Groups.STRUCTURED, "Sequence", "A <sequence> activity contains one or more activities that are performed sequentially, in the lexical order in which they appear within the <sequence> element. (p. 98, BPEL)");
    private static final FeatureSet IF_CONSTRUCT = new FeatureSet(Groups.STRUCTURED, "If", "The <if> activity provides conditional behavior. The activity consists of an ordered list of one or more conditional branches defined by the <if> and optional <elseif> elements, followed by an optional <else> element. (p. 99, BPEL)");
    private static final FeatureSet FLOW_CONSTRUCT = new FeatureSet(Groups.STRUCTURED, "Flow", "The <flow> activity provides concurrency and synchronization. (p. 102, BPEL)");

    public static final Test SEQUENCE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Sequence", "A receive-reply pair enclosed in a sequence.",
            new Feature(SEQUENCE_CONSTRUCT, "Sequence"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow", "A receive-reply pair with an intermediate flow that contains two assigns.",
            new Feature(FLOW_CONSTRUCT, "Flow"),
            new BPELTestCase().checkDeployment().sendSync(5, 7)
    );

    public static final Test FLOW_LINKS_RECEIVE_CREATING_INSTANCES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-ReceiveCreatingInstances",
            "A flow with a starting activity (receive with createInstance set to yes) and a non-starting activity (assign), where a precedence relationship is defined using links.",
            new Feature(FLOW_CONSTRUCT, "Flow-Links-ReceiveCreatingInstances"),
            new BPELTestCase().checkDeployment().sendSync(5, 6)
    );

    public static final Test FLOW_LINKS = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links",
            "A receive-reply pair with an intermediate flow that contains two assigns which have a precedence relationship between each other using links.",
            new Feature(FLOW_CONSTRUCT, "Flow-Links"),
            new BPELTestCase().checkDeployment().sendSync(1, 2)
    );

    public static final Test FLOW_LINKS_TRANSITION_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-TransitionCondition",
            "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions that do fire only if the input is greater than two.",
            new Feature(FLOW_CONSTRUCT, "Flow-Links-TransitionCondition"),
            new BPELTestCase().checkDeployment().sendSync(2, 4),
            new BPELTestCase().checkDeployment().sendSync(3, 6)
    );

    public static final Test FLOW_BOUNDARY_LINKS = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-BoundaryLinks",
            "A receive-reply pair with an intermediate flow that contains an assign and a sequence with an assign, as well as a link pointing from the former to the later assign. That way the links crosses the boundary of a structured activity, the sequence.",
            new Feature(FLOW_CONSTRUCT, "Flow-BoundaryLinks"),
            new BPELTestCase().checkDeployment().sendSync(1, 2)
    );

    //Permutate all allowed combinations of calls here
    public static final Test FLOW_GRAPH_EXAMPLE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-GraphExample",
            "An implementation of the flow graph process defined in Sec. 11.6.4.",
            new Feature(FLOW_CONSTRUCT, "Flow-GraphExample"),
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

    public static final Test FLOW_LINKS_JOIN_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-JoinCondition",
            "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions and their target a joinCondition defined upon them. A joinFailure should result, given not both of the links are activated.",
            new Feature(FLOW_CONSTRUCT, "Flow-Links-JoinCondition"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("joinFailure")),
            new BPELTestCase().checkDeployment().sendSync(3, 6)
    );

    public static final Test FLOW_LINKS_SUPPRESS_JOIN_FAILURE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-SuppressJoinFailure",
            "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions and their target a joinCondition defined upon them. The transitionConditions do never evaluate to true, resulting in a joinFailure on each invocation. However, this joinFailure is suppressed.",
            new Feature(FLOW_CONSTRUCT, "Flow-Links-SuppressJoinFailure"),
            new BPELTestCase().checkDeployment().sendSync(1, 3),
            new BPELTestCase().checkDeployment().sendSync(3, 5)
    );

    public static final Test FLOW_LINKS_JOIN_FAILURE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Links-JoinFailure",
            "A receive-reply pair with an intermediate flow that contains three assigns, two of which point to the third using links. Both links have transitionConditions and their target a joinCondition defined upon them. The transitionConditions do never evaluate to true, resulting in a joinFailure on each invocation.",
            new Feature(FLOW_CONSTRUCT, "Flow-Links-JoinFailure"),
            new BPELTestCase().checkDeployment().sendSync(1, new AssertSoapFault("joinFailure")),
            new BPELTestCase().checkDeployment().sendSync(3, new AssertSoapFault("joinFailure"))
    );
    public static final Test FLOW_TWO_STARTING_ON_MESSAGE_CORRELATION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Two-Starting-OnMessage-Correlation",
            "A flow that contains two pick activities that can both be start activity and reply 0 or '0'. After the flow a simple synchronous receive-reply pair responses the concatenation of the two starting message inputParts.",
            new Feature(FLOW_CONSTRUCT, "Flow-Two-Starting-OnMessage-Correlation"),
            new BPELTestCase().checkDeployment().sendSync(1, 0).sendSyncString(1, "0").sendSyncString(1, "11"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "0").sendSync(2, 0).sendSyncString(2, "22")
    );

    public static
    final Test FLOW_STARTING_RECEIVE_ON_MESSAGE_CORRELATION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Starting-Receive-OnMessage-Correlation",
            "A flow that contains a receive-reply pair in a sequence, replying 0, and a pick activity that replies '0'. Both message activities can be start activity. After the flow a simple synchronous receive-reply pair responses the concatenation of the two starting message inputParts.",
            new Feature(FLOW_CONSTRUCT, "Flow-Starting-Receive-OnMessage-Correlation"),
            new BPELTestCase().checkDeployment().sendSync(1, 0).sendSyncString(1, "0").sendSyncString(1, "11"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "0").sendSync(2, 0).sendSyncString(2, "22")
    );

    public static final Test FLOW_TWO_STARTING_RECEIVE_CORRELATION = BPELProcessBuilder.buildStructuredActivityProcess(
            "Flow-Two-Starting-Receive-Correlation",
            "A flow that contains two receive-reply pair in a sequence that can both be start activity and reply 0 or '0'. After the flow a simple synchronous receive-reply pair responses the concatenation of the two starting message inputParts.",
            new Feature(FLOW_CONSTRUCT, "Flow-Two-Starting-Receive-Correlation"),
            new BPELTestCase().checkDeployment().sendSync(1, 0).sendSyncString(1, "0").sendSyncString(1, "11"),
            new BPELTestCase().checkDeployment().sendSyncString(2, "0").sendSync(2, 0).sendSyncString(2, "22")
    );

    public static final List<Test> STRUCTURED_ACTIVITIES_FLOW = Arrays.asList(
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

    public static final Test IF = BPELProcessBuilder.buildStructuredActivityProcess(
            "If", "A receive-reply pair with an intermediate if that checks whether the input is even.",
            new Feature(IF_CONSTRUCT, "If"),
            new BPELTestCase("Not-If-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1)
    );

    public static final Test IF_ELSE = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-Else", "A receive-reply pair with an intermediate if-else that checks whether the input is even.",
            new Feature(IF_CONSTRUCT, "If-Else"),
            new BPELTestCase("Else-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1)
    );

    public static final Test IF_ELSE_IF = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-ElseIf", "A receive-reply pair with an intermediate if-elseif that checks whether the input is even or divisible by three.",
            new Feature(IF_CONSTRUCT, "If-ElseIf"),
            new BPELTestCase("Not-If-Or-ElseIf-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1),
            new BPELTestCase("ElseIf-Case").checkDeployment().sendSync(3, 2)
    );

    public static final Test IF_ELSE_IF_ELSE = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-ElseIf-Else", "A receive-reply pair with an intermediate if-elseif-else that checks whether the input is even or divisible by three.",
            new Feature(IF_CONSTRUCT, "If-ElseIf-Else"),
            new BPELTestCase("Else-Case").checkDeployment().sendSync(1, 0),
            new BPELTestCase("If-Case").checkDeployment().sendSync(2, 1),
            new BPELTestCase("ElseIf-Case").checkDeployment().sendSync(3, 2)
    );
    public static final Test IF_SUBLANGUAGE_EXECUTION_FAULT = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-SubLanguageExecutionFault", "A receive-reply pair with an intermediate if that should throw an subLanguageExecutionFault because of an invalid condition.",
            new Feature(IF_CONSTRUCT, "If-SubLanguageExecutionFault"),
            new BPELTestCase("SubLanguageExecutionFault").checkDeployment().
                    sendSync(1, new AssertSoapFault("subLanguageExecutionFault"))
    );

    public static
    final Test IF_SUBLANGUAGE_EXECUTION_FAULT_EMPTY_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "If-SubLanguageExecutionFault-EmptyCondition", "A receive-reply pair with an intermediate if that should throw an subLanguageExecutionFault because of an empty condition.",
            new Feature(IF_CONSTRUCT, "If-SubLanguageExecutionFault-EmptyCondition"),
            new BPELTestCase("SubLanguageExecutionFault").checkDeployment().
                    sendSync(1, new AssertSoapFault("subLanguageExecutionFault"))
    );

    public static final List<Test> STRUCTURED_ACTIVITIES_IF = Arrays.asList(
            IF,
            IF_ELSE,
            IF_ELSE_IF,
            IF_ELSE_IF_ELSE,
            IF_SUBLANGUAGE_EXECUTION_FAULT,
            IF_SUBLANGUAGE_EXECUTION_FAULT_EMPTY_CONDITION
    );
    public static final Test WHILE = BPELProcessBuilder.buildStructuredActivityProcess(
            "While", "A receive-reply pair with an intermediate while that loops for n times, where n is equal to the input.",
            new Feature(WHILE_CONSTRUCT, "While"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test WHILE_FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "While-Flow", "A receive-reply pair with an intermediate while that loops for n times, where n is equal to the input. The loop contains a flow that links the assignment of 1 to a counter and the assignment of the counter to the reply data.",
            new Feature(WHILE_CONSTRUCT, "While-Flow"),
            new BPELTestCase().checkDeployment().sendSync(5, 5)
    );

    public static final Test REPEAT_UNTIL = BPELProcessBuilder.buildStructuredActivityProcess(
            "RepeatUntil", "A receive-reply pair with an intermediate while that loops for n+1 times, where n is equal to the input.",
            new Feature(REPEAT_UNTIL_CONSTRUCT, "RepeatUntil"),
            new BPELTestCase().checkDeployment().sendSync(2, 3)
    );

    public static final Test REPEAT_UNTIL_EQUALITY = BPELProcessBuilder.buildStructuredActivityProcess(
            "RepeatUntilEquality", "A receive-reply pair with an intermediate while that loops for n times, where n is equal to the input.",
            new Feature(REPEAT_UNTIL_CONSTRUCT, "RepeatUntilEquality"),
            new BPELTestCase().checkDeployment().sendSync(2, 2)
    );
    public static final Test REPEAT_UNTIL_FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "RepeatUntil-Flow", "A receive-reply pair with an intermediate while that loops for n+1 times, where n is equal to the input. The loop contains a flow that links the assignment of 1 to a counter and the assignment of the counter to the reply data.",
            new Feature(REPEAT_UNTIL_CONSTRUCT, "RepeatUntil-Flow"),
            new BPELTestCase().checkDeployment().sendSync(2, 3)
    );

    public static final Test FOR_EACH = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. Each iteration the current loop number is added to the final result.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach"),
            new BPELTestCase("0-equals-0").checkDeployment().sendSync(0, 0),
            new BPELTestCase("0plus1-equals-0").checkDeployment().sendSync(1, 1),
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().sendSync(2, 3)
    );

    public static final Test FOR_EACH_READ_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Read-Counter", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. Each iteration the current loop number is added twice to the final result.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-Read-Counter"),
            new BPELTestCase().checkDeployment().sendSync(0, 0),
            new BPELTestCase().checkDeployment().sendSync(1, 2),
            new BPELTestCase().checkDeployment().sendSync(2, 6)
    );

    public static final Test FOR_EACH_WRITE_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Write-Counter", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. The loop contains an if activity, so each odd iteration of the current loop number is added to the final result, when it also has a successor in range.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-Write-Counter"),
            new BPELTestCase().checkDeployment().sendSync(0, 0),
            new BPELTestCase().checkDeployment().sendSync(2, 1),
            new BPELTestCase().checkDeployment().sendSync(6, 9)
    );

    public static final Test FOR_EACH_FLOW = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Flow", "A receive-reply pair with an intermediate forEach that loops for n times, where n is equal to the input. Each iteration the current loop number is added to a intermediary and from there to the final result, and these assigns are linked within a flow.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-Flow"),
            new BPELTestCase("0-equals-0").checkDeployment().sendSync(0, 0),
            new BPELTestCase("0plus1-equals-0").checkDeployment().sendSync(1, 1),
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().sendSync(2, 3)
    );

    public static final Test FOR_EACH_NEGATIVE_STOP_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-NegativeStopCounter", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as finalCounterValue is negative.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-NegativeStopCounter"),
            new BPELTestCase("NegativeStopCounter").checkDeployment().sendSync(1, new AssertSoapFault("invalidExpressionValue"))
            // NaN or large numbers cannot be sent due to type constraint to xsd:int
    );

    public static final Test FOR_EACH_NEGATIVE_START_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-NegativeStartCounter", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as startCounterValue is negative.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-NegativeStartCounter"),
            new BPELTestCase("Iterate-Twice").checkDeployment().sendSync(2, new AssertSoapFault("invalidExpressionValue"))
    );

    public static
    final Test FOR_EACH_COMPLETION_CONDITION_NEGATIVE_BRANCHES = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition-NegativeBranches", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as branches is initialized with a negative value.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-CompletionCondition-NegativeBranches"),
            new BPELTestCase("Iterate-Twice").checkDeployment().sendSync(2, new AssertSoapFault("invalidExpressionValue"))
    );

    public static final Test FOR_EACH_TOO_LARGE_START_COUNTER = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-TooLargeStartCounter", "A receive-reply pair with an intermediate forEach that should always fail with an invalidExpressionValue fault as startCounterValue is initialized with a value that exceeds xs:unsignedInt.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-TooLargeStartCounter"),
            new BPELTestCase("Iterate-Twice").checkDeployment().sendSync(2, new AssertSoapFault("invalidExpressionValue"))
    );

    public static final Test FOR_EACH_PARALLEL = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-Parallel", "A receive-reply pair with an intermediate forEach that executes its children in parallel.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-Parallel"),
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().sendSync(2, 3)
    );

    public static final Test FOR_EACH_PARALLEL_INVOKE = BPELProcessBuilder.buildStructuredProcessWithPartner(
            "ForEach-Parallel-Invoke", "A receive-reply pair with an intermediate forEach that executes its children in parallel.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-Parallel-Invoke"),
            new BPELTestCase("0plus1plus2-equals-3").checkDeployment().buildPartnerConcurrencySetup().
                    sendSync(2, 3).
                    assertConcurrencyAtPartner().assertNumberOfPartnerCalls(3)
    );

    public static final Test FOR_EACH_COMPLETION_CONDITION = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated. N+1 children are scheduled for execution, where n is equal to the input. If N+1 is less than two, an invalidBranchConditionFault should be thrown.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-CompletionCondition"),
            new BPELTestCase("Skipping the third iteration").checkDeployment().sendSync(2, 1),
            new BPELTestCase("Cannot meet completion condition").checkDeployment().
                    sendSync(0, new AssertSoapFault("invalidBranchCondition"))
    );

    public static final Test FOR_EACH_COMPLETION_CONDITION_PARALLEL = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition-Parallel", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated. N+1 children are scheduled for execution in parallel, where n is equal to the input. If N+1 is less than two, an invalidBranchConditionFault should be thrown.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-CompletionCondition-Parallel"),
            new BPELTestCase("Skipping the third iteration").checkDeployment().sendSync(2, 1),
            new BPELTestCase("Cannot meet completion condition").checkDeployment().
                    sendSync(0, new AssertSoapFault("invalidBranchCondition"))
    );

    public static
    final Test FOR_EACH_COMPLETION_CONDITION_SUCCESSFUL_BRANCHES_ONLY = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionCondition-SuccessfulBranchesOnly", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated successfully. Each child throws a fault, given the current counter value is even. N children are scheduled for execution, where n is equal to the input.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-CompletionCondition-SuccessfulBranchesOnly"),
            new BPELTestCase().checkDeployment().sendSync(5, 6),
            new BPELTestCase().checkDeployment().sendSync(10, 6)
    );

    public static final Test FOR_EACH_COMPLETION_CONDITION_FAILURE = BPELProcessBuilder.buildStructuredActivityProcess(
            "ForEach-CompletionConditionFailure", "A receive-reply pair with an intermediate forEach that should terminate given two of its children have terminated. N+1 children are scheduled for execution in parallel, where n is equal to the input. If N+1 is less than two, an invalidBranchConditionFault should be thrown. This is a seperate test case that tests only for the failure.",
            new Feature(FOR_EACH_CONSTRUCT, "ForEach-CompletionConditionFailure"),
            new BPELTestCase("Expect completionConditionFailure").checkDeployment().
                    sendSync(1, new AssertSoapFault("completionConditionFailure"))
    );

    public static final List<Test> STRUCTURED_ACTIVITIES_FOR_EACH = Arrays.asList(
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
    public static final Test PICK_CORRELATIONS_INIT_ASYNC = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Correlations-InitAsync", "An asynchronous receive that initiates a correlationSet, followed by a pick with a synchronous onMessage that correlates on this set.",
            new Feature(PICK_CONSTRUCT, "Pick-Correlations-InitAsync"),
            new BPELTestCase().checkDeployment().sendAsync(1).sendSync(1, 1)
    );

    public static final Test PICK_CORRELATIONS_INIT_SYNC = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Correlations-InitSync", "A receive-reply pair that initiates a correlationSet, followed by a pick with a synchronous onMessage that correlates on this set.",
            new Feature(PICK_CONSTRUCT, "Pick-Correlations-InitSync"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test PICK_CREATE_INSTANCE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-CreateInstance", "A pick with a synchronous onMessage that has createInstance set to yes.",
            new Feature(PICK_CONSTRUCT, "Pick-CreateInstance"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test PICK_MESSAGE_EXCHANGE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-MessageExchange", "A pick with a synchronous onMessage that has createInstance set to yes and uses messageExchange.",
            new Feature(PICK_CONSTRUCT, "Pick-MessageExchange"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test PICK_MESSAGE_EXCHANGE_SCOPE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-MessageExchange-Scope", "A pick with a synchronous onMessage that has createInstance set to yes and uses messageExchange in a scope.",
            new Feature(PICK_CONSTRUCT, "Pick-MessageExchange-Scope"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test PICK_MULTIPLE_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Multiple-MessageExchanges", "A pick with a synchronous onMessage that has createInstance set to yes and a second pick with a synchronous onMessage of the same type and both use messageExchanges to define which reply belongs to which onMessage and the response is the initial value first then the sum of the received values.",
            new Feature(PICK_CONSTRUCT, "Pick-Multiple-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test PICK_MULTIPLE_MESSAGE_EXCHANGES_SCOPE = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Multiple-MessageExchanges-Scope", "A pick with a synchronous onMessage that has createInstance set to yes and a second pick with a synchronous onMessage of the same type and both use messageExchanges in a scope to define which reply belongs to which onMessage and the response is the initial value first then the sum of the received values.",
            new Feature(PICK_CONSTRUCT, "Pick-Multiple-MessageExchanges-Scope"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test PICK_FIFO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-FIFO-MessageExchanges", "Two onMessages of the same operation that use messageExchanges to define which reply belongs to which onMessage and the response is 1 for the reply to the first onMessage and 2 for the second reply to the second onMessage.",
            new Feature(PICK_CONSTRUCT, "Pick-FIFO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test PICK_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-FILO-MessageExchanges", "Two onMessages of the same operation that use messageExchanges to define which reply belongs to which onMessage and the response is 2 for the reply to the second onMessage and 1 for the second reply to the first onMessage.",
            new Feature(PICK_CONSTRUCT, "Pick-FILO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test RECEIVE_PICK_FIFO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Receive-Pick-FIFO-MessageExchanges", "A receive and a onMessage of the same operation that use messageExchanges to define which reply belongs to which receive/onMessage and the response is 1 for the reply to the onMessage and 2 for the second reply to the receive.",
            new Feature(PICK_CONSTRUCT, "Receive-Pick-FIFO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test RECEIVE_PICK_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Receive-Pick-FILO-MessageExchanges", "A receive and a onMessage of the same operation that use messageExchanges to define which reply belongs to which receive/onMessage and the response is 2 for the reply to the onMessage and 1 for the second reply to the receive.",
            new Feature(PICK_CONSTRUCT, "Receive-Pick-FILO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test PICK_RECEIVE_FIFO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Receive-FIFO-MessageExchanges", "A onMessage and a receive of the same operation that use messageExchanges to define which reply belongs to which onMessage/receive and the response is 1 for the reply to the receive and 2 for the second reply to the onMessage.",
            new Feature(PICK_CONSTRUCT, "Pick-Receive-FIFO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test PICK_RECEIVE_FILO_MESSAGE_EXCHANGES = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-Receive-FILO-MessageExchanges", "A onMessage and a receive of the same operation that use messageExchanges to define which reply belongs to which onMessage/receive and the response is 2 for the reply to the receive and 1 for the second reply to the onMessage.",
            new Feature(PICK_CONSTRUCT, "Pick-Receive-FILO-MessageExchanges"),
            new BPELTestCase().checkDeployment().sendSync(1, 1).sendSync(1, 2)
    );

    public static final Test PICK_CREATE_INSTANCE_FROM_PARTS = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-CreateInstance-FromParts", "A pick with a synchronous onMessage that has createInstance set to yes using fromParts.",
            new Feature(PICK_CONSTRUCT, "Pick-CreateInstance-FromParts"),
            new BPELTestCase().checkDeployment().sendSync(1, 1)
    );

    public static final Test PICK_ON_ALARM_UNTIL = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-OnAlarm-Until", "A receive-reply pair that initiates a correlationSet and an intermediate pick that contains an onMessage and an onAlarm with an until element. The onAlarm should fire immediately.",
            new Feature(PICK_CONSTRUCT, "Pick-OnAlarm-Until"),
            new BPELTestCase().checkDeployment().sendSync(1, -1)
    );

    public static final Test PICK_ON_ALARM_FOR = BPELProcessBuilder.buildStructuredActivityProcess(
            "Pick-OnAlarm-For", "An onAlarm with for test case. The test contains a receive-reply pair that initiates a correlationSet and an intermediate pick that contains an onMessage and an onAlarm with an for element. The onAlarm should fire after two seconds and the process should reply with a default value.",
            new Feature(PICK_CONSTRUCT, "Pick-OnAlarm-For"),
            new BPELTestCase().checkDeployment().sendSync(1, -1)
    );

    public static final List<Test> STRUCTURED_ACTIVITIES_PICK = Arrays.asList(
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

    public static final List<Test> STRUCTURED_ACTIVITIES = CollectionsUtil.union(Arrays.asList(
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
