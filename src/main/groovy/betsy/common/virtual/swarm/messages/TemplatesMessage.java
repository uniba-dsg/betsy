package betsy.common.virtual.swarm.messages;

import betsy.common.virtual.cbetsy.WorkerTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public final class TemplatesMessage implements Serializable {

    private List<WorkerTemplate> templates;

    public TemplatesMessage(List<WorkerTemplate> templates) {
        this.templates = templates;
    }

    public List<WorkerTemplate> getTemplates() {
        return templates;
    }
}
