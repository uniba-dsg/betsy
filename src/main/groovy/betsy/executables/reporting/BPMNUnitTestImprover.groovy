package betsy.executables.reporting

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BPMNTestSuite
import betsy.data.engines.BPMNEngine
import betsy.tasks.FileTasks

import java.nio.file.Path
import java.nio.file.Paths

class BPMNUnitTestImprover {

    AntBuilder ant = AntUtil.builder()

    BPMNTestSuite suite

    void addProcessImagesToUnitReports(){
        //prepare folder
        Path imgDir = Paths.get("test/reports/imgs")
        FileTasks.mkdirs(imgDir)

        //copy images
        for(BPMNProcess process : suite.engines.get(0).processes){
            FileTasks.mkdirs(imgDir.resolve(process.normalizedId))
            ant.copy(todir: imgDir.resolve(process.normalizedId)) {
                ant.fileset(file: process.resourcePath.resolve("${process.name}.png"))
            }
        }

        //add images to html
        for(BPMNEngine engine : suite.engines){
            for (BPMNProcess process : engine.processes){
                String path = new FileNameFinder().getFileNames("test/reports/html/${engine.name}/${process.group}", "*_${process.name}.html").first()
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))
                StringBuilder stringBuilder = new StringBuilder()
                reader.eachLine { line ->
                    if(line.contains("</body>")){
                        stringBuilder.append("<img src=\"../../../imgs/${process.normalizedId}/${process.name}.png\" alt=\"${process}\" />")
                    }
                    stringBuilder.append(line)
                }
                reader.close()
                FileWriter writer = new FileWriter(path)
                writer.print(stringBuilder)
                writer.close()
            }
        }
    }
}
