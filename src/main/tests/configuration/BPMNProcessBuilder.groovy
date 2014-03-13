package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase
import betsy.data.engines.BPMNEngine

/**
 * Created with IntelliJ IDEA.
 * User: stmcasar
 * Date: 13.03.14
 * Time: 10:03
 * To change this template use File | Settings | File Templates.
 */
class BPMNProcessBuilder {

    public static BPMNProcess buildProcess(String name, List<BPMNTestCase> testCases){
        new BPMNProcess(name: name,group: ,key: , groupId: , version: ,engine: )
    }
}