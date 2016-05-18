package betsy.common.virtual.swarm;

import betsy.common.virtual.cbetsy.WorkerTemplate;

import java.util.List;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class WorkerTemplateMessage {

    private List<WorkerTemplate> templates;

    public WorkerTemplateMessage(List<WorkerTemplate> templates){
        this.templates = templates;
    }

    public List<WorkerTemplate> getTemplates() {
        return templates;
    }
}
